

const userId = document.getElementById('userId');
const userName = document.getElementById('name');
const emailInput = document.getElementById('email');
const teacherCollegeName = document.getElementById('collegeName');
const curpassword = document.getElementById('curpassword');
const newpassword1 = document.getElementById('newpassword1');
const newpassword2 = document.getElementById('newpassword2');

const updateBtn = document.getElementById("update")





async function loadData() {
    const res = await fetch("/api/teacher/my");
    let data = await res.json();
    if (res.ok) {
        console.log(data);
        userId.value = data.userId;
        userName.value = data.name;
        emailInput.value = data.email;
        teacherCollegeName.value = data.collegeName;
    }
    else {
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadData();

async function changePassword() {

    const current = curpassword.value.trim();
    const newPass1 = newpassword1.value.trim();
    const newPass2 = newpassword2.value.trim();

    // Validation
    if (!current || !newPass1 || !newPass2) {
        showSnackbar("All fields are required", "error");
        return;
    }

    if (newPass1 !== newPass2) {
        showSnackbar("New passwords do not match", "error");
        return;
    }


    try {
        const res = await fetch("/api/user/change-password", {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                password: current,
                newPassword: newPass1,
                confirmPassword: newPass2
            })
        });

        const data = await res.json();
        if (res.ok) {
            showSnackbar("Password updated successfully", "success");

            // Clear fields
            curpassword.value = "";
            newpassword1.value = "";
            newpassword2.value = "";

        } else {
            if(data.error ==="WRONG_PASSWORD"){
                showSnackbar("Wrong Password", "error");
            }
            
        }

    } catch (err) {
        console.error(err);
        showSnackbar("Something went wrong", "error");
    }
}

updateBtn.addEventListener("click", changePassword);

