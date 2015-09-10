
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
            if (!confirm('Editing these fields will result in all existing responses for' +
                         ' this question to be deleted. Are you sure you want to continue?')) {
                event.stopImmediatePropagation();
                return false;
            }
        }
    });
    $('form.form_question').submit(function() {
        return checkFeedbackQuestion(this);        
    });

    // Bind destructive changes
    $('form[id|=form_editquestion]').find(':input').not('.nonDestructive').change(function() {
        var editStatus = $(this).parents('form').attr('editStatus');
        if(editStatus === 'hasResponses') {
            $(this).parents('form').attr('editStatus', 'mustDeleteResponses');
        }
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
    
    // Bind feedback session edit form submission
    bindFeedbackSessionEditFormSubmission();
}

function bindFeedbackSessionEditFormSubmission() {
    $('#form_feedbacksession').submit(function( event ) {
        // Prevent form submission
        event.preventDefault();
        
        var $form = $(event.target);
        // Use Ajax to submit form data
        $.ajax({
            url: '/page/instructorFeedbackEditSave',
            type: 'POST',
            data: $form.serialize(),
            beforeSend: function() {
                $('#statusMessage').hide();
            },
            success: function(result) {
            	$statusMessage = $('#statusMessage');
            		
            	$statusMessage.text(result.statusForAjax);
                
            	$statusMessage.removeClass("alert alert-danger alert-warning");
                if (result.hasError) {
                	$statusMessage.addClass("alert alert-danger");
                } else {
                    disableEditFS();
                    $statusMessage.addClass("alert alert-success");
                }
                $statusMessage.show();
                
                // focus on status message
                $(document).scrollTop($statusMessage.offset().top - $('.navbar').height() - 30);
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

    //enable edit
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
 * @param number
 */
function enableEdit(qnNumber, maxQuestions) {
    var i = maxQuestions;
    while (i) {
        if (qnNumber === i) {
            enableQuestion(i);
        } else {
            disableQuestion(i);
        }
        i--;
    }
    
    return false;
}

/**
 * Enables question fields and "save changes" button for the given question number,
 * and hides the edit link.
 * @param number
 */
function enableQuestion(number) {
    var $currentQuestionTable = $('#questionTable' + number);
    
    $currentQuestionTable.find('text,button,textarea,select,input')
                         .not('[name="receiverFollowerCheckbox"]')
                         .not('.disabled_radio')
                         .removeAttr('disabled', 'disabled');
    
    $currentQuestionTable.find('.removeOptionLink').show();
    $currentQuestionTable.find('.addOptionLink').show();

    $currentQuestionTable.find('#rubricAddChoiceLink-' + number).show();
    $currentQuestionTable.find('#rubricAddSubQuestionLink-' + number).show();
    $currentQuestionTable.find('.rubricRemoveChoiceLink-' + number).show();
    $currentQuestionTable.find('.rubricRemoveSubQuestionLink-' + number).show();
    
    if ($('#generateOptionsCheckbox-' + number).prop('checked')) {
        $('#mcqChoiceTable-' + number).hide();
        $('#msqChoiceTable-' + number).hide();
        $("#mcqOtherOptionFlag-" + number).parent().hide();
        $("#msqOtherOptionFlag-" + number).parent().hide();
        $('#mcqGenerateForSelect-' + number).prop('disabled', false);
        $('#msqGenerateForSelect-' + number).prop('disabled', false);
    } else {
        $('#mcqChoiceTable-' + number).show();
        $('#msqChoiceTable-' + number).show();
        $("#mcqOtherOptionFlag-" + number).parent().show();
        $("#msqOtherOptionFlag-" + number).parent().show();
        $('#mcqGenerateForSelect-' + number).prop('disabled', true);
        $('#msqGenerateForSelect-' + number).prop('disabled', true);
    }
    
    if ($('#constSumToRecipients-' + number).val() === 'true') {
        $('#constSumOptionTable-' + number).hide();
        $('#constSumOption_Option-' + number).hide();
    } else {
        $('#constSumOptionTable-' + number).show();
        $('#constSumOption_Recipient-' + number).hide();
    }
    
    $('#constSumOption_distributeUnevenly-' + number).prop('disabled', false);
    
    if ($('#questionTable' + number).parent().find('input[name="questiontype"]').val() === 'CONTRIB') {
        fixContribQnGiverRecipient(number);
        setContribQnVisibilityFormat(number);
    }
    
    $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + number).hide();
    $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + number).show();
    $('#' + FEEDBACK_QUESTION_CANCELEDIT+'-' + number).show();
    $('#' + 'button_question_submit-' + number).show();
    $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + number).val('edit');
    // $('#questionTable'+number).find('.visibilityOptionsLabel').click();
}

function enableNewQuestion() {
    var newQnSuffix = 'New';
    var number = '-1';
    
    var $currentQuestionTableSuffix = $('#questionTable' + newQnSuffix);
    var $currentQuestionTableNumber = $('#questionTable' + number);
    
    $currentQuestionTableSuffix.find('text,button,textarea,select,input')
                               .not('[name="receiverFollowerCheckbox"]')
                               .not('.disabled_radio')
                               .removeAttr('disabled', 'disabled');
    $currentQuestionTableSuffix.find('.removeOptionLink').show();
    $currentQuestionTableSuffix.find('.addOptionLink').show();

    $currentQuestionTableNumber.find('#rubricAddChoiceLink-' + number).show();
    $currentQuestionTableNumber.find('#rubricAddSubQuestionLink-' + number).show();
    $currentQuestionTableNumber.find('.rubricRemoveChoiceLink-' + number).show();
    $currentQuestionTableNumber.find('.rubricRemoveSubQuestionLink-' + number).show();


    if ($('#generateOptionsCheckbox-' + number).prop('checked')) {
        $('#mcqChoiceTable-' + number).hide();
        $('#msqChoiceTable-' + number).hide();
        $('#mcqGenerateForSelect-' + number).prop('disabled', false);
        $('#msqGenerateForSelect-' + number).prop('disabled', false);
    } else {
        $('#mcqChoiceTable-' + number).show();
        $('#msqChoiceTable-' + number).show();
        $('#mcqGenerateForSelect-' + number).prop('disabled', true);
        $('#msqGenerateForSelect-' + number).prop('disabled', true);
    }       
    
    $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + number).hide();
    $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + number).show();
    $('#' + 'button_question_submit-' + number).show();
    $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + number).val('edit');
    // $('#questionTable'+number).find('.visibilityOptionsLabel').click();
}

/**
 * Disable question fields and "save changes" button for the given question number,
 * and shows the edit link.
 * @param number
 */
function disableQuestion(number) {
    var $currentQuestionTable = $('#questionTable' + number);

    $currentQuestionTable.find('text,button,textarea,select,input').attr('disabled', 'disabled');
    
    $currentQuestionTable.find('#mcqAddOptionLink').hide();
    $currentQuestionTable.find('#msqAddOptionLink').hide();
    $currentQuestionTable.find('.removeOptionLink').hide();
    
    /* Check whether generate options for students/instructors/teams is selected
       If so, hide 'add Other option' */
    if ($currentQuestionTable.find("#generateOptionsCheckbox-" + number).attr("checked")) {
        $currentQuestionTable.find("#mcqOtherOptionFlag-" + number).parent().hide();
        $currentQuestionTable.find("#msqOtherOptionFlag-" + number).parent().hide();
    } else {
        $currentQuestionTable.find("#mcqOtherOptionFlag-" + number).parent().show();
        $currentQuestionTable.find("#msqOtherOptionFlag-" + number).parent().show();
    }

    $currentQuestionTable.find('#rubricAddChoiceLink-' + number).hide();
    $currentQuestionTable.find('#rubricAddSubQuestionLink-' + number).hide();
    $currentQuestionTable.find('.rubricRemoveChoiceLink-' + number).hide();
    $currentQuestionTable.find('.rubricRemoveSubQuestionLink-' + number).hide();

    $('#' + FEEDBACK_QUESTION_EDITTEXT + '-' + number).show();
    $('#' + FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + number).hide();
    $('#' + 'button_question_submit-' + number).hide();
}

/**
 * Pops up confirmation dialog whether to delete specified question
 * @param question number
 * @returns
 */
function deleteQuestion(number) {
    if (number === -1) {
        location.reload();
        return false;
    } else if (confirm('Are you sure you want to delete this question?')) {
        $('#' + FEEDBACK_QUESTION_EDITTYPE + '-' + number).val('delete'); 
        $('#form_editquestion-' + number).submit();
        return true;
    } else {
        return false;
    }
}

/**
 * Allows users to cancel editing questions
 */
function cancelEdit(number) {
    var confirmationMsg = number === -1 ?
            'Are you sure you want to cancel adding this question?' :
            'Are you sure you want to cancel your changes?';
    if (confirm(confirmationMsg)) {
        location.reload();
    }
}

/**
 * Formats all questions to hide the 'Number of Recipients Box' 
 * when participant type is not STUDENTS OR TEAMS, and show
 * it when it is. Formats the label for the number box to fit
 * the selection as well.
 */
function formatNumberBoxes() {
    disallowNonNumericEntries($('input.numberOfEntitiesBox'), false, false);
    disallowNonNumericEntries($('input.minScaleBox'), false, true);
    disallowNonNumericEntries($('input.maxScaleBox'), false, true);
    disallowNonNumericEntries($('input.stepBox'), true, false);
    disallowNonNumericEntries($('input.pointsBox'), false, false);
    
    // Binds onChange of recipientType to modify numEntityBox visibility
    var modifyVisibility = function() {
        var qnNumber = $(this).prop('id').split('-')[1];
        qnNumber = qnNumber || '';
        
        var value = $(this).val();
        
        formatNumberBox(value, qnNumber);
    }
    $('select[name=' + FEEDBACK_QUESTION_RECIPIENTTYPE + ']').each(modifyVisibility)
                                                             .change(modifyVisibility);
    
}

/**
 * Hides/shows the "Number of Recipients Box" of the question 
 * depending on the participant type and formats the label text for it.
 * @param value, qnNumber
 */
function formatNumberBox(value, qnNumber) {
    if (value === 'STUDENTS' || value === 'TEAMS') {
        $('div.numberOfEntitiesElements' + qnNumber).show();
        
        var $span = $('span#' + FEEDBACK_QUESTION_NUMBEROFENTITIES + '_text_inner-' + qnNumber);
        $span.html(value === 'STUDENTS' ? 'students' : 'teams');
    } else {
        $('div.numberOfEntitiesElements' + qnNumber).hide();
    }
    
    tallyCheckboxes(qnNumber);
}

/**
 * Pushes the values of all checked check boxes for the specified question
 * into the appropriate feedback question parameters.
 * @returns qnNumber
 */
function tallyCheckboxes(qnNumber) {
    // update hidden parameters (the values in checkboxTypes)
    var checkboxTypes = {
        '.answerCheckbox': FEEDBACK_QUESTION_SHOWRESPONSESTO,
        '.giverCheckbox': FEEDBACK_QUESTION_SHOWGIVERTO,
        '.recipientCheckbox': FEEDBACK_QUESTION_SHOWRECIPIENTTO
    };
    
    for (var checkboxType in checkboxTypes) {
        var checked = [];
        $(checkboxType + qnNumber + ':checked').each(function() {
            checked.push($(this).val());
        });
        $('[name=' + checkboxTypes[checkboxType] + ']').val(checked.toString());
    }
}

/**
 * Shows the new question div frame and scrolls to it
 */
function showNewQuestionFrame(type) {
    copyOptions();
    prepareQuestionForm(type);
    $('#questionTableNew').show();
    enableNewQuestion();
    
    $('#addNewQuestionTable').hide();
    $('#empty_message').hide();
    var headerOffset = $('div.navbar-fixed-top').height();
    $('html, body').animate({
        scrollTop: $('#questionTableNew').offset().top - headerOffset
    }, 1000);
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
}

function prepareQuestionForm(type) {
    switch(type) {
        case 'TEXT':
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_TEXT);
            
            hideAllNewQuestionForms();
            break;
        case 'MCQ':
            $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '--1').val(2);
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_MCQ);
            
            hideAllNewQuestionForms();
            
            $('#mcqForm').show();
            break;
        case 'MSQ':
            $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '--1').val(2);
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_MSQ);
            
            hideAllNewQuestionForms();
            
            $('#msqForm').show();
            break;
        case 'NUMSCALE':
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_NUMSCALE);
            
            hideAllNewQuestionForms();
            
            $('#numScaleForm').show();
            $('#' + FEEDBACK_QUESTION_TEXT).attr('placeholder', 'e.g. Rate the class from 1 (very bad) to 5 (excellent)');
            break;
        case 'CONSTSUM_OPTION':
            $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '--1').val(2);
            $('#' + FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS + '--1').val('false');
            $('#constSumOption_Recipient' + '--1').hide();
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION);
            
            hideAllNewQuestionForms();
            
            $('#constSumForm').show();
            break;
        case 'CONSTSUM_RECIPIENT':
            $('#' + FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS + '--1').val('true');
            $('#constSumOption_Option' + '--1').hide();
            hideConstSumOptionTable(-1);
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT);
            
            hideAllNewQuestionForms();
            
            $('#constSumForm').show();
            var optionText = $('#constSum_labelText-' + '-1').text();
            $('#constSum_labelText-' + '-1').text(optionText.replace('option', 'recipient'));
            var tooltipText = $('#constSum_tooltipText-' + '-1').attr('data-original-title');
            $('#constSum_tooltipText-' + '-1').attr('data-original-title', tooltipText.replace('option', 'recipient'));
            break;
        case 'CONTRIB':
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_CONTRIB);
            
            hideAllNewQuestionForms();
            
            $('#contribForm').show();
            fixContribQnGiverRecipient();
            setDefaultContribQnVisibility();
            setContribQnVisibilityFormat();
            break;
        case 'RUBRIC':
            $('#questionTypeHeader').append(FEEDBACK_QUESTION_TYPENAME_RUBRIC);
            
            hideAllNewQuestionForms();
            
            $('#rubricForm').show();
            break;
    }
}


