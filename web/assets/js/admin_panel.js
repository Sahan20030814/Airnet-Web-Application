window.addEventListener("load", async function () {
    loadAdminPanelDashBoardData();
    loadAdminPanelAllContentData();
    loadAdminPanelAllSellersData();
    loadAdminPanelAllAudienceData();
    loadAdminPanelPurchasingContentsData();
    loadAdminPanelAllInvoicesData();
});

async function loadAdminPanelDashBoardData() {

    const response = await fetch("LoadAdminPanelDashBoardData");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {

            // Admin Dashboard
            document.getElementById("all-contents-count").innerHTML = json.allContentCount;
            document.getElementById("current-month-contents-count").innerHTML = json.currentMonthContentCount;
            document.getElementById("all-sellers-count").innerHTML = json.allSellersCount;
            document.getElementById("current-month-sellers-count").innerHTML = json.currentMonthSellersCount;
            document.getElementById("all-users-count").innerHTML = json.allUsersCount;
            document.getElementById("current-month-users-count").innerHTML = json.currentMonthUsersCount;

            document.getElementById("all-invoices-count").innerHTML = json.allInvoicesCount;
            document.getElementById("all-total-income").innerHTML = "Total Income: Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.all_total_income);
            document.getElementById("all-sellers-income").innerHTML = "Sellers Income: Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.all_sellers_income);
            document.getElementById("all-site-profit").innerHTML = "Site Profit: Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.all_site_profit);

            document.getElementById("current-month-invoices-count").innerHTML = json.currentMonthInvoicesCount;
            document.getElementById("current-month-total-income").innerHTML = "Total Income: Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.current_month_total_income);
            document.getElementById("current-month-sellers-income").innerHTML = "Sellers Income: Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.current_month_sellers_income);
            document.getElementById("current-month-site-profit").innerHTML = "Site Profit: Rs. " + new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(json.current_month_site_profit);

            // Admin All Contents
            loadSelect("contentTypeSelector", json.movieTypeList);
            loadSellerSelect("contentSellersSelector", json.allSellersList);

            // Admin All Sellers
            loadContentSelect("allContentsSelector", json.allContentList);

            // Admin Audience
            loadContentSelect("allSellingContentsSelector", json.allContentList);

            // Admin Purchasing Contents
            loadUserSelect("specificUserSelector", json.allCheckoutUsersList);

            // Admin All Invoices
            loadUserSelect("allCheckoutUsersSelector", json.allCheckoutUsersList);
            loadContentSelect("allPurchasingContentsSelector", json.allContentList);

        } else {
            swal({
                title: "Error message!",
                text: "Dashboard Data Loading Failed. Please sign in again!",
                type: "error",
                timer: 4000
            });
            setTimeout(() => {
                window.location.reload();
            }, 4000);
        }

    } else {
        swal({
            title: "Error message!",
            text: "Dashboard Data Loading Failed!",
            type: "error",
            timer: 4000
        });
    }
}

function loadSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.name;
        select.appendChild(option);
    });
}

function loadSellerSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.email + " (" + item.first_name + " " + item.last_name + ")";
        select.appendChild(option);
    });
}

function loadContentSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.name + " (" + new Intl.DateTimeFormat("en-CA", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        }).format(new Date(item.released_at)) + " / " + item.movieType.name + " / " + new Intl.DateTimeFormat("en-CA", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        }).format(new Date(item.registered_at)) + " " + new Intl.DateTimeFormat("en-US", {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit",
            hour12: true
        }).format(new Date(item.registered_at)) + ")";
        select.appendChild(option);
    });
}

function loadUserSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.user.id;
        option.innerHTML = item.user.email + " (" + item.user.first_name + " " + item.user.last_name + ")";
        select.appendChild(option);
    });
}

