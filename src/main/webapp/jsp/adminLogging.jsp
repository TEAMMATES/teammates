<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminHomeHelper"%>
<% AdminHomeHelper helper = (AdminHomeHelper)request.getAttribute("helper"); %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="com.google.appengine.api.log.AppLogLine" %>
<%@ page import="java.util.Calendar" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Administrator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />

	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
</head>

<body>
	<div id="dhtmltooltip"></div>

	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47"
					src="/images/teammateslogo.jpg"
					width="150" />
			</div>
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='t_logout' href="<%= Common.JSP_LOGOUT %>">Logout</a></li>
				</ul>
			</div>
		</div>
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
			   <%
			      List<AppLogLine> appLogs = (List<AppLogLine>) request.getAttribute("appLogs");
			      if (appLogs != null) {
			    %>
			 <table>
		        <tr>
		          <th>Date</th>
		          <th></th>
		          <th>Level</th>
		          <th>Message</th>
		        </tr>
		    <%
		        if (appLogs.isEmpty()) {
		    %>
		        <tr>
		          <td colspan='4'><i>No application logs found</i></td>
		        </tr>
		    <%
		        } else {
			        Calendar appCal = Calendar.getInstance();
	
		          for (AppLogLine log : appLogs) {
		  	        appCal.setTimeInMillis(log.getTimeUsec() / 1000);

		    %>
		        <tr>
		          <td>
		            <%=appCal.getTime().toString()%>
		          </td>
		          <td>
		          </td>
		          <td>
		          <%=log.getLogLevel()%>
		          </td>
		          <td>
		          <%=log.getLogMessage()%>
		          </td>
		        
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