/**
 * Binds each question's check box field such that the user
 * cannot select an invalid combination.
 */
function formatCheckBoxes() {
    $(document).ready(function() {
        // TODO: change class -> name?
        $('input[class*="answerCheckbox"]').change(function() {
            if (!$(this).is(':checked')) {
                $(this).parent().parent().find('input[class*="giverCheckbox"]')
                                         .prop('checked', false);
                $(this).parent().parent().find('input[class*="recipientCheckbox"]')
                                         .prop('checked', false);
            }
        });
        $('input[class*="giverCheckbox"]').change(function() {
            if ($(this).is(':checked')) {
                $(this).parent().parent().find('input[class*="answerCheckbox"]')
                                         .prop('checked', true)
                                         .trigger('change');
            }
        });
        $('input[class*="recipientCheckbox"]').change(function() {
            if ($(this).is(':checked')) {
                $(this).parent().parent().find('input[class*="answerCheckbox"]')
                                         .prop('checked', true);
            }
        });
        $('input[name=receiverLeaderCheckbox]').change(function () {
            $(this).parent().parent().find('input[name=receiverFollowerCheckbox]')
                                     .prop('checked', $(this).prop('checked'));
        });
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
    
    $currRadioButtons.each(function (index) {
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
    elem = $(elem);
    if (elem.val() === 'OWN_TEAM' || elem.val() === 'TEAMS' ||
            elem.val() === 'INSTRUCTORS' || elem.val() === 'OWN_TEAM_MEMBERS') {
        enableRow(elem, 1);
        disableRow(elem, 3);
        return;
    } else if(elem.val() === 'NONE') {
        disableRow(elem, 3);
        disableRow(elem, 1);
        return;
    }
    
    enableRow(elem, 1);
    enableRow(elem, 3);
}

function feedbackGiverUpdateVisibilityOptions(elem) {
    elem = $(elem);
    if (elem.val() === 'INSTRUCTORS' || elem.val() === 'TEAMS') {
        disableRow(elem, 2);
        return;
    }
    enableRow(elem, 2);
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

function getQuestionLink(qnNumber) {
    var courseid = $('input[name="courseid"]').val();
    var fsname = encodeURIComponent($('input[name="fsname"]').val());
    
    var questionId = $('#form_editquestion-' + qnNumber).find('input[name="questionid"]').val();
    
    var giverType = $('#givertype-' + qnNumber).val();
    
    var actionUrl = giverType === 'STUDENTS' || giverType === 'TEAMS' ?
                    '/page/studentFeedbackQuestionSubmissionEditPage' :
                    '/page/instructorFeedbackQuestionSubmissionEditPage';
    
    var questionLink =  window.location.protocol + '//' +
                        window.location.host + actionUrl +
                        '?courseid=' + courseid +
                        '&fsname=' + fsname +
                        '&questionid=' + questionId;
    
    setStatusMessage('Link for question ' + qnNumber + ': ' + questionLink, false);
}

function toParameterFormat(str) {
    return str.replace(/\s/g,'+');
}

function bindCopyButton() {
    $('#button_copy').on('click', function(e) {
        e.preventDefault();
        
        var questionRows = $('#copyTableModal >tbody>tr');
        if (!questionRows.length) {
            setStatusMessage(FEEDBACK_QUESTION_COPY_INVALID, true);
        } else {
            setStatusMessage('', false);
            $('#copyModal').modal('show');
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

        if (!hasRowSelected) {
            setStatusMessage('No questions are selected to be copied', true);
            $('#copyModal').modal('hide');
        } else {
            $('#copyModalForm').submit();
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
            $(this).children('td:first').html('<input type="checkbox" checked="checked">');
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
    var $visibilityMessage = $elementParent.find('.visibilityMessage');

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
    var qnNumber = $form.find('[name=questionnum]').val();
    var newQnNumber = $('input[name=questionnum]').last().val();
    
    if (qnNumber === newQnNumber) {
        tallyCheckboxes('');
    } else {
        tallyCheckboxes(qnNumber);
    }
    
    var formData = $form.serialize();
    
    var $formOptions = $form.find('.visibilityOptions');
    var $formVisibility = $form.find('.visibilityMessage') 
    
    if (previousFormDataMap[qnNumber] === formData) {
        $formOptions.hide();
        $formVisibility.show();
        return;
    }
    // update stored form data
    previousFormDataMap[qnNumber] = formData;

    // empty current visibility message in the form
    $formVisibility.html('');
    
    var url = '/page/instructorFeedbackQuestionvisibilityMessage';
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        success: function(data) {
            $formVisibility.html(formatVisibilityMessageHtml(data.visibilityMessage));
            $formVisibility.show();
            $formOptions.hide();
        },
        error: function(jqXHR, textStatus, errorThrown) {

        }
    });    
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

function getQuestionIdSuffix(questionNumber) {
    var newQuestionNumber = -1;
    var isValidQuestionNumber = questionNumber > 0 ||
                                questionNumber === newQuestionNumber;
    
    var idSuffix = isValidQuestionNumber ? ('-' + questionNumber) : '';
    return idSuffix;
}

/**
 * ----------------------------------------------------------------------------
 * Mcq Question
 * ----------------------------------------------------------------------------
 */

function addMcqOption(questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var curNumberOfChoiceCreated =
            parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
    
    $(    "<div id=\"mcqOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
        +   "<div class=\"input-group\">"
        +       "<span class=\"input-group-addon\">"
        +          "<input type=\"radio\" disabled=\"disabled\">"
        +       "</span>"
        +       "<input type=\"text\" name=\""+FEEDBACK_QUESTION_MCQCHOICE+"-"+curNumberOfChoiceCreated+"\" "
        +               "id=\""+FEEDBACK_QUESTION_MCQCHOICE+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"form-control mcqOptionTextBox\">"
        +       "<span class=\"input-group-btn\">"
        +           "<button type=\"button\" class=\"btn btn-default removeOptionLink\" id=\"mcqRemoveOptionLink\" "
        +                   "onclick=\"removeMcqOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
        +               "<span class=\"glyphicon glyphicon-remove\"></span>"
        +           "</button>"
        +       "</span>"
        +   "</div>"
        + "</div>"
    ).insertBefore($('#mcqAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
        $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var $thisRow = $('#mcqOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
        
        if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
            $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function toggleMcqGeneratedOptions(checkbox, questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);

    if (checkbox.checked) {
        $('#mcqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', true)
        $('#mcqChoiceTable' + idSuffix).hide();
        $('#mcqGenerateForSelect' + idSuffix).prop('disabled', false);
        $("#mcqOtherOptionFlag" + idSuffix).parent().hide();
        $('#generatedOptions' + idSuffix).attr('value',
                                               $('#mcqGenerateForSelect' + idSuffix).prop('value'));
    } else {
        $('#mcqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', false);
        $('#mcqChoiceTable' + idSuffix).show();
        $('#mcqGenerateForSelect' + idSuffix).prop('disabled', true);
        $("#mcqOtherOptionFlag" + idSuffix).parent().show();
        $('#generatedOptions' + idSuffix).attr('value', 'NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNumber) {
	idOfQuestion = '#form_editquestion-' + questionNumber;
	idSuffix = getQuestionIdSuffix(questionNumber);

	if($(idOfQuestion).attr('editStatus') === "hasResponses") {
		$(idOfQuestion).attr('editStatus', "mustDeleteResponses");
	}
}

function changeMcqGenerateFor(questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);

    $('#generatedOptions' + idSuffix).attr('value',
                                           $('#mcqGenerateForSelect' + idSuffix).prop('value'));
}

/**
 * ----------------------------------------------------------------------------
 * Msq Question
 * ----------------------------------------------------------------------------
 */

function addMsqOption(questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);

    var curNumberOfChoiceCreated =
            parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $(   "<div id=\"msqOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
        +   "<div class=\"input-group\">"
        +       "<span class=\"input-group-addon\">"
        +          "<input type=\"checkbox\" disabled=\"disabled\">"
        +       "</span>"
        +       "<input type=\"text\" name=\""+FEEDBACK_QUESTION_MSQCHOICE+"-"+curNumberOfChoiceCreated+"\" "
        +               "id=\""+FEEDBACK_QUESTION_MSQCHOICE+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"form-control msqOptionTextBox\">"
        +       "<span class=\"input-group-btn\">"
        +           "<button type=\"button\" class=\"btn btn-default removeOptionLink\" id=\"msqRemoveOptionLink\" "
        +                   "onclick=\"removeMsqOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
        +               "<span class=\"glyphicon glyphicon-remove\"></span>"
        +           "</button>"
        +       "</span>"
        +   "</div>"
        + '</div>'
    ).insertBefore($('#msqAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
        $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
    }
}


function removeMsqOption(index, questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var $thisRow = $('#msqOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
    
        if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
            $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function toggleMsqGeneratedOptions(checkbox, questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);

    if (checkbox.checked) {
        $('#msqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', true)
        $('#msqChoiceTable' + idSuffix).hide();
        $('#msqGenerateForSelect' + idSuffix).prop('disabled', false);
        $("#msqOtherOptionFlag" + idSuffix).parent().hide();
        $('#generatedOptions' + idSuffix).attr('value',
                                               $('#msqGenerateForSelect' + idSuffix).prop('value'));
    } else {
        $('#msqChoiceTable' + idSuffix).find('input[type=text]').prop('disabled', false);
        $('#msqChoiceTable' + idSuffix).show();
        $('#msqGenerateForSelect' + idSuffix).prop('disabled', true);
        $("#msqOtherOptionFlag" + idSuffix).parent().show();
        $('#generatedOptions' + idSuffix).attr('value', 'NONE');
    }
}

function changeMsqGenerateFor(questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);

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

function updateNumScalePossibleValues(questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
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
    if (roundToThreeDp(largestValueInRange) != max) {
        $numScalePossibleValues.css('color', 'red');
        var possibleValuesString = '[The interval ' + min.toString() + ' - ' + max.toString() +
                                   ' is not divisible by the specified increment.]';

        if (isNaN(min) || isNaN(max) || isNaN(step)) {
            possibleValuesString = '[Please enter valid numbers for all the options.]'
        }

        $numScalePossibleValues.text(possibleValuesString);
        return false;
    } else {
        $numScalePossibleValues.css('color', 'black');
        var possibleValuesString = '[Based on the above settings, acceptable responses are: ';
        
        // step is 3 d.p. at most, so round it after * 1000. 
        if (possibleValuesCount > 6) {
            possibleValuesString += min.toString() + ', ' +
                                    (Math.round((min +     step) * 1000) / 1000).toString() + ', ' +
                                    (Math.round((min + 2 * step) * 1000) / 1000).toString() + ', ..., ' +
                                    (Math.round((max - 2 * step) * 1000) / 1000).toString() + ', ' +
                                    (Math.round((max -     step) * 1000) / 1000).toString() + ', ' +
                                    max.toString();       
        } else {
            possibleValuesString += min.toString();
            var cur = min + step;
            while ((max - cur) >= -1e-9) {
                possibleValuesString += ', ' + (Math.round(cur * 1000) / 1000).toString();
                cur += step;
            }
        }
        
        possibleValuesString += ']';
        $numScalePossibleValues.text(possibleValuesString);
        return true;
    }
}

/**
 * ----------------------------------------------------------------------------
 * Constant Sum Question
 * ----------------------------------------------------------------------------
 */

function updateConstSumPointsValue(questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    if ($('#' + FEEDBACK_QUESTION_CONSTSUMPOINTS + idSuffix).val() < 1) {
        $('#' + FEEDBACK_QUESTION_CONSTSUMPOINTS + idSuffix).val(1);
    }
}

function addConstSumOption(questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var curNumberOfChoiceCreated = parseInt($('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val());
        
    $(    "<div id=\"constSumOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
        +   "<div class=\"input-group\">"
        +       "<input type=\"text\" name=\""+FEEDBACK_QUESTION_CONSTSUMOPTION+"-"+curNumberOfChoiceCreated+"\" "
        +               "id=\""+FEEDBACK_QUESTION_CONSTSUMOPTION+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"form-control constSumOptionTextBox\">"
        +       "<span class=\"input-group-btn\">"
        +           "<button class=\"btn btn-default removeOptionLink\" id=\"constSumRemoveOptionLink\" "
        +                   "onclick=\"removeConstSumOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
        +               "<span class=\"glyphicon glyphicon-remove\"></span>"
        +           "</button>"
        +       "</span>"
        +   "</div>"
        + '</div>'
    ).insertBefore($('#constSumAddOptionRow' + idSuffix));

    $('#' + FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + idSuffix).val(curNumberOfChoiceCreated + 1);
    
    if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
        $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideConstSumOptionTable(questionNumber) {
    var idSuffix = getQuestionIdSuffix(questionNumber);
    $('#' + FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE + idSuffix).hide();
}

function removeConstSumOption(index, questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    var $thisRow = $('#constSumOptionRow-' + index + idSuffix);
    
    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;
    
    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();
    
        if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
            $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
        }
    }
}


/**
 * ----------------------------------------------------------------------------
 * Contribution Question
 * ----------------------------------------------------------------------------
 */

function setDefaultContribQnVisibility(questionNumber) {
    var idSuffix = questionNumber ? questionNumber : 'New';
    var idSuffix2 = questionNumber ? questionNumber : '';
    
    $currentQuestionTable = $('#questionTable' + idSuffix);

    $currentQuestionTable.find('input.visibilityCheckbox').prop('checked', false);
    //All except STUDENTS can see answer
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="answerCheckbox' + idSuffix2 + '"]')
                         .not('[value="STUDENTS"]').prop('checked', true);
    //Only instructor can see giver
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="giverCheckbox' + idSuffix2 + '"]')
                         .filter('[value="INSTRUCTORS"]').prop('checked', true);
    //Recipient and instructor can see recipient
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="recipientCheckbox' + idSuffix2 + '"]')
                         .filter('[value="INSTRUCTORS"],[value="RECEIVER"]').prop('checked', true);

}

function setContribQnVisibilityFormat(questionNumber) {
    var idSuffix = questionNumber ? questionNumber : 'New';
    var idSuffix2 = questionNumber ? questionNumber : '';

    $currentQuestionTable = $('#questionTable' + idSuffix);

    //Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $currentQuestionTable.find('input.visibilityCheckbox').off('change');
    
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="answerCheckbox"]')
                         .change(function() {
        if (!$(this).prop('checked')) {
            if ($(this).val() === 'RECEIVER' || $(this).val() === 'OWN_TEAM_MEMBERS' ||
                                                $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
                $currentQuestionTable.find('input.visibilityCheckbox')
                                     .filter('input[class*="giverCheckbox"],input[class*="recipientCheckbox"]')
                                     .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                     .prop('checked', false);
            } else {
                $(this).parent().parent().find('input[class*="giverCheckbox"]')
                                         .prop('checked',false);
                $(this).parent().parent().find('input[class*="recipientCheckbox"]')
                                         .prop('checked',false);
            }
            
        }
        
        if ($(this).val() === 'RECEIVER' || $(this).val() === 'OWN_TEAM_MEMBERS' ||
                                            $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox')
                                 .filter('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
        }
        
        if ($(this).val() === 'RECEIVER' || $(this).val() === 'OWN_TEAM_MEMBERS' ||
                                            $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox')
                                 .filter('[class*="answerCheckbox"]')
                                 .filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]')
                                 .prop('checked', $(this).prop('checked'));
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="giverCheckbox"]')
                         .change(function() {
        if ($(this).is(':checked')) {
            $(this).parent().parent().find('input[class*="answerCheckbox"]')
                                     .prop('checked',true)
                                     .trigger('change');
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[class*="recipientCheckbox"]')
                         .change(function() {
        if ($(this).is(':checked')) {
            $(this).parent().parent().find('input[class*="answerCheckbox"]')
                                     .prop('checked',true)
                                     .trigger('change');
        }
    });
    
    $currentQuestionTable.find('input.visibilityCheckbox')
                         .filter('[name=receiverLeaderCheckbox]')
                         .change(function () {
        $(this).parent().parent().find('input[name=receiverFollowerCheckbox]')
                                 .prop('checked', $(this).prop('checked'));
    });

}

function fixContribQnGiverRecipient(questionNumber) {
    var idSuffix = questionNumber ? ('-' + questionNumber) : '';
    var $giverType = $('#givertype' + idSuffix);
    var $recipientType = $('#recipienttype' + idSuffix);

    //Fix giver->recipient to be STUDENT->OWN_TEAM_MEMBERS_INCLUDING_SELF
    $giverType.find('option').not('[value="STUDENTS"]').hide();
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').hide();

    $giverType.find('option').not('[value="STUDENTS"]').prop('disabled', true);
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('disabled', true);

    $giverType.find('option').filter('[value="STUDENTS"]').attr('selected','selected');
    $recipientType.find('option').filter('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').attr('selected','selected');
}

/**
 * ----------------------------------------------------------------------------
 * Rubric Question
 * ----------------------------------------------------------------------------
 */


function addRubricRow(questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var numberOfRows = parseInt($('#' + 'rubricNumRows' + idSuffix).val());
    var numberOfCols = parseInt($('#' + 'rubricNumCols' + idSuffix).val());

    var newRowNumber = numberOfRows + 1;

    var rubricRowTemplate =
        "<tr id=\"rubricRow-${qnIndex}-${row}\">"
      +     "<td>"
      +         "<div class=\"col-sm-12 input-group\">"
      +             "<span class=\"input-group-addon btn btn-default rubricRemoveSubQuestionLink-${qnIndex}\" id=\"rubricRemoveSubQuestionLink-${qnIndex}-${row}\" onclick=\"removeRubricRow(${row},${qnIndex})\""
      +                     "onmouseover=\"highlightRubricRow(${row}, ${qnIndex}, true)\" onmouseout=\"highlightRubricRow(${row}, ${qnIndex}, false)\">"
      +                 "<span class=\"glyphicon glyphicon-remove\"></span>"
      +             "</span>"
      +             "<textarea class=\"form-control\" rows=\"3\" id=\"${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}-${qnIndex}-${row}\" name=\"${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}-${row}\">${subQuestion}</textarea>"
      +         "</div>"
      +     "</td>"
      +     "${rubricRowBodyFragments}"
      + "</tr>";

    var rubricRowFragmentTemplate =
        "<td class=\"align-center rubricCol-${qnIndex}-${col}\">"
      +   "<textarea class=\"form-control\" rows=\"3\" id=\"${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${qnIndex}-${row}-${col}\" name=\"${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${row}-${col}\">${description}</textarea>"
      + "</td>";

    var rubricRowBodyFragments = '';
    // Create numberOfCols of <td>'s 
    for (var cols = 0; cols < numberOfCols; cols++) {
        if (!$('.rubricCol' + idSuffix + '-' + cols).length) {
            continue;
        }
        var fragment = rubricRowFragmentTemplate;
        fragment = replaceAll(fragment, '${qnIndex}', questionNumber);
        fragment = replaceAll(fragment, '${row}', newRowNumber - 1);
        fragment = replaceAll(fragment, '${col}', cols);
        fragment = replaceAll(fragment, '${description}', '');
        fragment = replaceAll(fragment, '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}', 'rubricDesc');
        rubricRowBodyFragments += fragment;
    }

    
    // Create new rubric row
    var newRubricRow = rubricRowTemplate;
    newRubricRow = replaceAll(newRubricRow, '${qnIndex}', questionNumber);
    newRubricRow = replaceAll(newRubricRow, '${row}', newRowNumber - 1);
    newRubricRow = replaceAll(newRubricRow, '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICSUBQUESTION}', 'rubricSubQn');
    newRubricRow = replaceAll(newRubricRow, '${subQuestion}', '');
    newRubricRow = replaceAll(newRubricRow, '${rubricRowBodyFragments}', rubricRowBodyFragments);

    // Row to insert new row after
    var lastRow = $('#rubricEditTable' + idSuffix + ' tr:last');
    $(newRubricRow).insertAfter(lastRow);

    // Increment
    $('#' + 'rubricNumRows' + idSuffix).val(newRowNumber);
    
    if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
        $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
    }
}

function addRubricCol(questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var numberOfRows = parseInt($('#' + 'rubricNumRows' + idSuffix).val());
    var numberOfCols = parseInt($('#' + 'rubricNumCols' + idSuffix).val());
    
    var newColNumber = numberOfCols + 1;

    //Insert header <th>
    var rubricHeaderFragmentTemplate = 
       "<th class=\"rubricCol-${qnIndex}-${col}\">"
      +     "<div class=\"input-group\">"
      +         "<input type=\"text\" class=\"col-sm-12 form-control\" value=\"${rubricChoiceValue}\" id=\"${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}-${qnIndex}-${col}\" name=\"${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}-${col}\">"
      +         "<span class=\"input-group-addon btn btn-default rubricRemoveChoiceLink-${qnIndex}\" id=\"rubricRemoveChoiceLink-${qnIndex}-${col}\" onclick=\"removeRubricCol(${col}, ${qnIndex})\" "
      +                 "onmouseover=\"highlightRubricCol(${col}, ${qnIndex}, true)\" onmouseout=\"highlightRubricCol(${col}, ${qnIndex}, false)\">"
      +             "<span class=\"glyphicon glyphicon-remove\"></span>"
      +         "</span>"
      +     "</div>"
      + "</th>";

    var rubricHeaderFragment = rubricHeaderFragmentTemplate;
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${qnIndex}', questionNumber);
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${col}', newColNumber - 1);
    rubricHeaderFragment = replaceAll(rubricHeaderFragment, '${rubricChoiceValue}', '');
    rubricHeaderFragment = replaceAll(rubricHeaderFragment,
                                      '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICCHOICE}',
                                      'rubricChoice');

    // Insert after last <th>
    var lastTh = $('#rubricEditTable' + idSuffix + ' th:last');
    $(rubricHeaderFragment).insertAfter(lastTh);

    // Insert body <td>'s
    var rubricRowFragmentTemplate =
        '<td class=\'align-center rubricCol-${qnIndex}-${col}\'>'
      +   '<textarea class=\'form-control\' rows=\'3\' id=\'${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${qnIndex}-${row}-${col}\' name=\'${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}-${row}-${col}\'>${description}</textarea>'
      + '</td>';

    var rubricRowBodyFragments = '';
    // Create numberOfRows of <td>'s
    for (var rows = 0; rows < numberOfRows; rows++) {
        if (!$('#rubricRow' + idSuffix + '-' + rows).length) {
            continue;
        }
        var fragment = rubricRowFragmentTemplate;
        fragment = replaceAll(fragment, '${qnIndex}', questionNumber);
        fragment = replaceAll(fragment, '${row}', rows);
        fragment = replaceAll(fragment, '${col}', newColNumber-1);
        fragment = replaceAll(fragment, '${description}', '');
        fragment = replaceAll(fragment,
                              '${Const.ParamsNames.FEEDBACK_QUESTION_RUBRICDESCRIPTION}',
                              'rubricDesc');
        
        // Insert after previous <td>
        var lastTd = $('#rubricRow' + idSuffix + '-' + rows + ' td:last');
        $(fragment).insertAfter(lastTd);
    }

    // Increment
    $('#' + 'rubricNumCols' + idSuffix).val(newColNumber);
    
    if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
        $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeRubricRow(index, questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var $thisRow = $('#rubricRow' + idSuffix + '-' + index);
    
    // count number of table rows from table body
    var numberOfRows = $thisRow.parent().children('tr').length;
    
    var delStr = numberOfRows <= 1 ? 'clear' : 'delete';
    if (!confirm('Are you sure you want to ' + delStr + ' the row?')) {
        return
    }
    
    if (numberOfRows <= 1) {
        $thisRow.find('textarea').val('');
    } else {
        $thisRow.remove();
    
        if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
            $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function removeRubricCol(index, questionNumber) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var $thisCol = $('.rubricCol' + idSuffix + '-' + index);
    
    // count number of table rows from table body
    var numberOfCols = $thisCol.not('align-center').parent().children('th').length - 1;
    
    var delStr = numberOfCols <= 1 ? 'clear' : 'delete';
    if (!confirm('Are you sure you want to ' + delStr + ' the column?')) {
        return
    }
    
    if (numberOfCols <= 1) {
        $thisCol.find('input, textarea').val('');
    } else {
        $thisCol.remove();
    
        if ($(idOfQuestion).attr('editStatus') === 'hasResponses') {
            $(idOfQuestion).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function highlightRubricRow(index, questionNumber, highlight) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var $rubricRow = $('#rubricRow' + idSuffix + '-' + index);

    if (highlight) {
        $rubricRow.find('td').addClass('cell-selected-negative');
    } else {
        $rubricRow.find('td').removeClass('cell-selected-negative');
    }

}

function highlightRubricCol(index, questionNumber, highlight) {
    var idOfQuestion = '#form_editquestion-' + questionNumber;
    var idSuffix = getQuestionIdSuffix(questionNumber);
    
    var $rubricCol = $('.rubricCol' + idSuffix + '-' + index);

    if (highlight) {
        $rubricCol.addClass('cell-selected-negative');
    } else {
        $rubricCol.removeClass('cell-selected-negative');
    }
}
