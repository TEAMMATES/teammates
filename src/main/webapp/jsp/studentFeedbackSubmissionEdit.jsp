<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.Url"%>
<%@ page import="teammates.ui.controller.FeedbackSubmissionEditPageData"%>
<%
    FeedbackSubmissionEditPageData data = (FeedbackSubmissionEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
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
        if (!data.isPreview && !data.isModeration) {
    %>
            <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
            <jsp:include page="<%=Const.ViewURIs.STUDENT_MOTD%>" />
    <%    
        } else if (data.isPreview) { 
            
    %>
        <nav class="navbar navbar-default navbar-fixed-top">
            <h3 class="text-center">Previewing Session as Student <%=data.studentToViewPageAs.name%> (<%=data.studentToViewPageAs.email%>)</h3>
        </nav>
    <%
    	} else if (data.isModeration) {
    %>
        <nav class="navbar navbar-default navbar-fixed-top">
            <h3 class="text-center">Moderating Responses for Student <%=data.studentToViewPageAs.name%> (<%=data.studentToViewPageAs.email%>)</h3>
        </nav>    
    <%
        }
    %>
    <div id="frameBody">
    <div id="frameBodyWrapper" class="container">
        <div id="topOfPage"></div>
        <h1>Submit Feedback</h1>
        <br />
        <%
        	if (data.account.googleId == null) { 
                String joinUrl = new Url(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                                .withRegistrationKey(request.getParameter(Const.ParamsNames.REGKEY))
                                .withStudentEmail(request.getParameter(Const.ParamsNames.STUDENT_EMAIL))
                                .withCourseId(request.getParameter(Const.ParamsNames.COURSE_ID))
                                .toString();
        %>
            <div id="registerMessage" class="alert alert-info">
                <%=String.format(Const.StatusMessages.UNREGISTERED_STUDENT, joinUrl)%>
            </div>
        <%
        	} 
            String submitAction = data.isModeration ? 
                                 Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE :
                                 Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
        %>
        <form method="post" name="form_student_submit_response"
              action="<%=submitAction%>" >
            <jsp:include page="<%=Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT%>" />
            
            <div class="bold align-center">
            <%
            	if (data.isModeration) {
            %>
                	<input name="moderatedstudent" value="<%=data.studentToViewPageAs.email%>" type="hidden">
            <%  }
            
                boolean isSubmittable = data.isSessionOpenForSubmission || data.isModeration;
                if (data.bundle.questionResponseBundle.isEmpty()) {
            %>
                    There are no questions for you to answer here!
            <%
                } else if (data.isPreview || !isSubmittable) {
            %>
                    <input disabled="disabled" type="submit" class="btn btn-primary" id="response_submit_button" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>" value="Submit Feedback" style="background: #66727A;"/>
            <%
                } else {
            %>
                    <input type="submit" class="btn btn-primary" id="response_submit_button" data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>" value="Submit Feedback"/>
            <%
                } 
            %>
            </div>
            <br><br>
        </form>
    </div></div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>
