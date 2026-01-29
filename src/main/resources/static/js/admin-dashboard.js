const menuBtn = document.getElementById("menuBtn");
const menuBody = document.getElementById("menuBody");


const userId = document.getElementById("userId");
const nameData = document.getElementById("name");
const collegeName = document.getElementById("collegeName");
const email = document.getElementById("email");

const menu = document.getElementById("menu");

document.getElementById("updateDetails").addEventListener("click",()=>window.location.href="/admin/update");


function redirect(path) {
    window.location.href = path;
}

// STUDENT
document.getElementById("addstudent").addEventListener("click", () => {
    redirect("/admin/student/add");
});

document.getElementById("updatestudent").addEventListener("click", () => {
    redirect("/admin/search-user?role=student");
});

document.getElementById("deletestudent").addEventListener("click", () => {
    redirect("/admin/search-user?role=student");
});

document.getElementById("allstudent").addEventListener("click", () => {
    redirect("/admin/all-students");
});

// TEACHER
document.getElementById("addteacher").addEventListener("click", () => {
    redirect("/admin/teacher/add");
});

document.getElementById("updateteacher").addEventListener("click", () => {
    redirect("/admin/search-user?role=teacher");
});

document.getElementById("deleteteacher").addEventListener("click", () => {
    redirect("/admin/search-user?role=teacher");
});

document.getElementById("allteacher").addEventListener("click", () => {
    redirect("/admin/all-teachers");
});

// COMMON
document.getElementById("searchuser").addEventListener("click", () => {
    redirect("/admin/search-user?role=student");
});

document.getElementById("academicstruct").addEventListener("click", () => {
    redirect("/admin/academic");
});

document.getElementById("imagechange").addEventListener("click", () => {
    redirect("/admin/student-image-requests");
});


menu.addEventListener("click", (e) => {
    e.stopPropagation();
});

async function loadData() {
    try {
        const res = await fetch("/api/admin/my");
        if (!res.ok) throw new Error("Failed to load");

        const data = (await res.json()).response;

        userId.innerHTML = "&nbsp;" + data.userId;
        nameData.innerHTML = "&nbsp;" + data.name;
        email.innerHTML = "&nbsp;" + data.email;
        collegeName.innerHTML = "&nbsp;" + data.collegeName;

    } catch (err) {
        console.error(err);
        showSnackbar("Failed to load profile", "error");
    }
}

loadData();

document.getElementById("logout").addEventListener("click", async () => {
    const response = await fetch('/api/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include'
    });

    if (response.ok) {
        window.location.href = "/auth/login";
    }
});

let clicked = false;

menuBtn.addEventListener("click", () => {
    clicked = !clicked;
    if (clicked) {
        menuBody.style.display = 'flex';
    } else {
        menuBody.style.display = 'none';
    }

    console.log(clicked);
});

menuBody.addEventListener("click", () => {
    menuBody.style.display = 'none';
});

