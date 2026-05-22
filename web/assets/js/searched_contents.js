var genres_list;
var countries_list;
var languages_list;

window.addEventListener("load", async function () {

    let urlServlet = "LoadSearchContentData?";
    let searchParams = new URLSearchParams(window.location.search);

    const url = new URL(window.location);
    if (searchParams.has("firstResult")) {
        if (!isNonNegativeInteger(searchParams.get("firstResult"))) {
            url.searchParams.set('firstResult', '0');
            window.history.pushState({}, '', url);
        }
        urlServlet += "firstResult=" + searchParams.get("firstResult");
    } else {
        url.searchParams.set('firstResult', '0');
        window.history.pushState({}, '', url);
        searchParams = new URLSearchParams(window.location.search);
        urlServlet += "firstResult=0";
    }

    if (searchParams.has("contentType")) {
        urlServlet += "&contentType=" + searchParams.get("contentType");

        if (searchParams.has("quality")) {
            urlServlet += "&quality=" + searchParams.get("quality");
        }

        if (searchParams.has("releasedYear")) {
            urlServlet += "&releasedYear=" + searchParams.get("releasedYear");
        }

        if (searchParams.has("minPrice")) {
            urlServlet += "&minPrice=" + searchParams.get("minPrice");
        }

        if (searchParams.has("maxPrice")) {
            urlServlet += "&maxPrice=" + searchParams.get("maxPrice");
        }

        const genreIds = searchParams.getAll("genreId");
        const countryIds = searchParams.getAll("countryId");
        const languageIds = searchParams.getAll("languageId");

        genreIds.forEach(genreId => {
            urlServlet += "&genreId=" + genreId;
        });

        countryIds.forEach(countryId => {
            urlServlet += "&countryId=" + countryId;
        });

        languageIds.forEach(languageId => {
            urlServlet += "&languageId=" + languageId;
        });

    } else {

        if (searchParams.has("name")) {
            urlServlet += "&name=" + searchParams.get("name");
        }

        if (searchParams.has("content")) {
            urlServlet += "&content=" + searchParams.get("content");
        }

        if (searchParams.has("genreId")) {
            urlServlet += "&genreId=" + searchParams.get("genreId");
        }

        if (searchParams.has("countryId")) {
            urlServlet += "&countryId=" + searchParams.get("countryId");
        }

    }

    const response = await fetch(urlServlet);
    if (response.ok) {

        const json = await response.json();
        if (json.status) {

            genres_list = json.genreList;
            countries_list = json.countryList;
            languages_list = json.languageList;

            // load genres
            let genre_list = document.getElementById("genre-list");
            let genre_item = document.getElementById("genre-item");
            genre_list.innerHTML = "";
            json.genreList.forEach(genre => {
                let genre_item_clone = genre_item.cloneNode(true);
                genre_item_clone.innerHTML = genre.name;
                genre_item_clone.href = "searched_contents.html?genreId=" + genre.id;
                genre_list.appendChild(genre_item_clone);
            });
            // load genres

            // load countries
            let country_list = document.getElementById("country-list");
            let country_item = document.getElementById("country-item");
            country_list.innerHTML = "";
            json.countryList.forEach(country => {

                let country_item_clone = country_item.cloneNode(true);
                country_item_clone.innerHTML = country.name;
                country_item_clone.href = "searched_contents.html?countryId=" + country.id;
                country_list.appendChild(country_item_clone);
            });
            // load countries

            loadSelect("filterType", json.movieTypeList);
            loadSelect("filterQuality", json.qualityList);
            loadSelect("filterReleasedYear", json.yearList);
            loadCheckBox("genre", json.genreList);
            loadCheckBox("country", json.countryList);
            loadCheckBox("language", json.languageList);

            setCheckData();
            updateContentView(json);


        } else {
            swal({
                title: "Error message!",
                text: "Content loading failed!",
                type: "error",
                timer: 5000
            });
        }

    } else {
        swal({
            title: "Error message!",
            text: "Content loading failed!",
            type: "error",
            timer: 5000
        });
    }

});

function loadSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        if (selectId == "filterReleasedYear") {
            option.value = item;
            option.innerHTML = item;
        } else {
            option.value = item.id;
            option.innerHTML = item.name;
        }
        select.appendChild(option);
    });
}

function loadCheckBox(prefix, dataList) {
    let item_list = document.getElementById(prefix + "-item-list");
    let item_content = document.getElementById(prefix + "-item-content");

    item_list.innerHTML = "";

    dataList.forEach(item => {
        let item_content_clone = item_content.cloneNode(true);
        item_content_clone.querySelector("input").id = prefix + "-" + item.id;
        item_content_clone.querySelector("input").value = item.id;
        item_content_clone.querySelector("#" + prefix + "-item-text").innerHTML = item.name;
        item_list.appendChild(item_content_clone);
    });
}

function setFilterURL() {

    document.getElementById("search-bar-content").value = "";

    let url = "searched_contents.html?";

    url += "contentType=" + document.getElementById("filterType").value
            + "&quality=" + document.getElementById("filterQuality").value
            + "&releasedYear=" + document.getElementById("filterReleasedYear").value
            + "&minPrice=" + document.getElementById("minPriceRange").value
            + "&maxPrice=" + document.getElementById("maxPriceRange").value;

    if (genres_list !== null) {
        genres_list.forEach(genre => {
            if (document.getElementById("genre-" + genre.id).checked) {
                url += "&genreId=" + document.getElementById("genre-" + genre.id).value;
            }
        });
    }

    if (countries_list !== null) {
        countries_list.forEach(country => {
            if (document.getElementById("country-" + country.id).checked) {
                url += "&countryId=" + document.getElementById("country-" + country.id).value;
            }
        });
    }

    if (languages_list !== null) {
        languages_list.forEach(language => {
            if (document.getElementById("language-" + language.id).checked) {
                url += "&languageId=" + document.getElementById("language-" + language.id).value;
            }
        });
    }

    window.location = url;
}

function setCheckData() {

    const params = new URLSearchParams(window.location.search);
    const searchTitle = params.get("name");
    const typeId = params.get("contentType");
    const qualityId = params.get("quality");
    const releasedId = params.get("releasedYear");
    const genreIds = params.getAll("genreId");
    const countryIds = params.getAll("countryId");
    const languageIds = params.getAll("languageId");
    const minPrice = params.get("minPrice");
    const maxPrice = params.get("maxPrice");

    if (searchTitle != null) {
        document.getElementById("search-bar-content").value = searchTitle;
    }

    if (typeId != null) {
        document.getElementById("filterType").value = typeId;
    }

    if (qualityId != null) {
        document.getElementById("filterQuality").value = qualityId;
    }

    if (qualityId != null) {
        document.getElementById("filterReleasedYear").value = releasedId;
    }

    genreIds.forEach(genreId => {
        document.getElementById("genre-" + genreId).checked = true;
    });

    countryIds.forEach(countryId => {
        document.getElementById("country-" + countryId).checked = true;
    });

    languageIds.forEach(languageId => {
        document.getElementById("language-" + languageId).checked = true;
    });

    if (minPrice != null) {
        if (minPrice >= 0 && minPrice <= 2000) {
            document.getElementById("minPriceRange").value = minPrice;
            document.getElementById("minPriceDisplay").innerHTML = "Rs." + minPrice;
        }
    }

    if (maxPrice != null) {
        if (maxPrice >= 0 && maxPrice <= 2000) {
            document.getElementById("maxPriceRange").value = maxPrice;
            document.getElementById("maxPriceDisplay").innerHTML = "Rs." + maxPrice;
        }
    }
}

function resetFilter() {
    window.location = "searched_contents.html";
}

function goToSearch() {
    const search_bar_content = document.getElementById("search-bar-content").value;
    if (search_bar_content !== "") {
        window.location = "searched_contents.html?name=" + search_bar_content;
    }
}

