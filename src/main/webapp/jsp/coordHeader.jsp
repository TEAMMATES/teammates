<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.ui.controller.Helper"%>
<% Helper helper = (Helper)request.getAttribute("helper"); %>
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px"
					src="/images/teammateslogo.jpg"
					width="150px">
			</div>			
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='t_home' href="<%= helper.getCoordHomeLink() %>">Home</a></li>
					<li><a class='t_courses' href="<%= helper.getCoordCourseLink() %>">Courses</a></li>
					<li><a class='t_evaluations' href="<%= helper.getCoordEvaluationLink() %>">Evaluations</a></li>
					<li><a class='t_help' href="/coordHelp.html" target="_blank">Help</a></li>
					<li><a class='t_logout' href="<%= Common.JSP_LOGOUT %>">Logout</a>
					 (<%= Helper.truncate(helper.userId.toLowerCase(),23) %>)</li>
				</ul>
			</div>
		</div>