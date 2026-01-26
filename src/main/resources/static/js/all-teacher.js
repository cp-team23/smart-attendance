function showData(data){
    console.log(data);
}

async function loadTeacher() {
    try {
        const res = await fetch("/api/admin/all-teacher");
        const data = await res.json();
        if (res.ok){
            if(data.response.length === 0){
                 showSnackbar("No Teacher Found.", "warning");
            }else{
                showData(data.response);
            }
        }else{
            showSnackbar("Something went wrong. Try again", "warning");
        }
        
    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadTeacher();