/* global
StatusType:false richTextEditorBuilder:false setStatusMessage:false setStatusMessageToForm:false
Const:false clearStatusMessages:false bindPublishButtons:false bindRemindButtons:false
bindUnpublishButtons:false bindDeleteButtons:false linkAjaxForResponseRate:false
updateNumScalePossibleValues:false addLoadingIndicator:false setupFsCopyModal:false

FEEDBACK_QUESTION_RECIPIENTTYPE:false, FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE:false, FEEDBACK_QUESTION_TEXT:false
DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID:false, DISPLAY_FEEDBACK_QUESTION_TEXTINVALID:false
FEEDBACK_QUESTION_TYPE:false, FEEDBACK_SESSION_STARTDATE:false, FEEDBACK_SESSION_STARTTIME:false
FEEDBACK_QUESTION_NUMSCALE_MIN:false, FEEDBACK_QUESTION_NUMSCALE_MAX:false, FEEDBACK_QUESTION_NUMSCALE_STEP:false
DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID:false, DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID:false
DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID:false, DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID:false
FEEDBACK_SESSION_TIMEZONE:false, COURSE_ID:false, FEEDBACK_SESSION_NAME:false, FEEDBACK_SESSION_COPY_INVALID:false
DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE:false, FEEDBACK_SESSION_SESSIONVISIBLEBUTTON:false
FEEDBACK_SESSION_RESULTSVISIBLEBUTTON:false, FEEDBACK_SESSION_VISIBLEDATE:false, FEEDBACK_SESSION_VISIBLETIME:false
FEEDBACK_SESSION_PUBLISHDATE:false, FEEDBACK_SESSION_PUBLISHTIME:false
*/

// TODO: Move constants from Common.js into appropriate files if not shared.
const TIMEZONE_SELECT_UNINITIALISED = '-9999';

function extractQuestionNumFromEditFormId(id) {
    return parseInt(id.substring('form_editquestion-'.length, id.length), 10);
}

function getQuestionNumFromEditForm(form) {
    if ($(form).attr('name') === 'form_addquestions') {
        return -1;
    }
    return extractQuestionNumFromEditFormId($(form).attr('id'));
}

/**
 * Check whether the feedback question input is valid
 * @param form
 * @returns {Boolean}
 */
function checkFeedbackQuestion(form) {
    const recipientType = $(form).find(`select[name|=${FEEDBACK_QUESTION_RECIPIENTTYPE}]`)
                               .find(':selected')
                               .val();
    if (recipientType === 'STUDENTS' || recipientType === 'TEAMS') {
        if ($(form).find(`[name|=${FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE}]:checked`).val() === 'custom'
                && !$(form).find('.numberOfEntitiesBox').val()) {
            setStatusMessageToForm(DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID, StatusType.DANGER, form);
            return false;
        }
    }
    if (!$(form).find(`[name=${FEEDBACK_QUESTION_TEXT}]`).val()) {
        setStatusMessageToForm(DISPLAY_FEEDBACK_QUESTION_TEXTINVALID, StatusType.DANGER, form);
        return false;
    }
    if ($(form).find(`[name=${FEEDBACK_QUESTION_TYPE}]`).val() === 'NUMSCALE') {
        if (!$(form).find(`[name=${FEEDBACK_QUESTION_NUMSCALE_MIN}]`).val()
                || !$(form).find(`[name=${FEEDBACK_QUESTION_NUMSCALE_MAX}]`).val()
                || !$(form).find(`[name=${FEEDBACK_QUESTION_NUMSCALE_STEP}]`).val()) {
            setStatusMessageToForm(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID, StatusType.DANGER, form);
            return false;
        }
        const qnNum = getQuestionNumFromEditForm(form);
        if (updateNumScalePossibleValues(qnNum)) {
            return true;
        }
        setStatusMessageToForm(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID, StatusType.DANGER, form);
        return false;
    }
    return true;
}

function checkEditFeedbackSession(form) {
    if (form.visibledate.getAttribute('disabled')) {
        if (!form.visibledate.value) {
            setStatusMessageToForm(DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID, StatusType.DANGER, form);
            return false;
        }
    }
    if (form.publishdate.getAttribute('disabled')) {
        if (!form.publishdate.value) {
            setStatusMessageToForm(DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID, StatusType.DANGER, form);
            return false;
        }
    }

    return true;
}

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

