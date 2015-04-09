<%@ page import="java.util.List"%><%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.FeedbackSubmissionEditPageData"%>
<%@ page import="teammates.ui.controller.FeedbackQuestionSubmissionEditPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
    boolean isQuestion = false;
    FeedbackSubmissionEditPageData data = null;
    FeedbackQuestionSubmissionEditPageData questionData = null;
    if (request.getParameter("isQuestion").equals("true")) {
        isQuestion = true;
        questionData = (FeedbackQuestionSubmissionEditPageData)request.getAttribute("data");
    } else {
    	data = (FeedbackSubmissionEditPageData)request.getAttribute("data");
    }
%><%=isQuestion ? "" : "\n"%>    
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=isQuestion ? questionData.bundle.feedbackSession.feedbackSessionName : data.bundle.feedbackSession.feedbackSessionName%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=isQuestion ? questionData.bundle.feedbackSession.courseId : data.bundle.feedbackSession.courseId%>"/>
<%=isQuestion ? "" : "    "%><% if (isQuestion) { %>    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=questionData.account.googleId%>"><% } else { %><%
        if (data.account.googleId != null) {
    %>
        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
    <%
        } else {
    %>
        <input type="hidden" name="<%=Const.ParamsNames.REGKEY%>" value="<%=data.student.key%>">
        <input type="hidden" name="<%=Const.ParamsNames.STUDENT_EMAIL%>" value="<%=data.account.email%>">
    <%
        }
    %><% } %><%=isQuestion ? "\n    " : ""%>
    <div class="well well-plain" <%=isQuestion ? "" : "id=\"course1\""%>><%=isQuestion ? "" : "\n            <div class=\"panel-body\">"%>
        <%=isQuestion ? "" : "        "%><div class="form-horizontal"<%=isQuestion ? " role=\"form\"" : ""%>><%=isQuestion ? "" : "\n                    <div class=\"panel-heading\">"%>
            <%=isQuestion ? "" : "            "%><div class="form-group">
                <%=isQuestion ? "" : "            "%><label class="col-sm-2 control-label">Course ID:</label>
                <%=isQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=isQuestion ? "" : "            "%><p class="form-control-static"><%=sanitizeForHtml(isQuestion ? questionData.bundle.feedbackSession.courseId : data.bundle.feedbackSession.courseId)%></p>
                <%=isQuestion ? "" : "            "%></div>
            <%=isQuestion ? "" : "            "%></div><%=isQuestion ? "" : " "%>
            <%=isQuestion ? "" : "            "%><div class="form-group">
                <%=isQuestion ? "" : "            "%><label class="col-sm-2 control-label">Session<%=isQuestion ? " Name" : ""%>:</label>
                <%=isQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=isQuestion ? "" : "            "%><p class="form-control-static"><%=sanitizeForHtml(isQuestion ? questionData.bundle.feedbackSession.feedbackSessionName : data.bundle.feedbackSession.feedbackSessionName)%></p>
                <%=isQuestion ? "" : "            "%></div>
            <%=isQuestion ? "" : "            "%></div><%=isQuestion ? "" : "  "%>
            <%=isQuestion ? "" : "            "%><div class="form-group">
                <%=isQuestion ? "" : "            "%><label class="col-sm-2 control-label">Opening <%=isQuestion ? "Time" : "time"%>:</label>
                <%=isQuestion ? "" : "            "%><div class="col-sm-<%=isQuestion ? "2" : "10"%>">
                    <%=isQuestion ? "" : "            "%><p class="form-control-static"><%=isQuestion ? "\n                        " : ""%><%=TimeHelper.formatTime(isQuestion ? questionData.bundle.feedbackSession.startTime : data.bundle.feedbackSession.startTime)%><%=isQuestion ? "\n                    " : ""%></p>
                <%=isQuestion ? "" : "            "%></div>
            <%=isQuestion ? "" : "            "%></div>
            <%=isQuestion ? "" : "            "%><div class="form-group">
                <%=isQuestion ? "" : "            "%><label class="col-sm-2 control-label">Closing <%=isQuestion ? "Time" : "time"%>:</label>
                <%=isQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=isQuestion ? "" : "            "%><p class="form-control-static"><%=isQuestion ? "\n                        " : ""%><%=TimeHelper.formatTime(isQuestion ? questionData.bundle.feedbackSession.endTime : data.bundle.feedbackSession.endTime)%><%=isQuestion ? "\n                    " : ""%></p>
                <%=isQuestion ? "" : "            "%></div>
            <%=isQuestion ? "" : "            "%></div>
            <%=isQuestion ? "" : "            "%><div class="form-group">
                <%=isQuestion ? "" : "            "%><label class="col-sm-2 control-label">Instructions:</label>
                <%=isQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=isQuestion ? "" : "            "%><p class="form-control-static<%=isQuestion ? "" : " text-preserve-space"%>"><%=sanitizeForHtml(isQuestion ? questionData.bundle.feedbackSession.instructions.getValue() : data.bundle.feedbackSession.instructions.getValue())%></p><%=isQuestion ? "" : "\n                            </div>"%><%=isQuestion ? "" : "\n                        </div> "%>
                <%=isQuestion ? "" : "    "%></div><%=isQuestion ? "" : " "%>
            <%=isQuestion ? "" : "    "%></div>
        <%=isQuestion ? "" : "    "%></div>
    <%=isQuestion ? "" : "    "%></div><%=isQuestion ? "\n\n    <br>" : ""%>
    <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
