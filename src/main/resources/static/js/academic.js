const year = document.getElementById('year');
const branch = document.getElementById('branch');
const sem = document.getElementById('sem');
const className = document.getElementById('className');
const batch = document.getElementById('batch');
const addBtn = document.getElementById('addAcademic');
const contentBody = document.querySelector(".cards-container");
const refreshBtn = document.getElementById('refreshBtn');

const addAcademicTitle = document.getElementById("addAcademicTitle");
let updateBtn = null;
let deleteBtn = null;
let academicId = null;

const API_URL = "/api/admin/academic-structure";

async function loadData() {

    try {

        showLoader();

        const res = await fetch(API_URL);
        const data = await res.json();

        const list = data.response;

        let html = "";
        list.sort((a, b) => {
        return (
            b.year.localeCompare(a.year) ||
            a.branch.localeCompare(b.branch) ||
            a.semester.localeCompare(b.semester) ||
            a.className.localeCompare(b.className) ||
            a.batch.localeCompare(b.batch)
        );
    });
        list.forEach(element => {

            html += `
            <div class="card"
                data-id="${element.academicId}"
                data-year="${element.year}"
                data-branch="${element.branch}"
                data-semester="${element.semester}"
                data-class-name="${element.className}"
                data-batch="${element.batch}">

                <div class="card-top">
                    <div class="branch">${element.branch}</div>
                    <div class="year">${element.year}</div>
                </div>

                <div class="card-middle">

                    <div class="data-sem-class">
                        <div class="sem data">
                            Sem <span class="div">&nbsp;:&nbsp;</span> ${element.semester}
                        </div>

                        <div class="className data">
                            Class <span class="div">&nbsp;:&nbsp;</span> ${element.className}
                        </div>
                    </div>

                    <div class="data-batch-count">

                        <div class="batch data">
                            Batch <span class="div">&nbsp;:&nbsp;</span> ${element.batch}
                        </div>

                        <div class="student-count data">
                            Students <span class="div">&nbsp;:&nbsp;</span> ${element.studentCount}
                        </div>

                        <div class="student-count data">
                            Deleted Students <span class="div">&nbsp;:&nbsp;</span> ${element.deletedStudentstudentCount}
                        </div>

                    </div>

                </div>

                <div class="card-btn">
                    <button class="darkBtn darkBtn2 all-student">All students</button>
                    <button class="darkBtn darkBtn2 update-btn">Update</button>
                    <button class="darkBtn darkBtn2 delete-btn">Delete</button>
                </div>

            </div>`;

        });

        contentBody.innerHTML = html;

    } catch (error) {

        showSnackbar("Failed to load data", "error");

    } finally {

        removeLoader();

    }

}

async function add() {

    const payload = {
        year: year.value.trim(),
        branch: branch.value.trim(),
        semester: sem.value.trim(),
        className: className.value.trim(),
        batch: batch.value.trim()
    };

    if (!payload.year || !payload.branch || !payload.semester || !payload.className || !payload.batch) {
        showSnackbar("Fill data first", "warning");
        return;
    }

    try {

        showLoader();

        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok) {

            showSuccess();
            showSnackbar("Academic Added", "success");

            setTimeout(() => {
                loadData();
            }, 1200);

        }
        else if (result.error === "ACADEMIC_ALREADY_PRESENT") {

            removeLoader();
            showSnackbar("Same data present already", "warning");

        }
        else {

            removeLoader();
            showSnackbar("Something went wrong. Try again", "error");

        }

        year.value = "";
        branch.value = "";
        sem.value = "";
        className.value = "";
        batch.value = "";

    } catch (error) {

        removeLoader();
        showSnackbar("Something went wrong. Try again", "error");

    }

}

