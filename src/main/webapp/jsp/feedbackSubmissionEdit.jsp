<%@ page import="java.util.List"%><%@ page import="java.util.ArrayList"%>
<%@ page import="teammates.common.util.TimeHelper"%><%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.FeedbackSubmissionEditPageData"%>
<%@ page import="teammates.ui.controller.FeedbackQuestionSubmissionEditPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
    boolean showSingleQuestion = false;
    FeedbackSubmissionEditPageData data = null;
    FeedbackQuestionSubmissionEditPageData questionData = null;
    if (request.getParameter("showSingleQuestion").equals("true")) {
        showSingleQuestion = true;
        questionData = (FeedbackQuestionSubmissionEditPageData)request.getAttribute("data");
    } else {
    	data = (FeedbackSubmissionEditPageData)request.getAttribute("data");
    }
%><%=showSingleQuestion ? "" : "\n"%>    
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=showSingleQuestion ? questionData.bundle.feedbackSession.feedbackSessionName : data.bundle.feedbackSession.feedbackSessionName%>"/>
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=showSingleQuestion ? questionData.bundle.feedbackSession.courseId : data.bundle.feedbackSession.courseId%>"/>
<%=showSingleQuestion ? "" : "    "%><% if (showSingleQuestion) { %>    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=questionData.account.googleId%>"><% } else { %><%
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
    %><% } %><%=showSingleQuestion ? "\n    " : ""%>
    <div class="well well-plain" <%=showSingleQuestion ? "" : "id=\"course1\""%>><%=showSingleQuestion ? "" : "\n            <div class=\"panel-body\">"%>
        <%=showSingleQuestion ? "" : "        "%><div class="form-horizontal"<%=showSingleQuestion ? " role=\"form\"" : ""%>><%=showSingleQuestion ? "" : "\n                    <div class=\"panel-heading\">"%>
            <%=showSingleQuestion ? "" : "            "%><div class="form-group">
                <%=showSingleQuestion ? "" : "            "%><label class="col-sm-2 control-label">Course ID:</label>
                <%=showSingleQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=showSingleQuestion ? "" : "            "%><p class="form-control-static"><%=sanitizeForHtml(showSingleQuestion ? questionData.bundle.feedbackSession.courseId : data.bundle.feedbackSession.courseId)%></p>
                <%=showSingleQuestion ? "" : "            "%></div>
            <%=showSingleQuestion ? "" : "            "%></div><%=showSingleQuestion ? "" : " "%>
            <%=showSingleQuestion ? "" : "            "%><div class="form-group">
                <%=showSingleQuestion ? "" : "            "%><label class="col-sm-2 control-label">Session<%=showSingleQuestion ? " Name" : ""%>:</label>
                <%=showSingleQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=showSingleQuestion ? "" : "            "%><p class="form-control-static"><%=sanitizeForHtml(showSingleQuestion ? questionData.bundle.feedbackSession.feedbackSessionName : data.bundle.feedbackSession.feedbackSessionName)%></p>
                <%=showSingleQuestion ? "" : "            "%></div>
            <%=showSingleQuestion ? "" : "            "%></div><%=showSingleQuestion ? "" : "  "%>
            <%=showSingleQuestion ? "" : "            "%><div class="form-group">
                <%=showSingleQuestion ? "" : "            "%><label class="col-sm-2 control-label">Opening <%=showSingleQuestion ? "Time" : "time"%>:</label>
                <%=showSingleQuestion ? "" : "            "%><div class="col-sm-<%=showSingleQuestion ? "2" : "10"%>">
                    <%=showSingleQuestion ? "" : "            "%><p class="form-control-static"><%=showSingleQuestion ? "\n                        " : ""%><%=TimeHelper.formatTime(showSingleQuestion ? questionData.bundle.feedbackSession.startTime : data.bundle.feedbackSession.startTime)%><%=showSingleQuestion ? "\n                    " : ""%></p>
                <%=showSingleQuestion ? "" : "            "%></div>
            <%=showSingleQuestion ? "" : "            "%></div>
            <%=showSingleQuestion ? "" : "            "%><div class="form-group">
                <%=showSingleQuestion ? "" : "            "%><label class="col-sm-2 control-label">Closing <%=showSingleQuestion ? "Time" : "time"%>:</label>
                <%=showSingleQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=showSingleQuestion ? "" : "            "%><p class="form-control-static"><%=showSingleQuestion ? "\n                        " : ""%><%=TimeHelper.formatTime(showSingleQuestion ? questionData.bundle.feedbackSession.endTime : data.bundle.feedbackSession.endTime)%><%=showSingleQuestion ? "\n                    " : ""%></p>
                <%=showSingleQuestion ? "" : "            "%></div>
            <%=showSingleQuestion ? "" : "            "%></div>
            <%=showSingleQuestion ? "" : "            "%><div class="form-group">
                <%=showSingleQuestion ? "" : "            "%><label class="col-sm-2 control-label">Instructions:</label>
                <%=showSingleQuestion ? "" : "            "%><div class="col-sm-10">
                    <%=showSingleQuestion ? "" : "            "%><p class="form-control-static<%=showSingleQuestion ? "" : " text-preserve-space"%>"><%=sanitizeForHtml(showSingleQuestion ? questionData.bundle.feedbackSession.instructions.getValue() : data.bundle.feedbackSession.instructions.getValue())%></p><%=showSingleQuestion ? "" : "\n                            </div>"%><%=showSingleQuestion ? "" : "\n                        </div> "%>
                <%=showSingleQuestion ? "" : "    "%></div><%=showSingleQuestion ? "" : " "%>
            <%=showSingleQuestion ? "" : "    "%></div>
        <%=showSingleQuestion ? "" : "    "%></div>
    <%=showSingleQuestion ? "" : "    "%></div><%=showSingleQuestion ? "\n\n    <br>" : ""%>
    <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
    <%=showSingleQuestion ? "<br>\n    <form class=\"form-horizontal\" role=\"form\">" : "<br />\n    "%>
