async function signup() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
    };

    const userJson = JSON.stringify(user);

    const response = await fetch("SignUpStep1",
            {
                method: "POST",
                body: userJson,
                headers: {
                    "Content-Type": "application/json"
                }
            });

    if (response.ok) {   //success
        const json = await response.json();

        if (json.status) {   // if true
            window.location = "signup_step2.html";
        } else {
            document.getElementById("notification").innerHTML = json.notification;
            document.getElementById("loginNotification").classList.remove('d-none');
        }

    } else {
        document.getElementById("notification").innerHTML = "Registration failed. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
    }

}


