// Payment completed. It can be a successful failure.
payhere.onCompleted = async function onCompleted(orderId) {
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

let cart_items_list;

async function loadCartItems() {
    const response = await fetch("LoadCartItems");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            if (json.cartItemsCount > 0) {
                cart_items_list = json.cartItemsList;
                document.getElementById("select-all-cart-items-div").classList.remove('d-none');
                document.getElementById("emptyCartViewingArea").classList.add('d-none');

                let cart_item_container = document.getElementById("cart-items-container");
                let cart_item_card = document.getElementById("cart-item-card");
                cart_item_container.innerHTML = "";

                json.cartItemsList.forEach(cart => {
                    let cart_item_card_clone = cart_item_card.cloneNode(true);

                    cart_item_card_clone.classList.remove('d-none');

                    cart_item_card_clone.querySelector("input").id = "cart-item-" + cart.mainMovie.id;
                    cart_item_card_clone.querySelector("input").value = cart.mainMovie.id;
                    cart_item_card_clone.querySelector("input").addEventListener(
                            "change", (e) => {
                        calculateTotal();
                        e.preventDefault();
                    });

                    cart_item_card_clone.querySelector("#cart-item-img-a").href = "single_product_view.html?id=" + cart.mainMovie.id;
                    cart_item_card_clone.querySelector("#cart-item-content-image").src = "product_images\\" + cart.mainMovie.id + "\\card_image.jpg";
                    cart_item_card_clone.querySelector("#cart-item-content-name").innerHTML = cart.mainMovie.name;
                    cart_item_card_clone.querySelector("#cart-item-content-type").innerHTML = cart.mainMovie.movieType.name;
                    cart_item_card_clone.querySelector("#cart-item-content-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                            "en-US",
                            {minimumFractionDigits: 2})
                            .format(cart.mainMovie.price);

                    cart_item_card_clone.querySelector("#cart-item-content-delete-btn").addEventListener(
                            "click", (e) => {
                        deleteSingleCartItem(cart.mainMovie.id);
                        e.preventDefault();
                    });

                    cart_item_container.appendChild(cart_item_card_clone);
                });

                calculateTotal();
            } else {
                emptyCartView();
            }
        } else {
            emptyCartView();
        }
    } else {
        emptyCartView();
    }
}

function calculateTotal() {
    let count = 0;
    let total = 0;
    cart_items_list.forEach(cartItem => {
        if (document.getElementById("cart-item-" + cartItem.mainMovie.id).checked) {
            count++;
            total += cartItem.mainMovie.price;
        } else {
            document.getElementById("select-all-cart-items-check-box").checked = false;
        }
    });

    document.getElementById("selected-cart-items-count").innerHTML = count;
    document.getElementById("selected-cart-items-total-price").innerHTML = "Rs. " + new Intl.NumberFormat(
            "en-US",
            {minimumFractionDigits: 2})
            .format(total);

    if (count > 0 && total > 0) {
        document.getElementById("cart-checkout-btn").disabled = false;
    } else {
        document.getElementById("cart-checkout-btn").disabled = true;
    }
}

function emptyCartView() {
    document.getElementById("emptyCartViewingArea").classList.remove('d-none');
    document.getElementById("select-all-cart-items-div").classList.add('d-none');
    document.getElementById("cart-items-container").innerHTML = "";
    document.getElementById("selected-cart-items-count").innerHTML = "0";
    document.getElementById("selected-cart-items-total-price").innerHTML = "Rs. 0.00";
    document.getElementById("cart-checkout-btn").disabled = true;
}

function selectAllCartItems() {
    if (cart_items_list != null) {
        let isChecked = false;
        if (document.getElementById("select-all-cart-items-check-box").checked) {
            isChecked = true;
        }
        cart_items_list.forEach(cartItem => {
            document.getElementById("cart-item-" + cartItem.mainMovie.id).checked = isChecked;
        });
        calculateTotal();
    }
}

async function deleteSingleCartItem(contentId) {
    const response = await fetch("DeleteSingleCartItem?contentId=" + contentId);
    if (response.ok) {
        const json = await response.json();
        window.location.reload();
        loadCartItems();
    } else {
        window.location.reload();
        loadCartItems();
    }
}

async function deleteAllCartItems() {
    swal({
        title: "Are you sure?",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Yes, delete all!",
        closeOnConfirm: false
    },
            async function () {
                const response = await fetch("DeleteAllCartItems");
                if (response.ok) {
                    const json = await response.json();
                    window.location.reload();
                    loadCartItems();
                } else {
                    window.location.reload();
                    loadCartItems();
                }
            });
}

async function checkout() {
    let checkout_content_ids = "";
    cart_items_list.forEach(cartItem => {
        if (document.getElementById("cart-item-" + cartItem.mainMovie.id).checked) {
            checkout_content_ids += cartItem.mainMovie.id + ",";
        }
    });
    checkout_content_ids = checkout_content_ids.replace(/,\s*$/, "");

    let checkOutData = {
        content_ids: checkout_content_ids
    };

    const checkOutDataJSON = JSON.stringify(checkOutData);

    const response = await fetch("CartCheckOut", {
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
    let checkout_content_ids = "";
    cart_items_list.forEach(cartItem => {
        if (document.getElementById("cart-item-" + cartItem.mainMovie.id).checked) {
            checkout_content_ids += cartItem.mainMovie.id + ",";
        }
    });
    checkout_content_ids = checkout_content_ids.replace(/,\s*$/, "");

    let checkOutData = {
        content_ids: checkout_content_ids,
        checkoutId: checkoutId
    };

    const checkOutDataJSON = JSON.stringify(checkOutData);

    const response = await fetch("CartCheckOutToDatabase", {
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


