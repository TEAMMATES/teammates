<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
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
            <form method="post" action="<%=data.getInstructorSearchLink()%>" name="search_form">
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
        <% if(data.commentSearchResultBundle.comments.size() != 0) { %>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <strong>Comments for students</strong>
            </div>
            <div class="panel-body">
                <%
                    int commentIdx = 0;
                    int studentIdx = 0;
                    for (CommentAttributes comment : data.commentSearchResultBundle.comments) {//comment loop starts
                        studentIdx++;
                %>
                <%
                    String recipientDisplay = data.commentSearchResultBundle.recipientTable.get(comment.getCommentId().toString());
                %>
                <div class="panel panel-info student-record-comments">
                    <div class="panel-heading">
                        To <b><%=recipientDisplay%></b>
                    </div>
                    <ul class="list-group comments">
                        <%
                            commentIdx++;
                        %>
                        <li class="list-group-item list-group-item-warning"
                            name="form_commentedit"
                            class="form_comment"
                            id="form_commentedit-<%=commentIdx%>">
                            <div id="commentBar-<%=commentIdx%>">
                                <span class="text-muted">From <b><%=data.commentSearchResultBundle.giverTable.get(comment.getCommentId().toString())%></b> on
                                    <%=TimeHelper.formatTime(comment.createdAt)%></span>
                            </div>
                            <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                        </li>
                    </ul>
                </div>
                <%
                    }//comment loop ends
                %>
            </div>
        </div>
        <% } %>
        <br>
        <% if(data.feedbackResponseCommentSearchResultBundle.comments.size() != 0) { %>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <strong>Comments for responses</strong>
            </div>
            <div class="panel-body">
                <%
                    int commentIdx = 0;
                    int studentIdx = 0;
                    for (FeedbackResponseCommentAttributes comment : data.feedbackResponseCommentSearchResultBundle.comments) {//response comment loop starts
                        studentIdx++;
                %>
                <div class="panel panel-info student-record-comments">
                    <div class="panel-heading">
                        From <b><%=data.feedbackResponseCommentSearchResultBundle.giverTable.get(comment.getId().toString())%></b>
                    </div>
                    <ul class="list-group comments">
                        <%
                            commentIdx++;
                        %>
                        <li class="list-group-item list-group-item-warning"
                            name="form_commentedit"
                            class="form_comment"
                            id="form_commentedit-<%=commentIdx%>">
                            <div id="commentBar-<%=commentIdx%>">
                                <span class="text-muted">on
                                    <%=TimeHelper.formatTime(comment.createdAt)%></span>
                            </div>
                            <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                        </li>
                    </ul>
                </div>
                <%
                    }//response comment loop ends
                %>
            </div>
        </div>
        <% } %>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>