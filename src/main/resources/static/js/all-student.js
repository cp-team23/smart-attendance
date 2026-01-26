const app = document.getElementById("app");
let academicId = app.dataset.academicId;



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

let allData = [];
let year = [];
let branch = [];
let sem = [];
let className = [];
let batch = [];


function showData(student){
    console.log(student);
} 


async function loadData() {
    const res = await fetch("/api/admin/academic-structure");
    const data = await res.json();

    allData = data.response;

    allData.forEach(element => {
        if (!year.includes(element.year)) {
            year.push(element.year);
        }
    });
}

function setBatch() {
    batchOptionsBox.innerHTML = "";

    batch.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            batchInput.value = item;
            batchInput.classList.add("filled");
            batchOptionsBox.style.display = "none";

            allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && batchInput.value === element.batch) {
                    academicId = element.academicId;
                }
            });
            loadStudent(academicId);
        };
        batchOptionsBox.appendChild(li);
    });
}


function setClass() {
    classOptionsBox.innerHTML = "";
    batch = [];
    className.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            classInput.value = item;
            classInput.classList.add("filled");
            classOptionsBox.style.display = "none";
        };
        classOptionsBox.appendChild(li);
    });
}

function setSem() {
    semOptionsBox.innerHTML = "";
    className = [];
    sem.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            semInput.value = item;
            semInput.classList.add("filled");
            semOptionsBox.style.display = "none";
        };
        semOptionsBox.appendChild(li);
    });
}

function setBranch() {
    branchOptionsBox.innerHTML = "";
    sem = [];
    branch.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
             if(item!==branchInput.value){
                semInput.value = "";
                semInput.classList.remove("filled");
                semOptionsBox.style.display = "none";
                classInput.value = "";
                classInput.classList.remove("filled");
                classOptionsBox.style.display = "none";
                batchInput.value = "";
                batchInput.classList.remove("filled");
                batchOptionsBox.style.display = "none";
            }
            branchInput.value = item;
            branchInput.classList.add("filled");
            branchOptionsBox.style.display = "none";
        };
        branchOptionsBox.appendChild(li);
    });
}

function setYear() {
    yearOptionsBox.innerHTML = "";
    year.forEach(item => {
        const li = document.createElement("li");
        li.textContent = item;
        li.onclick = () => {
            if(item!==yearInput.value){
                branchInput.value = "";
                branchInput.classList.remove("filled");
                branchOptionsBox.style.display = "none";
                semInput.value = "";
                semInput.classList.remove("filled");
                semOptionsBox.style.display = "none";
                classInput.value = "";
                classInput.classList.remove("filled");
                classOptionsBox.style.display = "none";
                batchInput.value = "";
                batchInput.classList.remove("filled");
                batchOptionsBox.style.display = "none";
            }
            yearInput.value = item;
            yearInput.classList.add("filled");
            yearOptionsBox.style.display = "none";

        };
        yearOptionsBox.appendChild(li);
    });

}

loadData().then(() => {
    setYear();
});

yearInput.onclick = () => {
    branch = [];
    yearOptionsBox.style.display =
        yearOptionsBox.style.display === "block" ? "none" : "block";
};

branchInput.onclick = () => {
    sem = [];
    allData.forEach(element => {
                if (yearInput.value == element.year && !branch.includes(element.branch)) {
                    branch.push(element.branch);
                }
    });
    setBranch();
    branchOptionsBox.style.display =
        branchOptionsBox.style.display === "block" ? "none" : "block";
};


semInput.onclick = () => {
    className = [];
     allData.forEach(element => {
                if (yearInput.value == element.year && branchInput.value === element.branch && !sem.includes(element.semester)) {
                    sem.push(element.semester);
                }
    });
    setSem();
    semOptionsBox.style.display =
        semOptionsBox.style.display === "block" ? "none" : "block";
};

classInput.onclick = () => {
    batch = [];
    allData.forEach(element => {
                if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && !className.includes(element.className)) {
                    className.push(element.className);
                }
    });
    setClass();
    classOptionsBox.style.display =
        classOptionsBox.style.display === "block" ? "none" : "block";
};

batchInput.onclick = () => {
    
    allData.forEach(element => {
        if (yearInput.value === element.year && branchInput.value === element.branch && semInput.value === element.semester && classInput.value === element.className && !batch.includes(element.batch)) {
            batch.push(element.batch);
        }
    });
    setBatch();
    batchOptionsBox.style.display =
        batchOptionsBox.style.display === "block" ? "none" : "block";
};

async function loadStudent(academicId){
 try{
        const res = await fetch("/api/admin/all-student/" +academicId);
        const data = await res.json();
        if(res.ok){
            if(data.response.length === 0){
                showSnackbar("Student Not found.", "success");
            }else{
                yearInput.value = data.response[0].year;
                yearInput.classList.add("filled");
                branchInput.value = data.response[0].branch;
                branchInput.classList.add("filled");
                semInput.value = data.response[0].semester;
                semInput.classList.add("filled");
                classInput.value = data.response[0].className;
                classInput.classList.add("filled");
                batchInput.value = data.response[0].batch;
                batchInput.classList.add("filled");
                showData(data.response);
            }
        }
        else{
            showSnackbar("Something went wrong. Try again","warning");
            return ;  

        }
    }catch{
        showSnackbar("Something went wrong. Try again","error");
    }
}

loadStudent(academicId);

