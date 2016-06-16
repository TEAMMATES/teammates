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

