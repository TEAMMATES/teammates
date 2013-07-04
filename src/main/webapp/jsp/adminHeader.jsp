<%@ page import="teammates.common.util.Const"%>
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
		    <li><a class='t_logout' href="<%=Const.ActionURIs.ADMIN_HOME%>">Create Instructor</a></li>
		    <li><a class='t_logout' href="<%=Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT%>">Account Management</a></li>
			<li><a class='t_logout' href="<%=Const.ActionURIs.ADMIN_SEARCH%>">Search</a></li>
			<li><a class='t_logout' href="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG%>">Activity Log</a></li>
			<li><a class='t_logout' href="<%=Const.ViewURIs.LOGOUT%>">Logout</a></li>
		</ul>
	</div>
</div>
