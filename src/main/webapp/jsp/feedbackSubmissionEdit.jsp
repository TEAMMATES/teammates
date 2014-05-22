<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.FeedbackSubmissionEditPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
    FeedbackSubmissionEditPageData data = (FeedbackSubmissionEditPageData)request.getAttribute("data");
%>    
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
    <div class="panel panel-default" id="course1">
            <div class="panel-heading">
                <div class="form-horizontal">
                    <div class="panel-heading">
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Course:</label>
                            <div class="col-sm-10">
                                <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.courseId)%></p>
                            </div>
                        </div> 
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Session:</label>
                            <div class="col-sm-10">
                                <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%></p>
                            </div>
                        </div>  
                        <div class="form-group">
                            <label class="col-sm-2 control-label">Duration:</label>
                            <div class="col-sm-10">
                                <p class="form-control-static">from:  <%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%>  
                                To: <%=TimeHelper.formatTime(data.bundle.feedbackSession.endTime)%></p>
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
            </div>
        </div>
    <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
<%
    int qnIndx = 1;
    List<FeedbackQuestionAttributes> questions = data.bundle.getSortedQuestions();
    for (FeedbackQuestionAttributes question : questions) {
        int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
        int maxResponsesPossible = data.bundle.recipientList.get(question.getId()).size();
        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();

        if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS ||
                numOfResponseBoxes > maxResponsesPossible) {
            numOfResponseBoxes = maxResponsesPossible;
        }
        if (numOfResponseBoxes == 0) {
            // Don't display question if no recipients.
            continue;
        }
%>
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>-<%=Integer.toString(qnIndx)%>" value="<%=question.questionType%>"/>
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>-<%=Integer.toString(qnIndx)%>" value="<%=question.getId()%>"/>
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL%>-<%=Integer.toString(qnIndx)%>" value="<%=numOfResponseBoxes%>"/>
        <table class="inputTable responseTable">
            <tr style="border-bottom: 3px dotted white;">
                <td class="bold" colspan="2" style="white-space:pre-wrap;">Question <%=qnIndx%>:<br/><%=sanitizeForHtml(questionDetails.questionText)%></td>
            </tr>
            <tr><td class="bold" colspan="2">Only the following persons can see your responses:</tr>
            <tr style="border-bottom: 3px dotted white;">
                <td colspan="2"
                    onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_RESPONSE_VISIBILITY_INFO%>')"
                    onmouseout="hideddrivetip()">
                    <ul>
                    <%
                        if (question.getVisibilityMessage().isEmpty()) {
                    %>
                            <li>No-one but the feedback session creator can see your responses.</li>
                    <%
                        }
                        for (String line : question.getVisibilityMessage()) {
                    %>
                            <li><%=line%></li>
                    <%
                        }
                    %>
                    </ul>
                </td>
            </tr>
            <tr><td class="bold centeralign" style="padding-top: 15px; padding-bottom: 0px" colspan="3">Your Response</td></tr>
        <%
            int responseIndx = 0;
            List<FeedbackResponseAttributes> existingResponses =
                    data.bundle.questionResponseBundle.get(question);
            for (FeedbackResponseAttributes existingResponse : existingResponses) {
        %>
                <tr>
                    <td class="middlealign nowrap" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : ""%>>
                        <span class="label bold">To: </span> 
                        <select class="participantSelect middlealign" 
                            name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>
                            <%=data.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: data.getRecipientOptionsForQuestion(question.getId(), existingResponse.recipientEmail)){
                                out.println(opt);
                            }
                        %>
                        </select>
                    </td>
                    <td class="responseText">
                        <%=questionDetails.getQuestionWithExistingResponseSubmissionFormHtml(
                            data.isSessionOpenForSubmission, 
                            qnIndx, responseIndx, question.courseId, 
                            existingResponse.getResponseDetails())%>
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>" value="<%=existingResponse.getId()%>"/>
                    </td>
                </tr>
        <%
            responseIndx++;
                        }
                        while (responseIndx < numOfResponseBoxes) {
        %>
                <tr>
                    <td class="middlealign nowrap" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : ""%>>
                        <span class="label bold">To: </span> 
                        <select class="participantSelect middlealign newResponse" 
                            name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>
                            <%=data.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: data.getRecipientOptionsForQuestion(question.getId(), null)) {
                                                            out.println(opt);
                                                        }
                        %>
                        </select>
                    </td>
                    <td class="responseText">
                    <%=questionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(
                            data.isSessionOpenForSubmission, 
                            qnIndx, responseIndx, question.courseId)%>
                    </td>
                </tr>
        <%
                responseIndx++;
            }
        %>
        </table>
        <br><br>
<%
        qnIndx++;
    }
%>