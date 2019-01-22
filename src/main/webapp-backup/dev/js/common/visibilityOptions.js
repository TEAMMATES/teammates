import {
    ParamsNames,
} from './const';

const ROW_RECIPIENT = 1;
const ROW_GIVER_TEAM = 2;
const ROW_RECIPIENT_TEAM = 3;
const ROW_OTHER_STUDENTS = 4;
const ROW_INSTRUCTORS = 5;

const NEW_QUESTION = -1;

// ////////////// //
// HELPER METHODS //
// ////////////// //

function setVisibilityDropdownMenuText(text, $containingForm) {
    const $visibilityDropdown = $containingForm.find('.visibility-options-dropdown');

    if (text === 'Custom visibility options...') {
        $visibilityDropdown.find('button').text('Custom visibility option:');
    } else {
        $visibilityDropdown.find('button').text(text);
    }
}

function uncheckAllVisibilityOptionCheckboxes($containingForm) {
    $containingForm.find('input.visibilityCheckbox').each((index, checkbox) => {
        checkbox.checked = false;
    });
}

/**
 * Checks the checkboxes for recipient
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowRecipientToSee(checkboxClass, $containingForm) {
    $containingForm.find(`input[type="checkbox"][value="RECEIVER"]${checkboxClass}`).prop('checked', true);
}

/**
 * Checks the checkboxes for giver's team members
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowGiversTeamToSee(checkboxClass, $containingForm) {
    $containingForm.find(`input[type="checkbox"][value="OWN_TEAM_MEMBERS"]${checkboxClass}`).prop('checked', true);
}

/**
 * Checks the checkboxes for recipient's team members
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowRecipientsTeamToSee(checkboxClass, $containingForm) {
    $containingForm.find(`input[type="checkbox"][value="RECEIVER_TEAM_MEMBERS"]${checkboxClass}`).prop('checked', true);
}

/**
 * Checks the checkboxes for instructors
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowInstructorToSee(checkboxClass, $containingForm) {
    $containingForm.find(`input[type="checkbox"][value="INSTRUCTORS"]${checkboxClass}`).prop('checked', true);
}

/**
 * Checks the visibility checkboxes according to the common visibility option as selected using the dropdown menu
 */
function checkCorrespondingCheckboxes(selectedOption, $containingForm) {
    switch (selectedOption) {
    case 'OTHER':
        return;
    case 'ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS':
        // recipient and instructor can see answer and recipient, but not giver name
        allowRecipientToSee('.answerCheckbox', $containingForm);
        allowRecipientToSee('.recipientCheckbox', $containingForm);

        allowInstructorToSee('.answerCheckbox', $containingForm);
        allowInstructorToSee('.recipientCheckbox', $containingForm);
        break;
    case 'ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS':
        // recipient can see answer and recipient, but not giver name
        allowRecipientToSee('.answerCheckbox', $containingForm);
        allowRecipientToSee('.recipientCheckbox', $containingForm);

        // instructor can see answer, recipient AND giver name
        allowInstructorToSee('.answerCheckbox', $containingForm);
        allowInstructorToSee('.giverCheckbox', $containingForm);
        allowInstructorToSee('.recipientCheckbox', $containingForm);
        break;
    case 'ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS':
        // recipient can see answer and recipient, but not giver name
        allowRecipientToSee('.answerCheckbox', $containingForm);
        allowRecipientToSee('.recipientCheckbox', $containingForm);

        // instructor can see answer, recipient AND giver name
        allowInstructorToSee('.answerCheckbox', $containingForm);
        allowInstructorToSee('.giverCheckbox', $containingForm);
        allowInstructorToSee('.recipientCheckbox', $containingForm);

        // recipient team (same as givers team) can see answer and recipient, but not giver name
        allowRecipientsTeamToSee('.answerCheckbox', $containingForm);
        allowGiversTeamToSee('.answerCheckbox', $containingForm);
        break;
    case 'VISIBLE_TO_INSTRUCTORS_ONLY':
        allowInstructorToSee('.answerCheckbox', $containingForm);
        allowInstructorToSee('.giverCheckbox', $containingForm);
        allowInstructorToSee('.recipientCheckbox', $containingForm);
        break;
    case 'VISIBLE_TO_RECIPIENT_AND_INSTRUCTORS':
        allowRecipientToSee('.answerCheckbox', $containingForm);
        allowRecipientToSee('.giverCheckbox', $containingForm);
        allowRecipientToSee('.recipientCheckbox', $containingForm);

        allowInstructorToSee('.answerCheckbox', $containingForm);
        allowInstructorToSee('.giverCheckbox', $containingForm);
        allowInstructorToSee('.recipientCheckbox', $containingForm);
        break;
    default:
        throw new Error('Unexpected common visibility option type');
    }
}

