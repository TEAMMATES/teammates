import {
    showModalConfirmation,
} from './bootboxWrapper';

import {
    scrollToElement,
} from './scrollTo';

import {
    BootstrapContextualColors,
    ParamsNames,
} from './const';
import {
    setStatusMessage,
} from './statusMessage';

import {
    countRemainingCharactersOnInput,
} from './countRemainingCharactersOnInput';

import {
    TimeZone,
} from './timezone';

/*
 * This JavaScript file is included in all instructor pages. Functions here
 * should be common to some/all instructor pages.
 */

// -----------------------------------------------------------------------------

function setupFsCopyModal() {
    $('#fsCopyModal').on('show.bs.modal', (event) => {
        const button = $(event.relatedTarget); // Button that triggered the modal
        const actionlink = button.data('actionlink');
        const courseid = button.data('courseid');
        const fsname = button.data('fsname');
        const appUrl = window.location.origin;
        const currentPage = window.location.href.substring(appUrl.length); // use the page's relative URL

        $.ajax({
            type: 'GET',
            url: `${actionlink}&courseid=${encodeURIComponent(courseid)}&fsname=${encodeURIComponent(fsname)}`
                    + `&currentPage=${encodeURIComponent(currentPage)}`,
            beforeSend() {
                $('#fscopy_submit').prop('disabled', true);
                $('#courseList').html('Loading possible destination courses. Please wait ...<br>'
                                      + "<img class='margin-center-horizontal' src='/images/ajax-loader.gif'/>");
            },
            error() {
                $('#courseList').html("<p id='fs-copy-modal-error'>Error retrieving course list."
                                      + 'Please close the dialog window and try again.</p>');
            },
            success(data) {
                $('#courseList').html(data);
                // If the user alt-clicks, the form does not send any parameters and results in an error.
                // Prevent default form submission and submit using jquery.
                $('#fscopy_submit').off('click')
                        .on('click', (e) => {
                            $('#fscopy_submit').prop('disabled', true);
                            e.preventDefault();
                            $('#fscopy_submit').closest('form').submit();
                        });
                $('#fscopy_submit').prop('disabled', false);
                countRemainingCharactersOnInput(ParamsNames.COPIED_FEEDBACK_SESSION_NAME);
            },
        });
    });

    $('#instructorCopyModalForm').submit(function (e) {
        e.preventDefault();
        const $this = $(this);

        const $copyModalStatusMessage = $('#feedback-copy-modal-status');

        $.ajax({
            type: 'POST',
            url: $this.prop('action'),
            data: $this.serialize(),
            beforeSend() {
                $copyModalStatusMessage.removeClass('alert alert-danger');
                $copyModalStatusMessage.html('<img src="/images/ajax-loader.gif" class="margin-center-horizontal">');
            },
            error() {
                $copyModalStatusMessage.addClass('alert alert-danger');
                $copyModalStatusMessage.text('There was an error during submission. '
                                                 + 'Please close the dialog window and try again.');
            },
            success(data) {
                const isError = data.errorMessage !== '';
                if (!isError && data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    $copyModalStatusMessage.addClass('alert alert-danger');
                    $copyModalStatusMessage.text(data.errorMessage);
                    $('#fscopy_submit').prop('disabled', false);
                }
            },
        });
    });
}

function initializeTimeZoneOptions($selectElement) {
    if (typeof moment !== 'undefined') {
        TimeZone.prepareTimeZoneInput($selectElement);

        const existingTimeZone = $selectElement.data('timeZone');
        if (existingTimeZone) {
            TimeZone.updateTimeZone($selectElement, existingTimeZone);
        } else {
            TimeZone.autoDetectAndUpdateTimeZone($selectElement);
        }

        $('#auto-detect-time-zone').on('click', () => {
            TimeZone.autoDetectAndUpdateTimeZone($selectElement);
        });
    }
}

// Student Profile Picture
// --------------------------------------------------------------------------

/**
 * updates all the student names that show profile picture
 * on hover with the resolved link after one instance of the name
 * has been loaded<br>
 * Helps to avoid clicking view photo when hovering over names of
 * students whose picture has already been loaded elsewhere in the page
 * @param link
 * @param resolvedLink
 */
