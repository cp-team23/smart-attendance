const app = document.getElementById("app");
let attendanceId = app.dataset.attendanceId || null;

const role = localStorage.getItem("role").toLowerCase();

const subjectInput = document.getElementById("subject");
const dateInput = document.getElementById("date");
const timeInput = document.getElementById("time");
const teacherName = document.getElementById("teacherName");

const addBtn = document.getElementById("add");
const showQr = document.getElementById("showQr");
const startStopBtn = document.getElementById("startstop");

const academicContainer = document.getElementById("academic");
const academicItemContainer = document.getElementById("academic-item");

const addAcademicBtn = document.getElementById("addAcademic");
const modal = document.querySelector(".add-academic");
const studentContainer = document.querySelector(".student");

const qrCanvas = document.getElementById("qrCanvas");
const qrOverlay = document.getElementById("qrOverlay");
const qrSpeedInput = document.getElementById("qrSpeed");
const closeQr = document.getElementById("closeQr");



function showLoader() {
    const root = document.getElementById("loader-root");
    root.innerHTML = `
        <div class="loader-overlay">
            <div class="loader-circle">
                <svg class="loader-ring" viewBox="0 0 50 50">
                    <circle class="ring-track" cx="25" cy="25" r="20" />
                    <circle class="ring-spin" cx="25" cy="25" r="20" />
                </svg>
            </div>
        </div>`;
}

function removeLoader() {
    const root = document.getElementById("loader-root");
    const overlay = root.querySelector(".loader-overlay");
    if (overlay) {
        overlay.classList.add("fade-out");
        setTimeout(() => { root.innerHTML = ""; }, 250);
    } else {
        root.innerHTML = "";
    }
}

function showSuccess() {
    const root = document.getElementById("loader-root");
    root.innerHTML = `
        <div class="loader-overlay">
            <div class="success-circle">
                <svg viewBox="0 0 24 24" class="check">
                    <path d="M20 6L9 17l-5-5" />
                </svg>
            </div>
        </div>`;
    setTimeout(() => {
        const overlay = root.querySelector(".loader-overlay");
        if (overlay) overlay.classList.add("fade-out");
        setTimeout(() => { root.innerHTML = ""; }, 250);
    }, 1000);
}


let selectedAcademicIds = new Set();
let running = false;
let qrInterval = null;
let allData = null;

/* =============================
   KEYBOARD ATTENDANCE FEATURE
============================= */

let currentStudentIndex = 0;
let studentRows = [];
let presentList = [];
let absentList = [];

function highlightStudentRow() {
    studentRows.forEach(row => row.classList.remove("active"));
    const row = studentRows[currentStudentIndex];
    if (row) {
        row.classList.add("active");
        row.scrollIntoView({ block: "nearest" });
    }
}

function toggleStudentAttendance() {
    const row = studentRows[currentStudentIndex];
    if (!row) return;

    const statusDiv = row.querySelector(".student-status");
    const userId = row.dataset.userid;

    if (statusDiv.classList.contains("absent")) {
        statusDiv.classList.remove("absent");
        statusDiv.classList.add("present");
        statusDiv.textContent = "PRESENT";
        absentList = absentList.filter(id => id !== userId);
        if (!presentList.includes(userId)) presentList.push(userId);
    } else {
        statusDiv.classList.remove("present");
        statusDiv.classList.add("absent");
        statusDiv.textContent = "ABSENT";
        presentList = presentList.filter(id => id !== userId);
        if (!absentList.includes(userId)) absentList.push(userId);
    }
}

document.addEventListener("keydown", (e) => {
    if (!studentRows.length) return;
    if (e.key === "ArrowDown") {
        e.preventDefault();
        if (currentStudentIndex < studentRows.length - 1) {
            currentStudentIndex++;
            highlightStudentRow();
        }
    }
    if (e.key === "ArrowUp") {
        e.preventDefault();
        if (currentStudentIndex > 0) {
            currentStudentIndex--;
            highlightStudentRow();
        }
    }
    if (e.code === "Space") {
        e.preventDefault();
        toggleStudentAttendance();
    }
});



if (role === "admin") {
    showQr.style.display = "none";
    startStopBtn.style.display = "none";
}

/* =============================
   START / STOP ATTENDANCE
============================= */

