import {
    linkAjaxForResponseRate,
} from '../common/ajaxResponseRate';

import {
    ParamsNames,
    BootstrapContextualColors,
} from '../common/const';

import {
    prepareDatepickers,
} from '../common/datepicker';

import {
    bindDeleteButtons,
    bindPublishButtons,
    bindRemindButtons,
    bindUnpublishButtons,
    prepareInstructorPages,
    setupFsCopyModal,
} from '../common/instructor';

import {
    bindUncommonSettingsEvents,
    collapseIfPrivateSession,
    formatResponsesVisibilityGroup,
    formatSessionVisibilityGroup,
    showUncommonPanelsIfNotInDefaultValues,
    updateUncommonSettingsInfo,
} from '../common/instructorFeedbacks';

import {
    prepareRemindModal,
} from '../common/remindModal';

import {
    richTextEditorBuilder,
} from '../common/richTextEditor';

import {
    appendStatusMessage,
    clearStatusMessages,
    setStatusMessage,
} from '../common/statusMessage';

import {
    addLoadingIndicator,
} from '../common/ui';

let isSessionsAjaxSending = false;
let oldStatus = null;

const TIMEZONE_SELECT_UNINITIALISED = '-9999';

const DISPLAY_FEEDBACK_SESSION_COPY_INVALID = 'There is no feedback session to be copied.';
const DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE =
        'This feedback session name already existed in this course. Please use another name.';

function isTimeZoneIntialized() {
    return $('#timezone').val() !== TIMEZONE_SELECT_UNINITIALISED;
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

    if (!isTimeZoneIntialized()) {
        /*
         * A workaround to hide the datepicker which opens up at the bottom of the page
         * when setting the start date using the datepicker.
         */
        $('#ui-datepicker-div').css('display', 'none');

        $(`#${ParamsNames.FEEDBACK_SESSION_STARTDATE}`).datepicker('setDate', now);
        $(`#${ParamsNames.FEEDBACK_SESSION_STARTTIME}`).val(now.getHours() + 1);
        $(`#${ParamsNames.FEEDBACK_SESSION_TIMEZONE}`).val(-now.getTimezoneOffset() / 60);
    }

    const uninitializedTimeZone = $(`#timezone > option[value='${TIMEZONE_SELECT_UNINITIALISED}']`);
    if (uninitializedTimeZone) {
        uninitializedTimeZone.remove();
    }
}

function bindCopyButton() {
    $('#button_copy').on('click', (e) => {
        e.preventDefault();
        const selectedCourseId = $(`#${ParamsNames.COURSE_ID} option:selected`).text();
        const newFeedbackSessionName = $(`#${ParamsNames.FEEDBACK_SESSION_NAME}`).val();

        let isExistingSession = false;

        const $sessionsList = $('tr[id^="session"]');
        if (!$sessionsList.length) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_COPY_INVALID, BootstrapContextualColors.DANGER);
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
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE, BootstrapContextualColors.DANGER);
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
                    + 'Please <a href="javascript:;" id="loadSessionsFailErrorMsg">click here</a> to retry.';
            setStatusMessage(msg, BootstrapContextualColors.DANGER);

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

window.setIsSessionsAjaxSendingFalse = () => {
    isSessionsAjaxSending = false;
};