async function searchContents(firstResult) {

    const url = new URL(window.location);
    if (!isNonNegativeInteger(firstResult)) {
        url.searchParams.set('firstResult', '0');
        firstResult = 0;
    } else {
        url.searchParams.set('firstResult', firstResult);
    }
    window.history.pushState({}, '', url);

    let urlServlet = "SearchContentData?firstResult=" + firstResult;

    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("contentType")) {
        urlServlet += "&contentType=" + searchParams.get("contentType");

        if (searchParams.has("quality")) {
            urlServlet += "&quality=" + searchParams.get("quality");
        }

        if (searchParams.has("releasedYear")) {
            urlServlet += "&releasedYear=" + searchParams.get("releasedYear");
        }

        if (searchParams.has("minPrice")) {
            urlServlet += "&minPrice=" + searchParams.get("minPrice");
        }

        if (searchParams.has("maxPrice")) {
            urlServlet += "&maxPrice=" + searchParams.get("maxPrice");
        }

        const genreIds = searchParams.getAll("genreId");
        const countryIds = searchParams.getAll("countryId");
        const languageIds = searchParams.getAll("languageId");

        genreIds.forEach(genreId => {
            urlServlet += "&genreId=" + genreId;
        });

        countryIds.forEach(countryId => {
            urlServlet += "&countryId=" + countryId;
        });

        languageIds.forEach(languageId => {
            urlServlet += "&languageId=" + languageId;
        });

    } else {

        if (searchParams.has("name")) {
            urlServlet += "&name=" + searchParams.get("name");
        }

        if (searchParams.has("content")) {
            urlServlet += "&content=" + searchParams.get("content");
        }

        if (searchParams.has("genreId")) {
            urlServlet += "&genreId=" + searchParams.get("genreId");
        }

        if (searchParams.has("countryId")) {
            urlServlet += "&countryId=" + searchParams.get("countryId");
        }

    }

    const response = await fetch(urlServlet);
    if (response.ok) {

        const json = await response.json();
        if (json.status) {
            updateContentView(json);
        } else {
            swal({
                title: "Error message!",
                text: "Something went wrong. Please try again later!",
                type: "error",
                timer: 5000
            });
        }

    } else {
        swal({
            title: "Error message!",
            text: "Something went wrong. Please try again later!",
            type: "error",
            timer: 5000
        });
    }
}

const card = document.getElementById("content-card");  // content card parent node
let sc_pagination_button = document.getElementById("sc-pagination-button");
let sc_bottom_pagination_button = document.getElementById("sc-bottom-pagination-button");
let current_page;
if (new URLSearchParams(window.location.search).has("firstResult") && isNonNegativeInteger(new URLSearchParams(window.location.search).get("firstResult"))) {
    current_page = (new URLSearchParams(window.location.search).get("firstResult") / 16);
} else {
    current_page = 0;
}

