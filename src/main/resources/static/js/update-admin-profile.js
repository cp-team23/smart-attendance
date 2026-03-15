const userId = document.getElementById('userId');
const userName = document.getElementById('name');
const emailInput = document.getElementById('email');
const otp = document.getElementById('otp');
const adminCollegeName = document.getElementById('collegeName');
const password = document.getElementById('password');
const sendOtpBox = document.getElementById("sendOtpBox");
const updateBtn = document.getElementById("update");
const errorBoxOtp = document.getElementById('errorBoxOtp');
const otpBtn = document.getElementById('otpBtn');

const newpassword1 = document.getElementById('newpassword1');
const newpassword2 = document.getElementById('newpassword2');

let interval = null;
let originalProfile = {};

/* =========================
   LOAD PROFILE DATA
========================= */

async function loadData() {
    showLoader();
    try {
        const res = await fetch("/api/admin/my");
        let data = await res.json();
        data = data.response;

        if (res.ok) {

            userId.value = data.userId;
            userName.value = data.name;
            emailInput.value = data.email;
            adminCollegeName.value = data.collegeName;

            // store original values
            originalProfile = {
                name: data.name,
                email: data.email,
                collegeName: data.collegeName
            };

            removeLoader();
        } else {
            removeLoader();
            showSnackbar("Something went wrong. Try again", "error");
        }
    } catch (err) {
        console.error(err);
        removeLoader();
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadData();

/* =========================
   EMAIL CHANGE → SHOW OTP BOX
========================= */

emailInput.addEventListener("change", () => {
    sendOtpBox.style.display = 'flex';
});

/* =========================
   OTP TIMER
========================= */

function startOtpTimer(durationInSeconds = 120) {

    let timer = durationInSeconds;

    errorBoxOtp.style.display = 'inline';
    errorBoxOtp.style.fontSize = "10px";
    errorBoxOtp.style.marginTop = "3px";
    errorBoxOtp.style.marginBottom = "1px";

    interval = setInterval(() => {

        const minutes = Math.floor(timer / 60);
        const seconds = timer % 60;

        errorBoxOtp.textContent =
            `OTP expires in ${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;

        if (timer <= 0) {
            errorBoxOtp.textContent = "Resend OTP";
            errorBoxOtp.style.color = "red";
        }

        timer--;

    }, 1000);
}

/* =========================
   SEND OTP
========================= */

otpBtn.addEventListener('click', async () => {

    const email = emailInput.value.trim();
    if (!email) return;

    showLoader();
    otpBtn.disabled = true;

    try {

        const response = await fetch('/api/auth/sendotp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        const data = await response.json();

        if (response.ok) {

            removeLoader();
            otpBtn.textContent = "Resend OTP";
            errorBoxOtp.style.display = "none";

            showSnackbar("OTP sent successfully", "success");

            clearInterval(interval);
            startOtpTimer();

        } else {

            removeLoader();
            otpBtn.textContent = "Send OTP";
            showSnackbar("Failed to send OTP. Try again", "warning");

        }

    } catch (err) {

        console.error(err);
        removeLoader();
        otpBtn.textContent = "Send OTP";
        showSnackbar("Failed to send OTP. Try again", "warning");

    }

    otpBtn.disabled = false;

});

/* =========================
   CHANGE DETECTION
========================= */

function isProfileChanged() {

    return (
        userName.value.trim() !== originalProfile.name ||
        emailInput.value.trim() !== originalProfile.email ||
        adminCollegeName.value.trim() !== originalProfile.collegeName
    );

}

function isPasswordChanged() {

    return (
        newpassword1.value.trim() ||
        newpassword2.value.trim()
    );

}

/* =========================
   CHANGE PASSWORD
========================= */

async function changePassword() {

    const current = password.value.trim();
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

    const ok = await showConfirm({
        title: "Update Password ?",
        message: " ",
        confirmText: "Update"
    });

    if (!ok) return;

    showLoader();
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

            showSuccess();
            showSnackbar("Password updated successfully", "success");

            password.value = "";
            newpassword1.value = "";
            newpassword2.value = "";

        } else {

            removeLoader();

            if (data.error === "WRONG_PASSWORD") {
                showSnackbar("Wrong Password", "error");
            } else {
                showSnackbar("Something went wrong", "error");
            }

        }

    } catch (err) {

        console.error(err);
        removeLoader();
        showSnackbar("Something went wrong", "error");

    } finally {

        updateBtn.disabled = false;

    }
}

/* =========================
   UPDATE PROFILE
========================= */

async function updateProfile() {

    const userData = {

        userId: userId.value.trim(),
        name: userName.value.trim(),
        email: emailInput.value.trim(),
        collegeName: adminCollegeName.value.trim(),
        password: password.value.trim(),
        otp: otp.value.trim()

    };

    if (!userData.userId) return showSnackbar("Don't change user id", "warning");
    if (!userData.name) return showSnackbar("Fill name", "warning");
    if (!userData.email) return showSnackbar("Fill email", "warning");
    if (!userData.collegeName) return showSnackbar("Fill College name", "warning");
    if (!userData.password) return showSnackbar("Confirm with your password", "warning");

    const ok = await showConfirm({
        title: "Update Profile ?",
        message: "Are you sure you want to update profile?",
        confirmText: "Update"
    });

    if (!ok) return;

    showLoader();
    updateBtn.disabled = true;

    try {

        const response = await fetch('/api/admin/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {

            showSuccess();
            showSnackbar("Updated successfully", "success");

            // update original values
            originalProfile.name = userData.name;
            originalProfile.email = userData.email;
            originalProfile.collegeName = userData.collegeName;
            nameData.innerHTML = userData.name;
            collegeName.innerHTML = userData.collegeName;
            password.value = "";
            newpassword1.value = "";
            newpassword2.value = "";

        } else {

            removeLoader();

            switch (data.error) {

                case "EMAIL_NOT_AVAILABLE":
                    showSnackbar("Please try different email", "error");
                    break;

                case "OTP_EXPIRED":
                    showSnackbar("OTP expired! Please send again", "error");
                    break;

                case "OTP_INVALID":
                    showSnackbar("Wrong OTP", "error");
                    break;

                case "WRONG_PASSWORD":
                    showSnackbar("Wrong Password", "error");
                    break;

                default:
                    showSnackbar("Something went wrong. Try again", "warning");

            }

        }

    } catch (err) {

        console.error(err);
        removeLoader();
        showSnackbar("Something went wrong. Try again", "error");

    } finally {

        updateBtn.disabled = false;

    }
}

/* =========================
   UPDATE BUTTON LOGIC
========================= */

updateBtn.addEventListener('click', async () => {

    const profileChanged = isProfileChanged();
    const passwordChanged = isPasswordChanged();

    if (!profileChanged && !passwordChanged) {

        showSnackbar("Nothing changed", "warning");
        return;

    }

    if (profileChanged) {
        await updateProfile();
    }

    if (passwordChanged) {
        await changePassword();
    }

});