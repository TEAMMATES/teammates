var FEEDBACK_RESPONSE_RECIPIENT = 'responserecipient';
var FEEDBACK_RESPONSE_TEXT = 'responsetext';
var FEEDBACK_MISSING_RECIPIENT = 'You did not specify a recipient for your response in question(s)';
var WARNING_STATUS_MESSAGE = '.alert-warning.statusMessage';

// text displayed to user
var SESSION_NOT_OPEN = 'Feedback Session Not Open';

function isPreview() {
    return $(document).find('.navbar').text().indexOf('Preview') !== -1;
}

$(document).ready(function() {

    /**
     * Handles Keyup and Keydown on Text question to display response length
     */
    $('textarea[id^="responsetext-"]').keyup(function() {
        updateTextQuestionCharCount(this.id, $(this).data('lengthtextid'));
    });

    $('textarea[id^="responsetext-"]').keydown(function() {
        updateTextQuestionCharCount(this.id, $(this).data('lengthtextid'));
    });

    /**
     * Triggering keyup event for all text question type textfields, to call
     * function that finds out input length.
     */
    $('textarea[id^="responsetext-"]').keyup();

    $('form[name="form_submit_response"]').submit(function() {
        formatRubricQuestions();

        var validationStatus = true;

        validationStatus &= validateConstSumQuestions();
        validationStatus &= validateRankQuestions();
        validationStatus &= validateAllAnswersHaveRecipient();
        
        updateMcqOtherOptionField();
        updateMsqOtherOptionField();
        
        if (!validationStatus) {
            return false;
        }

        reenableFieldsForSubmission();
    });

    formatRecipientLists();

    // Replace hidden dropdowns with text
    $('select.participantSelect:hidden').each(function() {
        $(this).after('<span>' + $(this).find('option:selected').html() + '</span>');
    });
    
    $("input[type='radio']").change(function() {
        idOfOtherOptionText = 'otherOptionText' + $(this).attr('name').substr($(this).attr('name').search('-'));
        idOfOtherOptionFlag = 'otherOptionFlag' + $(this).attr('name').substr($(this).attr('name').search('-'));
    
        if ($(this).data('text') === 'otherOptionText') {
            // Other option is selected by the student
            $('#' + idOfOtherOptionText).prop('disabled', false);
            $('#' + idOfOtherOptionFlag).val('1');
        } else {
            // Any option except the other option is selected
            $('#' + idOfOtherOptionText).prop('disabled', true);
            $('#' + idOfOtherOptionFlag).val('0');
        }
    });
                   
    $("input[id^='otherOptionText']").keyup(function() {
        idOfOtherOptionRadioButton = $(this).attr('id').replace('Text', '');
        $('#' + idOfOtherOptionRadioButton).val($(this).val());
    });
    
    disallowNonNumericEntries($('input[type=number]'), true, true);

    $('input.pointsBox').off('keydown');

    disallowNonNumericEntries($('input.pointsBox'), false, false);

    prepareContribQuestions();

    prepareMSQQuestions();

    prepareConstSumQuestions();

    updateConstSumMessages();

    prepareRubricQuestions();

    prepareMCQQuestions();

    prepareRankQuestions();

    focusModeratedQuestion();

    showModalWarningIfSessionClosed();
});

// Saves the value in the other option textbox for MCQ questions
function updateMcqOtherOptionField() {
    var mcqQuestionNums = getQuestionTypeNumbers('MCQ');
    
    for (var i = 0; i < mcqQuestionNums.length; i++) {
        var qnNum = mcqQuestionNums[i];
        var numResponses = $('[name="questionresponsetotal-' + qnNum + '"]').val();

        for (var j = 0; j < numResponses; j++) {
            $('[data-text="otherOptionText"][name="responsetext-' + qnNum + '-' + j + '"]')
                 .val($('#otherOptionText-' + qnNum + '-' + j).val());
        }
    }
}

