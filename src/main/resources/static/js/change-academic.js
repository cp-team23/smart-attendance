let allData = [];
let academicId1 = null;
let academicId2 = null;
let leftStudents = [];
let rightStudents = [];

/* ================= LOAD DATA ================= */

async function loadData() {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/academic-structure");
        const data = await res.json();

        if (!res.ok) throw new Error("Failed");

        allData = data.response;
        removeLoader(); // 👈
    } catch (err) {
        removeLoader(); // 👈
        showSnackbar("Failed to load academic structure", "error");
        throw err;
    }
}

/* ================= DROPDOWN ================= */

function initDropdown(prefix = "") {

    const yearInput = document.getElementById("yearInput" + prefix);
    const branchInput = document.getElementById("branchInput" + prefix);
    const semInput = document.getElementById("semInput" + prefix);
    const classInput = document.getElementById("classInput" + prefix);
    const batchInput = document.getElementById("batchInput" + prefix);

    const yearOptionsBox = document.getElementById("yearOption" + prefix);
    const branchOptionsBox = document.getElementById("branchOption" + prefix);
    const semOptionsBox = document.getElementById("semOption" + prefix);
    const classOptionsBox = document.getElementById("classOption" + prefix);
    const batchOptionsBox = document.getElementById("batchOption" + prefix);

    let year = [...new Set(allData.map(e => e.year))];

    /* ---------- YEAR ---------- */
    yearInput.onclick = () => {
        closeAll();
        yearOptionsBox.innerHTML = "";

        year.forEach(item => {
            const li = document.createElement("li");
            li.textContent = item;
            li.onclick = () => {
                yearInput.value = item;
                yearInput.classList.add("filled");
                branchInput.value = "";
                semInput.value = "";
                classInput.value = "";
                batchInput.value = "";
                yearOptionsBox.style.display = "none";
            };
            yearOptionsBox.appendChild(li);
        });

        toggle(yearOptionsBox);
    };

    /* ---------- BRANCH ---------- */
    branchInput.onclick = () => {
        if (!yearInput.value) return;

        closeAll();
        branchOptionsBox.innerHTML = "";

        const branch = [...new Set(
            allData.filter(e => e.year == yearInput.value).map(e => e.branch)
        )];

        branch.forEach(item => {
            const li = document.createElement("li");
            li.textContent = item;
            li.onclick = () => {
                branchInput.value = item;
                branchInput.classList.add("filled");
                semInput.value = "";
                classInput.value = "";
                batchInput.value = "";
                branchOptionsBox.style.display = "none";
            };
            branchOptionsBox.appendChild(li);
        });

        toggle(branchOptionsBox);
    };

    /* ---------- SEM ---------- */
    semInput.onclick = () => {
        if (!branchInput.value) return;

        closeAll();
        semOptionsBox.innerHTML = "";

        const sem = [...new Set(
            allData.filter(e =>
                e.year == yearInput.value &&
                e.branch == branchInput.value
            ).map(e => e.semester)
        )];

        sem.forEach(item => {
            const li = document.createElement("li");
            li.textContent = item;
            li.onclick = () => {
                semInput.value = item;
                semInput.classList.add("filled");
                classInput.value = "";
                batchInput.value = "";
                semOptionsBox.style.display = "none";
            };
            semOptionsBox.appendChild(li);
        });

        toggle(semOptionsBox);
    };

    /* ---------- CLASS ---------- */
    classInput.onclick = () => {
        if (!semInput.value) return;

        closeAll();
        classOptionsBox.innerHTML = "";

        const className = [...new Set(
            allData.filter(e =>
                e.year == yearInput.value &&
                e.branch == branchInput.value &&
                e.semester == semInput.value
            ).map(e => e.className)
        )];

        className.forEach(item => {
            const li = document.createElement("li");
            li.textContent = item;
            li.onclick = () => {
                classInput.value = item;
                classInput.classList.add("filled");
                batchInput.value = "";
                classOptionsBox.style.display = "none";
            };
            classOptionsBox.appendChild(li);
        });

        toggle(classOptionsBox);
    };

    /* ---------- BATCH ---------- */
    batchInput.onclick = () => {
        if (!classInput.value) return;

        closeAll();
        batchOptionsBox.innerHTML = "";

        const batch = [...new Set(
            allData.filter(e =>
                e.year == yearInput.value &&
                e.branch == branchInput.value &&
                e.semester == semInput.value &&
                e.className == classInput.value
            ).map(e => e.batch)
        )];

        batch.forEach(item => {
            const li = document.createElement("li");
            li.textContent = item;

            li.onclick = () => {
                batchInput.value = item;
                batchInput.classList.add("filled");
                batchOptionsBox.style.display = "none";

                const selectedAcademic = allData.find(e =>
                    e.year == yearInput.value &&
                    e.branch == branchInput.value &&
                    e.semester == semInput.value &&
                    e.className == classInput.value &&
                    e.batch == item
                );

                if (selectedAcademic) {
                    if (prefix == "") {
                        academicId1 = selectedAcademic.academicId;
                        fetchStudents(academicId1, "left");
                    }
                    if (prefix == "2") {
                        academicId2 = selectedAcademic.academicId;
                        fetchStudents(academicId2, "right");
                    }
                }
            };

            batchOptionsBox.appendChild(li);
        });

        toggle(batchOptionsBox);
    };

    function toggle(box) {
        box.style.display = box.style.display === "block" ? "none" : "block";
    }

    function closeAll() {
        yearOptionsBox.style.display = "none";
        branchOptionsBox.style.display = "none";
        semOptionsBox.style.display = "none";
        classOptionsBox.style.display = "none";
        batchOptionsBox.style.display = "none";
    }
}

