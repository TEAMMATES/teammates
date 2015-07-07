//TODO: Move constants from Common.js into appropriate files if not shared.
var TIMEZONE_SELECT_UNINITIALISED = '-9999';

/**
 * Check whether the feedback question input is valid
 * @param form
 * @returns {Boolean}
 */
function checkFeedbackQuestion(form) {
    var recipientType = $(form).find('select[name|=' + FEEDBACK_QUESTION_RECIPIENTTYPE + ']')
                               .find(':selected')
                               .val();
    if (recipientType === 'STUDENTS' || recipientType === 'TEAMS') {
        if ($(form).find('[name|=' + FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE + ']:checked')
                   .val() === 'custom' &&
                !$(form).find('.numberOfEntitiesBox').val()) {
            setStatusMessage(DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID, true);
            return false;
        }
    }
    if (!$(form).find('[name=' + FEEDBACK_QUESTION_TEXT + ']').val()) {
        setStatusMessage(DISPLAY_FEEDBACK_QUESTION_TEXTINVALID, true);
        return false;
    }
    if ($(form).find('[name=' + FEEDBACK_QUESTION_TYPE + ']').val() === 'NUMSCALE') {
        if (!$(form).find('[name=' + FEEDBACK_QUESTION_NUMSCALE_MIN + ']').val() ||
                !$(form).find('[name=' + FEEDBACK_QUESTION_NUMSCALE_MAX + ']').val()||
                !$(form).find('[name=' + FEEDBACK_QUESTION_NUMSCALE_STEP + ']').val()) {
            setStatusMessage(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID, true);
            return false;
        }
        var qnNum = getQuestionNumFromEditForm(form);
        if (updateNumScalePossibleValues(qnNum)) {
            return true;
        } else {
            setStatusMessage(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID, true);
            return false;
        }
    }
    return true;
}

function getQuestionNumFromEditForm(form) {
    if ($(form).attr('name') === 'form_addquestions') {
        return -1;
    } else {
        return extractQuestionNumFromEditFormId($(form).attr('id'));
    }
}

function extractQuestionNumFromEditFormId(id) {
    return parseInt(id.substring('form_editquestion-'.length, id.length));
}

function checkEditFeedbackSession(form) {
    if (form.visibledate.getAttribute('disabled')) {
        if (!form.visibledate.value) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID, true);
            return false;
        }
    }
    if (form.publishdate.getAttribute('disabled')) {
        if (!form.publishdate.value) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID, true);
            return false;
        }
    }

    return true;
}

/**
 * To be run on page finish loading, this will select the input: start date,
 * start time, and timezone based on client's time.
 *
 * The default values will not be set if the form was submitted previously and
 * failed validation.
 */
function selectDefaultTimeOptions() {
    var now = new Date();

    var hours = convertDateToHHMM(now).substring(0, 2);
    var currentTime = (parseInt(hours) + 1) % 24;
    var timeZone = -now.getTimezoneOffset() / 60;

    if (!isTimeZoneIntialized()) {
        $('#' + FEEDBACK_SESSION_STARTTIME).val(currentTime);
        $('#' + FEEDBACK_SESSION_TIMEZONE).val(timeZone);
    }

    var uninitializedTimeZone = $('#timezone > option[value=\'' + TIMEZONE_SELECT_UNINITIALISED + '\']');
    if (uninitializedTimeZone) {
        uninitializedTimeZone.remove();
    }
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
    return formatDigit(date.getDate()) + '/' +
           formatDigit(date.getMonth() + 1) + '/' +
           date.getFullYear();
}

/**
 * Format a date object into HHMM format
 * @param date
 * @returns {String}
 */
function convertDateToHHMM(date) {
    return formatDigit(date.getHours()) + formatDigit(date.getMinutes());
}

function bindCopyButton() {
    $('#button_copy').on('click', function(e) {
        e.preventDefault();
        var selectedCourseId = $('#' + COURSE_ID + ' option:selected').text();
        var newFeedbackSessionName = $('#' + FEEDBACK_SESSION_NAME).val();

        var isExistingSession = false;

        var $sessionsList = $('tr[id^="session"]');
        if (!$sessionsList.length) {
            setStatusMessage(FEEDBACK_SESSION_COPY_INVALID, true);
            return false;
        }

        $sessionsList.each(function() {
            var $cells = $(this).find('td');
            var courseId = $($cells[0]).text();
            var feedbackSessionName = $($cells[1]).text();
            if (selectedCourseId === courseId && newFeedbackSessionName === feedbackSessionName) {
                isExistingSession = true;
                return false;
            }
        });

        if (isExistingSession) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE, true);
        } else {
            setStatusMessage('', false);

            var $firstSession = $($sessionsList[0]).find('td');
            var firstSessionCourseId = $($firstSession[0]).text();
            var firstSessionName = $($firstSession[1]).text();

            $('#copyModal').modal('show');
            $('#modalCopiedSessionName').val(newFeedbackSessionName.trim());
            $('#modalCopiedCourseId').val(selectedCourseId.trim());
            var $modalCourseId = $('#modalCourseId');
            if (!$modalCourseId.val().trim()) {
                $modalCourseId.val(firstSessionCourseId);
            }
            var $modalSessionName = $('#modalSessionName');
            if (!$modalSessionName.val().trim()) {
                $modalSessionName.val(firstSessionName);
            }
        }

        return false;
    });

    $('#button_copy_submit').on('click', function(e) {
        e.preventDefault();
        $('#copyModalForm').submit();
        return false;
    });
}

