const cardsContainer = document.getElementById("cardsContainer");
const searchInput = document.getElementById("searchInput");
const refreshBtn = document.getElementById("refreshBtn");


let allTeachers = [];

/* =========================
   FETCH TEACHERS
========================= */
async function fetchTeachers() {
    try {
        const response = await fetch("/api/admin/all-teacher");
        const data = await response.json();

        allTeachers = data.response || [];
        renderCards(allTeachers);
        console.log("done");
    } catch (error) {
        console.error("Error fetching teachers:", error);
    }
}

/* =========================
   RENDER CARDS
========================= */
function renderCards(teachers) {

    if (teachers.length === 0) {
        cardsContainer.innerHTML = "<p>No teachers found</p>";
        return;
    }

    let cards = "";

    teachers.forEach(teacher => {

        const initials = teacher.name
            .split(" ")
            .map(word => word[0])
            .join("")
            .toUpperCase();

        cards += `
            <div class="teacher-card" data-id="${teacher.userId}">
                <div>
                    <div class="card-top">
                        <div class="avatar">${initials}</div>
                        <div>
                            <div class="teacher-name">${teacher.name}</div>
                            <div class="teacher-id">Teacher ID: ${teacher.userId}</div>
                        </div>
                    </div>

                    <div class="card-info">
                        <div class="info-row">
                            <span class="info-label">Email:</span>
                            ${teacher.email}
                        </div>
                        <div class="info-row">
                            <span class="info-label">College:</span>
                            ${teacher.collegeName}
                        </div>
                    </div>
                </div>

                <div class="card-actions">
                    <button class="card-btn edit-btn">Edit</button>
                    <button class="card-btn delete-btn">Delete</button>
                </div>
            </div>
        `;
    });

    cardsContainer.innerHTML = cards;
}

/* =========================
   SEARCH BY USER ID
========================= */
searchInput.addEventListener("input", function () {

    const searchValue = searchInput.value.toLowerCase();

    const filteredTeachers = allTeachers.filter(teacher =>
        teacher.userId.toLowerCase().includes(searchValue)
    );

    renderCards(filteredTeachers);
});



 cardsContainer.addEventListener("click", function (e) {

    const card = e.target.closest(".teacher-card");
    if (!card) return;

    const teacherId = card.dataset.id;

    if (e.target.classList.contains("edit-btn")) {
        window.location.href = "/admin/user/update/teacher/" + teacherId;
    }

    if (e.target.classList.contains("delete-btn")) {
        console.log("Delete:", teacherId);
        deleteTeacher(teacherId).then(()=>fetchTeachers());
    }
});

/* =========================
   REFRESH BUTTON
========================= */
refreshBtn.addEventListener("click", fetchTeachers);

/* =========================
   INITIAL LOAD
========================= */
fetchTeachers();