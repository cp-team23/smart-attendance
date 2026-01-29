const app = document.getElementById("app");
const enrollmentNo = app.dataset.enrollmentNo;

const userId = document.getElementById("userId");
const nameInput = document.getElementById("name");
const enrollment = document.getElementById("enrollment");
const email = document.getElementById("email");
const password = document.getElementById("password");
const updateStudentBtn = document.getElementById("updateStudentBtn");

const yearInput = document.getElementById("yearInput");
const yearOptionsBox = document.getElementById("yearOption");

const branchInput = document.getElementById("branchInput");
const branchOptionsBox = document.getElementById("branchOption");

const semInput = document.getElementById("semInput");
const semOptionsBox = document.getElementById("semOption");

const classInput = document.getElementById("classInput");
const classOptionsBox = document.getElementById("classOption");

const batchInput = document.getElementById("batchInput");
const batchOptionsBox = document.getElementById("batchOption");


document.getElementById("gotoDashboard").addEventListener("click",()=>window.location.href = "/admin/dashboard");


let allData = [];
let year = [];
let branch = [];
let sem = [];
let className = [];
let batch = [];
let orgUserId;
let academicId = null;
async function loadStudent() {
    try{
        const res = await fetch("/api/admin/student/" + enrollmentNo);
        const data = await res.json();
        if(!res.ok){
            showSnackbar("User not found","warning");
            return ;
        }

        const student = data.response;
        userId.value = student.userId;
        orgUserId = student.userId;
        nameInput.value = student.name;
        enrollment.value = student.enrollmentNo;
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
    }catch{
        showSnackbar("Something went wrong. Try again","error");
    }

}
loadStudent();



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
             if(item!==branchInput.value){
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
            if(item!==yearInput.value){
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
    classOptionsBox.style.display =
        classOptionsBox.style.display === "block" ? "none" : "block";
};

batchInput.onclick = () => {
    
    allData.forEach(element => {
        if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && !batch.includes(element.batch)) {
            batch.push(element.batch);
        }
    });
    setBatch();
    batchOptionsBox.style.display =
        batchOptionsBox.style.display === "block" ? "none" : "block";
};

updateStudentBtn.onclick = async () => {
     allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && batchInput.value === element.batch) {
                    academicId = element.academicId;
                }
            });
    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        enrollmentNo: enrollment.value.trim(),
        password: password.value.trim(),
        academicId:academicId
    };

    if (!userData.userId) {
        showSnackbar("Please enter student id", "warning");
        return;
    }
    if(userData.userId!==orgUserId){
        showSnackbar("Don't change student id", "warning");
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

    updateStudentBtn.textContent = "Updating...";
    updateStudentBtn.disabled = true;

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

    updateStudentBtn.textContent = "Update Student";
    updateStudentBtn.disabled = false;
    loadStudent();
};



