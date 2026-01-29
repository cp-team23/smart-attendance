const boxTitle = document.getElementById('box-title');
const userId = document.getElementById('userId');
const teacherBtn = document.getElementById('teacherBtn');
const adminBtn = document.getElementById('adminBtn');
const forgotLink = document.getElementById('forgotLink');
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const errorBoxId = document.getElementById('errorBoxId');
const errorBoxPwd = document.getElementById('errorBoxPwd');

let role = 'teacher';

function setRole(role) {

    [teacherBtn, adminBtn].forEach(btn => btn.classList.remove('active'));

    if (role === 'teacher') {
        teacherBtn.classList.add('active');
        boxTitle.textContent = 'Teacher Login';
        forgotLink.style.display = 'none';
        registerBtn.style.display = 'none';
        errorBoxId.style.display = 'none'
        errorBoxId.textContent = "";
        errorBoxPwd.style.display = 'none'
        errorBoxPwd.textContent = "";
    } else if (role === 'admin') {
        adminBtn.classList.add('active');
        boxTitle.textContent = 'Admin Login';
        forgotLink.style.display = 'inline-block';
        registerBtn.style.display = 'inline-block';
        errorBoxId.style.display = 'none'
        errorBoxId.textContent = "";
        errorBoxPwd.style.display = 'none'
        errorBoxPwd.textContent = "";
    }
}



registerBtn.addEventListener('click', () => {
    window.location.href = "/register";
});

teacherBtn.addEventListener('click', () => setRole(role = 'teacher'));
adminBtn.addEventListener('click', () => setRole(role = 'admin'));

loginBtn.addEventListener('click', () => login());


async function login() {
    errorBoxId.style.display = 'none';
    errorBoxId.textContent = "";
    errorBoxPwd.style.display = 'none';
    errorBoxPwd.textContent = "";

    const userIdValue = userId.value.trim();
    const passwordValue = document.getElementById('password').value.trim();

    if (!userIdValue) {
        errorBoxId.style.display = 'block';
        errorBoxId.textContent = "Please enter your user Id";
        return;
    }
    if (!passwordValue) {
        errorBoxPwd.style.display = 'block';
        errorBoxPwd.textContent = "Please enter your password";
        return;
    }

    // Request Body
    const loginData = {
        userId: userIdValue,
        password: passwordValue,
        role:(role || "").toUpperCase(),
    };

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',     // IMPORTANT: allows browser to store JWT cookie
            body: JSON.stringify(loginData)
        });

        const data = await response.json();

        if (response.ok) {

            localStorage.setItem("jwtToken", data.token);
            localStorage.setItem("role", data.role);
            localStorage.setItem("username", data.username);
            
            showSnackbar("Login successful", "success");

            setTimeout(() => {
                if (data.role === 'ADMIN') {
                    window.location.href = "/admin/dashboard";
                }else if(data.role === 'TEACHER'){
                    window.location.href = "/teacher/dashboard";
                }
            }, 1000);
            
        } else {
            if (data.error === "USER_NOT_FOUND") {
                errorBoxId.style.display = 'block';
                errorBoxId.textContent = "User Id not found";
            }
            else if (data.error === "WRONG_PASSWORD") {
                errorBoxPwd.style.display = 'block';
                errorBoxPwd.textContent = "Wrong password";
            }
            else if (data.error === "TEMPORARY_BLOCKED") {
                showSnackbar("Account temporarily blocked try after 2 min", "warning");
            }
        }

    } catch (error) {
        showSnackbar("Something went wrong. Try again", "error"); 
    }
}





