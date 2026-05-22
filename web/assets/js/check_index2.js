window.addEventListener("load", async function () {

    const response = await fetch("CheckSignIn");

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            document.getElementById("myAccount_btn").classList.remove('d-none');
            document.getElementById("userDropdown").innerHTML = "<i class='fas fa-user-circle'></i> Hello " + json.user.first_name;
        } else {
            document.getElementById("myAccount_btn").classList.add('d-none');
        }

    } else {
        document.getElementById("myAccount_btn").classList.add('d-none');
    }

});

async function logout() {
    const response = await fetch("SignOut");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {

            if (window.location.pathname.endsWith("index.html") | window.location.pathname.endsWith("searched_contents.html") |
                    window.location.pathname.endsWith("single_product_view.html")) {

                window.location.reload();

            } else {
                window.location = 'index.html';
            }

        } else {
            swal({
                title: "Error message!",
                text: "Logout failed!",
                type: "error",
                timer: 5000
            });
        }

    } else {
        swal({
            title: "Error message!",
            text: "Logout failed!",
            type: "error",
            timer: 5000
        });
    }
}