// Saves the value in the other option textbox for MSQ questions
function updateMsqOtherOptionField() {
    var msqQuestionNums = getQuestionTypeNumbers('MSQ');
    
    for (var i = 0; i < msqQuestionNums.length; i++) {
        var qnNum = msqQuestionNums[i];
        var numResponses = $('[name="questionresponsetotal-' + qnNum + '"]').val();

        for (var j = 0; j < numResponses; j++) {
            $('[data-text="msqOtherOptionText"][name="responsetext-' + qnNum + '-' + j + '"]')
                 .val($('#msqOtherOptionText-' + qnNum + '-' + j).val());
        }
    }
}

// Looks for the question to be moderated (if it exists)
function focusModeratedQuestion() {
    if ($('.moderated-question').length > 0) {
        scrollToElement($('.moderated-question')[0], { duration: 1000 });
    }
}

function prepareMCQQuestions() {
    var mcqQuestionNums = getQuestionTypeNumbers('MCQ');

    var radioButtons = {};
    var radioStates = {};

    for (var i = 0; i < mcqQuestionNums.length; i++) {
        var qnNum = mcqQuestionNums[i];
        var numResponses = $('[name="questionresponsetotal-' + qnNum + '"]').val();

        for (var j = 0; j < numResponses; j++) {
            var id = 'responsetext-' + qnNum + '-' + j;
            radioButtons[id] = $('[name=' + id + ']');
            radioStates[id] = {};

            // initialize radio buttons' states
            $.each(radioButtons[id], function(index, radio) {
                radioStates[id][radio.value] = $(radio).is(':checked');
            });

            radioButtons[id].click(function(event) {
                var val = $(this).val();
                var name = $(this).attr('name');
                var indexSuffix = name.substring(name.indexOf('-'));
          
                // toggle the radio button checked state
                $(this).attr('checked', radioStates[name][val] = !radioStates[name][val]);
                
                // If the radio button corresponding to 'Other' is clicked
                if ($(this).data('text') === 'otherOptionText') {
                    if ($(this).is(':checked')) {
                        $('#otherOptionText' + indexSuffix).prop('disabled', false); // enable textbox
                        $('#mcqIsOtherOptionAnswer' + indexSuffix).val('1');
                    } else {
                        $('#otherOptionText' + indexSuffix).prop('disabled', true); // disable textbox
                        $('#mcqIsOtherOptionAnswer' + indexSuffix).val('0');
                    }
                } else if ($('#mcqIsOtherOptionAnswer' + indexSuffix).length > 0) {
                    // If other option is enabled for the question
                    $('#otherOptionText' + indexSuffix).prop('disabled', true); // disable textbox
                    $('#mcqIsOtherOptionAnswer' + indexSuffix).val('0');
                }

                $.each(radioButtons[name], function(index, radio) {
                    if (radio.value !== val) {
                        radioStates[name][radio.value] = false;
                    }
                });

                event.stopImmediatePropagation();
            });
        }
    }
}

function prepareContribQuestions() {
    var contribQuestionNums = getQuestionTypeNumbers('CONTRIB');

    for (var i = 0; i < contribQuestionNums.length; i++) {
        var qnNum = contribQuestionNums[i];

        // Get number of options for the specified question number of contribution question type
        var optionNums = $('[name^="responsetext-' + qnNum + '-"]').length;

        for (var k = 0; k < optionNums; k++) {
            var $dropdown = $('[name="responsetext-' + qnNum + '-' + k + '"]');

            // Set initial color
            $dropdown.addClass($dropdown[0].options[$dropdown[0].selectedIndex].className);

            $dropdown.on('change', function() {
                $(this).removeClass('color_neutral');
                $(this).removeClass('color-positive');
                $(this).removeClass('color-negative');
                $(this).addClass(this.options[this.selectedIndex].className);
            });
        }
    }
}

