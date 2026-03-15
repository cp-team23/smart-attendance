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
        container.innerHTML = `<p class="notfound">No attendance sessions found.</p>`;
        return;
    }

    list.sort((a, b) => {
    return (
        b.attendanceDate.localeCompare(a.attendanceDate) ||
        b.attendanceTime.localeCompare(a.attendanceTime) ||
        b.subjectName.localeCompare(a.subjectName)
        );
    });

    list.forEach((item, i) => {
        const card = document.createElement("div");
        card.classList.add("attendance-card");
        card.style.animationDelay = `${i * 0.04}s`;

        const total   = item.totalStudentCount   || 0;
        const present = item.presentStudentCount || 0;
        const pct     = total > 0 ? Math.round((present / total) * 100) : 0;
        const isLow   = pct < 70;

        // "10:58:00" → "10:58 AM"
        const [hh, mm] = (item.attendanceTime || "00:00").split(":");
        const h24 = parseInt(hh);
        const ampm = h24 >= 12 ? "PM" : "AM";
        const h12  = h24 % 12 || 12;
        const timeFormatted = `${h12}:${mm} ${ampm}`;

        card.innerHTML = `
            <div class="card-top">
                <div>
                    <div class="card-subject">${item.subjectName}</div>
                    <div class="card-teacher">
                        <svg width="11" height="11" viewBox="0 0 12 12" fill="none">
                            <circle cx="6" cy="4" r="2.3" stroke="currentColor" stroke-width="1.1"/>
                            <path d="M1.5 11c0-2.2 2-3.8 4.5-3.8s4.5 1.6 4.5 3.8" stroke="currentColor" stroke-width="1.1" stroke-linecap="round"/>
                        </svg>
                        ${item.teacherName}
                    </div>
                </div>
                <span class="status ${item.running ? 'running' : 'stopped'}">
                    ${item.running ? 'Running' : 'Closed'}
                </span>
            </div>

            <div class="card-meta">
                <span class="meta-chip">
                    <svg width="11" height="11" viewBox="0 0 12 12" fill="none">
                        <rect x=".5" y="1.5" width="11" height="10" rx="2" stroke="currentColor" stroke-width="1.1"/>
                        <path d="M3.5.5v2M8.5.5v2M.5 4.5h11" stroke="currentColor" stroke-width="1.1" stroke-linecap="round"/>
                    </svg>
                    ${item.attendanceDate}
                </span>
                <span class="meta-chip">
                    <svg width="11" height="11" viewBox="0 0 12 12" fill="none">
                        <circle cx="6" cy="6" r="5" stroke="currentColor" stroke-width="1.1"/>
                        <path d="M6 3v3l2 1.5" stroke="currentColor" stroke-width="1.1" stroke-linecap="round"/>
                    </svg>
                    ${timeFormatted}
                </span>
            </div>

            <div class="card-progress">
                <div class="progress-header">
                    <span class="progress-label">Attendance</span>
                    <span class="progress-value">${present} / ${total} &nbsp;·&nbsp; ${pct}%</span>
                </div>
                <div class="progress-track">
                    <div class="progress-fill ${isLow ? 'low' : ''}" style="width:${pct}%"></div>
                </div>
            </div>

            <div class="card-actions">
                <button class="view-btn"   onclick="viewAttendance('${item.attendanceId}')">View</button>
                <button class="delete-btn" onclick="deleteAttendance('${item.attendanceId}')">Delete</button>
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