async function update(academicId) {

    const ok = await showConfirm({
        title: "Update Academic ?",
        message: "This will update in students!",
        confirmText: "update"
    });

    if (!ok) return;

    const payload = {
        academicId: academicId,
        year: year.value.trim(),
        branch: branch.value.trim(),
        semester: sem.value.trim(),
        className: className.value.trim(),
        batch: batch.value.trim()
    };

    if (!payload.year || !payload.branch || !payload.semester || !payload.className || !payload.batch) {
        showSnackbar("Fill data first", "warning");
        return;
    }

    try {

        showLoader();

        const response = await fetch(API_URL, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (response.ok) {

            showSuccess();
            showSnackbar("Academic updated", "success");

            setTimeout(() => {
                loadData();
            }, 1200);

            year.value = "";
            branch.value = "";
            sem.value = "";
            className.value = "";
            batch.value = "";

            academicId = null;

            addBtn.innerHTML = "Add";

        }
        else if (result.error === "ACADEMIC_ALREADY_PRESENT") {

            removeLoader();
            showSnackbar("Same data present already", "warning");

        }
        else {

            removeLoader();
            showSnackbar("Something went wrong. Try again", "error");

        }

    } catch (error) {

        removeLoader();
        showSnackbar("Something went wrong. Try again", "error");

    }

}

async function deleteAcademic(academicId) {

    const ok = await showConfirm({
        title: "Delete Academic ?",
        message: "Are you sure you want to delete this academic? This action cannot be undone.",
        confirmText: "Delete"
    });

    if (!ok) return;

    if (!academicId) {
        showSnackbar("Invalid academic id", "error");
        return;
    }

    try {

        showLoader();

        const response = await fetch(`${API_URL}/${academicId}`, {
            method: "DELETE"
        });

        const result = await response.json();

        if (response.ok) {

            showSuccess();
            showSnackbar("Academic deleted successfully", "success");

            setTimeout(() => {
                loadData();
            }, 1200);

        }
        else if (result.error === "CANT_DELETE_ACADEMIC") {

            removeLoader();
            showSnackbar("Delete failed: students are assigned to this academic.", "error");

        }
        else if (result.error === "ACADEMIC_NOT_FOUND") {

            removeLoader();
            showSnackbar("Something went wrong", "error");

        }
        else if (result.error === "CANT_DELETE_ACADEMIC_ATTENDANCE") {

            removeLoader();
            showSnackbar("Delete failed: attendance present from this academic", "error");

        }
        else {

            removeLoader();
            showSnackbar("Something went wrong. Try again", "error");

        }

    } catch (error) {

        removeLoader();
        showSnackbar("Something went wrong. Try again", "error");

    }

}

contentBody.addEventListener("click", function (e) {

    if (e.target.classList.contains("update-btn")) {

        e.stopPropagation();

        const card = e.target.closest(".card");

        if (e.target.innerText === "Cancle") {

            year.value = "";
            branch.value = "";
            sem.value = "";
            className.value = "";
            batch.value = "";

            academicId = null;

            e.target.innerText = "Update";
            addBtn.innerHTML = "Add";
            addAcademicTitle.innerHTML = "Add New Record";

            updateBtn = null;

            card.classList.remove("selected");

        } else {

            academicId = card.dataset.id;

            year.value = card.dataset.year;
            branch.value = card.dataset.branch;
            sem.value = card.dataset.semester;
            className.value = card.dataset.className;
            batch.value = card.dataset.batch;

            e.target.innerText = "Cancle";

            addBtn.innerHTML = "Update";
            addAcademicTitle.innerHTML = "Update Record";

            card.classList.add("selected");

            if (updateBtn != null) {

                const prevCard = updateBtn.closest(".card");
                prevCard.classList.remove("selected");
                updateBtn.innerHTML = "Update";

            }

            updateBtn = e.target;

        }

    }

    if (e.target.classList.contains("delete-btn")) {

        e.stopPropagation();

        const card = e.target.closest(".card");
        const cardId = card.dataset.id;

        deleteAcademic(cardId);

    }

    if (e.target.classList.contains("all-student")) {

        e.stopPropagation();

        const card = e.target.closest(".card");
        academicId = card.dataset.id;

        window.location.href = "all-students/" + academicId;

    }

});

addBtn.addEventListener("click", () => {

    if (academicId == null) {
        add();
    } else {
        update(academicId);
    }

});

loadData();