<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.ui.controller.FeedbackSubmissionEditPageData"%>
<%
    FeedbackSubmissionEditPageData data = (FeedbackSubmissionEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Submit Feedback</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
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
    <%
        if (!data.isPreview) {
    %>
            <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
    <%    
        } else { 
    %>
        <nav class="navbar navbar-default navbar-fixed-top">
            <h3 class="text-center">Previewing Session as Instructor <%=data.previewInstructor.name%> (<%=data.previewInstructor.email%>)</h3>
        </nav>
    <% 
        }
    %>

    <div id="frameBodyWrapper" class="container">
        <div id="topOfPage"></div>
        <h1>Submit Feedback</h1>
        <br>
        
        <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE%>" name="form_submit_response">
            
            <jsp:include page="<%=Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT%>" />
            
            <div class="bold align-center">
            <%
                if (data.bundle.questionResponseBundle.isEmpty()) {
            %>
                    There are no questions for you to answer here!
            <%
                } else if (data.isPreview || !data.isSessionOpenForSubmission) {
            %>
                    <input disabled="disabled" type="submit" class="btn btn-primary center-block" id="response_submit_button" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>" value="Submit Feedback"/>
            <%
                } else {
            %>
                    <input type="submit" class="btn btn-primary center-block" id="response_submit_button" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>" value="Submit Feedback"/>
            <%
                }
            %>
            </div>
            <br><br>    
        </form>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>