<% if (isQuestion) { %>    <br>
    <form class="form-horizontal" role="form">
<%
    int qnIndx = 1;

    FeedbackQuestionAttributes question = questionData.bundle.question;
    int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
    int maxResponsesPossible = questionData.bundle.recipientList.size();
    FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

    if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS ||
            numOfResponseBoxes > maxResponsesPossible) {
        numOfResponseBoxes = maxResponsesPossible;
    }
%>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>-1" value="<%=question.questionType%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>-1" value="<%=question.getId()%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL%>-1" value="<%=numOfResponseBoxes%>"/>
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
        List<FeedbackResponseAttributes> existingResponses = questionData.bundle.responseList;
        for (FeedbackResponseAttributes existingResponse : existingResponses) {
    %>
                <br />
                <div class="form-group">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right;\""%>>
                        <label for="input">To: </label>
                        <select class="participantSelect middlealign form-control" style="max-width:125px"
                            name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none\"" : ""%>
                            <%=questionData.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: questionData.getRecipientOptions(existingResponse.recipientEmail)) {
                                out.println(opt);
                            }
                        %>
                        </select>
                    </div>
                    <div <%=(question.isRecipientNameHidden()) ? "class=\"col-sm-12\"" : "class=\"col-sm-10\""%>>
                        <%=questionDetails.getQuestionWithExistingResponseSubmissionFormHtml(
                        	questionData.isSessionOpenForSubmission,
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
                            <%=questionData.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: questionData.getRecipientOptions(null)) {
                                out.println(opt);
                            }
                        %>
                        </select>
                    </div>
                    <div <%=(question.isRecipientNameHidden()) ? "class=\"col-sm-12\"" : "class=\"col-sm-10\""%>>
                    <%=questionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(
                    		questionData.isSessionOpenForSubmission,
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
        </div><% } else { %>    <br />
    <%
        if (data.isModeration) {
    %>
            <div class="row"><span class="help-block align-center">
                <%=Const.FEEDBACK_SESSION_QUESTIONS_HIDDEN%>
            </span></div>
    <%
        }
    %>
<%
    int qnIndx = 1;
    List<FeedbackQuestionAttributes> questions = data.bundle.getSortedQuestions();
    for (FeedbackQuestionAttributes question : questions) {
        int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
        int maxResponsesPossible = data.bundle.recipientList.get(question.getId()).size();
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

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
        <div class="form-horizontal">
            <div class="panel panel-primary">
                <div class="panel-heading">Question <%=qnIndx%>:<br/>
                    <span class="text-preserve-space"><%=sanitizeForHtml(questionDetails.questionText)%></div></span>
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
            List<FeedbackResponseAttributes> existingResponses =
                    data.bundle.questionResponseBundle.get(question);
            for (FeedbackResponseAttributes existingResponse : existingResponses) {
        %>
                <br />
                <div class="form-group margin-0">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right;\""%>>
                        <label for="input">To: </label>
                        <select class="participantSelect middlealign form-control" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none;max-width:125px\"" : "style=\"max-width:125px\""%>
                            <%=data.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: data.getRecipientOptionsForQuestion(question.getId(), existingResponse.recipientEmail)){
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
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>" value="<%=sanitizeForHtml(existingResponse.getId())%>"/>
                    </div>
                </div>
        <%
            responseIndx++;
                        }
                        while (responseIndx < numOfResponseBoxes) {
        %>
                <br />
                <div class="form-group margin-0">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right\""%>>
                        <label for="input">To:</label>
                        <select class="participantSelect middlealign newResponse form-control" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none;max-width:125px\"" : "style=\"max-width:125px\""%>
                            <%=data.isSessionOpenForSubmission ? "" : "disabled=\"disabled\""%>>
                        <%
                            for(String opt: data.getRecipientOptionsForQuestion(question.getId(), null)) {
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
        %></div></div>
        </div>
        <br><br>
<%
        qnIndx++;
    }
%><% } %>