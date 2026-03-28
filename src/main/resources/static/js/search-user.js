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
                    <button class="delete-btn" id="deleteImageBtn">Delete Photo</button>
                    <button class="delete-btn" id="deleteBtn">Delete</button>
                </div>
            </div>
        </div>
    `;

    deleteBtn = document.getElementById("deleteBtn");
    updateBtn = document.getElementById("updateBtn");
    deleteImageBtn = document.getElementById("deleteImageBtn");

    deleteBtn.addEventListener("click", async () => {
        const res = await deleteStudent(student.userId); // loader handled inside deleteStudent (global.js)
        if (res) {
            cardsContainer.innerHTML = "";
        }
    });
    deleteImageBtn.addEventListener("click", async () => {
        const ok = await showConfirm({
            title: "Delete Student Image ?",
            message: `Are you sure you want to delete ${student.userId} image ? This action cannot be undone.`,
            confirmText: "Delete"
        });
        if (!ok) return false;
        try {
            showLoader(); 
            const res = await fetch(`/api/admin/student/image/${student.userId}`, {
                method: "DELETE"
            });

            if (!res.ok) {
                removeLoader(); // 👈
                showSnackbar("Failed to delete Student Photo", "error");
                return false;
            }
            searchUser();
            showSuccess();
            showSnackbar("Student Photo Deleted Successfully", "success");
        } catch (err) {
            removeLoader();
            showSnackbar("Something went wrong", "error");
        }
    });

    updateBtn.addEventListener("click", () => {
        window.location.href = "/admin/user/update/student/" + student.enrollmentNo;
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

    deleteBtn.addEventListener("click", async () => {
        const res = await deleteTeacher(teacher.userId); // loader handled inside deleteTeacher (global.js)
        if (res) {
            cardsContainer.innerHTML = "";
        }
    });

    updateBtn.addEventListener("click", () => {
        window.location.href = "/admin/user/update/teacher/" + teacher.userId;
    });
}

/* =========================
   SEARCH USER
========================= */

async function searchUser() {
    const value = searchInput.value.trim();
    if (!value) return;

    const API_URL = role === 'teacher'
        ? '/api/admin/teacher/'
        : '/api/admin/student/';

    showLoader(); // 👈
    try {
        searchInput.disabled = true;

        const res = await fetch(API_URL + value);
        const data = await res.json();

        if (!res.ok) {
            removeLoader(); // 👈
            if (data.error === "USER_NOT_FOUND") {
                cardsContainer.innerHTML = `<p class="notfound">No user found</p>`;
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

        removeLoader(); // 👈

    } catch (err) {
        console.log(err);
        removeLoader(); // 👈
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
    if (e.key === 'Enter') searchUser();
});

/* =========================
   DEFAULT INIT
========================= */

setRole('student');