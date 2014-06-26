var sectionIndex = 0;
var recipientIndex = 0;
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
    for(var giver in data.responses[recipient]){
        var giversList = data.responses[recipient];
        if(giversList.hasOwnProperty(giver)){
            firstResponse = giversList[giver][0];
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
        appendedResponses += '<a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-' + teamIndex + '%>">'
                                + 'Expand Students</a><br><br>';
    }

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

    var giverIndex = 0;

    var hasResponse = false;
    for(var giver in data.responses[recipient]){
        var giversList = data.responses[recipient];
        if(giversList.hasOwnProperty(giver)){
            hasResponse = true;
            giverIndex++;
            appendedResponses += getResponsesFromrecipient(recipient, giver, giverIndex, data);
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

function getResponsesFromrecipient(recipient, giver, giverIndex, data){
    var appendedResponses = '';
    var responsesList = data.responses[recipient][giver];
    appendedResponses += '<div class="row ' + (giverIndex == 1 ? "": "border-top-gray") + '">';
    appendedResponses += '<div class="col-md-2"><strong>From: ' + giver + '</strong></div>';
    appendedResponses += '<div class="col-md-10">';

    var qnIndx = 0;
    for(var i = 0; i < responsesList.length; i++){
        qnIndx++;
        var response = responsesList[i];
        var responseId = response['feedbackResponseId'];
        var questionId = response['feedbackQuestionId'];
        var giverSection = response['giverSection'];
        var recipientSection = response['recipientSection'];
        var question = data.questionsInfo[questionId];
        appendedResponses += '<div class="panel panel-info">';
        appendedResponses += '<div class="panel-heading">Question ' + question['questionNum'] + ': ' + question['questionText'] + question['questionAdditionalInfo'].replace(/additionalInfoId/g,'recipient-' + recipientIndex +'-giver-' + giverIndex) + '</div>';
        appendedResponses += '<div class="panel-body"> <div style="clear:both; overflow: hidden"><div class="pull-left">';
        appendedResponses +=  data.answer[responseId] + '</div>';
        appendedResponses += '<button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment"'
                                + 'onclick="showResponseCommentAddForm('+ recipientIndex +',' + giverIndex+',' + qnIndx + ')"'
                                + 'data-toggle="tooltip" data-placement="top" title="Add comment" ';
        if(!data.privilegesInfo[recipientSection]['cansubmitsessioninsection'] || !data.privilegesInfo[giverSection]['cansubmitsessioninsection']){
            appendedResponses += 'disabled="disabled"';
        }
        var responseComments = data.comments[responseId];
        appendedResponses += '><span class="glyphicon glyphicon-comment glyphicon-primary"></span></button></div>'
        appendedResponses += '<ul class="list-group" id="responseCommentTable-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '"'
                                + 'style="' + ((typeof responseComments != 'undefined' && responseComments.length > 0) ? "margin-top:15px;": "display:none;") + '">';
        if(typeof responseComments != 'undefined' && responseComments.length > 0){
            var responseCommentIndex = 0;
            for(var j = 0; j < responseComments.length; j++){
                responseCommentIndex++;
                var comment = responseComments[j];
                appendedResponses += '<li class="list-group-item list-group-item-warning" id="responseCommentRow-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '-' + responseCommentIndex + '">';
                appendedResponses += '<div id="commentBar-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '-' + responseCommentIndex + '">';
                appendedResponses += '<span class="text-muted">From: '+ comment['giverEmail'] + ' [' + comment['createdAt'] + ']</span>';
                appendedResponses += '<form class="responseCommentDeleteForm pull-right">';
                appendedResponses += '<a href="/page/instructorFeedbackResponseCommentDelete" type="button" id="commentdelete-' + responseCommentIndex + '" class="btn btn-default btn-xs icon-button"'
                                        + 'data-toggle="tooltip" data-placement="top" title="Delete this comment"';
                if (!data.privilegesInfo[giverSection]['canmodifysessioncommentinsection'] || !data.privilegesInfo[recipientSection]['canmodifysessioncommentinsection']) { 
                    appendedResponses += 'disabled="disabled"';
                } 
                appendedResponses += '><span class="glyphicon glyphicon-trash glyphicon-primary"></span></a>';
                appendedResponses += '<input type="hidden" name="responseid" value="' + responseId + '">';
                appendedResponses += '<input type="hidden" name="responsecommentid" value="' + comment['feedbackResponseCommentId'] + '">';
                appendedResponses += '<input type="hidden" name="courseid" value="' + response['courseId'] + '">';
                appendedResponses += '<input type="hidden" name="fsname" value="' + response['feedbackSessionName'] + '">';
                appendedResponses += '<input type="hidden" name="user" value="' + data.account['googleId'] + '">';
                appendedResponses += '</form>';
                appendedResponses += '<a type="button" id="commentedit-' + responseCommentIndex + '" class="btn btn-default btn-xs icon-button pull-right"'
                                        + 'onclick="showResponseCommentEditForm(' + recipientIndex + ',' + giverIndex + ',' + qnIndx + ',' + responseCommentIndex + ')"'
                                        + 'data-toggle="tooltip" data-placement="top" title="Edit this comment"';
                if (!data.privilegesInfo[giverSection]['canmodifysessioncommentinsection'] || !data.privilegesInfo[recipientSection]['canmodifysessioncommentinsection']) { 
                    appendedResponses += 'disabled="disabled"';
                }
                appendedResponses += '><span class="glyphicon glyphicon-pencil glyphicon-primary"></span></a></div>';
                appendedResponses += '<div id="plainCommentText-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '-' + responseCommentIndex + '">' + comment['commentText']['value'] + '</div>';
                appendedResponses += '<form style="display:none;" id="responseCommentEditForm-' + giverIndex + '-' + recipientIndex + '-' + qnIndx + '-' + responseCommentIndex + '" class="responseCommentEditForm">';
                appendedResponses += '<div class="form-group"><textarea class="form-control" rows="3" placeholder="Your comment about this response"' 
                                        + 'name="responsecommenttext" id="responsecommenttext-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '-' + responseCommentIndex + '">' + comment['commentText']['value'] + '</textarea></div>';
                appendedResponses += '<div class="col-sm-offset-5"><a href="/page/instructorFeedbackResponseCommentEdit" type="button" class="btn btn-primary" id="button_save_comment_for_edit-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '-' + responseCommentIndex + '">';
                appendedResponses += 'Save</a><input type="button" class="btn btn-default" value="Cancel" onclick="return hideResponseCommentEditForm(' +recipientIndex + ',' + giverIndex + ',' + qnIndx + ',' + responseCommentIndex + ');"></div>';
                appendedResponses += '<input type="hidden" name="responseid" value="' + responseId + '">';
                appendedResponses += '<input type="hidden" name="responsecommentid" value="' + comment['feedbackResponseCommentId'] + '">';
                appendedResponses += '<input type="hidden" name="courseid" value="' + response['courseId'] + '">';
                appendedResponses += '<input type="hidden" name="fsname" value="' + response['feedbackSessionName'] + '">';
                appendedResponses += '<input type="hidden" name="user" value="' + data.account['googleId'] + '">';
                appendedResponses += '</form></li>';
            }
        }  

        appendedResponses += '<li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '" style="display:none;">';
        appendedResponses += '<form class="responseCommentAddForm"><div class="form-group"><textarea class="form-control" rows="3" placeholder="Your comment about this response" name="responsecommenttext" id="responseCommentAddForm-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '"></textarea></div>';
        appendedResponses += '<div class="col-sm-offset-5"><a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '">Add</a>';
        appendedResponses += '  <input type="button" class="btn btn-default" value="Cancel" onclick="hideResponseCommentAddForm(' + recipientIndex + ',' + giverIndex + ',' + qnIndx + ')">';
        appendedResponses += '<input type="hidden" name="responseid" value="' + responseId + '">';
        appendedResponses += '<input type="hidden" name="questionid" value="' + questionId + '">';
        appendedResponses += '<input type="hidden" name="courseid" value="' + response['courseId'] + '">';
        appendedResponses += '<input type="hidden" name="fsname" value="' + response['feedbackSessionName'] + '">';
        appendedResponses += '<input type="hidden" name="user" value="' + data.account['googleId'] + '">';
        appendedResponses += '</div></form></li></ul></div></div>';                                                          
    }    
    
    appendedResponses += '</div></div>';

    return appendedResponses;
}

function getNumberOfKeys(jsonObject){
    var i = 0;
    for(var key in jsonObject){
        if(jsonObject.hasOwnProperty(key)){
            i++;
        }
    }
    return i;
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
            url :   $(formObject[0]).attr('action') + "?" + formData,
            beforeSend : function() {
                displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>")
            },
            error : function() {
                console.log('Error');
            },
            success : function(data) {
                $(panelBody[0]).html(getAppendedData(data));
                $(panelHeading).removeClass('ajax_submit');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');
                if(numPanels == 0){
                    numPanels +=  getNumberOfKeys(data.privilegesInfo) + 2;
                }
                var childrenPanels = $(panelBody[0]).find("div.panel");
                numPanels = bindCollapseEvents(childrenPanels, numPanels);

                $(panelBody[0]).find("form[class*='responseCommentAddForm'] > div > a").click(addCommentHandler);
    
                $(panelBody[0]).find("form[class*='responseCommentEditForm'] > div > a").click(editCommentHandler);
    
                $(panelBody[0]).find("form[class*='responseCommentDeleteForm'] > a").click(deleteCommentHandler);

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