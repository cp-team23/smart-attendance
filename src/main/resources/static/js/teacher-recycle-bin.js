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
        cardsContainer.innerHTML = `<p class="notfound">No attendance found in recycle bin.</p>`;
        return;
    }

    let html = "";
    list.sort((a, b) => {
    return (
        b.attendanceDate.localeCompare(a.attendanceDate) ||
        b.attendanceTime.localeCompare(a.attendanceTime) ||
        b.subjectName.localeCompare(a.subjectName)
        );
    });
    list.forEach((item, i) => {
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

        const delay = `style="animation-delay:${i * 0.04}s"`;

        html += `
        <div class="attendance-card" ${delay}>
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
                    <span class="progress-value">${present} / ${total} · ${pct}%</span>
                </div>
                <div class="progress-track">
                    <div class="progress-fill ${isLow ? 'low' : ''}" style="width:${pct}%"></div>
                </div>
            </div>

            <div class="card-actions">
                <button class="view-btn" onclick="recycleAttendance('${item.attendanceId}')">
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                        <path d="M6 8V2M3.5 4.5 6 2l2.5 2.5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M2 9.5h8" stroke="currentColor" stroke-width="1.3" stroke-linecap="round"/>
                    </svg>
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