function updateUncommonSettingsSessionVisibilityInfo() {
    const info = 'Session is visible at submission opening time, '
             + 'responses are only visible when you publish the results.';

    $('#uncommonSettingsSessionResponsesVisibleInfoText').html(info);
}

function updateUncommonSettingsEmailSendingInfo() {
    const info = 'Emails are sent when session opens (within 15 mins), '
             + '24 hrs before session closes and when results are published.';

    $('#uncommonSettingsSendEmailsInfoText').html(info);
}

function updateUncommonSettingsInfo() {
    updateUncommonSettingsSessionVisibilityInfo();
    updateUncommonSettingsEmailSendingInfo();
}

function isDefaultSessionResponsesVisibleSetting() {
    return $('#sessionVisibleFromButton_atopen').prop('checked')
           && $('#resultsVisibleFromButton_later').prop('checked');
}

function isDefaultSendEmailsSetting() {
    return $('#sendreminderemail_open').prop('checked')
           && $('#sendreminderemail_closing').prop('checked')
           && $('#sendreminderemail_published').prop('checked');
}

function showUncommonPanelsForSessionResponsesVisible() {
    const $sessionResponsesVisiblePanel = $('#sessionResponsesVisiblePanel');

    $('#uncommonSettingsSessionResponsesVisible').after($sessionResponsesVisiblePanel);
    $sessionResponsesVisiblePanel.show();
    $('#uncommonSettingsSessionResponsesVisibleInfoText').parent().hide();
}

function showUncommonPanelsForSendEmails() {
    const $sendEmailsForPanel = $('#sendEmailsForPanel');

    $('#uncommonSettingsSendEmails').after($sendEmailsForPanel);
    $sendEmailsForPanel.show();
    $('#uncommonSettingsSendEmailsInfoText').parent().hide();
}

function showUncommonPanels() {
    showUncommonPanelsForSessionResponsesVisible();
    showUncommonPanelsForSendEmails();
}

function showUncommonPanelsIfNotInDefaultValues() {
    if (!isDefaultSessionResponsesVisibleSetting()) {
        showUncommonPanelsForSessionResponsesVisible();
    }

    if (!isDefaultSendEmailsSetting()) {
        showUncommonPanelsForSendEmails();
    }
}

function bindUncommonSettingsEvents() {
    $('#editUncommonSettingsSessionResponsesVisibleButton')
        .click(showUncommonPanelsForSessionResponsesVisible);
    $('#editUncommonSettingsSendEmailsButton')
        .click(showUncommonPanelsForSendEmails);
}

/**
 * Saves the (disabled) state of the element in attribute data-last.<br>
 * Toggles whether the given element {@code id} is disabled or not based on
 * {@code bool}.<br>
 * Disabled if true, enabled if false.
 */
function toggleDisabledAndStoreLast(id, bool) {
    $(`#${id}`).prop('disabled', bool);
    $(`#${id}`).data('last', $(`#${id}`).prop('disabled'));
}

/**
 * Collapses/hides unnecessary fields/cells/tables if private session option is selected.
 */
function collapseIfPrivateSession() {
    if ($(`[name=${FEEDBACK_SESSION_SESSIONVISIBLEBUTTON}]`).filter(':checked').val() === 'never') {
        $('#timeFramePanel, #instructionsRow, #responsesVisibleFromColumn').hide();
    } else {
        $('#timeFramePanel, #instructionsRow, #responsesVisibleFromColumn').show();
    }
}

/**
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatResponsesVisibilityGroup() {
    const $responsesVisibilityBtnGroup = $(`[name=${FEEDBACK_SESSION_RESULTSVISIBLEBUTTON}]`);
    $responsesVisibilityBtnGroup.change(() => {
        if ($responsesVisibilityBtnGroup.filter(':checked').val() === 'custom') {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, false);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, false);
        } else {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, true);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, true);
        }
    });
}

/**
 * Hides / shows the 'Submissions Opening/Closing Time' and 'Grace Period' options
 * depending on whether a private session is selected.<br>
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatSessionVisibilityGroup() {
    const $sessionVisibilityBtnGroup = $(`[name=${FEEDBACK_SESSION_SESSIONVISIBLEBUTTON}]`);
    $sessionVisibilityBtnGroup.change(() => {
        collapseIfPrivateSession();
        if ($sessionVisibilityBtnGroup.filter(':checked').val() === 'custom') {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, false);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, false);
        } else {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, true);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, true);
        }
    });
}

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

/* exported
checkFeedbackQuestion, checkEditFeedbackSession, readyFeedbackPage, bindEventsAfterAjax, showUncommonPanels
*/
