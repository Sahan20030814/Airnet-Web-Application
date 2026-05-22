async function updatePassword() {
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const passwordData = {
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };

    const passwordDataJSON = JSON.stringify(passwordData);

    const response = await fetch("AdminUpdatePassword", {
        method: "PUT",
        body: passwordDataJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            document.getElementById("notification").innerHTML = json.message;
            document.getElementById("loginNotification").classList.remove('d-none');
            document.getElementById("loginNotification").className = "notification-area text-center bg-success";
            document.getElementById("notification").className = "notification-text text-light";

            setTimeout(() => {
                window.location = "admin_signin.html";
            }, 2500);

        } else {

            if (json.message === "1") {
                document.getElementById("notification").innerHTML = "Your session was timeout.";
                document.getElementById("loginNotification").classList.remove('d-none');

                setTimeout(() => {
                    window.location = "admin_signin.html";
                }, 2500);
            } else if (json.message === "2") {
                document.getElementById("notification").innerHTML = "Something went wrong!";
                document.getElementById("loginNotification").classList.remove('d-none');

                setTimeout(() => {
                    window.location = "admin_signin.html";
                }, 2500);
            } else {
                document.getElementById("notification").innerHTML = json.message;
                document.getElementById("loginNotification").classList.remove('d-none');
            }
        }

    } else {
        document.getElementById("notification").innerHTML = "Something went wrong. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
    }
}

