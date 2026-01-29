const app = document.getElementById("app");
const teacherId = app.dataset.teacherId;

const userId = document.getElementById("userId");
const nameInput = document.getElementById("name");
const email = document.getElementById("email");
const password = document.getElementById("password");
const updateTeacherBtn = document.getElementById("updateTeacherBtn");


document.getElementById("gotoDashboard").addEventListener("click",()=>window.location.href = "/admin/dashboard");


let orgUserId;

async function loadTeacher() {
    const res = await fetch("/api/admin/teacher/" + teacherId);
    const data = await res.json();

    const teacher = data.response;
    userId.value = teacher.userId;
    orgUserId = teacher.userId;
    nameInput.value = teacher.name;
    email.value = teacher.email;
}
loadTeacher();


updateTeacherBtn.onclick = async () => {
    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        password: password.value.trim()
    };

    if (!userData.userId) {
        showSnackbar("Please enter student id", "warning");
        return;
    }
    if(userData.userId!==orgUserId){
        showSnackbar("Don't change student id", "warning");
        return;
    }
    if (!userData.name) {
        showSnackbar("Please enter student name", "warning");
        return;
    } if (!userData.email) {
        showSnackbar("Please enter student email id", "warning");
        return;
    }if (!userData.password) {
        showSnackbar("Please enter password", "warning");
        return;
    }

    updateTeacherBtn.textContent = "Updating...";
    updateTeacherBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/teacher', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSnackbar("Teacher updated successfull", "success");
        } else {
            console.log(data.error)
            switch (data.error) {
                case "ENROLLMENT_NOT_AVAILABLE":
                    showSnackbar("Enrollment no not available", "warning");
                    break;
                default:
                    showSnackbar("Something went wrong. Try again", "warning");
            }
        }

    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }

    updateTeacherBtn.textContent = "Update Teacher";
    updateTeacherBtn.disabled = false;
    loadTeacher();

};