async function loadAdminPanelAllContentData() {

    const contentName = document.getElementById("all-content-search-bar").value;
    const typeId = document.getElementById("contentTypeSelector").value;
    const sellerId = document.getElementById("contentSellersSelector").value;

    const response = await fetch("LoadAdminPanelAllContentData?contentName=" + contentName + "&typeId=" + typeId + "&sellerId=" + sellerId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.selectedAllContentListCount > 0) {
                document.getElementById("noSelectedContentMessage").classList.add("d-none");
                document.getElementById("selectedContentItemsTable").classList.remove("d-none");

                let table_body = document.getElementById("all-contents-table-body");
                let table_row = document.getElementById("all-contents-table-row");
                table_body.innerHTML = "";

                let count = 1;

                json.selectedAllContentList.forEach(content => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    table_row_clone.querySelector("#all-contents-item-number").innerHTML = count;
                    table_row_clone.querySelector("#all-contents-item-img-a").href = "admin_single_product_view.html?id=" + content.id;
                    table_row_clone.querySelector("#all-contents-item-img").src = "product_images\\" + content.id + "\\card_image.jpg";
                    table_row_clone.querySelector("#all-contents-item-name").innerHTML = content.name;
                    table_row_clone.querySelector("#all-contents-item-content-type").innerHTML = content.movieType.name;
                    table_row_clone.querySelector("#all-contents-item-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(content.price);

                    table_row_clone.querySelector("#all-contents-item-seller-email").innerHTML = content.user.email;
                    table_row_clone.querySelector("#all-contents-item-watch-now-btn").href = "admin_single_product_view.html?id=" + content.id;

                    if (content.status.id == "1") {
                        table_row_clone.querySelector("#all-contents-item-status-chage-btn").innerHTML = `<span class="badge rounded-pill text-bg-success">Active</span>`;
                    } else {
                        table_row_clone.querySelector("#all-contents-item-status-chage-btn").innerHTML = `<span class="badge rounded-pill text-bg-danger">Banned</span>`;
                    }

                    table_row_clone.querySelector("#all-contents-item-status-active-btn").addEventListener(
                            "click", (e) => {
                        contentStatusChange(content.id, 1);
                        e.preventDefault();
                    });

                    table_row_clone.querySelector("#all-contents-item-status-banned-btn").addEventListener(
                            "click", (e) => {
                        contentStatusChange(content.id, 2);
                        e.preventDefault();
                    });

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noSelectedContentMessage").classList.remove("d-none");
                document.getElementById("selectedContentItemsTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noSelectedContentMessage").classList.remove("d-none");
            document.getElementById("selectedContentItemsTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noSelectedContentMessage").classList.remove("d-none");
        document.getElementById("selectedContentItemsTable").classList.add("d-none");
    }
}

async function contentStatusChange(contentId, statusId) {
    const response = await fetch("AdminContentStatusChange?contentId=" + contentId + "&statusId=" + statusId);
    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.differentStatus) {
                swal({
                    title: "Success message!",
                    text: "Status updated successfully!",
                    type: "success",
                    timer: 3000
                });
                setTimeout(() => {
                    window.location.reload();
                }, 3000);
            }
        } else {
            swal({
                title: "Error message!",
                text: "Something went wrong!",
                type: "error",
                timer: 3000
            });
            setTimeout(() => {
                window.location.reload();
            }, 3000);
        }

    } else {
        swal({
            title: "Error message!",
            text: "Something went wrong!",
            type: "error",
            timer: 3000
        });
        setTimeout(() => {
            window.location.reload();
        }, 3000);
    }
}

async function loadAdminPanelAllSellersData() {

    const sellerEmail = document.getElementById("all-sellers-search-bar").value;
    const contentId = document.getElementById("allContentsSelector").value;

    const response = await fetch("LoadAdminPanelAllSellersData?sellerEmail=" + sellerEmail + "&contentId=" + contentId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.selectedAllSellersListCount > 0) {
                document.getElementById("noSelectedSellerMessage").classList.add("d-none");
                document.getElementById("selectedSellersListTable").classList.remove("d-none");

                let table_body = document.getElementById("all-sellers-table-body");
                let table_row = document.getElementById("all-sellers-table-row");
                table_body.innerHTML = "";

                let count = 1;

                json.selectedAllSellersList.forEach(seller => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    table_row_clone.querySelector("#all-sellers-person-number").innerHTML = count;
                    table_row_clone.querySelector("#all-sellers-person-name").innerHTML = seller.first_name + " " + seller.last_name;
                    table_row_clone.querySelector("#all-sellers-person-email").innerHTML = seller.email;
                    table_row_clone.querySelector("#all-sellers-person-registered-at").innerHTML = new Intl.DateTimeFormat("en-CA", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit"
                    }).format(new Date(seller.registered_at));

                    table_row_clone.querySelector("#all-sellers-person-status").innerHTML = seller.user_status.name;

                    table_row_clone.querySelector("#all-sellers-person-contents-btn").addEventListener(
                            "click", (e) => {
                        document.getElementById("all-content-search-bar").value = "";
                        document.getElementById("contentTypeSelector").value = "0";
                        const sellerId = document.getElementById("contentSellersSelector").value = seller.id;
                        loadAdminPanelAllContentData();
                        navigateToContent('all-contents-content', null, null);
                    });

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noSelectedSellerMessage").classList.remove("d-none");
                document.getElementById("selectedSellersListTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noSelectedSellerMessage").classList.remove("d-none");
            document.getElementById("selectedSellersListTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noSelectedSellerMessage").classList.remove("d-none");
        document.getElementById("selectedSellersListTable").classList.add("d-none");
    }
}

