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
    prepareResendPublishedEmailModal,
} from '../common/resendPublishedEmailModal';

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

import {
    countRemainingCharactersOnInput,
} from '../common/countRemainingCharactersOnInput';

let isSessionsAjaxSending = false;
let oldStatus = null;

const DISPLAY_FEEDBACK_SESSION_COPY_INVALID = 'There is no feedback session to be copied.';
const DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE =
        'This feedback session name already existed in this course. Please use another name.';

/**
 * To be run on page finish loading. This will fill the start date and
 * start time inputs based on the client's time.
 *
 * The default values will not be set if the form was submitted previously and
 * failed validation.
 */
function selectDefaultStartDateTime() {
    const isFormSubmittedPreviously = $(`#${ParamsNames.FEEDBACK_SESSION_TIMEZONE}`).data('timeZone');
    if (isFormSubmittedPreviously) {
        return;
    }

    const now = new Date();

    /*
     * A workaround to hide the datepicker which opens up at the bottom of the page
     * when setting the start date using the datepicker.
     */
    $('#ui-datepicker-div').css('display', 'none');

    $(`#${ParamsNames.FEEDBACK_SESSION_STARTDATE}`).datepicker('setDate', now);
    $(`#${ParamsNames.FEEDBACK_SESSION_STARTTIME}`).val(now.getHours() + 1);
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

            $('#copyModal').modal('show');
        }
        countRemainingCharactersOnInput('modalCopiedSessionName');

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

function escapeXml(unsafe) {
    return (unsafe || '').replace(/&/g, '&amp;').replace(/</g, '&lt;')
            .replace(/>/g, '&gt;').replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
}

function updateCourseNameAndTimeZoneFromSelection() {
    const selectedCourseId = $(`#${ParamsNames.COURSE_ID}`).val();
    if (!selectedCourseId) {
        return;
    }
    const selectedCourseData = $('.course-attributes-data').data(selectedCourseId);
    $(`#${ParamsNames.COURSE_NAME}`).html(escapeXml(selectedCourseData.name));
    $(`#${ParamsNames.FEEDBACK_SESSION_TIMEZONE}`).html(selectedCourseData.timeZone);
}

function initializeCourseName() {
    $('.course-attributes-data').each((idx, obj) => {
        const $obj = $(obj);
        $('.course-attributes-data').data(obj.id, { name: $obj.data('name'), timeZone: $obj.data('timeZone') });
    });
    updateCourseNameAndTimeZoneFromSelection();
}

function bindSelectField() {
    $(`#${ParamsNames.COURSE_ID}`).change(updateCourseNameAndTimeZoneFromSelection);
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

function bindCollapseEvents() {
    $('body').on('mouseover', '.panel-heading', () => {
        $('.panel-heading').css('cursor', 'pointer');
    });

    $('body').on('click', '.panel-heading', (event) => {
        if ($(event.target).hasClass('ajax_submit')) {
            const panel = $(event.currentTarget);
            const toggleChevronDown = $(panel[0]).find('.glyphicon-chevron-down');
            const toggleChevronUp = $(panel[0]).find('.glyphicon-chevron-up');

            if (toggleChevronDown.length === 0) {
                $(toggleChevronUp[0]).addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
            } else {
                $(toggleChevronDown[0]).addClass('glyphicon-chevron-up').removeClass('glyphicon-chevron-down');
            }

            $('.panel-collapse').collapse('toggle');
        }
    });
}

function readyFeedbackPage() {
    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();

    selectDefaultStartDateTime();
    loadSessionsByAjax();
    bindUncommonSettingsEvents();

    bindDeleteButtons();
    bindRemindButtons();
    bindPublishButtons();
    bindUnpublishButtons();

    initializeCourseName();
    bindSelectField();

    updateUncommonSettingsInfo();
    showUncommonPanelsIfNotInDefaultValues();

    bindCollapseEvents();
}

$(document).ready(() => {
    prepareInstructorPages();

    oldStatus = $('.statusMessage').clone();
    $('#ajaxForSessions').submit(ajaxRequest);

    prepareDatepickers();
    linkAjaxForResponseRate();

    prepareRemindModal();
    prepareResendPublishedEmailModal();

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor('#instructions', {
            inline: true,
            readonly: false,
        });
        /* eslint-enable camelcase */
    }

    countRemainingCharactersOnInput(ParamsNames.FEEDBACK_SESSION_NAME);

    readyFeedbackPage();
});

window.setIsSessionsAjaxSendingFalse = () => {
    isSessionsAjaxSending = false;
};
