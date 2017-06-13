/**
 * Contains functions related to student MOTD.
 */
function fetchMotd(motdUrl, motdContentSelector, motdContainerSelector) {
    $.ajax({
        type: 'GET',
        url: `${window.location.origin}/${motdUrl}`,
        success(data) {
            $(motdContentSelector).html(data);
        },
        error() {
            $(motdContainerSelector).html('');
        },
    });
}

function bindCloseMotdButton(btnSelector, motdContainerSelector) {
    $(document).on('click', btnSelector, () => {
        $(motdContainerSelector).hide();
    });
}

$(document).ready(() => {
    const motdUrl = $('#motd-url').val();
    fetchMotd(motdUrl, '#student-motd', '#student-motd-container');
    bindCloseMotdButton('#btn-close-motd', '#student-motd-container');
});

/*
export default {
    fetchMotd,
    bindCloseMotdButton,
};
*/
/* exported fetchMotd, bindCloseMotdButton */
