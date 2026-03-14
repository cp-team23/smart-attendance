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

let interval = null;

/* =========================
   LOAD PROFILE DATA
========================= */

async function loadData() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/my");
        let data = await res.json();
        data = data.response;

        if (res.ok) {
            userId.value = data.userId;
            userName.value = data.name;
            emailInput.value = data.email;
            adminCollegeName.value = data.collegeName;
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

    showLoader(); // 👈
    otpBtn.disabled = true;

    try {
        const response = await fetch('/api/auth/sendotp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        const data = await response.json();

        if (response.ok) {
            removeLoader(); // 👈 (no success animation — OTP send is not a final action)
            otpBtn.textContent = "Resend OTP";
            errorBoxOtp.style.display = "none";
            showSnackbar("OTP sent successfully", "success");
            clearInterval(interval);
            startOtpTimer();
        } else {
            removeLoader(); // 👈
            otpBtn.textContent = "Send OTP";
            showSnackbar("Failed to send OTP. Try again", "warning");
        }

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        otpBtn.textContent = "Send OTP";
        showSnackbar("Failed to send OTP. Try again", "warning");
    }

    otpBtn.disabled = false;
});

/* =========================
   UPDATE PROFILE
========================= */

updateBtn.addEventListener('click', async () => {
    const ok = await showConfirm({
        title: "Update Profile ?",
        message: "Are you sure you want to update profile?",
        confirmText: "Update"
    });
    if (!ok) return;

    const userData = {
        userId: userId.value.trim(),
        name: userName.value.trim(),
        email: emailInput.value.trim(),
        collegeName: adminCollegeName.value.trim(),
        password: password.value.trim(),
        otp: otp.value.trim()
    };

    // Validations before loader
    if (!userData.userId) return showSnackbar("Don't change user id", "warning");
    if (!userData.name) return showSnackbar("Fill name", "warning");
    if (!userData.email) return showSnackbar("Fill email", "warning");
    if (!userData.collegeName) return showSnackbar("Fill College name", "warning");
    if (!userData.password) return showSnackbar("Confirm with your password", "warning");

    showLoader(); // 👈
    updateBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSuccess(); // 👈
            showSnackbar("Updated successfully", "success");
            sessionStorage.clear();
        } else {
            removeLoader(); // 👈
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
        removeLoader(); // 👈
        showSnackbar("Something went wrong. Try again", "error");
    } finally {
        updateBtn.disabled = false; // 👈 always re-enable
    }
});