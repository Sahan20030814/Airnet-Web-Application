var genres_list;
var countries_list;
var languages_list;

let usersContentId;
let goBackTypeId;

window.addEventListener("load", async function () {
    const response = await fetch("MyAccount");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            genres_list = json.genreList;
            countries_list = json.countryList;
            languages_list = json.languageList;

            // My Profile
            document.getElementById("firstName").value = json.firstName;
            document.getElementById("lastName").value = json.lastName;
            document.getElementById("email").value = json.email;
            document.getElementById("currentPassword").value = json.password;
            // My Profile

            // Movies & Tv Shows
            loadSelect("contentTypeSelector", json.movieTypeList);
            loadSelect("contentQualitySelector", json.qualityList);

            loadCheckBox("genre", json.genreList);
            loadCheckBox("country", json.countryList);
            loadCheckBox("language", json.languageList);

            updateContentSelect("updateContentSelector", json.userContentList);
            loadSelect("updateContentTypeSelector", json.movieTypeList);
            loadSelect("updateContentQualitySelector", json.qualityList);

            loadCheckBox("update-genre", json.genreList);
            loadCheckBox("update-country", json.countryList);
            loadCheckBox("update-language", json.languageList);
            // Movies & Tv Shows

            //Uploads
            loadSelect("episodeContentType", json.movieTypeList);
            loadSelect("deleteEpisodeContentType", json.movieTypeList);
            //Uploads

            //Watchlist
            loadSelect("watchlistContentType", json.movieTypeList);
            //Watchlist
        }
    }

});

function loadSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.name;
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

function updateContentSelect(selectId, list) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option");
        option.value = item.id;
        option.innerHTML = item.name + " (" + formatDateTime(new Date(item.registered_at)) + ")";
        select.appendChild(option);
    });
}

async function saveUserDetails() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;

    const userData = {
        firstName: firstName,
        lastName: lastName,
        savingType: "userDetails"
    };

    const userDataJSON = JSON.stringify(userData);

    const response = await fetch("MyAccount", {
        method: "PUT",
        body: userDataJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            document.getElementById("notification").innerHTML = json.message;
            document.getElementById("loginNotification").classList.remove('d-none');
            document.getElementById("loginNotification").className = "notification-area text-center bg-success";
            document.getElementById("notification").className = "notification-text text-light";
            setTimeout(() => {
                document.getElementById("loginNotification").classList.add('d-none');
                window.location.reload();
            }, 4000);
        } else {
            document.getElementById("notification").innerHTML = json.message;
            document.getElementById("loginNotification").classList.remove('d-none');
            setTimeout(() => {
                document.getElementById("loginNotification").classList.add('d-none');
            }, 8000);
        }

    } else {
        document.getElementById("notification").innerHTML = "Changes saving failed. Please try again later!";
        document.getElementById("loginNotification").classList.remove('d-none');
        setTimeout(() => {
            document.getElementById("loginNotification").classList.add('d-none');
        }, 8000);
    }
}

async function savePasswordDetails() {

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const userPasswordData = {
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword,
        savingType: "userPasswordDetails"
    };

    const userPasswordDataJSON = JSON.stringify(userPasswordData);

    const response = await fetch("MyAccount", {
        method: "PUT",
        body: userPasswordDataJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            document.getElementById("notification2").innerHTML = json.message;
            document.getElementById("loginNotification2").classList.remove('d-none');
            document.getElementById("loginNotification2").className = "notification-area text-center bg-success";
            document.getElementById("notification2").className = "notification-text text-light";
            setTimeout(() => {
                document.getElementById("loginNotification2").classList.add('d-none');
                window.location.reload();
            }, 4000);

        } else {
            document.getElementById("notification2").innerHTML = json.message;
            document.getElementById("loginNotification2").classList.remove('d-none');
            setTimeout(() => {
                document.getElementById("loginNotification2").classList.add('d-none');
            }, 8000);
        }

    } else {
        document.getElementById("notification2").innerHTML = "Changes saving failed. Please try again later!";
        document.getElementById("loginNotification2").classList.remove('d-none');
        setTimeout(() => {
            document.getElementById("loginNotification2").classList.add('d-none');
        }, 8000);
    }
}

function formatDateTime(dateObj) {
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
    const day = String(dateObj.getDate()).padStart(2, '0');

    let hours = dateObj.getHours();
    const minutes = String(dateObj.getMinutes()).padStart(2, '0');
    const seconds = String(dateObj.getSeconds()).padStart(2, '0');
    const ampm = hours >= 12 ? 'PM' : 'AM';

    hours = hours % 12;
    hours = hours ? hours : 12; // handle midnight (0 becomes 12)
    const hourStr = String(hours).padStart(2, '0');

    return `${year}-${month}-${day} ${hourStr}:${minutes}:${seconds} ${ampm}`;
}

