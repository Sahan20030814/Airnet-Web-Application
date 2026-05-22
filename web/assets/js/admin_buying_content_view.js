let content_id = "";
window.addEventListener("load", async function () {
    const searchParams = new URLSearchParams(window.location.search);

    if (searchParams.has("id")) {
        const contentId = searchParams.get("id");
        const response = await fetch("LoadAdminContentViewData?contentId=" + contentId);

        if (response.ok) {
            const json = await response.json();
            if (json.status) {
                document.getElementById("go-back-btn-a").href = "admin_single_product_view.html?id=" + json.mainContent.id;

                if (json.episodeCount > 0) {
                    content_id = json.mainContent.id;
                    document.getElementById("episode-view-container").classList.remove('d-none');

                    let first_episode_id = "";
                    let count = 1;
                    const select = document.getElementById("episodeSelector");
                    json.episodesList.forEach(episode => {
                        const option = document.createElement("option");
                        option.value = json.mainContent.id + "_" + episode.id + ".mp4";
                        option.innerHTML = "Eps " + count + " : " + episode.name;
                        if (count == 1) {
                            first_episode_id = episode.id;
                            option.selected = true;
                        }
                        select.appendChild(option);
                        count++;
                    });

                    document.getElementById('videoPlayer').src = "product_images\\" + json.mainContent.id + "\\" + json.mainContent.id + "_" + first_episode_id + ".mp4";
                    document.getElementById("content-name").innerHTML = json.mainContent.name;

                } else {
                    document.getElementById("episode-view-container").classList.add('d-none');
                    const select = document.getElementById("episodeSelector");
                    const option = document.createElement("option");
                    option.value = "0";
                    option.innerHTML = "Select";
                    option.selected = true;
                    select.appendChild(option);

                    swal({
                        title: "Message!",
                        text: "No episodes of \"" + json.mainContent.name + "\" " + json.mainContent.movieType.name + " were uploaded yet!",
                        type: "warning"
                    });
                }
            } else {
                window.location = "admin_single_product_view.html?id=" + json.mainContent.id;
            }
        } else {
            window.location = "admin_single_product_view.html?id=" + json.mainContent.id;
        }
    } else {
        window.location = "admin_panel.html";
    }
});

function setEpisodeVideo() {
    let episode_id = document.getElementById("episodeSelector").value;
    let episodeFilePath = "";
    if (episode_id != 0) {
        episodeFilePath = "product_images\\" + content_id + "\\" + episode_id;
    }
    document.getElementById('videoPlayer').src = episodeFilePath;
}