<%@page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.ui.controller.InstructorSearchPageData"%>
<%
    InstructorSearchPageData data = (InstructorSearchPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>TEAMMATES - Instructor</title>
<!-- Bootstrap core CSS -->
<link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- Bootstrap theme -->
<link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link rel="stylesheet" href="/stylesheets/teammatesCommon.css"
    type="text/css" media="screen">
<link href="/stylesheets/omniComment.css" rel="stylesheet">
<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
<script type="text/javascript" src="/js/instructor.js"></script>
<script src="/js/omniComment.js"></script>
<script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
<jsp:include page="../enableJS.jsp"></jsp:include>
<!-- Bootstrap core JavaScript ================================================== -->
<script src="/bootstrap/js/bootstrap.min.js"></script>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBody">
        <div id="frameBodyWrapper" class="container">
            <div>
                <h1>Search</h1>
            </div>
            <br>
            <div>
                <form method="get" action="<%=data.getInstructorSearchLink()%>" name="search_form">
                    <div class="input-group">
                        <span class="input-group-btn">
                            <button class="btn btn-default" type="submit" value="Search" id="buttonSearch">Search</button>
                        </span>
                        <input type="text" name="searchkey" value="<%=data.sanitizeForHtml(data.searchKey)%>" title="Search for comment" placeholder="Your search keyword" class="form-control" id="searchBox">
                    </div>
                    <input type="hidden" name="user" value="<%=data.account.googleId%>">
                </form>
            </div>
            <br><br>
            <% if(data.commentSearchResultBundle.giverCommentTable.keySet().size() != 0) { %>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <strong>Comments for students</strong>
                </div>
                <div class="panel-body">
                    <%
                        int commentIdx = 0;
                        for (String giverEmailPlusCourseId : data.commentSearchResultBundle.giverCommentTable.keySet()) {//comment loop starts
                    %>
                    <div class="panel panel-info student-record-comments">
                        <div class="panel-heading">
                            From <b><%=data.commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId)%></b>
                        </div>
                        <ul class="list-group comments">
                            <%
                                for (CommentAttributes comment : data.commentSearchResultBundle.giverCommentTable.get(giverEmailPlusCourseId)) {
                                    String recipientDisplay = data.commentSearchResultBundle.recipientTable.get(comment.getCommentId().toString());
                                    commentIdx++;
                            %>
                            <li class="list-group-item list-group-item-warning"
                                name="form_commentedit"
                                class="form_comment"
                                id="form_commentedit-<%=commentIdx%>">
                                <div id="commentBar-<%=commentIdx%>">
                                    <span class="text-muted">To <b><%=recipientDisplay%></b> on
                                        <%=TimeHelper.formatTime(comment.createdAt)%></span>
                                </div>
                                <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                            </li>
                            <% } %>
                        </ul>
                    </div>
                    <%
                        }//comment loop ends
                    %>
                </div>
            </div>
            <% } %>
            <% if(data.feedbackResponseCommentSearchResultBundle.numberOfCommentFound != 0) { %>
            <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Comments for responses</strong>
                    </div>
                <%
                    int fsIndx = 0;
                    for (String fsName : data.feedbackResponseCommentSearchResultBundle.questions.keySet()) {//FeedbackSession loop starts
                        List<FeedbackQuestionAttributes> questionList = data.feedbackResponseCommentSearchResultBundle.questions.get(fsName);
                        fsIndx++;
                %>
                <div class="panel-body">
                <div class="row <%=fsIndx == 1?"":"border-top-gray"%>">
                    <div class="col-md-2">
                        <strong>Session: <%=fsName%> (<%=data.feedbackResponseCommentSearchResultBundle.sessions.get(fsName).courseId%>)</strong>
                    </div>
                    <div class="col-md-10">
                        <%
                                int qnIndx = 0;
                                for (FeedbackQuestionAttributes question : questionList) {//FeedbackQuestion loop starts
                                    qnIndx++;
                        %>
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <b>Question <%=question.questionNumber%></b>:
                                <%=question.getQuestionDetails().questionText%>
                                <%=question.getQuestionDetails().getQuestionAdditionalInfoHtml(question.questionNumber, "")%>
                            </div>
                            <table class="table">
                                <tbody>
                                    <%
                                        int responseIndex = 0;
                                        List<FeedbackResponseAttributes> responseList = data.feedbackResponseCommentSearchResultBundle.responses.get(question.getId());
                                        for (FeedbackResponseAttributes responseEntry : responseList) {//FeedbackResponse loop starts
                                            responseIndex++;
                                            String giverName = data.feedbackResponseCommentSearchResultBundle.responseGiverTable.get(responseEntry.getId());
                                            String recipientName = data.feedbackResponseCommentSearchResultBundle.responseRecipientTable.get(responseEntry.getId());
                                    %>
                                    <tr>
                                        <td><b>From:</b> <%=data.getGiverName(responseEntry)%>
                                            <b>To:</b> <%=data.getRecipientName(responseEntry)%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Response:
                                        </strong><%=responseEntry.getResponseDetails().getAnswerHtml(question.getQuestionDetails())%>
                                        </td>
                                    </tr>
                                    <tr class="active">
                                        <td>Comment(s):
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <%
                                                List<FeedbackResponseCommentAttributes> frcList = data.feedbackResponseCommentSearchResultBundle.comments.get(responseEntry.getId());
                                            %>
                                            <ul
                                                class="list-group comments"
                                                id="responseCommentTable-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>"
                                                style="<%=frcList != null && frcList.size() > 0 ? "" : "display:none"%>">
                                                <%
                                                    int responseCommentIndex = 0;
                                                                for (FeedbackResponseCommentAttributes frc : frcList) {//FeedbackResponseComments loop starts
                                                                    responseCommentIndex++;
                                                                    String frCommentGiver = data.feedbackResponseCommentSearchResultBundle.commentGiverTable.get(frc.getId().toString());
                                                %>
                                                <li
                                                    class="list-group-item list-group-item-warning"
                                                    id="responseCommentRow-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>">
                                                    <div
                                                        id="commentBar-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>">
                                                        <span class="text-muted">From:
                                                            <b><%=frCommentGiver%></b>
                                                            on <%=TimeHelper.formatDate(frc.createdAt)%>
                                                        </span>
                                                    </div> <!-- frComment Content -->
                                                    <div
                                                        id="plainCommentText-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>"><%=InstructorSearchPageData.sanitizeForHtml(frc.commentText.getValue())%></div>
                                                </li>
                                                <%
                                                    }//FeedbackResponseComments loop ends
                                                %>
                                            </ul>
                                        </td>
                                    </tr>
                                    <%
                                        }//FeedbackResponse loop ends
                                    %>
                                </tbody>
                            </table>
                        </div>
                        <%
                            }//FeedbackQuestion loop ends
                        %>
                    </div>
                    </div>
                    </div>
                <%
                    }//FeedbackSession loop ends
                %>
                </div>
                <% } %>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>