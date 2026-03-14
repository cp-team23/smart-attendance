/* =========================
   ROUTES CONFIG
========================= */
const routes = {
    dashboard: "/admin/dashboard",
    addUser: "/admin/user/add",
    searchUser: "/admin/user/search",
    userUpdate: (role, key) => `/admin/user/update/${role}/${key}`,
    userUpdate: `/admin/user/update`,

    // dynamic route
    allStudent: (academicId) => `/admin/all-students/${academicId}`,

    allTeacher: "/admin/all-teachers",
    changeAcademic: "/admin/student/academic/change",
    academic: "/admin/academic",
    allAttendance: "/admin/attendance/all",
    recycleBin: "/admin/recycle-bin",
    profile: "/admin/profile",
    imageChangeReq: "/admin/student/image/requests"
};


/* =========================
   SIDEBAR CLICK HANDLER
========================= */
Object.keys(routes).forEach(id => {
    const element = document.getElementById(id);
    if (!element) return;

    element.addEventListener("click", () => {
        if (id === "allStudent") {
            window.location.href = routes.allStudent(null);
            return;
        }
        if (typeof routes[id] === "string") {
            window.location.href = routes[id];
        }
    });
});


/* =========================
   ADMIN PROFILE LOAD
========================= */

const nameData = document.getElementById("adminname");
const collegeName = document.getElementById("admincollegeName");

async function loadAdminProfile() {
    const cached = sessionStorage.getItem("adminProfile");

    if (cached && cached !== "undefined" && cached !== "null") {
        try {
            const data = JSON.parse(cached);
            if (nameData && collegeName) {
                nameData.innerHTML = data.name;
                collegeName.innerHTML = data.collegeName;
            }
            return;
        } catch {
            sessionStorage.removeItem("adminProfile");
        }
    }

    try {
        const res = await fetch("/api/admin/my");

        if (!res.ok) throw new Error("Failed to load profile");

        const data = (await res.json()).response;
        sessionStorage.setItem("adminProfile", JSON.stringify(data));

        nameData.innerHTML = data.name;
        collegeName.innerHTML = data.collegeName;

    } catch (err) {
        console.error(err);
        showSnackbar("Failed to load profile", "error");
    }
}

loadAdminProfile();


/* =========================
   LOGOUT
========================= */

document.getElementById("logout")?.addEventListener("click", async () => {
    showLoader(); // 👈
    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (response.ok) {
            showSuccess(); // 👈 brief flash before redirect
            setTimeout(() => {
                sessionStorage.clear();
                window.location.href = "/auth/login";
            }, 1200); // wait for success animation to finish
        } else {
            removeLoader(); // 👈
            showSnackbar("Logout failed", "error");
        }

    } catch (error) {
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
});


/* =========================
   DELETE STUDENT
========================= */

async function deleteStudent(studentId) {
    const ok = await showConfirm({
        title: "Delete Student",
        message: `Are you sure you want to delete ${studentId}? This action cannot be undone.`,
        confirmText: "Delete"
    });
    if (!ok) return false;

    showLoader(); // 👈
    try {
        const res = await fetch(`/api/admin/student/${studentId}`, {
            method: "DELETE"
        });

        if (!res.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to delete student", "error");
            return false;
        }

        showSuccess(); // 👈
        return true;

    } catch (err) {
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
        return false;
    }
}


/* =========================
   DELETE TEACHER
========================= */

async function deleteTeacher(teacherId) {
    const ok = await showConfirm({
        title: "Delete Teacher",
        message: `Are you sure you want to delete ${teacherId}? This action cannot be undone.`,
        confirmText: "Delete"
    });
    if (!ok) return false;

    showLoader(); // 👈
    try {
        const res = await fetch(`/api/admin/teacher/${teacherId}`, {
            method: "DELETE"
        });

        if (!res.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to delete teacher", "error");
            return false;
        }

        showSuccess(); // 👈
        return true;

    } catch (err) {
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
        return false;
    }
}


/* =========================
   CONFIRM DIALOG
========================= */

function showConfirm({
    icon = "!",
    title = "Confirm Action",
    message = "Are you sure you want to continue?",
    confirmText = "Confirm",
    cancelText = "Cancel"
} = {}) {
    return new Promise((resolve) => {
        const root = document.getElementById("confirm-root");

        root.innerHTML = `
        <div class="confirm-overlay">
            <div class="confirm-box">
                <div class="confirm-icon">${icon}</div>
                <h3>${title}</h3>
                <p>${message}</p>
                <div class="confirm-buttons">
                    <button class="confirm-cancel">${cancelText}</button>
                    <button class="confirm-ok">${confirmText}</button>
                </div>
            </div>
        </div>`;

        root.querySelector(".confirm-cancel").onclick = () => {
            root.innerHTML = "";
            resolve(false);
        };

        root.querySelector(".confirm-ok").onclick = () => {
            root.innerHTML = "";
            resolve(true);
        };
    });
}


/* =========================
   LOADER
========================= */
function showLoader() {
    const root = document.getElementById("loader-root");
    root.innerHTML = `
        <div class="loader-overlay">
            <div class="loader-circle">
                <svg class="loader-ring" viewBox="0 0 50 50">
                    <circle class="ring-track" cx="25" cy="25" r="20" />
                    <circle class="ring-spin" cx="25" cy="25" r="20" />
                </svg>
            </div>
        </div>`;
}

function removeLoader() {
    const root = document.getElementById("loader-root");
    const overlay = root.querySelector(".loader-overlay");
    if (overlay) {
        overlay.classList.add("fade-out");
        setTimeout(() => { root.innerHTML = ""; }, 250);
    } else {
        root.innerHTML = "";
    }
}

function showSuccess() {
    const root = document.getElementById("loader-root");
    root.innerHTML = `
        <div class="loader-overlay">
            <div class="success-circle">
                <svg viewBox="0 0 24 24" class="check">
                    <path d="M20 6L9 17l-5-5" />
                </svg>
            </div>
        </div>`;
    setTimeout(() => {
        const overlay = root.querySelector(".loader-overlay");
        if (overlay) overlay.classList.add("fade-out");
        setTimeout(() => { root.innerHTML = ""; }, 250);
    }, 1000);
}