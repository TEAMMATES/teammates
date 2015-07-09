<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.SessionResultsBundle"%>
<%@ page import="teammates.ui.controller.InstructorStudentRecordsAjaxPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml" %>
<%
    InstructorStudentRecordsAjaxPageData data = (InstructorStudentRecordsAjaxPageData)request.getAttribute("data");
%>
<% // Ajax content to be loaded
int sessionIndex = -1;
int fbIndex = -1;
for (SessionResultsBundle sessionResult: data.results) {
    sessionIndex++;
    if (sessionResult instanceof FeedbackSessionResultsBundle) {
        FeedbackSessionResultsBundle feedback = (FeedbackSessionResultsBundle) sessionResult;
        fbIndex++;
        String giverName = feedback.appendTeamNameToName(InstructorStudentRecordsAjaxPageData.sanitizeForHtml(data.student.name), data.student.team);
        String recipientName = giverName;
        Map<String, List<FeedbackResponseAttributes>> received = feedback.getResponsesSortedByRecipient().get(recipientName);
        Map<String, List<FeedbackResponseAttributes>> given = feedback.getResponsesSortedByGiver().get(giverName);
        if (received != null) { %>
            <br>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    To: <strong><%=recipientName%></strong>
                </div>
                <div class="panel-body">
                    <% int giverIndex = 0;
                    for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesReceived : received.entrySet()) {
                        giverIndex++; %>
                        <div class="row <%= giverIndex == 1 ? "" : "border-top-gray" %>">
                            <div class="col-md-2">
                                From: <strong><%= responsesReceived.getKey() %></strong>
                            </div>
                            <div class="col-md-10">
                                <% int qnIndx = 1;
                                for (FeedbackResponseAttributes singleResponse : responsesReceived.getValue()) { %>
                                    <div class="panel panel-info">
                                        <div class="panel-heading">
                                            Question <%= feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber%>: <span class="text-preserve-space"><%=feedback.getQuestionText(singleResponse.feedbackQuestionId) %><%
                                            Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-" + giverIndex + "-session-" + fbIndex)); %></span>
                                        </div>
                                        <div class="panel-body">
                                            <div style="clear:both; overflow: hidden">
                                                <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                <div class="pull-left text-preserve-space"><%= feedback.getResponseAnswerHtml(singleResponse, question) %></div>
                                            </div>
                                            <% List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                                            if (responseComments != null && responseComments.size() > 0) { %>
                                                <ul class="list-group" id="responseCommentTable-<%= fbIndex %>-<%= giverIndex %>-<%= qnIndx %>-GRQ" style="margin-top:15px;">
                                                    <% for (FeedbackResponseCommentAttributes comment : responseComments) { %>
                                                        <li class="list-group-item list-group-item-warning" id="responseCommentRow-<%= comment.getId() %>">
                                                            <div id="commentBar-<%= comment.getId() %>">
                                                                <span class="text-muted">
                                                                    From: <%= comment.giverEmail %> [<%= comment.createdAt %>] <%=comment.getEditedAtText(comment.giverEmail.equals("Anonymous"))%>
                                                                </span>
                                                            </div>
                                                            <div id="plainCommentText-<%= comment.getId() %>" style="margin-left: 15px;">
                                                                <%= comment.commentText.getValue() %>
                                                            </div>
                                                        </li>
                                                    <% } %>
                                                </ul>
                                            <% } %>
                                        </div>
                                    </div>
                                <% }
                                qnIndx++;
                                if (responsesReceived.getValue().isEmpty()) { %>
                                    <div class="col-sm-12" style="color: red;">
                                        No feedback from this user.
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    <br>
                    <% } %>
                </div>
            </div>
        <% } else { %>
            <br>
            <div class="panel panel-info">
                <div class="panel-body">
                    No feedback for <%= InstructorStudentRecordsAjaxPageData.sanitizeForHtml(data.student.name) %> found
                </div>
            </div>
        <% }
        if (given != null) { %>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    From: <strong><%= giverName %></strong>
                </div>
                <div class="panel-body">
                    <% int recipientIndex = 0;
                    for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesGiven : given.entrySet()) {
                        recipientIndex++; %>
                        <div class="row <%= recipientIndex == 1 ? "" : "border-top-gray" %>">
                            <div class="col-md-2">
                                To: <strong><%= responsesGiven.getKey() %></strong>
                            </div>
                            <div class="col-md-10">
                                <% int qnIndx = 1;
                                for (FeedbackResponseAttributes singleResponse : responsesGiven.getValue()) { %>
                                    <div class="panel panel-info">
                                        <div class="panel-heading">
                                            Question <%= feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber %>: <span class="text-preserve-space"><%= feedback.getQuestionText(singleResponse.feedbackQuestionId) %><%
                                            Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "recipient-" + recipientIndex + "-session-" + fbIndex)); %></span>
                                        </div>
                                        <div class="panel-body">
                                            <div style="clear:both; overflow: hidden">
                                                <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                <div class="pull-left text-preserve-space"><%= singleResponse.getResponseDetails().getAnswerHtml(questionDetails) %></div>
                                            </div>
                                            <% List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                                            if (responseComments != null && responseComments.size() > 0) { %>
                                                <ul class="list-group" id="responseCommentTable-<%= fbIndex %>-<%= recipientIndex %>-<%= qnIndx %>-RGQ" style="margin-top:15px;">
                                                    <% for (FeedbackResponseCommentAttributes comment : responseComments) { %>
                                                        <li class="list-group-item list-group-item-warning" id="responseCommentRow-<%= comment.getId() %>">
                                                            <div id="commentBar-<%= comment.getId() %>">
                                                                <span class="text-muted">
                                                                    From: <%= comment.giverEmail %> [<%= comment.createdAt %>] <%= comment.getEditedAtText(comment.giverEmail.equals("Anonymous")) %>
                                                                </span>
                                                            </div>
                                                            <div id="plainCommentText-<%= comment.getId() %>" style="margin-left: 15px;">
                                                                <%= comment.commentText.getValue() %>
                                                            </div>
                                                        </li>
                                                    <% } %>
                                                </ul>
                                            <% } %>
                                        </div>
                                    </div>
                                <% qnIndx++;
                                }
                                if (responsesGiven.getValue().isEmpty()) { %>
                                    <div class="col-sm-12" style="color: red;">
                                        No feedback from this user.
                                    </div>
                                <% } %>
                            </div>
                        </div>
                        <br>
                    <% } %>
                </div>
            </div>
        <% } else{ %>
            <br>
            <div class="panel panel-info">
                <div class="panel-body">
                    No feedback by <%= InstructorStudentRecordsAjaxPageData.sanitizeForHtml(data.student.name) %> found
                </div>
            </div>
        <% }
    }
} %> 