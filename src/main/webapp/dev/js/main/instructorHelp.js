/**
 * If the incoming link to this site targets a panel,
 * expands the targeted collapsed panel
 */
function openAnchorPanel() {
    if (window.location.hash) {
        const target = $('body').find(window.location.hash);
        if ($(target).hasClass('panel')) {
            const targetCollapse = $(target).find('.collapse');
            $(targetCollapse).collapse('show');
        }
    }
}

/**
 * To be used by links on the instructorHelp.jsp page itself.
 * If a link is of the class 'collapse-link', expands the
 * collapse with the ID 'data-target'.
 */
function handleCollapseLinks() {
    $('.collapse-link').on('click', function () {
        const targetPanel = this.getAttribute('data-target');
        $(targetPanel).collapse('show');
    });
}

$(document).ready(() => {
    openAnchorPanel();
    handleCollapseLinks();
});
