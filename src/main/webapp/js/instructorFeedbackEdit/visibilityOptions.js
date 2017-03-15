'use strict';

var ROW_RECIPIENT = 1;
var ROW_GIVER_TEAM = 2;
var ROW_RECIPIENT_TEAM = 3;
var ROW_OTHER_STUDENTS = 4;
var ROW_INSTRUCTORS = 5;

// ////////////// //
// EVENT HANDLERS //
// ////////////// //

function matchVisibilityOptionToFeedbackPath(selectedFeedbackPathOption) {
    var $containingForm = $(selectedFeedbackPathOption).closest('form');
    updateVisibilityCheckboxesDiv($containingForm);
}

function toggleVisibilityEditTab(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    var $editTab = $containingForm.find('.visibilityOptions');
    var $visibilityMessageDiv = $containingForm.find('.visibilityMessage');

    // enable edit
    $containingForm.find('[id|="questionedittext"]').click();

    if ($editTab.is(':hidden')) {
        $editTab.show();
        $visibilityMessageDiv.hide();
        updateVisibilityCheckboxesDiv($containingForm);
    } else {
        $editTab.hide();
        $visibilityMessageDiv.show();
    }
}

function toggleVisibilityPreviewTab(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    var $editTab = $containingForm.find('.visibilityOptions');

    $editTab.hide();
    var $disabledInputs = $containingForm.find('input:disabled, select:disabled');
    $disabledInputs.prop('disabled', false);

    updateVisibilityCheckboxesDiv($containingForm);

    updateVisibilityMessageDiv($containingForm);
    $disabledInputs.prop('disabled', true);
}

function getVisibilityMessage(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    updateVisibilityMessageDiv($containingForm);
}

/**
 * binds events to the visibility dropdown menu to
 *  - show/hide visibility checkboxes div
 *  - update dropdown button text to reflected selected option
 *  - update visibility message div
 */
