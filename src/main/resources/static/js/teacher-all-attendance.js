const container = document.querySelector(".contentbody");
const refreshBtn = document.getElementById("refreshBtn");
const searchInput = document.getElementById("searchInput");

let allAttendance = [];

async function loadAttendance() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/teacher/all-attendance");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        allAttendance = data.response;
        renderAttendance(allAttendance);
        removeLoader(); // 👈

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Failed to load attendance", "error");
    }
}

function renderAttendance(list) {
    container.innerHTML = "";

    if (list.length === 0) {
        container.innerHTML = `<p class="notfound">No attendance found</p>`;;
        return;
    }

    list.forEach(item => {
        const card = document.createElement("div");
        card.classList.add("attendance-card");

        card.innerHTML = `
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
                <button class="view-btn" onclick="viewAttendance('${item.attendanceId}')">
                    View
                </button>
                <button class="delete-btn" onclick="deleteAttendance('${item.attendanceId}')">
                    Delete
                </button>
            </div>
        `;

        container.appendChild(card);
    });
}

loadAttendance();

/* ================= VIEW ================= */

function viewAttendance(id) {
    window.location.href = `/teacher/attendance/${id}`;
}

/* ================= DELETE ================= */

async function deleteAttendance(id) {
    const ok = await showConfirm({
        title: "Move to recycle bin",
        message: "Are you sure you want to move attendance to recycle bin?",
        confirmText: "Delete"
    });
    if (!ok) return;

    showLoader(); // 👈
    try {
        const res = await fetch(`/api/teacher/attendance/${id}`, {
            method: "DELETE"
        });

        if (!res.ok) {
            removeLoader(); // 👈
            showSnackbar("Delete failed", "error");
            return;
        }

        showSuccess(); // 👈
        showSnackbar("Attendance deleted", "success");
        loadAttendance(); // triggers its own loader cycle

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Error deleting attendance", "error");
    }
}

/* ================= REFRESH ================= */

refreshBtn.addEventListener("click", () => {
    loadAttendance();
});

/* ================= SEARCH ================= */

searchInput.addEventListener("input", function () {
    const value = this.value.toLowerCase().trim();

    if (!value) {
        renderAttendance(allAttendance);
        return;
    }

    const filtered = allAttendance.filter(item =>
        item.subjectName.toLowerCase().includes(value) ||
        item.teacherName.toLowerCase().includes(value) ||
        item.attendanceDate.includes(value) ||
        item.attendanceTime.includes(value)
    );

    renderAttendance(filtered);
});