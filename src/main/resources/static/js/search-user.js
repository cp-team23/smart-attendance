const searchInput  = document.getElementById("searchBar");
const searchBtn = document.getElementById("searchBtn");

const studentBtn = document.getElementById("studentBtn");
const teacherBtn = document.getElementById("teacherBtn");

const borderMsg = document.getElementById("borderMsg");

const contentbody = document.querySelector(".contentbody");

document.getElementById("gotoDashboard").addEventListener("click",()=>window.location.href = "/admin/dashboard");



const app = document.getElementById("app");
const role = app.dataset.role;

function setRole(selectedRole) {
    [teacherBtn, studentBtn].forEach(btn =>
        btn.classList.remove('active')
    );

    if (selectedRole === 'teacher') {
        borderMsg.innerHTML = "Search Teacher";
        teacherBtn.classList.add('active');
    } else {
        borderMsg.innerHTML = "Search Student";
        studentBtn.classList.add('active');
    }
}

console.log(role);
setRole(role);

teacherBtn.addEventListener('click', () => setRole('teacher'));
studentBtn.addEventListener('click', () => setRole('student'));

function showTeacher(data){
    
    contentbody.innerHTML=` <div class="teacher-card" data-id="${data.userId}">
                <div class="teacher-card-data">
                    <div class="teacherid teacher-data">Taecher Id : ${data.userId}</div>
                    <div class="name teacher-data">Name : ${data.name}</div>
                    <div class="email teacher-data">Email : ${data.email}</div>
                </div>
                <div class="teacher-card-btn">
                    <button class="darkBtn delete-btn">Delete</button>
                    <button class="darkBtn update-btn">Update</button>
                </div>
            </div>`;

}
function showStudent(data){
    contentbody.innerHTML=`<div class="student-card student-card-1" data-en="${data.enrollmentNo}">
                <div class="student-card-img-1">
                    <img class="img" src="/assets/images/defaultimage.jpg">
                </div>
                <div class="student-card-info student-card-info-1">
                    <div class="student-card-data">
                        <div class="student-card-data-header">
                            <div class="userid">${data.userId}</div>
                            <div class="year">${data.year}</div>
                        </div>
                        <div class="en-no data">${data.enrollmentNo}</div>
                        <div class="email data">${data.email}</div>
                        <div class="branch-sem">
                            <div class="branch data">${data.branch}</div>
                            <div class="sem data">${data.semester}</div>
                        </div>
                        <div class="class-batch">
                            <div class="class-name data">${data.className}</div>
                            <div class="batch data">${data.batch}</div>
                        </div>
                    </div>
                    <div class="student-card-btn">
                        <button class="darkBtn delete-btn">Delete</button>
                        <button class="darkBtn update-btn">Update</button>
                    </div>
                </div>
           </div>
        </div>`;
}


async function searchUser() {
    const value = searchInput.value.trim();
    if (!value) return;
    contentbody.innerHTML="";
    const API_URL = role === 'teacher'? '/api/admin/teacher/': '/api/admin/student/';

    try {
        const res = await fetch(API_URL + value);
        const data = await res.json();
        if (res.ok){

            if(role==='teacher'){
                showTeacher(data.response)
            }else{
                showStudent(data.response);
            }
        };
        if(data.error==="USER_NOT_FOUND"){
            showSnackbar("User not found", "warning");
        }
        
        

        // append in content body
    } catch (err) {
        console.log(err);
        showSnackbar("Something went wrong. Try again", "error");
    }
}

searchUser();
searchBtn.addEventListener('click', searchUser);

searchInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        searchUser();
    }
});



async function updateTeacher(cardId) {
    window.location.href="/admin/teacher/"+cardId;
}

async function deleteStudent(cardId) {
    await fetch(`/api/admin/teacher/${cardId}`, {
        method: "DELETE"
    });
    loadStudent(academicId);
}


contentbody.addEventListener("click", function (e) {

    if (e.target.classList.contains("update-btn")) {
        e.stopPropagation();
        const card = e.target.closest(".teacher-card");
        const cardId = card.dataset.id;
        updateTeacher(cardId);
    }

    if (e.target.classList.contains("delete-btn")) {
        e.stopPropagation();

        const card = e.target.closest(".teacher-card");
        const cardId = card.dataset.id;
        deleteTeacher(cardId);
       
    }

});


async function updateStudent(cardId) {
    window.location.href="/admin/student/"+cardId;
}

async function deleteStudent(cardId) {
    await fetch(`/api/admin/student/${cardId}`, {
        method: "DELETE"
    });
    loadStudent(academicId);
}


contentbody.addEventListener("click", function (e) {

    if (e.target.classList.contains("update-btn")) {
        e.stopPropagation();
        const card = e.target.closest(".student-card");
        const cardId = card.dataset.en;
        updateStudent(cardId);
    }

    if (e.target.classList.contains("delete-btn")) {
        e.stopPropagation();

        const card = e.target.closest(".student-card");
        const cardId = card.dataset.id;
        deleteStudent(cardId);
       
    }

});
