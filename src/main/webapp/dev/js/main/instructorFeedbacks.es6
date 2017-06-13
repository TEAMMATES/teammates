/* global prepareDatepickers:false linkAjaxForResponseRate:false readyFeedbackPage:false richTextEditorBuilder:false
setStatusMessage:false, appendStatusMessage:false, clearStatusMessages:false, bindEventsAfterAjax:false, StatusType:false
loadSessionsByAjax:false prepareRemindModal:false prepareInstructorPages:false
bindPublishButtons:false, bindRemindButtons:false, bindUnpublishButtons:false, bindDeleteButtons:false
linkAjaxForResponseRate:falsem addLoadingIndicator:false, setupFsCopyModal:false, bindCopyEvents:false
formatSessionVisibilityGroup:false, formatResponsesVisibilityGroup:false, collapseIfPrivateSession:false
bindUncommonSettingsEvents:false, updateUncommonSettingsInfo:false, showUncommonPanelsIfNotInDefaultValues:false

FEEDBACK_SESSION_STARTDATE:false, FEEDBACK_SESSION_STARTTIME:false, FEEDBACK_SESSION_TIMEZONE:false
COURSE_ID:false, FEEDBACK_SESSION_NAME:false, FEEDBACK_SESSION_COPY_INVALID:false
DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE:false
*/

let isSessionsAjaxSending = false;
let oldStatus = null;

const TIMEZONE_SELECT_UNINITIALISED = '-9999';

function isTimeZoneIntialized() {
    return $('#timezone').val() !== TIMEZONE_SELECT_UNINITIALISED;
}

/**
 * Format a number to be two digits
 */
function formatDigit(num) {
    return (num < 10 ? '0' : '') + num;
}

/**
 * Format a date object into DD/MM/YYYY format
 * @param date
 * @returns {String}
 */
function convertDateToDDMMYYYY(date) {
    return `${formatDigit(date.getDate())}/${formatDigit(date.getMonth() + 1)}/${date.getFullYear()}`;
}

/**
 * Format a date object into HHMM format
 * @param date
 * @returns {String}
 */
function convertDateToHHMM(date) {
    return formatDigit(date.getHours()) + formatDigit(date.getMinutes());
}

/**
 * To be run on page finish loading, this will select the input: start date,
 * start time, and timezone based on client's time.
 *
 * The default values will not be set if the form was submitted previously and
 * failed validation.
 */
function selectDefaultTimeOptions() {
    const now = new Date();

    const currentDate = convertDateToDDMMYYYY(now);
    const hours = convertDateToHHMM(now).substring(0, 2);
    const currentTime = parseInt(hours, 10) + 1;
    const timeZone = -now.getTimezoneOffset() / 60;

    if (!isTimeZoneIntialized()) {
        $(`#${FEEDBACK_SESSION_STARTDATE}`).val(currentDate);
        $(`#${FEEDBACK_SESSION_STARTTIME}`).val(currentTime);
        $(`#${FEEDBACK_SESSION_TIMEZONE}`).val(timeZone);
    }

    const uninitializedTimeZone = $(`#timezone > option[value='${TIMEZONE_SELECT_UNINITIALISED}']`);
    if (uninitializedTimeZone) {
        uninitializedTimeZone.remove();
    }
}

function bindCopyButton() {
    $('#button_copy').on('click', (e) => {
        e.preventDefault();
        const selectedCourseId = $(`#${COURSE_ID} option:selected`).text();
        const newFeedbackSessionName = $(`#${FEEDBACK_SESSION_NAME}`).val();

        let isExistingSession = false;

        const $sessionsList = $('tr[id^="session"]');
        if (!$sessionsList.length) {
            setStatusMessage(FEEDBACK_SESSION_COPY_INVALID, StatusType.DANGER);
            return false;
        }

        $sessionsList.each(function (ev) {
            const $cells = $(this).find('td');
            const courseId = $($cells[0]).text();
            const feedbackSessionName = $($cells[1]).text();
            if (selectedCourseId === courseId && newFeedbackSessionName === feedbackSessionName) {
                isExistingSession = true;
                ev.preventDefault();
            }
        });

        if (isExistingSession) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE, StatusType.DANGER);
        } else {
            clearStatusMessages();

            const $firstSession = $($sessionsList[0]).find('td');
            const firstSessionCourseId = $($firstSession[0]).text();
            const firstSessionName = $($firstSession[1]).text();

            $('#copyModal').modal('show');
            $('#modalCopiedSessionName').val(newFeedbackSessionName.trim());
            $('#modalCopiedCourseId').val(selectedCourseId.trim());
            const $modalCourseId = $('#modalCourseId');
            if (!$modalCourseId.val().trim()) {
                $modalCourseId.val(firstSessionCourseId);
            }
            const $modalSessionName = $('#modalSessionName');
            if (!$modalSessionName.val().trim()) {
                $modalSessionName.val(firstSessionName);
            }
        }

        return false;
    });

    $('#button_copy_submit').on('click', (e) => {
        e.preventDefault();
        const $newSessionName = $('#modalCopiedSessionName');
        if ($newSessionName.val()) {
            addLoadingIndicator($('#button_copy_submit'), 'Copying ');
            $('#copyModalForm').submit();
        } else {
            $newSessionName.addClass('text-box-error');
            $('#copyModal').animate({ scrollTop: $newSessionName.offset().top }, 500);
        }
        return false;
    });
}

