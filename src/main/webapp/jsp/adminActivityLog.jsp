<%@ page import="java.util.List" %>
<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminActivityLogHelper"%>
<%@ page import="teammates.ui.controller.ActivityLogEntry" %>

<%
    AdminActivityLogHelper helper = (AdminActivityLogHelper)request.getAttribute("helper");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Administrator</title>
	<link rel=stylesheet href="/stylesheets/common.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/adminActivityLog.css" type="text/css" />

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	<script type="text/javascript" src="/js/administrator.js"></script>
	<script type="text/javascript" src="/js/adminActivityLog.js"></script>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
	<jsp:include page="<%=Common.JSP_ADMIN_HEADER%>" />
	</div>
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h2>Admin Activity Log</h2>
			</div>
		    <!-- Filter form to be added in later -->
			<%
			    List<ActivityLogEntry> appLogs = (List<ActivityLogEntry>) request.getAttribute("appLogs");
						  if (appLogs != null) {
			%>
			<table class="dataTable">
		        <tr>
		          <th width="10%">Date</th>
		          <th width="5%">Role</th>
		          <th width="20%">Person</th>
		          <th width="10%">Action / Page</th>
		          <th width="50%">Information</th>
		        </tr>
		    <%
		        if (appLogs.isEmpty()) {
		    %>
		        <tr>
		          <td colspan='5'><i>No application logs found</i></td>
		        </tr>
		    <%
		        } else {
		    		            for (ActivityLogEntry log : appLogs){
		    %>
		        <tr>
		          <td><%= log.getDateInfo() %></td>
		          <td><%= log.getRoleInfo() %></td>
		          <td><%= log.getPersonInfo() %></td>
		          <td><%= log.getActionInfo() %></td>
		          <td><%= log.getMessageInfo() %></td>
		        </tr>
		        <tr>
		          <td colspan="5"><%= log.getUrlInfo() %></td>
		        </tr>
		    <%
		          }
		        }
		    %>
		      </table>
		      <%
			      }
			  %>
			    
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<br>
			<div class="rightalign"><a href="#frameBodyWrapper">Back To Top</a></div>
			<br>
			<br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>