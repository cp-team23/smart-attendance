const userId = document.getElementById('userId');
const userName = document.getElementById('name');
const emailInput = document.getElementById('email');
const otp = document.getElementById('otp');
const collegeName = document.getElementById('collegeName');
const password = document.getElementById('password');
const confirmPassword = document.getElementById('confirmPassword');


const otpBtn = document.getElementById('otpBtn');
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');

const errorBoxUserId = document.getElementById('errorBoxUserId');
const errorBoxName = document.getElementById('errorBoxName');
const errorBoxEmail = document.getElementById('errorBoxEmail');
const errorBoxOtp = document.getElementById('errorBoxOtp');
const errorBoxCollegeName = document.getElementById('errorBoxCollegeName');
const errorBoxPwd = document.getElementById('errorBoxPwd');
const errorBoxCnfmPwd = document.getElementById('errorBoxCnfmPwd');

let interval = null;

loginBtn.addEventListener('click', () => {
    window.location.href = '/login';
});

function startOtpTimer(durationInSeconds = 120) {
    
    let timer = durationInSeconds;
    errorBoxOtp.style.display='inline';
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
    errorBoxEmail.style.display = "none";
    const email = emailInput.value.trim();

    if (email === "") {
        errorBoxEmail.textContent = "Please enter email id";
        errorBoxEmail.style.display = "block";
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
            clearInterval(interval);
            startOtpTimer();
        } else {
            errorBoxEmail.textContent = "Failed to send OTP";
            errorBoxEmail.style.display = "block";
            otpBtn.textContent = "Send OTP";
        }
    } catch (err) {
        errorBoxOtp.textContent = "Network error";
        errorBoxOtp.style.display = "block";
        otpBtn.textContent = "Send OTP";
    }

    otpBtn.disabled = false;
});


//REGISTER
registerBtn.addEventListener('click', async () => {

    // hide previous errors
    errorBoxUserId.style.display = "none";
    errorBoxEmail.style.display = "none";
    errorBoxOtp.style.display = "none";
    errorBoxPwd.style.display = "none";

    const userData = {
        userId: userId.value.trim(),
        name: userName.value.trim(),
        email: emailInput.value.trim(),
        collegeName: collegeName.value.trim(),
        password: password.value.trim(),
        confirmPassword: confirmPassword.value.trim(),
        otp: otp.value.trim()
    };

    // validations
    if (!userData.userId) {
        errorBoxUserId.textContent = "Please enter user id";
        errorBoxUserId.style.display = "block";
        return;
    }
    if (!userData.name) {
        errorBoxUserId.style.display = "none";
        errorBoxName.textContent = "Please enter your name";
        errorBoxName.style.display = "block";
        return;
    } if (!userData.email) {
        errorBoxName.style.display = "none";
        errorBoxEmail.textContent = "Please enter your email id";
        errorBoxEmail.style.display = "block";
        return;
    } if (!userData.otp) {
        errorBoxEmail.style.display = "none";
        errorBoxOtp.textContent = "Please enter OTP";
        errorBoxOtp.style.display = "block";
        return;
    } if (!userData.collegeName) {
        errorBoxOtp.style.display = "none";
        errorBoxCollegeName.textContent = "Please enter college name";
        errorBoxCollegeName.style.display = "block";
        return;
    } if (!userData.password) {
        errorBoxCollegeName.style.display = "none";
        errorBoxPwd.textContent = "Please enter password";
        errorBoxPwd.style.display = "block";
        return;
    } if (!userData.confirmPassword) {
        errorBoxPwd.style.display = "none";
        errorBoxCnfmPwd.textContent = "Please enter password";
        errorBoxCnfmPwd.style.display = "block";
        return;
    }

    if (userData.password !== userData.confirmPassword) {
        errorBoxPwd.textContent = "Passwords do not match!";
        errorBoxPwd.style.display = "block";
        return;
    }

    registerBtn.textContent = "Registering...";
    registerBtn.disabled = true;

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            alert("Registration Successful!");
            window.location.href = "/login";
        } else {
            console.log(data.error)
            switch (data.error) {
                case "USERID_NOT_AVAILABLE":
                    errorBoxUserId.textContent = "Please try different user id";
                    break;
                case "EMAIL_NOT_AVAILABLE":
                    errorBoxEmail.textContent = "Please try different email";
                    break;
                case "NO_OTP_RECORD":
                    errorBoxOtp.textContent = "Please send OTP first";
                    break;
                case "OTP_EXPIRED":
                    errorBoxOtp.textContent = "OTP expired! please send again";
                    break;
                case "INVALID_OTP":
                    errorBoxOtp.textContent = "Wrong OTP";
                    break;
                default:
                    alert("Server error please try again");
                    console.log(data.error);
            }
        }

    } catch (err) {
        alert("Sorry for truble server not response")
    }

    registerBtn.textContent = "Register";
    registerBtn.disabled = false;
});
