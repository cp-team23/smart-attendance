const searchInput  = document.getElementById("searchBar");
const searchBtn = document.getElementById("searchBtn");

const studentBtn = document.getElementById("studentBtn");
const teacherBtn = document.getElementById("teacherBtn");

const borderMsg = document.getElementById("borderMsg");

let role = 'student';

function setRole(selectedRole) {
    role = selectedRole;

    [teacherBtn, studentBtn].forEach(btn =>
        btn.classList.remove('active')
    );

    if (role === 'teacher') {
        borderMsg.innerHTML = "Search Teacher";
        teacherBtn.classList.add('active');
    } else {
        borderMsg.innerHTML = "Search Student";
        studentBtn.classList.add('active');
    }
}


teacherBtn.addEventListener('click', () => setRole('teacher'));
studentBtn.addEventListener('click', () => setRole('student'));

function showTeacher(data){
    console.log(data)
}
function showStudent(data){
    console.log(data)
}


async function searchUser() {
    const value = searchInput.value.trim();
    if (!value) return;

    const API_URL =role === 'teacher'? '/api/admin/teacher/': '/api/admin/student/';

    try {
        const res = await fetch(API_URL + value);
        const data = await res.json();
        if (res.ok){
            role==='teacher'?showTeacher(data.response):showStudent(data.response);
        };
        if(data.error==="USER_NOT_FOUND"){
            showSnackbar("User not found", "warning");
        }
        
        

        // append in content body
    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }
}


searchBtn.addEventListener('click', searchUser);

searchInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        searchUser();
    }
});