function prepareMSQQuestions() {
    // Get index of MSQ questions
    var msqQuestionNums = getQuestionTypeNumbers('MSQ');

    for (var i = 0; i < msqQuestionNums.length; i++) {
        var qnNum = msqQuestionNums[i];

        var noneOfTheAboveOption = $('input[name^="responsetext-' + qnNum + '-"][value=""]:not([data-text])');
        var otherOption = $('input[name^="responsetext-' + qnNum + '-"][data-text="msqOtherOptionText"]');

        // If 'other' is enabled for the question
        if (otherOption.length > 0) {
            // checkbox corresponding to 'other' is clicked
            otherOption.click(function() {
                var name = $(this).attr('name');
                var indexSuffix = name.substring(name.indexOf('-'));
                updateOtherOptionAttributes($(this), indexSuffix);
            });
        }

        // reset other options when "none of the above" is clicked
        noneOfTheAboveOption.click(function() {
            var $options = $(this).closest('table')
                                  .find('input[name^="responsetext-"][value!=""], '
                                        + 'input[name^="responsetext-"][data-text]'); // includes 'other'
            var name = $(this).attr('name');
            var indexSuffix = name.substring(name.indexOf('-'));
            
            $options.each(function() {
                $(this).prop('checked', false);
                
                // 'other' option is clicked
                if ($(this).attr('data-text') !== undefined) {
                    updateOtherOptionAttributes($(this), indexSuffix);
                }
            });
            
        });

        // reset "none of the above" if any option is clicked
        var $options = $('input[name^="responsetext-' + qnNum + '-"][value!=""], '
                        + 'input[name^="responsetext-' + qnNum + '-"][data-text]'); // includes 'other'

        $options.click(function() {
            var noneOfTheAboveOption = $(this).closest('table').find(
                                           'input[name^="responsetext-"][value=""]:not([data-text])');
            var name = $(this).attr('name');
            var indexSuffix = name.substring(name.indexOf('-'));
            
            noneOfTheAboveOption.prop('checked', false);
            
            // 'other' option is clicked
            if ($(this).attr('data-text') !== undefined) {
                updateOtherOptionAttributes($(this), indexSuffix);
            }
        });

    }
}

function updateOtherOptionAttributes(otherOption, indexSuffix) {
    if (otherOption.is(':checked')) {
        $('#msqOtherOptionText' + indexSuffix).prop('disabled', false); // enable textbox
        $('#msqIsOtherOptionAnswer' + indexSuffix).val('1');
    } else {
        $('#msqOtherOptionText' + indexSuffix).prop('disabled', true); // disable textbox
        $('#msqIsOtherOptionAnswer' + indexSuffix).val('0');
    }
}

function prepareRubricQuestions() {
    prepareDesktopRubricQuestions();
    prepareMobileRubricQuestions();
}

/**
 * Prepares desktop view for rubric questions by setting cells to highlight on hover, highlight when checked
 * and bind the cells click to click radio buttons
 */
function prepareDesktopRubricQuestions() {
    var $rubricRadioInputs = $('[name^="rubricChoice-"]');

    for (var i = 0; i < $rubricRadioInputs.length; i++) {
        var $parentCell = $($rubricRadioInputs[i]).parent();

        $parentCell.hover(function() {
            $(this).addClass('cell-hover');
        }, function() {
            $(this).removeClass('cell-hover');
        });

        $parentCell.click(function(event) {
            var $radioInput = $(this).find('[name^="rubricChoice-"]');

            if ($radioInput.prop('disabled')) {
                return;
            }

            if (event.target === this) {
                $radioInput.prop('checked', !$radioInput.prop('checked'));
                $radioInput.trigger('change');
            }
        });

        // Bind refresh highlights on check
        $($rubricRadioInputs[i]).on('change', function(event, isSync) {
            // Update all radio inputs in the same row.
            var $rowRadioInputs = $(this).closest('tr').find('[name^="rubricChoice-"]');
            var tableRow = $(this).closest('tr');

            if (tableRow.hasClass('row-answered')) {
                tableRow.removeClass('row-answered');
            }
                
            for (var j = 0; j < $rowRadioInputs.length; j++) {
                updateRubricCellSelectedColor($rowRadioInputs[j]);
            }

            if (isSync === undefined) {
                // Sync mobile UI
                syncRubricsMobileUi(this);
            }
        });

        // First time update of checked cells
        for (var j = 0; j < $rubricRadioInputs.length; j++) {
            updateRubricCellSelectedColor($rubricRadioInputs[j]);
        }
    }
}

/**
 * Prepares mobile view for rubric questions by setting panels
 * to change color on clicking the radio buttons and uncheck logic
 */