backBtn.addEventListener("click", async () => {
    if (presentList.length != 0 || absentList.length != 0) {
        const ok = await showConfirm({
            title: "Did you want leave ?",
            message: "Attendance is not saved yet !",
            confirmText: "Don't save"
        });
        if (!ok) return;
    }
    window.history.back();
});

startStopBtn.addEventListener("click", toggleAttendance);

async function toggleAttendance() {
    showLoader(); // 👈
    try {
        const url = running
            ? `/api/${role}/stop-attendance`
            : `/api/${role}/start-attendance`;

        const response = await fetch(url, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(attendanceId)
        });

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to update attendance", "error");
            return;
        }

        const data = await response.json();
        running = !running;
        updateStartStopButton();

        if (data.message === "ATTENDANCE_STARTED") {
            showSnackbar("Attendance Started", "success");
        } else if (data.message === "ATTENDANCE_STOP_SUCCESSFULLY") {
            showSnackbar("Attendance Stopped", "success");
        }

        if (!running && qrInterval) clearInterval(qrInterval);

        showSuccess(); // 👈

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Error updating attendance", "error");
    }
}

function updateStartStopButton() {
    if (running) {
        startStopBtn.textContent = "Stop Attendance";
        startStopBtn.style.background = "#dc2626";
    } else {
        startStopBtn.textContent = "Start Attendance";
        startStopBtn.style.background = "var(--dark)";
    }
}

/* =============================
   GET ATTENDANCE DATA
============================= */

async function getAttendance() {
    showLoader(); // 👈
    try {
        const response = await fetch(`/api/${role}/attendance/${attendanceId}`);

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to fetch attendance data", "error");
            return;
        }

        const data = await response.json();
        allData = data.response;
        loadAttendanceData(data.response);
        removeLoader(); // 👈

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Error loading attendance data", "error");
    }
}

document.getElementById("dowloadEXL").addEventListener("click", () => {
    exportAttendanceExcel(allData);
});

/* =============================
   LOAD ATTENDANCE DATA
============================= */

function loadAttendanceData(data) {
    teacherName.value = data.teacherName;
    subjectInput.value = data.subjectName;
    dateInput.value = data.attendanceDate;
    timeInput.value = data.attendanceTime;

    running = data.running;
    updateStartStopButton();

    academicContainer.innerHTML = "";
    selectedAcademicIds.clear();

    data.academicDatas.forEach(element => {
        selectedAcademicIds.add(element.academicId);

        academicContainer.innerHTML += `
        <div class="classCard">
            <div class="classTitle">${element.className}</div>
            <div class="classInfo">
                <div><b>Branch:</b> ${element.branch}</div>
                <div><b>Batch:</b> ${element.batch}</div>
                <div><b>Semester:</b> ${element.semester}</div>
                <div><b>Year:</b> ${element.year}</div>
                <div><b>Total Students:</b> ${element.studentCount}</div>
            </div>
            <button class="removeBtn" onclick="removeAcademic('${element.academicId}')">Remove</button>
        </div>`;
    });

    studentContainer.innerHTML = "";
    presentList = [];
    absentList = [];

    showStudent(data.student);
    loadAcademicStructure();
}

function showStudent(student) {
    student.sort((a, b) => a.enrollmentNo.localeCompare(b.enrollmentNo));
    
     present = 0;
     absent = 0;
     temp = "";
    student.forEach(s => {
        s.status === "PRESENT"?present++:absent++;
        temp += `
        <div class="student-row" data-userid="${s.userId}">
            <div class="student-main">
                <div class="student-enroll">${s.enrollmentNo}</div>
                <div class="student-name">${s.name}</div>
            </div>
            <div class="student-info">
                <span>${s.year}</span>
                <span>${s.branch}</span>
                <span>${s.semester}</span>
                <span><b>${s.className}</b></span>
                <span><b>${s.batch}</b></span>
            </div>
            <div class="student-status ${s.status === "PRESENT" ? "present" : "absent"}" >
                ${s.status}
            </div>
        </div>`;
    });

    const total = present + absent;

    studentContainer.innerHTML = `
    <div class="attendance-summary">
        <div class="count-box total">
            <span>Total</span>
            <strong>${total}</strong>
        </div>

        <div class="count-box present">
            <span>Present</span>
            <strong>${present}</strong>
        </div>

        <div class="count-box absent">
            <span>Absent</span>
            <strong>${absent}</strong>
        </div>
    </div>
    ` + temp;

    
    studentRows = document.querySelectorAll(".student-row");

    studentRows.forEach((row, index) => {
        row.addEventListener("click", () => {
            currentStudentIndex = index;
            highlightStudentRow();
            toggleStudentAttendance();
        });
    });

    currentStudentIndex = 0;
    highlightStudentRow();
}

