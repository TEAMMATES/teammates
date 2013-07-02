<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Config"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
	InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Feedback Session Results</title>
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
	<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
	<link rel="stylesheet" href="/stylesheets/instructorFeedback.css" type="text/css" media="screen">
	
	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/AnchorPosition.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Config.JSP_INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Feedback Results - Instructor</h1>
			</div>			
			<jsp:include page="<%=Config.JSP_INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
			<%
				for(Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries :
														data.bundle.getQuestionResponseMap().entrySet()) {
			%>
				
				<div class="backgroundBlock">
					<h2 class="color_white" style="padding-left: 20px;">Question <%=responseEntries.getKey().questionNumber%>: [<%=responseEntries.getKey().questionText.getValue()%>]</h2>
					<table class="dataTable">
						<tr>
							<th class="leftalign color_white bold">
								<input class="buttonSortNone" type="button" id="button_sortgiver" 
									onclick="toggleSort(this,1)">From:</th>
							<th class="leftalign color_white bold">
								<input class="buttonSortAscending" type="button" id="button_sortrecipient"
									onclick="toggleSort(this,2)">To:</th>
							<th class="leftalign color_white bold">
								<input class="buttonSortNone" type="button" id="button_sortanswer"
									onclick="toggleSort(this,3)">Feedback:</th>
						</tr>
						<%
							for(FeedbackResponseAttributes responseEntry: responseEntries.getValue()) {
						%>
						<tr>
							<td><%=data.bundle.emailNameTable.get(responseEntry.giverEmail)%></td>
							<td><%=data.bundle.emailNameTable.get(responseEntry.recipient)%></td>
							<td><%=responseEntry.answer.getValue()%></td>
						</tr>		
						<%
									}
								%>	
					</table>
				</div>
			<br>
			<%
				}
			%>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Config.JSP_FOOTER%>" />
	</div>
</body>
</html>