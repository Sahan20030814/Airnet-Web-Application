async function verifyAccount() {

    const code = document.getElementById("verificationCode").value;

    const verification = {
        verificationCode: code
    };

    const verificationJson = JSON.stringify(verification);

    const response = await fetch("AdminVerification", {
        method: "POST",
        body: verificationJson,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "admin_panel.html";
        } else {
            if (json.message === "1") {
                document.getElementById("notification").innerHTML = "Your session was expired. Please sign in again!";
                document.getElementById("loginNotification").classList.remove('d-none');
                setTimeout(() => {
                    window.location = "admin_signin.html";
                }, 2500);
            } else if (json.message === "2") {
                document.getElementById("notification").innerHTML = "Something went wrong. Please sign in again!";
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
        document.getElementById("notification").innerHTML = "Account verification failed. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
    }

}

async function resendCode() {

    document.getElementById("resendSnipper").classList.remove('d-none');
    const response = await fetch("ResendAdminVerifyCode");

    if (response.ok) {   //success
        const json = await response.json();

        if (json.status === "0") {   // email is not in session
            document.getElementById("resendSnipper").className = "spinner-border spinner-border-sm text-light ms-2 d-none";
            document.getElementById("notification").innerHTML = "Your session was expired. Please sign in again!";
            document.getElementById("loginNotification").classList.remove('d-none');
            setTimeout(() => {
                window.location = "admin_signin.html";
            }, 2500);
        } else if (json.status === "1") {   // email is not in database
            document.getElementById("resendSnipper").className = "spinner-border spinner-border-sm text-light ms-2 d-none";
            document.getElementById("notification").innerHTML = "Something went wrong. Please sign in again!";
            document.getElementById("loginNotification").classList.remove('d-none');
            setTimeout(() => {
                window.location = "admin_signin.html";
            }, 2500);
        } else if (json.status === "2") {   // email was already verified
            setTimeout(() => {
                document.getElementById("resendSnipper").className = "spinner-border spinner-border-sm text-light ms-2 d-none";
                document.getElementById("notification").innerHTML = "Verification code resent successful!";
                document.getElementById("loginNotification").classList.remove('d-none');
            }, 1500);
        }

    } else {
        document.getElementById("resendSnipper").className = "spinner-border spinner-border-sm text-light ms-2 d-none";
        document.getElementById("notification").innerHTML = "Something went wrong. Please sign in again!";
        document.getElementById("loginNotification").classList.remove('d-none');
        setTimeout(() => {
            window.location = "admin_signin.html";
        }, 2500);
    }
}

