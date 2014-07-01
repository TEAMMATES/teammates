var sectionIndex = 0;
var recipientIndex = 0;
var numPanels  = 0;
var currentTeam;
var teamIndex = 0;

function getAppendedData(data){
    var appendedHtml = '';
    var hasUsers = false;

    var groupByTeamEnabled = $('#frgroupbyteam').attr('checked') == 'checked';
    appendedHtml += '<a class="btn btn-success btn-xs pull-right" id="collapse-panels-button-section-' + sectionIndex + '" style="display:block;" data-toggle="tooltip" title="Collapse or expand all ' + (groupByTeamEnabled == true ? "team" : "student" ) + ' panels. You can also click on the panel heading to toggle each one individually.">'
                        + 'Expand ';
    if(groupByTeamEnabled){
        appendedHtml += 'Teams</a><br><br>';
    } else {
        appendedHtml += 'Students</a><br><br>';
    }

    for(var recipient in data.responses){
        if(data.responses.hasOwnProperty(recipient)){
            hasUsers = true;
            appendedHtml += getResponsesToRecipient(recipient, data);
        }
    }
    if(!hasUsers){
        appendedHtml += 'There is currently no responses to this section.';
    }
    sectionIndex++;

    return appendedHtml;
}

function getResponsesToRecipient(recipient, data){    
    var appendedResponses = '';
    var firstResponse;
    var questionsList = data.responses[recipient];
    for(var question in questionsList){
        if(questionsList.hasOwnProperty(question)){
            firstResponse = questionsList[question][0];
            break;
        }
    }

    var targetEmail = firstResponse.recipientEmail;
    var participantType = data.questionsInfo[firstResponse.feedbackQuestionId].questionRecipientType;
    var mailToStyleAttr = (targetEmail.indexOf("@@") != -1 || participantType == "Nobody specific (For general class feedback)" || participantType == "Other teams in the course") ? "style='display:none;'" : "";

    var groupByTeamEnabled = $('#frgroupbyteam').attr('checked') == 'checked';
    var recipientTeam = data.emailTeamNameTable[targetEmail];
    if(groupByTeamEnabled && (typeof currentTeam == 'undefined' || recipientTeam != currentTeam)){
        if(typeof currentTeam != 'undefined'){
            appendedResponses += '</div></div></div>';
        }
        teamIndex++;
        currentTeam = recipientTeam;
        appendedResponses += '<div class="panel panel-warning"><div class="panel-heading">';
        appendedResponses += '<strong>'+ currentTeam + '</strong>';
        appendedResponses += '<span class="glyphicon glyphicon-chevron-down pull-right"></span></div>';
        appendedResponses += '<div class="panel-collapse collapse"><div class="panel-body background-color-warning">';
        appendedResponses += '<a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-' + teamIndex + '" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">'
                                + 'Expand Students</a><br><br>';
        
        // Statistics
        appendedResponses += '<div class="resultStatistics">';
        appendedResponses += '<h3>' + currentTeam + ' Received Responses Statistics </h3><hr class="margin-top-0">';
        var numStatsShown = 0;
        if(typeof data.participantStats != 'undefined' && typeof data.participantStats[currentTeam] != 'undefined'){
            var currentParticipantStats = data.participantStats[currentTeam];
            for(var questionKey in currentParticipantStats){
                if(currentParticipantStats.hasOwnProperty(questionKey)){
                    var question = data.questionsInfo[questionKey];
                    if(currentParticipantStats[questionKey] == ""){
                        continue;
                    }
                    appendedResponses += '<div class="panel panel-info"><div class="panel-heading">';
                    appendedResponses += '<strong>Question ' + question['questionNum'] + ': </strong>' + question['questionText'] + question['questionAdditionalInfo'].replace(/additionalInfoId/g,'team-' + teamIndex) + '</div>';
                    appendedResponses += '<div class="panel-body padding-0"><div class="resultStatistics">';
                    appendedResponses +=  currentParticipantStats[questionKey];
                    appendedResponses += '</div></div></div>';
                    numStatsShown++;
                }
            }
        }      
        if(numStatsShown == 0){
            appendedResponses += '<p class="text-color-gray"><i>No statistics available.</i></p>';
        }
        appendedResponses += '</div>';
        appendedResponses += '<h3>' + currentTeam + ' Detailed Responses </h3><hr class="margin-top-0">';
    }

    // Responses
    appendedResponses += '<div class="panel panel-primary">';
    appendedResponses += '<div class="panel-heading">';
    appendedResponses += 'To: <strong>' + recipient + '</strong>';
    appendedResponses += '<a class="link-in-dark-bg" href="mailTo:' + targetEmail + '" '+ mailToStyleAttr + '> [' + targetEmail + ']</a>';
    if(groupByTeamEnabled){
        appendedResponses += '<span class="glyphicon glyphicon-chevron-down pull-right"></span>';
    } else {
        appendedResponses += '<span class="glyphicon glyphicon-chevron-down pull-right"></span>';
    }
    appendedResponses += '</div>';
    if(groupByTeamEnabled){
        appendedResponses += '<div class="panel-collapse collapse">';
    } else {
        appendedResponses += '<div class="panel-collapse collapse">';
    }
    
    appendedResponses += '<div class="panel-body">';

    var questionIndex = 0;

    var hasResponse = false;
    for(var question in questionsList){
        if(questionsList.hasOwnProperty(question)){
            hasResponse = true;
            questionIndex++;
            appendedResponses += getResponsesForQuestion(recipient, question, questionIndex, data);
        }
    }

    if(!hasResponse){
        appendedResponses += 'There is currently no responses to this user';
    }

    appendedResponses += '</div>';
    appendedResponses += '</div>';
    appendedResponses += '</div>';
    recipientIndex++;

    return appendedResponses;
}

