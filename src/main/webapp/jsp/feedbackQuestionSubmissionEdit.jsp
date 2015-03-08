<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.FeedbackQuestionSubmissionEditPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
	FeedbackQuestionSubmissionEditPageData data = (FeedbackQuestionSubmissionEditPageData)request.getAttribute("data");
%>    
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
    
    <div class="well well-plain" >
        <div class="form-horizontal" role="form">
            <div class="form-group">
                <label class="col-sm-2 control-label">Course ID:</label>
                <div class="col-sm-10">
                    <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.courseId)%></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Session Name:</label>
                <div class="col-sm-10">
                    <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Opening Time:</label>
                <div class="col-sm-2">
                    <p class="form-control-static">
                        <%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%>
                    </p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Closing Time:</label>
                <div class="col-sm-10">
                    <p class="form-control-static">
                        <%=TimeHelper.formatTime(data.bundle.feedbackSession.endTime)%>
                    </p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">Instructions:</label>
                <div class="col-sm-10">
                    <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.instructions.getValue())%></p>
                </div>
            </div>
        </div>
    </div>

    <br>
    <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
    <br>
    <form class="form-horizontal" role="form">
<%
	int qnIndx = 1;

    FeedbackQuestionAttributes question = data.bundle.question;
    int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
    int maxResponsesPossible = data.bundle.recipientList.size();
    FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

    if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS ||
            numOfResponseBoxes > maxResponsesPossible) {
        numOfResponseBoxes = maxResponsesPossible;
    }
%>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>" value="<%=question.questionType%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>" value="<%=question.getId()%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL%>" value="<%=numOfResponseBoxes%>"/>
    <div class="form-horizontal">
        <div class="panel panel-primary">
            <div class="panel-heading">Question <%=question.questionNumber%>:<br/>
                <span class="text-preserve-space"><%=sanitizeForHtml(questionDetails.questionText)%></span>
            </div>
            <div class="panel-body">
                <p class="text-muted">Only the following persons can see your responses: </p>
                <ul class="text-muted">
                <%
                    if (question.getVisibilityMessage().isEmpty()) {
                %>
                        <li class="unordered">No-one but the feedback session creator can see your responses.</li>
                <%
                    }
                    for (String line : question.getVisibilityMessage()) {
                %>
                        <li class="unordered"><%=line%></li>
                <%
                    }
                %>
                </ul>
    <%
        int responseIndx = 0;
        List<FeedbackResponseAttributes> existingResponses = data.bundle.responseList;
        for (FeedbackResponseAttributes existingResponse : existingResponses) {
    %>
                <br />
                <div class="form-group">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right;\""%>>
                        <label for="input">To: </label>
                        <select class="participantSelect middlealign form-control" style="max-width:125px"
                            name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>
                            <%=data.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: data.getRecipientOptions(existingResponse.recipientEmail)) {
                                out.println(opt);
                            }
                        %>
                        </select>
                    </div>
                    <div <%=(question.isRecipientNameHidden()) ? "class=\"col-sm-12\"" : "class=\"col-sm-10\""%>>
                        <%=questionDetails.getQuestionWithExistingResponseSubmissionFormHtml(
                            data.isSessionOpenForSubmission, 
                            qnIndx, responseIndx, question.courseId, 
                            existingResponse.getResponseDetails())%>
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>" value="<%=existingResponse.getId()%>"/>
                    </div>
                </div>
    <%
            responseIndx++;
        }
        while (responseIndx < numOfResponseBoxes) {
    %>
                <br />
                <div class="form-group">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right\""%>>
                        <label for="input">To:</label>
                        <select class="participantSelect middlealign newResponse form-control" 
                            name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none;max-width:125px\"" : "style=\"max-width:125px\""%>
                            <%=data.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: data.getRecipientOptions(null)) {
                                out.println(opt);
                            }
                        %>
                        </select>
                    </div>
                    <div <%=(question.isRecipientNameHidden()) ? "class=\"col-sm-12\"" : "class=\"col-sm-10\""%>>
                    <%=questionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(
                            data.isSessionOpenForSubmission, 
                            qnIndx, responseIndx, question.courseId)%>
                    </div>
                </div>
    <%
            responseIndx++;
        }

        if (numOfResponseBoxes == 0) {
    %>
            <div class="form-group">
                <div class="col-sm-10">
                    <b>You are not assigned any recipients for this question.</b>
                </div>
            </div>
    <%    
        }
    %>
        </div>
        </div>