function prepareMobileRubricQuestions() {
    var $rubricRadioInputs = $('[name^="mobile-rubricChoice-"]');

    // setup initial panel colors
    var $filledInPanels = $rubricRadioInputs.filter(':checked').closest('.panel');
    $filledInPanels.removeClass('panel-default').addClass('panel-success');

    // setup panel highlighting when changing an option
    $rubricRadioInputs.on('change', function(event, isSync) {
        var $self = $(this);
        var $parentPanel = $self.closest('.panel');
        if ($self.is(':checked')) {
            $parentPanel.removeClass('panel-default').addClass('panel-success');
        } else {
            $parentPanel.addClass('panel-default').removeClass('panel-success');
        }
        if (isSync === undefined) {
            syncRubricsDesktopUi(this);
        }
    });

    // setup unchecking when clicking on selected radio button
    // reference: http://stackoverflow.com/a/6246260
    $rubricRadioInputs.closest('label').mousedown(function() {
        var $self = $(this);
        var $radioInput = $self.find('[name^="mobile-rubricChoice-"]');
        if ($radioInput.is(':checked') && !$radioInput.prop('disabled')) {
            var uncheck = function() {
                setTimeout(function() {
                    $radioInput.prop('checked', false);
                    $radioInput.trigger('change');
                }, 0);
            };
            var unbind = function() {
                $self.unbind('mouseup', up);
            };
            var up = function() {
                uncheck();
                unbind();
            };
            $self.bind('mouseup', up);
            $self.one('mouseout', unbind);
        }
    });
}

/**
 * Syncs the mobile ui for rubrics on changes to the desktop ui
 */
function syncRubricsMobileUi(changedInput) {
    var $changedInput = $(changedInput);
    var mobileInputId = '#mobile-' + changedInput.id;
    var mobileInputName = '[name^="mobile-' + changedInput.name + '"]';
    if ($changedInput.is(':checked')) {
        $(mobileInputId).click();
    } else {
        $(mobileInputName).prop('checked', false);
        $(mobileInputId).trigger('change', [true]);
    }
}

/**
 * Syncs the desktop ui for rubrics on changes to the mobile ui
 */
function syncRubricsDesktopUi(changedInput) {
    var $changedInput = $(changedInput);
    var desktopInputId = '#' + changedInput.id.replace('mobile-', '');
    var desktopInputName = '[name^="' + changedInput.name.replace('mobile-', '') + '"]';
    if ($changedInput.is(':checked')) {
        $(desktopInputId).click();
    } else {
        $(desktopInputName).prop('checked', false);
        $(desktopInputId).trigger('change', [true]);
    }
}

/**
 *  Updates the colour of a rubric cell if it is checked.
 */
function updateRubricCellSelectedColor(radioInput) {

    var cell = $(radioInput).parent();
    var tableRow = cell.parent();

    if ($(radioInput).prop('checked')) {
        cell.addClass('cell-selected');
        tableRow.addClass('row-answered');
    } else if (cell.hasClass('cell-selected')) {
        cell.removeClass('cell-selected');
    }
}

function formatRubricQuestions() {
    var rubricQuestionNums = getQuestionTypeNumbers('RUBRIC');
    for (var i = 0; i < rubricQuestionNums.length; i++) {
        var qnNum = rubricQuestionNums[i];
        var numResponses = $('[name="questionresponsetotal-' + qnNum + '"]').val();
        numResponses = parseInt(numResponses);

        for (var j = 0; j < numResponses; j++) {
            var responsetext = [];

            var $responses = $('[name^="rubricChoice-' + qnNum + '-' + j + '-"]:checked');

            for (var k = 0; k < $responses.length; k++) {
                responsetext.push($($responses[k]).val());
            }

            $('[name="responsetext-' + qnNum + '-' + j + '"]').val(responsetext);
        }
    }
}

function prepareConstSumQuestions() {
    var constSumQuestionNums = getQuestionTypeNumbers('CONSTSUM');

    for (var i = 0; i < constSumQuestionNums.length; i++) {
        var qnNum = constSumQuestionNums[i];

        if (!$('#response_submit_button').is(':disabled')
            || isPreview()) {
            if ($('#constSumToRecipients-' + qnNum).val() === 'true') {
                var numResponses = $('[name="questionresponsetotal-' + qnNum + '"]').val();
                numResponses = parseInt(numResponses);

                $('#constSumInfo-' + qnNum + '-' + (numResponses - 1)).show();
            }
        } else {
            $('[id^="constSumInfo-' + qnNum + '-"]').hide();
        }
    }
}