/**
 * Ensures the hidden checkbox for Recipient's Team Members can see answer is consistent with Recipient can see answer
 */
function fixCheckboxValuesForTeamContribQuestion($containingForm) {
    if ($containingForm.find('input[name="questiontype"]').val() !== 'CONTRIB') {
        return;
    }
    const recipientCanSeeAnswerCheckbox =
        $containingForm.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]');
    const recipientTeamCanSeeAnswerCheckbox =
        $containingForm.find('input.answerCheckbox').filter('[value=RECEIVER_TEAM_MEMBERS]');

    if (recipientCanSeeAnswerCheckbox.prop('checked')) {
        recipientTeamCanSeeAnswerCheckbox.prop('checked', true);
    }
}

/**
 * Pushes the values of all checked check boxes for the specified question
 * into the appropriate feedback question parameters.
 * @returns questionNum
 */
function tallyCheckboxes(questionNum) {
    // update hidden parameters (the values in checkboxTypes)
    const checkboxTypes = {
        '.answerCheckbox': ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO,
        '.giverCheckbox': ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO,
        '.recipientCheckbox': ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO,
    };

    $.each(checkboxTypes, (className, checkboxType) => {
        const checked = [];
        $(`#form_editquestion-${questionNum}`).find(`${className}:checked`).each(function () {
            checked.push($(this).val());
        });
        $(`[name=${checkboxType}]`).val(checked.toString());
    });
}

/**
 * Binds each question's check box field such that the user
 * cannot select an invalid combination.
 */
function formatCheckBoxes() {
    $('.visibility-checkbox-delegate').on('change', 'input.answerCheckbox', (e) => {
        const checkbox = $(e.target);
        if (!checkbox.is(':checked')) {
            const $editTabRows = checkbox.closest('tr');
            $editTabRows.find('input.giverCheckbox').prop('checked', false);
            $editTabRows.find('input.recipientCheckbox').prop('checked', false);
        }
    });
    $('.visibility-checkbox-delegate').on('change', 'input.giverCheckbox', (e) => {
        const checkbox = $(e.target);
        if (checkbox.is(':checked')) {
            const $editTabRows = checkbox.closest('tr');
            $editTabRows.find('input.answerCheckbox').prop('checked', true).trigger('change');
        }
    });
    $('.visibility-checkbox-delegate').on('change', 'input.recipientCheckbox', (e) => {
        const checkbox = $(e.target);
        if (checkbox.is(':checked')) {
            const $editTabRows = checkbox.closest('tr');
            $editTabRows.find('input.answerCheckbox').prop('checked', true);
        }
    });
    $('.visibility-checkbox-delegate').on('change', 'input[name=receiverLeaderCheckbox]', (e) => {
        const checkbox = $(e.target);
        const $editTabRows = checkbox.closest('tr');
        $editTabRows.find('input[name=receiverFollowerCheckbox]').prop('checked', checkbox.prop('checked'));
    });
}

function enableRow($containingForm, row) {
    const $table = $containingForm.find('.visibilityOptions').find('table');
    $($table.children().children()[row]).show();
}

function disableRow($containingForm, row) {
    const $table = $containingForm.find('.visibilityOptions').find('table');
    const $row = $($table.children().children()[row]);
    $row.find('input[type="checkbox"]').each((index, checkbox) => {
        checkbox.checked = false;
    });
    $row.hide();
}

function enableAllRows($containingForm) {
    const allRows = [ROW_RECIPIENT, ROW_GIVER_TEAM, ROW_RECIPIENT_TEAM, ROW_OTHER_STUDENTS, ROW_INSTRUCTORS];
    allRows.forEach((row) => {
        enableRow($containingForm, row);
    });
}

