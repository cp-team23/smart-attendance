const app = document.getElementById("app");
let key = app.dataset.key || null;
let newRole = app.dataset.role || null;

const teacherBtn = document.getElementById('teacherBtn');
const studentBtn = document.getElementById('studentBtn');
const cardsContainer = document.getElementById('cardsContainer');

const searchInput = document.getElementById("searchInput");

let role = 'student';


let userId = null;
let nameInput = null;
let email = null;
let password = null;
let enrollmentNo = null;

let yearInput = null;
let yearOptionsBox = null;

let branchInput = null;
let branchOptionsBox = null;

let semInput = null;
let semOptionsBox = null;

let classInput = null;
let classOptionsBox = null;

let batchInput = null;
let batchOptionsBox = null;

let addBtn = null;



let allData = [];
let year = [];
let branch = [];
let sem = [];
let className = [];
let batch = [];

let academicId = null;

function teacherContent() {
    cardsContainer.innerHTML = `<form>
                <div class="field">
                    <input type="text" id="userId" required />
                    <label>Enter teacher id</label>
                </div>
                <div class="field">
                    <input type="text" id="name" required />
                    <label>Enter teacher name</label>
                </div>
                <div class="field">
                    <input type="text" id="email" required />
                    <label>Enter teacher email id</label>
                </div>

                <div class="field">
                    <input type="text" id="password" required />
                    <label>Enter password</label>
                </div>
                <button type="button" class="addBtn" id="addBtn">Update Teacher</button>
            </form>`;

    addBtn = document.getElementById("addBtn");

    userId = document.getElementById("userId");
    nameInput = document.getElementById("name");
    email = document.getElementById("email");
    password = document.getElementById("password");

    addBtn.addEventListener("click", updateTeacher);
}
function studentContent() {
    cardsContainer.innerHTML = `<form>
                <div class="field">
                    <input type="text" id="userId"  required/>
                    <label>Enter student id</label>
                </div>
                <div class="field">
                    <input type="text" id="name" required />
                    <label>Enter student name</label>
                </div>
                <div class="field">
                    <input type="text" id="enrollment" required />
                    <label>Enter student enrollment no</label>
                </div>
                <div class="field">
                    <input type="text" id="email" required />
                    <label>Enter student email id</label>
                </div>
                <div class="field">
                    <div class="custom-dropdown">
                        <input type="text" id="yearInput" required readonly />
                        <label for="yearInput">Select year</label>
                        <ul id="yearOption" class="options"></ul>
                    </div>
                </div>
                <div class="field">
                    <div class="custom-dropdown">
                        <input type="text" id="branchInput" required readonly />
                        <label for="branchInput">Select branch</label>
                        <ul id="branchOption" class="options"></ul>
                    </div>
                </div>
                <div class="field">
                    <div class="custom-dropdown">
                        <input type="text" id="semInput" required readonly />
                        <label for="semInput">Select semester</label>
                        <ul id="semOption" class="options"></ul>
                    </div>
                </div>
                <div class="field">
                    <div class="custom-dropdown">
                        <input type="text" id="classInput" required readonly />
                        <label for="classInput">Select class</label>
                        <ul id="classOption" class="options"></ul>
                    </div>
                </div>
                <div class="field">
                    <div class="custom-dropdown">
                        <input type="text" id="batchInput" required readonly />
                        <label for="batchInput">Select batch</label>
                        <ul id="batchOption" class="options"></ul>
                    </div>
                </div>


                <div class="field">
                    <input type="text" id="password" required />
                    <label>Enter password</label>
                </div>
                <button type="button" class="addBtn" id="addBtn">Update Student</button>
            </form>`;

    addBtn = document.getElementById("addBtn");

    userId = document.getElementById("userId");
    nameInput = document.getElementById("name");

    email = document.getElementById("email");
    enrollmentNo = document.getElementById("enrollment");
    password = document.getElementById("password");

    yearInput = document.getElementById("yearInput");
    yearOptionsBox = document.getElementById("yearOption");

    branchInput = document.getElementById("branchInput");
    branchOptionsBox = document.getElementById("branchOption");

    semInput = document.getElementById("semInput");
    semOptionsBox = document.getElementById("semOption");

    classInput = document.getElementById("classInput");
    classOptionsBox = document.getElementById("classOption");

    batchInput = document.getElementById("batchInput");
    batchOptionsBox = document.getElementById("batchOption");

    addBtn.addEventListener("click", updateStudent);
}


function setRole() {

    [teacherBtn, studentBtn].forEach(btn => btn.classList.remove('active'));

    if (role === 'teacher') {
        teacherBtn.classList.add('active');
        searchInput.placeholder = "Enter Teacher Id";
        searchInput.value = "";

        teacherContent();
    } else if (role === 'student') {
        studentBtn.classList.add('active');
        searchInput.placeholder = "Enter Student enrollment number"
        searchInput.value = "";
        studentContent();
    }
}

