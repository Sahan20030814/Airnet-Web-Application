window.addEventListener("load", async function () {
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("id")) {
        const contentId = searchParams.get("id");
        const response = await fetch("LoadAdminSingleProduct?id=" + contentId);

        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                document.getElementById("main-card-container").classList.remove("d-none");

                document.getElementById("main-content-background-image").src = "product_images\\" + json.mainContent.id + "\\background_image.jpg";
                document.getElementById("main-content-card-image").src = "product_images\\" + json.mainContent.id + "\\card_image.jpg";
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
                document.getElementById("main-content-watch-now-btn").href = "admin_buying_content_view.html?id=" + json.mainContent.id;
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