async function loadAdminPanelAllAudienceData() {

    const userEmail = document.getElementById("all-users-search-bar").value;
    const contentId = document.getElementById("allSellingContentsSelector").value;

    const response = await fetch("LoadAdminPanelAllUsersData?userEmail=" + userEmail + "&contentId=" + contentId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.selectedAllUsersListCount > 0) {
                document.getElementById("noSelectedUserMessage").classList.add("d-none");
                document.getElementById("selectedUsersListTable").classList.remove("d-none");

                let table_body = document.getElementById("all-users-table-body");
                let table_row = document.getElementById("all-users-table-row");
                table_body.innerHTML = "";

                let count = 1;

                json.selectedAllUsersList.forEach(user => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    table_row_clone.querySelector("#all-users-person-number").innerHTML = count;
                    table_row_clone.querySelector("#all-users-person-name").innerHTML = user.first_name + " " + user.last_name;
                    table_row_clone.querySelector("#all-users-person-email").innerHTML = user.email;
                    table_row_clone.querySelector("#all-users-person-registered-at").innerHTML = new Intl.DateTimeFormat("en-CA", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit"
                    }).format(new Date(user.registered_at));

                    table_row_clone.querySelector("#all-users-person-status").innerHTML = user.user_status.name;

                    table_row_clone.querySelector("#all-users-person-purchasing-items-btn").addEventListener(
                            "click", (e) => {
                        document.getElementById("specificUserSelector").value = user.id;
                        loadAdminPanelPurchasingContentsData();
                        navigateToContent('all-purchasing-items-content', null, null);
                    });

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noSelectedUserMessage").classList.remove("d-none");
                document.getElementById("selectedUsersListTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noSelectedUserMessage").classList.remove("d-none");
            document.getElementById("selectedUsersListTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noSelectedUserMessage").classList.remove("d-none");
        document.getElementById("selectedUsersListTable").classList.add("d-none");
    }
}