async function contentRegistration() {

    const content_name = document.getElementById("content-name").value;
    const content_description = document.getElementById("content-description").value;
    const typeId = document.getElementById("contentTypeSelector").value;
    const qualityId = document.getElementById("contentQualitySelector").value;
    const price = document.getElementById("content-price").value;

    const genreCollection = [];
    if (genres_list !== null) {
        genres_list.forEach(genre => {
            if (document.getElementById("genre-" + genre.id).checked) {
                genreCollection.push(genre.id);
            }
        });
    }

    const countryCollection = [];
    if (countries_list !== null) {
        countries_list.forEach(country => {
            if (document.getElementById("country-" + country.id).checked) {
                countryCollection.push(country.id);
            }
        });
    }

    const languageCollection = [];
    if (languages_list !== null) {
        languages_list.forEach(language => {
            if (document.getElementById("language-" + language.id).checked) {
                languageCollection.push(language.id);
            }
        });
    }

    const releasedAt = document.getElementById("content-release-at").value;
    const episodeCount = document.getElementById("episode-count").value;
    const duration = document.getElementById("episode-duration").value;
    const cast = document.getElementById("content-cast").value;
    const production = document.getElementById("content-production").value;
    const trailer = document.getElementById("youtubeTrailerLink").value;

    const bgImg = document.getElementById("contentBgImg").files[0];
    const cardImg = document.getElementById("contentCardImg").files[0];

    const form = new FormData();
    form.append("name", content_name);
    form.append("description", content_description);
    form.append("typeId", typeId);
    form.append("qualityId", qualityId);
    form.append("price", price);
    form.append("genreCollection", genreCollection);
    form.append("countryCollection", countryCollection);
    form.append("languageCollection", languageCollection);
    form.append("releasedAt", releasedAt);
    form.append("episodeCount", episodeCount);
    form.append("duration", duration);
    form.append("cast", cast);
    form.append("production", production);
    form.append("trailer", trailer);
    form.append("bgImg", bgImg);
    form.append("cardImg", cardImg);

    const response = await fetch("RegisterContentData", {
        method: "POST",
        body: form
    });

    if (response.ok) {

        const json = await response.json();

        if (json.status) {
            document.getElementById("content-registration-btn").disabled = true;
            document.getElementById("notification3").innerHTML = json.message;
            document.getElementById("loginNotification3").classList.remove('d-none');
            document.getElementById("loginNotification3").className = "notification-area text-center bg-success";
            document.getElementById("notification3").className = "notification-text text-light";
            setTimeout(() => {
                document.getElementById("loginNotification3").classList.add('d-none');
                window.location.reload();
            }, 4000);

        } else {
            if (json.message === "Please sign in first!") {
                window.location = "signin.html";
            } else {
                viewErrorNotification("notification3", "loginNotification3", json.message);
            }
        }
    } else {
        viewErrorNotification("notification3", "loginNotification3", "Something went wrong. Please try again later!");
    }
}

function resetRegistrationSection() {
    document.getElementById("content-name").value = "";
    document.getElementById("content-description").value = "";
    document.getElementById("contentTypeSelector").value = "0";
    document.getElementById("contentQualitySelector").value = "0";
    document.getElementById("content-price").value = "";

    if (genres_list !== null) {
        genres_list.forEach(genre => {
            document.getElementById("genre-" + genre.id).checked = false;
        });
    }

    if (countries_list !== null) {
        countries_list.forEach(country => {
            document.getElementById("country-" + country.id).checked = false;
        });
    }

    if (languages_list !== null) {
        languages_list.forEach(language => {
            document.getElementById("language-" + language.id).checked = false;
        });
    }

    document.getElementById("content-release-at").value = "";
    document.getElementById("episode-count").value = "";
    document.getElementById("episode-duration").value = "";
    document.getElementById("content-cast").value = "";
    document.getElementById("content-production").value = "";
    document.getElementById("youtubeTrailerLink").value = "";
    document.getElementById("bgImagePreview").src = "https://placehold.co/300x150/333333/ffffff?text=Background+Image";
    document.getElementById("cardImagePreview").src = "https://placehold.co/150x225/333333/ffffff?text=Card+Image";
}

async function updateContentLoading() {
    const selectedContentId = document.getElementById("updateContentSelector").value;

    if (selectedContentId == "0") {
        resetUpdateSection();
    } else {
        const contentIdData = {
            contentId: selectedContentId
        };

        const contentIdDataJSON = JSON.stringify(contentIdData);

        const response = await fetch("LoadUpdatedContentData?id=" + selectedContentId);

        if (response.ok) {
            const json = await response.json();
            if (json.status) {

                document.getElementById("update-content-name").value = json.mainContent.name;
                document.getElementById("update-content-name").disabled = false;
                document.getElementById("update-content-description").value = json.mainContent.description;
                document.getElementById("update-content-description").disabled = false;
                document.getElementById("updateContentTypeSelector").value = json.mainContent.movieType.id;
                document.getElementById("updateContentTypeSelector").disabled = false;
                document.getElementById("updateContentQualitySelector").value = json.mainContent.qualityType.id;
                document.getElementById("updateContentQualitySelector").disabled = false;
                document.getElementById("update-content-price").value = json.mainContent.price;
                document.getElementById("update-content-price").disabled = false;

                if (genres_list !== null) {
                    genres_list.forEach(genre => {
                        document.getElementById("update-genre-" + genre.id).checked = false;
                        document.getElementById("update-genre-" + genre.id).disabled = false;
                    });
                }

                if (countries_list !== null) {
                    countries_list.forEach(country => {
                        document.getElementById("update-country-" + country.id).checked = false;
                        document.getElementById("update-country-" + country.id).disabled = false;
                    });
                }

                if (languages_list !== null) {
                    languages_list.forEach(language => {
                        document.getElementById("update-language-" + language.id).checked = false;
                        document.getElementById("update-language-" + language.id).disabled = false;
                    });
                }

                if (json.contentGenreList !== null) {
                    json.contentGenreList.forEach(contentHasGenre => {
                        document.getElementById("update-genre-" + contentHasGenre.genre.id).checked = true;
                    });
                }

                if (json.contentCountryList !== null) {
                    json.contentCountryList.forEach(contentHasCountry => {
                        document.getElementById("update-country-" + contentHasCountry.country.id).checked = true;
                    });
                }

                if (json.contentLanguageList !== null) {
                    json.contentLanguageList.forEach(contentHasLanguage => {
                        document.getElementById("update-language-" + contentHasLanguage.language.id).checked = true;
                    });
                }

                document.getElementById("update-content-release-at").value = new Intl.DateTimeFormat("en-CA", {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit"
                }).format(new Date(json.mainContent.released_at));
                document.getElementById("update-content-release-at").disabled = false;
                document.getElementById("update-content-episode-count").value = json.mainContent.episode_count;
                document.getElementById("update-content-episode-count").disabled = false;
                document.getElementById("update-content-duration").value = json.mainContent.duration;
                document.getElementById("update-content-duration").disabled = false;
                document.getElementById("update-content-cast").value = json.mainContent.cast;
                document.getElementById("update-content-cast").disabled = false;
                document.getElementById("update-content-production").value = json.mainContent.production;
                document.getElementById("update-content-production").disabled = false;
                document.getElementById("updateYoutubeTrailerLink").value = json.mainContent.trailer;
                document.getElementById("updateYoutubeTrailerLink").disabled = false;
                document.getElementById("updateBgImagePreview").src = "product_images\\" + json.mainContent.id + "\\background_image.jpg";
                document.getElementById("update-content-bg-img-btn").disabled = false;
                document.getElementById("updateCardImagePreview").src = "product_images\\" + json.mainContent.id + "\\card_image.jpg";
                document.getElementById("update-content-card-img-btn").disabled = false;
                document.getElementById("update-content-save-btn").disabled = false;

            } else {
                document.getElementById("updateContentSelector").value = "0";
            }
        } else {
            document.getElementById("updateContentSelector").value = "0";
        }
    }
}

