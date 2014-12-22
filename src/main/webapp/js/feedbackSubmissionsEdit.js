jQuery.fn.reverse = [].reverse;

var FEEDBACK_RESPONSE_RECIPIENT = "responserecipient";
var FEEDBACK_RESPONSE_TEXT = "responsetext";

// On body load event
$(document).ready(function () {

    // Bind submission event
    $('form[name="form_submit_response"],form[name="form_student_submit_response"]').submit(function() {
        if(!validateConstSumQuestions()){
            return false;
        }
        formatRubricQuestions();
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

    readyConstSumQuestions();
    updateConstSumMessages();

    readyRubricQuestions();
});

//Ready rubric questions for answering by user
function readyRubricQuestions() {
    //Set cell to highlight on hover
    //Set cell to highloght when checked
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
        parentCell.click(function(){
                var radioInput = $(this).find("[name^='rubricChoice-']")
                radioInput.prop("checked", 
                    !radioInput.prop("checked"));
                radioInput.trigger("change");
                event.stopPropagation();
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

//Ready constant sum questions for answering by user
function readyConstSumQuestions() {
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
    if($("p[id^='constSumMessage-'].text-color-red").length > 0){
        setStatusMessage("Please fix the errors for distribution questions. To skip a distribution question, leave the boxes blank.", true)
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
    $('select.participantSelect.newResponse').each(function(){
        var firstUnhidden = "";
        $(this).children().reverse().each(function(){
            if (this.style.display != 'none' && $(this).val() != "") {
                firstUnhidden = this;
            }
        });
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