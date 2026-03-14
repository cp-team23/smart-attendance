const teacherBtn = document.getElementById('teacherBtn');
const studentBtn = document.getElementById('studentBtn');
const attendanceBtn = document.getElementById('attendanceBtn');
const cardsContainer = document.getElementById('cardsContainer');
const searchInput = document.getElementById("searchInput");
let role = 'student';
let allData = [];

/* ================= ROLE SWITCHING ================= */

function setRole(role) {
    [teacherBtn, studentBtn, attendanceBtn].forEach(btn => btn.classList.remove('active'));

    if (role === 'teacher') {
        teacherBtn.classList.add('active');
        searchInput.placeholder = "Enter Teacher Id";
        loadTeacher();
    } else if (role === 'student') {
        studentBtn.classList.add('active');
        searchInput.placeholder = "Enter Student Enrollment No";
        loadStudent();
    } else if (role === 'attendance') {
        attendanceBtn.classList.add('active');
        searchInput.placeholder = "Enter subject name";
        loadAttendance();
    }
}

loadStudent();

teacherBtn.addEventListener('click', () => setRole(role = 'teacher'));
studentBtn.addEventListener('click', () => setRole(role = 'student'));
attendanceBtn.addEventListener('click', () => setRole(role = 'attendance'));

/* ================= STUDENT ================= */

function showData(student) {
    let html = "";
    student.forEach(element => {
        html += `<div class="student-row" data-id="${element.userId}">
            <div class="student-profile">
                <img src="${element.curImage}" alt="Student">
                <div>
                    <div class="student-name">${element.name}</div>
                    <div class="det">
                        <div class="student-email">${element.userId}</div>
                        <div class="student-field">Enrollment: ${element.enrollmentNo}</div>
                        <div class="student-field">Email : ${element.email}</div>
                    </div>
                </div>
            </div>
            <div class="student-actions">
                <button class="update-btn" onclick="recycleStudent('${element.userId}')">
                    Restore
                </button>
            </div>
        </div>`;
    });
    cardsContainer.innerHTML = html;
}

async function loadStudent() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/all-deleted-student");
        const data = await res.json();

        if (res.ok) {
            allData = data.response;
            if (allData.length === 0) {
                cardsContainer.innerHTML = `<p class="notfound">No student found</p>`;;
                removeLoader(); // 👈
                showSnackbar("No deleted students found.", "warning");
                return;
            }
            showData(allData);
            removeLoader(); // 👈
        } else {
            removeLoader(); // 👈
            showSnackbar("Something went wrong. Try again", "warning");
        }
    } catch (e) {
        console.log(e);
        removeLoader(); // 👈
        showSnackbar("Something went wrong. Try again", "error");
    }
}

async function recycleStudent(studentId) {
    const ok = await showConfirm({
        title: "Restore Student ?",
        message: "Are you sure you want to restore student?",
        confirmText: "Restore"
    });
    if (!ok) return;

    showLoader(); // 👈
    try {
        const response = await fetch(`/api/admin/student/restore/${studentId}`, {
            method: "PATCH",
            credentials: "include"
        });

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to restore student", "warning");
            return;
        }

        showSuccess(); // 👈
        showSnackbar("Student restored successfully", "success");
        loadStudent(); // triggers its own loader cycle

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
}

/* ================= TEACHER ================= */

function showDataTeacher(teacher) {
    let html = "";
    teacher.forEach(element => {
        const initials = element.name
            .split(" ")
            .map(word => word[0])
            .join("")
            .toUpperCase();

        html += `<div class="student-row" data-id="${element.userId}">
            <div class="student-profile">
                <div class="avatar">${initials}</div>
                <div>
                    <div class="student-name">${element.name}</div>
                    <div class="det">
                        <div class="student-email">${element.userId}</div>
                        <div class="student-field">Email : ${element.email}</div>
                    </div>
                </div>
            </div>
            <div class="student-actions">
                <button class="update-btn" onclick="recycleTeacher('${element.userId}')">
                    Restore
                </button>
            </div>
        </div>`;
    });
    cardsContainer.innerHTML = html;
}

