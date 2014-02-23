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
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/studentFeedback.css" type="text/css" media="screen">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	<script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
	<%
		if (!data.isPreview) {
	%>
			<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	<%	
		} else { 
	%>
			<div id="frameTopWrapper">
				<h1 class="color_white centeralign">Previewing Session as Instructor <%=data.previewInstructor.name%> (<%=data.previewInstructor.email%>)</h1>
			</div>
	<% 
		}
	%>
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Submit Feedback</h1>
			</div>
			
			<form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE%>" name="form_submit_response">
				
				<jsp:include page="<%=Const.ViewURIs.FEEDBACK_SUBMISSION_EDIT%>" />
				
				<div class="bold centeralign">
				<%
					if (data.bundle.questionResponseBundle.isEmpty()) {
				%>
						There are no questions for you to answer here!
				<%
					} else if (data.isPreview) {
				%>
						<input disabled="disabled" type="submit" class="button" id="response_submit_button" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>')" onmouseout="hideddrivetip()" value="Save Feedback"/>
				<%
					} else if (data.isSessionOpenForSubmission) {
				%>
						<input type="submit" class="button" id="response_submit_button" onmouseover="ddrivetip('<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>')" onmouseout="hideddrivetip()" value="Save Feedback"/>
				<%
					} else {
				%>
						<%=Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN%>
				<%
					}
				%>
				</div>
				<br><br>	
			</form>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>