function getResponsesForQuestion(recipient, question, questionIndex, data){
    var appendedResponses = '';
    var responsesList = data.responses[recipient][question];
    var questionId = responsesList[0].feedbackQuestionId;
    var questionInfo = data.questionsInfo[questionId];

    appendedResponses += '<div class="panel panel-info">';
    appendedResponses += ' <div class="panel-heading">Question ' + questionInfo['questionNum'] + ': ' + questionInfo['questionText'] + questionInfo['questionAdditionalInfo'].replace(/additionalInfoId/g,'recipient-' + recipientIndex +'-question-' + questionIndex) + '</div>';
    appendedResponses += '<div class="panel-body padding-0"><div class="resultStatistics">';
    appendedResponses += (typeof data.participantStats == 'undefined' || typeof data.participantStats[recipient][questionId] == 'undefined' ? "" : data.participantStats[recipient][questionId]) + '</div>';
    appendedResponses += '<table class="table table-striped table-bordered dataTable margin-0">';
    appendedResponses += '<thead class="background-color-medium-gray text-color-gray font-weight-normal"><tr>';
    appendedResponses += '<th id="button_sortTo" onclick="toggleSort(this,1)" style="width: 15%;">Giver</th>';
    appendedResponses += '<th id="button_sortFromTeam" onclick="toggleSort(this,2)" style="width: 15%;">Team</th>';
    appendedResponses += '<th id="button_sortFeedback" onclick="toggleSort(this,3)">Feedback</th>';
    appendedResponses += '</tr></thead>';
    appendedResponses += '<tbody>';
    for(var i = 0; i < responsesList.length; i++){
        var response = responsesList[i];
        appendedResponses += '<tr>';
        appendedResponses += '<td class="middlealign">' + data.emailNameTable[response.giverEmail] + '</td>';
        appendedResponses += '<td class="middlealign">' + data.emailTeamNameTable[response.giverEmail] + '</td>';
        appendedResponses += '<td class="multiline">' + data.answer[response.feedbackResponseId] + '</td>';
        appendedResponses += '</tr>';
    }
    appendedResponses += '</tbody></table></div></div>'                                
                                            
    return appendedResponses;
}

function getNumberOfSections(){
    return $("#sectionSelect option").length;    
}

$(document).ready(function(){
    var seeMoreRequest = function(e) {
        var panelHeading = $(this);
        var displayIcon = $(this).children('.display-icon');
        var formObject = $(this).children("form");
        var panelCollapse = $(this).parent().children('.panel-collapse');
        var panelBody = $(panelCollapse[0]).children('.panel-body');
        var formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type : 'POST',
            url : 	$(formObject[0]).attr('action') + "?" + formData,
            beforeSend : function() {
                displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>")
            },
            error : function() {
                console.log('Error');
            },
            success : function(data) {
                console.log(data);
                $(panelBody[0]).html(getAppendedData(data));
                $(panelHeading).removeClass('ajax_submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');
                if(numPanels == 0){
                    numPanels +=  getNumberOfSections() + 1;
                }
                var childrenPanels = $(panelBody[0]).find("div.panel");
                numPanels = bindCollapseEvents(childrenPanels, numPanels);

                $("a[id^='collapse-panels-button-section-'],a[id^='collapse-panels-button-team-']").off('click');
                $("a[id^='collapse-panels-button-section-'],a[id^='collapse-panels-button-team-']").on('click', function(){
                    var panels = $(this).parent().children('div.panel').children('.panel-collapse');
                    toggleCollapse(this, panels);
                });

                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            }
        });
    };
    $(".ajax_submit").click(seeMoreRequest);
});