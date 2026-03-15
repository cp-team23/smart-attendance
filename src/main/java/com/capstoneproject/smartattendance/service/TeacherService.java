package com.capstoneproject.smartattendance.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AtteandanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceDto;
import com.capstoneproject.smartattendance.dto.BasicAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceStatus;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.QRDto;
import com.capstoneproject.smartattendance.dto.SaveAttendanceDto;
import com.capstoneproject.smartattendance.dto.SessionDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.dto.TeacherDashboardDto;
import com.capstoneproject.smartattendance.entity.Academic;
import com.capstoneproject.smartattendance.entity.Attendance;
import com.capstoneproject.smartattendance.entity.AttendanceAcademic;
import com.capstoneproject.smartattendance.entity.AttendanceRecord;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.entity.Teacher;
import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.AcademicRepo;
import com.capstoneproject.smartattendance.repository.AttendanceAcademicRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRecordRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRepo;
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.repository.TeacherRepo;
import com.capstoneproject.smartattendance.util.CryptoUtil;
import com.capstoneproject.smartattendance.util.RandomStringUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepo teacherRepo;

    private final AcademicRepo academicRepo;

    private final StudentRepo studentRepo;

    private final AttendanceRepo attendanceRepo;

    private final AttendanceAcademicRepo attendanceAcademicRepo;

    private final AttendanceRecordRepo attendanceRecordRepo;

    private final ModelMapper modelMapper;

    // private final AdminService adminService;

    public BasicDataDto getMyDetailsService(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);
        return basicDataDto;
    }

    // create attendance
    @Transactional
    public UUID createAttendanceService(AttendanceDto attendanceDto, String teacherId) {

        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        String verificationCode = RandomStringUtil.generate(12);
        String key = RandomStringUtil.generate(12);

        Attendance attendace = new Attendance();
        attendace.setTeacher(teacher);
        attendace.setAttendanceDate(attendanceDto.getAttendanceDate());
        attendace.setAttendanceTime(attendanceDto.getAttendaceTime());
        attendace.setVerificationCode(verificationCode);
        attendace.setAttendanceKey(key);
        attendace.setSubjectName(attendanceDto.getSubjectName());
        attendace.setRunning(false);
        attendace.setDeleted(false);
        attendace.setDeletedDate(null);

        List<AttendanceAcademic> attendanceAcademics = new ArrayList<>();
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();

        for (UUID academicId : attendanceDto.getAcademicIds()) {

            Academic academic = academicRepo.findById(academicId)
                    .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

            AttendanceAcademic aa = new AttendanceAcademic();
            aa.setAttendance(attendace);
            aa.setAcademic(academic);
            attendanceAcademics.add(aa);

            List<Student> students = studentRepo.findByAcademic_AcademicId(academicId);

            for (Student student : students) {
                AttendanceRecord record = new AttendanceRecord();
                if (student.isDeleted()) {
                    continue;
                }
                record.setAttendance(attendace);
                record.setStudent(student);
                record.setStatus(AttendanceStatus.ABSENT); // default
                attendanceRecords.add(record);
            }
        }
        attendace.setAttendanceAcademics(attendanceAcademics);
        attendace.setAttendanceRecords(attendanceRecords);
        attendanceRepo.save(attendace);
        return attendace.getAttendanceId();
    }

    // start attendance
    public void startAttendanceService(UUID attendanceId, String teacherId) {
        if (attendanceId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendance.isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }
        attendance.setRunning(true);
        attendanceRepo.save(attendance);
    }

    // refresh attendance
    public QRDto refreshQRCodeService(UUID attendanceId, String teacherId, int refreshTime) {
        if (attendanceId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if (refreshTime == 0) {
            refreshTime = 3;
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendance.isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }
        if (!attendance.isRunning()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_IS_CLOSED);
        }
        long expireTime = Instant.now().plusSeconds(refreshTime).toEpochMilli();
        String key = attendance.getAttendanceKey() + expireTime;
        String encryptedCode = CryptoUtil.encrypt(attendance.getVerificationCode(), key);

        QRDto response = new QRDto();
        response.setAttendanceId(attendanceId);
        response.setExpireTime(expireTime);
        response.setEncryptedCode(encryptedCode);
        return response;
    }

    // stop attendance
    public void stopAttendanceService(UUID attendanceId, String teacherId) {
        if (attendanceId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendance.setRunning(false);
        attendanceRepo.save(attendance);
    }

    public void updateAttendanceService(UUID attendanceId, SaveAttendanceDto saveAttendanceDto, String teacherId) {
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));
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
    }

    // add new academic
    @Transactional
    public void addNewAcademicInAttendanceService(UUID attendanceId, UUID academicId, String teacherId) {
        if (attendanceId == null || academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendance.isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(attendance.getTeacher().getAdmin().getUserId())) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        boolean alreadyAdded = attendanceAcademicRepo
                .existsByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId, academicId);
        if (alreadyAdded) {
            throw new CustomeException(ErrorCode.ACADEMIC_ALREADY_PRESENT);
        }

        AttendanceAcademic aa = new AttendanceAcademic();
        aa.setAttendance(attendance);
        aa.setAcademic(academic);
        attendanceAcademicRepo.save(aa);

        List<Student> students = studentRepo.findByAcademic_AcademicId(academicId);

        for (Student student : students) {
            boolean recordExists = attendanceRecordRepo.existsByAttendance_AttendanceIdAndStudent_UserId(attendanceId,
                    student.getUserId());

            if (!recordExists && !student.isDeleted()) {
                AttendanceRecord record = new AttendanceRecord();
                record.setAttendance(attendance);
                record.setStudent(student);
                record.setStatus(AttendanceStatus.ABSENT);
                attendanceRecordRepo.save(record);
            }
        }
    }

    // remove academic
    @Transactional
    public void removeAcademicInAttendanceService(UUID attendanceId, UUID academicId, String teacherId) {

        if (attendanceId == null || academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendance.isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(attendance.getTeacher().getAdmin().getUserId())) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        boolean alreadyAdded = attendanceAcademicRepo
                .existsByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId, academicId);
        if (!alreadyAdded) {
            throw new CustomeException(ErrorCode.ACADEMIC_NOT_PRESENT);
        }

        attendanceAcademicRepo.deleteByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId, academicId);
        attendanceRecordRepo.deleteByAttendance_AttendanceIdAndStudent_Academic_AcademicId(attendanceId, academicId);

    }

    // delete attendance
    public void deleteAttendanceService(UUID attendanceId, String teacherId) {

        if (attendanceId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendance.setDeleted(true);
        attendance.setDeletedDate(LocalDate.now());
        attendanceRepo.save(attendance);
    }

    // get all attendance
    public List<BasicAttendanceResponseDto> getAllAttendanceService(String teacherId) {

        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

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

    public AtteandanceResponseDto getAttendancByAttendanceIdService(UUID attendanceId, String teacherId) {
        if (attendanceId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendance.isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }
        // ✅ Fetch AttendanceRecord (has both Student + status)
        List<AttendanceRecord> attendanceRecords = attendanceRecordRepo
                .findByAttendance_AttendanceId(attendanceId);

        AtteandanceResponseDto response = new AtteandanceResponseDto();
        response.setAttendanceId(attendance.getAttendanceId());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setAttendanceTime(attendance.getAttendanceTime());
        response.setSubjectName(attendance.getSubjectName());
        response.setRunning(attendance.isRunning());
        response.setTeacherName(attendance.getTeacher().getName());

        // ✅ Map AttendanceRecord → StudentResponseDto with present/absent status
        response.setStudent(attendanceRecords.stream()
                .map(record -> {
                    Student s = record.getStudent(); // get student from record
                    StudentResponseDto res = modelMapper.map(s, StudentResponseDto.class);
                    res.setYear(s.getAcademic().getYear());
                    res.setBranch(s.getAcademic().getBranch());
                    res.setSemester(s.getAcademic().getSemester()); // ✅ fixed: was getBranch()
                    res.setClassName(s.getAcademic().getClassName());
                    res.setBatch(s.getAcademic().getBatch());
                    res.setStatus(record.getStatus()); // ✅ set from AttendanceRecord
                    return res;
                })
                .toList());

        response.setAcademicDatas(attendance.getAttendanceAcademics()
                .stream()
                .map(a -> {

                    AcademicDto res = modelMapper.map(a.getAcademic(), AcademicDto.class);

                    List<Student> students = Optional.ofNullable(a.getAcademic().getStudents())
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
                .toList());

        return response;
    }

    public List<BasicAttendanceResponseDto> getAllAttendanceBySubjectNameService(String subjectName, String teacherId) {

        if (subjectName == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response = attendances
                .stream()
                .filter(a -> a.getSubjectName().equals(subjectName))
                .filter(a -> !a.isDeleted())
                .map(a -> modelMapper.map(a, BasicAttendanceResponseDto.class))
                .toList();
        return response;
    }

    public List<BasicAttendanceResponseDto> getAllAttendanceByDateService(LocalDate date, String teacherId) {
        if (date == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response = attendances
                .stream()
                .filter(a -> a.getAttendanceDate().equals(date))
                .filter(a -> !a.isDeleted())
                .map(a -> modelMapper.map(a, BasicAttendanceResponseDto.class))
                .toList();

        return response;
    }

    public List<BasicAttendanceResponseDto> getAllttendanceByDateAndSubjectNameService(String subjectName,
            LocalDate date, String teacherId) {
        if (subjectName == null || date == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response = attendances
                .stream()
                .filter(a -> a.getSubjectName().equals(subjectName) && a.getAttendanceDate().equals(date))
                .filter(a -> !a.isDeleted())
                .map(a -> modelMapper.map(a, BasicAttendanceResponseDto.class))
                .toList();

        return response;
    }

    public List<AcademicDto> getAcademicDataService(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        return teacher.getAdmin().getAcademicDatas()
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

    // mark presnt student in attendance
    public void markStudentPresentInAttendanceService(UUID attendanceId, String studentId, String teacherId) {
        if (attendanceId == null || studentId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        AttendanceRecord attendanceRecord = attendanceRecordRepo
                .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendanceRecord.getAttendance().isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }
        if (attendanceRecord.getStudent().isDeleted()) {
            throw new CustomeException(ErrorCode.USER_NOT_FOUND);
        }
        attendanceRecord.setStatus(AttendanceStatus.PRESENT);
        attendanceRecordRepo.save(attendanceRecord);
    }

    // mark absent student in attendance
    public void markStudentAbsentInAttendanceService(UUID attendanceId, String studentId, String teacherId) {
        if (attendanceId == null || studentId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        AttendanceRecord attendanceRecord = attendanceRecordRepo
                .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (attendanceRecord.getAttendance().isDeleted()) {
            throw new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND);
        }
        if (attendanceRecord.getStudent().isDeleted()) {
            throw new CustomeException(ErrorCode.USER_NOT_FOUND);
        }

        attendanceRecord.setStatus(AttendanceStatus.ABSENT);
        attendanceRecordRepo.save(attendanceRecord);
    }

    public List<BasicAttendanceResponseDto> getDeletedAttendance(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<BasicAttendanceResponseDto> response = teacher.getAttendance()
                .stream()
                .filter(a -> a.isDeleted())
                .map(a -> {
                    BasicAttendanceResponseDto temp = modelMapper.map(a, BasicAttendanceResponseDto.class);
                    int total = a.getAttendanceRecords().size();

                    int present = (int) a.getAttendanceRecords()
                            .stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    temp.setTotalStudentCount(total);
                    temp.setPresentStudentCount(present);
                    return temp;
                })
                .collect(Collectors.toList());
        return response;
    }

    public void restoreAttendanceService(UUID attendanceId, String teacherId) {
        teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        if (!attendance.getTeacher().getUserId().equals(teacherId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        attendance.setDeleted(false);
        attendance.setDeletedDate(null);
        attendanceRepo.save(attendance);
    }

    public TeacherDashboardDto getAdminDashboardService(String teacherId, int days) {

        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = teacher.getAttendance();

        List<Attendance> activeAttendances = attendances.stream()
                .filter(a -> !a.isDeleted())
                .collect(Collectors.toList());

        int totalSession = activeAttendances.size();

        int activeSession = (int) activeAttendances.stream()
                .filter(Attendance::isRunning)
                .count();

        // calculate average attendance
        double avgAttendance = activeAttendances.stream()
                .mapToDouble(a -> {

                    int total = a.getAttendanceRecords().size();

                    long present = a.getAttendanceRecords()
                            .stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    return total == 0 ? 0 : (present * 100.0) / total;

                })
                .average()
                .orElse(0);

        LocalDate fromDate = LocalDate.now().minusDays(days);

        List<SessionDto> sessions = activeAttendances.stream()
                .filter(a -> !a.getAttendanceDate().isBefore(fromDate))
                .map(a -> {

                    int total = a.getAttendanceRecords().size();

                    long present = a.getAttendanceRecords()
                            .stream()
                            .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                            .count();

                    double percentage = total == 0 ? 0 : (present * 100.0) / total;

                    SessionDto dto = new SessionDto();
                    dto.setDate(a.getAttendanceDate());
                    dto.setSubjectName(a.getSubjectName());
                    dto.setAttendancePercentage(percentage);

                    return dto;

                })
                .collect(Collectors.toList());

        TeacherDashboardDto response = new TeacherDashboardDto();

        response.setTotalSession(totalSession);
        response.setActiveSession(activeSession);
        response.setAvgAttendance((float) avgAttendance);
        response.setSessions(sessions);

        return response;
    }

}