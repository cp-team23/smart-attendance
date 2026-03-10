const teacherBtn = document.getElementById('teacherBtn');
const studentBtn = document.getElementById('studentBtn');
const cardsContainer = document.getElementById('cardsContainer');
const searchInput = document.getElementById("searchInput");

let role = 'student';

let deleteBtn = null;
let updateBtn = null;

/* =========================
   ROLE SWITCHING
========================= */

function setRole(newRole) {

    role = newRole;

    [teacherBtn, studentBtn].forEach(btn =>
        btn.classList.remove('active')
    );

    if (role === 'teacher') {
        teacherBtn.classList.add('active');
        searchInput.placeholder = "Enter Teacher Id";
    } else {
        studentBtn.classList.add('active');
        searchInput.placeholder = "Enter Student Enrollment No";
    }

    // Reset UI when switching role
    cardsContainer.innerHTML = "";
    searchInput.value = "";
}


/* =========================
   SHOW STUDENT CARD
========================= */

function showStudent(student) {

    cardsContainer.innerHTML = `
        <div class="user-card">

            <div class="user-image">
                <img src="${student.curImage}" alt="Profile Image">
            </div>

            <div class="user-details">
                <h2>${student.name}</h2>
                <div class="user-college">${student.collegeName}</div>

                <div class="details-grid">
                    <div class="label">User ID</div>
                    <div class="value">${student.userId}</div>

                    <div class="label">Email</div>
                    <div class="value">${student.email}</div>

                    <div class="label">Enrollment No</div>
                    <div class="value">${student.enrollmentNo}</div>

                    <div class="label">Year</div>
                    <div class="value">${student.year}</div>

                    <div class="label">Branch</div>
                    <div class="value">${student.branch}</div>

                    <div class="label">Semester</div>
                    <div class="value">${student.semester}</div>

                    <div class="label">Class</div>
                    <div class="value">${student.className}</div>

                    <div class="label">Batch</div>
                    <div class="value">${student.batch}</div>
                </div>

                <div class="card-buttons">
                    <button class="update-btn" id="updateBtn">Edit</button>
                    <button class="delete-btn" id="deleteBtn">Delete</button>
                </div>
            </div>
        </div>
    `;

    deleteBtn = document.getElementById("deleteBtn");
    updateBtn = document.getElementById("updateBtn");

    deleteBtn.addEventListener("click", () =>
        deleteStudent(student.userId).then(() => cardsContainer.innerHTML = "" )
    );

    updateBtn.addEventListener("click", () => {
        window.location.href =
            "/admin/user/update/student/" + student.enrollmentNo;
    });
}


/* =========================
   SHOW TEACHER CARD
========================= */

function showTeacher(teacher) {

    cardsContainer.innerHTML = `
        <div class="teacher-card">

            <h2>${teacher.name}</h2>
            <div class="teacher-college">${teacher.collegeName}</div>

            <div class="teacher-details">
                <div class="teacher-label">User ID</div>
                <div class="teacher-value">${teacher.userId}</div>

                <div class="teacher-label">Email</div>
                <div class="teacher-value">${teacher.email}</div>
            </div>

            <div class="teacher-buttons">
                <button class="update-btn" id="updateBtn">Edit</button>
                <button class="delete-btn" id="deleteBtn">Delete</button>
            </div>

        </div>
    `;

    deleteBtn = document.getElementById("deleteBtn");
    updateBtn = document.getElementById("updateBtn");

    deleteBtn.addEventListener("click", () =>
        deleteTeacher(teacher.userId).then(() => cardsContainer.innerHTML = "" )
    );

    updateBtn.addEventListener("click", () => {
        window.location.href =
            "/admin/user/update/teacher/" + teacher.userId;
    });
}


/* =========================
   SEARCH USER
========================= */

async function searchUser() {

    const value = searchInput.value.trim();
    if (!value) return;

    const API_URL =
        role === 'teacher'
            ? '/api/admin/teacher/'
            : '/api/admin/student/';

    try {

        searchInput.disabled = true;

        const res = await fetch(API_URL + value);
        const data = await res.json();

        if (!res.ok) {

            if (data.error === "USER_NOT_FOUND") {
                showSnackbar("User not found", "warning");
            } else {
                showSnackbar("Something went wrong", "error");
            }

            return;
        }

        if (role === 'teacher') {
            showTeacher(data.response);
        } else {
            showStudent(data.response);
        }

    } catch (err) {

        console.log(err);
        showSnackbar("Something went wrong. Try again", "error");

    } finally {
        searchInput.disabled = false;
    }
}


/* =========================
   EVENTS
========================= */

teacherBtn.addEventListener('click', () => setRole('teacher'));
studentBtn.addEventListener('click', () => setRole('student'));

searchInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        searchUser();
    }
});


/* =========================
   DEFAULT INIT
========================= */

setRole('student');