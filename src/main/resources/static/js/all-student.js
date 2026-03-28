const app = document.getElementById("app");
let academicId = app.dataset.academicId || null;

const searchInput = document.getElementById("searchInput");

const yearInput = document.getElementById("yearInput");
const yearOptionsBox = document.getElementById("yearOption");

const branchInput = document.getElementById("branchInput");
const branchOptionsBox = document.getElementById("branchOption");

const semInput = document.getElementById("semInput");
const semOptionsBox = document.getElementById("semOption");

const classInput = document.getElementById("classInput");
const classOptionsBox = document.getElementById("classOption");

const batchInput = document.getElementById("batchInput");
const batchOptionsBox = document.getElementById("batchOption");

const contentbody = document.querySelector(".contentbody");

const ALL = "All";

let allData = [];
let allStudents = [];

// ─── Render ───────────────────────────────────────────────────────────────────

function showData(students) {
    if (students.length === 0) {
        contentbody.innerHTML = `<p class="notfound">No students found</p>`;
        return;
    }
    students.sort((a, b) => a.enrollmentNo.localeCompare(b.enrollmentNo));
    contentbody.innerHTML = students.map(s => `
        <div class="student-row" data-en="${s.enrollmentNo}" data-id="${s.userId}">
            <div class="student-profile">
                <img src="${s.curImage}" alt="Student">
                <div>
                    <div class="student-name">${s.name}</div>
                    <div class="det">
                        <div class="student-email">${s.userId}</div>
                        <div class="student-field">Enrollment: ${s.enrollmentNo}</div>
                        <div class="student-field">Email : ${s.email}</div>
                    </div>
                </div>
            </div>
            <div class="student-actions">
                <button class="update-btn">Edit</button>
                <button class="delete-btn">Delete</button>
            </div>
        </div>`).join("");
}

// ─── Get matching academicIds from current filter state ───────────────────────

function getFilteredAcademicIds() {
    const y  = yearInput.value;
    const br = branchInput.value;
    const sm = semInput.value;
    const cl = classInput.value;
    const bt = batchInput.value;

    return allData
        .filter(e =>
            (!y  || y  === ALL || e.year      === y)  &&
            (!br || br === ALL || e.branch    === br) &&
            (!sm || sm === ALL || e.semester  === sm) &&
            (!cl || cl === ALL || e.className === cl) &&
            (!bt || bt === ALL || e.batch     === bt)
        )
        .map(e => e.academicId);
}

// ─── Fetch students from backend ──────────────────────────────────────────────

async function fetchStudents(academicIds) {
    showLoader();
    try {
        const res = await fetch("/api/admin/student/all", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(academicIds)
        });

        const data = await res.json();

        if (!res.ok) {
            showSnackbar("Something went wrong. Try again", "warning");
            removeLoader();
            return;
        }

        allStudents = data.response ?? [];
        removeLoader();
        applySearch();

        if (allStudents.length === 0) {
            showSnackbar("No students found.", "warning");
        }

    } catch (e) {
        removeLoader();
        showSnackbar("Something went wrong. Try again", "error");
    }
}

// ─── Search filter (client-side only) ────────────────────────────────────────

function applySearch() {
    const q = searchInput.value.toLowerCase().trim();
    if (!q) {
        showData(allStudents);
        return;
    }
    const filtered = allStudents.filter(s =>
        s.name.toLowerCase().includes(q) ||
        s.userId.toLowerCase().includes(q) ||
        s.enrollmentNo.toLowerCase().includes(q) ||
        s.email.toLowerCase().includes(q)
    );
    showData(filtered);
}

// ─── On filter change → get ids → fetch ──────────────────────────────────────

function onFilterChange() {
    const ids = getFilteredAcademicIds();
    if (ids.length === 0) {
        contentbody.innerHTML = `<p class="notfound">No matching academic group found</p>`;
        return;
    }
    fetchStudents(ids);
}

// ─── Dropdown builder ─────────────────────────────────────────────────────────

function buildDropdown(box, input, items) {
    box.innerHTML = "";
    const options = [ALL, ...items];
    options.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        if (input.value === item) li.classList.add("selected");
        li.onclick = (e) => {
            e.stopPropagation();
            input.value = item;
            input.classList.add("filled"); // always keep label up
            box.style.display = "none";
            onFilterChange();
        };
        box.appendChild(li);
    });
}

// ─── Unique values helpers ────────────────────────────────────────────────────

function uniq(arr) {
    return [...new Set(arr)].sort();
}