async function loadTeacher() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/all-deleted-teacher");
        const data = await res.json();

        if (res.ok) {
            allData = data.response;
            if (allData.length === 0) {
                cardsContainer.innerHTML = `<p class="notfound">No teacher found</p>`;
                removeLoader(); // 👈
                showSnackbar("No deleted teachers found.", "warning");
                return;
            }
            showDataTeacher(allData);
            removeLoader(); // 👈
        } else {
            removeLoader(); // 👈
            showSnackbar("Something went wrong. Try again", "warning");
        }
    } catch (e) {
        console.log(e);
        removeLoader(); // 👈
        showSnackbar("Something went wrong. Try again", "error");
    }
}

async function recycleTeacher(teacherId) {
    const ok = await showConfirm({
        title: "Restore Teacher ?",
        message: "Are you sure you want to restore teacher?",
        confirmText: "Restore"
    });
    if (!ok) return;

    showLoader(); // 👈
    try {
        const response = await fetch(`/api/admin/teacher/restore/${teacherId}`, {
            method: "PATCH",
            credentials: "include"
        });

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to restore teacher", "warning");
            return;
        }

        showSuccess(); // 👈
        showSnackbar("Teacher restored successfully", "success");
        loadTeacher(); // triggers its own loader cycle

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
}

/* ================= ATTENDANCE ================= */

async function loadAttendance() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/all-deleted-attendance");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        allData = data.response;

        if (allData.length === 0) {
            cardsContainer.innerHTML = `<p class="notfound">No attendance found</p>`;
            removeLoader(); // 👈
            return;
        }

        renderAttendance(allData);
        removeLoader(); // 👈

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Failed to load attendance", "error");
    }
}

function renderAttendance(list) {
    cardsContainer.innerHTML = "";

    if (list.length === 0) {
        cardsContainer.innerHTML = `<p class=notfound">No attendance found</p>`;
        return;
    }

    let html = "";
    list.forEach(item => {
        html += `<div class="attendance-card">
            <div class="card-header">
                <h3>${item.subjectName}</h3>
                <span class="status ${item.running ? "running" : "stopped"}">
                    ${item.running ? "Running" : "Closed"}
                </span>
            </div>
            <div class="card-body">
                <p><strong>Date:</strong> ${item.attendanceDate}</p>
                <p><strong>Time:</strong> ${item.attendanceTime}</p>
                <p><strong>Teacher:</strong> ${item.teacherName}</p>
            </div>
            <div class="card-actions">
                <button class="view-btn" onclick="recycleAttendance('${item.attendanceId}')">
                    Restore
                </button>
            </div>
        </div>`;
    });
    cardsContainer.innerHTML = html;
}

async function recycleAttendance(attendanceId) {
    const ok = await showConfirm({
        title: "Restore Attendance ?",
        message: "Are you sure you want to restore attendance?",
        confirmText: "Restore"
    });
    if (!ok) return;

    showLoader(); // 👈
    try {
        const response = await fetch(`/api/admin/attendance/restore/${attendanceId}`, {
            method: "PATCH",
            credentials: "include"
        });

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to restore attendance", "warning");
            return;
        }

        showSuccess(); // 👈
        showSnackbar("Attendance restored successfully", "success");
        loadAttendance(); // triggers its own loader cycle

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
}

/* ================= SEARCH ================= */

searchInput.addEventListener("input", function () {
    const searchValue = this.value.toLowerCase().trim();

    if (!searchValue) {
        if (role === 'student') showData(allData);
        else if (role === 'teacher') showDataTeacher(allData);
        else if (role === 'attendance') renderAttendance(allData);
        return;
    }

    if (role === 'student') {
        const filtered = allData.filter(element =>
            element.name.toLowerCase().includes(searchValue) ||
            element.userId.toLowerCase().includes(searchValue) ||
            element.enrollmentNo.toLowerCase().includes(searchValue) ||
            element.email.toLowerCase().includes(searchValue)
        );
        showData(filtered);
    }

    if (role === 'teacher') {
        const filtered = allData.filter(element =>
            element.name.toLowerCase().includes(searchValue) ||
            element.userId.toLowerCase().includes(searchValue) ||
            element.email.toLowerCase().includes(searchValue)
        );
        showDataTeacher(filtered);
    }

    if (role === 'attendance') {
        const filtered = allData.filter(item =>
            item.subjectName.toLowerCase().includes(searchValue) ||
            item.teacherName.toLowerCase().includes(searchValue) ||
            item.attendanceDate.includes(searchValue) ||
            item.attendanceTime.includes(searchValue)
        );
        renderAttendance(filtered);
    }
});