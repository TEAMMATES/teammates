<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.*"%>
<%@ page import="teammates.Accounts"%>


<%
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/coordinator.jsp") );
		return ;
	}
%>

<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Coordinator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	
	<script language="JavaScript" src="js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="js/tooltip.js"></script>
	<script language="JavaScript" src="js/date.js"></script>
	<script language="JavaScript" src="js/CalendarPopup.js"></script>
	<script language="JavaScript" src="js/AnchorPosition.js"></script>
	<script language="JavaScript" src="js/helper.js"></script>
	<script language="JavaScript" src="js/common.js"></script>
	<script language="JavaScript" src="js/coordinator.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>

	<%
		// Check if user is allowed to view this page
		if (!accounts.isCoordinator()) {
	%>
	<p>
		You are not authorized to view this page.
		<br /><br />
		<a href="javascript:logout();">Logout and return to main page.</a>
	</p>
	<%
		} else {
	%>

	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px"
					src="images/teammateslogo.jpg"
					width="150px" />
			</div>
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='t_courses' href="javascript:displayCoursesTab();">Courses</a></li>
					<li><a class='t_evaluations' href="javascript:displayEvaluationsTab();">Evaluations</a></li>
					<li><a class='t_logout' href="javascript:logout();">Logout</a>
					 ( <% 	
					 out.println(accounts.getUser().getNickname());
					%>)</li>
				</ul>
			</div>
		</div>
	</div>
	
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation"></div>
			<div id="coordinatorCourseEnrollmentResults"></div>
			<div id="coordinatorCourseEnrollment"></div>
			<div id="coordinatorCourseInformation"></div>
			<div id="coordinatorEvaluationInformation"></div>
			<div id="coordinatorStudentInformation"></div>
			<div id="coordinatorCourseManagement"></div>
			<div id="coordinatorEvaluationManagement"></div>
			<br />
			<div id="statusMessage"></div>
			<div id="coordinatorStudentTable"></div>
			<div id="coordinatorCourseTable"></div>
			<div id="coordinatorEvaluationSummaryTable"></div>
			<div id="coordinatorEvaluationTable"></div>
			<div id="coordinatorEditEvaluationResults"></div>
			<div id="coordinatorEditEvaluationResultsStatusMessage"></div>
			<div id="coordinatorCourseEnrollmentButtons"></div>
			<div id="coordinatorEditEvaluationButtons"></div>
			<div id="coordinatorEditEvaluationResultsButtons"></div>
			<div id="coordinatorEditStudentButtons"></div>
		</div>
	</div>
	
	<div id="frameBottom">
		<div id="contentFooter">
			BEST VIEWED IN FIREFOX, CHROME, SAFARI AND INTERNET EXPLORER 8 AND ABOVE. FOR ENQUIRIES: <a class="footer"
				href="http://www.comp.nus.edu.sg/~teams/contact.html"
				target="_blank">Contact Us</a>
		</div>
	</div>
	<%
		}
	%>
</body>
</html>