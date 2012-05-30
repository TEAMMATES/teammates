<%@ page import="teammates.*" %>
<%@ page import="teammates.jsp.*" %>
<% Accounts accounts = Accounts.inst(); %>
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px"
					src="/images/teammateslogo.jpg"
					width="150px" />
			</div>			
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='t_home' href="coordHome.jsp">Home</a></li>
					<li><a class='t_courses' href="coordCourse.jsp">Courses</a></li>
					<li><a class='t_teamForming' href="coordTFS.jsp">Team-Forming</a></li>
					<li><a class='t_evaluations' href="coordEval.jsp">Evaluations</a></li>
					<li><a class='t_help' href="http://www.comp.nus.edu.sg/~teams/coordinatorhelp.html" target="_blank">Help</a></li>
					<li><a class='t_logout' href="javascript:logout();">Logout</a>
					 (<% out.print(Helper.truncate(accounts.getUser().getNickname())); %>)</li>
				</ul>
			</div>
		</div>