function updateHoverShowPictureEvents(actualLink, resolvedLink) {
    $(`.profile-pic-icon-hover[data-link="${actualLink}"]`)
            .attr('data-link', '')
            .off('mouseenter mouseleave')
            .popover('destroy')
            .popover({
                html: true,
                trigger: 'manual',
                placement: 'top',
                content() {
                    return `<img class="profile-pic" src="${resolvedLink}">`;
                },
            })
            .mouseenter(function () {
                $(this).popover('show');
                $(this).siblings('.popover').on('mouseleave', function () {
                    $(this).siblings('.profile-pic-icon-hover').popover('hide');
                });
                $(this).mouseleave(function () {
                    // this is so that the user can hover over the
                    // pop-over photo without hiding the photo
                    setTimeout((obj) => {
                        if ($(obj).siblings('.popover').find(':hover').length === 0) {
                            $(obj).popover('hide');
                        }
                    }, 200, this);
                });
            })
            .children('img[src=""]')
            .attr('src', resolvedLink);
}

/**
 * @param elements:
 * identifier that points to elements with
 * class: student-profile-pic-view-link
 */
function bindStudentPhotoLink(elements) {
    $(elements).on('click', function (e) {
        const event = e || window.event;

        event.cancelBubble = true;

        if (event.stopPropagation) {
            event.stopPropagation();
        }

        const actualLink = $(this).parent().attr('data-link');
        const $loadingImage = $('<img>').attr('src', '/images/ajax-loader.gif')
                .addClass('center-block margin-top-7px');

        $(this).siblings('img').attr('src', actualLink).load(function () {
            const resolvedLink = $(this).attr('src');

            $loadingImage.remove();

            $(this).removeClass('hidden')
                    .parent().attr('data-link', '')
                    .popover({
                        html: true,
                        trigger: 'manual',
                        placement: 'top',
                        content() {
                            return `<img class="profile-pic" src="${resolvedLink}">`;
                        },
                    })
                    .mouseenter(function () {
                        $(this).popover('show');
                        $(this).siblings('.popover').on('mouseleave', function () {
                            $(this).siblings('.profile-pic-icon-click').popover('hide');
                        });
                        $(this).mouseleave(function () {
                            // this is so that the user can hover over the
                            // pop-over photo without hiding the photo
                            setTimeout((obj) => {
                                if ($(obj).siblings('.popover').find(':hover').length === 0) {
                                    $(obj).popover('hide');
                                }
                            }, 200, this);
                        });
                    });

            updateHoverShowPictureEvents(actualLink, resolvedLink);
        });

        const $imageCell = $(this).closest('td');
        $(this).remove();
        $imageCell.append($loadingImage);
    });
}

/**
 * completes the loading cycle for showing profile picture
 * for a onhover event
 * @param link
 * @param resolvedLink
 */
function loadProfilePictureForHoverEvent(obj) {
    obj.children('img')[0].src = obj.attr('data-link');

    const $loadingImage = $('<img>').attr('src', '/images/ajax-loader.gif');

    // load the pictures in all similar links
    obj.children('img').load(function () {
        const actualLink = $(this).parent().attr('data-link');
        const resolvedLink = $(this).attr('src');

        $loadingImage.remove();

        updateHoverShowPictureEvents(actualLink, resolvedLink);

        // this is to show the picture immediately for the one
        // the user just clicked on
        $(this).parent()
                .popover('show')
                // this is to handle the manual hide action of the popover
                .siblings('.popover')
                .on('mouseleave', function () {
                    $(this).siblings('.profile-pic-icon-hover').popover('hide');
                });
    });

    obj.popover('destroy').popover({
        html: true,
        trigger: 'manual',
        placement: 'top',
        content() {
            return $loadingImage.get(0).outerHTML;
        },
    });
    obj.popover('show');
}

/**
 * @param elements:
 * identifier that points to elements with
 * class: profile-pic-icon-hover
 */
function bindStudentPhotoHoverLink(elements) {
    $(elements)
            .mouseenter(function () {
                $(this).popover('show');
                $(this).siblings('.popover').on('mouseleave', function () {
                    $(this).siblings('.profile-pic-icon-hover').popover('hide');
                });
            })
            .mouseleave(function () {
                // this is so that the user can hover over the
                // pop-over without accidentally hiding the 'view photo' link
                setTimeout((obj) => {
                    if ($(obj).siblings('.popover').find('.profile-pic').length !== 0
                    || $(obj).siblings('.popover').find(':hover').length === 0) {
                        $(obj).popover('hide');
                    }
                }, 200, this);
            });

    // bind the default popover event for the
    // show picture onhover events
    $(elements).popover({
        html: true,
        trigger: 'manual',
        placement: 'top',
        content() {
            $('body').on('click', '.cursor-pointer', (event) => {
                const toLoad = $(event.currentTarget).closest('.popover').siblings('.profile-pic-icon-hover');
                loadProfilePictureForHoverEvent(toLoad);
            });
            return '<a class="cursor-pointer">View Photo</a>';
        },
    });
}

