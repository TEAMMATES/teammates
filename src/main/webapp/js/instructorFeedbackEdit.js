var NEW_QUESTION = -1;

var questionsBeforeEdit = [];

$(document).ready(function() {
    readyFeedbackEditPage();
    bindUncommonSettingsEvents();
    updateUncommonSettingsInfo();
    hideUncommonPanels();
});

var CONFIRMATION_MODAL = '#confirmation-modal';
var CONFIRMATION_MODAL_TITLE = '#confirmation-modal-title';
var CONFIRMATION_MODAL_BODY = '#confirmation-modal-body';
var CONFIRMATION_MODAL_CANCEL = '#confirmation-modal-cancel';
var CONFIRMATION_MODAL_OK = '#confirmation-modal-ok';

var DEFAULT_CANCEL_BUTTON_TEXT = 'Cancel';
var WARNING_DELETE_RESPONSES = 'Warning: Existing responses will be deleted by your action';
var CONFIRMATION_BODY =
        '<p>Editing these fields will result in <strong>all existing responses for this question to be deleted.</strong></p>'
        + '<p>Are you sure you want to continue?</p>';
var CONFIRM_DELETE = 'Yes, continue and delete the existing responses.';

/**
 * This function is called on edit page load.
 */
function readyFeedbackEditPage() {
    // Disable all questions
    disableAllQuestions();

    // Hide option tables
    $('.visibilityOptions').hide();
    
    // Bind submit text links
    $('a[id|=questionsavechangestext]').click(function() {
        $(this).parents('form.form_question').submit();
    });
    
    // Bind submit actions
    $('form[id|=form_editquestion]').submit(function(event) {
        if ($(this).attr('editStatus') === 'mustDeleteResponses') {
            event.preventDefault();
            showConfirmationModal(WARNING_DELETE_RESPONSES, CONFIRMATION_BODY, DEFAULT_CANCEL_BUTTON_TEXT, CONFIRM_DELETE);
            checkForConfirmation(event);
        }
    });
    $('form.form_question').submit(function() {
        return checkFeedbackQuestion(this);
    });

    // Bind destructive changes
    $('form[id|=form_editquestion]').find(':input').not('.nonDestructive').change(function() {
        var editStatus = $(this).parents('form').attr('editStatus');
        if (editStatus === 'hasResponses') {
            $(this).parents('form').attr('editStatus', 'mustDeleteResponses');
        }
    });
    
    $('.dropdown-menu li').click(function() {
        showNewQuestionFrame($(this).data('questiontype'));
    });
    
    // Copy Binding
    bindCopyButton();
    bindCopyEvents();

    // Additional formatting & bindings.
    disableEditFS();
    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();
    formatNumberBoxes();
    formatCheckBoxes();
    formatQuestionNumbers();
    collapseIfPrivateSession();
    
    setupFsCopyModal();
    
    bindAssignWeightsCheckboxes();
    
    // Bind feedback session edit form submission
    bindFeedbackSessionEditFormSubmission();
}

function bindFeedbackSessionEditFormSubmission() {
    $('#form_feedbacksession').submit(function(event) {
        // Prevent form submission
        event.preventDefault();
        
        var $form = $(event.target);
        // Use Ajax to submit form data
        $.ajax({
            url: '/page/instructorFeedbackEditSave',
            type: 'POST',
            data: $form.serialize(),
            beforeSend: function() {
                clearStatusMessages();
            },
            success: function(result) {
                
                if (result.hasError) {
                    setStatusMessage(result.statusForAjax, StatusType.DANGER);
                } else {
                    setStatusMessage(result.statusForAjax, StatusType.SUCCESS);
                    disableEditFS();
                }
                
                // focus on status message
                scrollToElement($('#statusMessagesToUser'), { offset: ($('.navbar').height() + 30) * -1 });
            }
        });
    });
}

/**
 * Disables the editing of feedback session details.
 */
function disableEditFS() {
    // Save then disable fields
    getCustomDateTimeFields().each(function() {
        $(this).data('last', $(this).prop('disabled'));
    });
    $('#form_feedbacksession').find('text,input,button,textarea,select')
                                  .prop('disabled', true);
    $('#fsEditLink').show();
    $('#fsSaveLink').hide();
    $('#button_submit').hide();
}

/**
 * Disables all questions
 */
function disableAllQuestions() {
    var numQuestions = $('.questionTable').length;
    for (var i = 0; i < numQuestions; i++) {
        disableQuestion(i);
    }
}

/**
 * Enables the editing of feedback session details.
 */
function enableEditFS() {
    var $customDateTimeFields = getCustomDateTimeFields();

    $customDateTimeFields.each(function() {
        $(this).prop('disabled', $(this).data('last'));
    });
    
    // instructors should not be able to prevent Session Opening reminder from getting sent
    // as students without accounts need to receive the session opening email to respond
    var $sessionOpeningReminder = $('#sendreminderemail_open');
    
    $('#form_feedbacksession').find('text,input,button,textarea,select')
                              .not($customDateTimeFields)
                              .not($sessionOpeningReminder)
                              .not('.disabled')
                              .prop('disabled', false);
    $('#fsEditLink').hide();
    $('#fsSaveLink').show();
    $('#button_submit').show();
}

function getCustomDateTimeFields() {
    return $('#' + FEEDBACK_SESSION_PUBLISHDATE).add('#' + FEEDBACK_SESSION_PUBLISHTIME)
                                                .add('#' + FEEDBACK_SESSION_VISIBLEDATE)
                                                .add('#' + FEEDBACK_SESSION_VISIBLETIME);
}

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
 * Enables editing of question fields and enables the "save changes" button for
 * the given question number, while hiding the edit link. Does the opposite for all other questions.
 * @param questionNum
 */
function enableEdit(questionNum, maxQuestions) {
    var i = maxQuestions;
    while (i) {
        if (questionNum === i) {
            backupQuestion(i);
            enableQuestion(i);
        } else {
            disableQuestion(i);
        }
        i--;
    }
    
    return false;
}

/**
 * Creates a copy of the original question before any new edits
 * @param questionNum
 */
