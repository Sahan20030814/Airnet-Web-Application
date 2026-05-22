async function verifyAccount() {

    const code = document.getElementById("verificationCode").value;

    const verification = {
        verificationCode: code
    };

    const verificationJson = JSON.stringify(verification);

    const response = await fetch("SignUpStep3", {
        method: "POST",
        body: verificationJson,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "index.html";
        } else {
            if (json.message === "1") {
                window.location = "signin.html";
            } else if (json.message === "2") {
                window.location = "signup_step1.html";
            } else {
                document.getElementById("notification").innerHTML = json.message;
                document.getElementById("loginNotification").classList.remove('d-none');
            }
        }
    } else {
        document.getElementById("notification").innerHTML = "Account verification failed. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
    }

}

async function resendCode() {

    document.getElementById("resendSnipper").classList.remove('d-none');
    const response = await fetch("ResendVerifyCode");

    if (response.ok) {   //success
        const json = await response.json();

        if (json.status === "0") {   // email is not in session
            window.location = "signin.html";
        } else if (json.status === "1") {   // email is not in database
            window.location = "signup_step1.html";
        } else if (json.status === "2") {   // email was already verified
            window.location = "signin.html";
        } else if (json.status === "3") {   // verify email address

            setTimeout(() => {
                document.getElementById("resendSnipper").className = "spinner-border spinner-border-sm text-light ms-2 d-none";
                document.getElementById("notification").innerHTML = "Verification code resent successful!";
                document.getElementById("loginNotification").classList.remove('d-none');
            }, 1500);

        }

    } else {
        window.location = "signin.html";
    }

}

