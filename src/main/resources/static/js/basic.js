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

        // Special handling for allStudent (needs academicId)
        if (id === "allStudent") {
            window.location.href = routes.allStudent(null);
            return;
        }

        // Normal static routes
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


async function deleteStudent(studentId) {
    await fetch(`/api/admin/student/${studentId}`, {
        method: "DELETE"
    });
}

async function deleteTeacher(teacherId) {
    await fetch(`/api/admin/teacher/${teacherId}`, {
        method: "DELETE"
    });

}
