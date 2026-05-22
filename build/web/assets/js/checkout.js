// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    checkoutToDatabase(orderId);
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};

function goToSearch() {
    const search_bar_content = document.getElementById("search-bar-content").value;
    if (search_bar_content !== "") {
        window.location = "searched_contents.html?name=" + search_bar_content;
    }
}

let checkout_content_id;

window.addEventListener("load", async function () {
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("id")) {
        const contentId = searchParams.get("id");
        const response = await fetch("LoadCheckoutData?id=" + contentId);
        document.getElementById("checkout-content-go-back-btn").href = "single_product_view.html?id=" + contentId;

        if (response.ok) {
            const json = await response.json();
            if (json.status && !json.contentCheckoutStatus) {

                checkout_content_id = json.checkoutContent.id;

                document.getElementById("checkout-content-viewing-area").classList.remove("d-none");
                document.getElementById("checkout-content-img-a").href = "single_product_view.html?id=" + json.checkoutContent.id;
                document.getElementById("checkout-content-img").src = "product_images\\" + json.checkoutContent.id + "\\card_image.jpg";
                document.getElementById("checkout-content-name").innerHTML = json.checkoutContent.name;
                document.getElementById("checkout-content-quality-type").innerHTML = json.checkoutContent.qualityType.name;
                document.getElementById("checkout-content-rating").innerHTML = "<i class='fas fa-star text-warning'></i> " + json.checkoutContent.rating + "/10 IMDB";

                document.getElementById("checkout-content-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.checkoutContent.price);

                document.getElementById("checkout-content-description").innerHTML = json.checkoutContent.description;
                document.getElementById("checkout-content-released-at").innerHTML = "<strong>Released:</strong> " + new Intl.DateTimeFormat("en-CA", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit"
                }).format(new Date(json.checkoutContent.released_at));

                document.getElementById("checkout-content-genre-line").innerHTML = "<strong>Genre:</strong> " + json.genre_line;
                document.getElementById("checkout-content-language-line").innerHTML = "<strong>Language:</strong> " + json.language_line;
                document.getElementById("checkout-content-cast").innerHTML = "<strong>Cast:</strong> " + json.checkoutContent.cast;
                document.getElementById("checkout-content-duration").innerHTML = "<strong>Duration:</strong> " + formatDuration(json.checkoutContent.duration) + "/ep";
                document.getElementById("checkout-content-country-line").innerHTML = "<strong>Country:</strong> " + json.country_line;
                document.getElementById("checkout-content-episode-count").innerHTML = "<strong>Episodes:</strong> " + json.checkoutContent.episode_count;
                document.getElementById("checkout-content-production").innerHTML = "<strong>Production:</strong> " + json.checkoutContent.production;

                document.getElementById("checkout-content-order-summary-area").classList.remove("d-none");
                document.getElementById("checkout-content-sub-total").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.checkoutContent.price);

                document.getElementById("checkout-content-total-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.checkoutContent.price);

                if (json.checkoutContent.price > 0) {
                    document.getElementById("checkout-content-checkout-btn").disabled = false;
                } else {
                    document.getElementById("checkout-content-checkout-btn").disabled = true;
                }

            } else {
                window.location = "single_product_view.html?id=" + contentId;
            }
        } else {
            window.location = "single_product_view.html?id=" + contentId;
        }
    } else {
        window.location = "index.html";
    }

});

function formatDuration(minStr) {
    const totalMinutes = parseInt(minStr, 10);
    // Check for invalid or negative input
    if (isNaN(totalMinutes) || totalMinutes < 0) {
        return "N/A";
    }
    const hours = Math.floor(totalMinutes / 60);
    const mins = totalMinutes % 60;
    if (hours === 0) {
        return `${mins}m`;
    }
    return `${hours}h ${mins}m`;
}

async function checkout() {
    let checkOutData = {
        content_id: checkout_content_id
    };

    const checkOutDataJSON = JSON.stringify(checkOutData);

    const response = await fetch("CheckOut", {
        method: "POST",
        body: checkOutDataJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            //Payhere process
            payhere.startPayment(json.payhereJson);
        } else {
            swal({
                title: "Error message!",
                text: json.message,
                type: "error"
            });
        }
    } else {
        swal({
            title: "Error message!",
            text: "Something went wrong. Please try again later!",
            type: "error"
        });
    }
}

async function checkoutToDatabase(invoiceId) {
    let checkoutId = invoiceId.replace(/^#000/, "");
    let checkOutData = {
        content_id: checkout_content_id,
        checkoutId: checkoutId
    };

    const checkOutDataJSON = JSON.stringify(checkOutData);

    const response = await fetch("CheckOutToDatabase", {
        method: "POST",
        body: checkOutDataJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = "invoice.html?invoiceId=" + checkoutId;
        }
    }
}