function attachVisibilityDropdownEvent() {
    $('body').on('click', '.visibility-options-dropdown-option', function() {
        var $clickedElem = $(this);
        var selectedOption = $clickedElem.data('optionName');
        var $containingForm = $clickedElem.closest('form');

        checkAndMarkDestructiveChange($clickedElem.text(), $containingForm);
        setVisibilityDropdownMenuText($clickedElem.text(), $containingForm);

        var $editTab = $containingForm.find('.visibilityOptions');
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

function checkAndMarkDestructiveChange(selectedOption, $containingForm) {
    if (selectedOption === 'Custom visibility options...') {
        return;
    }

    var currentOption = $containingForm.find('.visibility-options-dropdown button').text();
    var isSelectionChanged = selectedOption !== currentOption;
    var hasResponses = $containingForm.attr('editStatus') === 'hasResponses';

    if (isSelectionChanged && hasResponses) {
        $containingForm.attr('editStatus', 'mustDeleteResponses');
    }
}

/**
 * binds click event of each visibility checkbox to update visibility message div
 */
function attachVisibilityCheckboxEvent() {
    $('body').on('change', '.visibilityCheckbox', function() {
        var $containingForm = $(this).closest('form');
        updateVisibilityMessageDiv($containingForm);
    });
}

// ////////////// //
// HELPER METHODS //
// ////////////// //

function setVisibilityDropdownMenuText(text, $containingForm) {
    var $visibilityDropdown = $containingForm.find('.visibility-options-dropdown');

    if (text === 'Custom visibility options...') {
        $visibilityDropdown.find('button').text('Custom visibility option:');
    } else {
        $visibilityDropdown.find('button').text(text);
    }
}

function showVisibilityCheckboxesIfCustomOptionSelected($containingForm) {
    var selectedOption = $containingForm.find('.visibility-options-dropdown > button').text().trim();
    var $visibilityCheckboxes = $containingForm.find('.visibilityOptions');
    if (selectedOption === 'Custom visibility option:') {
        updateVisibilityCheckboxesDiv($containingForm);
        $visibilityCheckboxes.show();
    } else {
        $visibilityCheckboxes.hide();
    }
}

function uncheckAllVisibilityOptionCheckboxes($containingForm) {
    $containingForm.find('input.visibilityCheckbox').each(function(index, checkbox) {
        checkbox.checked = false;
    });
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
 * Checks the checkboxes for recipient
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowRecipientToSee(checkboxClass, $containingForm) {
    $containingForm.find('input[type="checkbox"][value="RECEIVER"]' + checkboxClass).prop('checked', true);
}

/**
 * Checks the checkboxes for instructors
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowInstructorToSee(checkboxClass, $containingForm) {
    $containingForm.find('input[type="checkbox"][value="INSTRUCTORS"]' + checkboxClass).prop('checked', true);
}

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

/**
 * Ensures the hidden checkbox for Recipient's Team Members can see answer is consistent with Recipient can see answer
 */
function fixCheckboxValuesForTeamContribQuestion($containingForm) {
    if ($containingForm.find('input[name="questiontype"]').val() !== 'CONTRIB') {
        return;
    }
    var recipientCanSeeAnswerCheckbox =
        $containingForm.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]');
    var recipientTeamCanSeeAnswerCheckbox =
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
    var checkboxTypes = {
        '.answerCheckbox': FEEDBACK_QUESTION_SHOWRESPONSESTO,
        '.giverCheckbox': FEEDBACK_QUESTION_SHOWGIVERTO,
        '.recipientCheckbox': FEEDBACK_QUESTION_SHOWRECIPIENTTO
    };

    $.each(checkboxTypes, function(className, checkboxType) {
        var checked = [];
        $('#form_editquestion-' + questionNum).find(className + ':checked').each(function() {
            checked.push($(this).val());
        });
        $('[name=' + checkboxType + ']').val(checked.toString());
    });
}

/**
 * Binds each question's check box field such that the user
 * cannot select an invalid combination.
 */
function formatCheckBoxes() {
    $('input.answerCheckbox').change(function() {
        if (!$(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input.giverCheckbox').prop('checked', false);
            $editTabRows.find('input.recipientCheckbox').prop('checked', false);
        }
    });
    $('input.giverCheckbox').change(function() {
        if ($(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input.answerCheckbox').prop('checked', true).trigger('change');
        }
    });
    $('input.recipientCheckbox').change(function() {
        if ($(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input.answerCheckbox').prop('checked', true);
        }
    });
    $('input[name=receiverLeaderCheckbox]').change(function() {
        var $editTabRows = $(this).closest('tr');
        $editTabRows.find('input[name=receiverFollowerCheckbox]').prop('checked', $(this).prop('checked'));
    });
}

function enableAllRows($containingForm) {
    var allRows = [ROW_RECIPIENT, ROW_GIVER_TEAM, ROW_RECIPIENT_TEAM, ROW_OTHER_STUDENTS, ROW_INSTRUCTORS];
    allRows.forEach(function(row) {
        enableRow($containingForm, row);
    });
}

function enableRow($containingForm, row) {
    var $table = $containingForm.find('.visibilityOptions').find('table');
    $($table.children().children()[row]).show();
}

function disableRow($containingForm, row) {
    var $table = $containingForm.find('.visibilityOptions').find('table');
    var $row = $($table.children().children()[row]);
    $row.find('input[type="checkbox"]').each(function(index, checkbox) {
        checkbox.checked = false;
    });
    $row.hide();
}

function disableRowsAccordingToRecipient($containingForm) {
    var recipientType = $containingForm.find('select[name="recipienttype"]').val();
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
    var giverType = $containingForm.find('select[name="givertype"]').val();
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
    var giverType = $containingForm.find('select[name="givertype"]').val();
    var recipientType = $containingForm.find('select[name="recipienttype"]').val();

    if ((giverType === 'SELF' || giverType === 'INSTRUCTORS') && recipientType === 'SELF') {
        // ROW_RECIPIENT_TEAM is disbled because it is the same as ROW_INSTRUCTORS
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
    } else if (giverType === 'TEAMS' && recipientType === 'OWN_TEAM_MEMBERS_INCLUDING_SELF') {
        // ROW_RECIPIENT is disbled because this is almost like a self-feedback where giver can always see the response
        disableRow($containingForm, ROW_RECIPIENT);
    }
}

// Meant to be declared outside to prevent unncessary AJAX calls
var previousFormDataMap = {};

/**
 * Updates the visibility message div according to configurations in the
 * visibility checkboxes div (using AJAX)
 * @param $containingForm
 */
function updateVisibilityMessageDiv($containingForm) {
    var questionNum = $containingForm.find('[name=questionnum]').val();
    var newQuestionNum = $('input[name=questionnum]').last().val();

    if (questionNum === newQuestionNum) {
        tallyCheckboxes(NEW_QUESTION);
    } else {
        tallyCheckboxes(questionNum);
    }

    var formData = $containingForm.serialize();
    var $visibilityMessageDiv = $containingForm.find('.visibilityMessage');

    if (previousFormDataMap[questionNum] === formData) {
        return;
    }

    // empty current visibility message in the form
    $visibilityMessageDiv.html('');

    var url = '/page/instructorFeedbackQuestionvisibilityMessage';
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function(data) {
            // update stored form data
            previousFormDataMap[questionNum] = formData;

            $visibilityMessageDiv.html(formatVisibilityMessageDivHtml(data.visibilityMessage));
        },
        error: function() {
            showAjaxErrorMessage($containingForm);
        }
    });
}

function formatVisibilityMessageDivHtml(visibilityMessage) {
    var htmlString = 'This is the visibility hint as seen by the feedback giver:';
    htmlString += '<ul class="text-muted background-color-warning">';
    for (var i = 0; i < visibilityMessage.length; i++) {
        htmlString += '<li>' + visibilityMessage[i] + '</li>';
    }
    htmlString += '</ul>';
    return htmlString;
}

/**
 * Updates visibility message div with error message and add onclick event for re-loading the visibility message
 */
function showAjaxErrorMessage($containingForm) {
    var $visibilityMessageDiv = $containingForm.find('.visibilityMessage');

    var htmlString = 'This is the visibility hint as seen by the feedback giver:';
    htmlString += '<ul class="text-muted background-color-warning">';
    htmlString += '<li><a>Error loading visibility hint. Click here to retry.</a></li>';
    htmlString += '</ul>';

    $visibilityMessageDiv.html(htmlString);
    $visibilityMessageDiv.find('ul').on('click', function() {
        $visibilityMessageDiv.html('');
        updateVisibilityMessageDiv($containingForm);
    });
}
