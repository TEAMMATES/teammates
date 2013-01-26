<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminActivityLogHelper"%>
<%@ page import="com.google.appengine.api.log.AppLogLine" %>
<%@ page import="com.google.appengine.api.log.LogService.LogLevel" %>
<% AdminActivityLogHelper helper = (AdminActivityLogHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Administrator</title>
	<link rel=stylesheet href="/stylesheets/common.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/adminActivityLog.css" type="text/css" />

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script language="JavaScript" src="/js/jquery-minified.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	<script language="JavaScript" src="/js/administrator.js"></script>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
	<jsp:include page="<%= Common.JSP_ADMIN_HEADER %>" />
	</div>
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h2>Admin Activity Log</h2>
			</div>
			  
			<%
			  List<AppLogLine> appLogs = (List<AppLogLine>) request.getAttribute("appLogs");
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
			        Calendar appCal = Calendar.getInstance();
	
		          for (AppLogLine log : appLogs) {
		  	        appCal.setTimeInMillis((log.getTimeUsec() / 1000) + 8*3600*1000);
					String logMessageTableRow = AdminActivityLogHelper.parseLogMessage(appCal.getTime().toString(), log.getLogMessage());

		    %>
		        <tr>
		          <%= logMessageTableRow %>
		        </tr>
		    <%
		          }
		        }
		    %>
		      </table>
		      <%
			      }
			    %>
			    </div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>