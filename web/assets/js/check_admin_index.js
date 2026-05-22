window.addEventListener("load", async function () {

    const response = await fetch("CheckAdminSignIn");

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            document.getElementById("myAccount_btn").classList.remove('d-none');
            document.getElementById("userDropdown").innerHTML = "<i class='fas fa-user-circle'></i> Hello " + json.admin.first_name;
        } else {
            document.getElementById("myAccount_btn").classList.add('d-none');
        }

    } else {
        document.getElementById("myAccount_btn").classList.add('d-none');
    }

});

async function logout() {
    const response = await fetch("AdminSignOut");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location.reload();
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


