window.addEventListener("load", async function () {
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("invoiceId")) {
        const invoiceId = searchParams.get("invoiceId");
        const response = await fetch("LoadAdminInvoiceData?invoiceId=" + invoiceId);

        if (response.ok) {
            const json = await response.json();
            if (json.status) {

                document.getElementById("invoice-id").innerHTML = "<strong>Invoice ID:</strong><span style='color:#e50914;'> #" + json.checkoutList.id + "</span>";
                document.getElementById("invoice-user-name").innerHTML = "<strong>Name:</strong> " + json.checkoutList.user.first_name + " " + json.checkoutList.user.last_name;
                document.getElementById("invoice-user-email").innerHTML = "<strong>Email:</strong> " + json.checkoutList.user.email;
                document.getElementById("invoice-date").innerHTML = "<strong>Date:</strong> " + new Intl.DateTimeFormat("en-CA", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit"
                }).format(new Date(json.checkoutList.registered_at));

                document.getElementById("invoice-time").innerHTML = "<strong>Time:</strong> " + new Intl.DateTimeFormat("en-US", {
                    hour: "2-digit",
                    minute: "2-digit",
                    second: "2-digit",
                    hour12: true
                }).format(new Date(json.checkoutList.registered_at));

                let table_body = document.getElementById("invoice-table-body");
                let table_row = document.getElementById("invoice-table-row");
                table_body.innerHTML = "";
                let count = 1;
                let total = 0;
                let first_content_id = "";
                let first_content_name = "";
                let first_content_type = "";

                json.checkoutItemsList.forEach(checkoutItem => {
                    if (count === 1) {
                        first_content_id = checkoutItem.mainMovie.id;
                        first_content_name = checkoutItem.mainMovie.name;
                        first_content_type = checkoutItem.mainMovie.movieType.name;
                    }

                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");
                    table_row_clone.querySelector("#invoice-table-row-content-id").innerHTML = count;
                    table_row_clone.querySelector("#invoice-table-row-content-name").href = "admin_single_product_view.html?id=" + checkoutItem.mainMovie.id;
                    table_row_clone.querySelector("#invoice-table-row-content-name").innerHTML = checkoutItem.mainMovie.name;
                    table_row_clone.querySelector("#invoice-table-row-content-type").innerHTML = checkoutItem.mainMovie.movieType.name;
                    table_row_clone.querySelector("#invoice-table-row-content-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(checkoutItem.price);

                    table_body.appendChild(table_row_clone);

                    count++;
                    total += checkoutItem.price;
                });

                document.getElementById("invoice-subtotal").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US", {minimumFractionDigits: 2}).format(total);
                document.getElementById("invoice-total").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US", {minimumFractionDigits: 2}).format(total);

            } else {
                window.location = "admin_panel.html";
            }
        } else {
            window.location = "admin_panel.html";
        }
    } else {
        window.location = "admin_panel.html";
    }
});

function printInvoice() {
    var restorePage = document.body.innerHTML;
    var page = document.getElementById("invoice-content-div").innerHTML;
    document.body.innerHTML = page;
    window.print();
    document.body.innerHTML = restorePage;
}

function downloadInvoice() {
    let divToDownload = document.getElementById("invoice-content-area").cloneNode(true);
    let removeDiv1 = divToDownload.querySelector("#button-div");

    if (removeDiv1) {
        removeDiv1.remove();
    }

    let container = document.createElement("div");
    container.appendChild(divToDownload);

    // Use html2pdf to download as PDF
    html2pdf().from(container).set({
        margin: 10,
        filename: 'AIRNET_Invoice.pdf',
        image: {type: 'jpeg', quality: 0.98},
        html2canvas: {scale: 2},
        jsPDF: {unit: 'mm', format: 'a4', orientation: 'portrait'}
    }).save();
}