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
    updateEditTab($containingForm);
}

function toggleVisibilityEditTab(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    var $editTab = $containingForm.find('.visibilityOptions');
    var $previewTab = $containingForm.find('.visibilityMessage');

    // enable edit
    $containingForm.find('[id|="questionedittext"]').click();

    if ($editTab.is(':hidden')) {
        $editTab.show();
        $previewTab.hide();
        updateEditTab($containingForm);
    } else {
        $editTab.hide();
        $previewTab.show();
    }
}

function toggleVisibilityPreviewTab(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    var $editTab = $containingForm.find('.visibilityOptions');

    $editTab.hide();
    var $disabledInputs = $containingForm.find('input:disabled, select:disabled');
    $disabledInputs.prop('disabled', false);

    updateEditTab($containingForm);

    updatePreviewTab($containingForm);
    $disabledInputs.prop('disabled', true);
}

function getVisibilityMessageIfPreviewIsActive(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    
    if ($containingForm.find('.visibilityMessageButton').hasClass('active')) {
        updatePreviewTab($containingForm);
    }
}

// ////////////// //
// HELPER METHODS //
// ////////////// //

/**
 * Updates the Edit Visibility tab to show/hide visibility option rows
 * according to the feedback path
 */
function updateEditTab($containingForm) {
    enableAllRows($containingForm);

    disableRowsAccordingToGiver($containingForm);
    disableRowsAccordingToRecipient($containingForm);
    disableRowsForSpecificGiverRecipientCombinations($containingForm);
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
    
    $.each(checkboxTypes, function(i, checkboxType) {
        var checked = [];
        $(i + questionNum + ':checked').each(function() {
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
    // TODO: change class -> name?
    $('input[class*="answerCheckbox"]').change(function() {
        if (!$(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input[class*="giverCheckbox"]').prop('checked', false);
            $editTabRows.find('input[class*="recipientCheckbox"]').prop('checked', false);
        }
    });
    $('input[class*="giverCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input[class*="answerCheckbox"]').prop('checked', true).trigger('change');
        }
    });
    $('input[class*="recipientCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input[class*="answerCheckbox"]').prop('checked', true);
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
    $($table.children().children()[row]).hide();
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
        throw 'Unexpected recipientType';
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
        throw 'Unexpected giverType';
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
 * Updates the Preview Visibility tab according to configurations in the
 * Edit Visibility tab (using AJAX)
 * @param $containingForm
 */
function updatePreviewTab($containingForm) {
    var questionNum = $containingForm.find('[name=questionnum]').val();
    var newQuestionNum = $('input[name=questionnum]').last().val();
    
    if (questionNum === newQuestionNum) {
        tallyCheckboxes('');
    } else {
        tallyCheckboxes(questionNum);
    }
    
    var formData = $containingForm.serialize();
    
    var $editTab = $containingForm.find('.visibilityOptions');
    var $previewTab = $containingForm.find('.visibilityMessage');
    
    if (previousFormDataMap[questionNum] === formData) {
        $editTab.hide();
        $previewTab.show();
        return;
    }

    // empty current visibility message in the form
    $previewTab.html('');
    
    var url = '/page/instructorFeedbackQuestionvisibilityMessage';
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function(data) {
            updateToggleVisibilityPreviewButton($containingForm, true);
            
            // update stored form data
            previousFormDataMap[questionNum] = formData;
            
            $previewTab.html(formatPreviewTabHtml(data.visibilityMessage));
            $previewTab.show();
            $editTab.hide();
        },
        error: function() {
            updateToggleVisibilityPreviewButton($containingForm, false);
            $containingForm.find('.visibilityOptionsLabel').click();
        }
    });
}

function updateToggleVisibilityPreviewButton($containingForm, isLoadSuccessful) {
    var $visibilityPreviewButton = $containingForm.find('.visibilityMessageButton');
    
    var $radioInput = $visibilityPreviewButton.find('input[type="radio"]');
    var icon = '<span class="glyphicon glyphicon-'
               + (isLoadSuccessful ? 'eye-open' : 'warning-sign')
               + '"></span>';
    var message = isLoadSuccessful ? 'Preview Visibility'
                                   : 'Visibility preview failed to load. Click here to retry.';
    
    $visibilityPreviewButton.html(icon + ' ' + message)
                            .prepend($radioInput);
}

function formatPreviewTabHtml(visibilityMessage) {
    var htmlString = 'This is the visibility as seen by the feedback giver.';
    htmlString += '<ul class="background-color-warning">';
    for (var i = 0; i < visibilityMessage.length; i++) {
        htmlString += '<li>' + visibilityMessage[i] + '</li>';
    }
    htmlString += '</ul>';
    return htmlString;
}

