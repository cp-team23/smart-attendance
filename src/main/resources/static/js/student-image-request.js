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
    
    // Show message if no data
    if(data.length === 0){
        contentbody.innerHTML = '<div style="text-align:center; padding:50px; color:#666;">No pending requests</div>';
    }
}

async function loadImageRequest() {
    try {
        const res = await fetch("/api/admin/all-image-change-request");
        const data = await res.json();
        if (res.ok){
            showData(data.response);
        }else{
            showSnackbar("Something went wrong. Try again", "warning");
        }
        console.log(data);
    } catch (err) {
        showSnackbar("Something went wrong. Try again: " + err, "error");
    }
}

loadImageRequest();

async function approve(cardId, cardElement) {
    try {
        // Disable buttons to prevent double-click
        const buttons = cardElement.querySelectorAll('button');
        buttons.forEach(btn => btn.disabled = true);
        
        const res = await fetch(`/api/admin/image-change-request/${cardId}/approve`, {
            method: "PATCH"
        });
        
        if (res.ok) {
            showSnackbar("Request approved successfully", "success");
            // Remove card with animation
            cardElement.style.opacity = '0';
            cardElement.style.transition = 'opacity 0.3s';
            setTimeout(() => {
                cardElement.remove();
                // Check if no cards left
                if(contentbody.children.length === 0){
                    contentbody.innerHTML = '<div style="text-align:center; padding:50px; color:#666;">No pending requests</div>';
                }
            }, 300);
        } else {
            showSnackbar("Failed to approve request", "error");
            buttons.forEach(btn => btn.disabled = false);
        }
    } catch (err) {
        showSnackbar("Error approving request: " + err, "error");
    }
}

async function reject(cardId, cardElement) {
    try {
        // Disable buttons to prevent double-click
        const buttons = cardElement.querySelectorAll('button');
        buttons.forEach(btn => btn.disabled = true);
        
        const res = await fetch(`/api/admin/image-change-request/${cardId}/reject`, {
            method: "DELETE"
        });
        
        if (res.ok) {
            showSnackbar("Request rejected successfully", "success");
            // Remove card with animation
            cardElement.style.opacity = '0';
            cardElement.style.transition = 'opacity 0.3s';
            setTimeout(() => {
                cardElement.remove();
                // Check if no cards left
                if(contentbody.children.length === 0){
                    contentbody.innerHTML = '<div style="text-align:center; padding:50px; color:#666;">No pending requests</div>';
                }
            }, 300);
        } else {
            showSnackbar("Failed to reject request", "error");
            buttons.forEach(btn => btn.disabled = false);
        }
    } catch (err) {
        showSnackbar("Error rejecting request: " + err, "error");
    }
}

contentbody.addEventListener("click", function (e) {

    if (e.target.classList.contains("update-btn")) {
        e.stopPropagation();
        const card = e.target.closest(".student-card");
        const cardId = card.dataset.id;
        approve(cardId, card); // Pass the card element
    }

    if (e.target.classList.contains("delete-btn")) {
        e.stopPropagation();
        const card = e.target.closest(".student-card");
        const cardId = card.dataset.id;
        reject(cardId, card); // Pass the card element
    }

});