studentContent();
teacherBtn.addEventListener('click', () => setRole(role = 'teacher'));
studentBtn.addEventListener('click', () => setRole(role = 'student'));




async function loadStudent(key) {
    try{
        const res = await fetch("/api/admin/student/" + key);
        const data = await res.json();
        if(!res.ok){
            showSnackbar("User not found","warning");
            return ;
        }


        const student = data.response;
        userId.value = student.userId;
        userId.readOnly = true;
        nameInput.value = student.name;
        enrollmentNo.value = student.enrollmentNo;
        email.value = student.email;
        yearInput.value = student.year;
        yearInput.classList.add("filled");
        yearOptionsBox.style.display = "none";
        branchInput.value = student.branch;
        branchInput.classList.add("filled");
        branchOptionsBox.style.display = "none";
        semInput.value = student.semester;
        semInput.classList.add("filled");
        semOptionsBox.style.display = "none";
        classInput.value = student.className;
        classInput.classList.add("filled");
        classOptionsBox.style.display = "none";
        batchInput.value = student.batch;
        batchInput.classList.add("filled");
        batchOptionsBox.style.display = "none";

        searchInput.value = student.enrollmentNo;

    }catch{
        showSnackbar("Something went wrong. Try again","error");
    }

}

async function loadTeacher(key) {
    try{
        const res = await fetch("/api/admin/teacher/" + key);
        const data = await res.json();
        if(!res.ok){
            showSnackbar("User not found","warning");
            return ;
        }else{
            
            const teacher = data.response;
            userId.value = teacher.userId;
            userId.readOnly = true;
            nameInput.value = teacher.name;
            email.value = teacher.email;

            searchInput.value = teacher.userId;
        }
    }
    catch{
        showSnackbar("Something went wrong. Try again","error");
    }
    
}



if(newRole==='student'){
    loadStudent(key);
}
if(newRole==='teacher'){
    setRole(role = 'teacher');
    loadTeacher(key);
}



async function loadData() {
    const res = await fetch("/api/admin/academic-structure");
    const data = await res.json();

    allData = data.response;

    allData.forEach(element => {
        if (!year.includes(element.year)) {
            year.push(element.year);
        }
    });
}

function setBatch() {
    batchOptionsBox.innerHTML = "";

    batch.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            batchInput.value = item;
            batchInput.classList.add("filled");
            batchOptionsBox.style.display = "none";
        };
        batchOptionsBox.appendChild(li);
    });
}


function setClass() {
    classOptionsBox.innerHTML = "";
    batch = [];
    className.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            if (item !== branchInput.value) {
                batchInput.value = "";
                batchInput.classList.remove("filled");
                batchOptionsBox.style.display = "none";
            }
            classInput.value = item;
            classInput.classList.add("filled");
            classOptionsBox.style.display = "none";
        };
        classOptionsBox.appendChild(li);
    });
}

function setSem() {
    semOptionsBox.innerHTML = "";
    className = [];
    sem.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            if (item !== branchInput.value) {
                classInput.value = "";
                classInput.classList.remove("filled");
                classOptionsBox.style.display = "none";
                batchInput.value = "";
                batchInput.classList.remove("filled");
                batchOptionsBox.style.display = "none";
            }
            semInput.value = item;
            semInput.classList.add("filled");
            semOptionsBox.style.display = "none";
        };
        semOptionsBox.appendChild(li);
    });
}

function setBranch() {
    branchOptionsBox.innerHTML = "";
    sem = [];
    branch.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            if (item !== branchInput.value) {
                semInput.value = "";
                semInput.classList.remove("filled");
                semOptionsBox.style.display = "none";
                classInput.value = "";
                classInput.classList.remove("filled");
                classOptionsBox.style.display = "none";
                batchInput.value = "";
                batchInput.classList.remove("filled");
                batchOptionsBox.style.display = "none";
            }
            branchInput.value = item;
            branchInput.classList.add("filled");
            branchOptionsBox.style.display = "none";
        };
        branchOptionsBox.appendChild(li);
    });
}

function setYear() {
    yearOptionsBox.innerHTML = "";
    year.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            if (item !== yearInput.value) {
                branchInput.value = "";
                branchInput.classList.remove("filled");
                branchOptionsBox.style.display = "none";
                semInput.value = "";
                semInput.classList.remove("filled");
                semOptionsBox.style.display = "none";
                classInput.value = "";
                classInput.classList.remove("filled");
                classOptionsBox.style.display = "none";
                batchInput.value = "";
                batchInput.classList.remove("filled");
                batchOptionsBox.style.display = "none";
            }
            yearInput.value = item;
            yearInput.classList.add("filled");
            yearOptionsBox.style.display = "none";

        };
        yearOptionsBox.appendChild(li);
    });

}

