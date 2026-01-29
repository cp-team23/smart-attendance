function showSnackbar(message, type = "success") {

  const iconMap = {
    success: "/assets/icons/success.svg",
    warning: "/assets/icons/warning.svg",
    error: "/assets/icons/error.svg"
  };

  const snack = document.createElement("div");
  snack.className = `snackbar ${type}`;

  snack.innerHTML = `
    <div class="snack-content">
      <img src="${iconMap[type]}" class="snack-icon" alt="${type}">
      <span>${message}</span>
    </div>
  `;

  document.body.appendChild(snack);

  setTimeout(() => snack.classList.add("show"), 100);

  setTimeout(() => {
    snack.classList.remove("show");
    setTimeout(() => snack.remove(), 300);
  }, 3000);
}

