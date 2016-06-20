/**
 * Toggles visibility of the Edit Visibility tab
 * @param elem is the anchor link being clicked on.
 */
function toggleVisibilityEditTab(elem) {
    var $containingForm = $(elem).closest('form');
    var $editTab = $containingForm.find('.visibilityOptions');
    var $previewTab = $containingForm.find('.visibilityMessage');

    // enable edit
    $containingForm.find('[id|="questionedittext"]').click();

    if ($editTab.is(':hidden')) {
        giverType = $containingForm.find('select[name="givertype"]');
        recipientType = $containingForm.find('select[name="recipienttype"]');
        $editTab.show();
        $previewTab.hide();
        updateEditTabAccordingToGiver(giverType);
        updateEditTabAccordingToRecipient(recipientType);
    } else {
        $editTab.hide();
        $previewTab.show();
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
            var editTabRows = $(this).closest('tr');
            editTabRows.find('input[class*="giverCheckbox"]')
                                     .prop('checked', false);
            editTabRows.find('input[class*="recipientCheckbox"]')
                                     .prop('checked', false);
        }
    });
    $('input[class*="giverCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var editTabRows = $(this).closest('tr');
            editTabRows.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });
    $('input[class*="recipientCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var editTabRows = $(this).closest('tr');
            editTabRows.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true);
        }
    });
    $('input[name=receiverLeaderCheckbox]').change(function() {
        var editTabRows = $(this).closest('tr');
        editTabRows.find('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
    });
}

function enableRow(elem, row) {
    var $editTab = $(elem).closest('form').find('.visibilityOptions');
    var $table = $editTab.find('table');
    var $tdElements = $($table.children().children()[row]).children();
    
    if ($tdElements.parent().prop('tagName') === 'tr') {
        return;
    }
    $tdElements.unwrap().wrapAll('<tr>');
}

function disableRow(elem, row) {
    var $editTab = $(elem).closest('form').find('.visibilityOptions');
    var $table = $editTab.find('table');
    var $tdElements = $($table.children().children()[row]).children();
    
    if ($tdElements.parent().prop('tagName') === 'hide') {
        return;
    }
    $tdElements.unwrap().wrapAll('<hide>');
    $tdElements.parent().hide();
}

function updateEditTabAccordingToRecipient(elem) {
    var $elem = $(elem);
    if (isRecipientsTeamMembersVisibilityOptionInvalidForRecipientType($elem.val())) {
        // show the row Recipient(s) and hide the row Recipient's Team Members
        enableRow($elem, 1);
        disableRow($elem, 3);
        return;
    } else if ($elem.val() === 'NONE') {
        // hide both the row Recipient(s) and the row Recipient's Team Members
        disableRow($elem, 3);
        disableRow($elem, 1);
        return;
    }
    
    enableRow($elem, 1);
    enableRow($elem, 3);
}

/**
 * Returns true if "recipient's team members" visibility option
 * is not applicable for the recipient type
 */
function isRecipientsTeamMembersVisibilityOptionInvalidForRecipientType(recipientType) {
    return recipientType === 'OWN_TEAM' || recipientType === 'TEAMS'
           || recipientType === 'INSTRUCTORS' || recipientType === 'OWN_TEAM_MEMBERS'
           || recipientType === 'OWN_TEAM_MEMBERS_INCLUDING_SELF';
}

function updateEditTabAccordingToGiver(elem) {
    var $elem = $(elem);
    if ($elem.val() === 'INSTRUCTORS' || $elem.val() === 'TEAMS') {
        disableRow($elem, 2);
        return;
    }
    enableRow($elem, 2);
}

function toggleVisibilityPreviewTab(elem) {
    var $containingForm = $(elem).closest('form');
    var $editTab = $containingForm.find('.visibilityOptions');

    var $giverType = $containingForm.find('select[name="givertype"]');
    var $recipientType = $containingForm.find('select[name="recipienttype"]');

    $editTab.hide();
    var $disabledInputs = $containingForm.find('input:disabled, select:disabled');
    $disabledInputs.prop('disabled', false);

    updateEditTabAccordingToGiver($giverType);
    updateEditTabAccordingToRecipient($recipientType);

    updatePreviewTab(elem);
    $disabledInputs.prop('disabled', true);
}

// Meant to be declared outside to prevent unncessary AJAX calls
var previousFormDataMap = {};

/**
 * Updates the Preview Visibility tab according to configurations in the
 * Edit Visibility tab (using AJAX)
 * @param buttonElem
 */
function updatePreviewTab(buttonElem) {
    var $containingForm = $(buttonElem).closest('form');
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
    var visibilityPreviewButton = $containingForm.find('.visibilityMessageButton');
    
    var radioInput = visibilityPreviewButton.find('input[type="radio"]');
    var icon = '<span class="glyphicon glyphicon-'
               + (isLoadSuccessful ? 'eye-open' : 'warning-sign')
               + '"></span>';
    var message = isLoadSuccessful ? 'Preview Visibility'
                                   : 'Visibility preview failed to load. Click here to retry.';
    
    visibilityPreviewButton.html(icon + ' ' + message)
                           .prepend(radioInput);
}

function getVisibilityMessageIfPreviewIsActive(buttonElem) {
    var $containingForm = $(buttonElem).closest('form');
    
    if ($containingForm.find('.visibilityMessageButton').hasClass('active')) {
        updatePreviewTab(buttonElem);
    }
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

