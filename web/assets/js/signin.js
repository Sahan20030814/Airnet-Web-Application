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

    const response = await fetch("SignIn", {
        method: "POST",
        body: userJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            if (json.message === "1") {
                window.location = "signup_step2.html";
            } else {
                window.location = "index.html";
            }
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

    const response = await fetch("ForgotPasswordVerification", {
        method: "POST",
        body: userJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            window.location = "forgot_password.html";
        } else {
            document.getElementById("notification").innerHTML = json.message;
            document.getElementById("loginNotification").classList.remove('d-none');
        }
    } else {
        document.getElementById("message").innerHTML = "Something went wrong. Please try again later!";
    }

}