function getQuestionTypeNumbers(qnType) {
    var questions = $('input[name^="questiontype-"]').filter(function() {
        return $(this).val() === qnType;
    });

    var questionNums = [];

    for (var i = 0; i < questions.length; i++) {
        questionNums[i] = questions[i].name.substring('questiontype-'.length, questions[i].name.length);
    }

    return questionNums;
}

function updateConstSumMessages() {
    var constSumQuestionNums = getQuestionTypeNumbers('CONSTSUM');

    for (var i = 0; i < constSumQuestionNums.length; i++) {
        var qnNum = constSumQuestionNums[i];
        updateConstSumMessageQn(qnNum);
    }
}

function updateConstSumMessageQn(qnNum) {
    var numOptions = 0;
    var points = parseInt($('#constSumPoints-' + qnNum).val());
    var numRecipients = parseInt($('[name="questionresponsetotal-' + qnNum + '"]').val());
    var distributeToRecipients = $('#constSumToRecipients-' + qnNum).val() === 'true';
    var pointsPerOption = $('#constSumPointsPerOption-' + qnNum).val() === 'true';
    var forceUnevenDistribution = $('#constSumUnevenDistribution-' + qnNum).val() === 'true';

    if (distributeToRecipients) {
        numOptions = numRecipients;
    } else {
        numOptions = parseInt($('#constSumNumOption-' + qnNum).val());
    }

    if (pointsPerOption) {
        points *= numOptions;
    }

    var sum = 0;
    var remainingPoints = points;
    var allUnique = true;
    var allNotNumbers = true;
    var answerSet = {};

    function checkAndDisplayMessage(messageElement) {
        var message = '';

        if (allNotNumbers) {
            message = 'Please distribute ' + points + ' points among the above '
                      + (distributeToRecipients ? 'recipients.' : 'options.');
            messageElement.addClass('text-color-blue');
            messageElement.removeClass('text-color-red');
            messageElement.removeClass('text-color-green');
        } else if (remainingPoints === 0) {
            if (!forceUnevenDistribution || allUnique) {
                message = 'All points distributed!';
                messageElement.addClass('text-color-green');
                messageElement.removeClass('text-color-red');
                messageElement.removeClass('text-color-blue');
            }
        } else if (remainingPoints > 0) {
            message = remainingPoints + ' points left to distribute.';
            messageElement.addClass('text-color-red');
            messageElement.removeClass('text-color-green');
            messageElement.removeClass('text-color-blue');
        } else {
            message = 'Over allocated ' + -remainingPoints + ' points.';
            messageElement.addClass('text-color-red');
            messageElement.removeClass('text-color-green');
            messageElement.removeClass('text-color-blue');
        }

        if (!allNotNumbers && forceUnevenDistribution && !allUnique) {
            message += ' The same amount of points should not be given multiple times.';
            messageElement.addClass('text-color-red');
            messageElement.removeClass('text-color-green');
        }

        messageElement.text(message);
    }

    function updateSumBasedOn(ptsAllocatedParam) {
        var pointsAllocated = ptsAllocatedParam;
        if (isNumber(pointsAllocated)) {
            allNotNumbers = false;
        } else {
            pointsAllocated = 0;
        }

        sum += pointsAllocated;

        if (pointsAllocated in answerSet) {
            allUnique = false;
        }

        answerSet[pointsAllocated] = true;
    }

    if (distributeToRecipients) {
        var $constSumMessageElement = $('#constSumMessage-' + qnNum + '-' + (numOptions - 1));

        for (var i = 0; i < numOptions; i++) {
            var pointsAllocated = parseInt($('#' + FEEDBACK_RESPONSE_TEXT + '-' + qnNum + '-' + i + '-0').val());

            updateSumBasedOn(pointsAllocated);
        }

        remainingPoints = points - sum;

        checkAndDisplayMessage($constSumMessageElement);
    } else {
        for (var j = 0; j < numRecipients; j++) {
            sum = 0;
            allNotNumbers = true;
            answerSet = {};
            allUnique = true;
            remainingPoints = points;

            var $constSumMsgElement = $('#constSumMessage-' + qnNum + '-' + j);

            for (var k = 0; k < numOptions; k++) {
                var ptsAllocated = parseInt($('#' + FEEDBACK_RESPONSE_TEXT + '-' + qnNum + '-' + j + '-' + k).val());

                updateSumBasedOn(ptsAllocated);
            }

            remainingPoints = points - sum;

            checkAndDisplayMessage($constSumMsgElement);
        }
    }
}

