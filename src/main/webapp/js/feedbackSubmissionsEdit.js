jQuery.fn.reverse = [].reverse;

var FEEDBACK_RESPONSE_RECIPIENT = "responserecipient";
var FEEDBACK_RESPONSE_TEXT = "responsetext";
var FEEDBACK_MISSING_RECIPIENT = "You did not specify a recipient for your response in question(s)";

// On body load event
$(document).ready(function () {

    // Bind submission event
    $('form[name="form_submit_response"],form[name="form_student_submit_response"]').submit(function() {
        formatRubricQuestions();
        
        var validationStatus = true;
        validationStatus &= validateConstSumQuestions();
        validationStatus &= validateAllAnswersHaveRecipient();
        if(!validationStatus) {
            return false;
        }
        
        reenableFieldsForSubmission();
    });
    
    // Format recipient dropdown lists
    formatRecipientLists();
    
    // Replace hidden dropdowns with text
    $('select.participantSelect:hidden').each(function (){
        $(this).after('<span> '+$(this).find('option:selected').html()+'</span>');
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
});

// Prepare mcq questions for answering by user
function prepareMCQQuestions() {
	// Get index of mcq question
	var mcqQuestionNums = getQuestionTypeNumbers("MCQ");

	var radioButtons = {};
	var radioStates = {};
	for (var i = 0; i < mcqQuestionNums.length; i++) {
		var qnNum = mcqQuestionNums[i];
		var numResponses = $("[name='questionresponsetotal-" + qnNum + "']")
				.val();

		for (var j = 0; j < numResponses; j++) {
			var id = "responsetext-" + qnNum + "-" + j;
			radioButtons[id] = $("[name^=" + id + "]");
			radioStates[id] = {};

			// initialize radio buttons' states
			$.each(radioButtons[id], function(index, radio) {
				radioStates[id][radio.value] = $(radio).is(":checked");
			});

			radioButtons[id].click(function() {
				var val = $(this).val();
				var name = $(this).attr("name");
				// toggle the radio button checked state
				$(this).attr("checked",
						(radioStates[name][val] = !radioStates[name][val]));

				// set other radio buttons' states to false
				$.each(radioButtons[name], function(index, radio) {
					if (radio.value != val) {
						radioStates[name][radio.value] = false;
					}
				});
			});
		}
	}
}

// Prepare contrib questions for answering by user
function prepareContribQuestions() {

    // Get index of contribution questions
    var contribQuestionNums = getQuestionTypeNumbers("CONTRIB");
    for(var i=0 ; i<contribQuestionNums.length ; i++){
        var qnNum = contribQuestionNums[i];
        
        // Get number of options for the specified question number of contribution question type
        var optionNums = $("[name^='responsetext-" + qnNum + "']").length;
        for(var k=0; k<optionNums; k++){

            var dropdown = $("[name^='responsetext-" + qnNum + "-" + k + "']");

            // Bind on change event
            dropdown.on("change", function() {
                $(this).removeClass("color_neutral");
                $(this).removeClass("color-positive");
                $(this).removeClass("color-negative");
                $(this).addClass(this.options[this.selectedIndex].className);
            });            
        }
    }
}

// Prepare MSQ questions 
function prepareMSQQuestions() {
    // Get index of MSQ questions
    var msqQuestionNums = getQuestionTypeNumbers("MSQ");
    for(var i=0 ; i<msqQuestionNums.length ; i++) {
        var qnNum = msqQuestionNums[i];
        
        var noneOfTheAboveOption = $("input[name^='responsetext-" + qnNum + "'][value='']");

        // reset other options when "none of the above" is clicked
        noneOfTheAboveOption.click(function() {
            var options = $(this).closest("table").find("input[name^='responsetext-'][value!='']");
            
            options.each(function() {
                $(this).prop('checked', false);
            });
        });

        // reset "none of the above" if any option is clicked
        var options = $("input[name^='responsetext-" + qnNum + "'][value!='']");
        options.click(function() {
            var noneOfTheAboveOption = $(this).closest("table").find("input[name^='responsetext-'][value='']");
            noneOfTheAboveOption.prop('checked', false);
        });

    }
}

// Prepare rubric questions for answering by user
function prepareRubricQuestions() {
    //Set cell to highlight on hover
    //Set cell to highlight when checked
    //Bind cell click to click radio
    
    var rubricRadioInputs = $("[name^='rubricChoice-']");
    for (var i=0 ; i<rubricRadioInputs.length ; i++) {
        var parentCell = $(rubricRadioInputs[i]).parent();
        // Bind hover events
        parentCell.hover(
            function() {
                // Mouse enter
                $(this).addClass("cell-hover");
            }, function() {
                // Mouse leave
                $(this).removeClass("cell-hover");
            }
        );

        // Bind click
        parentCell.click(function(event){
                var radioInput = $(this).find("[name^='rubricChoice-']")

                // If input is disabled, do not check.
                if (radioInput.prop("disabled")) {
                    return;
                }

                // trigger checkbox manually if cell is clicked.
                if (event.target==this) {
                    radioInput.prop("checked", !radioInput.prop("checked"));
                    radioInput.trigger("change");
                }
            });

        // Bind refresh highlights on check
        $(rubricRadioInputs[i]).on("change", function(){
                // Update all radio inputs in the same row.
                var rowRadioInputs = $(this).closest("tr").find("[name^='rubricChoice-']");
                for (var j=0 ; j<rowRadioInputs.length ; j++) {
                    updateRubricCellSelectedColor(rowRadioInputs[j]);
                }
            });

        // First time update of checked cells
        for (var j=0 ; j<rubricRadioInputs.length ; j++) {
            updateRubricCellSelectedColor(rubricRadioInputs[j]);
        }
    }

}

/**
 *  Updates the colour of a rubric cell if it is checked.
 */
function updateRubricCellSelectedColor(radioInput) {
    if ($(radioInput).prop("checked")) {
        $(radioInput).parent().addClass("cell-selected");
    } else {
        if ($(radioInput).parent().hasClass("cell-selected")) {
            $(radioInput).parent().removeClass("cell-selected");
        }
    }
}

//Format rubric question for form submission
function formatRubricQuestions() {
    var rubricQuestionNums = getQuestionTypeNumbers("RUBRIC");
    for(var i=0 ; i<rubricQuestionNums.length ; i++) {
        var qnNum = rubricQuestionNums[i];
        var numResponses = $("[name='questionresponsetotal-"+qnNum+"']").val();
        numResponses = parseInt(numResponses);

        for (var j=0 ; j<numResponses ; j++) {
            var responsetext = [];

            var responses = $("[name^='rubricChoice-"+qnNum+"-"+j+"-']:checked");
            for (var k=0 ; k<responses.length ; k++) {
                responsetext.push($(responses[k]).val());
            }

            $("[name='responsetext-"+qnNum+"-"+j+"']").val(responsetext);
        }
    }
}

// Prepare constant sum questions for answering by user
function prepareConstSumQuestions() {
    var constSumQuestionNums = getQuestionTypeNumbers("CONSTSUM");

    for(var i=0 ; i<constSumQuestionNums.length ; i++){
        var qnNum = constSumQuestionNums[i];
        //const sum to recipients
        if(! $("#response_submit_button").is(":disabled") || $(document).find('.navbar').text().indexOf('Preview')!=-1){
            if( $("#constSumToRecipients-"+qnNum).val() === "true" ){
                var numResponses = $("[name='questionresponsetotal-"+qnNum+"']").val();
                numResponses = parseInt(numResponses);
                $("#constSumInfo-"+qnNum+"-"+(numResponses-1)).show();
            }
        } else {
            $("[id^='constSumInfo-"+qnNum+"-']").hide();
        }
    }
}

function getQuestionTypeNumbers(qnType){
    var questions = $("input[name^='questiontype-']").filter(function( index ) {
                                    return $(this).val() === qnType;
                                });
    var questionNums = [];
    for(var i=0 ; i<questions.length ; i++){
        questionNums[i] = questions[i].name.substring('questiontype-'.length,questions[i].name.length);
    }
    return questionNums;
}

//Updates all const sum messages
function updateConstSumMessages(){
    var constSumQuestionNums = getQuestionTypeNumbers("CONSTSUM");
    for(var i=0 ; i<constSumQuestionNums.length ; i++){
        var qnNum = constSumQuestionNums[i];
        updateConstSumMessageQn(qnNum);
    }
}

//updates const sum message for one question
function updateConstSumMessageQn(qnNum){
    var points = parseInt($("#constSumPoints-"+qnNum).val());
    var distributeToRecipients = $("#constSumToRecipients-"+qnNum).val() === "true" ? true : false;
    var pointsPerOption = $("#constSumPointsPerOption-"+qnNum).val() === "true" ? true : false;
    var numOptions = 0;
    var numRecipients = parseInt($("[name='questionresponsetotal-"+qnNum+"']").val());
    var forceUnevenDistribution = $("#constSumUnevenDistribution-"+qnNum).val() == "true" ? true : false;

    if(distributeToRecipients){
        numOptions = numRecipients;
    } else {
        numOptions = parseInt($("#constSumNumOption-"+qnNum).val());
    }

    if(pointsPerOption){
        points *= numOptions;
    }

    if(distributeToRecipients){
        var messageElement = $("#constSumMessage-"+qnNum+"-"+(numOptions-1));
        var sum = 0;
        var allNotNumbers = true;
        var answerSet = {};
        var allUnique = true;
        for(var i=0 ; i<numOptions ; i++){
            var p = parseInt($("#"+FEEDBACK_RESPONSE_TEXT+"-"+qnNum+"-"+i+"-0").val());
            if(!isNumber(p)) {
                p = 0;
            } else {
                allNotNumbers = false;
            }
            sum += p;
            if (p in answerSet) {
                allUnique = false;
            }
            answerSet[p] = true;
        }
        var remainingPoints = points - sum;
        var message = "";
        if(allNotNumbers){
            message = "Please distribute " + points + " points among the above " + (distributeToRecipients? "recipients." : "options.");
            $(messageElement).addClass("text-color-blue");
            $(messageElement).removeClass("text-color-red");
            $(messageElement).removeClass("text-color-green");
        } else if(remainingPoints === 0){
            if (!forceUnevenDistribution || allUnique) {
                message = "All points distributed!";
                $(messageElement).addClass("text-color-green");
                $(messageElement).removeClass("text-color-red");
                $(messageElement).removeClass("text-color-blue");
            }
        } else if(remainingPoints > 0){
            message = remainingPoints + " points left to distribute.";
            $(messageElement).addClass("text-color-red");
            $(messageElement).removeClass("text-color-green");
            $(messageElement).removeClass("text-color-blue");
        } else {
            message = "Over allocated " + (-remainingPoints) + " points.";
            $(messageElement).addClass("text-color-red");
            $(messageElement).removeClass("text-color-green");
            $(messageElement).removeClass("text-color-blue");
        }
        if (!allNotNumbers && forceUnevenDistribution && !allUnique) {
            message += " The same amount of points should not given multiple times.";
            $(messageElement).addClass("text-color-red");
            $(messageElement).removeClass("text-color-green");
        } 
        $(messageElement).text(message);
    } else {
        for(var j=0 ; j<numRecipients ; j++){
            var messageElement = $("#constSumMessage-"+qnNum+"-"+j);
            var sum = 0;
            var allNotNumbers = true;
            var answerSet = {};
            var allUnique = true;
            for(var i=0 ; i<numOptions ; i++){
                var p = parseInt($("#"+FEEDBACK_RESPONSE_TEXT+"-"+qnNum+"-"+j+"-"+i).val());
                if(!isNumber(p)) {
                    p = 0;
                } else {
                    allNotNumbers = false;
                }
                sum += p;
                if (p in answerSet) {
                    allUnique = false;
                }
                answerSet[p] = true;
            }
            var remainingPoints = points - sum;
            var message = "";
            if(allNotNumbers){
                message = "Please distribute " + points + " points among the above " + (distributeToRecipients? "recipients." : "options.");
                $(messageElement).addClass("text-color-blue");
                $(messageElement).removeClass("text-color-red");
                $(messageElement).removeClass("text-color-green");
            } else if(remainingPoints === 0){
                if (!forceUnevenDistribution || allUnique) {
                    message = "All points distributed!";
                    $(messageElement).addClass("text-color-green");
                    $(messageElement).removeClass("text-color-red");
                }
            } else if(remainingPoints > 0){
                message = remainingPoints + " points left to distribute.";
                $(messageElement).addClass("text-color-red");
                $(messageElement).removeClass("text-color-green");
                $(messageElement).removeClass("text-color-blue");
            } else {
                message = "Over allocated " + (-remainingPoints) + " points.";
                $(messageElement).addClass("text-color-red");
                $(messageElement).removeClass("text-color-green");
                $(messageElement).removeClass("text-color-blue");
            }
            if (!allNotNumbers && forceUnevenDistribution && !allUnique) {
                message += " The same amount of points should not be given multiple times.";
                $(messageElement).addClass("text-color-red");
                $(messageElement).removeClass("text-color-green");
            }
            $(messageElement).text(message);
        }
    }
    
}

function validateConstSumQuestions(){
    updateConstSumMessages();

    // When any of the const sum questions has an error.
    if($("p[id^='constSumMessage-'].text-color-red").length > 0){

        var constSumQuestionNums = getQuestionTypeNumbers("CONSTSUM");
        var statusMessage = "Please fix the error(s) for distribution question(s) ";
        var errorCount = 0;
        for(var i=0 ; i<constSumQuestionNums.length ; i++){
            var qnNum = constSumQuestionNums[i];
            
            // indicate the question number where the errors are located at
            if($("p[id^='constSumMessage-"+qnNum+"-'].text-color-red").length > 0){
                statusMessage += (errorCount == 0) ? "" : ",";
                statusMessage += " ";
                statusMessage += qnNum;
                errorCount++;
            }
        }

        statusMessage += ". ";
        statusMessage += "To skip a distribution question, leave the boxes blank.";
        setStatusMessage(statusMessage, true);
        return false;
    }
    return true;
}

/**
 * Removes already selected options for recipients
 * from other select dropdowns within the same question.
 * Binds further changes to show/hide options such that duplicates
 * cannot be selected.
 */
function formatRecipientLists(){
    $('select.participantSelect').each(function(){
        if (!$(this).hasClass(".newResponse")) {
            // Remove options from existing responses
            var questionNumber = 
                $(this).attr('name').split('-')[1];
            var selectedOption = $(this).find('option:selected').val();
            
            if (selectedOption != "") {
                $("select[name|="+FEEDBACK_RESPONSE_RECIPIENT+"-"+questionNumber+"]").not(this).
                    find("option[value='"+selectedOption+"']").hide();
            }
        }
        // Save initial data.
        $(this).data('previouslySelected',$(this).val());
    }).change(function() {
        var questionNumber = $(this).attr('name').split('-')[1];
        var lastSelectedOption = $(this).data('previouslySelected');
        var curSelectedOption = $(this).find('option:selected').val();

        if(lastSelectedOption != "") {
            $("select[name|="+FEEDBACK_RESPONSE_RECIPIENT+"-"+questionNumber+"]").not(this).
            find("option[value='"+lastSelectedOption+"']").show();
        }
        if (curSelectedOption != "") {
            $("select[name|="+FEEDBACK_RESPONSE_RECIPIENT+"-"+questionNumber+"]").not(this).
                find("option[value='"+curSelectedOption+"']").hide();
        }
        // Save new data
        $(this).data('previouslySelected',$(this).val());
    });
    
    // Auto-select first valid option.
    $('select.participantSelect.newResponse').each(function() {
        var firstUnhidden = "";
        // select the first valid recipient if the dropdown is hidden from the user,
        // otherwise, leave it as ""
        if (this.style.display == 'none') {
            $(this).children().reverse().each(function(){
                if (this.style.display != 'none' && $(this).val() != "") {
                    firstUnhidden = this;
                }
            });
        } 

        $(this).val($(firstUnhidden).val()).change();
    });
}

function reenableFieldsForSubmission() {
    $(':disabled').prop('disabled',false);
}

function validateNumScaleAnswer(qnIdx, responseIdx) {
    var answerBox = $("[name=responsetext-"+qnIdx+"-"+responseIdx+"]");
    var min = parseInt(answerBox.attr("min"));
    var max = parseInt(answerBox.attr("max"));
    var answer = parseInt(answerBox.val());
    if (answer < min) {
        answerBox.val(answerBox.attr("min"));
    } else if (answer > max) {
        answerBox.val(answerBox.attr("max"));
    }
}


function isAnswerBlank(question, response) {
    var answer = $("[name^=responsetext-" + question + "-" + response + "]");
    if (answer.attr("type") === "radio" || answer.attr("type") === "checkbox") {
        // for question types that involve checking boxes such as MSQ, MCQ 
        return !answer.is(":checked");
    } else {
        return answer.val().trim() === "";
    }
}

// Checks that there are no responses written to an unspecified recipient
function validateAllAnswersHaveRecipient() {
    var blankRecipients = $("select[name^='responserecipient-']").filter(function( index ) {
                              return $(this).val() === "";
                          });

    var isAllAnswersToMissingRecipientEmpty = true;
    var statusMessage = FEEDBACK_MISSING_RECIPIENT ;
    var errorCount = 0;
    // for every response without a recipient, check that the response is empty
    for (var i = 0; i < blankRecipients.length; i++) {
        var recipient = blankRecipients[i];

        var question = $(recipient).attr("name").split('-')[1];
        var response = $(recipient).attr("name").split('-')[2];

        var answer = $("[name^=responsetext-" + question + "-" + response + "]");

        if (!isAnswerBlank(question, response)) {
            statusMessage += (errorCount == 0) ? "" : ",";
            statusMessage += " ";
            statusMessage += question;
            errorCount++;
            
            isAllAnswersToMissingRecipientEmpty = false;
        }
    }

    if (!isAllAnswersToMissingRecipientEmpty) {
        setStatusMessage(statusMessage + ".", true);
    }

    return isAllAnswersToMissingRecipientEmpty;
}