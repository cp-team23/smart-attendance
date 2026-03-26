const teacherBtn = document.getElementById('teacherBtn');
const studentBtn = document.getElementById('studentBtn');
const mulstudentBtn = document.getElementById('mulstudentBtn');
const cardsContainer = document.getElementById('cardsContainer');
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

// ===================== CONTENT FUNCTIONS =====================

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
        <button type="button" class="addBtn" id="addBtn">Add Teacher</button>
    </form>`;

    addBtn = document.getElementById("addBtn");
    userId = document.getElementById("userId");
    nameInput = document.getElementById("name");
    email = document.getElementById("email");
    password = document.getElementById("password");

    addBtn.addEventListener("click", addTeacher);
}

function studentContent() {
    cardsContainer.innerHTML = `<form>
        <div class="field">
            <input type="text" id="userId" required />
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
        <button type="button" class="addBtn" id="addBtn">Add Student</button>
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

    addBtn.addEventListener("click", addSingleStudent);

    setYear();

    // ===================== DROPDOWN ONCLICK =====================

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
        branch = [];
        allData.forEach(element => {
            if (yearInput.value == element.year && !branch.includes(element.branch)) {
                branch.push(element.branch);
            }
        });
        yearOptionsBox.style.display = "none";
        semOptionsBox.style.display = "none";
        classOptionsBox.style.display = "none";
        batchOptionsBox.style.display = "none";
        setBranch();
        branchOptionsBox.style.display =
            branchOptionsBox.style.display === "block" ? "none" : "block";
    };

    semInput.onclick = () => {
        sem = [];
        className = [];
        allData.forEach(element => {
            if (yearInput.value == element.year && branchInput.value === element.branch && !sem.includes(element.semester)) {
                sem.push(element.semester);
            }
        });
        yearOptionsBox.style.display = "none";
        branchOptionsBox.style.display = "none";
        classOptionsBox.style.display = "none";
        batchOptionsBox.style.display = "none";
        setSem();
        semOptionsBox.style.display =
            semOptionsBox.style.display === "block" ? "none" : "block";
    };

    classInput.onclick = () => {
        className = [];
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
        batch = [];
        allData.forEach(element => {
            if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && !batch.includes(element.batch)) {
                batch.push(element.batch);
            }
        });
        setBatch();
        yearOptionsBox.style.display = "none";
        branchOptionsBox.style.display = "none";
        semOptionsBox.style.display = "none";
        classOptionsBox.style.display = "none";
        batchOptionsBox.style.display =
            batchOptionsBox.style.display === "block" ? "none" : "block";
    };
}

function multipleStudentContent() {
    cardsContainer.innerHTML = `
        <div class="bulk-layout">
            <div class="bulk-upload-panel">
                <div class="bulk-header">
                    <h3>Multiple Student Upload</h3>
                    <p>Upload Excel file and monitor live processing</p>
                </div>
                <label class="modern-upload">
                    <input type="file" id="excelFile" accept=".xlsx,.xls" hidden />
                    <div class="upload-box">
                        <div class="upload-icon">📂</div>
                        <div>
                            <p id="fileText">Choose Excel File</p>
                            <span>Supported: .xlsx / .xls</span>
                        </div>
                    </div>
                </label>
                <button id="uploadExcelBtn" class="gradient-btn">Start Upload</button>
                <div class="download-area" id="downloadArea" style="display:none;">
                    <button id="downloadReportBtn" class="secondary-btn">Download Output Report</button>
                </div>
            </div>
            <div class="bulk-result-panel">
                <div class="result-header">
                    <h3>Execution Results</h3>
                    <div id="progressText">Waiting...</div>
                </div>
                <div id="resultContainer" class="result-container">
                    <div class="empty-state">Upload file to see results</div>
                </div>
            </div>
        </div>
    `;

    const excelFile = document.getElementById("excelFile");
    const fileText = document.getElementById("fileText");
    const uploadBtn = document.getElementById("uploadExcelBtn");

    excelFile.addEventListener("change", () => {
        fileText.textContent = excelFile.files.length > 0
            ? excelFile.files[0].name
            : "Choose Excel File";
    });

    uploadBtn.addEventListener("click", handleExcelUploadAdvanced);
}