function validateConstSumQuestions() {
    updateConstSumMessages();

    // When any of the const sum questions has an error.
    if ($('p[id^="constSumMessage-"].text-color-red').length > 0) {
        var constSumQuestionNums = getQuestionTypeNumbers('CONSTSUM');
        var statusMessage = 'Please fix the error(s) for distribution question(s)';
        var errorCount = 0;

        for (var i = 0; i < constSumQuestionNums.length; i++) {
            var qnNum = constSumQuestionNums[i];

            // indicate the question number where the errors are located at
            if ($('p[id^="constSumMessage-' + qnNum + '-"].text-color-red').length > 0) {
                statusMessage += errorCount === 0 ? '' : ',';
                statusMessage += ' ';
                statusMessage += qnNum;
                errorCount++;
            }
        }

        statusMessage += '. ';
        statusMessage += 'To skip a distribution question, leave the boxes blank.';

        setStatusMessage(statusMessage, StatusType.DANGER);
        return false;
    }

    return true;
}

/**
 * Removes already selected options for recipients from other select dropdowns within the same question.
 * Binds further changes to show/hide options such that duplicates cannot be selected.
 */
function formatRecipientLists() {
    $('select.participantSelect').each(function() {
        if (!$(this).hasClass('.newResponse')) {
            // Remove options from existing responses
            var questionNumber = $(this).attr('name').split('-')[1];
            var selectedOption = $(this).find('option:selected').val();

            if (selectedOption !== '') {
                selectedOption = sanitizeForJs(selectedOption);
                $('select[name|=' + FEEDBACK_RESPONSE_RECIPIENT + '-' + questionNumber + ']')
                    .not(this)
                    // leave this in double quotes and single within, will fail otherwise
                    .find("option[value='" + selectedOption + "']")
                        .hide();
            }
        }

        // Save initial data.
        $(this).data('previouslySelected', $(this).val());
    }).change(function() {
        var questionNumber = $(this).attr('name').split('-')[1];
        var lastSelectedOption = $(this).data('previouslySelected');
        var curSelectedOption = $(this).find('option:selected').val();

        if (lastSelectedOption !== '') {
            $('select[name|=' + FEEDBACK_RESPONSE_RECIPIENT + '-' + questionNumber + ']')
                .not(this)
                // leave this in double quotes and single within, will fail otherwise
                .find("option[value='" + lastSelectedOption + "']")
                    .show();
        }

        if (curSelectedOption !== '') {
            curSelectedOption = sanitizeForJs(curSelectedOption);
            $('select[name|=' + FEEDBACK_RESPONSE_RECIPIENT + '-' + questionNumber + ']')
                .not(this)
                // leave this in double quotes and single within, will fail otherwise
                .find("option[value='" + curSelectedOption + "']")
                    .hide();
        }

        // Save new data
        $(this).data('previouslySelected', $(this).val());
    });

    // Auto-select first valid option.
    $('select.participantSelect.newResponse').each(function() {
        var firstUnhidden = '';

        // select the first valid recipient if the dropdown is hidden from the user,
        // otherwise, leave it as ""
        if (this.style.display === 'none') {
            $($(this).children().get().reverse()).each(function() {
                if (this.style.display !== 'none' && $(this).val() !== '') {
                    firstUnhidden = this;
                }
            });
        }

        $(this).val($(firstUnhidden).val()).change();
    });
}

function reenableFieldsForSubmission() {
    $(':disabled').prop('disabled', false);
}

