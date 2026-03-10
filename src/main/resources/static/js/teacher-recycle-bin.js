const cardsContainer = document.getElementById('cardsContainer');
const searchInput = document.getElementById("searchInput");




async function loadAttendance() {

    try {

        const res = await fetch("/api/teacher/all-deleted-attendance");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        allData = data.response;

        renderAttendance(allData);

    } catch (err) {
        console.error(err);
        showSnackbar("Failed to load attendance", "error");
    }
}

function renderAttendance(list) {

    cardsContainer.innerHTML = "";

    if (list.length === 0) {
        cardsContainer.innerHTML = "<p>No attendance found</p>";
        return;
    }
    html = "";
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
                </div>
        `;
    });
    cardsContainer.innerHTML = html;
}


async function recycleAttendance(attendanceId) {

    try {
        const response = await fetch(
            `/api/teacher/attendance/restore/${attendanceId}`,
            {
                method: "PATCH",
                credentials: "include"
            }
        );

        const data = await response.json();

        if (!response.ok) {
            showSnackbar("Failed to restore attendance", "warning");
            return;
        }

        showSnackbar("attendance restored successfully", "success");

        // reload list after restore
        loadAttendance();

    } catch (error) {
        console.error(error);
        showSnackbar("Something went wrong", "error");
    }
}
loadAttendance();




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