async function contentUpdate() {

    const contentId = document.getElementById("updateContentSelector").value;

    if (contentId == "0") {
        resetUpdateSection();
    } else {
        const content_name = document.getElementById("update-content-name").value;
        const content_description = document.getElementById("update-content-description").value;
        const typeId = document.getElementById("updateContentTypeSelector").value;
        const qualityId = document.getElementById("updateContentQualitySelector").value;
        const price = document.getElementById("update-content-price").value;

        const genreCollection = [];
        if (genres_list !== null) {
            genres_list.forEach(genre => {
                if (document.getElementById("update-genre-" + genre.id).checked) {
                    genreCollection.push(genre.id);
                }
            });
        }

        const countryCollection = [];
        if (countries_list !== null) {
            countries_list.forEach(country => {
                if (document.getElementById("update-country-" + country.id).checked) {
                    countryCollection.push(country.id);
                }
            });
        }

        const languageCollection = [];
        if (languages_list !== null) {
            languages_list.forEach(language => {
                if (document.getElementById("update-language-" + language.id).checked) {
                    languageCollection.push(language.id);
                }
            });
        }

        const releasedAt = document.getElementById("update-content-release-at").value;
        const episodeCount = document.getElementById("update-content-episode-count").value;
        const duration = document.getElementById("update-content-duration").value;
        const cast = document.getElementById("update-content-cast").value;
        const production = document.getElementById("update-content-production").value;
        const trailer = document.getElementById("updateYoutubeTrailerLink").value;

        const bgImg = document.getElementById("updateContentBgImg").files[0];
        const cardImg = document.getElementById("updateContentCardImg").files[0];

        const form = new FormData();
        form.append("id", contentId);
        form.append("name", content_name);
        form.append("description", content_description);
        form.append("typeId", typeId);
        form.append("qualityId", qualityId);
        form.append("price", price);
        form.append("genreCollection", genreCollection);
        form.append("countryCollection", countryCollection);
        form.append("languageCollection", languageCollection);
        form.append("releasedAt", releasedAt);
        form.append("episodeCount", episodeCount);
        form.append("duration", duration);
        form.append("cast", cast);
        form.append("production", production);
        form.append("trailer", trailer);
        form.append("bgImg", bgImg);
        form.append("cardImg", cardImg);

        const response = await fetch("UpdateContentData", {
            method: "POST",
            body: form
        });

        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                document.getElementById("notification4").innerHTML = json.message;
                document.getElementById("loginNotification4").classList.remove('d-none');
                document.getElementById("loginNotification4").className = "notification-area text-center bg-success";
                document.getElementById("notification4").className = "notification-text text-light";
                setTimeout(() => {
                    document.getElementById("loginNotification4").classList.add('d-none');
                }, 4000);

            } else {
                if (json.message === "Please sign in first!") {
                    window.location = "signin.html";
                } else {
                    viewErrorNotification("notification4", "loginNotification4", json.message);
                }
            }
        } else {
            viewErrorNotification("notification4", "loginNotification4", "Something went wrong. Please try again later!");
        }
    }
}

