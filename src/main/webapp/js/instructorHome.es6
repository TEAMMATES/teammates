/* global bindDeleteButtons:false bindRemindButtons:false bindPublishButtons:false */
/* global bindUnpublishButtons:false setupFsCopyModal:false BootboxWrapper:false */
/* global StatusType:false global linkAjaxForResponseRate:false prepareRemindModal:false prepareInstructorPages:false */

const COURSE_PANELS_TO_AUTO_LOAD_COUNT = 3;
const CURRENT_YEAR = (new Date()).getFullYear();

$(document).ready(() => {
    prepareInstructorPages();
    bindDeleteButtons();
    bindRemindButtons();
    bindPublishButtons();
    bindUnpublishButtons();

    setupFsCopyModal();

    // Click event binding for radio buttons
    const $radioButtons = $('label[name="sortby"]');
    $.each($radioButtons, function () {
        $(this).click(function () {
            const currentPath = window.location.pathname;
            const query = window.location.search.substring(1);
            const params = {};

            const paramValues = query.split('&');
            for (let i = 0; i < paramValues.length; i += 1) {
                const paramValue = paramValues[i].split('=');
                params[paramValue[0]] = paramValue[1];
            }

            if ('user' in params === false) {
                params.user = $('input[name="user"]').val();
            }

            window.location.href = `${currentPath}?user=${params.user}&sortby=${$(this).attr('data')}`;
        });
    });

    // Click event binding for course archive button
    $('body').on('click', '.course-archive-for-test', (event) => {
        event.preventDefault();
        const $clickedLink = $(event.target);

        const messageText = `Are you sure you want to archive ${$clickedLink.data('courseId')}? `
            + 'This action can be reverted by going to the "courses" tab and unarchiving the desired course(s).';
        const okCallback = function () {
            window.location = $clickedLink.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Confirm archiving course', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });

    // AJAX loading of course panels
    const $coursePanels = $('div[id|="course"]');
    $.each($coursePanels, function () {
        $(this).filter(function () {
            const isNotLoaded = $(this).find('form').length;
            return isNotLoaded;
        }).click(function () {
            const $panel = $(this);
            const formData = $panel.find('form').serialize();
            const content = $panel.find('.pull-right')[0];

            $.ajax({
                type: 'POST',
                url: `/page/instructorHomePage?${formData}`,
                beforeSend() {
                    $(content).html("<img src='/images/ajax-loader.gif'/>");
                },
                error() {
                    const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
                    let errorMsg = '[ Failed to load. Click here to retry. ]';
                    errorMsg = `<strong style="margin-left: 1em; margin-right: 1em;">${errorMsg}</strong>`;
                    const chevronDown = '<span class="glyphicon glyphicon-chevron-down"></span>';
                    $(content).html(warningSign + errorMsg + chevronDown);
                },
                success(data) {
                    // .outerHTML is used instead of jQuery's .replaceWith() to avoid the <span>
                    // for statuses' tooltips from being closed due to the presence of <br>
                    $panel[0].outerHTML = data;
                    linkAjaxForResponseRate();
                },
            });
        });
    });

    // Automatically load top few course panels
    $coursePanels.slice(0, COURSE_PANELS_TO_AUTO_LOAD_COUNT).click();

    linkAjaxForResponseRate();

    prepareRemindModal();
});

/**
 * This is the comparator that is used for sorting start and end times on the InstructorHome page
 * @param x
 * @param y
 * @returns 1 if Date x is after y, 0 if same and -1 if before
 */
function instructorHomeDateComparator(x, y) {
    const x0 = Date.parse(x);
    const y0 = Date.parse(y);
    if (x0 > y0) {
        return 1;
    }
    return x0 < y0 ? -1 : 0;
}