loadData().then(() => {
    setYear();
});

yearInput.onclick = () => {
    branch = [];

    branchOptionsBox.style.display = "none";
    semOptionsBox.style.display = "none";
    classOptionsBox.style.display = "none";
    batchOptionsBox.style.display = "none";

    yearOptionsBox.style.display =
        yearOptionsBox.style.display === "block" ? "none" : "block";
};

branchInput.onclick = () => {
    sem = [];
    allData.forEach(element => {
        if (yearInput.value == element.year && !branch.includes(element.branch)) {
            branch.push(element.branch);
        }
    });
    setBranch();
    yearOptionsBox.style.display = "none";
    semOptionsBox.style.display = "none";
    classOptionsBox.style.display = "none";
    batchOptionsBox.style.display = "none";
    branchOptionsBox.style.display =
        branchOptionsBox.style.display === "block" ? "none" : "block";
};


semInput.onclick = () => {
    className = [];
    allData.forEach(element => {
        if (yearInput.value == element.year && branchInput.value === element.branch && !sem.includes(element.semester)) {
            sem.push(element.semester);
        }
    });
    setSem();
    yearOptionsBox.style.display = "none";
    branchOptionsBox.style.display = "none";
    classOptionsBox.style.display = "none";
    batchOptionsBox.style.display = "none";
    semOptionsBox.style.display =
        semOptionsBox.style.display === "block" ? "none" : "block";
};

classInput.onclick = () => {
    batch = [];
    allData.forEach(element => {
        if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && !className.includes(element.className)) {
            className.push(element.className);
        }
    });
    setClass();
    yearOptionsBox.style.display = "none";
    branchOptionsBox.style.display = "none";
    semOptionsBox.style.display = "none";
    batchOptionsBox.style.display = "none";
    classOptionsBox.style.display =
        classOptionsBox.style.display === "block" ? "none" : "block";
};

batchInput.onclick = () => {

    allData.forEach(element => {
        if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && !batch.includes(element.batch)) {
            batch.push(element.batch);
        }
    });
    yearOptionsBox.style.display = "none";
    branchOptionsBox.style.display = "none";
    semOptionsBox.style.display = "none";
    classOptionsBox.style.display = "none";
    setBatch();
    batchOptionsBox.style.display =
        batchOptionsBox.style.display === "block" ? "none" : "block";
};




searchInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        if(role==='student'){
            loadStudent(searchInput.value);
        }else if(role==='teacher'){
            loadTeacher(searchInput.value);
        }
    }
});



async function updateStudent() {
     allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && batchInput.value === element.batch) {
                    academicId = element.academicId;
                }
            });
    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        enrollmentNo: enrollmentNo.value.trim(),
        password: password.value.trim(),
        academicId:academicId
    };

    if (!userData.userId) {
        showSnackbar("Please enter student id", "warning");
        return;
    }
    if (!userData.name) {
        showSnackbar("Please enter student name", "warning");
        return;
    } if (!userData.email) {
        showSnackbar("Please enter student email id", "warning");
        return;
    } if (!userData.enrollmentNo) {
        showSnackbar("Please enter student enrollment No", "warning");
        return;
    } if (!userData.password) {
        showSnackbar("Please enter password", "warning");
        return;
    }
    if (!userData.academicId) {
        showSnackbar("Please select academic structure", "warning");
        return;
    }

    

    addBtn.textContent = "Updating...";
    addBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/student', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSnackbar("Student updated successfull", "success");
        } else {
            console.log(data.error)
            switch (data.error) {
                case "USER_NOT_FOUND":
                    showSnackbar("User not found", "warning");
                    break;
                case "ENROLLMENT_NOT_AVAILABLE":
                    showSnackbar("Enrollment no not available", "warning");
                    break;
                default:
                    showSnackbar("Something went wrong. Try again", "warning");
            }
        }

    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }

    addBtn.textContent = "Update Student";
    addBtn.disabled = false;
};



async function updateTeacher() {
    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        password: password.value.trim()
    };

    if (!userData.userId) {
        showSnackbar("Please enter student id", "warning");
        return;
    }
    if (!userData.name) {
        showSnackbar("Please enter student name", "warning");
        return;
    } if (!userData.email) {
        showSnackbar("Please enter student email id", "warning");
        return;
    }if (!userData.password) {
        showSnackbar("Please enter teacher password", "warning");
        return;
    }

    addBtn.textContent = "Updating...";
    addBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/teacher', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSnackbar("Teacher updated successfull", "success");
        } else {
            showSnackbar("Something went wrong. Try again", "warning");
        }

    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }

    addBtn.textContent = "Update Teacher";
    addBtn.disabled = false;

};