/* =============================
   REMOVE ACADEMIC
============================= */

async function removeAcademic(academicId) {
    const ok = await showConfirm({
        title: "Delete Academic",
        message: "Are you sure you want to delete this Academic? This action cannot be undone.",
        confirmText: "Delete"
    });
    if (!ok) return;

    showLoader(); // 👈
    try {
        const response = await fetch(
            `/api/${role}/academic-in-attendance/${attendanceId}/${academicId}`,
            { method: "DELETE" }
        );

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to delete academic", "error");
            return;
        }

        showSuccess(); // 👈
        showSnackbar("Academic removed", "success");
        getAttendance(); // triggers its own loader cycle

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Error deleting academic", "error");
    }
}

/* =============================
   LOAD ACADEMIC STRUCTURE
============================= */

async function loadAcademicStructure() {
    try {
        const res = await fetch(`/api/${role}/academic-structure`);
        const data = await res.json();

        if (!res.ok) {
            showSnackbar("Failed to load academic data", "error");
            return;
        }

        academicItemContainer.innerHTML = "";

        data.response.forEach(item => {
            if (selectedAcademicIds.has(item.academicId)) return;

            academicItemContainer.innerHTML += `
            <div class="academic-item">
                <label>
                    <input type="checkbox" value="${item.academicId}">
                    <span>
                        ${item.year} | ${item.semester} |
                        ${item.branch} | ${item.className} |
                        ${item.batch}
                        (${item.studentCount} Students)
                    </span>
                </label>
            </div>`;
        });

    } catch (error) {
        console.error(error);
        showSnackbar("Failed to load academic data", "error");
    }
}

/* =============================
   ADD ACADEMIC
============================= */

addBtn.addEventListener("click", addAcademicToAttendance);

async function addAcademicToAttendance() {
    const selected = document.querySelectorAll(
        "#academic-item input[type='checkbox']:checked"
    );

    if (selected.length === 0) {
        modal.style.display = "none";
        showSnackbar("Select at least one batch", "error");
        return;
    }

    showLoader(); // 👈
    try {
        for (const cb of selected) {
            const response = await fetch(
                `/api/${role}/academic-in-attendance/${attendanceId}/${cb.value}`,
                { method: "POST" }
            );

            if (!response.ok) {
                removeLoader(); // 👈
                showSnackbar("Failed to add academic", "error");
                return;
            }
        }

        showSuccess(); // 👈
        showSnackbar("Academic added successfully", "success");
        modal.style.display = "none";
        getAttendance(); // triggers its own loader cycle

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Error adding academic", "error");
    }
}

/* =============================
   MODAL CONTROL
============================= */

modal.style.display = "none";

addAcademicBtn.addEventListener("click", () => {
    modal.style.display = "flex";
});

modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.style.display = "none";
});

/* =============================
   QR CODE LOGIC
============================= */

showQr.addEventListener("click", startQrRefresh);

async function refreshQRCode() {
    // No loader here — QR refreshes silently on interval, loader would flash repeatedly
    try {
        const response = await fetch(`/api/${role}/refresh-qrcode`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                attendanceId: attendanceId,
                refreshTime: parseInt(qrSpeedInput.value)
            })
        });

        if (!response.ok) {
            qrOverlay.style.display = "none";
            const data = await response.json();
            if (qrInterval) {
                clearInterval(qrInterval);
                qrInterval = null;
            }
            if (data.error === "ATTENDANCE_IS_CLOSED") {
                showSnackbar("Please Start Attendance", "error");
                return;
            }
            showSnackbar("Failed to refresh QR", "error");
            return;
        }

        const data = await response.json();
        const qrData = data.response;

        const qrPayload = JSON.stringify({
            attendanceId: qrData.attendanceId,
            encryptedCode: qrData.encryptedCode,
            expireTime: qrData.expireTime
        });

        QRCode.toCanvas(qrCanvas, qrPayload, {
            width: 600,
            errorCorrectionLevel: 'L'
        });

    } catch (error) {
        console.error(error);
        showSnackbar("QR refresh error", "error");
    }
}