function resetUpdateSection() {
    document.getElementById("update-content-name").value = "";
    document.getElementById("update-content-name").disabled = true;
    document.getElementById("update-content-description").value = "";
    document.getElementById("update-content-description").disabled = true;
    document.getElementById("updateContentTypeSelector").value = "0";
    document.getElementById("updateContentTypeSelector").disabled = true;
    document.getElementById("updateContentQualitySelector").value = "0";
    document.getElementById("updateContentQualitySelector").disabled = true;
    document.getElementById("update-content-price").value = "";
    document.getElementById("update-content-price").disabled = true;

    if (genres_list !== null) {
        genres_list.forEach(genre => {
            document.getElementById("update-genre-" + genre.id).checked = false;
            document.getElementById("update-genre-" + genre.id).disabled = true;
        });
    }

    if (countries_list !== null) {
        countries_list.forEach(country => {
            document.getElementById("update-country-" + country.id).checked = false;
            document.getElementById("update-country-" + country.id).disabled = true;
        });
    }

    if (languages_list !== null) {
        languages_list.forEach(language => {
            document.getElementById("update-language-" + language.id).checked = false;
            document.getElementById("update-language-" + language.id).disabled = true;
        });
    }

    document.getElementById("update-content-release-at").value = "";
    document.getElementById("update-content-release-at").disabled = true;
    document.getElementById("update-content-episode-count").value = "";
    document.getElementById("update-content-episode-count").disabled = true;
    document.getElementById("update-content-duration").value = "";
    document.getElementById("update-content-duration").disabled = true;
    document.getElementById("update-content-cast").value = "";
    document.getElementById("update-content-cast").disabled = true;
    document.getElementById("update-content-production").value = "";
    document.getElementById("update-content-production").disabled = true;
    document.getElementById("updateYoutubeTrailerLink").value = "";
    document.getElementById("updateYoutubeTrailerLink").disabled = true;
    document.getElementById("updateBgImagePreview").src = "https://placehold.co/300x150/333333/ffffff?text=Background+Image";
    document.getElementById("update-content-bg-img-btn").disabled = true;
    document.getElementById("updateCardImagePreview").src = "https://placehold.co/150x225/333333/ffffff?text=Card+Image";
    document.getElementById("update-content-card-img-btn").disabled = true;
    document.getElementById("update-content-save-btn").disabled = true;
}

function viewErrorNotification(textId, areaId, message) {
    document.getElementById(textId).innerHTML = message;
    document.getElementById(areaId).classList.remove('d-none');
    document.getElementById(areaId).className = "notification-area text-center bg-warning";
    document.getElementById(textId).className = "notification-text text-dark";
    setTimeout(() => {
        document.getElementById(areaId).classList.add('d-none');
    }, 8000);
}

async function loadContentOfType() {
    const contentTypeId = document.getElementById("episodeContentType").value;

    if (contentTypeId == "0") {
        resetEpisodeSelectorData("episodeMovieTvSeries");
        document.getElementById("registerEpisodeName").value = "";
        document.getElementById("registerEpisodeFileUpload").value = "";
    } else {
        const response = await fetch("LoadEpisodeContents?typeId=" + contentTypeId);
        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                resetEpisodeSelectorData("episodeMovieTvSeries");
                updateContentSelect("episodeMovieTvSeries", json.episodeContentList);
                document.getElementById("registerEpisodeName").value = "";
                document.getElementById("registerEpisodeFileUpload").value = "";
                setEpisodeUploadBtnEnable();
            } else {
                resetEpisodeSelectorData("episodeMovieTvSeries");
                document.getElementById("registerEpisodeName").value = "";
                document.getElementById("registerEpisodeFileUpload").value = "";
                setEpisodeUploadBtnEnable();
            }
        } else {
            resetEpisodeSelectorData("episodeMovieTvSeries");
            document.getElementById("registerEpisodeName").value = "";
            document.getElementById("registerEpisodeFileUpload").value = "";
            setEpisodeUploadBtnEnable();
        }
    }
}

function resetEpisodeSelectorData(selectorId) {
    document.getElementById(selectorId).innerHTML = "";
    const option = document.createElement("option");
    option.value = "0";
    option.innerHTML = "Select";
    option.selected = true;
    document.getElementById(selectorId).appendChild(option);
}

function setEpisodeUploadBtnEnable() {
    if (document.getElementById("episodeMovieTvSeries").value == "0") {
        document.getElementById("episode-upload-btn").disabled = true;
    } else {
        document.getElementById("episode-upload-btn").disabled = false;
    }
}

async function loadDeleteContentOfType() {
    const contentTypeId = document.getElementById("deleteEpisodeContentType").value;
    if (contentTypeId == "0") {
        resetEpisodeSelectorData("deleteEpisodeMovieTvSeries");
        loadDeleteEpisodeOfContent();
    } else {
        const response = await fetch("LoadEpisodeContents?typeId=" + contentTypeId);
        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                resetEpisodeSelectorData("deleteEpisodeMovieTvSeries");
                updateContentSelect("deleteEpisodeMovieTvSeries", json.episodeContentList);
                loadDeleteEpisodeOfContent();
            } else {
                resetEpisodeSelectorData("deleteEpisodeMovieTvSeries");
                loadDeleteEpisodeOfContent();
            }
        } else {
            resetEpisodeSelectorData("deleteEpisodeMovieTvSeries");
            loadDeleteEpisodeOfContent();
        }
    }
}

async function loadDeleteEpisodeOfContent() {
    const contentId = document.getElementById("deleteEpisodeMovieTvSeries").value;
    if (contentId == "0") {
        resetEpisodeListData("deletedEpisode");
    } else {
        const response = await fetch("LoadDeleteEpisode?contentId=" + contentId);
        if (response.ok) {
            const json = await response.json();

            if (json.status) {
                resetEpisodeListData("deletedEpisode");
                updateContentSelect("deletedEpisode", json.contentEpisodeList);
            } else {
                resetEpisodeListData("deletedEpisode");
            }
        } else {
            resetEpisodeListData("deletedEpisode");
        }
    }
}

