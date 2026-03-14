/* =========================
   ROUTES CONFIG
========================= */
const routes = {
    dashboard: "/teacher/dashboard",
    newsession: "/teacher/attendance/new",
    profile: "/teacher/profile",
    allAttendance: "/teacher/all-attendance",
    recycleBin: "/teacher/recycle-bin"
};

/* =========================
   SIDEBAR CLICK HANDLER
========================= */
Object.keys(routes).forEach(id => {
    const element = document.getElementById(id);
    if (!element) return;

    element.addEventListener("click", () => {
        if (typeof routes[id] === "string") {
            window.location.href = routes[id];
        }
    });
});

/* =========================
   TEACHER PROFILE LOAD
========================= */

const nameData = document.getElementById("teachername");
const collegeName = document.getElementById("teachercollegeName");

async function loadTeacherProfile() {
    const cached = sessionStorage.getItem("teacherProfile");

    if (cached && cached !== "undefined" && cached !== "null") {
        try {
            const data = JSON.parse(cached);
            if (nameData && collegeName) {
                nameData.innerHTML = data.name;
                collegeName.innerHTML = data.collegeName;
            }
            return;
        } catch (e) {
            console.warn("Invalid cached JSON, clearing...");
            sessionStorage.removeItem("teacherProfile");
        }
    }

    try {
        const res = await fetch("/api/teacher/my");

        if (!res.ok) throw new Error("Failed to load profile");

        const json = await res.json();
        const data = json.response ?? json;

        sessionStorage.setItem("teacherProfile", JSON.stringify(data));

        if (nameData && collegeName) {
            nameData.innerHTML = data.name;
            collegeName.innerHTML = data.collegeName;
        }

    } catch (err) {
        console.error(err);
        showSnackbar("Failed to load profile", "error");
    }
}

loadTeacherProfile();

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
            showSuccess(); // 👈
            setTimeout(() => {
                sessionStorage.clear();
                window.location.href = "/auth/login";
            }, 1200); // wait for success animation
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