/* ================= FETCH STUDENTS ================= */

async function fetchStudents(academicId, side) {
    showLoader(); // 👈
    try {
        const res = await fetch("/api/admin/all-student/" + academicId);
        const data = await res.json();

        if (!res.ok) {
            removeLoader(); // 👈
            showSnackbar("Failed to load students", "error");
            return;
        }

        if (side === "left") {
            leftStudents = data.response;
        } else {
            rightStudents = data.response;
        }

        printStudents(data.response, side);
        removeLoader(); // 👈

    } catch (err) {
        removeLoader(); // 👈
        showSnackbar("Failed to load students", "error");
    }
}

/* ================= PRINT STUDENTS ================= */

function printStudents(students, side) {
    const container = document.getElementById(
        side === "left" ? "leftStudentResult" : "rightStudentResult"
    );

    container.innerHTML = "";

    if (!students || students.length === 0) {
        container.innerHTML = `<p class="notfound">No students found</p>`;
        return;
    }
    students.sort((a, b) => a.enrollmentNo.localeCompare(b.enrollmentNo));
    students.forEach(element => {
        container.innerHTML += `
        <div class="student-row"
            data-en="${element.enrollmentNo}"
            data-id="${element.userId}">
            <div class="student-profile">
                <img src="${element.curImage}" alt="Student">
                <div>
                    <div class="student-name">${element.name}</div>
                    <div class="det">
                        <div class="student-email">${element.userId}</div>
                        <div class="student-field">Enrollment: ${element.enrollmentNo}</div>
                    </div>
                </div>
            </div>
            <div class="student-actions">
                <button class="transfer-btn"
                    onclick='transferStudent(${JSON.stringify(element)}, "${side}")'>
                    ${side === "left" ? "Move to Right" : "Move to Left"}
                </button>
            </div>
        </div>`;
    });
}

/* ================= TRANSFER STUDENT ================= */

async function transferStudent(student, fromSide) {
    const targetAcademicId = fromSide === "left" ? academicId2 : academicId1;

    if (!targetAcademicId) {
        showSnackbar("Select target batch first", "warning");
        return;
    }

    const userData = {
        userId: student.userId,
        academicId: targetAcademicId
    };

    showLoader(); // 👈
    try {
        const response = await fetch('/api/admin/student/academic', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            removeLoader(); // 👈
            showSnackbar("Transfer failed", "error");
            return;
        }

        showSuccess(); // 👈
        // Both sides refresh after success — they carry their own loader cycles
        if (academicId1) fetchStudents(academicId1, "left");
        if (academicId2) fetchStudents(academicId2, "right");

    } catch (err) {
        console.error(err);
        removeLoader(); // 👈
        showSnackbar("Something went wrong", "error");
    }
}

/* ================= INIT ================= */

loadData().then(() => {
    initDropdown("");
    initDropdown("2");
});

/* ================= SEARCH ================= */

searchInput.addEventListener("input", function () {
    const searchValue = this.value.toLowerCase().trim();

    if (!searchValue) {
        printStudents(leftStudents, "left");
        printStudents(rightStudents, "right");
        return;
    }

    const filterLogic = student =>
        student.name.toLowerCase().includes(searchValue) ||
        student.userId.toLowerCase().includes(searchValue) ||
        student.enrollmentNo.toLowerCase().includes(searchValue) ||
        student.email.toLowerCase().includes(searchValue);

    printStudents(leftStudents.filter(filterLogic), "left");
    printStudents(rightStudents.filter(filterLogic), "right");
});

/* ================= REFRESH ================= */

refreshBtn.addEventListener("click", () => {
    if (academicId1) fetchStudents(academicId1, "left");
    if (academicId2) fetchStudents(academicId2, "right");
});