async function  loadAdminPanelPurchasingContentsData() {

    const userId = document.getElementById("specificUserSelector").value;

    const response = await fetch("LoadAdminPanelPurchasingItemsData?userId=" + userId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.specificPurchasingItemsListCount > 0) {
                document.getElementById("noPurchasingContentMessage").classList.add("d-none");
                document.getElementById("purchasingContentItemsTable").classList.remove("d-none");

                let table_body = document.getElementById("purchasing-contents-table-body");
                let table_row = document.getElementById("purchasing-contents-table-row");
                table_body.innerHTML = "";

                let count = 1;

                json.specificPurchasingItemsList.forEach(checkoutItem => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    table_row_clone.querySelector("#purchasing-contents-item-number").innerHTML = count;
                    table_row_clone.querySelector("#purchasing-contents-item-img-a").href = "admin_single_product_view.html?id=" + checkoutItem.mainMovie.id;
                    table_row_clone.querySelector("#purchasing-contents-item-img").src = "product_images\\" + checkoutItem.mainMovie.id + "\\card_image.jpg";
                    table_row_clone.querySelector("#purchasing-contents-item-name").innerHTML = checkoutItem.mainMovie.name;
                    table_row_clone.querySelector("#purchasing-contents-item-content-type").innerHTML = checkoutItem.mainMovie.movieType.name;
                    table_row_clone.querySelector("#purchasing-contents-item-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(checkoutItem.mainMovie.price);

                    table_row_clone.querySelector("#purchasing-contents-item-seller-email").innerHTML = checkoutItem.mainMovie.user.email;
                    table_row_clone.querySelector("#purchasing-contents-item-watch-now-btn").href = "admin_single_product_view.html?id=" + checkoutItem.mainMovie.id;

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noPurchasingContentMessage").classList.remove("d-none");
                document.getElementById("purchasingContentItemsTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noPurchasingContentMessage").classList.remove("d-none");
            document.getElementById("purchasingContentItemsTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noPurchasingContentMessage").classList.remove("d-none");
        document.getElementById("purchasingContentItemsTable").classList.add("d-none");
    }
}

async function  loadAdminPanelAllInvoicesData() {

    const dateFrom = document.getElementById("all-invoices-content-date-from").value;
    const dateTo = document.getElementById("all-invoices-content-date-to").value;
    const orderType = document.getElementById("all-invoices-content-order").value;
    const userId = document.getElementById("allCheckoutUsersSelector").value;
    const contentId = document.getElementById("allPurchasingContentsSelector").value;

    const response = await fetch("LoadAdminPanelAllInvoicesData?dateFrom=" + dateFrom + "&dateTo=" + dateTo + "&orderType=" + orderType + "&userId=" + userId + "&contentId=" + contentId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.selectedAllInvoicesListCount > 0) {
                document.getElementById("noInvoicesContentItemsMessage").classList.add("d-none");
                document.getElementById("invoicesContentItemsTable").classList.remove("d-none");

                let total_income = 0;
                let total_sellers_income = 0;
                let total_site_profit = 0;

                let table_body = document.getElementById("all-invoice-items-table-body");
                let table_row = document.getElementById("all-invoice-items-table-row");
                table_body.innerHTML = "";

                let row_id = "";
                let same_row = 0;

                json.selectedAllInvoicesList.forEach(checkoutItem => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    if (row_id == checkoutItem.checkout.id) {
                        table_row_clone.querySelector("#all-invoice-items-invoice-number").innerHTML = "";
                        table_row_clone.querySelector("#all-invoice-items-invoice-btn").classList.add("d-none");
                    } else {
                        table_row_clone.querySelector("#all-invoice-items-invoice-number").innerHTML = "#" + checkoutItem.checkout.id;
                        table_row_clone.querySelector("#all-invoice-items-invoice-btn").classList.remove("d-none");
                        row_id = checkoutItem.checkout.id;
                        same_row++;
                    }

                    if (same_row % 2 === 0) {
                        table_row_clone.classList.add("table-success");
                    } else {
                        table_row_clone.classList.add("table-secondary");
                    }

                    table_row_clone.querySelector("#all-invoice-items-invoice-date").innerHTML = new Intl.DateTimeFormat("en-CA", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit"
                    }).format(new Date(checkoutItem.registered_at));

                    table_row_clone.querySelector("#all-invoice-items-invoice-time").innerHTML = new Intl.DateTimeFormat("en-US", {
                        hour: "2-digit",
                        minute: "2-digit",
                        second: "2-digit",
                        hour12: true
                    }).format(new Date(checkoutItem.registered_at));

                    table_row_clone.querySelector("#all-invoice-items-email").innerHTML = checkoutItem.checkout.user.email;
                    table_row_clone.querySelector("#all-invoice-items-invoice-item-name").innerHTML = checkoutItem.mainMovie.name;
                    table_row_clone.querySelector("#all-invoice-items-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(checkoutItem.price);
                    table_row_clone.querySelector("#all-invoice-items-rate").innerHTML = checkoutItem.rate + "%";
                    table_row_clone.querySelector("#all-invoice-items-seller-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(checkoutItem.owner_price);
                    table_row_clone.querySelector("#all-invoice-items-site-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(checkoutItem.site_price);

                    table_row_clone.querySelector("#all-invoice-items-invoice-btn").href = "admin_invoice.html?invoiceId=" + checkoutItem.checkout.id;

                    table_body.appendChild(table_row_clone);

                    total_income += checkoutItem.price;
                    total_sellers_income += checkoutItem.owner_price;
                    total_site_profit += checkoutItem.site_price;
                });

                document.getElementById("all-invoice-items-total-income").innerHTML = "Total Income: Rs. " + new Intl.NumberFormat(
                        "en-US", {minimumFractionDigits: 2}).format(total_income);
                document.getElementById("all-invoice-items-seller-income").innerHTML = "Sellers Income: Rs. " + new Intl.NumberFormat(
                        "en-US", {minimumFractionDigits: 2}).format(total_sellers_income);
                document.getElementById("all-invoice-items-site-profit").innerHTML = "Site Profit: Rs. " + new Intl.NumberFormat(
                        "en-US", {minimumFractionDigits: 2}).format(total_site_profit);

            } else {
                document.getElementById("all-invoice-items-total-income").innerHTML = "Total Income: Rs. 0.00";
                document.getElementById("all-invoice-items-seller-income").innerHTML = "Sellers Income: Rs. 0.00";
                document.getElementById("all-invoice-items-site-profit").innerHTML = "Site Profit: Rs. 0.00";
                document.getElementById("noInvoicesContentItemsMessage").classList.remove("d-none");
                document.getElementById("invoicesContentItemsTable").classList.add("d-none");
            }

        } else {
            document.getElementById("all-invoice-items-total-income").innerHTML = "Total Income: Rs. 0.00";
            document.getElementById("all-invoice-items-seller-income").innerHTML = "Sellers Income: Rs. 0.00";
            document.getElementById("all-invoice-items-site-profit").innerHTML = "Site Profit: Rs. 0.00";
            document.getElementById("noInvoicesContentItemsMessage").classList.remove("d-none");
            document.getElementById("invoicesContentItemsTable").classList.add("d-none");
        }

    } else {
        document.getElementById("all-invoice-items-total-income").innerHTML = "Total Income: Rs. 0.00";
        document.getElementById("all-invoice-items-seller-income").innerHTML = "Sellers Income: Rs. 0.00";
        document.getElementById("all-invoice-items-site-profit").innerHTML = "Site Profit: Rs. 0.00";
        document.getElementById("noInvoicesContentItemsMessage").classList.remove("d-none");
        document.getElementById("invoicesContentItemsTable").classList.add("d-none");
    }
}