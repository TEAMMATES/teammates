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
                                <strong>From: <%= responsesReceived.getKey() %></strong>
                            </div>
                            <div class="col-md-10">
                                <% int qnIndx = 1;
                                for (FeedbackResponseAttributes singleResponse : responsesReceived.getValue()) { %>
                                    <div class="panel panel-info">
                                        <div class="panel-heading">
                                            Question <%= feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber%>: <%=feedback.getQuestionText(singleResponse.feedbackQuestionId) %><%
                                            Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-" + giverIndex + "-session-" + fbIndex)); %>
                                        </div>
                                        <div class="panel-body">
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <span class="text-preserve-space"><%= feedback.getResponseAnswerHtml(singleResponse, question) %></span>
                                            <% List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                                            if (responseComments != null) { %>
                                                <ul class="list-group comment-list">
                                                    <% for (FeedbackResponseCommentAttributes comment : responseComments) { %>
                                                        <li class="list-group-item list-group-item-warning">
                                                            <span class="text-muted">
                                                                From: <%= comment.giverEmail %> [<%= comment.createdAt %>]
                                                            </span>
                                                            <div>
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
                            <div class="col-md-2"><strong>
                                To: <%= responsesGiven.getKey() %></strong>
                            </div>
                            <div class="col-md-10">
                                <% int qnIndx = 1;
                                for (FeedbackResponseAttributes singleResponse : responsesGiven.getValue()) { %>
                                    <div class="panel panel-info">
                                        <div class="panel-heading">
                                            Question <%= feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber %>: <%= feedback.getQuestionText(singleResponse.feedbackQuestionId) %><%
                                            Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "recipient-" + recipientIndex + "-session-" + fbIndex)); %>
                                        </div>
                                        <div class="panel-body">
                                            <%= singleResponse.getResponseDetails().getAnswerHtml(questionDetails) %>
                                            <% List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                                            if (responseComments != null) { %>
                                                <ul class="list-group comment-list">
                                                    <% for (FeedbackResponseCommentAttributes comment : responseComments) { %>
                                                        <li class="list-group-item list-group-item-warning">
                                                            <span class="text-muted">
                                                                From: <%= comment.giverEmail %> [<%= comment.createdAt %>]
                                                            </span>
                                                            <div>
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