function backupQuestion(questionNum) {
    questionsBeforeEdit[questionNum] = questionsBeforeEdit[questionNum]
                                || $('#questionTable' + questionNum + ' > .panel-body').html();
}

/**
 * Enables question fields and "save changes" button for the given question number,
 * and hides the edit link.
 * @param questionNum
 */
function enableQuestion(questionNum) {
    var $currentQuestionTable = $('#questionTable' + questionNum);
    
    $currentQuestionTable.find('text,button,textarea,select,input')
                         .not('[name="receiverFollowerCheckbox"]')
                         .not('.disabled_radio')
                         .prop('disabled', false);
    
    $currentQuestionTable.find('.removeOptionLink').show();
    $currentQuestionTable.find('.addOptionLink').show();

    $currentQuestionTable.find('#rubricAddChoiceLink-' + questionNum).show();
    $currentQuestionTable.find('#rubricAddSubQuestionLink-' + questionNum).show();
    $currentQuestionTable.find('.rubricRemoveChoiceLink-' + questionNum).show();
    $currentQuestionTable.find('.rubricRemoveSubQuestionLink-' + questionNum).show();

    if ($('#generateOptionsCheckbox-' + questionNum).prop('checked')) {
        $('#mcqChoiceTable-' + questionNum).hide();
        $('#msqChoiceTable-' + questionNum).hide();
        $('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $('#mcqGenerateForSelect-' + questionNum).prop('disabled', false);
        $('#msqGenerateForSelect-' + questionNum).prop('disabled', false);
    } else {
        $('#mcqChoiceTable-' + questionNum).show();
        $('#msqChoiceTable-' + questionNum).show();
        $('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $('#mcqGenerateForSelect-' + questionNum).prop('disabled', true);
        $('#msqGenerateForSelect-' + questionNum).prop('disabled', true);
    }
    
    if ($('#constSumToRecipients-' + questionNum).val() === 'true') {
        $('#constSumOptionTable-' + questionNum).hide();
        $('#constSumOption_Option-' + questionNum).hide();
    } else {
        $('#constSumOptionTable-' + questionNum).show();
        $('#constSumOption_Recipient-' + questionNum).hide();
    }
    
    $('#constSumOption_distributeUnevenly-' + questionNum).prop('disabled', false);
    
    if ($('#questionTable' + questionNum).parent().find('input[name="questiontype"]').val() === 'CONTRIB') {
        fixContribQnGiverRecipient(questionNum);
        setContribQnVisibilityFormat(questionNum);
    }
    
    $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).hide();
    $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).show();
    $('#' + FEEDBACK_QUESTION_CANCELEDIT + '-' + questionNum).show();
    $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('edit');
    $('#button_question_submit-' + questionNum).show();
}

function enableNewQuestion() {
    var newQnSuffix = 'New';
    
    var $currentQuestionTableSuffix = $('#questionTable' + newQnSuffix);
    var $currentQuestionTableNumber = $('#questionTable' + NEW_QUESTION);
    
    $currentQuestionTableSuffix.find('text,button,textarea,select,input')
                               .not('[name="receiverFollowerCheckbox"]')
                               .not('.disabled_radio')
                               .prop('disabled', false);
    $currentQuestionTableSuffix.find('.removeOptionLink').show();
    $currentQuestionTableSuffix.find('.addOptionLink').show();

    $currentQuestionTableNumber.find('#rubricAddChoiceLink-' + NEW_QUESTION).show();
    $currentQuestionTableNumber.find('#rubricAddSubQuestionLink-' + NEW_QUESTION).show();
    $currentQuestionTableSuffix.find('#rubricWeights-' + NEW_QUESTION).hide();
    $currentQuestionTableNumber.find('.rubricRemoveChoiceLink-' + NEW_QUESTION).show();
    $currentQuestionTableNumber.find('.rubricRemoveSubQuestionLink-' + NEW_QUESTION).show();

    moveAssignWeightsCheckbox($currentQuestionTableSuffix.find('#rubricAssignWeights-' + NEW_QUESTION));

    if ($('#generateOptionsCheckbox-' + NEW_QUESTION).prop('checked')) {
        $('#mcqChoiceTable-' + NEW_QUESTION).hide();
        $('#msqChoiceTable-' + NEW_QUESTION).hide();
        $('#mcqGenerateForSelect-' + NEW_QUESTION).prop('disabled', false);
        $('#msqGenerateForSelect-' + NEW_QUESTION).prop('disabled', false);
    } else {
        $('#mcqChoiceTable-' + NEW_QUESTION).show();
        $('#msqChoiceTable-' + NEW_QUESTION).show();
        $('#mcqGenerateForSelect-' + NEW_QUESTION).prop('disabled', true);
        $('#msqGenerateForSelect-' + NEW_QUESTION).prop('disabled', true);
    }
    
    $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + NEW_QUESTION).hide();
    $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + NEW_QUESTION).show();
    $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + NEW_QUESTION).val('edit');
    $('#button_question_submit-' + NEW_QUESTION).show();
}

/**
 * Disable question fields and "save changes" button for the given question number,
 * and shows the edit link.
 * @param questionNum
 */
function disableQuestion(questionNum) {
    var $currentQuestionTable = $('#questionTable' + questionNum);

    $currentQuestionTable.find('text,button,textarea,select,input').prop('disabled', true);
    
    $currentQuestionTable.find('#mcqAddOptionLink').hide();
    $currentQuestionTable.find('#msqAddOptionLink').hide();
    $currentQuestionTable.find('.removeOptionLink').hide();
    
    /* Check whether generate options for students/instructors/teams is selected
       If so, hide 'add Other option' */
    if ($currentQuestionTable.find('#generateOptionsCheckbox-' + questionNum).prop('checked')) {
        $currentQuestionTable.find('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $currentQuestionTable.find('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
    } else {
        $currentQuestionTable.find('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $currentQuestionTable.find('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
    }

    $currentQuestionTable.find('#rubricAddChoiceLink-' + questionNum).hide();
    $currentQuestionTable.find('#rubricAddSubQuestionLink-' + questionNum).hide();
    $currentQuestionTable.find('.rubricRemoveChoiceLink-' + questionNum).hide();
    $currentQuestionTable.find('.rubricRemoveSubQuestionLink-' + questionNum).hide();
    
    moveAssignWeightsCheckbox($currentQuestionTable.find('input[id^="rubricAssignWeights"]'));

    if (!hasAssignedWeights(questionNum)) {
        $currentQuestionTable.find('#rubricWeights-' + questionNum).hide();
    }

    $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).show();
    $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).hide();
    $('#button_question_submit-' + questionNum).hide();
}

