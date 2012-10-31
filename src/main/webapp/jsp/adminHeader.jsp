<%@ page import="teammates.common.Common"%>
<div id="frameTopWrapper">
	<div id="logo">
		<img alt="Teammates" height="47px"
			src="/images/teammateslogo.jpg"
			width="150px">
	</div>
	<div id="contentLinks">
		<ul id="navbar">
		    <li><a class='t_logout' href="<%= Common.PAGE_ADMIN_HOME %>">Create Coordinator</a></li>
			<li><a class='t_logout' href="<%= Common.PAGE_ADMIN_SEARCH %>">Search</a></li>
			<li><a class='t_logout' href="<%= Common.PAGE_ADMIN_ACTIVITY_LOG %>">Activity Log</a></li>
			<li><a class='t_logout' href="<%= Common.JSP_LOGOUT %>">Logout</a></li>
		</ul>
	</div>
</div>