function resetEpisodeListData(selectorId) {
    document.getElementById(selectorId).innerHTML = "";
    const option = document.createElement("option");
    option.value = "0";
    option.innerHTML = "Select";
    option.selected = true;
    document.getElementById(selectorId).appendChild(option);
    document.getElementById("episode-delete-btn").disabled = true;
}

function setEpisodeDeleteBtnEnable() {
    if (document.getElementById("deletedEpisode").value == "0") {
        document.getElementById("episode-delete-btn").disabled = true;
    } else {
        document.getElementById("episode-delete-btn").disabled = false;
    }
}

async function episodeUploading() {
    document.getElementById("episodeUploadingSnipper").classList.remove('d-none');
    document.getElementById("episode-upload-btn").disabled = true;

    const typeId = document.getElementById("episodeContentType").value;
    const contentId = document.getElementById("episodeMovieTvSeries").value;
    const episodeName = document.getElementById("registerEpisodeName").value;

    const episodePath = document.getElementById("registerEpisodeFileUpload").files[0];

    const form = new FormData();
    form.append("typeId", typeId);
    form.append("contentId", contentId);
    form.append("episodeName", episodeName);
    form.append("episode", episodePath);

    const response = await fetch("RegisterEpisodeData", {
        method: "POST",
        body: form
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            document.getElementById("episodeUploadingSnipper").className = "spinner-border spinner-border text-light d-none";
            document.getElementById("episode-upload-btn").disabled = true;

            document.getElementById("notification5").innerHTML = json.message;
            document.getElementById("loginNotification5").classList.remove('d-none');
            document.getElementById("loginNotification5").className = "notification-area text-center bg-success";
            document.getElementById("notification5").className = "notification-text text-light";

            setTimeout(() => {
                document.getElementById("loginNotification5").classList.add('d-none');
                window.location.reload();
            }, 4000);

        } else {
            document.getElementById("episodeUploadingSnipper").className = "spinner-border spinner-border text-light d-none";
            document.getElementById("episode-upload-btn").disabled = false;
            if (json.message === "Please sign in first!") {
                window.location = "signin.html";
            } else {
                viewErrorNotification("notification5", "loginNotification5", json.message);
            }
        }
    } else {
        document.getElementById("episodeUploadingSnipper").className = "spinner-border spinner-border text-light d-none";
        document.getElementById("episode-upload-btn").disabled = false;
        viewErrorNotification("notification5", "loginNotification5", "Something went wrong. Please try again later!");
    }
}

async function episodeDeleting() {
    const typeId = document.getElementById("deleteEpisodeContentType").value;
    const contentId = document.getElementById("deleteEpisodeMovieTvSeries").value;
    const episodeId = document.getElementById("deletedEpisode").value;

    const deleteEpisodeData = {
        typeId: typeId,
        contentId: contentId,
        episodeId: episodeId
    };

    const deleteEpisodeDataJSON = JSON.stringify(deleteEpisodeData);

    const response = await fetch("DeleteEpisodeData", {
        method: "POST",
        body: deleteEpisodeDataJSON,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            document.getElementById("episode-delete-btn").disabled = true;
            document.getElementById("notification6").innerHTML = json.message;
            document.getElementById("loginNotification6").classList.remove('d-none');
            document.getElementById("loginNotification6").className = "notification-area text-center bg-success";
            document.getElementById("notification6").className = "notification-text text-light";

            setTimeout(() => {
                document.getElementById("loginNotification6").classList.add('d-none');
                window.location.reload();
            }, 4000);

        } else {
            if (json.message === "Please sign in first!") {
                window.location = "signin.html";
            } else {
                viewErrorNotification("notification6", "loginNotification6", json.message);
            }
        }
    } else {
        viewErrorNotification("notification6", "loginNotification6", "Something went wrong. Please try again later!");
    }
}