function bindDeleteButtons() {
    $('body').on('click', '.session-delete-for-test', (event) => {
        event.preventDefault();

        const $button = $(event.currentTarget);
        const courseId = $button.data('courseid');
        const feedbackSessionName = $button.data('fsname');

        const messageText = `Are you want to delete the feedback session ${feedbackSessionName} in ${courseId}?`;
        const okCallback = function () {
            window.location = $button.attr('href');
        };

        showModalConfirmation('Confirm deleting feedback session', messageText, okCallback, null,
                null, null, BootstrapContextualColors.DANGER);
    });
}

function bindCourseDeleteLinks() {
    $('body').on('click', '.course-delete-link', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.currentTarget);
        const messageText = `Are you sure you want to delete the course: ${$clickedLink.data('courseId')}? `
                          + 'This operation will delete all students and sessions in this course. '
                          + 'All instructors of this course will not be able to access it hereafter as well.';
        const okCallback = function () {
            window.location = $clickedLink.attr('href');
        };

        showModalConfirmation('Confirm deleting course', messageText, okCallback, null,
                null, null, BootstrapContextualColors.DANGER);
    });
}

function bindSessionDeleteLinks() {
    $('body').on('click', '#fsDeleteLink', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.currentTarget);
        const messageText = 'Are you sure you want to delete the feedback session '
                + `${$clickedLink.data('feedbackSessionName')} in ${$clickedLink.data('courseId')}?`;
        const okCallback = function () {
            window.location = $clickedLink.attr('href');
        };

        showModalConfirmation('Confirm deleting feedback session', messageText, okCallback, null,
                null, null, BootstrapContextualColors.DANGER);
    });
}

function attachEventToSendInviteLink() {
    $(document).on('click', '.course-student-remind-link', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.currentTarget);
        const messageText = 'Usually, there is no need to use this feature because TEAMMATES sends an automatic '
                + 'invite to students at the opening time of each session. Send a join request anyway?';
        const okCallback = function okCallback() {
            $.get($clickedLink.attr('href'), () => {
                const studentEmail = $clickedLink.parent().siblings("td[id|='studentemail']").html().trim();
                const message = `An email has been sent to ${studentEmail}`;
                setStatusMessage(message, 'success');
            });
        };

        showModalConfirmation('Confirm sending join request', messageText, okCallback, null,
                null, null, BootstrapContextualColors.INFO);
    });
}

function attachEventToDeleteStudentLink() {
    $(document).on('click', '.course-student-delete-link', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.currentTarget);
        const messageText = `Are you sure you want to remove ${$clickedLink.data('studentName')}`
                + ` from the course ${$clickedLink.data('courseId')}?`;
        const okCallback = function () {
            window.location = $clickedLink.attr('href');
        };

        showModalConfirmation('Confirm deletion', messageText, okCallback, null,
                null, null, BootstrapContextualColors.DANGER);
    });
}

function sendRemindersToStudents(urlLink) {
    const $statusMessage = $('#statusMessagesToUser');
    $.ajax({
        type: 'POST',
        url: urlLink,
        beforeSend() {
            $statusMessage.html('<img src="/images/ajax-loader.gif">');
            $statusMessage.css('display', 'block');
        },
        error() {
            $statusMessage.html('An error has occurred while sending reminder. Please try again.');
        },
        success(data) {
            const statusToUser = $(data).find('#statusMessagesToUser').html();
            $statusMessage.html(statusToUser);

            scrollToElement($statusMessage[0], { duration: 1000 });
        },
    });
}

function resendPublishedEmailToStudents(urlLink) {
    const $statusMessage = $('#statusMessagesToUser');
    $.ajax({
        type: 'POST',
        url: urlLink,
        beforeSend() {
            $statusMessage.html('<img src="/images/ajax-loader.gif">');
            $statusMessage.css('display', 'block');
        },
        error() {
            $statusMessage.html('An error has occurred while requesting for emails to be resent. Please try again.');
        },
        success(data) {
            const statusToUser = $(data).find('#statusMessagesToUser').html();
            $statusMessage.html(statusToUser);

            scrollToElement($statusMessage[0], { duration: 1000 });
        },
    });
}

