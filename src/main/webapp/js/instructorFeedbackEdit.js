var NEW_QUESTION = -1;

var WARNING_DISCARD_CHANGES = 'Warning: Any unsaved changes will be lost';
var CONFIRM_DISCARD_CHANGES = 'Are you sure you want to discard your unsaved edits?';
var CONFIRM_DISCARD_NEW_QNS = 'Are you sure you want to discard this question?';

var WARNING_DELETE_QNS = 'Warning: Deleted question cannot be recovered';
var CONFIRM_DELETE_QNS = 'Are you sure you want to delete this question?';

var WARNING_EDIT_DELETE_RESPONSES = 'Warning: Existing responses will be deleted by your action';
var CONFIRM_EDIT_DELETE_RESPONSES =
        '<p>Editing these fields will result in <strong>all existing responses for this question to be deleted.</strong></p>'
        + '<p>Are you sure you want to continue?</p>';

var questionsBeforeEdit = [];

$(document).ready(function() {
    readyFeedbackEditPage();
    bindUncommonSettingsEvents();
    updateUncommonSettingsInfo();
    hideUncommonPanels();
});


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
            var okCallback = function() {
                event.currentTarget.submit();
            };
            BootboxWrapper.showModalConfirmation(
                    WARNING_EDIT_DELETE_RESPONSES, CONFIRM_EDIT_DELETE_RESPONSES, okCallback, null,
                    BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
                    StatusType.DANGER);
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
    
    $('#add-new-question-dropdown > li').click(function() {
        showNewQuestionFrame($(this).data('questiontype'));
    });
    
    // Copy Binding
    bindCopyButton();
    bindCopyEvents();
    setupQuestionCopyModal();

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
        $('#constSumOption_Recipient-' + questionNum).show();
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
    $('#' + FEEDBACK_QUESTION_DISCARDCHANGES + '-' + questionNum).show();
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
    }

    var okCallback = function() {
        $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('delete');
        $('#form_editquestion-' + questionNum).submit();
    };
    BootboxWrapper.showModalConfirmation(
            WARNING_DELETE_QNS, CONFIRM_DELETE_QNS, okCallback, null,
            BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
            StatusType.DANGER);
    return false;
}

/**
 * Allows users to discard unsaved edits to the question
 */
function discardChanges(questionNum) {
    var confirmationMsg = questionNum === NEW_QUESTION
                          ? CONFIRM_DISCARD_NEW_QNS
                          : CONFIRM_DISCARD_CHANGES;
    var okCallback = function() {
        restoreOriginal(questionNum);
    };
    BootboxWrapper.showModalConfirmation(
            WARNING_DISCARD_CHANGES, confirmationMsg, okCallback, null,
            BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT,
            StatusType.WARNING);
}

/**
 * Discards new changes made and restores the original question
 * @param questionNum
 */
function restoreOriginal(questionNum) {
    if (questionNum === NEW_QUESTION) {
        hideNewQuestionAndShowNewQuestionForm();
    } else {
        $('#questionTable' + questionNum + ' > .panel-body').html(questionsBeforeEdit[questionNum]);

        $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).show();
        $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).hide();
        $('#' + FEEDBACK_QUESTION_DISCARDCHANGES + '-' + questionNum).hide();
        $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('');
        $('#button_question_submit-' + questionNum).hide();
    }

    // re-attach onChange event to show/hide numEntitiesBox according to recipient type
    $('#' + FEEDBACK_QUESTION_RECIPIENTTYPE + '-' + questionNum).change(updateVisibilityOfNumEntitiesBox);
}

function hideNewQuestionAndShowNewQuestionForm() {
    $('#questionTableNew').hide();
    $('#addNewQuestionTable').show();

    // re-enables all feedback path options, which may have been hidden by team contribution question
    $('#givertype').find('option').show().prop('disabled', false);
    $('#recipienttype').find('option').show().prop('disabled', false);
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
        $('#constSumOption_Recipient-' + NEW_QUESTION).show();
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

/**
 * Adds event handler to load 'copy question' modal contents by ajax.
 */
function setupQuestionCopyModal() {
    $('#copyModal').on('show.bs.modal', function(event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var actionlink = button.data('actionlink');
        var courseid = button.data('courseid');
        var fsname = button.data('fsname');
        
        var $questionCopyStatusMessage = $('#question-copy-modal-status');
        $.ajax({
            type: 'GET',
            url: actionlink + '&courseid=' + encodeURIComponent(courseid)
                            + '&fsname=' + encodeURIComponent(fsname),
            beforeSend: function() {
                $('#button_copy_submit').prop('disabled', true);
                $('#copyTableModal').remove();
                $questionCopyStatusMessage.removeClass('alert alert-danger');
                $questionCopyStatusMessage.html(
                        'Loading possible questions to copy. Please wait ...<br>'
                      + "<img class='margin-center-horizontal' src='/images/ajax-loader.gif'/>");
            },
            error: function() {
                $questionCopyStatusMessage.html(
                        'Error retrieving questions. Please close the dialog window and try again.');
                $questionCopyStatusMessage.addClass('alert alert-danger');
            },
            success: function(data) {
                var $questionRows = $(data).find('tbody > tr');
                if ($questionRows.length) {
                    $('#copyModalForm').prepend(data);
                    $questionCopyStatusMessage.html('');
                } else {
                    $questionCopyStatusMessage.addClass('alert alert-danger');
                    $questionCopyStatusMessage.prepend('<br>').html(FEEDBACK_QUESTION_COPY_INVALID);
                }
            }
        });
    });
}

function bindCopyButton() {

    $('#button_copy_submit').click(function(e) {
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

    $('body').on('click', '#copyTableModal > tbody > tr', function(e) {
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

function getQuestionIdSuffix(questionNum) {
    var isValidQuestionNumber = questionNum > 0 || questionNum === NEW_QUESTION;
    
    var idSuffix = isValidQuestionNumber ? '-' + questionNum : '';
    return idSuffix;
}

