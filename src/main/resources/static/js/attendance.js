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

let selectedAcademicIds = new Set();
let running = false;
let qrInterval = null;

if (role === "admin") {
    showQr.style.display = "none";
    startStopBtn.style.display = "none";
}

/* =============================
   START / STOP ATTENDANCE
============================= */

startStopBtn.addEventListener("click", toggleAttendance);

async function toggleAttendance() {

    try {

        const url = running
            ? `/api/${role}/stop-attendance`
            : `/api/${role}/start-attendance`;

        const response = await fetch(url, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(attendanceId)
        });

        if (!response.ok) {
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

        if (!running && qrInterval) {
            clearInterval(qrInterval);
        }

    } catch (error) {

        console.error(error);
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
    try {

        const response = await fetch(`/api/${role}/attendance/${attendanceId}`);

        if (!response.ok) {
            showSnackbar("Failed to fetch attendance data", "error");
            return;
        }

        const data = await response.json();

        loadAttendanceData(data.response);

    } catch (error) {

        console.error(error);
        showSnackbar("Error loading attendance data", "error");

    }
}

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

            <button class="removeBtn" onclick="removeAcademic('${element.academicId}')">
                Remove
            </button>
        </div>
        `;
    });

    studentContainer.innerHTML = "";

    data.student.forEach(s => {
        studentContainer.innerHTML += `
        <div class="student-row">

            <div class="student-main">
                <div class="student-name">${s.name}</div>
                <div class="student-enroll">${s.enrollmentNo}</div>
            </div>

            <div class="student-info">
                <span>${s.className}</span>
                <span>${s.branch}</span>
                <span>${s.batch}</span>
                <span>${s.semester}</span>
                <span>${s.year}</span>
            </div>

            <div class="student-status ${s.status === "PRESENT" ? "present" : "absent"}">
                ${s.status}
            </div>

        </div>
        `;
    });

    loadAcademicStructure();
}

/* =============================
   DELETE ACADEMIC
============================= */

async function removeAcademic(academicId) {

    try {

        const response = await fetch(
            `/api/${role}/academic-in-attendance/${attendanceId}/${academicId}`,
            { method: "DELETE" }
        );

        if (!response.ok) {
            showSnackbar("Failed to delete academic", "error");
            return;
        }

        showSnackbar("Academic removed", "success");

        getAttendance();

    } catch (error) {

        console.error(error);
        showSnackbar("Error deleting academic", "error");

    }
}

/* =============================
   LOAD ALL ACADEMICS
============================= */

async function loadAcademicStructure() {

    try {

        const res = await fetch(`/api/${role}/academic-structure`);
        const data = await res.json();

        if (!res.ok) {
            showSnackbar("Failed to load academic data", "error");
            return;
        }

        const list = data.response;

        academicItemContainer.innerHTML = "";

        list.forEach(item => {

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
            </div>
            `;
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

    try {

        for (const cb of selected) {

            const academicId = cb.value;

            const response = await fetch(
                `/api/${role}/academic-in-attendance/${attendanceId}/${academicId}`,
                { method: "POST" }
            );

            if (!response.ok) {
                showSnackbar("Failed to add academic", "error");
                return;
            }

        }

        showSnackbar("Academic added successfully", "success");

        modal.style.display = "none";

        getAttendance();

    } catch (error) {

        console.error(error);
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
    if (e.target === modal) {
        modal.style.display = "none";
    }
});

/* =============================
   QR CODE LOGIC
============================= */

showQr.addEventListener("click", startQrRefresh);

async function refreshQRCode() {

    try {

        const response = await fetch(`/api/${role}/refresh-qrcode`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                attendanceId: attendanceId,
                refreshTime: parseInt(qrSpeedInput.value)
            })
        });
        console.log(parseInt(qrSpeedInput.value));

        if (!response.ok) {
            qrOverlay.style.display = "none";
            const data = await response.json();
            if (qrInterval) {
                clearInterval(qrInterval);
                qrInterval = null;
            }
            if(data.error==="ATTENDANCE_IS_CLOSED"){
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
            width: 600 ,
            errorCorrectionLevel:'L'
        });

    } catch (error) {

        console.error(error);
        showSnackbar("QR refresh error", "error");

    }
}

function startQrRefresh() {

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
   INITIAL LOAD
============================= */

getAttendance();