/**
 * Pops up confirmation dialog whether to delete specified question
 * @param question questionNum
 * @returns
 */
function deleteQuestion(questionNum) {
    if (questionNum === NEW_QUESTION) {
        location.reload();
        return false;
    } else if (confirm('Are you sure you want to delete this question?')) {
        $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('delete');
        $('#form_editquestion-' + questionNum).submit();
        return true;
    }
    return false;
}

/**
 * Allows users to cancel editing questions
 */
function cancelEdit(questionNum) {
    var confirmationMsg = questionNum === NEW_QUESTION
                        ? 'Are you sure you want to cancel adding this question?'
                        : 'Are you sure you want to cancel your changes?';
    if (confirm(confirmationMsg)) {
        discardChanges(questionNum);
    }
}

/**
 * Discards new changes made and restores the original question
 * @param questionNum
 */
function discardChanges(questionNum) {
    if (questionNum === NEW_QUESTION) {
        hideNewQuestionAndShowNewQuestionForm();
    } else {
        $('#questionTable' + questionNum + ' > .panel-body').html(questionsBeforeEdit[questionNum]);

        $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).show();
        $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).hide();
        $('#' + FEEDBACK_QUESTION_CANCELEDIT + '-' + questionNum).hide();
        $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('');
        $('#button_question_submit-' + questionNum).hide();
    }

    // re-attach onChange event to show/hide numEntitiesBox according to recipient type
    $('#' + FEEDBACK_QUESTION_RECIPIENTTYPE + '-' + questionNum).change(updateVisibilityOfNumEntitiesBox);
}

function hideNewQuestionAndShowNewQuestionForm() {
    $('#questionTableNew').hide();
    $('#addNewQuestionTable').show();
}

/**
 * 1. Disallow non-numeric input to all inputs expecting numbers
 * 2. Initialize the visibility of 'Number of Recipients Box' according to the participant type (visible only
 * when participant type is STUDENTS OR TEAMS)
 * 3. Bind onChange of recipientType to modify numEntityBox visibility
 */
function formatNumberBoxes() {
    disallowNonNumericEntries($('input.numberOfEntitiesBox'), false, false);
    disallowNonNumericEntries($('input.minScaleBox'), false, true);
    disallowNonNumericEntries($('input.maxScaleBox'), false, true);
    disallowNonNumericEntries($('input.stepBox'), true, false);
    disallowNonNumericEntries($('input.pointsBox'), false, false);
    disallowNonNumericEntries($('input[id^="rubricWeight"]'), true, true);
    
    $('select[name=' + FEEDBACK_QUESTION_RECIPIENTTYPE + ']').each(updateVisibilityOfNumEntitiesBox)
                                                             .change(updateVisibilityOfNumEntitiesBox);
}

var updateVisibilityOfNumEntitiesBox = function() {
    var questionNum = $(this).prop('id').split('-')[1];
    questionNum = questionNum || '';

    var value = $(this).val();

    formatNumberBox(value, questionNum);
};

/**
 * Hides/shows the "Number of Recipients Box" of the question
 * depending on the participant type and formats the label text for it.
 * @param value, questionNum
 */
function formatNumberBox(value, questionNum) {
    if (value === 'STUDENTS' || value === 'TEAMS') {
        $('div.numberOfEntitiesElements' + questionNum).show();
        
        var $span = $('span#' + FEEDBACK_QUESTION_NUMBEROFENTITIES + '_text_inner-' + questionNum);
        $span.html(value === 'STUDENTS' ? 'students' : 'teams');
    } else {
        $('div.numberOfEntitiesElements' + questionNum).hide();
    }
    
    tallyCheckboxes(questionNum);
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
 * Shows the new question div frame and scrolls to it
 */
function showNewQuestionFrame(type) {
    $('#questiontype').val(type);
	
    copyOptions();
    prepareQuestionForm(type);
    $('#questionTableNew').show();
    enableNewQuestion();
    
    $('#addNewQuestionTable').hide();
    $('#empty_message').hide();
    scrollToElement($('#questionTableNew')[0], { duration: 1000 });
    $('#questionTableNew').find('.visibilityOptions').hide();
    getVisibilityMessage($('#questionTableNew').find('.visibilityMessageButton'));
}

function hideAllNewQuestionForms() {
    $('#mcqForm').hide();
    $('#msqForm').hide();
    $('#numScaleForm').hide();
    $('#constSumForm').hide();
    $('#rubricForm').hide();
    $('#contribForm').hide();
    $('#rankOptionsForm').hide();
    $('#rankRecipientsForm').hide();
}

function prepareQuestionForm(type) {
    switch (type) {
    case 'TEXT':
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_TEXT);
        
        hideAllNewQuestionForms();
        break;
    case 'MCQ':
        $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_MCQ);
        
        hideAllNewQuestionForms();
        
        $('#mcqForm').show();
        break;
    case 'MSQ':
        $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_MSQ);
        
        hideAllNewQuestionForms();
        
        $('#msqForm').show();
        break;
    case 'NUMSCALE':
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_NUMSCALE);
        
        hideAllNewQuestionForms();
        
        $('#numScaleForm').show();
        $('#' + FEEDBACK_QUESTION_TEXT).attr('placeholder', 'e.g. Rate the class from 1 (very bad) to 5 (excellent)');
        break;
    case 'CONSTSUM_OPTION':
        $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
        $('#' + FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS + '-' + NEW_QUESTION).val('false');
        $('#constSumOption_Recipient-' + NEW_QUESTION).hide();
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION);
        
        hideAllNewQuestionForms();
        
        $('#constSumForm').show();
        break;
    case 'CONSTSUM_RECIPIENT':
        $('#' + FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS + '-' + NEW_QUESTION).val('true');
        $('#constSumOption_Option-' + NEW_QUESTION).hide();
        hideConstSumOptionTable(NEW_QUESTION);
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT);
        
        hideAllNewQuestionForms();
        
        $('#constSumForm').show();
        var optionText = $('#constSum_labelText-' + NEW_QUESTION).text();
        $('#constSum_labelText-' + NEW_QUESTION).text(optionText.replace('option', 'recipient'));
        var tooltipText = $('#constSum_tooltipText-' + NEW_QUESTION).attr('data-original-title');
        $('#constSum_tooltipText-' + NEW_QUESTION).attr('data-original-title', tooltipText.replace('option', 'recipient'));
        break;
    case 'CONTRIB':
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_CONTRIB);
        
        hideAllNewQuestionForms();
        
        $('#contribForm').show();
        fixContribQnGiverRecipient();
        setDefaultContribQnVisibility();
        setContribQnVisibilityFormat();
        break;
    case 'RUBRIC':
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_RUBRIC);
        
        hideAllNewQuestionForms();
        
        $('#rubricForm').show();
        break;
    case 'RANK_OPTIONS':
        $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
        $('#' + FEEDBACK_QUESTION_RANKTORECIPIENTS + '-' + NEW_QUESTION).val('false');
        $('#rankOption_Recipient-' + NEW_QUESTION).hide();
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_RANK_OPTION);
        
        hideAllNewQuestionForms();
        
        $('#rankOptionsForm').show();
        break;
    case 'RANK_RECIPIENTS':
        $('#' + FEEDBACK_QUESTION_RANKTORECIPIENTS + '-' + NEW_QUESTION).val('true');
        $('#rankOption_Option-' + NEW_QUESTION).hide();
        hideRankOptionTable(NEW_QUESTION);
        $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_RANK_RECIPIENT);
        
        hideAllNewQuestionForms();
        
        $('#rankRecipientsForm').show();
        break;
    default:
        // do nothing if the question type is not recognized, which should not happen
        break;
    }
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