function startQrRefresh() {
    if (!running) {
        showSnackbar("Please Start Attendance", "error");
        return;
    }

    qrOverlay.style.display = "flex";
    refreshQRCode();

    if (qrInterval) clearInterval(qrInterval);

    const speed = parseInt(qrSpeedInput.value) || 3;
    qrInterval = setInterval(refreshQRCode, speed * 1000);
}

qrSpeedInput.addEventListener("input", () => {
    if (qrInterval) clearInterval(qrInterval);
    const speed = parseInt(qrSpeedInput.value) || 3;
    qrInterval = setInterval(refreshQRCode, speed * 1000);
});

closeQr.addEventListener("click", () => {
    qrOverlay.style.display = "none";
    if (qrInterval) {
        clearInterval(qrInterval);
        qrInterval = null;
    }
    getAttendance();
});

/* =============================
   EXPORT EXCEL
============================= */

function exportAttendanceExcel(attendance) {
    const students = [...attendance.student];

    students.sort((a, b) => (
        a.year.localeCompare(b.year) ||
        a.branch.localeCompare(b.branch) ||
        a.semester.localeCompare(b.semester) ||
        a.className.localeCompare(b.className) ||
        a.batch.localeCompare(b.batch) ||
        a.enrollmentNo.localeCompare(b.enrollmentNo)
    ));

    const rows = [];
    rows.push(["Subject", attendance.subjectName]);
    rows.push(["Date", attendance.attendanceDate]);
    rows.push(["Time", attendance.attendanceTime]);
    rows.push(["Teacher", attendance.teacherName]);
    rows.push([]);
    rows.push(["Enrollment No", "Student Name", "Year", "Branch", "Semester", "Class", "Batch", "P/A"]);

    students.forEach(s => {
        rows.push([
            s.enrollmentNo, s.name, s.year,
            s.branch, s.semester, s.className,
            s.batch, s.status === "PRESENT" ? "P" : "A"
        ]);
    });

    const ws = XLSX.utils.aoa_to_sheet(rows);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Attendance");
    XLSX.writeFile(wb, `attendance_${attendance.attendanceDate}_${attendance.subjectName}.xlsx`);
}

/* =============================
   SAVE ATTENDANCE
============================= */

saveBtn.addEventListener("click", saveAttendance);

async function saveAttendance() {
    const body = {
        attendanceTime: timeInput.value,
        attendanceDate: dateInput.value,
        subjectName: subjectInput.value,
        presentList: presentList,
        absentList: absentList
    };

    showLoader(); // 👈
    try {
        const response = await fetch(`/api/${role}/attendance/save/${attendanceId}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (response.ok) {
            showSuccess(); // 👈
            showSnackbar("Attendance Saved Successfully", "success");
            getAttendance(); // triggers its own loader cycle
        } else {
            removeLoader(); // 👈
            showSnackbar("Something went wrong! Try again", "error");
        }

    } catch (e) {
        removeLoader(); // 👈
        showSnackbar("Something went wrong!", "error");
    }
}

/* =============================
   SEARCH
============================= */

searchInput.addEventListener("input", function () {
    const searchValue = this.value.toLowerCase().trim();

    if (!searchValue) {
        showStudent(allData.student);
        return;
    }

    const filtered = allData.student.filter(student =>
        student.name.toLowerCase().includes(searchValue) ||
        student.enrollmentNo.toLowerCase().includes(searchValue)
    );

    showStudent(filtered);
});

/* =============================
   CONFIRM DIALOG
============================= */

function showConfirm({
    icon = "!",
    title = "Confirm Action",
    message = "Are you sure you want to continue?",
    confirmText = "Confirm",
    cancelText = "Cancel"
} = {}) {
    return new Promise((resolve) => {
        const root = document.getElementById("confirm-root");

        root.innerHTML = `
        <div class="confirm-overlay">
            <div class="confirm-box">
                <div class="confirm-icon">${icon}</div>
                <h3>${title}</h3>
                <p>${message}</p>
                <div class="confirm-buttons">
                    <button class="confirm-cancel">${cancelText}</button>
                    <button class="confirm-ok">${confirmText}</button>
                </div>
            </div>
        </div>`;

        root.querySelector(".confirm-cancel").onclick = () => {
            root.innerHTML = "";
            resolve(false);
        };

        root.querySelector(".confirm-ok").onclick = () => {
            root.innerHTML = "";
            resolve(true);
        };
    });
}

/* =============================
   INITIAL LOAD
============================= */

getAttendance();


