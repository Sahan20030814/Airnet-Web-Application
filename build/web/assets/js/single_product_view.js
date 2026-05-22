function goToSearch() {
    const search_bar_content = document.getElementById("search-bar-content").value;
    if (search_bar_content !== "") {
        window.location = "searched_contents.html?name=" + search_bar_content;
    }
}

window.addEventListener("load", async function () {
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("id")) {
        const contentId = searchParams.get("id");
        const response = await fetch("LoadSingleProduct?id=" + contentId);

        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                document.getElementById("content-type-name").innerHTML = json.mainContent.movieType.name;

                if (json.mainContent.movieType.id == 1) {
                    document.getElementById("content-type-name").href = "searched_contents.html?content=latestMovies";
                } else if (json.mainContent.movieType.id == 2) {
                    document.getElementById("content-type-name").href = "searched_contents.html?content=latestTvShows";
                }

                document.getElementById("main-card-container").classList.remove("d-none");

                document.getElementById("content-title").innerHTML = json.mainContent.name;
                document.getElementById("main-content-background-image").src = "product_images\\" + json.mainContent.id + "\\background_image.jpg";
                document.getElementById("content-vote-count").innerHTML = json.ratingCount + " Votes";

                if (json.contentRatingType == 1) {
                    document.getElementById("content-like-btn").className = "btn active-like-btn-icon like-btn";
                    document.getElementById("content-like-icon").classList.remove("far");
                    document.getElementById("content-like-icon").classList.add("fas");

                    document.getElementById("content-dislike-btn").className = "btn dislike-btn-icon dislike-btn";
                    document.getElementById("content-dislike-icon").classList.remove("fas");
                    document.getElementById("content-dislike-icon").classList.add("far");
                } else if (json.contentRatingType == 2) {
                    document.getElementById("content-dislike-btn").className = "btn active-dislike-btn-icon dislike-btn";
                    document.getElementById("content-dislike-icon").classList.remove("far");
                    document.getElementById("content-dislike-icon").classList.add("fas");

                    document.getElementById("content-like-btn").className = "btn like-btn-icon like-btn";
                    document.getElementById("content-like-icon").classList.remove("fas");
                    document.getElementById("content-like-icon").classList.add("far");
                }

                if (json.contentRatingStatus) {
                    document.getElementById("content-like-btn").disabled = false;
                    document.getElementById("content-dislike-btn").disabled = false;
                } else {
                    document.getElementById("content-like-btn").disabled = true;
                    document.getElementById("content-dislike-btn").disabled = true;
                }

                document.getElementById("content-like-btn").addEventListener(
                        "click", (e) => {
                    addRate(json.mainContent.id, 1);
                    e.preventDefault();
                });

                document.getElementById("content-dislike-btn").addEventListener(
                        "click", (e) => {
                    addRate(json.mainContent.id, 2);
                    e.preventDefault();
                });

                document.getElementById("main-content-card-image").src = "product_images\\" + json.mainContent.id + "\\card_image.jpg";

                if (json.watchlistStatus) {
                    document.getElementById("card-watchlist-btn").style.color = "#FFD700";
                    document.getElementById("card-watchlist-star").classList.remove("far");
                    document.getElementById("card-watchlist-star").classList.add("fas");
                    document.getElementById("card-watchlist-star").classList.add("active");
                }

                document.getElementById("card-watchlist-btn").addEventListener(
                        "click", (e) => {
                    mainAddToWatchlist(json.mainContent.id);
                    e.preventDefault();
                });

                document.getElementById("main-content-title").innerHTML = json.mainContent.name;
                document.getElementById("main-content-quality").innerHTML = json.mainContent.qualityType.name;
                document.getElementById("main-content-rating").innerHTML = "<i class='fas fa-star text-warning'></i> " + json.mainContent.rating + "/10 IMDB";

                document.getElementById("main-content-trailer-btn").addEventListener(
                        "click", (e) => {
                    openVideoModal(json.mainContent.trailer);
                    e.preventDefault();
                });

                document.getElementById("main-content-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.mainContent.price);

                document.getElementById("main-content-description").innerHTML = json.mainContent.description;
                document.getElementById("main-content-released-at").innerHTML = "<strong>Released:</strong> " + new Intl.DateTimeFormat("en-CA", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit"
                }).format(new Date(json.mainContent.released_at));

                document.getElementById("main-content-genre").innerHTML = "<strong>Genre:</strong> " + json.genre_line;
                document.getElementById("main-content-language").innerHTML = "<strong>Language:</strong> " + json.language_line;
                document.getElementById("main-content-cast").innerHTML = "<strong>Cast:</strong> " + json.mainContent.cast;
                document.getElementById("main-content-duration").innerHTML = "<strong>Duration:</strong> " + formatDuration(json.mainContent.duration);
                document.getElementById("main-content-country").innerHTML = "<strong>Country:</strong> " + json.country_line;
                document.getElementById("main-content-episode-count").innerHTML = "<strong>Episodes:</strong> " + json.mainContent.episode_count;
                document.getElementById("main-content-production").innerHTML = "<strong>Production:</strong> " + json.mainContent.production;

                if (json.contentCheckoutStatus) {
                    document.getElementById("main-content-buy-now-btn").classList.add("d-none");
                    document.getElementById("main-content-add-to-cart-btn").classList.add("d-none");
                    document.getElementById("main-content-watch-now-btn").classList.remove("d-none");
                } else {
                    document.getElementById("main-content-watch-now-btn").classList.add("d-none");
                    document.getElementById("main-content-buy-now-btn").classList.remove("d-none");
                    document.getElementById("main-content-add-to-cart-btn").classList.remove("d-none");
                }

                document.getElementById("main-content-buy-now-btn").href = "checkout.html?id=" + json.mainContent.id;
                document.getElementById("main-content-watch-now-btn").href = "buying_content_view.html?contentId=" + json.mainContent.id;

                document.getElementById("main-content-add-to-cart-btn").addEventListener(
                        "click", (e) => {
                    addToCart(json.mainContent.id);
                    e.preventDefault();
                });

                let similar_card_container = document.getElementById("similar-card-container");
                let similar_card = document.getElementById("similar-card");

                similar_card_container.innerHTML = "";

                json.similarFinalList.forEach(content => {
                    let similar_card_clone = similar_card.cloneNode(true);

                    similar_card_clone.classList.remove("d-none");

                    similar_card_clone.querySelector("#similar-card-image-a").href = "single_product_view.html?id=" + content.id;
                    similar_card_clone.querySelector("#similar-card-image").src = "product_images\\" + content.id + "\\card_image.jpg";

                    let isWatchListItem = false;

                    json.allWatchlistData.forEach(item => {
                        if (item.mainMovie.id === content.id) {
                            isWatchListItem = true;
                        }
                    });

                    if (isWatchListItem) {
                        similar_card_clone.querySelector("#similar-card-watchlist-btn").style.color = "#FFD700";
                        similar_card_clone.querySelector("#similar-card-watchlist-star").classList.remove("far");
                        similar_card_clone.querySelector("#similar-card-watchlist-star").classList.add("fas");
                        similar_card_clone.querySelector("#similar-card-watchlist-star").classList.add("active");
                    }

                    similar_card_clone.querySelector("#similar-card-watchlist-btn").addEventListener(
                            "click", (e) => {
                        cardAddToWatchlist(similar_card_clone, content.id);
                        e.preventDefault();
                    });

                    similar_card_clone.querySelector("#similar-card-title").innerHTML = content.name;
                    similar_card_clone.querySelector("#similar-card-price").innerHTML = "Price: Rs. " + new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(content.price);

                    similar_card_clone.querySelector("#similar-card-details").innerHTML = "Release: " +
                            new Intl.DateTimeFormat("en-US", {
                                year: "numeric"
                            }).format(new Date(content.released_at)) + "\
         | Quality: " + content.qualityType.name + " | Duration: " + formatDuration(content.duration);

                    similar_card_clone.querySelector("#similar-card-buy-now-btn").href = "single_product_view.html?id=" + content.id;

                    similar_card_clone.querySelector("#similar-card-add-to-cart-btn").addEventListener(
                            "click", (e) => {
                        addToCart(json.mainContent.id);
                        e.preventDefault();
                    });

                    similar_card_container.appendChild(similar_card_clone);
                });

            } else {
                window.location = "index.html";
            }
        } else {
            window.location = "index.html";
        }
    } else {
        window.location = "index.html";
    }

});

async function addRate(contentId, rateTypeId) {
    const response = await fetch("AddContentRate?contentId=" + contentId + "&rateTypeId=" + rateTypeId);

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            swal({
                title: "Success message!",
                text: json.message,
                type: "success",
                timer: 2000
            });
            setTimeout(() => {
                window.location.reload();
            }, 2000);
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

async function mainAddToWatchlist(contentId) {
    const btn = document.getElementById("card-watchlist-btn");
    const star = document.getElementById("card-watchlist-star");

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

async function cardAddToWatchlist(cardClone, contentId) {
    const btn = cardClone.querySelector("#similar-card-watchlist-btn");
    const star = cardClone.querySelector("#similar-card-watchlist-star");

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

// Video Modal Functions
function openVideoModal(videoUrl) {
    const videoModalOverlay = document.getElementById("videoModalOverlay");
    const videoPlayer = document.getElementById("videoPlayer");
    videoPlayer.src = videoUrl + "?autoplay=1"; // Autoplay the video
    videoModalOverlay.classList.add('show-video-modal');
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