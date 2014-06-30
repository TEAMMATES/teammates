var sectionIndex = 0;
var giverIndex = 0;
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
    for(var receiver in data.responses[giver]){
        var receiversList = data.responses[giver];
        if(receiversList.hasOwnProperty(receiver)){
            firstResponse = receiversList[receiver][0];
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
        appendedResponses += '<a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-' + teamIndex + '" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">'
                                + 'Expand Students</a><br><br>';
    }

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

    var recipientIndex = 0;

    var hasResponse = false;
    for(var receiver in data.responses[giver]){
        var receiversList = data.responses[giver];
        if(receiversList.hasOwnProperty(receiver)){
            hasResponse = true;
            recipientIndex++;
            appendedResponses += getResponsesToReceiver(giver, receiver, recipientIndex, data);
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

function getResponsesToReceiver(giver, receiver, recipientIndex, data){
    var appendedResponses = '';
    var responsesList = data.responses[giver][receiver];
    appendedResponses += '<div class="row ' + (recipientIndex == 1 ? "": "border-top-gray") + '">';
    appendedResponses += '<div class="col-md-2"><strong>To: ' + receiver + '</strong></div>';
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
        appendedResponses += '<div class="panel-heading">Question ' + question['questionNum'] + ': ' + question['questionText'] + question['questionAdditionalInfo'].replace(/additionalInfoId/g,'giver-' + giverIndex +'-recipient-' + recipientIndex) + '</div>';
        appendedResponses += '<div class="panel-body"> <div style="clear:both; overflow: hidden"><div class="pull-left">';
        appendedResponses +=  data.answer[responseId] + '</div>';
        appendedResponses += '<button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment"'
                                + 'onclick="showResponseCommentAddForm('+ recipientIndex +',' + giverIndex+',' + qnIndx + ')"'
                                + 'data-toggle="tooltip" data-placement="top" title="Add comment" ';
        if(!data.privilegesInfo[giverSection]['cansubmitsessioninsection'] || !data.privilegesInfo[recipientSection]['cansubmitsessioninsection']){
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
                appendedResponses += '<form style="display:none;" id="responseCommentEditForm-' + recipientIndex + '-' + giverIndex + '-' + qnIndx + '-' + responseCommentIndex + '" class="responseCommentEditForm">';
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