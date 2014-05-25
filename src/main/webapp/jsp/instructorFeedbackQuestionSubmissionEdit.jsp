<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.ui.controller.FeedbackQuestionSubmissionEditPageData"%>
<%
    FeedbackQuestionSubmissionEditPageData data = (FeedbackQuestionSubmissionEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Submit Feedback Question</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
      <![endif]-->
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/tooltip.js"></script>
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <div id="frameBody" style="top:0px;">
        <div id="frameBodyWrapper">
            <div id="topOfPage"></div>
            <h1>Submit Feedback Question</h1>
            <br>
            
            <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE%>" name="form_submit_response">
                
                <jsp:include page="<%=Const.ViewURIs.FEEDBACK_QUESTION_SUBMISSION_EDIT%>" />
                
                <div class="bold centeralign">
                    <input type="submit" class="button" 
                        id="response_submit_button" 
                        onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>')" 
                        onmouseout="hideddrivetip()" 
                        value="Save Feedback"
                        <%=(data.bundle.feedbackSession.isOpened() || data.bundle.feedbackSession.isPrivateSession()) ? 
                                "" : "style=\"background: #66727A;\" disabled=\"disabled\""%>/>
                </div>
                <br><br>    
            </form>
        </div>
    </div>

</body>
</html>