function disableRowsAccordingToRecipient($containingForm) {
    const recipientType = $containingForm.find('select[name="recipienttype"]').val();
    switch (recipientType) {
    case 'SELF':
        // ROW_RECIPIENT is disabled because self-feedback is always visible to giver
        disableRow($containingForm, ROW_RECIPIENT);
        // ROW_RECIPIENT_TEAM is disabled because it is the same as ROW_GIVER_TEAM
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
        break;
    case 'STUDENTS':
        // all options enabled when recipientType is STUDENTS (subject to options disabled by giverType)
        break;
    case 'OWN_TEAM':
        // ROW_RECIPIENT and ROW_RECIPIENT_TEAM are disabled because they are the same as ROW_GIVER_TEAM
        disableRow($containingForm, ROW_RECIPIENT);
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
        break;
    case 'INSTRUCTORS':
        // ROW_RECIPIENT_TEAM is disabled because it is the same as ROW_INSTRUCTORS
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
        break;
    case 'TEAMS':
        // ROW_RECIPIENT_TEAM is disabled because it is the same as ROW_RECIPIENT
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
        break;
    case 'OWN_TEAM_MEMBERS':
    case 'OWN_TEAM_MEMBERS_INCLUDING_SELF':
        // ROW_RECIPIENT_TEAM is disabled for OWN_TEAM_MEMBERS and OWN_TEAM_MEMBERS_INCLUDING_SELF
        // because it is the same as ROW_GIVER_TEAM
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
        break;
    case 'NONE':
        // ROW_RECIPIENT and ROW_RECIPIENT_TEAM are disabled because there are no recipients
        disableRow($containingForm, ROW_RECIPIENT);
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
        break;
    default:
        throw new Error('Unexpected recipientType');
    }
}

function disableRowsAccordingToGiver($containingForm) {
    const giverType = $containingForm.find('select[name="givertype"]').val();
    switch (giverType) {
    case 'STUDENTS':
        // all options enabled when giverType is STUDENTS (subject to options disabled by recipientType)
        break;
    case 'SELF':
    case 'INSTRUCTORS':
        // ROW_GIVER_TEAM is disabled for SELF and INSTRUCTORS because it is the same as ROW_INSTRUCTORS
        disableRow($containingForm, ROW_GIVER_TEAM);
        break;
    case 'TEAMS':
        // ROW_GIVER_TEAM is disabled for TEAMS because giver can always see the response
        disableRow($containingForm, ROW_GIVER_TEAM);
        break;
    default:
        throw new Error('Unexpected giverType');
    }
}

function disableRowsForSpecificGiverRecipientCombinations($containingForm) {
    const giverType = $containingForm.find('select[name="givertype"]').val();
    const recipientType = $containingForm.find('select[name="recipienttype"]').val();

    if ((giverType === 'SELF' || giverType === 'INSTRUCTORS') && recipientType === 'SELF') {
        // ROW_RECIPIENT_TEAM is disbled because it is the same as ROW_INSTRUCTORS
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
    } else if (giverType === 'TEAMS' && recipientType === 'OWN_TEAM_MEMBERS_INCLUDING_SELF') {
        // ROW_RECIPIENT is disbled because this is almost like a self-feedback where giver can always see the response
        disableRow($containingForm, ROW_RECIPIENT);
    }
}

// Meant to be declared outside to prevent unncessary AJAX calls
const previousFormDataMap = {};

/**
 * Updates the visibility checkboxes div to show/hide visibility option rows
 * according to the feedback path
 */
function updateVisibilityCheckboxesDiv($containingForm) {
    enableAllRows($containingForm);

    disableRowsAccordingToGiver($containingForm);
    disableRowsAccordingToRecipient($containingForm);
    disableRowsForSpecificGiverRecipientCombinations($containingForm);

    // handles edge case for Team Contribution Question:
    // normal behavior is that all hidden checkboxes are unchecked, but Team Contribution Question expect even the hidden
    // Recipient's Team Members can see answer checkbox to be checked
    fixCheckboxValuesForTeamContribQuestion($containingForm);
}

function showVisibilityCheckboxesIfCustomOptionSelected($containingForm) {
    const selectedOption = $containingForm.find('.visibility-options-dropdown > button').text().trim();
    const $visibilityCheckboxes = $containingForm.find('.visibilityOptions');
    if (selectedOption === 'Custom visibility option:') {
        updateVisibilityCheckboxesDiv($containingForm);
        $visibilityCheckboxes.show();
    } else {
        $visibilityCheckboxes.hide();
    }
}

