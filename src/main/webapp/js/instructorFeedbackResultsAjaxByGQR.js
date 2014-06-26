var sectionIndex = 0;
var giverIndex = 0;
var numPanels  = 0;
var currentTeam;
var teamIndex = 0;

function getAppendedData(data){
    var appendedHtml = '';
    var hasUsers = false;

    var groupByTeamEnabled = $('#frgroupbyteam').attr('checked') == 'checked';
    appendedHtml += '<a class="btn btn-success btn-xs pull-right" id="collapse-panels-button-section-' + sectionIndex + '" style="display:block;">'
                        + 'Expand ';
    if(groupByTeamEnabled){
        appendedHtml += 'Teams</a><br><br>';
    } else {
        appendedHtml += 'Students</a><br><br>';
    }

    for(var giver in data.responses){
        if(data.responses.hasOwnProperty(giver)){
            hasUsers = true;
            appendedHtml += getResponsesFromGiver(giver, data);
        }
    }
    if(!hasUsers){
        appendedHtml += 'There is currently no responses from this section.';
    }
    sectionIndex++;

    return appendedHtml;
}

function getResponsesFromGiver(giver, data){    
    var appendedResponses = '';
    var firstResponse;
    var questionsList = data.responses[giver];
    for(var question in questionsList){
        if(questionsList.hasOwnProperty(question)){
            firstResponse = questionsList[question][0];
            break;
        }
    }

    var targetEmail = firstResponse.giverEmail.replace("'s Team", "");
    var targetEmailDisplay = firstResponse.giverEmail;
    var mailToStyleAttr = (targetEmailDisplay.indexOf("@@") != -1) ? "style='display:none;'" : "";

    var groupByTeamEnabled = $('#frgroupbyteam').attr('checked') == 'checked';
    var giverTeam = data.emailTeamNameTable[targetEmail];
    if(groupByTeamEnabled && (typeof currentTeam == 'undefined' || giverTeam != currentTeam)){
        if(typeof currentTeam != 'undefined'){
            appendedResponses += '</div></div></div>';
        }
        teamIndex++;
        currentTeam = giverTeam;
        appendedResponses += '<div class="panel panel-warning"><div class="panel-heading">';
        appendedResponses += '<strong>'+ currentTeam + '</strong>';
        appendedResponses += '<span class="glyphicon glyphicon-chevron-down pull-right"></span></div>';
        appendedResponses += '<div class="panel-collapse collapse"><div class="panel-body background-color-warning">';
        appendedResponses += '<a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-' + teamIndex + '%>">'
                                + 'Expand Students</a><br><br>';
        
        // Statistics
        appendedResponses += '<div class="resultStatistics">';
        appendedResponses += '<h3>' + currentTeam + ' Given Responses Statistics </h3><hr class="margin-top-0">';
        var numStatsShown = 0;
        if(typeof data.participantStats != 'undefined' && typeof data.participantStats[currentTeam] != 'undefined'){
            var currentParticipantStats = data.participantStats[currentTeam];
            for(var questionKey in currentParticipantStats){
                if(currentParticipantStats.hasOwnProperty(questionKey)){
                    var question = data.questionsInfo[questionKey];
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
    appendedResponses += 'From: <strong>' + giver + '</strong>';
    appendedResponses += '<a class="link-in-dark-bg" href="mailTo:' + targetEmail + '" '+ mailToStyleAttr + '> [' + targetEmailDisplay + ']</a>';
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
            appendedResponses += getResponsesForQuestion(giver, question, questionIndex, data);
        }
    }

    if(!hasResponse){
        appendedResponses += 'There is currently no responses from this user';
    }

    appendedResponses += '</div>';
    appendedResponses += '</div>';
    appendedResponses += '</div>';
    giverIndex++;

    return appendedResponses;
}

function getResponsesForQuestion(giver, question, questionIndex, data){
    var appendedResponses = '';
    var responsesList = data.responses[giver][question];
    var questionId = responsesList[0].feedbackQuestionId;
    var questionInfo = data.questionsInfo[questionId];

    appendedResponses += '<div class="panel panel-info">';
    appendedResponses += ' <div class="panel-heading">Question ' + questionInfo['questionNum'] + ': ' + questionInfo['questionText'] + questionInfo['questionAdditionalInfo'].replace(/additionalInfoId/g,'giver-' + giverIndex +'-question-' + questionIndex) + '</div>';
    appendedResponses += '<div class="panel-body padding-0"><div class="resultStatistics">';
    appendedResponses += (typeof data.participantStats == 'undefined' || typeof data.participantStats[giver][questionId] == 'undefined' ? "" : data.participantStats[giver][questionId]) + '</div>';
    appendedResponses += '<table class="table table-striped table-bordered dataTable margin-0">';
    appendedResponses += '<thead class="background-color-medium-gray text-color-gray font-weight-normal"><tr>';
    appendedResponses += '<th id="button_sortTo" onclick="toggleSort(this,1)" style="width: 15%;">Recipient</th>';
    appendedResponses += '<th id="button_sortFromTeam" onclick="toggleSort(this,2)" style="width: 15%;">Team</th>';
    appendedResponses += '<th id="button_sortFeedback" onclick="toggleSort(this,3)">Feedback</th>';
    appendedResponses += '</tr></thead>';
    appendedResponses += '<tbody>';
    for(var i = 0; i < responsesList.length; i++){
        var response = responsesList[i];
        appendedResponses += '<tr>';
        appendedResponses += '<td class="middlealign">' + data.emailNameTable[response.recipientEmail] + '</td>';
        appendedResponses += '<td class="middlealign">' + data.emailTeamNameTable[response.recipientEmail] + '</td>';
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
                    numPanels +=  getNumberOfSections();
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