function filteredUniq(field, filters = {}) {
    return uniq(
        allData
            .filter(e =>
                (!filters.year      || filters.year      === ALL || e.year      === filters.year) &&
                (!filters.branch    || filters.branch    === ALL || e.branch    === filters.branch) &&
                (!filters.semester  || filters.semester  === ALL || e.semester  === filters.semester) &&
                (!filters.className || filters.className === ALL || e.className === filters.className)
            )
            .map(e => e[field])
    );
}

// ─── Toggle dropdown ──────────────────────────────────────────────────────────

const allBoxes = [yearOptionsBox, branchOptionsBox, semOptionsBox, classOptionsBox, batchOptionsBox];

function toggleDropdown(box, buildFn) {
    const isOpen = box.style.display === "block";
    allBoxes.forEach(b => b.style.display = "none");
    if (!isOpen) {
        buildFn();
        box.style.display = "block";
    }
}

// ─── Wire up inputs ───────────────────────────────────────────────────────────

yearInput.onclick = (e) => {
    e.stopPropagation();
    toggleDropdown(yearOptionsBox, () => {
        buildDropdown(yearOptionsBox, yearInput, filteredUniq("year"));
    });
};

branchInput.onclick = (e) => {
    e.stopPropagation();
    toggleDropdown(branchOptionsBox, () => {
        buildDropdown(branchOptionsBox, branchInput,
            filteredUniq("branch", { year: yearInput.value })
        );
    });
};

semInput.onclick = (e) => {
    e.stopPropagation();
    toggleDropdown(semOptionsBox, () => {
        buildDropdown(semOptionsBox, semInput,
            filteredUniq("semester", { year: yearInput.value, branch: branchInput.value })
        );
    });
};

classInput.onclick = (e) => {
    e.stopPropagation();
    toggleDropdown(classOptionsBox, () => {
        buildDropdown(classOptionsBox, classInput,
            filteredUniq("className", { year: yearInput.value, branch: branchInput.value, semester: semInput.value })
        );
    });
};

batchInput.onclick = (e) => {
    e.stopPropagation();
    toggleDropdown(batchOptionsBox, () => {
        buildDropdown(batchOptionsBox, batchInput,
            filteredUniq("batch", { year: yearInput.value, branch: branchInput.value, semester: semInput.value, className: classInput.value })
        );
    });
};

// ─── Close on outside click ───────────────────────────────────────────────────

document.addEventListener("click", (e) => {
    if (!e.target.closest(".custom-dropdown")) {
        allBoxes.forEach(b => b.style.display = "none");
    }
});

// ─── Search ───────────────────────────────────────────────────────────────────

searchInput.addEventListener("input", applySearch);

// ─── Load academic structure ──────────────────────────────────────────────────

async function loadData() {
    showLoader();
    try {
        const res = await fetch("/api/admin/academic-structure");
        const data = await res.json();
        allData = data.response;
        removeLoader();
    } catch (err) {
        removeLoader();
        showSnackbar("Failed to load academic structure", "error");
        throw err;
    }
}

// ─── Set inputs from an academicData entry ────────────────────────────────────

function prefillInputs(entry) {
    yearInput.value   = entry.year;      yearInput.classList.add("filled");
    branchInput.value = entry.branch;    branchInput.classList.add("filled");
    semInput.value    = entry.semester;  semInput.classList.add("filled");
    classInput.value  = entry.className; classInput.classList.add("filled");
    batchInput.value  = entry.batch;     batchInput.classList.add("filled");
}

// ─── Initial load ─────────────────────────────────────────────────────────────

async function initialLoad() {
    if (academicId !== null) {
        // came from another page with specific academicId → load only that
        const entry = allData.find(e => e.academicId === academicId);
        if (entry) prefillInputs(entry);
        fetchStudents([academicId]);

    } else {
        // no academicId → load first entry that has students
        const firstEntry = allData.find(e => e.studentCount > 0) || allData[0];
        if (firstEntry) {
            prefillInputs(firstEntry);
            fetchStudents([firstEntry.academicId]);
        }
        // user "All" select kare tab onFilterChange() sab load karega
    }
}

// ─── Delete / Edit ────────────────────────────────────────────────────────────

contentbody.addEventListener("click", function (e) {
    const card = e.target.closest(".student-row");
    if (!card) return;

    if (e.target.classList.contains("update-btn")) {
        window.location.href = "/admin/user/update/student/" + card.dataset.en;
    }

    if (e.target.classList.contains("delete-btn")) {
        deleteStudent(card.dataset.id).then(() => onFilterChange());
    }
});

// ─── Init ─────────────────────────────────────────────────────────────────────

loadData().then(() => initialLoad());