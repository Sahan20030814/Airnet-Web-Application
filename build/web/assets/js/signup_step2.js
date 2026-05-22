async function confirmAgreement() {

    const response = await fetch("SignUpStep2");

    if (response.ok) {   //success
        const json = await response.json();

        if (json.status === "0") {   // email is not in session
            window.location = "signin.html";
        } else if (json.status === "1") {   // email is not in database
            window.location = "signup_step1.html";
        } else if (json.status === "2") {   // email was already verified
            window.location = "signin.html";
        } else if (json.status === "3") {   // verify email address
            window.location = "signup_step3.html";
        }

    } else {
        window.location = "signin.html";
    }

}


