/* global toggleSort:false selectElementContents:false attachEventToDeleteStudentLink:false setStatusMessage:false */
/* global BootboxWrapper:false StatusType:false prepareInstructorPages:false prepareComments:false */

function submitFormAjax() {
    const formObject = $('#csvToHtmlForm');
    const formData = formObject.serialize();
    const content = $('#detailsTable');
    const ajaxStatus = $('#ajaxStatus');

    const retryButtonHtml = '<button class="btn btn-info" id="instructorCourseDetailsRetryButton"> retry</button>';
    $('#instructorCourseDetailsRetryButton').on('click', submitFormAjax);

    $.ajax({
        type: 'POST',
        url: `/page/instructorCourseDetailsPage?${formData}`,
        beforeSend() {
            content.html("<img src='/images/ajax-loader.gif'/>");
        },
        error() {
            ajaxStatus.html('Failed to load student table. Please try again.');
            content.html(retryButtonHtml);
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    ajaxStatus.html(data.errorMessage);
                    content.html(retryButtonHtml);
                } else {
                    const table = data.studentListHtmlTableAsString;
                    content.html(`<small>${table}</small>`);
                }

                setStatusMessage(data.statusForAjax);
            }, 500);
        },
    });
}

function attachEventToRemindStudentsButton() {
    $('#button_remind').on('click', (event) => {
        const $clickedButton = $(event.target);
        const messageText = `${'Usually, there is no need to use this feature because TEAMMATES sends an automatic '
                          + 'invite to students at the opening time of each session. Send a join request to '
                          + 'all yet-to-join students in '}${$clickedButton.data('courseId')} anyway?`;
        const okCallback = function okCallback() {
            window.location = $clickedButton.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Confirm sending join requests', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });
}

function attachEventToSendInviteLink() {
    $('.course-student-remind-link').on('click', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.target);
        const messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an automatic '
                          + 'invite to students at the opening time of each session. Send a join request anyway?';
        const okCallback = function okCallback() {
            $.get($clickedLink.attr('href'), () => {
                const studentEmail = $clickedLink.parent().siblings("td[id|='studentemail']").html().trim();
                const message = `An email has been sent to ${studentEmail}`;
                setStatusMessage(message, 'success');
            });
        };

        BootboxWrapper.showModalConfirmation('Confirm sending join request', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.INFO);
    });
}

$(document).ready(() => {
    prepareInstructorPages();
    prepareComments();

    if ($('#button_sortstudentsection').length) {
        toggleSort($('#button_sortstudentsection'));
    } else {
        toggleSort($('#button_sortstudentteam'));
    }

    // auto select the html table when modal is shown
    $('#studentTableWindow').on('shown.bs.modal', () => {
        selectElementContents(document.getElementById('detailsTable'));
    });

    attachEventToRemindStudentsButton();
    attachEventToSendInviteLink();
    attachEventToDeleteStudentLink();
});

/*
export default {
    submitFormAjax,
};
*/
