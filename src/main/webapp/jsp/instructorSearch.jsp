<%@page import="teammates.common.datatransfer.CommentSendingState"%>
<%@page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@page import="teammates.common.datatransfer.StudentAttributes"%>
<%@page import="teammates.common.datatransfer.CommentStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.SessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page
    import="teammates.ui.controller.InstructorEvalSubmissionPageData"%>
<%@ page import="teammates.ui.controller.InstructorSearchPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
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
        <br>
        <form action="/page/instructorSearchPage" method="post" class="form form-horizontal">
            <div class="form-group">
                <label class="col-sm-1 control-label">Key:</label>
                <div class="col-sm-11">
                    <input class="form-control" name="searchkey" id="searchkey" value="">
                </div>
            </div>
            <br>
            <div>
                <input type="submit" class="btn btn-primary" id="button_submit" name="submit" value="Submit" >
            </div>
            <br>
            <br>
            <input type="hidden" name="user" value="<%=data.account.googleId%>">
        </form>
        <br>
        <%
        for(CommentAttributes comment:data.commentSearchResultBundle.comments){
            out.write(comment.toString() + "<br><br><br>");
        }
        %>
        <%=data.commentSearchResultBundle.cursor != null?data.commentSearchResultBundle.cursor.toWebSafeString():""%>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>