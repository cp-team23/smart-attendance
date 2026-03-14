const cardsContainer = document.getElementById("cardsContainer");
const searchInput = document.getElementById("searchInput");

/* =========================
   LOAD DATA FROM BACKEND
========================= */
async function loadImageRequest() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/all-image-change-request");

        if (!res.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to fetch requests", "error");
            return;
        }

        const data = await res.json();

        if (!data.response || data.response.length === 0) {
            cardsContainer.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📂</div>
                    <h3>No Image Requests</h3>
                    <p>All image change requests have been processed.</p>
                </div>
            `;
            removeLoader(); // 👈
            return;
        }

        renderCards(data.response);
        removeLoader(); // 👈

    } catch (err) {
        console.error("Server error:", err);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
}

loadImageRequest();

/* =========================
   RENDER CARDS
========================= */
function renderCards(data) {
    let html = "";

    data.forEach(element => {
        html += `
        <div class="approval-card"
             data-id="${element.userId}"
             data-enrollment="${element.enrollmentNo}">

            <div class="image-section">
                <img src="${element.curImage}">
                <img class="new-img" src="${element.newImage}">
            </div>

            <div class="student-section">
                <h3 class="student-name">${element.name}</h3>

                <div class="student-basic">
                    <span>${element.userId}</span>
                    <span>${element.enrollmentNo}</span>
                    <span>${element.email}</span>
                </div>

                <div class="student-academic">
                    <span>${element.branch}</span>
                    <span>${element.semester}</span>
                    <span>${element.className}</span>
                    <span>${element.batch}</span>
                </div>

                <div class="action-buttons">
                    <button class="approve">Approve</button>
                    <button class="reject">Reject</button>
                </div>
            </div>

        </div>
        `;
    });

    cardsContainer.innerHTML = html;
}

/* =========================
   SEARCH BY ENROLLMENT
========================= */
searchInput.addEventListener("input", function () {
    const value = this.value.toLowerCase();
    const cards = document.querySelectorAll(".approval-card");

    cards.forEach(card => {
        const enrollment = card.dataset.enrollment.toLowerCase();
        card.style.display = enrollment.includes(value) ? "flex" : "none";
    });
});

/* =========================
   APPROVE / REJECT
========================= */
cardsContainer.addEventListener("click", async function (e) {
    const card = e.target.closest(".approval-card");
    if (!card) return;

    const cardId = card.dataset.id;

    // APPROVE
    if (e.target.classList.contains("approve")) {
        const ok = await showConfirm({
            title: "Approve Request ?",
            message: "Are you sure you want to approve this request? This action cannot be undone.",
            confirmText: "Approve"
        });
        if (!ok) return;

        showLoader(); // 👈
        try {
            const res = await fetch(`/api/admin/image-change-request/${cardId}/approve`, {
                method: "PATCH"
            });

            if (res.ok) {
                showSuccess(); // 👈
                card.classList.add("swipe-right");
                setTimeout(() => card.remove(), 400);
            } else {
                removeLoader(); // 👈
                showSnackbar("Approve failed", "error");
            }

        } catch (err) {
            console.error(err);
            removeLoader(); // 👈
            showSnackbar("Something went wrong", "error");
        }
    }

    // REJECT
    if (e.target.classList.contains("reject")) {
        const ok = await showConfirm({
            title: "Reject Request ?",
            message: "Are you sure you want to reject this request? This action cannot be undone.",
            confirmText: "Reject"
        });
        if (!ok) return; // 👈 was missing in original!

        showLoader(); // 👈
        try {
            const res = await fetch(`/api/admin/image-change-request/${cardId}/reject`, {
                method: "DELETE"
            });

            if (res.ok) {
                showSuccess(); // 👈
                card.classList.add("swipe-left");
                setTimeout(() => card.remove(), 400);
            } else {
                removeLoader(); // 👈
                showSnackbar("Reject failed", "error");
            }

        } catch (err) {
            console.error(err);
            removeLoader(); // 👈
            showSnackbar("Something went wrong", "error");
        }
    }
});