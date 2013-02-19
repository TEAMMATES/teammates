<%@page import="teammates.common.Common"%>
<%@ page import="teammates.ui.controller.Helper" %>
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
					<li><a class='t_home' href="<%= helper.getStudentHomeLink() %>">Home</a></li>
					<li><a class='t_help' href="/studentHelp.html" target="_blank">Help</a></li>
					<li><a class='t_logout' href="<%= Common.JSP_LOGOUT %>">Logout</a><%if(helper.userId.length()>=23){ %>
					<span onmouseover="ddrivetip('<%=helper.userId %>')" onmouseout="hideddrivetip()">
							(<%=Helper.truncate(helper.userId,Common.NUMBER_TO_TRIGGER_HOVER_MESSAGE_IN_HEADER)%>)</span><%}else{%>
							(<%=Helper.truncate(helper.userId,Common.NUMBER_TO_TRIGGER_HOVER_MESSAGE_IN_HEADER)%>)<%} %>
					</li>
				</ul>
			</div>
		</div>