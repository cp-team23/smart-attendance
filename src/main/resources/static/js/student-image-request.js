document.getElementById("gotoDashboard").addEventListener("click",()=>window.location.href = "/admin/dashboard");

const contentbody = document.querySelector(".contentbody");

function showData(data){
    let html = "";
    data.forEach(element => {
        html +=`<div class="student-card" data-id="${element.userId}">
                <div class="student-card-img-2">
                    <div><img class="img" src="${element.curImage}"><div class="imgtext">Current Image</div></div>
                    <div><img class="img img2" src="${element.newImage}"><div class="imgtext">New Image</div></div>
                </div>
                <div class="student-card-info">
                    <div class="student-card-data">
                        <div class="student-card-data-header">
                            <div class="userid">${element.userId}</div>
                            <div class="year">${element.year}</div>
                        </div>
                        <div class="en-no data">${element.enrollmentNo}</div>
                        <div class="email data">${element.email}</div>
                        <div class="branch-sem">
                            <div class="branch data">${element.branch}</div>
                            <div class="sem data">${element.semester}</div>
                        </div>
                        <div class="class-batch">
                            <div class="class-name data">${element.className}</div>
                            <div class="batch data">${element.batch}</div>
                        </div>
                    </div>
                    <div class="student-card-btn">
                        <button class="darkBtn delete-btn">Reject</button>
                        <button class="darkBtn update-btn">Approve</button>
                    </div>
                </div>
           </div>
        </div>`;
    });
    contentbody.innerHTML = html;
}





async function loadImageRequest() {
    try {
        const res = await fetch("/api/admin/all-image-change-request");
        const data = await res.json();
        if (res.ok){
            if(data.response.length === 0){
                 showSnackbar("No request found.", "success");
            }else{
                showData(data.response);
            }
        }else{
            showSnackbar("Something went wrong. Try again", "warning");
        }
        console.log(data);
    } catch (err) {
        showSnackbar("Something went wrong. Try again" + err, "error");
    }
}

loadImageRequest();

async function approve(cardId) {
    await fetch(`/api/admin/image-change-request/${cardId}/approve`, {
        method: "PATCH"
    });
}

async function reject(cardId) {
    await fetch(`/api/admin/image-change-request/${cardId}/reject`, {
        method: "DELETE"
    });
}


contentbody.addEventListener("click", function (e) {

    if (e.target.classList.contains("update-btn")) {
        e.stopPropagation();
        const card = e.target.closest(".student-card");
        const cardId = card.dataset.id;
        approve(cardId);
    }

    if (e.target.classList.contains("delete-btn")) {
        e.stopPropagation();

        const card = e.target.closest(".student-card");
        const cardId = card.dataset.id;
        reject(cardId);
        loadImageRequest();
    }

});