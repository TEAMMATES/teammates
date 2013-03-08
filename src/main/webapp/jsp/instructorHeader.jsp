<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.ui.controller.Helper"%>
<% Helper helper = (Helper)request.getAttribute("helper"); %>
		<div id="frameTopWrapper">
			<div id="logo">
				<a href="/index.html">
				<img alt="Teammates" height="47px"
					src="/images/teammateslogo.jpg"
					width="150px">
				</a>
			</div>			
			<div id="contentLinks">
				<ul id="navbar">
					<li ><a class='nav home' data-link="instructorHome" href="<%= helper.getInstructorHomeLink() %>">Home</a></li>
					<li><a class='nav courses' data-link="instructorCourse" href="<%= helper.getInstructorCourseLink() %>">Courses</a></li>
					<li><a class='nav evaluations' data-link="instructorEval" href="<%= helper.getInstructorEvaluationLink() %>">Evaluations</a></li>
					<li><a class='nav help' href="/instructorHelp.html" target="_blank">Help</a></li>
					<li><a class='nav logout' href="<%= Common.JSP_LOGOUT %>">Logout</a>
					
					 <%if(helper.userId.length()>=Common.USER_ID_MAX_DISPLAY_LENGTH){ %>
					<span onmouseover="ddrivetip('<%=helper.userId %>')" onmouseout="hideddrivetip()">
							(<%=Helper.truncate(helper.userId,Common.USER_ID_MAX_DISPLAY_LENGTH)%>)</span><%}else{%>
							(<%=Helper.truncate(helper.userId,Common.USER_ID_MAX_DISPLAY_LENGTH)%>)<%} %>
					</li>
				</ul>
			</div>
			<div style="clear: both;"></div>
		</div>
