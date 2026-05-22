async function signin() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const rememberMe = document.getElementById("rememberMe").checked;

    const user = {
        email: email,
        password: password,
        rememberMe: rememberMe
    };

    const userJSON = JSON.stringify(user);

    const response = await fetch("AdminSignIn", {
        method: "POST",
        body: userJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "admin_verification.html";
        } else {
            document.getElementById("notification").innerHTML = json.message;
            document.getElementById("loginNotification").classList.remove('d-none');
        }
    } else {
        document.getElementById("notification").innerHTML = "Login failed. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
    }
}

async function forgotPassword() {
    const email = document.getElementById("email").value;

    const user = {
        email: email
    };

    const userJSON = JSON.stringify(user);

    const response = await fetch("AdminForgotPasswordVerification", {
        method: "POST",
        body: userJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "admin_forgot_password.html";
        } else {
            document.getElementById("notification").innerHTML = json.message;
            document.getElementById("loginNotification").classList.remove('d-none');
        }
    } else {
        document.getElementById("notification").innerHTML = "Something went wrong. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
    }
}



