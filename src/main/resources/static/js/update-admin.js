const userId = document.getElementById('userId');
const userName = document.getElementById('name');
const emailInput = document.getElementById('email');
const otp = document.getElementById('otp');
const collegeName = document.getElementById('collegeName');
const password = document.getElementById('password');

const sendOtpBox = document.getElementById("sendOtpBox");

const updateBtn = document.getElementById("update")

const errorBoxOtp = document.getElementById('errorBoxOtp');
const otpBtn = document.getElementById('otpBtn');



let interval = null;



async function loadData() {
    const res = await fetch("/api/admin/my");
    let data = await res.json();
    data = data.response;
    if (res.ok) {
        console.log(data);
        userId.value = data.userId;
        userName.value = data.name;
        emailInput.value = data.email;
        collegeName.value = data.collegeName;
    }
    else {
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadData();

emailInput.addEventListener("change", () => {
    sendOtpBox.style.display = 'flex';
});

function startOtpTimer(durationInSeconds = 120) {

    let timer = durationInSeconds;
    errorBoxOtp.style.display = 'inline';
    errorBoxOtp.style.fontSize = "10px";     // increase text size
    errorBoxOtp.style.marginTop = "3px";    // top margin
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


// --------------------- SEND OTP ---------------------
otpBtn.addEventListener('click', async () => {
    const email = emailInput.value.trim();

    if (email === "") {

        return;
    }

    otpBtn.textContent = "Sending...";
    otpBtn.disabled = true;

    try {
        const response = await fetch('/api/auth/sendotp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        const data = await response.json();

        if (response.ok) {
            errorBoxOtp.style.display = "none";
            otpBtn.textContent = "Resend OTP";
            showSnackbar("OTP sent successfully", "success");
            clearInterval(interval);
            startOtpTimer();
        } else {
            showSnackbar("Failed to send OTP. Try again 2 ", "warning");
            otpBtn.textContent = "Send OTP";
        }
    } catch (err) {
        console.log(err);
        showSnackbar("Failed to send OTP. Try again 3", "warning");
        otpBtn.textContent = "Send OTP";
    }

    otpBtn.disabled = false;
});


//REGISTER
updateBtn.addEventListener('click', async () => {


    const userData = {
        userId: userId.value.trim(),
        name: userName.value.trim(),
        email: emailInput.value.trim(),
        collegeName: collegeName.value.trim(),
        password: password.value.trim()
        // otp: otp.value.trim()
    };

    // validations
    if (!userData.userId) {
        showSnackbar("Don't change user id", "warning");
        return;
    }
    if (!userData.name) {
        showSnackbar("Fill name", "warning");
        return;
    } if (!userData.email) {
        showSnackbar("Fill email", "warning");
        return;
    } if (!userData.collegeName) {
        showSnackbar("Fill College name", "warning");
        return;
    } if (!userData.password) {
        showSnackbar("Confirm with your password", "warning");
        return;
    }
    updateBtn.textContent = "Updating...";
    updateBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        console.log(userData);
        const data = await response.json();
        console.log(response);

        if (response.ok) {
            showSnackbar("Updated successfull", "success");
            setTimeout(() => {
                window.location.href = "/admin/dashboard";
            }, 1000);
        } else {
            console.log(data.error)
            switch (data.error) {
                case "EMAIL_NOT_AVAILABLE":
                    errorBoxEmail.textContent = "Please try different email";
                    break;
                case "OTP_EXPIRED":
                    errorBoxOtp.textContent = "OTP expired! please send again";
                    break;
                case "OTP_INVALID":
                    errorBoxOtp.textContent = "Wrong OTP";
                    break;
                default:
                    showSnackbar("Something went wrong. Try again", "warning");
            }
        }

    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }

    updateBtn.textContent = "Update";
    updateBtn.disabled = false;
});