function formatVisibilityMessageDivHtml(visibilityMessage) {
    let htmlString = 'This is the visibility hint as seen by the feedback giver:';
    htmlString += '<ul class="text-muted background-color-warning">';
    for (let i = 0; i < visibilityMessage.length; i += 1) {
        htmlString += `<li>${visibilityMessage[i]}</li>`;
    }
    htmlString += '</ul>';
    return htmlString;
}

/**
 * Updates visibility message div with error message and add onclick event for re-loading the visibility message
 */
function showAjaxErrorMessage($containingForm) {
    const $visibilityMessageDiv = $containingForm.find('.visibility-message');

    let htmlString = 'This is the visibility hint as seen by the feedback giver:';
    htmlString += '<ul class="text-muted background-color-warning">';
    htmlString += '<li><a>Error loading visibility hint. Click here to retry.</a></li>';
    htmlString += '</ul>';

    $visibilityMessageDiv.html(htmlString);
    $visibilityMessageDiv.find('ul').on('click', () => {
        $visibilityMessageDiv.html('');
        updateVisibilityMessageDiv($containingForm); // eslint-disable-line no-use-before-define
    });
}

/**
 * Updates the visibility message div according to configurations in the
 * visibility checkboxes div (using AJAX)
 * @param $containingForm
 */
function updateVisibilityMessageDiv($containingForm) {
    const questionNum = $containingForm.find('[name=questionnum]').val();
    const newQuestionNum = $('input[name=questionnum]').last().val();

    if (questionNum === newQuestionNum) {
        tallyCheckboxes(NEW_QUESTION);
    } else {
        tallyCheckboxes(questionNum);
    }

    const formData = $containingForm.serialize();
    const $visibilityMessageDiv = $containingForm.find('.visibility-message');

    if (previousFormDataMap[questionNum] === formData) {
        return;
    }

    // empty current visibility message in the form
    $visibilityMessageDiv.html('');

    const url = '/page/instructorFeedbackQuestionvisibilityMessage';
    $.ajax({
        type: 'POST',
        url,
        data: formData,
        beforeSend() {
            $visibilityMessageDiv.html("<img src='/images/ajax-loader.gif'/>");
        },
        success(data) {
            // update stored form data
            previousFormDataMap[questionNum] = formData;

            $visibilityMessageDiv.html(formatVisibilityMessageDivHtml(data.visibilityMessage));
        },
        error() {
            showAjaxErrorMessage($containingForm);
        },
    });
}

// ////////////// //
// EVENT HANDLERS //
// ////////////// //

function matchVisibilityOptionToFeedbackPath(selectedFeedbackPathOption) {
    const $containingForm = $(selectedFeedbackPathOption).closest('form');
    updateVisibilityCheckboxesDiv($containingForm);
}

function getVisibilityMessage(clickedButton) {
    const $containingForm = $(clickedButton).closest('form');
    updateVisibilityMessageDiv($containingForm);
}

/**
 * binds events to the visibility dropdown menu to
 *  - show/hide visibility checkboxes div
 *  - update dropdown button text to reflected selected option
 *  - update visibility message div
 */
function attachVisibilityDropdownEvent() {
    $('body').on('click', '.visibility-options-dropdown-option', function () {
        const $clickedElem = $(this);
        const selectedOption = $clickedElem.data('optionName');
        const $containingForm = $clickedElem.closest('form');

        setVisibilityDropdownMenuText($clickedElem.text(), $containingForm);

        const $editTab = $containingForm.find('.visibilityOptions');
        if (selectedOption === 'OTHER') {
            $editTab.show();
            updateVisibilityCheckboxesDiv($containingForm);
        } else {
            // only uncheck all checkboxes and update accordingly if a common option is selected
            uncheckAllVisibilityOptionCheckboxes($containingForm);
            checkCorrespondingCheckboxes(selectedOption, $containingForm);
            $editTab.hide();
        }

        updateVisibilityMessageDiv($containingForm);
    });
}

/**
 * binds click event of each visibility checkbox to update visibility message div
 */
function attachVisibilityCheckboxEvent() {
    $('body').on('change', '.visibilityCheckbox', function () {
        const $containingForm = $(this).closest('form');
        updateVisibilityMessageDiv($containingForm);
    });
}

export {
    attachVisibilityCheckboxEvent,
    attachVisibilityDropdownEvent,
    formatCheckBoxes,
    getVisibilityMessage,
    matchVisibilityOptionToFeedbackPath,
    showVisibilityCheckboxesIfCustomOptionSelected,
    tallyCheckboxes,
};
