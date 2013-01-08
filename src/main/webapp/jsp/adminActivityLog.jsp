<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminHomeHelper"%>
<%@ page import="com.google.appengine.api.log.AppLogLine" %>
<%@ page import="com.google.appengine.api.log.LogService.LogLevel" %>
<% AdminHomeHelper helper = (AdminHomeHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Administrator</title>
	<link rel=stylesheet href="/stylesheets/common.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/adminActivityLog.css" type="text/css" />

	<script language="JavaScript" src="/js/jquery-minified.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
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
		          <th>Date</th>
		          <th>Level</th>
		          <th>Activity</th>
		          <th>Role</th>
		          <th>Name</th>
		          <th>ID</th>
		          <th>Email</th>
		          <th>Response</th>
		          <th>Request Parameter</th>
		        </tr>
		    <%
		        if (appLogs.isEmpty()) {
		    %>
		        <tr>
		          <td colspan='9'><i>No application logs found</i></td>
		        </tr>
		    <%
		        } else {
			        Calendar appCal = Calendar.getInstance();
	
		          for (AppLogLine log : appLogs) {
		  	        appCal.setTimeInMillis((log.getTimeUsec() / 1000) + 8*3600*1000);
					String[] tokens = log.getLogMessage().split("\\|");

		    %>
		        <tr>
		          <td>
		            <%=appCal.getTime().toString()%>
		          </td>
		          <% 
		          	if (log.getLogLevel().compareTo(LogLevel.WARN) == 0) {
		           %>
		            <td style="color:blue">
		          <% 
		          	} else if (log.getLogLevel().compareTo(LogLevel.INFO) == 0){ 
		          %>
		            <td style="color:green">
		          <% 
		          	} else {
		          %>
		            <td style="color:red">
		          <%
		          }
		          %>  
		          <%=log.getLogLevel()%>
		          </td>
		          <%
		          	for(int i=1; i<tokens.length; i++) {
		          %>
		            <td>
		              <%=tokens[i]%>
		            </td>
		          <%
		            }
		          %>
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