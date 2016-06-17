/**
 * Hides or show visibility checkboxes frame
 * @param elem is the anchor link being clicked on.
 */
function toggleVisibilityOptions(elem) {
    var $elementParent = $(elem).closest('form');
    var $options = $elementParent.find('.visibilityOptions');
    var $visibilityMessage = $elementParent.find('.visibilityMessage');

    // enable edit
    $elementParent.find('[id|="questionedittext"]').click();

    if ($options.is(':hidden')) {
        giverType = $elementParent.find('select[name="givertype"]');
        recipientType = $elementParent.find('select[name="recipienttype"]');
        $options.show();
        $visibilityMessage.hide();
        feedbackGiverUpdateVisibilityOptions(giverType);
        feedbackRecipientUpdateVisibilityOptions(recipientType);
    } else {
        $options.hide();
        $visibilityMessage.show();
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
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="giverCheckbox"]')
                                     .prop('checked', false);
            visibilityOptionsRow.find('input[class*="recipientCheckbox"]')
                                     .prop('checked', false);
        }
    });
    $('input[class*="giverCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });
    $('input[class*="recipientCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true);
        }
    });
    $('input[name=receiverLeaderCheckbox]').change(function() {
        var visibilityOptionsRow = $(this).closest('tr');
        visibilityOptionsRow.find('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
    });
}

function enableRow(elem, row) {
    var $visibilityOptions = $(elem).closest('form').find('.visibilityOptions');
    var $table = $visibilityOptions.find('table');
    var $tdElements = $($table.children().children()[row]).children();
    
    if ($tdElements.parent().prop('tagName') === 'tr') {
        return;
    }
    $tdElements.unwrap().wrapAll('<tr>');
}

function disableRow(elem, row) {
    var $visibilityOptions = $(elem).closest('form').find('.visibilityOptions');
    var $table = $visibilityOptions.find('table');
    var $tdElements = $($table.children().children()[row]).children();
    
    if ($tdElements.parent().prop('tagName') === 'hide') {
        return;
    }
    $tdElements.unwrap().wrapAll('<hide>');
    $tdElements.parent().hide();
}

function feedbackRecipientUpdateVisibilityOptions(elem) {
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

function feedbackGiverUpdateVisibilityOptions(elem) {
    var $elem = $(elem);
    if ($elem.val() === 'INSTRUCTORS' || $elem.val() === 'TEAMS') {
        disableRow($elem, 2);
        return;
    }
    enableRow($elem, 2);
}

function toggleVisibilityMessage(elem) {
    var $elementParent = $(elem).closest('form');
    var $options = $elementParent.find('.visibilityOptions');

    var $giverType = $elementParent.find('select[name="givertype"]');
    var $recipientType = $elementParent.find('select[name="recipienttype"]');

    $options.hide();
    var $disabledInputs = $elementParent.find('input:disabled, select:disabled');
    $disabledInputs.prop('disabled', false);

    feedbackGiverUpdateVisibilityOptions($giverType);
    feedbackRecipientUpdateVisibilityOptions($recipientType);

    getVisibilityMessage(elem);
    $disabledInputs.prop('disabled', true);
}

// Meant to be declared outside to prevent unncessary AJAX calls
var previousFormDataMap = {};

/**
 * Used to get the visibility message of a form closest
 * to the button element provided
 * @param buttonElem
 */
function getVisibilityMessage(buttonElem) {
    var $form = $(buttonElem).closest('form');
    var questionNum = $form.find('[name=questionnum]').val();
    var newQuestionNum = $('input[name=questionnum]').last().val();
    
    if (questionNum === newQuestionNum) {
        tallyCheckboxes('');
    } else {
        tallyCheckboxes(questionNum);
    }
    
    var formData = $form.serialize();
    
    var $formOptions = $form.find('.visibilityOptions');
    var $formVisibility = $form.find('.visibilityMessage');
    
    if (previousFormDataMap[questionNum] === formData) {
        $formOptions.hide();
        $formVisibility.show();
        return;
    }

    // empty current visibility message in the form
    $formVisibility.html('');
    
    var url = '/page/instructorFeedbackQuestionvisibilityMessage';
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function(data) {
            updateVisibilityMessageButton($form, true);
            
            // update stored form data
            previousFormDataMap[questionNum] = formData;
            
            $formVisibility.html(formatVisibilityMessageHtml(data.visibilityMessage));
            $formVisibility.show();
            $formOptions.hide();
        },
        error: function() {
            updateVisibilityMessageButton($form, false);
            $form.find('.visibilityOptionsLabel').click();
        }
    });
}

function updateVisibilityMessageButton($form, isLoadSuccessful) {
    var visibilityButton = $form.find('.visibilityMessageButton');
    
    var radioInput = visibilityButton.find('input[type="radio"]');
    var icon = '<span class="glyphicon glyphicon-'
               + (isLoadSuccessful ? 'eye-open' : 'warning-sign')
               + '"></span>';
    var message = isLoadSuccessful ? 'Preview Visibility'
                                   : 'Visibility preview failed to load. Click here to retry.';
    
    visibilityButton.html(icon + ' ' + message)
                    .prepend(radioInput);
}

function getVisibilityMessageIfPreviewIsActive(buttonElem) {
    var $form = $(buttonElem).closest('form');
    
    if ($form.find('.visibilityMessageButton').hasClass('active')) {
        getVisibilityMessage(buttonElem);
    }
}

function formatVisibilityMessageHtml(visibilityMessage) {
    var htmlString = 'This is the visibility as seen by the feedback giver.';
    htmlString += '<ul class="background-color-warning">';
    for (var i = 0; i < visibilityMessage.length; i++) {
        htmlString += '<li>' + visibilityMessage[i] + '</li>';
    }
    htmlString += '</ul>';
    return htmlString;
}

