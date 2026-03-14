const cardsContainer = document.getElementById('cardsContainer');
const searchInput = document.getElementById("searchInput");

let allData = [];

/* ================= LOAD ATTENDANCE ================= */

async function loadAttendance() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/teacher/all-deleted-attendance");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        allData = data.response;


        renderAttendance(allData);
        removeLoader(); // 👈

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Failed to load attendance", "error");
    }
}

/* ================= RENDER ATTENDANCE ================= */

function renderAttendance(list) {
    cardsContainer.innerHTML = "";

    if (list.length === 0) {
        cardsContainer.innerHTML = `<p class="notfound" >No attendance found</p>`;
        return;
    }

     list.sort((a, b) => {
    return (
        b.attendanceDate.localeCompare(a.attendanceDate) ||
        b.attendanceTime.localeCompare(a.attendanceTime) ||
        b.subjectName.localeCompare(a.subjectName)
        );
    });

    let html = "";
    list.forEach(item => {
        html += `<div class="attendance-card">
            <div class="card-header">
                <h3>${item.subjectName}</h3>
                <span class="status ${item.running ? "running" : "stopped"}">
                    ${item.running ? "Running" : "Closed"}
                </span>
            </div>
            <div class="card-body">
                <p><strong>Date:</strong> ${item.attendanceDate}</p>
                <p><strong>Time:</strong> ${item.attendanceTime}</p>
                <p><strong>Teacher:</strong> ${item.teacherName}</p>
            </div>
            <div class="card-actions">
                <button class="view-btn" onclick="recycleAttendance('${item.attendanceId}')">
                    Restore
                </button>
            </div>
        </div>`;
    });
    cardsContainer.innerHTML = html;
}

/* ================= RESTORE ATTENDANCE ================= */

async function recycleAttendance(attendanceId) {
    const ok = await showConfirm({
        title: "Restore Attendance ?",
        message: "Are you sure you want to restore attendance?",
        confirmText: "Restore"
    });
    if (!ok) return;

    showLoader(); // 👈
    try {
        const response = await fetch(`/api/teacher/attendance/restore/${attendanceId}`, {
            method: "PATCH",
            credentials: "include"
        });

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to restore attendance", "warning");
            return;
        }

        showSuccess(); // 👈
        showSnackbar("Attendance restored successfully", "success");
        loadAttendance(); // triggers its own loader cycle

    } catch (error) {
        console.error(error);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
}

loadAttendance();

/* ================= SEARCH ================= */

searchInput.addEventListener("input", function () {
    const searchValue = this.value.toLowerCase().trim();

    if (!searchValue) {
        renderAttendance(allData);
        return;
    }

    const filtered = allData.filter(item =>
        item.subjectName.toLowerCase().includes(searchValue) ||
        item.teacherName.toLowerCase().includes(searchValue) ||
        item.attendanceDate.includes(searchValue) ||
        item.attendanceTime.includes(searchValue)
    );

    renderAttendance(filtered);
});