<%
int qnIndx = 1;
List<FeedbackQuestionAttributes> questions;
if (showSingleQuestion) {
	questions = new ArrayList<FeedbackQuestionAttributes>();
    questions.add(questionData.bundle.question);
} else {
	questions = data.bundle.getSortedQuestions();
}
for (FeedbackQuestionAttributes question : questions) {
    int numOfResponseBoxes = question.numberOfEntitiesToGiveFeedbackTo;
    FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
    int maxResponsesPossible;
    if (!showSingleQuestion) {
        maxResponsesPossible = data.bundle.recipientList.get(question.getId()).size();
    } else {
    	maxResponsesPossible = questionData.bundle.recipientList.size();
    }
    if (numOfResponseBoxes == Const.MAX_POSSIBLE_RECIPIENTS ||
            numOfResponseBoxes > maxResponsesPossible) {
        numOfResponseBoxes = maxResponsesPossible;
    }
    if (numOfResponseBoxes == 0) {
        // Don't display question if no recipients.
        continue;
    }
%>
    <%=showSingleQuestion ? "" : "    "%><input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_TYPE%>-<%=Integer.toString(qnIndx)%>" value="<%=question.questionType%>"/>
    <%=showSingleQuestion ? "" : "    "%><input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>-<%=Integer.toString(qnIndx)%>" value="<%=question.getId()%>"/>
    <%=showSingleQuestion ? "" : "    "%><input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL%>-<%=Integer.toString(qnIndx)%>" value="<%=numOfResponseBoxes%>"/>
    <%=showSingleQuestion ? "" : "    "%><div class="form-horizontal">
        <%=showSingleQuestion ? "" : "    "%><div class="panel panel-primary">
            <%=showSingleQuestion ? "" : "    "%><div class="panel-heading">Question <%=showSingleQuestion ? questionData.bundle.question.questionNumber : qnIndx%>:<br/>
                <%=showSingleQuestion ? "" : "    "%><span class="text-preserve-space"><%=sanitizeForHtml(questionDetails.questionText)%><%=showSingleQuestion ? "" : "</div>"%></span><%=showSingleQuestion ? "\n            </div>" : ""%>
            <%=showSingleQuestion ? "" : "    "%><div class="panel-body">
                <%=showSingleQuestion ? "" : "    "%><p class="text-muted">Only the following persons can see your responses: </p>
                <%=showSingleQuestion ? "" : "    "%><ul class="text-muted">
                <%=showSingleQuestion ? "" : "    "%><%
                        if (question.getVisibilityMessage().isEmpty()) {
                    %>
                        <%=showSingleQuestion ? "" : "    "%><li class="unordered">No-one but the feedback session creator can see your responses.</li>
                    <%
                        }
                        for (String line : question.getVisibilityMessage()) {
                    %>
                        <%=showSingleQuestion ? "" : "    "%><li class="unordered"><%=line%></li>
                <%=showSingleQuestion ? "" : "    "%><%
                        }
                    %>
                <%=showSingleQuestion ? "" : "    "%></ul>
    <%=showSingleQuestion ? "" : "    "%><%
            int responseIndx = 0;
            List<FeedbackResponseAttributes> existingResponses;
            if (showSingleQuestion) {
            	existingResponses = questionData.bundle.responseList;
            } else {
            	existingResponses = data.bundle.questionResponseBundle.get(question);
            }    
            for (FeedbackResponseAttributes existingResponse : existingResponses) {
        %>
                <br />
                <div class="form-group margin-0">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right;\""%>>
                        <label for="input">To: </label>
                        <select class="participantSelect middlealign form-control" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none;max-width:125px\"" : "style=\"max-width:125px\""%>
                            <%=((showSingleQuestion && questionData.isSessionOpenForSubmission) || (!showSingleQuestion && data.isSessionOpenForSubmission)) ? "" : "disabled=\"disabled\""%>>
                        <%
                            if (showSingleQuestion) {
                            	for(String opt: questionData.getRecipientOptions(existingResponse.recipientEmail)) {
                                    out.println(opt);
                                }
                            } else {
                            	for(String opt: data.getRecipientOptionsForQuestion(question.getId(), existingResponse.recipientEmail)){
                                    out.println(opt);
                                }
                            }
                        %>
                        </select>
                    </div>
                    <div <%=(question.isRecipientNameHidden()) ? "class=\"col-sm-12\"" : "class=\"col-sm-10\""%>>
                        <%=questionDetails.getQuestionWithExistingResponseSubmissionFormHtml(
                        	showSingleQuestion ? questionData.isSessionOpenForSubmission : data.isSessionOpenForSubmission,
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
                <div class="form-group<%=showSingleQuestion ? "" : " margin-0"%>">
                    <div class="col-sm-2 form-inline" <%=(question.isRecipientNameHidden()) ? "style=\"display:none\"" : "style=\"text-align:right\""%>>
                        <label for="input">To:</label>
                        <select class="participantSelect middlealign newResponse form-control" <%=showSingleQuestion ? "\n                            " : ""%>name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT%>-<%=Integer.toString(qnIndx)%>-<%=Integer.toString(responseIndx)%>"
                            <%=(numOfResponseBoxes == maxResponsesPossible) ? "style=\"display:none;max-width:125px\"" : "style=\"max-width:125px\""%>
                            <%=((showSingleQuestion && questionData.isSessionOpenForSubmission) || (!showSingleQuestion && data.isSessionOpenForSubmission)) ? "" : "disabled=\"disabled\""%>>
                        <%
                            if (showSingleQuestion) {
                            	for(String opt: questionData.getRecipientOptions(null)) {
                                    out.println(opt);
                                }
                            } else {
                            	for(String opt: data.getRecipientOptionsForQuestion(question.getId(), null)) {
                                    out.println(opt);
                                }
                            }
                        %>
                        </select>
                    </div>
                    <div <%=(question.isRecipientNameHidden()) ? "class=\"col-sm-12\"" : "class=\"col-sm-10\""%>>
                    <%=questionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(
                    		showSingleQuestion ? questionData.isSessionOpenForSubmission : data.isSessionOpenForSubmission,
                            qnIndx, responseIndx, question.courseId)%>
                    </div>
                </div>
    <%=showSingleQuestion ? "" : "    "%><%
                responseIndx++;
            }
        %><%=showSingleQuestion ? "\n        " : "</div>"%></div>
        </div><%=showSingleQuestion ? "" : "\n        <br><br>\n"%><%
        qnIndx++;
    }
%>