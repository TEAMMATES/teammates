<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.*"%>
<%@ page import="teammates.Accounts"%>
<%@ page import="com.google.appengine.api.utils.SystemProperty"%>

<%
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
   
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/student.jsp") );
		return ;
	}
%>

<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Student</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />

	<script language="JavaScript" src="js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="js/common.js"></script>
	<script language="JavaScript" src="js/tooltip.js"></script>
	<script language="JavaScript" src="js/helper.js"></script>
	<script language="JavaScript" src="js/student.js"></script>
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px"
					src="images/teammateslogo.jpg"
					width="150px" />
			</div>
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='t_courses' href="javascript:displayCoursesTab();">Courses</a> </li>
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
			<div id="studentCourseManagement"></div>
			<div id="studentEvaluationInformation"></div>
			<div id="studentEvaluationResults"></div>
			<div id="studentCourseInformation"></div>
			<div id="studentPendingEvaluations"></div>
			<div id="studentEvaluationSubmissions"></div>
			<div id="statusMessage"></div>
			<div id="studentPastEvaluations"></div>
			<div id="studentEvaluationSubmissionButtons"></div>
			<div id="studentCourseTable"></div>
		</div>
	</div>

	<div id="frameBottom">
		<div id="contentFooter">
		<% 
		String version = SystemProperty.applicationVersion.get().split("\\.")[0].replace("-", ".");
		String build = SystemProperty.applicationVersion.get().split("\\.")[1];
		String footer = "[TEAMMATES Version " + version + " Build " + build + "] ";
		footer += "Best Viewed In Firefox, Chrome, Safari and Internet Explore 8+. For Enquires:";
		out.println(footer); 
		%>
		 <a class="footer"
			href="http://www.comp.nus.edu.sg/~teams/contact.html"
			target="_blank">Contact Us</a>
		</div>
	</div>
</body>
</html>