function validateNumScaleAnswer(qnIdx, responseIdx) {
    var $answerBox = $('[name=responsetext-' + qnIdx + '-' + responseIdx + ']');
    var min = parseInt($answerBox.attr('min'));
    var max = parseInt($answerBox.attr('max'));
    var answer = parseInt($answerBox.val());

    if (answer < min) {
        $answerBox.val($answerBox.attr('min'));
    } else if (answer > max) {
        $answerBox.val($answerBox.attr('max'));
    }
}

function isAnswerBlank(question, response) {
    var $answer = $('[name=responsetext-' + question + '-' + response + ']');

    if ($answer.attr('type') === 'radio' || $answer.attr('type') === 'checkbox') {
        // for question types that involve checking boxes such as MSQ, MCQ
        return !$answer.is(':checked');
    }
    return $answer.val().trim() === '';
}

// Checks that there are no responses written to an unspecified recipient
function validateAllAnswersHaveRecipient() {
    var blankRecipients = $('select[name^="responserecipient-"]').filter(function() {
        return $(this).val() === '';
    });

    var isAllAnswersToMissingRecipientEmpty = true;
    var statusMessage = FEEDBACK_MISSING_RECIPIENT;
    var errorCount = 0;

    // for every response without a recipient, check that the response is empty
    for (var i = 0; i < blankRecipients.length; i++) {
        var recipient = blankRecipients[i];

        var question = $(recipient).attr('name').split('-')[1];
        var response = $(recipient).attr('name').split('-')[2];

        if (!isAnswerBlank(question, response)) {
            statusMessage += errorCount === 0 ? '' : ',';
            statusMessage += ' ';
            statusMessage += question;
            errorCount++;

            isAllAnswersToMissingRecipientEmpty = false;
        }
    }

    if (!isAllAnswersToMissingRecipientEmpty) {
        setStatusMessage(statusMessage + '.', StatusType.DANGER);
    }

    return isAllAnswersToMissingRecipientEmpty;
}

function prepareRankQuestions() {
    var rankQuestionNums = getQuestionTypeNumbers('RANK_OPTIONS').concat(getQuestionTypeNumbers('RANK_RECIPIENTS'));

    for (var i = 0; i < rankQuestionNums.length; i++) {
        var qnNum = rankQuestionNums[i];

        var isRankingRecipients = $('#rankToRecipients-' + qnNum).val() === 'true';

        if (!$('#response_submit_button').is(':disabled')
            || isPreview()) {

            if (isRankingRecipients) {
                var numResponses = $('[name="questionresponsetotal-' + qnNum + '"]').val();
                numResponses = parseInt(numResponses);

                $('#rankInfo-' + qnNum + '-' + (numResponses - 1)).show();
            }
        } else {
            $('[id^="rankInfo-' + qnNum + '-"]').hide();
        }

    }
    updateRankMessages();
}

function updateRankMessages() {
    var rankQuestionNums = getQuestionTypeNumbers('RANK_OPTIONS').concat(getQuestionTypeNumbers('RANK_RECIPIENTS'));

    for (var i = 0; i < rankQuestionNums.length; i++) {
        var qnNum = rankQuestionNums[i];
        updateRankMessageQn(qnNum);
    }
}

function validateRankQuestions() {
    updateRankMessages();

    // if any of the rank questions has an error.
    if ($('p[id^="rankMessage-"].text-color-red').length > 0) {
        var rankQuestionNums = getQuestionTypeNumbers('RANK_OPTIONS').concat(getQuestionTypeNumbers('RANK_RECIPIENTS'));
        var statusMessage = 'Please fix the error(s) for rank question(s)';
        var errorCount = 0;

        for (var i = 0; i < rankQuestionNums.length; i++) {
            var qnNum = rankQuestionNums[i];

            // indicate the question number where the errors are located at
            if ($('p[id^="rankMessage-' + qnNum + '-"].text-color-red').length > 0) {
                statusMessage += errorCount === 0 ? '' : ',';
                statusMessage += ' ';
                statusMessage += qnNum;
                errorCount++;
            }
        }

        statusMessage += '. ';
        statusMessage += 'To skip a rank question, leave all the boxes blank.';

        setStatusMessage(statusMessage, StatusType.DANGER);
        return false;
    }

    return true;
}

