/* =========================
   ROUTES CONFIG
========================= */
const routes = {
    dashboard: "/teacher/dashboard",
    newsession:"/teacher/attendance/new",
    profile: "/teacher/profile",
    allAttendance:"/teacher/all-attendance",
    recycleBin:"/teacher/recycle-bin"
};


/* =========================
   SIDEBAR CLICK HANDLER
========================= */
Object.keys(routes).forEach(id => {

    const element = document.getElementById(id);
    if (!element) return;

    element.addEventListener("click", () => {

        // Normal static routes
        if (typeof routes[id] === "string") {
            window.location.href = routes[id];
        }
    });

});


/* =========================
   ADMIN PROFILE LOAD
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
        const data = json.response ?? json; // supports both formats

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
    console.log("hello");

    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (response.ok) {
            sessionStorage.clear();
            window.location.href = "/auth/login";
        } else {
            showSnackbar("Logout failed", "error");
        }

    } catch (error) {
        showSnackbar("Something went wrong", "error");
    }
});