function bindCopyEvents() {
    $('#copyTableModal > tbody > tr').on('click', function () {
        const $currentlySelectedRow = $(this);
        if ($currentlySelectedRow.hasClass('row-selected')) {
            return;
        }

        const $cells = $currentlySelectedRow.children('td');
        const courseId = $($cells[1]).text().trim();
        const feedbackSessionName = $($cells[2]).text().trim();
        $('#modalCourseId').val(courseId);
        $('#modalSessionName').val(feedbackSessionName);

        const $previouslySelectedRadio = $currentlySelectedRow.parent().find('input:checked');
        const $previouslySelectedRow = $previouslySelectedRadio.closest('tr');

        $previouslySelectedRadio.prop('checked', false);
        $previouslySelectedRow.removeClass('row-selected');

        const $currentlySelectedRadio = $currentlySelectedRow.children('td').children('input');
        $currentlySelectedRadio.prop('checked', true);
        $currentlySelectedRow.addClass('row-selected');

        $('#button_copy_submit').prop('disabled', false);
    });
}

function loadSessionsByAjax() {
    $('#ajaxForSessions').trigger('submit');
}

function bindEventsAfterAjax() {
    bindCopyButton();
    bindCopyEvents();
    linkAjaxForResponseRate();
    setupFsCopyModal();
}

const ajaxRequest = function (e) {
    e.preventDefault();

    if (isSessionsAjaxSending) {
        return;
    }

    const formData = $(this).serialize();
    $.ajax({
        type: 'POST',
        cache: false,
        url: `${$(this).attr('action')}?${formData}`,
        beforeSend() {
            isSessionsAjaxSending = true;
            $('#sessionList').html('<img height="75" width="75" class="margin-center-horizontal" '
                                   + 'src="/images/ajax-preload.gif"/>');
        },
        error() {
            isSessionsAjaxSending = false;
            $('#sessionList').html('');
            $('#loadSessionsFailErrorMsg').on('click', loadSessionsByAjax);
            const msg = 'Failed to load sessions. '
                    + 'Please <a href="#" id="loadSessionsFailErrorMsg">click here</a> to retry.';
            setStatusMessage(msg, StatusType.DANGER);

            if (oldStatus !== null && oldStatus !== undefined && oldStatus !== '') {
                appendStatusMessage(oldStatus);
            }
        },
        success(data) {
            clearStatusMessages();
            appendStatusMessage(oldStatus);

            const appendedModalBody = $(data).find('#copySessionsBody').html();
            const appendedSessionTable = $(data).find('#sessionList').html();

            $('#button_copy').text('Copy from previous feedback sessions');
            $('#copySessionsBody').html(appendedModalBody);
            $('#sessionList').removeClass('align-center')
                             .html(appendedSessionTable);
            bindEventsAfterAjax();
        },
    });
};

function readyFeedbackPage() {
    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();
    collapseIfPrivateSession();

    selectDefaultTimeOptions();
    loadSessionsByAjax();
    bindUncommonSettingsEvents();

    bindDeleteButtons();
    bindRemindButtons();
    bindPublishButtons();
    bindUnpublishButtons();

    updateUncommonSettingsInfo();
    showUncommonPanelsIfNotInDefaultValues();
}

$(document).ready(() => {
    prepareInstructorPages();

    oldStatus = $('.statusMessage').clone();
    $('#ajaxForSessions').submit(ajaxRequest);

    prepareDatepickers();
    linkAjaxForResponseRate();

    prepareRemindModal();

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor('#instructions', {
            inline: true,
            readonly: false,
        });
        /* eslint-enable camelcase */
    }

    readyFeedbackPage();
});
