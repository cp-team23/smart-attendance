const app = document.getElementById("app");
let academicId = app.dataset.academicId || null;

const searchInput = document.getElementById("searchInput");

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

const contentbody = document.querySelector(".contentbody");

let allData = [];
let year = [];
let branch = [];
let sem = [];
let className = [];
let batch = [];
let allStudents = [];

function showData(student) {
    let html = "";
    student.sort((a, b) => a.enrollmentNo.localeCompare(b.enrollmentNo));
    student.forEach(element => {
        html += `<div class="student-row" data-en="${element.enrollmentNo}" data-id="${element.userId}">
            <div class="student-profile">
                <img src="${element.curImage}" alt="Student">
                <div>
                    <div class="student-name">${element.name}</div>
                    <div class="det">
                        <div class="student-email">${element.userId}</div>
                        <div class="student-field">Enrollment: ${element.enrollmentNo}</div>
                        <div class="student-field">Email : ${element.email}</div>
                    </div>
                </div>
            </div>
            <div class="student-actions">
                <button class="update-btn">Edit</button>
                <button class="delete-btn">Delete</button>
            </div>
        </div>`;
    });
    contentbody.innerHTML = html;
}

async function loadData() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/academic-structure");
        const data = await res.json();

        allData = data.response;

        allData.forEach(element => {
            if (!year.includes(element.year)) {
                year.push(element.year);
            }
        });
        removeLoader(); // 👈
    } catch (err) {
        removeLoader(); // 👈
        showSnackbar("Failed to load academic structure", "error");
        throw err;
    }
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
                if (
                    yearInput.value === element.year &&
                    branchInput.value === element.branch &&
                    semInput.value === element.semester &&
                    classInput.value === element.className &&
                    batchInput.value === element.batch
                ) {
                    academicId = element.academicId;
                    loadStudent(academicId);
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
            if (item !== classInput.value) {
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
            if (item !== semInput.value) {
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
        if (
            yearInput.value === element.year &&
            branchInput.value === element.branch &&
            semInput.value === element.semester &&
            !className.includes(element.className)
        ) {
            className.push(element.className);
        }
    });
    setClass();
    classOptionsBox.style.display =
        classOptionsBox.style.display === "block" ? "none" : "block";
};

batchInput.onclick = () => {
    allData.forEach(element => {
        if (
            yearInput.value === element.year &&
            branchInput.value === element.branch &&
            semInput.value === element.semester &&
            classInput.value === element.className &&
            !batch.includes(element.batch)
        ) {
            batch.push(element.batch);
        }
    });
    setBatch();
    batchOptionsBox.style.display =
        batchOptionsBox.style.display === "block" ? "none" : "block";
};

async function loadStudent(academicId) {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/all-student/" + academicId);
        const data = await res.json();

        if (res.ok) {
            if (data.response.length === 0) {
                contentbody.innerHTML = `<p class=notfound">No student found</p>`;
                removeLoader(); // 👈
                showSnackbar("No students found.", "warning");
            } else {
                yearInput.value = data.response[0].year;
                yearInput.classList.add("filled");
                branchInput.value = data.response[0].branch;
                branchInput.classList.add("filled");
                semInput.value = data.response[0].semester;
                semInput.classList.add("filled");
                classInput.value = data.response[0].className;
                classInput.classList.add("filled");
                batchInput.value = data.response[0].batch;
                batchInput.classList.add("filled");

                allStudents = data.response;
                showData(allStudents);
                removeLoader(); // 👈
            }
        } else {
            removeLoader(); // 👈
            showSnackbar("Something went wrong. Try again", "warning");
        }
    } catch (e) {
        removeLoader(); // 👈
        showSnackbar("Something went wrong. Try again", "error");
    }
}

contentbody.addEventListener("click", function (e) {
    const card = e.target.closest(".student-row");
    if (!card) return;

    const enrollmentNo = card.dataset.en;
    const studentId = card.dataset.id;

    if (e.target.classList.contains("update-btn")) {
        window.location.href = "/admin/user/update/student/" + enrollmentNo;
    }

    if (e.target.classList.contains("delete-btn")) {
        deleteStudent(studentId).then(() => loadStudent(academicId));
    }
});

loadData().then(() => {
    setYear();
    if (academicId !== null) {
        loadStudent(academicId);
    } else if (allData.length > 0) {
        for (let element of allData) {
            if (element.studentCount > 0) {
                academicId = element.academicId;
                break;
            }
        }
        loadStudent(academicId);
    }
});

searchInput.addEventListener("input", function () {
    const searchValue = this.value.toLowerCase().trim();

    if (!searchValue) {
        showData(allStudents);
        return;
    }

    const filtered = allStudents.filter(student =>
        student.name.toLowerCase().includes(searchValue) ||
        student.userId.toLowerCase().includes(searchValue) ||
        student.enrollmentNo.toLowerCase().includes(searchValue) ||
        student.email.toLowerCase().includes(searchValue)
    );

    showData(filtered);
});