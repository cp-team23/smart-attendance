const userId = document.getElementById("userId");
const nameInput = document.getElementById("name");
const enrollment = document.getElementById("enrollment");
const email = document.getElementById("email");
const password = document.getElementById("password");


const addStudentBtn = document.getElementById("addStudentBtn");




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

let allData = [];
let year = [];
let branch = [];
let sem = [];
let className = [];
let batch = [];

let academicId = null;

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

            allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && batchInput.value === element.batch) {
                    academicId = element.academicId;
                }
            });

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

            allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && item === element.className && !batch.includes(element.batch)) {
                    batch.push(element.batch);
                }
            });

            setBatch();
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

            allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && item === element.semester && !sem.includes(element.className)) {
                    className.push(element.className);
                }
            });

            setClass();
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
            branchInput.value = item;
            branchInput.classList.add("filled");
            branchOptionsBox.style.display = "none";

            allData.forEach(element => {
                if (yearInput.value == element.year && item === element.branch && !sem.includes(element.semester)) {
                    sem.push(element.semester);
                }
            });

            setSem();
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
            yearInput.value = item;
            yearInput.classList.add("filled");
            yearOptionsBox.style.display = "none";

            allData.forEach(element => {
                if (item == element.year && !branch.includes(element.branch)) {
                    branch.push(element.branch);
                }
            });
            setBranch();
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
    branchOptionsBox.style.display =
        branchOptionsBox.style.display === "block" ? "none" : "block";
};


semInput.onclick = () => {
    className = [];
    semOptionsBox.style.display =
        semOptionsBox.style.display === "block" ? "none" : "block";
};

classInput.onclick = () => {
    batch = [];
    classOptionsBox.style.display =
        classOptionsBox.style.display === "block" ? "none" : "block";
};

batchInput.onclick = () => {
    batchOptionsBox.style.display =
        batchOptionsBox.style.display === "block" ? "none" : "block";
};

addStudentBtn.onclick = async () => {
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

    addStudentBtn.textContent = "Adding...";
    addStudentBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/student', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSnackbar("Student added successfull", "success");
        } else {
            console.log(data.error)
            switch (data.error) {
                case "USERID_NOT_AVAILABLE":
                    showSnackbar("Please try different student id", "warning");
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

    addStudentBtn.textContent = "Add Student";
    addStudentBtn.disabled = false;

};