function updateRankMessageQn(qnNum) {
    var isDistributingToRecipients = $('#rankToRecipients-' + qnNum).val() === 'true';
    var areDuplicateRanksAllowed = $('#rankAreDuplicatesAllowed-' + qnNum).val() === 'true';
    var numRecipients = parseInt($('[name="questionresponsetotal-' + qnNum + '"]').val(), 10);

    var numOptions = isDistributingToRecipients ? numRecipients
                                                : parseInt($('#rankNumOptions-' + qnNum).val(), 10);

    var areAllAnswersUnique;
    var allocatedRanks;
    var isAllOptionsRanked;

    function resetState() {
        allocatedRanks = {};
        areAllAnswersUnique = true;
        isAllOptionsRanked = true;
    }

    function updateRankMessages($messageElement) {
        $messageElement.removeClass('text-color-red text-color-green text-color-blue');

        var message = '';

        if (!areDuplicateRanksAllowed && !areAllAnswersUnique) {
            message += ' The same rank should not be given multiple times. ';
            $messageElement.addClass('text-color-red');
        } else if (!isAllOptionsRanked) {
            message = 'Please rank the above ' + (isDistributingToRecipients ? 'recipients. '
                                                                             : 'options. ');
            $messageElement.addClass('text-color-blue');
        }

        $messageElement.text(message);
        if (message === '') {
            $messageElement.parent().find('hr').hide();
        } else {
            $messageElement.parent().find('hr').show();
        }
    }

    function updateAllocatedRanks(rankAllocated) {
        if (!isNumber(rankAllocated)) {
            isAllOptionsRanked = false;
            return;
        }
        if (rankAllocated in allocatedRanks) {
            areAllAnswersUnique = false;
        }
    
        allocatedRanks[rankAllocated] = true;
    }

    function updateDropdownOptions(qnNum, recipientIndex) {
        var dropdownSelect = $('select[id^="responsetext-' + qnNum + '-' + recipientIndex + '-"]');

        dropdownSelect.find('option').each(function() {
            if (allocatedRanks.hasOwnProperty($(this).val())) {
                $(this).addClass('color_neutral');
            } else {
                $(this).removeClass('color_neutral');
            }
        });
    }

    if (isDistributingToRecipients) {
        // for Rank Recipients question
        resetState();
        
        var $rankMessageElement = $('#rankMessage-' + qnNum + '-' + (numOptions - 1));

        for (var i = 0; i < numOptions; i++) {
            var rankAllocated = parseInt($('#' + FEEDBACK_RESPONSE_TEXT + '-' + qnNum + '-' + i + '-0').val(), 10);
            updateAllocatedRanks(rankAllocated);
        }
        for (var j = 0; j < numOptions; j++) {
            updateDropdownOptions(qnNum, j);
        }

        updateRankMessages($rankMessageElement);
    } else {
        // for Rank options question
        for (var i1 = 0; i1 < numRecipients; i1++) {
            resetState();

            var $rankMsgElement = $('#rankMessage-' + qnNum + '-' + i1);

            for (var j1 = 0; j1 < numOptions; j1++) {
                var rankAlloc = parseInt($('#' + FEEDBACK_RESPONSE_TEXT + '-' + qnNum + '-' + i1 + '-' + j1).val(), 10);
                updateAllocatedRanks(rankAlloc);
            }

            updateDropdownOptions(qnNum, i1);
            updateRankMessages($rankMsgElement);
        }
    }
}

function showModalWarningIfSessionClosed() {
    if (hasWarningMessage()) {
        BootboxWrapper.showModalAlert(SESSION_NOT_OPEN, getWarningMessage(), BootboxWrapper.DEFAULT_OK_TEXT,
                                      StatusType.WARNING);
    }
}

function hasWarningMessage() {
    return $(WARNING_STATUS_MESSAGE).length;
}

function getWarningMessage() {
    return $(WARNING_STATUS_MESSAGE).html().trim();
}

/**
 * Updates the length of the textArea
 * @param textAreaId - Id of text area for which char are to be counted
 * @param charCountId - Id of Label to display length of text area
 */
function updateTextQuestionCharCount(textAreaId, charCountId) {
    var cs = $('#' + textAreaId).val().length;
    $('#' + charCountId).text(cs);
}