/**
 * Copy options (Feedback giver, recipient, and all check boxes)
 * from the previous question
 */
function copyOptions() {
    // If there is one or less questions, there's no need to copy.
    if ($('div[class~="questionTable"]').size() < 2) {
        return;
    }
    
    // Feedback giver setup
    var $prevGiver = $('select[name="givertype"]').eq(-2);
    var $currGiver = $('select[name="givertype"]').last();
    
    $currGiver.val($prevGiver.val());
    
    // Feedback recipient setup
    var $prevRecipient = $('select[name="recipienttype"]').eq(-2);
    var $currRecipient = $('select[name="recipienttype"]').last();
    
    $currRecipient.val($prevRecipient.val());
    
    // Number of recipient setup
    formatNumberBox($currRecipient.val(), '');
    var $prevRadioButtons = $('table[class~="questionTable"]').eq(-2).find('input[name="numofrecipientstype"]');
    var $currRadioButtons = $('table[class~="questionTable"]').last().find('input[name="numofrecipientstype"]');
    
    $currRadioButtons.each(function(index) {
        $(this).prop('checked', $prevRadioButtons.eq(index).prop('checked'));
    });
    
    var $prevNumOfRecipients = $('input[name="numofrecipients"]').eq(-2);
    var $currNumOfRecipients = $('input[name="numofrecipients"]').last();
    
    $currNumOfRecipients.val($prevNumOfRecipients.val());
    
    // Check boxes setup
    var $prevTable = $('.dataTable').eq(-2).find('.visibilityCheckbox');
    var $currTable = $('.dataTable').last().find('.visibilityCheckbox');
    
    $currTable.each(function(index) {
        $(this).prop('checked', $prevTable.eq(index).prop('checked'));
    });
    feedbackGiverUpdateVisibilityOptions($currGiver);
    feedbackRecipientUpdateVisibilityOptions($currRecipient);
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

/**
 * Sets the correct initial question number from the value field
 */
function formatQuestionNumbers() {
    var $questions = $('div[class~="questionTable"]');
    
    $questions.each(function(index) {
        var $selector = $(this).find('.questionNumber');
        $selector.val(index + 1);
        if (index !== $questions.size() - 1) {
            $selector.prop('disabled', true);
        }
    });
}

function getQuestionLink(questionNum) {
    var courseid = $('input[name="courseid"]').val();
    var fsname = encodeURIComponent($('input[name="fsname"]').val());
    
    var questionId = $('#form_editquestion-' + questionNum).find('input[name="questionid"]').val();
    
    var giverType = $('#givertype-' + questionNum).val();
    
    var actionUrl = giverType === 'STUDENTS' || giverType === 'TEAMS'
                  ? '/page/studentFeedbackQuestionSubmissionEditPage'
                  : '/page/instructorFeedbackQuestionSubmissionEditPage';
    
    var questionLink = window.location.protocol + '//'
                     + window.location.host + actionUrl
                     + '?courseid=' + courseid
                     + '&fsname=' + fsname
                     + '&questionid=' + questionId;
    
    setStatusMessage('Link for question ' + questionNum + ': ' + questionLink, StatusType.WARNING);
}

function toParameterFormat(str) {
    return str.replace(/\s/g, '+');
}

function bindCopyButton() {
    $('#button_copy').on('click', function(e) {
        e.preventDefault();
        
        var questionRows = $('#copyTableModal >tbody>tr');
        if (questionRows.length) {
            setStatusMessage('', StatusType.WARNING);
            $('#copyModal').modal('show');
        } else {
            setStatusMessage(FEEDBACK_QUESTION_COPY_INVALID, StatusType.DANGER);
        }
       
        return false;
    });

    $('#button_copy_submit').on('click', function(e) {
        e.preventDefault();

        var index = 0;
        var hasRowSelected = false;

        $('#copyTableModal >tbody>tr').each(function() {
            var input = $(this).children('input:first');
            
            if (!input.length) {
                return true;
            }
            if ($(this).hasClass('row-selected')) {
                $(input).attr('name', 'questionid-' + index++);
                hasRowSelected = true;
            }
        });

        if (hasRowSelected) {
            $('#copyModalForm').submit();
        } else {
            setStatusMessage('No questions are selected to be copied', StatusType.DANGER);
            $('#copyModal').modal('hide');
        }

        return false;
    });
}

var numRowsSelected = 0;

function bindCopyEvents() {

    $('#copyTableModal >tbody>tr').on('click', function(e) {
        e.preventDefault();
        
        if ($(this).hasClass('row-selected')) {
            $(this).removeClass('row-selected');
            $(this).children('td:first').html('<input type="checkbox">');
            numRowsSelected--;
        } else {
            $(this).addClass('row-selected');
            $(this).children('td:first').html('<input type="checkbox" checked>');
            numRowsSelected++;
        }

        var $button = $('#button_copy_submit');
        
        $button.prop('disabled', numRowsSelected <= 0);

        return false;
    });
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

/**
 *  ===========================================================================
 *  Code for specific question types
 *  ===========================================================================
 */

function getQuestionIdSuffix(questionNum) {
    var isValidQuestionNumber = questionNum > 0 || questionNum === NEW_QUESTION;
    
    var idSuffix = isValidQuestionNumber ? '-' + questionNum : '';
    return idSuffix;
}

/**
 * ----------------------------------------------------------------------------
 * Mcq Question
 * ----------------------------------------------------------------------------
 */

function addMcqOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var curNumberOfChoiceCreated =
            parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
    
    $('<div id="mcqOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group">'
              + '<span class="input-group-addon">'
                 + '<input type="radio" disabled>'
              + '</span>'
              + '<input type="text" name="' + FEEDBACK_QUESTION_MCQCHOICE + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_MCQCHOICE + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control mcqOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button type="button" class="btn btn-default removeOptionLink" id="mcqRemoveOptionLink" '
                          + 'onclick="removeMcqOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#mcqAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $thisRow = $('#mcqOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
        
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function toggleMcqGeneratedOptions(checkbox, questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    if (checkbox.checked) {
        $('#mcqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', true);
        $('#mcqChoiceTable' + idSuffix).hide();
        $('#mcqGenerateForSelect' + idSuffix).prop('disabled', false);
        $('#mcqOtherOptionFlag' + idSuffix).closest('.checkbox').hide();
        $('#generatedOptions' + idSuffix).attr('value',
                                               $('#mcqGenerateForSelect' + idSuffix).prop('value'));
    } else {
        $('#mcqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', false);
        $('#mcqChoiceTable' + idSuffix).show();
        $('#mcqGenerateForSelect' + idSuffix).prop('disabled', true);
        $('#mcqOtherOptionFlag' + idSuffix).closest('.checkbox').show();
        $('#generatedOptions' + idSuffix).attr('value', 'NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    questionId = '#form_editquestion-' + questionNum;
    idSuffix = getQuestionIdSuffix(questionNum);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMcqGenerateFor(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    $('#generatedOptions' + idSuffix).attr('value',
                                           $('#mcqGenerateForSelect' + idSuffix).prop('value'));
}

/**
 * ----------------------------------------------------------------------------
 * Msq Question
 * ----------------------------------------------------------------------------
 */

function addMsqOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);

    var curNumberOfChoiceCreated =
            parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $('<div id="msqOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group">'
              + '<span class="input-group-addon">'
                 + '<input type="checkbox" disabled>'
              + '</span>'
              + '<input type="text" name="' + FEEDBACK_QUESTION_MSQCHOICE + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_MSQCHOICE + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control msqOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button type="button" class="btn btn-default removeOptionLink" id="msqRemoveOptionLink" '
                          + 'onclick="removeMsqOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#msqAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMsqOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $thisRow = $('#msqOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
    
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function toggleMsqGeneratedOptions(checkbox, questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    if (checkbox.checked) {
        $('#msqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', true);
        $('#msqChoiceTable' + idSuffix).hide();
        $('#msqGenerateForSelect' + idSuffix).prop('disabled', false);
        $('#msqOtherOptionFlag' + idSuffix).closest('.checkbox').hide();
        $('#generatedOptions' + idSuffix).attr('value',
                                               $('#msqGenerateForSelect' + idSuffix).prop('value'));
    } else {
        $('#msqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', false);
        $('#msqChoiceTable' + idSuffix).show();
        $('#msqGenerateForSelect' + idSuffix).prop('disabled', true);
        $('#msqOtherOptionFlag' + idSuffix).closest('.checkbox').show();
        $('#generatedOptions' + idSuffix).attr('value', 'NONE');
    }
}

function changeMsqGenerateFor(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);

    $('#generatedOptions' + idSuffix).attr('value',
                                           $('#msqGenerateForSelect' + idSuffix).prop('value'));
}

/**
 * ----------------------------------------------------------------------------
 * NumScale Question
 * ----------------------------------------------------------------------------
 */

function roundToThreeDp(num) {
    return parseFloat(num.toFixed(3));
}

function updateNumScalePossibleValues(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var min = parseInt($('#minScaleBox' + idSuffix).val());
    var max = parseInt($('#maxScaleBox' + idSuffix).val());
    var step = parseFloat($('#stepBox' + idSuffix).val());
    
    if (max <= min) {
        max = min + 1;
        $('#maxScaleBox' + idSuffix).val(max);
    }
    
    step = roundToThreeDp(step);
    if (step === 0) {
        step = 0.001;
    }
    
    var $stepBox = $('#stepBox' + idSuffix);
    $stepBox.val(isNaN(step) ? '' : step);

    var possibleValuesCount = Math.floor(roundToThreeDp((max - min) / step)) + 1;
    var largestValueInRange = min + (possibleValuesCount - 1) * step;
    var $numScalePossibleValues = $('#numScalePossibleValues' + idSuffix);
    var possibleValuesString;
    if (roundToThreeDp(largestValueInRange) !== max) {
        $numScalePossibleValues.css('color', 'red');

        if (isNaN(min) || isNaN(max) || isNaN(step)) {
            possibleValuesString = '[Please enter valid numbers for all the options.]';
        } else {
            possibleValuesString = '[The interval ' + min.toString() + ' - ' + max.toString()
                                 + ' is not divisible by the specified increment.]';
        }

        $numScalePossibleValues.text(possibleValuesString);
        return false;
    }
    $numScalePossibleValues.css('color', 'black');
    possibleValuesString = '[Based on the above settings, acceptable responses are: ';
    
    // step is 3 d.p. at most, so round it after * 1000.
    if (possibleValuesCount > 6) {
        possibleValuesString += min.toString() + ', '
                              + (Math.round((min + step) * 1000) / 1000).toString() + ', '
                              + (Math.round((min + 2 * step) * 1000) / 1000).toString() + ', ..., '
                              + (Math.round((max - 2 * step) * 1000) / 1000).toString() + ', '
                              + (Math.round((max - step) * 1000) / 1000).toString() + ', '
                              + max.toString();
    } else {
        possibleValuesString += min.toString();
        var cur = min + step;
        while (max - cur >= -1e-9) {
            possibleValuesString += ', ' + (Math.round(cur * 1000) / 1000).toString();
            cur += step;
        }
    }
    
    possibleValuesString += ']';
    $numScalePossibleValues.text(possibleValuesString);
    return true;
}

/**
 * ----------------------------------------------------------------------------
 * Constant Sum Question
 * ----------------------------------------------------------------------------
 */

function updateConstSumPointsValue(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    if ($('#' + FEEDBACK_QUESTION_CONSTSUMPOINTS + idSuffix).val() < 1) {
        $('#' + FEEDBACK_QUESTION_CONSTSUMPOINTS + idSuffix).val(1);
    }
}

function addConstSumOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var curNumberOfChoiceCreated = parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $('<div class="margin-bottom-7px" id="constSumOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group width-100-pc">'
              + '<input type="text" name="' + FEEDBACK_QUESTION_CONSTSUMOPTION + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_CONSTSUMOPTION + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control constSumOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button class="btn btn-default removeOptionLink" id="constSumRemoveOptionLink" '
                          + 'onclick="removeConstSumOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" '
                          + 'tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#constSumAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideConstSumOptionTable(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    $('#' + FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE + idSuffix).hide();
}

function removeConstSumOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    var $thisRow = $('#constSumOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
    
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

/**
 * ----------------------------------------------------------------------------
 * Contribution Question
 * ----------------------------------------------------------------------------
 */

function setDefaultContribQnVisibility(questionNum) {
    var idSuffix = questionNum || 'New';
    var idSuffix2 = questionNum || '';
    
    $currentQuestionTable = $('#questionTable' + idSuffix);

    $currentQuestionTable.find('input.visibilityCheckbox').prop('checked', false);
    // All except STUDENTS can see answer
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="answerCheckbox' + idSuffix2 + '"]')
                         .not('[value="STUDENTS"]').prop('checked', true);
    // Only instructor can see giver
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="giverCheckbox' + idSuffix2 + '"]')
                         .filter('[value="INSTRUCTORS"]').prop('checked', true);
    // Recipient and instructor can see recipient
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="recipientCheckbox' + idSuffix2 + '"]')
                         .filter('[value="INSTRUCTORS"],[value="RECEIVER"]').prop('checked', true);

}

function setContribQnVisibilityFormat(questionNum) {
    var idSuffix = questionNum || 'New';

    $currentQuestionTable = $('#questionTable' + idSuffix);

    // Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $currentQuestionTable.find('input.visibilityCheckbox').off('change');
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="answerCheckbox"]').change(function() {
        if (!$(this).prop('checked')) {
            if ($(this).val() === 'RECEIVER'
                    || $(this).val() === 'OWN_TEAM_MEMBERS'
                    || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
                $currentQuestionTable.find('input.visibilityCheckbox')
                                     .filter('input[class*="giverCheckbox"],input[class*="recipientCheckbox"]')
                                     .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                     .prop('checked', false);
            } else {
                var visibilityOptionsRow = $(this).closest('tr');
                visibilityOptionsRow.find('input[class*="giverCheckbox"]')
                                         .prop('checked', false);
                visibilityOptionsRow.find('input[class*="recipientCheckbox"]')
                                         .prop('checked', false);
            }
            
        }
        
        if ($(this).val() === 'RECEIVER'
                || $(this).val() === 'OWN_TEAM_MEMBERS'
                || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox')
                                 .filter('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
        }
        
        if ($(this).val() === 'RECEIVER'
                || $(this).val() === 'OWN_TEAM_MEMBERS'
                || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox')
                                 .filter('[class*="answerCheckbox"]')
                                 .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                 .prop('checked', $(this).prop('checked'));
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="giverCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="recipientCheckbox"]').change(function() {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input[class*="answerCheckbox"]')
                                     .prop('checked', true)
                                     .trigger('change');
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]').change(function() {
        var visibilityOptionsRow = $(this).closest('tr');
        visibilityOptionsRow.find('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
    });

}

function fixContribQnGiverRecipient(questionNum) {
    var idSuffix = questionNum ? '-' + questionNum : '';
    var $giverType = $('#givertype' + idSuffix);
    var $recipientType = $('#recipienttype' + idSuffix);

    // Fix giver->recipient to be STUDENT->OWN_TEAM_MEMBERS_INCLUDING_SELF
    $giverType.find('option').not('[value="STUDENTS"]').hide();
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').hide();

    $giverType.find('option').not('[value="STUDENTS"]').prop('disabled', true);
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('disabled', true);

    $giverType.find('option').filter('[value="STUDENTS"]').prop('selected', true);
    $recipientType.find('option').filter('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('selected', true);
}

/**
 * ----------------------------------------------------------------------------
 * Rubric Question
 * ----------------------------------------------------------------------------
 */

function addRubricRow(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var numberOfRows = parseInt($('#rubricNumRows' + idSuffix).val());
    var numberOfCols = parseInt($('#rubricNumCols' + idSuffix).val());

    var newRowNumber = numberOfRows + 1;

    var rubricRowTemplate =
        '<tr id="rubricRow-${qnIndex}-${row}">'
          + '<td>'
              + '<div class="col-sm-12 input-group">'
                  + '<span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-${qnIndex}" '
                          + 'id="rubricRemoveSubQuestionLink-${qnIndex}-${row}" '
                          + 'onclick="removeRubricRow(${row},${qnIndex})" '
                          + 'onmouseover="highlightRubricRow(${row}, ${qnIndex}, true)" '
                          + 'onmouseout="highlightRubricRow(${row}, ${qnIndex}, false)">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</span>'
                  + '<textarea class="form-control" rows="3" '
                          + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}-${qnIndex}-${row}" '
                          + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}-${row}">'
                      + '${subQuestion}'
                  + '</textarea>'
              + '</div>'
          + '</td>'
          + '${rubricRowBodyFragments}'
      + '</tr>';

    var rubricRowFragmentTemplate =
        '<td class="align-center rubricCol-${qnIndex}-${col}">'
        + '<textarea class="form-control" rows="3" '
                + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${qnIndex}-${row}-${col}" '
                + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${row}-${col}">'
            + '${description}'
        + '</textarea>'
      + '</td>';

    var rubricRowBodyFragments = '';
    // Create numberOfCols of <td>'s
    for (var cols = 0; cols < numberOfCols; cols++) {
        if (!$('.rubricCol' + idSuffix + '-' + cols).length) {
            continue;
        }
        var fragment = rubricRowFragmentTemplate;
        fragment = replaceAll(fragment, '${qnIndex}', questionNum);
        fragment = replaceAll(fragment, '${row}', newRowNumber - 1);
        fragment = replaceAll(fragment, '${col}', cols);
        fragment = replaceAll(fragment, '${description}', '');
        fragment = replaceAll(fragment, '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}', 'rubricDesc');
        rubricRowBodyFragments += fragment;
    }

    // Create new rubric row
    var newRubricRow = rubricRowTemplate;
    newRubricRow = replaceAll(newRubricRow, '${qnIndex}', questionNum);
    newRubricRow = replaceAll(newRubricRow, '${row}', newRowNumber - 1);
    newRubricRow = replaceAll(newRubricRow, '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}', 'rubricSubQn');
    newRubricRow = replaceAll(newRubricRow, '${subQuestion}', '');
    newRubricRow = replaceAll(newRubricRow, '${rubricRowBodyFragments}', rubricRowBodyFragments);

    // Row to insert new row after
    var lastRow = $('#rubricEditTable' + idSuffix + ' tr:last');
    $(newRubricRow).insertAfter(lastRow);

    // Increment
    $('#rubricNumRows' + idSuffix).val(newRowNumber);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function addRubricCol(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var numberOfRows = parseInt($('#rubricNumRows' + idSuffix).val());
    var numberOfCols = parseInt($('#rubricNumCols' + idSuffix).val());
    
    var newColNumber = numberOfCols + 1;

    // Insert header <th>
    var rubricHeaderFragmentTemplate =
       '<th class="rubricCol-${qnIndex}-${col}">'
          + '<div class="input-group">'
              + '<input type="text" class="col-sm-12 form-control" value="${rubricChoiceValue}" '
                      + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}-${qnIndex}-${col}" '
                      + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}-${col}">'
              + '<span class="input-group-addon btn btn-default rubricRemoveChoiceLink-${qnIndex}" '
                      + 'id="rubricRemoveChoiceLink-${qnIndex}-${col}" onclick="removeRubricCol(${col}, ${qnIndex})" '
                      + 'onmouseover="highlightRubricCol(${col}, ${qnIndex}, true)" '
                      + 'onmouseout="highlightRubricCol(${col}, ${qnIndex}, false)">'
                  + '<span class="glyphicon glyphicon-remove"></span>'
              + '</span>'
          + '</div>'
      + '</th>';

    var rubricHeaderFragment = rubricHeaderFragmentTemplate;
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${qnIndex}', questionNum);
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${col}', newColNumber - 1);
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${rubricChoiceValue}', '');
    rubricHeaderFragment = replaceAll(rubricHeaderFragment,
                                      '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}',
                                      'rubricChoice');

    // Insert after last <th>
    var lastTh = $('#rubricEditTable' + idSuffix).find('tr:first').children().last();
    $(rubricHeaderFragment).insertAfter(lastTh);
    
    // Insert weight <th>
    var rubricWeightFragmentTemplate =
        '<th class="rubricCol-${qnIndex}-${col}">'
           + '<input type="number" class="form-control nonDestructive" value="${rubricWeight}" '
                   + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}-${qnIndex}-${col}" '
                   + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}-${col}" step="0.01">'
      + '</th>';

    var rubricWeightFragment = rubricWeightFragmentTemplate;
    rubricWeightFragment = replaceAll(rubricWeightFragment, '${qnIndex}', questionNum);
    rubricWeightFragment = replaceAll(rubricWeightFragment, '${col}', newColNumber - 1);
    rubricWeightFragment = replaceAll(rubricWeightFragment, '${rubricWeight}', 0);
    rubricWeightFragment = replaceAll(rubricWeightFragment,
                                      '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT}',
                                      'rubricWeight');

    // Insert after last <th>
    var lastWeightCell = $('#rubricWeights-' + questionNum + ' th:last');
    $(rubricWeightFragment).insertAfter(lastWeightCell);
    
    disallowNonNumericEntries($('#rubricWeight-' + questionNum + '-' + (newColNumber - 1)), true, true);

    // Insert body <td>'s
    var rubricRowFragmentTemplate =
        '<td class="align-center rubricCol-${qnIndex}-${col}">'
        + '<textarea class="form-control" rows="3" '
                + 'id="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${qnIndex}-${row}-${col}" '
                + 'name="${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${row}-${col}">'
            + '${description}'
        + '</textarea>'
      + '</td>';

    // Create numberOfRows of <td>'s
    for (var rows = 0; rows < numberOfRows; rows++) {
        if (!$('#rubricRow' + idSuffix + '-' + rows).length) {
            continue;
        }
        var fragment = rubricRowFragmentTemplate;
        fragment = replaceAll(fragment, '${qnIndex}', questionNum);
        fragment = replaceAll(fragment, '${row}', rows);
        fragment = replaceAll(fragment, '${col}', newColNumber - 1);
        fragment = replaceAll(fragment, '${description}', '');
        fragment = replaceAll(fragment,
                              '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}',
                              'rubricDesc');
        
        // Insert after previous <td>
        var lastTd = $('#rubricRow' + idSuffix + '-' + rows + ' td:last');
        $(fragment).insertAfter(lastTd);
    }

    // Increment
    $('#rubricNumCols' + idSuffix).val(newColNumber);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeRubricRow(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $thisRow = $('#rubricRow' + idSuffix + '-' + index);
    
    // count number of table rows from table body
    var numberOfRows = $thisRow.parent().children('tr').length;
    
    var delStr = numberOfRows <= 1 ? 'clear' : 'delete';
    if (!confirm('Are you sure you want to ' + delStr + ' the row?')) {
        return;
    }
    
    if (numberOfRows <= 1) {
        $thisRow.find('textarea').val('');
    } else {
        $thisRow.remove();
    
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function removeRubricCol(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $thisCol = $('.rubricCol' + idSuffix + '-' + index);
    
    // count number of table columns from table body
    var numberOfCols = $thisCol.first().parent().children().length - 1;
    
    var delStr = numberOfCols <= 1 ? 'clear' : 'delete';
    if (!confirm('Are you sure you want to ' + delStr + ' the column?')) {
        return;
    }
    
    if (numberOfCols <= 1) {
        $thisCol.find('input[id^="rubricChoice"], textarea').val('');
        $thisCol.find('input[id^="rubricWeight"]').val(0);
    } else {
        $thisCol.remove();
    
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function highlightRubricRow(index, questionNum, highlight) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $rubricRow = $('#rubricRow' + idSuffix + '-' + index);

    if (highlight) {
        $rubricRow.find('td').addClass('cell-selected-negative');
    } else {
        $rubricRow.find('td').removeClass('cell-selected-negative');
    }

}

function highlightRubricCol(index, questionNum, highlight) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var $rubricCol = $('.rubricCol' + idSuffix + '-' + index);

    if (highlight) {
        $rubricCol.addClass('cell-selected-negative');
    } else {
        $rubricCol.removeClass('cell-selected-negative');
    }
}

/**
 * Attaches event handlers to "weights" checkboxes to toggle the visibility of
 * the input boxes for rubric weights and move the "weights" checkbox to the
 * appropriate location
 */
function bindAssignWeightsCheckboxes() {
    $('body').on('click', 'input[id^="rubricAssignWeights"]', function() {

        var $checkbox = $(this);

        $checkbox.closest('form').find('tr[id^="rubricWeights"]').toggle();

        moveAssignWeightsCheckbox($checkbox);
    });
}

/**
 * Moves the "weights" checkbox to the weight row if it is checked, otherwise
 * moves it to the choice row
 *
 * @param checkbox the "weights" checkbox
 */
function moveAssignWeightsCheckbox(checkbox) {

    var $choicesRow = checkbox.closest('thead').find('tr').eq(0);
    var $weightsRow = checkbox.closest('thead').find('tr').eq(1);
    var $choicesRowFirstCell = $choicesRow.find('th').first();
    var $weightsRowFirstCell = $weightsRow.find('th').first();

    var $checkboxCellContent = checkbox.closest('th').children().detach();

    $choicesRowFirstCell.empty();
    $weightsRowFirstCell.empty();

    if (checkbox.prop('checked')) {
        $choicesRowFirstCell.append('Choices <span class="glyphicon glyphicon-arrow-right"></span>');
        $weightsRowFirstCell.append($checkboxCellContent);
        $weightsRowFirstCell.find('.glyphicon-arrow-right').show();
    } else {
        $choicesRowFirstCell.append($checkboxCellContent);
        $choicesRowFirstCell.find('.glyphicon-arrow-right').hide();
    }
}

/**
 * @param questionNum
 *            the question number of the feedback question
 * @returns {Boolean} true if the weights are assigned by the user, otherwise false
 */
function hasAssignedWeights(questionNum) {
    return $('#rubricAssignWeights-' + questionNum).prop('checked');
}

/**
 * ----------------------------------------------------------------------------
 * Rank Question
 * ----------------------------------------------------------------------------
 */

function updateRankPointsValue(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    if ($('#' + FEEDBACK_QUESTION_RANKPOINTS + idSuffix).val() < 1) {
        $('#' + FEEDBACK_QUESTION_RANKPOINTS + idSuffix).val(1);
    }
}

function addRankOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    
    var curNumberOfChoiceCreated = parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $('<div id="rankOptionRow-' + curNumberOfChoiceCreated + idSuffix + '">'
          + '<div class="input-group">'
              + '<input type="text" name="' + FEEDBACK_QUESTION_RANKOPTION + '-' + curNumberOfChoiceCreated + '" '
                      + 'id="' + FEEDBACK_QUESTION_RANKOPTION + '-' + curNumberOfChoiceCreated + idSuffix + '" '
                      + 'class="form-control rankOptionTextBox">'
              + '<span class="input-group-btn">'
                  + '<button class="btn btn-default removeOptionLink" id="rankRemoveOptionLink" '
                          + 'onclick="removeRankOption(' + curNumberOfChoiceCreated + ',' + questionNum + ')" tabindex="-1">'
                      + '<span class="glyphicon glyphicon-remove"></span>'
                  + '</button>'
              + '</span>'
          + '</div>'
        + '</div>'
    ).insertBefore($('#rankAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideRankOptionTable(questionNum) {
    var idSuffix = getQuestionIdSuffix(questionNum);
    $('#' + FEEDBACK_QUESTION_RANKOPTIONTABLE + idSuffix).hide();
}

function removeRankOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var idSuffix = getQuestionIdSuffix(questionNum);
    var $thisRow = $('#rankOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of 'add option' button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 2) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
    
        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function showConfirmationModal(title, body, cancelButtonText, confirmButtonText) {
    $(CONFIRMATION_MODAL_TITLE).html(title);
    $(CONFIRMATION_MODAL_BODY).html(body);
    $(CONFIRMATION_MODAL_CANCEL).html(cancelButtonText);
    $(CONFIRMATION_MODAL_OK).html(confirmButtonText);
    $(CONFIRMATION_MODAL).modal('show');
}

function checkForConfirmation(event) {
    $(CONFIRMATION_MODAL_OK).on('click', function() {
        event.currentTarget.submit();
    });
}