// ===================== ROLE SWITCHING =====================

function setRole() {
    [teacherBtn, studentBtn, mulstudentBtn].forEach(btn => btn.classList.remove('active'));

    if (role === 'teacher') {
        teacherBtn.classList.add('active');
        teacherContent();
    } else if (role === 'student') {
        studentBtn.classList.add('active');
        studentContent();
    } else if (role === 'mulstudent') {
        mulstudentBtn.classList.add('active');
        multipleStudentContent();
    }
}

teacherBtn.addEventListener('click', () => setRole(role = 'teacher'));
studentBtn.addEventListener('click', () => setRole(role = 'student'));
mulstudentBtn.addEventListener('click', () => setRole(role = 'mulstudent'));

// ===================== LOAD DATA =====================

async function loadData() {
    showLoader(); // 👈 show loader while fetching academic structure
    try {
        const res = await fetch("/api/admin/academic-structure");
        const data = await res.json();

        allData = data.response;
        year = [];

        allData.forEach(element => {
            if (!year.includes(element.year)) {
                year.push(element.year);
            }
        });
    } catch (err) {
        removeLoader();
        showSnackbar("Failed to load academic structure", "error");
        throw err;
    }
    removeLoader(); // 👈 remove after data is ready
}

// Initial load — wait for data then render student content
loadData().then(() => {
    setRole();
});

// ===================== DROPDOWN SETTERS =====================

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

