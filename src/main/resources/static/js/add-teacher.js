const userId = document.getElementById("userId");
const nameInput = document.getElementById("name");
const email = document.getElementById("email");
const password = document.getElementById("password");

document.getElementById("gotoDashboard").addEventListener("click",()=>window.location.href = "/admin/dashboard");


const addTeacherBtn = document.getElementById("addTeacherBtn");

addTeacherBtn.onclick = async () => {
    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        password: password.value.trim(),
    };

    if (!userData.userId) {
        showSnackbar("Please enter teacher id", "warning");
        return;
    }
    if (!userData.name) {
        showSnackbar("Please enter teacher name", "warning");
        return;
    } if (!userData.email) {
        showSnackbar("Please enter teacher email id", "warning");
        return;
    } if (!userData.password) {
        showSnackbar("Please enter password", "warning");
        return;
    }

    addTeacherBtn.textContent = "Adding...";
    addTeacherBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/teacher', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSnackbar("Teacher added successfull", "success");
        } else {
            console.log(data.error)
            switch (data.error) {
                case "USERID_NOT_AVAILABLE":
                    showSnackbar("Please try different teacher id", "warning");
                    break;
                default:
                    showSnackbar("Something went wrong. Try again", "warning");
            }
        }

    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }

    addTeacherBtn.textContent = "Add Teacher";
    addTeacherBtn.disabled = false;

};



