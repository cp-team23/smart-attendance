const userId = document.getElementById('userId');
const userName = document.getElementById('name');
const emailInput = document.getElementById('email');
const teacherCollegeName = document.getElementById('collegeName');
const curpassword = document.getElementById('curpassword');
const newpassword1 = document.getElementById('newpassword1');
const newpassword2 = document.getElementById('newpassword2');
const updateBtn = document.getElementById("update");

/* =========================
   LOAD PROFILE DATA
========================= */

async function loadData() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/teacher/my");
        const data = await res.json();

        if (res.ok) {
            userId.value = data.userId;
            userName.value = data.name;
            emailInput.value = data.email;
            teacherCollegeName.value = data.collegeName;
            removeLoader(); // 👈
        } else {
            removeLoader(); // 👈
            showSnackbar("Something went wrong. Try again", "error");
        }
    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadData();

/* =========================
   CHANGE PASSWORD
========================= */

async function changePassword() {
    const ok = await showConfirm({
        title: "Update Password ?",
        message: " ",
        confirmText: "Update"
    });
    if (!ok) return;

    const current = curpassword.value.trim();
    const newPass1 = newpassword1.value.trim();
    const newPass2 = newpassword2.value.trim();

    if (!current || !newPass1 || !newPass2) {
        showSnackbar("All fields are required", "error");
        return;
    }

    if (newPass1 !== newPass2) {
        showSnackbar("New passwords do not match", "error");
        return;
    }

    showLoader(); // 👈
    updateBtn.disabled = true;

    try {
        const res = await fetch("/api/user/change-password", {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({
                password: current,
                newPassword: newPass1,
                confirmPassword: newPass2
            })
        });

        const data = await res.json();

        if (res.ok) {
            showSuccess(); // 👈
            showSnackbar("Password updated successfully", "success");
            curpassword.value = "";
            newpassword1.value = "";
            newpassword2.value = "";
        } else {
            removeLoader(); // 👈
            if (data.error === "WRONG_PASSWORD") {
                showSnackbar("Wrong Password", "error");
            } else {
                showSnackbar("Something went wrong", "error");
            }
        }

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    } finally {
        updateBtn.disabled = false; // 👈 always re-enable
    }
}

updateBtn.addEventListener("click", changePassword);