function attachEventToDeleteAllStudentLink() {
    $('body').on('click', '.course-student-delete-all-link', (event) => {
        event.preventDefault();

        const $clickedLink = $(event.currentTarget);
        const messageText = `Are you sure you want to remove all students
                from the course ${$clickedLink.data('courseId')}?`;
        const okCallback = () => {
            window.location = $clickedLink.attr('href');
        };

        showModalConfirmation('Confirm deletion', messageText, okCallback, null,
                null, null, BootstrapContextualColors.DANGER);
    });
}

function bindRemindButtons() {
    $('body').on('click', '.session-remind-inner-for-test, .session-remind-for-test', (event) => {
        event.preventDefault();

        const $button = $(event.currentTarget);
        const messageText = 'Send e-mails to remind students who have not submitted their feedback for '
                + `${$button.data('fsname')}?`;
        const okCallback = function () {
            const urlLink = $button.attr('href');
            sendRemindersToStudents(urlLink);
        };

        showModalConfirmation('Confirm sending reminders', messageText, okCallback, null,
                null, null, BootstrapContextualColors.INFO);
    });
}

function bindPublishButtons() {
    $('body').on('click', '.session-publish-for-test', function (event) {
        event.preventDefault();

        const $button = $(this);
        const feedbackSessionName = $button.data('fsname');
        let messageText = `Are you sure you want to publish the responses for the session "${feedbackSessionName}"?`;

        const isSendingPublishedEmail = $button.data('sending-published-email');
        if (isSendingPublishedEmail) {
            messageText += ' An email will be sent to students to inform them that the responses are ready for viewing.';
        }

        const okCallback = function () {
            window.location = $button.attr('href');
        };

        showModalConfirmation('Confirm publishing responses', messageText, okCallback, null,
                null, null, BootstrapContextualColors.WARNING);
    });
}

function bindUnpublishButtons() {
    $('body').on('click', '.session-unpublish-for-test', (event) => {
        event.preventDefault();

        const $button = $(event.currentTarget);
        const messageText = `Are you sure you want to unpublish the session "${$button.data('fsname')}"?
                             An email will be sent to students to inform them that the session has been unpublished
                             and the session responses will no longer be viewable by students.`;
        const okCallback = function () {
            window.location = $button.attr('href');
        };

        showModalConfirmation('Confirm unpublishing responses', messageText, okCallback, null,
                null, null, BootstrapContextualColors.WARNING);
    });
}

// --------------------------------------------------------------------------

/**
 * Selects contents inside an element.
 * @param {HTML DOM Object} elementNode The element to select contents from.
 */
function selectElementContents(elementNode) {
    const { body } = document;
    let range;
    if (document.createRange && window.getSelection) {
        range = document.createRange();
        const selection = window.getSelection();
        selection.removeAllRanges();
        try {
            range.selectNodeContents(elementNode);
            selection.addRange(range);
        } catch (e) {
            range.selectNode(elementNode);
            selection.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(elementNode);
        range.select();
    }
}

/**
 * Simulates the copy action in the right-click menu, typically 'Ctrl + C'.
 */
function executeCopyCommand() {
    document.execCommand('copy');
}

// Initial load-up
// -----------------------------------------------------------------------------

function prepareInstructorPages() {
    // bind the show picture onclick events
    bindStudentPhotoLink('.profile-pic-icon-click > .student-profile-pic-view-link');

    // bind the show picture onhover events
    bindStudentPhotoHoverLink('.profile-pic-icon-hover');

    // bind the event handler to show confirmation modal
    bindCourseDeleteLinks();
    bindSessionDeleteLinks();
}

export {
    attachEventToDeleteStudentLink,
    attachEventToDeleteAllStudentLink,
    attachEventToSendInviteLink,
    bindDeleteButtons,
    bindPublishButtons,
    bindRemindButtons,
    bindStudentPhotoHoverLink,
    bindStudentPhotoLink,
    bindUnpublishButtons,
    executeCopyCommand,
    initializeTimeZoneOptions,
    prepareInstructorPages,
    resendPublishedEmailToStudents,
    selectElementContents,
    setupFsCopyModal,
    sendRemindersToStudents,
};