function updateContentView(json) {
    document.getElementById("searched-title").innerHTML = json.searchedTitle;

    let card_container = document.getElementById("content-card-container");

    card_container.innerHTML = "";

    json.finalMovieList.forEach(content => {

        let card_clone = card.cloneNode(true);

        card_clone.classList.remove("d-none");
        card_clone.querySelector("#content-card-img-a").href = "single_product_view.html?id=" + content.id;
        card_clone.querySelector("#content-card-img").src = "product_images\\" + content.id + "\\card_image.jpg";

        let isWatchListItem = false;
        json.watchListItemList.forEach(item => {
            if (item.mainMovie.id === content.id) {
                isWatchListItem = true;
            }
        });

        if (isWatchListItem) {
            card_clone.querySelector("#content-card-watchlist-btn").style.color = "#FFD700";
            card_clone.querySelector("#content-card-watchlist-star").classList.remove("far");
            card_clone.querySelector("#content-card-watchlist-star").classList.add("fas");
            card_clone.querySelector("#content-card-watchlist-star").classList.add("active");
        }

        card_clone.querySelector("#content-card-watchlist-btn").addEventListener(
                "click", (e) => {
            addToWatchlist(card_clone, content.id);
            e.preventDefault();
        });

        card_clone.querySelector("#content-card-title").innerHTML = content.name;
        card_clone.querySelector("#content-card-price").innerHTML = "Price: Rs. " + new Intl.NumberFormat(
                "en-US", {minimumFractionDigits: 2}).format(content.price);

        card_clone.querySelector("#content-card-released-date").innerHTML = "Release: " + new Intl.DateTimeFormat("en-CA", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        }).format(new Date(content.released_at));

        card_clone.querySelector("#content-card-quality").innerHTML = "Quality: " + content.qualityType.name;

        if (content.movieType.name == "Tv Show") {
            card_clone.querySelector("#content-card-duration").innerHTML = "Duration: " + formatDuration(content.duration) + " / EP " + content.episode_count;
        } else {
            card_clone.querySelector("#content-card-duration").innerHTML = "Duration: " + formatDuration(content.duration);
        }

        card_clone.querySelector("#content-card-buy-now-btn").innerHTML = "Watch Now";
        card_clone.querySelector("#content-card-buy-now-btn").href = "single_product_view.html?id=" + content.id;

        card_clone.querySelector("#content-card-add-to-cart-btn").innerHTML = "Add To Cart";
        card_clone.querySelector("#content-card-add-to-cart-btn").addEventListener(
                "click", (e) => {
            addToCart(content.id);
            e.preventDefault();
        });

        card_container.appendChild(card_clone);
    });

    if (json.allContentCount > 0) {
        // Top pagination
        let sc_pagination_container = document.getElementById("sc-pagination-container");
        sc_pagination_container.innerHTML = "";
        let all_content_count = json.allContentCount;
        let content_per_page = 16;
        let pages = Math.ceil(all_content_count / content_per_page);

        // previous button
        let sc_pagination_button_prev_clone = sc_pagination_button.cloneNode(true);
        if (current_page === 0) {
            sc_pagination_button_prev_clone.classList.add("disabled");
        } else {
            sc_pagination_button_prev_clone.classList.remove("disabled");
            sc_pagination_button_prev_clone.addEventListener(
                    "click", (e) => {
                current_page--;
                searchContents(current_page * content_per_page);
                e.preventDefault();
            });
        }
        sc_pagination_button_prev_clone.classList.remove("d-none");
        sc_pagination_button_prev_clone.querySelector("#sc-pagination-a").innerHTML = "Previous";

        sc_pagination_container.appendChild(sc_pagination_button_prev_clone);

        // pagination-button
        for (let i = 0; i < pages; i++) {
            let sc_pagination_button_clone = sc_pagination_button.cloneNode(true);
            sc_pagination_button_clone.classList.remove("d-none");
            sc_pagination_button_clone.querySelector("#sc-pagination-a").innerHTML = i + 1;
            sc_pagination_button_clone.addEventListener(
                    "click", (e) => {
                current_page = i;
                searchContents(i * content_per_page);
                e.preventDefault();
            });

            if (i === Number(current_page)) {
                sc_pagination_button_clone.classList.add("active");
            } else {
                sc_pagination_button_clone.classList.remove("active");
            }

            sc_pagination_container.appendChild(sc_pagination_button_clone);
        }

        // next button
        let sc_pagination_button_next_clone = sc_pagination_button.cloneNode(true);
        if (current_page !== (pages - 1)) {
            sc_pagination_button_next_clone.classList.remove("disabled");
            sc_pagination_button_next_clone.addEventListener(
                    "click", (e) => {
                current_page++;
                searchContents(current_page * content_per_page);
                e.preventDefault();
            });
        } else {
            sc_pagination_button_next_clone.classList.add("disabled");
        }
        sc_pagination_button_next_clone.classList.remove("d-none");
        sc_pagination_button_next_clone.querySelector("#sc-pagination-a").innerHTML = "Next";

        sc_pagination_container.appendChild(sc_pagination_button_next_clone);
        // Top pagination

        // Bottom pagination
        let sc_bottom_pagination_container = document.getElementById("sc-bottom-pagination-container");
        sc_bottom_pagination_container.innerHTML = "";

        // previous button
        let sc_bottom_pagination_button_prev_clone = sc_bottom_pagination_button.cloneNode(true);
        if (current_page === 0) {
            sc_bottom_pagination_button_prev_clone.classList.add("disabled");
        } else {
            sc_bottom_pagination_button_prev_clone.classList.remove("disabled");
            sc_bottom_pagination_button_prev_clone.addEventListener(
                    "click", (e) => {
                current_page--;
                searchContents(current_page * content_per_page);
                e.preventDefault();
            });
        }
        sc_bottom_pagination_button_prev_clone.classList.remove("d-none");
        sc_bottom_pagination_button_prev_clone.querySelector("#sc-bottom-pagination-a").innerHTML = "Previous";

        sc_bottom_pagination_container.appendChild(sc_bottom_pagination_button_prev_clone);

        // pagination-button
        for (let i = 0; i < pages; i++) {
            let sc_bottom_pagination_button_clone = sc_bottom_pagination_button.cloneNode(true);
            sc_bottom_pagination_button_clone.classList.remove("d-none");
            sc_bottom_pagination_button_clone.querySelector("#sc-bottom-pagination-a").innerHTML = i + 1;
            sc_bottom_pagination_button_clone.addEventListener(
                    "click", (e) => {
                current_page = i;
                searchContents(i * content_per_page);
                e.preventDefault();
            });

            if (i === Number(current_page)) {
                sc_bottom_pagination_button_clone.classList.add("active");
            } else {
                sc_bottom_pagination_button_clone.classList.remove("active");
            }

            sc_bottom_pagination_container.appendChild(sc_bottom_pagination_button_clone);
        }

        // next button
        let sc_bottom_pagination_button_next_clone = sc_bottom_pagination_button.cloneNode(true);
        if (current_page !== (pages - 1)) {
            sc_bottom_pagination_button_next_clone.classList.remove("disabled");
            sc_bottom_pagination_button_next_clone.addEventListener(
                    "click", (e) => {
                current_page++;
                searchContents(current_page * content_per_page);
                e.preventDefault();
            });
        } else {
            sc_bottom_pagination_button_next_clone.classList.add("disabled");
        }
        sc_bottom_pagination_button_next_clone.classList.remove("d-none");
        sc_bottom_pagination_button_next_clone.querySelector("#sc-bottom-pagination-a").innerHTML = "Next";

        sc_bottom_pagination_container.appendChild(sc_bottom_pagination_button_next_clone);
        // Bottom pagination

    }
}

