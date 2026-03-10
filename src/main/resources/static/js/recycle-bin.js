const teacherBtn = document.getElementById('teacherBtn');
const studentBtn = document.getElementById('studentBtn');
const attendanceBtn = document.getElementById('attendanceBtn');
const cardsContainer = document.getElementById('cardsContainer');
const searchInput = document.getElementById("searchInput");
let role = 'student';


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
    }
    else if (role === 'attendance') {
        attendanceBtn.classList.add('active');
        searchInput.placeholder = "Enter subject name";
        loadAttendance();
    }
}
loadStudent();

teacherBtn.addEventListener('click', () => setRole(role = 'teacher'));
studentBtn.addEventListener('click', () => setRole(role = 'student'));
attendanceBtn.addEventListener('click', () => setRole(role = 'attendance'));


function showData(student) {
    let html = "";
    student.forEach(element => {
        html += `<div class="student-row" data-id="${element.userId}" >

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
    try {
        const res = await fetch("/api/admin/all-deleted-student");
        const data = await res.json();
        if (res.ok) {
            if (data.response.length === 0) {
                cardsContainer.innerHTML = "";
                showSnackbar("Student Not found.", "success");
            }
            allData = data.response;
            showData(allData);
        }
        else {
            showSnackbar("Something went wrong. Try again", "warning");
            return;

        }
    } catch (e) {
        console.log(e);
        showSnackbar("Something went wrong. Try again ", "error");
    }
}

async function recycleStudent(studentId) {

    try {
        const response = await fetch(
            `/api/admin/student/restore/${studentId}`,
            {
                method: "PATCH",
                credentials: "include"
            }
        );

        const data = await response.json();

        if (!response.ok) {
            showSnackbar("Failed to restore student", "warning");
            return;
        }

        showSnackbar("Student restored successfully", "success");

        // reload list after restore
        loadStudent();

    } catch (error) {
        console.error(error);
        showSnackbar("Something went wrong", "error");
    }
}



function showDataTeacher(teacher) {
    let html = "";
    teacher.forEach(element => {

        const initials = element.name
            .split(" ")
            .map(word => word[0])
            .join("")
            .toUpperCase();

        html += `<div class="student-row" data-id="${element.userId}" >

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
                <button class="update-btn" onclick="recycleTeacher('${element.userId}')" >
                    Restore
                </button>
            </div>

        </div>`;
    });

    cardsContainer.innerHTML = html;
}

async function loadTeacher() {
    try {
        const res = await fetch("/api/admin/all-deleted-teacher");
        const data = await res.json();
        if (res.ok) {
            if (data.response.length === 0) {
                cardsContainer.innerHTML = "";
                showSnackbar("Teacher Not found.", "success");
            }
            allData = data.response;
            showDataTeacher(allData);
        }
        else {
            showSnackbar("Something went wrong. Try again", "warning");
            return;

        }
    } catch (e) {
        console.log(e);
        showSnackbar("Something went wrong. Try again ", "error");
    }
}


async function loadAttendance() {

    try {

        const res = await fetch("/api/admin/all-deleted-attendance");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        allData = data.response;

        renderAttendance(allData);

    } catch (err) {
        console.error(err);
        showSnackbar("Failed to load attendance", "error");
    }
}

function renderAttendance(list) {

    cardsContainer.innerHTML = "";

    if (list.length === 0) {
        cardsContainer.innerHTML = "<p>No attendance found</p>";
        return;
    }
    html = "";
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
                </div>
        `;
    });
    cardsContainer.innerHTML = html;
}


async function recycleAttendance(attendanceId) {

    try {
        const response = await fetch(
            `/api/admin/attendance/restore/${attendanceId}`,
            {
                method: "PATCH",
                credentials: "include"
            }
        );

        const data = await response.json();

        if (!response.ok) {
            showSnackbar("Failed to restore attendance", "warning");
            return;
        }

        showSnackbar("attendance restored successfully", "success");

        // reload list after restore
        loadAttendance();

    } catch (error) {
        console.error(error);
        showSnackbar("Something went wrong", "error");
    }
}




async function recycleTeacher(teacherId) {

    try {
        const response = await fetch(
            `/api/admin/teacher/restore/${teacherId}`,
            {
                method: "PATCH",
                credentials: "include"
            }
        );

        const data = await response.json();

        if (!response.ok) {
            showSnackbar("Failed to restore student", "warning");
            return;
        }

        showSnackbar("Teacher restored successfully", "success");

        // reload list after restore
        loadTeacher();

    } catch (error) {
        console.error(error);
        showSnackbar("Something went wrong", "error");
    }
}




searchInput.addEventListener("input", function () {

    const searchValue = this.value.toLowerCase().trim();

    if (!searchValue) {
        if (role === 'student') {
            showData(allData);
        }
        else if (role === 'teacher') {
            showDataTeacher(allData);
        }
        else if (role === 'attendance') {
            renderAttendance(allData);
        }
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