function setBranch() {
    branchOptionsBox.innerHTML = "";
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

function setSem() {
    semOptionsBox.innerHTML = "";
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

function setClass() {
    classOptionsBox.innerHTML = "";
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

// ===================== API CALLS =====================

async function addStudentAPI(userData, isBulk = false) {
    if (!isBulk) {
        if (!userData.userId) return showSnackbar("Please enter student id", "warning");
        if (!userData.name) return showSnackbar("Please enter student name", "warning");
        if (!userData.email) return showSnackbar("Please enter student email id", "warning");
        if (!userData.enrollmentNo) return showSnackbar("Please enter student enrollment No", "warning");
        if (!userData.password) return showSnackbar("Please enter password", "warning");
        if (!userData.academicId) return showSnackbar("Please select academic structure", "warning");
    }

    try {
        const response = await fetch('/api/admin/student', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            if (!isBulk) showSnackbar("Student added successfully", "success");
            return { success: true };
        } else {
            if (data.error == "USERID_NOT_AVAILABLE") {
                if (!isBulk) showSnackbar("User id not available", "error");
                return { success: false, error: "Student Id not available" };
            }
            if (data.error == "ENROLLMENT_NOT_AVAILABLE") {
                if (!isBulk) showSnackbar("Enrollment number not available", "error")
                return { success: false, error: "Enrollment number not available" };
            }
            return { success: false, error: data.error || "Unknown error" };
        }
    } catch (err) {
        return { success: false, error: "Server error" };
    }
}

async function addSingleStudent() {
    let selectedAcademic = allData.find(element =>
        yearInput.value === element.year &&
        branchInput.value === element.branch &&
        semInput.value === element.semester &&
        classInput.value === element.className &&
        batchInput.value === element.batch
    );

    if (!selectedAcademic) {
        return showSnackbar("Select complete academic structure", "warning");
    }

    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        enrollmentNo: enrollmentNo.value.trim(),
        password: password.value.trim(),
        academicId: selectedAcademic.academicId
    };

    // Validate before showing loader
    if (!userData.userId) return showSnackbar("Please enter student id", "warning");
    if (!userData.name) return showSnackbar("Please enter student name", "warning");
    if (!userData.email) return showSnackbar("Please enter student email id", "warning");
    if (!userData.enrollmentNo) return showSnackbar("Please enter student enrollment No", "warning");
    if (!userData.password) return showSnackbar("Please enter password", "warning");

    showLoader(); // 👈
    const result = await addStudentAPI(userData, false);
    if (result && result.success) {
        showSuccess(); // 👈
    } else {
        removeLoader(); // 👈 remove on failure (snackbar shown inside addStudentAPI)
    }
}

async function addTeacher() {
    const userData = {
        userId: userId.value.trim(),
        name: nameInput.value.trim(),
        email: email.value.trim(),
        password: password.value.trim(),
    };

    if (!userData.userId) return showSnackbar("Please enter teacher id", "warning");
    if (!userData.name) return showSnackbar("Please enter teacher name", "warning");
    if (!userData.email) return showSnackbar("Please enter teacher email id", "warning");
    if (!userData.password) return showSnackbar("Please enter teacher password", "warning");

    showLoader(); // 👈
    addBtn.disabled = true;

    try {
        const response = await fetch('/api/admin/teacher', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (response.ok) {
            showSuccess(); // 👈
            showSnackbar("Teacher added successfully", "success");
        } else {
            removeLoader(); // 👈
            switch (data.error) {
                case "USERID_NOT_AVAILABLE":
                    showSnackbar("Please try different teacher id", "warning");
                    break;
                default:
                    showSnackbar("Something went wrong. Try again", "warning");
            }
        }
    } catch (err) {
        removeLoader(); // 👈
        showSnackbar("Something went wrong. Try again", "error");
    }

    addBtn.disabled = false;
}

// ===================== BULK UPLOAD =====================

function generateOutputExcel(data) {
    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Upload_Report");

    document.getElementById("downloadReportBtn").onclick = () => {
        XLSX.writeFile(wb, "Bulk_Upload_Report.xlsx");
    };
}

async function handleExcelUploadAdvanced() {
    const excelFile = document.getElementById("excelFile");
    const resultContainer = document.getElementById("resultContainer");
    const progressText = document.getElementById("progressText");
    const downloadArea = document.getElementById("downloadArea");

    const file = excelFile.files[0];
    if (!file) return showSnackbar("Select Excel file", "warning");

    const reader = new FileReader();

    reader.onload = async function (e) {
        const workbook = XLSX.read(new Uint8Array(e.target.result), { type: 'array' });
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet);

        resultContainer.innerHTML = "";
        progressText.textContent = "Processing...";
        downloadArea.style.display = "none";

        let outputData = [];

        for (let i = 0; i < rows.length; i++) {
            const row = rows[i];
            progressText.textContent = `Processing ${i + 1} / ${rows.length}`;

            let academicMatch = allData.find(element =>
                row.year == element.year &&
                row.branch == element.branch &&
                row.semester == element.semester &&
                row.className == element.className &&
                row.batch == element.batch
            );

            const userData = {
                userId: row.userId?.toString().trim(),
                name: row.name?.toString().trim(),
                email: row.email?.toString().trim(),
                enrollmentNo: row.enrollmentNo?.toString().trim(),
                password: row.password?.toString().trim(),
                academicId: academicMatch ? academicMatch.academicId : null
            };

            let result;
            if (!academicMatch) {
                result = { success: false, error: "Academic structure not found" };
            } else {
                result = await addStudentAPI(userData, true);
            }

            const card = document.createElement("div");
            card.className = "result-card " + (result.success ? "success-card" : "fail-card");
            card.innerHTML = `
                <strong>${userData.userId || "No ID"}</strong>
                <div>Status: ${result.success ? "Success" : "Failed"}</div>
                ${!result.success ? `<div>Error: ${result.error}</div>` : ""}
            `;
            resultContainer.appendChild(card);

            outputData.push({
                ...row,
                status: result.success ? "Success" : "Failed",
                error: result.success ? "" : result.error
            });
        }

        progressText.textContent = "Completed";
        generateOutputExcel(outputData);
        downloadArea.style.display = "block";
    };

    reader.readAsArrayBuffer(file);
}