async function addToWatchlist(cardClone, contentId) {
    const btn = cardClone.querySelector("#content-card-watchlist-btn");
    const star = cardClone.querySelector("#content-card-watchlist-star");

    const response = await fetch("AddToWatchlist?contentId=" + contentId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.statusId === "1") {      // add to watchlist
                swal({
                    title: "Added to watchlist message!",
                    text: json.message,
                    type: "success",
                    timer: 2000
                });
                btn.style.color = "#FFD700";
                star.classList.remove("far");
                star.classList.add("fas");
                star.classList.add("active");
            } else if (json.statusId === "2") {  // remove from watchlist
                swal({
                    title: "Removed from watchlist message!",
                    text: json.message,
                    type: "success",
                    timer: 2000
                });
                btn.style.color = "#ffffff";
                star.classList.remove("fas");
                star.classList.add("far");
                star.classList.remove("active");
            }
        } else {
            swal({
                title: "Error message!",
                text: json.message,
                type: "error",
                timer: 4000
            });
        }

    } else {
        swal({
            title: "Error message!",
            text: "Something went wrong. Please try again later!",
            type: "error",
            timer: 4000
        });
    }
}

async function addToCart(contentId) {
    const response = await fetch("AddToCart?contentId=" + contentId);

    if (response.ok) {
        const json = await response.json();
        if (json.status === "3") {
            swal({
                title: "Success message!",
                text: json.message,
                type: "success",
                timer: 2000
            });
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else if (json.status === "2") {
            swal({
                title: "Message!",
                text: json.message,
                type: "info",
                timer: 3000
            });
        } else {
            swal({
                title: "Error message!",
                text: json.message,
                type: "error",
                timer: 3000
            });
        }
    } else {
        swal({
            title: "Error message!",
            text: "Something went wrong. Please try again later!",
            type: "error",
            timer: 3000
        });
    }
}

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

function isNonNegativeInteger(value) {
    return Number.isInteger(value) && value >= 0;
}