function bindCopyEvents() {
    $('#copyTableModal > tbody > tr').on('click', function(e) {

        var $currentlySelectedRow = $(this);
        if ($currentlySelectedRow.hasClass('row-selected')) {
            return;
        }

        var $cells = $currentlySelectedRow.children('td');
        var courseId = $($cells[1]).text().trim();
        var feedbackSessionName = $($cells[2]).text().trim();
        $('#modalCourseId').val(courseId);
        $('#modalSessionName').val(feedbackSessionName);

        var $previouslySelectedRadio = $currentlySelectedRow.parent().find('input:checked');
        var $previouslySelectedRow = $previouslySelectedRadio.parent().parent();

        $previouslySelectedRadio.prop('checked', false);
        $previouslySelectedRow.removeClass('row-selected');

        var $currentlySelectedRadio = $currentlySelectedRow.children('td').children('input');
        $currentlySelectedRadio.prop('checked', true);
        $currentlySelectedRow.addClass('row-selected');

        $('#button_copy_submit').prop('disabled', false);
    });
}

function readyFeedbackPage() {
    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();
    collapseIfPrivateSession();

    window.doPageSpecificOnload = selectDefaultTimeOptions();
    $('#ajaxForSessions').trigger('submit');
    bindUncommonSettingsEvents();
    updateUncommonSettingsInfo();
    hideUncommonPanels();
}

function bindEventsAfterAjax() {
    bindCopyButton();
    bindCopyEvents();
    linkAjaxForResponseRate();
    setupFsCopyModal();
}

function bindUncommonSettingsEvents() {
    $('#editUncommonSettingsButton').click(showUncommonPanels);
}

function updateUncommonSettingsInfo() {
    var info = 'Session is visible at submission opening time, ' +
               'responses are only visible when you publish the results.<br>' +
               'Emails are sent when session opens (within 15 mins), ' +
               '24 hrs before session closes and when results are published.';

    $('#uncommonSettingsInfoText').html(info);
}

function isDefaultSetting() {
    if ($('#sessionVisibleFromButton_atopen').prop('checked') &&
            $('#resultsVisibleFromButton_later').prop('checked') &&
            $('#sendreminderemail_open').prop('checked') &&
            $('#sendreminderemail_closing').prop('checked') &&
            $('#sendreminderemail_published').prop('checked')) {
        return true;
    } else {
        return false;
    }
}

function showUncommonPanels() {
    $('#sessionResponsesVisiblePanel, #sendEmailsForPanel').show();
    $('#uncommonSettingsInfo').hide();
}

function hideUncommonPanels() {
    //Hide panels only if they match the default values.
    if (isDefaultSetting()) {
        $('#sessionResponsesVisiblePanel, #sendEmailsForPanel').hide();
    } else {
        showUncommonPanels();
    }
}

/**
 * Hides / shows the 'Submissions Opening/Closing Time' and 'Grace Period' options
 * depending on whether a private session is selected.<br>
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatSessionVisibilityGroup() {
    var $sessionVisibilityBtnGroup = $('[name=' + FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + ']');
    $sessionVisibilityBtnGroup.change(function() {
        collapseIfPrivateSession();
        if ($sessionVisibilityBtnGroup.filter(':checked').val() == 'custom') {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, false);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, false);
        } else {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, true);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, true);
        }
    });
}

/**
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatResponsesVisibilityGroup() {
    var $responsesVisibilityBtnGroup = $('[name=' + FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + ']');
    $responsesVisibilityBtnGroup.change(function() {
        if ($responsesVisibilityBtnGroup.filter(':checked').val() == 'custom') {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, false);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, false);
        } else {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, true);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, true);
        }
    });
}

/**
 * Saves the (disabled) state of the element in attribute data-last.<br>
 * Toggles whether the given element {@code id} is disabled or not based on
 * {@code bool}.<br>
 * Disabled if true, enabled if false.
 */
function toggleDisabledAndStoreLast(id, bool) {
    $('#' + id).prop('disabled', bool);
    $('#' + id).data('last', $('#' + id).prop('disabled'));
}

/**
 * Collapses/hides unnecessary fields/cells/tables if private session option is selected.
 */
function collapseIfPrivateSession() {
    if ($('[name=' + FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + ']').filter(':checked').val() == 'never') {
        $('#timeFramePanel, #instructionsRow, #responsesVisibleFromColumn').hide();
    } else {
        $('#timeFramePanel, #instructionsRow, #responsesVisibleFromColumn').show();
    }
}
