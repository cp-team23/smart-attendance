
document.getElementById("gotoDashboard").addEventListener("click", () => window.location.href = "/admin/dashboard");
const contentbody = document.querySelector(".contentbody");

function showData(data) {
    let html = "";
    data.forEach(element => {
        html += ` <div class="teacher-card" data-id="${element.userId}">
                <div class="teacher-card-data">
                    <div class="teacherid teacher-data">Taecher Id : ${element.userId}</div>
                    <div class="name teacher-data">Name : ${element.name}</div>
                    <div class="email teacher-data">Email : ${element.email}</div>
                </div>
                <div class="teacher-card-btn">
                    <button class="darkBtn delete-btn">Delete</button>
                    <button class="darkBtn update-btn">Update</button>
                </div>
            </div>`;
    });
    contentbody.innerHTML = html;
}

async function loadTeacher() {
    try {
        const res = await fetch("/api/admin/all-teacher");
        const data = await res.json();
        if (res.ok) {
            if (data.response.length === 0) {
                showSnackbar("No Teacher Found.", "warning");
            } else {
                showData(data.response);
            }
        } else {
            showSnackbar("Something went wrong. Try again", "warning");
        }

    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadTeacher();




async function update(cardId) {
    window.location.href = "/admin/teacher/" + cardId;
}

async function deleteTeacher(cardId) {
    await fetch(`/api/admin/teacher/${cardId}`, {
        method: "DELETE"
    });
    loadStudent(academicId);
}


contentbody.addEventListener("click", function (e) {

    if (e.target.classList.contains("update-btn")) {
        e.stopPropagation();
        const card = e.target.closest(".teacher-card");
        const cardId = card.dataset.id;
        update(cardId);
    }

    if (e.target.classList.contains("delete-btn")) {
        const card = e.target.closest(".teacher-card");
        const cardId = card.dataset.id;

        openModal({
            title: "Delete Student?",
            message: "This action cannot be undone.",
            confirmText: "Delete",
            onConfirm: () => deleteTeacher(cardId)
        });
    }


});