async function loadOwnMovies() {

    const movieName = document.getElementById("own-movie-search-bar").value;

    const response = await fetch("LoadOwnMovies?movieName=" + movieName);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.movieCount > 0) {
                document.getElementById("noMoviesMessage").classList.add("d-none");
                document.getElementById("ownMovieTable").classList.remove("d-none");

                let table_body = document.getElementById("own-movie-table-body");
                let table_row = document.getElementById("own-movie-table-row");
                table_body.innerHTML = "";

                let count = 1;

                json.ownMovieList.forEach(movie => {
                    let table_row_clone = table_row.cloneNode(true);

                    table_row_clone.classList.remove("d-none");
                    table_row_clone.querySelector("#own-movie-number").innerHTML = count;
                    table_row_clone.querySelector("#own-movie-img-a").href = "single_product_view.html?id=" + movie.id;
                    table_row_clone.querySelector("#own-movie-img").src = "product_images\\" + movie.id + "\\card_image.jpg";
                    table_row_clone.querySelector("#own-movie-name").innerHTML = movie.name;

                    let genres = "";
                    json.movieHasGenreList.forEach(item => {
                        if (item.mainMovie.id == movie.id) {
                            genres += item.genre.name + ", ";
                        }
                    });
                    genres = genres.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#own-movie-genre").innerHTML = genres;

                    let languages = "";
                    json.movieHasLanguageList.forEach(item => {
                        if (item.mainMovie.id == movie.id) {
                            languages += item.language.name + ", ";
                        }
                    });
                    languages = languages.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#own-movie-language").innerHTML = languages;

                    let countries = "";
                    json.movieHasCountryList.forEach(item => {
                        if (item.mainMovie.id == movie.id) {
                            countries += item.country.name + ", ";
                        }
                    });
                    countries = countries.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#own-movie-country").innerHTML = countries;

                    table_row_clone.querySelector("#own-movie-quality").innerHTML = movie.qualityType.name;
                    table_row_clone.querySelector("#own-movie-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(movie.price);
                    table_row_clone.querySelector("#own-movie-status").innerHTML = movie.status.name;

                    table_row_clone.querySelector("#own-movie-view-users-btn").addEventListener(
                            "click", (e) => {
                        navigateToPanel('buyers-listing-content', movie.id, 1);
                        e.preventDefault();
                    });

                    table_row_clone.querySelector("#own-movie-update-btn").addEventListener(
                            "click", (e) => {
                        navigateToContent('movies-dramas-content', null, null);
                        e.preventDefault();
                    });

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noMoviesMessage").classList.remove("d-none");
                document.getElementById("ownMovieTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noMoviesMessage").classList.remove("d-none");
            document.getElementById("ownMovieTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noMoviesMessage").classList.remove("d-none");
        document.getElementById("ownMovieTable").classList.add("d-none");
    }
}

async function loadOwnTvShows() {

    const tvShowName = document.getElementById("own-tv-show-search-bar").value;

    const response = await fetch("LoadOwnTvShows?tvShowName=" + tvShowName);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.tvShowCount > 0) {
                document.getElementById("noTvShowMessage").classList.add("d-none");
                document.getElementById("ownTvShowTable").classList.remove("d-none");

                let table_body = document.getElementById("own-tv-show-table-body");
                let table_row = document.getElementById("own-tv-show-table-row");
                table_body.innerHTML = "";

                let count = 1;

                json.ownTvShowList.forEach(movie => {
                    let table_row_clone = table_row.cloneNode(true);

                    table_row_clone.classList.remove("d-none");
                    table_row_clone.querySelector("#own-tv-show-number").innerHTML = count;
                    table_row_clone.querySelector("#own-tv-show-img-a").href = "single_product_view.html?id=" + movie.id;
                    table_row_clone.querySelector("#own-tv-show-img").src = "product_images\\" + movie.id + "\\card_image.jpg";
                    table_row_clone.querySelector("#own-tv-show-name").innerHTML = movie.name;

                    let genres = "";
                    json.tvShowHasGenreList.forEach(item => {
                        if (item.mainMovie.id == movie.id) {
                            genres += item.genre.name + ", ";
                        }
                    });
                    genres = genres.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#own-tv-show-genre").innerHTML = genres;

                    let languages = "";
                    json.tvShowHasLanguageList.forEach(item => {
                        if (item.mainMovie.id == movie.id) {
                            languages += item.language.name + ", ";
                        }
                    });
                    languages = languages.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#own-tv-show-language").innerHTML = languages;

                    let countries = "";
                    json.tvShowHasCountryList.forEach(item => {
                        if (item.mainMovie.id == movie.id) {
                            countries += item.country.name + ", ";
                        }
                    });
                    countries = countries.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#own-tv-show-country").innerHTML = countries;

                    table_row_clone.querySelector("#own-tv-show-quality").innerHTML = movie.qualityType.name;
                    table_row_clone.querySelector("#own-tv-show-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(movie.price);
                    table_row_clone.querySelector("#own-tv-show-episodes").innerHTML = movie.episode_count;
                    table_row_clone.querySelector("#own-tv-show-status").innerHTML = movie.status.name;

                    table_row_clone.querySelector("#own-tv-show-view-users-btn").addEventListener(
                            "click", (e) => {
                        navigateToPanel('buyers-listing-content', movie.id, 2);
                        e.preventDefault();
                    });

                    table_row_clone.querySelector("#own-tv-show-update-btn").addEventListener(
                            "click", (e) => {
                        navigateToContent('movies-dramas-content', null, null);
                        e.preventDefault();
                    });

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noTvShowMessage").classList.remove("d-none");
                document.getElementById("ownTvShowTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noTvShowMessage").classList.remove("d-none");
            document.getElementById("ownTvShowTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noTvShowMessage").classList.remove("d-none");
        document.getElementById("ownTvShowTable").classList.add("d-none");
    }
}

async function loadPurchasingItems() {

    const itemName = document.getElementById("purchasing-items-search-bar").value;
    const orderId = document.getElementById("purchasingOrder").value;

    const response = await fetch("LoadPurchasingItems?itemName=" + itemName + "&orderId=" + orderId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.itemCount > 0) {
                document.getElementById("noPurchasingMessage").classList.add("d-none");
                document.getElementById("purchasingItemsTable").classList.remove("d-none");

                let table_body = document.getElementById("purchasing-item-table-body");
                let table_row = document.getElementById("purchasing-item-table-row");
                table_body.innerHTML = "";

                let row_id = "";
                let count = 1;
                let same_row = 0;
                json.checkoutItemList.forEach(checkoutItem => {
                    let table_row_clone = table_row.cloneNode(true);

                    table_row_clone.classList.remove("d-none");

                    if (row_id == checkoutItem.checkout.id) {
                        table_row_clone.querySelector("#purchasing-item-invoice-number").innerHTML = "";
                    } else {
                        table_row_clone.querySelector("#purchasing-item-invoice-number").innerHTML = "#" + checkoutItem.checkout.id;
                        table_row_clone.querySelector("#purchasing-item-invoice-btn").classList.remove("d-none");
                        row_id = checkoutItem.checkout.id;
                        count = 1;
                        same_row++;
                    }

                    if (same_row % 2 === 0) {
                        table_row_clone.classList.add("table-success");
                    } else {
                        table_row_clone.classList.add("table-secondary");
                    }

                    table_row_clone.querySelector("#purchasing-item-item-number").innerHTML = count;
                    table_row_clone.querySelector("#purchasing-item-img-a").href = "single_product_view.html?id=" + checkoutItem.mainMovie.id;
                    table_row_clone.querySelector("#purchasing-item-img").src = "product_images\\" + checkoutItem.mainMovie.id + "\\card_image.jpg";
                    table_row_clone.querySelector("#purchasing-item-name").innerHTML = checkoutItem.mainMovie.name;
                    table_row_clone.querySelector("#purchasing-item-content-type").innerHTML = checkoutItem.mainMovie.movieType.name;
                    table_row_clone.querySelector("#purchasing-item-episodes").innerHTML = checkoutItem.mainMovie.episode_count;
                    table_row_clone.querySelector("#purchasing-item-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(checkoutItem.price);
                    table_row_clone.querySelector("#purchasing-item-date").innerHTML = new Intl.DateTimeFormat("en-CA", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit"
                    }).format(new Date(checkoutItem.registered_at));

                    table_row_clone.querySelector("#purchasing-item-invoice-btn").href = "invoice.html?invoiceId=" + checkoutItem.checkout.id;

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noPurchasingMessage").classList.remove("d-none");
                document.getElementById("purchasingItemsTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noPurchasingMessage").classList.remove("d-none");
            document.getElementById("purchasingItemsTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noPurchasingMessage").classList.remove("d-none");
        document.getElementById("purchasingItemsTable").classList.add("d-none");
    }
}

async function loadWatchlistItems() {

    const itemName = document.getElementById("watchlist-items-search-bar").value;
    const typeId = document.getElementById("watchlistContentType").value;

    const response = await fetch("LoadWatchlistItems?itemName=" + itemName + "&typeId=" + typeId);

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            if (json.watchlistItemCount > 0) {
                document.getElementById("noWatchlistMessage").classList.add("d-none");
                document.getElementById("watchlist-delete-all-btn").classList.remove("d-none");
                document.getElementById("watchlistItemsTable").classList.remove("d-none");

                let table_body = document.getElementById("watchlist-item-table-body");
                let table_row = document.getElementById("watchlist-item-table-row");
                table_body.innerHTML = "";

                let count = 1;
                json.watchlistItemList.forEach(watchlistItem => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    table_row_clone.querySelector("#watchlist-item-number").innerHTML = count;
                    table_row_clone.querySelector("#watchlist-item-img-a").href = "single_product_view.html?id=" + watchlistItem.mainMovie.id;
                    table_row_clone.querySelector("#watchlist-item-img").src = "product_images\\" + watchlistItem.mainMovie.id + "\\card_image.jpg";
                    table_row_clone.querySelector("#watchlist-item-name").innerHTML = watchlistItem.mainMovie.name;
                    table_row_clone.querySelector("#watchlist-item-content-type").innerHTML = watchlistItem.mainMovie.movieType.name;

                    let genres = "";
                    json.contentHasGenreList.forEach(item => {
                        if (item.mainMovie.id == watchlistItem.mainMovie.id) {
                            genres += item.genre.name + ", ";
                        }
                    });
                    genres = genres.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#watchlist-item-genre").innerHTML = genres;

                    let languages = "";
                    json.contentHasLanguageList.forEach(item => {
                        if (item.mainMovie.id == watchlistItem.mainMovie.id) {
                            languages += item.language.name + ", ";
                        }
                    });
                    languages = languages.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#watchlist-item-language").innerHTML = languages;

                    let countries = "";
                    json.contentHasCountryList.forEach(item => {
                        if (item.mainMovie.id == watchlistItem.mainMovie.id) {
                            countries += item.country.name + ", ";
                        }
                    });
                    countries = countries.replace(/,\s*$/, "");
                    table_row_clone.querySelector("#watchlist-item-country").innerHTML = countries;

                    table_row_clone.querySelector("#watchlist-item-quality").innerHTML = watchlistItem.mainMovie.qualityType.name;
                    table_row_clone.querySelector("#watchlist-item-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(watchlistItem.mainMovie.price);

                    table_row_clone.querySelector("#watchlist-item-delete-btn").addEventListener(
                            "click", (e) => {
                        deleteWatchlistItem(watchlistItem.mainMovie.id);
                        e.preventDefault();
                    });

                    table_body.appendChild(table_row_clone);
                    count++;
                });

            } else {
                document.getElementById("noWatchlistMessage").classList.remove("d-none");
                document.getElementById("watchlist-delete-all-btn").classList.add("d-none");
                document.getElementById("watchlistItemsTable").classList.add("d-none");
            }

        } else {
            document.getElementById("noWatchlistMessage").classList.remove("d-none");
            document.getElementById("watchlist-delete-all-btn").classList.add("d-none");
            document.getElementById("watchlistItemsTable").classList.add("d-none");
        }

    } else {
        document.getElementById("noWatchlistMessage").classList.remove("d-none");
        document.getElementById("watchlist-delete-all-btn").classList.add("d-none");
        document.getElementById("watchlistItemsTable").classList.add("d-none");
    }
}

async function deleteWatchlistItem(contentId) {
    const response = await fetch("DeleteSingleWatchlist?contentId=" + contentId);
    if (response.ok) {
        const json = await response.json();
        window.location.reload();
    } else {
        window.location.reload();
    }
}

function deleteAllWatchlistItem() {
    swal({
        title: "Are you sure?",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Yes, delete all!",
        closeOnConfirm: false
    },
            async function () {
                const response = await fetch("DeleteAllWatchlistItems");
                if (response.ok) {
                    const json = await response.json();
                    window.location.reload();
                } else {
                    window.location.reload();
                }
            });
}

function navigateToPanel(targetId, contentId, typeId) {
    document.getElementById("users-content-search-bar").value = "";
    const sidebarLinks = document.querySelectorAll('.account-sidebar .sidebar-link');
    const contentSections = document.querySelectorAll('.account-content .content-section');

    // Remove 'active' class from all links and hide all content sections
    sidebarLinks.forEach(item => item.classList.remove('active'));
    contentSections.forEach(section => section.classList.add('d-none'));

    // Show the target content section
    const targetContent = document.getElementById(targetId);
    if (targetContent) {
        targetContent.classList.remove('d-none');
    }

    // Find and activate the corresponding sidebar link
    const targetLink = document.querySelector(`.account-sidebar .sidebar-link[data-target="${targetId}"]`);
    if (targetLink) {
        targetLink.classList.add('active');
    }

    usersContentId = contentId;
    goBackTypeId = typeId;
    loadBuyingUsers();
}

async function loadBuyingUsers() {

    let invoiceId = document.getElementById("users-content-search-bar").value;
    invoiceId = invoiceId.replace(/^#/, "");
    const response = await fetch("LoadContentBuyingUsers?contentId=" + usersContentId + "&checkoutId=" + invoiceId);

    if (response.ok) {
        const json = await response.json();

        document.getElementById("content-users-list-title").innerHTML = json.contentTitle;
        document.getElementById("content-users-list-goback-a").addEventListener(
                "click", (e) => {
            if (goBackTypeId == 1) {
                navigateToPanel('movies-listing-content', null, 1);
            } else if (goBackTypeId == 2) {
                navigateToPanel('dramas-listing-content', null, 2);
            } else {
                navigateToPanel('movies-listing-content', null, null);
            }
            e.preventDefault();
        });

        if (json.status) {
            if (json.buyingContentUsersCount > 0) {
                document.getElementById("content-users-list-user-count").innerHTML = "Users Count: " + json.buyingContentUsersCount;

                document.getElementById("noContentUsersMessage").classList.add("d-none");
                document.getElementById("contentUsersTable").classList.remove("d-none");

                let table_body = document.getElementById("content-users-list-table-body");
                let table_row = document.getElementById("content-users-list-table-row");
                table_body.innerHTML = "";

                let seller_profit = 0;

                json.buyingContentUsersList.forEach(buyingUser => {
                    let table_row_clone = table_row.cloneNode(true);
                    table_row_clone.classList.remove("d-none");

                    table_row_clone.querySelector("#content-users-list-invoice-number").innerHTML = "#" + buyingUser.checkout.id;
                    table_row_clone.querySelector("#content-users-list-invoice-date").innerHTML = new Intl.DateTimeFormat("en-CA", {
                        year: "numeric",
                        month: "2-digit",
                        day: "2-digit"
                    }).format(new Date(buyingUser.registered_at));

                    table_row_clone.querySelector("#content-users-list-invoice-time").innerHTML = new Intl.DateTimeFormat("en-US", {
                        hour: "2-digit",
                        minute: "2-digit",
                        second: "2-digit",
                        hour12: true
                    }).format(new Date(buyingUser.registered_at));

                    table_row_clone.querySelector("#content-users-list-email").innerHTML = buyingUser.checkout.user.email;
                    table_row_clone.querySelector("#content-users-list-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(buyingUser.price);
                    table_row_clone.querySelector("#content-users-list-rate").innerHTML = buyingUser.rate + "%";
                    table_row_clone.querySelector("#content-users-list-seller-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(buyingUser.owner_price);
                    table_row_clone.querySelector("#content-users-list-site-price").innerHTML = new Intl.NumberFormat(
                            "en-US", {minimumFractionDigits: 2}).format(buyingUser.site_price);

                    table_row_clone.querySelector("#content-users-list-invoice-btn").href = "invoice.html?invoiceId=" + buyingUser.checkout.id;

                    table_body.appendChild(table_row_clone);
                    seller_profit += buyingUser.owner_price;
                });

                document.getElementById("content-users-list-seller-profit").innerHTML = "Total Seller Profit: Rs. " + new Intl.NumberFormat(
                        "en-US", {minimumFractionDigits: 2}).format(seller_profit);

            } else {
                document.getElementById("content-users-list-user-count").innerHTML = "Users Count: 0";
                document.getElementById("content-users-list-seller-profit").innerHTML = "Total Seller Profit: Rs. 0.00";
                document.getElementById("noContentUsersMessage").classList.remove("d-none");
                document.getElementById("contentUsersTable").classList.add("d-none");
            }

        } else {
            document.getElementById("content-users-list-user-count").innerHTML = "Users Count: 0";
            document.getElementById("content-users-list-seller-profit").innerHTML = "Total Seller Profit: Rs. 0.00";
            document.getElementById("noContentUsersMessage").classList.remove("d-none");
            document.getElementById("contentUsersTable").classList.add("d-none");
        }

    } else {
        document.getElementById("content-users-list-user-count").innerHTML = "Users Count: 0";
        document.getElementById("content-users-list-seller-profit").innerHTML = "Total Seller Profit: Rs. 0.00";
        document.getElementById("noContentUsersMessage").classList.remove("d-none");
        document.getElementById("contentUsersTable").classList.add("d-none");
    }
}