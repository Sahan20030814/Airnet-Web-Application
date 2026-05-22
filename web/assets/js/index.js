window.addEventListener("load", async function () {
    checkSessionCart();
    checkSessionWatchlist();
    loadCarouselData();
    loadIndexData();
});

async function checkSessionCart() {
    const response = await fetch("CheckSessionCart");
    if (!response.ok) {
        swal({
            title: "Error message!",
            text: "Something went wrong with session cart!",
            type: "error",
            timer: 4000
        });
    }
}

async function checkSessionWatchlist() {
    const response = await fetch("CheckSessionWatchlist");
    if (!response.ok) {
        swal({
            title: "Error message!",
            text: "Something went wrong with session watchlist!",
            type: "error",
            timer: 4000
        });
    }
}

async function loadCarouselData() {
    const response = await fetch("LoadCarouselData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            let carousel = document.getElementById("carousel-content");
            let carousel_item = document.getElementById("carousel-content-item");

            carousel.innerHTML = "";
            let carousel_count = 0;

            json.trendingList.forEach(content => {

                let carousel_item_clone = carousel_item.cloneNode(true);
                carousel_item_clone.classList.remove("d-none");
                if (carousel_count == 0) {
                    carousel_item_clone.classList.add("active");
                    carousel_count = 1;
                }

                carousel_item_clone.style.backgroundImage = "url('product_images/" + content.id + "/background_image.jpg')";
                carousel_item_clone.querySelector("#carousel-content-item-title").innerHTML = content.name;

                if (content.movieType.name == "Movie") {
                    carousel_item_clone.querySelector("#carousel-content-item-description").innerHTML = "Don't miss out on this critically acclaimed movie. Watch now!";
                } else if (content.movieType.name == "Tv Show") {
                    carousel_item_clone.querySelector("#carousel-content-item-description").innerHTML = "Don't miss out on this critically acclaimed series. Watch now!";
                }

                carousel_item_clone.querySelector("#carousel-content-item-watch-now-btn").href = "single_product_view.html?id=" + content.id;
                carousel.appendChild(carousel_item_clone);
            });

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

}

async function loadIndexData() {
    const response = await fetch("LoadIndexData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

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

            loadContent("trending-movie", json.trendingMovieList, json.watchListItemList);
            loadContent("trending-tv-show", json.trendingTvShowList, json.watchListItemList);
            loadContent("latest-movie", json.latestMovieList, json.watchListItemList);
            loadContent("latest-tv-show", json.latestTvShowList, json.watchListItemList);

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
}

function loadContent(prefix, contentList, allWatchlist) {

    let container = document.getElementById(prefix + "-container");
    let card = document.getElementById(prefix + "-card");

    container.innerHTML = "";

    contentList.forEach(content => {

        let card_clone = card.cloneNode(true);

        card_clone.querySelector("#" + prefix + "-card-image-a").href = "single_product_view.html?id=" + content.id;
        card_clone.querySelector("#" + prefix + "-card-image").src = "product_images\\" + content.id + "\\card_image.jpg";

        let isWatchListItem = false;
        allWatchlist.forEach(item => {
            if (item.mainMovie.id === content.id) {
                isWatchListItem = true;
            }
        });

        if (isWatchListItem) {
            card_clone.querySelector("#" + prefix + "-watchlist-btn").style.color = "#FFD700";
            card_clone.querySelector("#" + prefix + "-watchlist-star").classList.remove("far");
            card_clone.querySelector("#" + prefix + "-watchlist-star").classList.add("fas");
            card_clone.querySelector("#" + prefix + "-watchlist-star").classList.add("active");
        }

        card_clone.querySelector("#" + prefix + "-watchlist-btn").addEventListener(
                "click", (e) => {
            addToWatchlist(content.id);
            e.preventDefault();
        });

        card_clone.querySelector("#" + prefix + "-title").innerHTML = content.name;
        card_clone.querySelector("#" + prefix + "-price").innerHTML = "Price: Rs. " + new Intl.NumberFormat(
                "en-US", {minimumFractionDigits: 2}).format(content.price);

        card_clone.querySelector("#" + prefix + "-details").innerHTML = "Release: " +
                new Intl.DateTimeFormat("en-US", {
                    year: "numeric"
                }).format(new Date(content.released_at)) + "\
         | Quality: " + content.qualityType.name + " | Duration: " + formatDuration(content.duration);

        card_clone.querySelector("#" + prefix + "-watch-now-btn").href = "single_product_view.html?id=" + content.id;
        card_clone.querySelector("#" + prefix + "-add-to-cart-btn").addEventListener(
                "click", (e) => {
            addToCart(content.id);
            e.preventDefault();
        });

        container.appendChild(card_clone);
    });
}

function goToSearch() {
    const search_bar_content = document.getElementById("search-bar-content").value;
    if (search_bar_content !== "") {
        window.location = "searched_contents.html?name=" + search_bar_content;
    }
}

async function addToWatchlist(contentId) {
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
                setTimeout(() => {
                    window.location.reload();
                }, 2000);
            } else if (json.statusId === "2") {  // remove from watchlist
                swal({
                    title: "Removed from watchlist message!",
                    text: json.message,
                    type: "success",
                    timer: 2000
                });
                setTimeout(() => {
                    window.location.reload();
                }, 2000);
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




