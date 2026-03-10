const dateInput = document.getElementById("attendanceDate");
const timeInput = document.getElementById("attendanceTime");
const academicDiv = document.querySelector(".academic");

function setCurrentDateTime() {
    const now = new Date();

    // Format date → YYYY-MM-DD
    const today = now.toISOString().split("T")[0];

    // Format time → HH:MM
    const hours = String(now.getHours()).padStart(2, "0");
    const minutes = String(now.getMinutes()).padStart(2, "0");
    const currentTime = `${hours}:${minutes}`;

    if (dateInput) dateInput.value = today;
    if (timeInput) timeInput.value = currentTime;
}

setCurrentDateTime();



async function loadData() {
    try {
        const res = await fetch("/api/teacher/academic-structure");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        const list = data.response;

        academicDiv.innerHTML = ""; // clear first

        list.forEach(item => {

            const wrapper = document.createElement("div");
            wrapper.classList.add("academic-item");

            wrapper.innerHTML = `
                <label>
                    <input type="checkbox" 
                           value="${item.academicId}" 
                           data-year="${item.year}"
                           data-semester="${item.semester}"
                           data-branch="${item.branch}"
                           data-class="${item.className}"
                           data-batch="${item.batch}">
                    
                    ${item.year} | ${item.semester} | 
                    ${item.branch} | ${item.className} | 
                    ${item.batch} 
                    (${item.studentCount} Students)
                </label>
            `;

            academicDiv.appendChild(wrapper);
        });

    } catch (error) {
        console.error(error);
        showSnackbar("Failed to load academic data", "error");
    }
}

loadData();



const subjectInput = document.getElementById("userId");
const updateBtn = document.getElementById("update");

async function createAttendance() {

    const subjectName = subjectInput.value.trim();
    const attendanceDate = dateInput.value;
    const attendaceTime = timeInput.value;

    const academicIds = Array.from(
        document.querySelectorAll(".academic input:checked")
    ).map(cb => cb.value);

    if (!subjectName || !attendanceDate || !attendaceTime || academicIds.length === 0) {
        showSnackbar("All fields are required", "error");
        return;
    }

    try {

        const res = await fetch("/api/teacher/attendance", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify({
                subjectName,
                attendanceDate,
                attendaceTime,
                academicIds
            })
        });

        const data = await res.json();

        if (!res.ok) {
            showSnackbar("Failed to create attendance", "error");
            return;
        }

        console.log("Attendance created:", data.attendanceId);

        showSnackbar("Attendance session created", "success");

    } catch (err) {
        console.error(err);
        showSnackbar("Something went wrong", "error");
    }
}

updateBtn.addEventListener("click", createAttendance);