package com.capstoneproject.smartattendance.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AdminDashboardDto;
import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.AtteandanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceStatus;
import com.capstoneproject.smartattendance.dto.BasicAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.dto.Role;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.dto.TeacherDto;
import com.capstoneproject.smartattendance.dto.SaveAttendanceDto;
import com.capstoneproject.smartattendance.dto.SessionDto;
import com.capstoneproject.smartattendance.entity.Academic;
import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.Attendance;
import com.capstoneproject.smartattendance.entity.AttendanceRecord;
import com.capstoneproject.smartattendance.entity.Teacher;
import com.capstoneproject.smartattendance.entity.Student;

import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

import com.capstoneproject.smartattendance.repository.AcademicRepo;
import com.capstoneproject.smartattendance.repository.AdminRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRecordRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRepo;
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.repository.TeacherRepo;

import com.capstoneproject.smartattendance.service.mail.AdminMailService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final StudentRepo studentRepo;

    private final AdminRepo adminRepo;

    private final TeacherRepo teacherRepo;

    private final AcademicRepo academicRepo;

    private final AttendanceRecordRepo attendanceRecordRepo;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final AdminMailService adminMailService;

    private final OtpService otpService;

    private final AttendanceRepo attendanceRepo;

    private final TeacherService teacherService;

    private final StringRedisTemplate redisTemplate;

    private final ImageApprovalAsyncService imageApprovalAsyncService;

    // Add field
    private final CloudinaryService cloudinaryService;

    public AdminDashboardDto getAdminDashboardService(String adminId, int days) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        AdminDashboardDto response = new AdminDashboardDto();

        response.setStudentCount((int) admin.getStudents().stream()
                .filter(student -> !student.isDeleted())
                .count());
        response.setTeacherCount((int) admin.getTeachers().stream()
                .filter(student -> !student.isDeleted())
                .count());
        response.setAcademicCount(admin.getAcademicDatas().size());
        response.setImageReqCount(this.getAllImageChangeRequestService(adminId).size());

        response.setSessions(getLastDaysSessions(admin, days));
        return response;
    }

    public List<SessionDto> getLastDaysSessions(Admin admin, int days) {

        LocalDate fromDate = LocalDate.now().minusDays(days);

        return admin.getTeachers().stream()
                .filter(teacher -> !teacher.isDeleted())
                .flatMap(teacher -> teacher.getAttendance().stream())
                .filter(att -> !att.isDeleted())
                .filter(att -> !att.getAttendanceDate().isBefore(fromDate))
                .map(att -> {
                    int total = att.getAttendanceRecords().size();
                    long present = att.getAttendanceRecords().stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                            .count();
                    double percentage = total == 0 ? 0 : (present * 100.0) / total;
                    SessionDto dto = new SessionDto();
                    dto.setDate(att.getAttendanceDate());
                    dto.setSubjectName(att.getSubjectName());
                    dto.setAttendancePercentage(percentage);
                    dto.setTeacherName(att.getTeacher().getName());
                    return dto;
                }).toList();
    }

    public List<AcademicDto> getAcademicDataService(String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        return admin.getAcademicDatas()
                .stream()
                .map(a -> {

                    AcademicDto res = modelMapper.map(a, AcademicDto.class);

                    List<Student> students = Optional.ofNullable(a.getStudents())
                            .orElse(Collections.emptyList());

                    long deletedCount = 0;
                    long activeCount = 0;

                    for (Student student : students) {
                        if (student.isDeleted()) {
                            deletedCount++;
                        } else {
                            activeCount++;
                        }
                    }

                    res.setDeletedStudentstudentCount(deletedCount);
                    res.setStudentCount(activeCount);

                    return res;
                })
                .toList();
    }

    @Transactional
    public void createAcademicDataService(AcademicDto academicDto, String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = modelMapper.map(academicDto, Academic.class);

        boolean exists = admin.getAcademicDatas().stream()
                .anyMatch(a -> a.getYear().equalsIgnoreCase(academicDto.getYear()) &&
                        a.getBranch().equalsIgnoreCase(academicDto.getBranch()) &&
                        a.getSemester().equalsIgnoreCase(academicDto.getSemester()) &&
                        a.getClassName().equalsIgnoreCase(academicDto.getClassName()) &&
                        a.getBatch().equalsIgnoreCase(academicDto.getBatch()) &&
                        a.getAdmin().getUserId().equals(adminId));

        if (exists) {
            throw new CustomeException(ErrorCode.ACADEMIC_ALREADY_PRESENT);
        }

        academic.setAdmin(admin);

        admin.getAcademicDatas().add(academic);
        adminRepo.save(admin);

    }

    @Transactional
    public void updateAcademicDataService(AcademicDto academicDto, String adminId) {
        UUID academicId = academicDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        academic.setYear(academicDto.getYear());
        academic.setBranch(academicDto.getBranch());
        academic.setSemester(academicDto.getSemester());
        academic.setClassName(academicDto.getClassName());
        academic.setBatch(academicDto.getBatch());

        boolean exists = admin.getAcademicDatas().stream().anyMatch(a -> !a.getAcademicId().equals(academicId) &&
                a.getYear().equalsIgnoreCase(academicDto.getYear()) &&
                a.getBranch().equalsIgnoreCase(academicDto.getBranch()) &&
                a.getSemester().equalsIgnoreCase(academicDto.getSemester()) &&
                a.getClassName().equalsIgnoreCase(academicDto.getClassName()) &&
                a.getBatch().equalsIgnoreCase(academicDto.getBatch()));

        if (exists) {
            throw new CustomeException(ErrorCode.ACADEMIC_ALREADY_PRESENT);
        }
        academic.setAdmin(admin);
        admin.getAcademicDatas().add(academic);

        adminRepo.save(admin);
    }

    @Transactional
    public void deleteAcademicDataService(UUID academicId, String adminId) {

        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        // Check if students exist
        if (!academic.getStudents().isEmpty()) {
            throw new CustomeException(ErrorCode.CANT_DELETE_ACADEMIC);
        }

        // Check if attendance exists
        if (!academic.getAttendanceAcademics().isEmpty()) {
            throw new CustomeException(ErrorCode.CANT_DELETE_ACADEMIC_ATTENDANCE);
        }

        academicRepo.delete(academic);
    }

    public BasicDataDto getMyDetailsService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(admin, BasicDataDto.class);
        return basicDataDto;
    }

    public void updateAdminService(AdminDto adminDto, String adminId) {
        String collegeName = adminDto.getCollegeName();
        String name = adminDto.getName();
        String otp = adminDto.getOtp();
        String email = adminDto.getEmail();
        String password = adminDto.getPassword();

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new CustomeException(ErrorCode.WRONG_PASSWORD);
        }

        if (!admin.getEmail().equals(email)) {
            if (otp == null) {
                throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
            }
            otpService.verifyOtp(email, otp);
            admin.setEmail(email);
        }
        if (!collegeName.equals(admin.getCollegeName())) {
            admin.setCollegeName(collegeName);
            if (admin.getStudents() != null) {
                admin.getStudents().forEach(s -> s.setCollegeName(collegeName));
            }

            if (admin.getTeachers() != null) {
                admin.getTeachers().forEach(t -> t.setCollegeName(collegeName));
            }
        }
        admin.setName(name);

        adminRepo.save(admin);
    }

    public void addStudentService(StudentDto studentDto, String adminId) {
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();
        UUID academicId = studentDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Optional<Student> studentOpt = studentRepo.findById(userId);
        if (studentOpt.isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        boolean flag = studentRepo.existsByAdminAndEnrollmentNo(admin, studentDto.getEnrollmentNo());
        if (flag) {
            throw new CustomeException(ErrorCode.ENROLLMENT_NOT_AVAILABLE);
        }
        Student student = modelMapper.map(studentDto, Student.class);

        student.setRole(Role.STUDENT);
        student.setAdmin(admin);
        student.setCollegeName(admin.getCollegeName());
        student.setAcademic(academic);
        student.setCurImage("https://res.cloudinary.com/dzyjaax7p/image/upload/v1773846089/defaultimage_kuxomk.jpg");
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentDetailsMail(student, adminId, studentDto.getPassword(), "created");
        studentRepo.save(student);
    }

    public void updateStudentService(StudentDto studentDto, String adminId) {
        String userId = studentDto.getUserId();
        UUID academicId = studentDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Student student = studentRepo.findByUserIdAndIsDeleted(userId, false)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        student.setName(studentDto.getName());
        student.setCollegeName(admin.getCollegeName());
        student.setEnrollmentNo(studentDto.getEnrollmentNo());
        student.setEmail(studentDto.getEmail());
        student.setAcademic(academic);
        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        adminMailService.sendStudentDetailsMail(student, adminId, studentDto.getPassword(), "updated");
        studentRepo.save(student);
    }

    public void updateStudentAcademicService(StudentDto studentDto, String adminId) {
        String userId = studentDto.getUserId();
        UUID academicId = studentDto.getAcademicId();
        if (academicId == null || userId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Student student = studentRepo.findByUserIdAndIsDeleted(userId, false)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        student.setAcademic(academic);
        adminMailService.sendStudentDetailsMail(student, adminId, studentDto.getPassword(), "updated");
        studentRepo.save(student);
    }

    public void deleteStudentService(String userId, String adminId) throws IOException {

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!student.getRole().equals(Role.STUDENT)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        // Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        // Files.createDirectories(uploadPath);

        // if (student.getCurImage() != null &&
        // !student.getCurImage().equals("defaultimage.jpg")) {
        // String curFile = student.getCurImage();
        // Path curPath = uploadPath.resolve(curFile).normalize();
        // Files.deleteIfExists(curPath);
        // }
        // if (student.getNewImage() != null) {
        // String curFile = student.getNewImage();
        // Path curPath = uploadPath.resolve(curFile).normalize();
        // Files.deleteIfExists(curPath);
        // }
        redisTemplate.delete("jwt:" + userId);
        student.setDeleted(true);
        student.setDeletedDate(LocalDate.now());
        studentRepo.save(student);
    }

    public List<StudentResponseDto> getAllImageChangeRequestService(String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Student> students = studentRepo.findByIsDeletedAndNewImageIsNotNullAndAdmin_UserId(false, adminId);

        List<StudentResponseDto> response = students
                .stream()
                .map(s -> {
                    StudentResponseDto res = modelMapper.map(s, StudentResponseDto.class);
                    res.setYear(s.getAcademic().getYear());
                    res.setBranch(s.getAcademic().getBranch());
                    res.setSemester(s.getAcademic().getSemester());
                    res.setClassName(s.getAcademic().getClassName());
                    res.setBatch(s.getAcademic().getBatch());
                    res.setCurImage(res.getCurImage());
                    res.setNewImage(res.getNewImage());
                    return res;
                })
                .toList();
        return response;
    }

    public void approveImageChangeRequestService(String adminId, String userId) throws IOException {

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (student.getNewImage() == null) {
            throw new CustomeException(ErrorCode.NO_REQUEST_FOUND);
        }

        String defaultUrl = "https://res.cloudinary.com/dzyjaax7p/image/upload/v1773846089/defaultimage_kuxomk.jpg";
        if (student.getCurImage() != null && !student.getCurImage().equals(defaultUrl)) {
            String publicId = cloudinaryService.extractPublicId(student.getCurImage());
            cloudinaryService.deleteImage(publicId);
        }

        student.setCurImage(student.getNewImage());
        student.setNewImage(null);
        studentRepo.save(student);

    }

    public void rejectImageChangeRequestService(String adminId, String userId) throws IOException {

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (student.getNewImage() == null) {
            throw new CustomeException(ErrorCode.NO_REQUEST_FOUND);
        }

        String publicId = cloudinaryService.extractPublicId(student.getNewImage());
        cloudinaryService.deleteImage(publicId);
        student.setNewImage(null);
        studentRepo.save(student);

    }

    public void addTeacherService(TeacherDto teacherDto, String adminId) {
        String userId = teacherDto.getUserId();
        String password = teacherDto.getPassword();

        Optional<Teacher> teacherOpt = teacherRepo.findById(userId);
        if (teacherOpt.isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Admin admin = adminRepo.findById(adminId).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = modelMapper.map(teacherDto, Teacher.class);

        teacher.setRole(Role.TEACHER);
        teacher.setAdmin(admin);
        teacher.setCollegeName(admin.getCollegeName());
        teacher.setPassword(passwordEncoder.encode(password));

        adminMailService.sendTeacherDetailsMail(teacher, adminId, teacherDto.getPassword(), "created");
        teacherRepo.save(teacher);
    }

    public void updateTeacherService(TeacherDto teacherDto, String adminId) {
        String userId = teacherDto.getUserId();
        String password = teacherDto.getPassword();

        Teacher teacher = teacherRepo.findByUserIdAndIsDeleted(userId, false)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!teacher.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        teacher.setName(teacherDto.getName());
        teacher.setEmail(teacherDto.getEmail());
        teacher.setAdmin(admin);
        teacher.setPassword(passwordEncoder.encode(password));

        adminMailService.sendTeacherDetailsMail(teacher, adminId, teacherDto.getPassword(), "updated");
        teacherRepo.save(teacher);

    }

    public void deleteTeacherService(String userId, String adminId) {

        Teacher teacher = teacherRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!teacher.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!teacher.getRole().equals(Role.TEACHER)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        redisTemplate.delete("jwt:" + userId);
        teacher.setDeleted(true);
        teacher.setDeletedDate(LocalDate.now());
        teacherRepo.save(teacher);
    }

    public StudentResponseDto getStudentService(String enrollmentNo, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo
                .findByAdminAndEnrollmentNoAndIsDeletedFalseAndDeletedDateIsNull(admin, enrollmentNo)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = student.getAcademic();
        StudentResponseDto studentResponseDto = modelMapper.map(student, StudentResponseDto.class);

        studentResponseDto.setYear(academic.getYear());
        studentResponseDto.setBranch(academic.getBranch());
        studentResponseDto.setSemester(academic.getSemester());
        studentResponseDto.setClassName(academic.getClassName());
        studentResponseDto.setBatch(academic.getBatch());
        studentResponseDto.setCurImage(studentResponseDto.getCurImage());
        studentResponseDto.setNewImage(studentResponseDto.getNewImage());

        return studentResponseDto;
    }

    public BasicDataDto getTeacherService(String userId, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = teacherRepo.findByAdminAndUserIdAndIsDeletedFalseAndDeletedDateIsNull(admin, userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);

        return basicDataDto;
    }

    public List<StudentResponseDto> getAllStudentService(List<UUID> academicsList, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        return admin.getStudents()
                .stream()
                .filter(student -> academicsList.contains(student.getAcademic().getAcademicId()))
                .filter(student -> !student.isDeleted())
                .map(student -> {
                    Academic academic = student.getAcademic();

                    StudentResponseDto dto = modelMapper.map(student, StudentResponseDto.class);
                    dto.setYear(academic.getYear());
                    dto.setBranch(academic.getBranch());
                    dto.setSemester(academic.getSemester());
                    dto.setClassName(academic.getClassName());
                    dto.setBatch(academic.getBatch());

                    return dto;
                })
                .toList();
    }

    public List<BasicDataDto> getAllteacherService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<BasicDataDto> response = admin.getTeachers()
                .stream()
                .filter(a -> !a.isDeleted())
                .map(a -> modelMapper.map(a, BasicDataDto.class))
                .toList();

        return response;

    }

    public List<BasicAttendanceResponseDto> getAllAttendanceService(String adminId) {

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo
                .findByTeacher_Admin_UserIdOrderByAttendanceDateDescAttendanceTimeDesc(adminId);

        List<BasicAttendanceResponseDto> response = attendances
                .stream()
                .filter(a -> !a.isDeleted())
                .map(a -> {

                    BasicAttendanceResponseDto temp = modelMapper.map(a, BasicAttendanceResponseDto.class);

                    temp.setTeacherName(a.getTeacher().getName());

                    int total = a.getAttendanceRecords().size();

                    int present = (int) a.getAttendanceRecords()
                            .stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    temp.setTotalStudentCount(total);
                    temp.setPresentStudentCount(present);

                    return temp;
                })
                .toList();

        return response;
    }

    public AtteandanceResponseDto getAttendancByAttendanceIdService(UUID attendanceId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        return teacherService.getAttendancByAttendanceIdService(attendanceId, attendance.getTeacher().getUserId());
    }

    @Transactional
    public void addNewAcademicInAttendanceService(UUID attendanceId, UUID academicId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        teacherService.addNewAcademicInAttendanceService(attendanceId, academicId, attendance.getTeacher().getUserId());
    }

    @Transactional
    public void removeAcademicInAttendanceService(UUID attendanceId, UUID academicId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        teacherService.removeAcademicInAttendanceService(attendanceId, academicId, attendance.getTeacher().getUserId());
    }

    public void deleteAttendanceService(UUID attendanceId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        attendance.setDeleted(true);
        attendance.setDeletedDate(LocalDate.now());
        attendanceRepo.save(attendance);
    }

    public void markStudentPresentInAttendanceService(UUID attendanceId, String studentId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        teacherService.markStudentPresentInAttendanceService(attendanceId, studentId,
                attendance.getTeacher().getUserId());
    }

    public void markStudentAbsentInAttendanceService(UUID attendanceId, String studentId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        teacherService.markStudentAbsentInAttendanceService(attendanceId, studentId,
                attendance.getTeacher().getUserId());
    }

    @Transactional
    public void deleteAllAttendanceService(String adminId, String otp) {
        if (otp == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        otpService.verifyOtp(admin.getEmail(), otp);

        List<Attendance> attendanceList = attendanceRepo.findByTeacher_Admin_UserId(adminId);
        for (Attendance attendance : attendanceList) {
            attendance.setDeleted(true);
            attendance.setDeletedDate(LocalDate.now());
            attendanceRepo.save(attendance);
        }
    }

    public List<StudentResponseDto> allDeletedStudentService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<StudentResponseDto> response = admin.getStudents()
                .stream()
                .filter(a -> a.isDeleted())
                .map(a -> {
                    StudentResponseDto studentResponseDto = modelMapper.map(a, StudentResponseDto.class);
                    studentResponseDto.setYear(a.getAcademic().getYear());
                    studentResponseDto.setBranch(a.getAcademic().getBranch());
                    studentResponseDto.setSemester(a.getAcademic().getSemester());
                    studentResponseDto.setClassName(a.getAcademic().getClassName());
                    studentResponseDto.setBatch(a.getAcademic().getBatch());
                    studentResponseDto.setCurImage(studentResponseDto.getCurImage());
                    studentResponseDto.setNewImage(studentResponseDto.getNewImage());
                    return studentResponseDto;
                })
                .toList();

        return response;
    }

    public void restoreStudentService(String studentId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findByUserIdAndAdmin_UserId(studentId, adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        student.setDeleted(false);
        student.setDeletedDate(null);
        studentRepo.save(student);
    }

    public List<BasicDataDto> allDeletedTeacherService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<BasicDataDto> response = admin.getTeachers()
                .stream()
                .filter(a -> a.isDeleted())
                .map(a -> modelMapper.map(a, BasicDataDto.class))
                .toList();

        return response;

    }

    public void restoreTeacherService(String teacherId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = teacherRepo.findByUserIdAndAdmin_UserId(teacherId, adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        teacher.setDeleted(false);
        teacher.setDeletedDate(null);
        teacherRepo.save(teacher);
    }

    public List<BasicAttendanceResponseDto> getDeletedAttendance(String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo
                .findByTeacher_Admin_UserIdOrderByAttendanceDateDescAttendanceTimeDesc(adminId);

        List<BasicAttendanceResponseDto> response = attendances
                .stream()
                .filter(a -> a.isDeleted())
                .map(a -> {

                    BasicAttendanceResponseDto temp = modelMapper.map(a, BasicAttendanceResponseDto.class);

                    temp.setTeacherName(a.getTeacher().getName());

                    int total = a.getAttendanceRecords().size();

                    int present = (int) a.getAttendanceRecords()
                            .stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    temp.setTotalStudentCount(total);
                    temp.setPresentStudentCount(present);

                    return temp;
                })
                .toList();
        return response;
    }

    public void updateAttendanceService(UUID attendanceId, SaveAttendanceDto saveAttendanceDto, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));
        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (attendance.isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }
        attendance.setAttendanceDate(saveAttendanceDto.getAttendanceDate());
        attendance.setAttendanceTime(saveAttendanceDto.getAttendanceTime());
        attendance.setSubjectName(saveAttendanceDto.getSubjectName());

        if (saveAttendanceDto.getPresentList() != null) {

            for (String userId : saveAttendanceDto.getPresentList()) {

                AttendanceRecord record = attendanceRecordRepo
                        .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, userId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
                record.setStatus(AttendanceStatus.PRESENT);
            }
        }

        if (saveAttendanceDto.getAbsentList() != null) {

            for (String userId : saveAttendanceDto.getAbsentList()) {

                AttendanceRecord record = attendanceRecordRepo
                        .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, userId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

                record.setStatus(AttendanceStatus.ABSENT);
            }
        }

        attendanceRepo.save(attendance);
        attendanceRepo.save(attendance);
    }

    public void restoreAttendanceService(UUID attendanceId, String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        attendance.setDeleted(false);
        attendance.setDeletedDate(null);
        attendanceRepo.save(attendance);
    }

    public void removeStudentImageService(String userId, String adminId) throws IOException {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findByUserIdAndAdmin_UserId(userId, adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        String defaultUrl = "https://res.cloudinary.com/dzyjaax7p/image/upload/v1773846089/defaultimage_kuxomk.jpg";
        if (student.getCurImage() != null && !student.getCurImage().equals(defaultUrl)) {
            String publicId = cloudinaryService.extractPublicId(student.getCurImage());
            cloudinaryService.deleteImage(publicId);
        }
        student.setCurImage(defaultUrl);
        studentRepo.save(student);
    }

    public void approveImageAllStudentImageService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

       
        List<Student> studentsWithNewImage = admin.getStudents()
                .stream()
                .filter(s -> s.getNewImage() != null)
                .toList();

        if (studentsWithNewImage.isEmpty()) {
            return;
        }
        imageApprovalAsyncService.processImageApprovals(admin, studentsWithNewImage);
    }

   

}
