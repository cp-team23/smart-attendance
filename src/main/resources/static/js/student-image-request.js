function showData(data){
    console.log(data);
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
        
    } catch (err) {
        showSnackbar("Something went wrong. Try again", "error");
    }
}

loadImageRequest();