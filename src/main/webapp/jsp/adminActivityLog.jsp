<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
	<script language="JavaScript" src="/js/common.js"></script>
	<script language="JavaScript" src="/js/administrator.js"></script>
	<script language="JavaScript" src="/js/adminActivityLog.js"></script>
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
			
			<form method="post" action="" id="logSearch">
			<table class="inputTable" id="activityLogSearch">
				<tr>
					<td>
						<span class="bold">Servlets to View:</span>
					</td>
					<td>
					<%
					    if (helper.checkAllServlets != null && helper.checkAllServlets.equals("on")){
					%>
						<input type="checkbox" name="selectAll" onclick="toggleAllServlets(this.checked)" checked="checked"> All Servlets
					<%
						} else {
					%>
						<input type="checkbox" name="selectAll" onclick="toggleAllServlets(this.checked)"> All Servlets
					<%
						}
					%>
					</td>
					<td></td>
					<td><a href="#" onclick="showServlets();">Display Servlet Options</a></td>
				</tr>
				<tr><td colspan="4" <%=(helper.checkAllServlets != null && helper.checkAllServlets.equals("on")) ? "style=\"display: none;\"" : "" %>><hr width="75%" id="topHR"></td></tr>
				<%
					int limit = 4;
					int counter = 0;
					String display = "";
					if (helper.checkAllServlets != null && helper.checkAllServlets.equals("on")){
					    display = "style=\"display: none;\"";
					}
					for (int i = 0; i < helper.listOfServlets.size(); i++){
						String servletName = helper.listOfServlets.get(i);
						if (counter == 0){
							out.print("<tr>");
						}
						
						if (helper.searchServlets(servletName)){
							out.println("<td " + display + "><input type=\"checkbox\" name=\"toggle_servlets\" value=\"" + servletName + "\" checked=\"checked\">" + servletName + "</td>");
						} else {
							out.println("<td " + display + "><input type=\"checkbox\" name=\"toggle_servlets\" value=\"" + servletName + "\">" + servletName + "</td>");
						}
						
						
						if (counter == 3){
							out.print("</tr>");
						}
						
						counter ++;
						counter %= 4;
					}
					if (counter == 0){
						out.print("</tr>");
					}
				%>
				<tr><td colspan="4" <%=(helper.checkAllServlets != null && helper.checkAllServlets.equals("on")) ? "style=\"display: none;\"" : "" %>><hr width="75%" id="bottomHR"></td></tr>
				<tr>
					<td colspan="2">
						<span class="bold">Search Person: </span>
						<input type="text" name="searchPerson" value="<%=(helper.searchPerson != null) ? helper.searchPerson : "" %>">
					</td>
					<td>
						<span class="bold">Role:</span>
						<select name="searchRole">
							<option value="All" <%=(helper.searchRole != null && helper.searchRole.equals("All")) ? "selected" : ""%>>All</option>
							<option value="Instructor" <%=(helper.searchRole != null && helper.searchRole.equals("Instructor")) ? "selected" : ""%>>Instructor</option>
							<option value="Student" <%=(helper.searchRole != null && helper.searchRole.equals("Student")) ? "selected" : ""%>>Student</option>
							<option value="Others" <%=(helper.searchRole != null && helper.searchRole.equals("Others")) ? "selected" : ""%>>Others</option>
						</select>
					</td>
					<td class="centeralign">
						<input class="button" type="submit" name="search_submit" value="Search Submit">
					</td>
				</tr>
			</table>
			<input type="hidden" name="offset" value="<%=helper.offset %>">
			<input type="hidden" name="pageChange" value="false">
			</form>  
			<br>
			<br>
			<br>  
			  
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
			        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		          for (AppLogLine log : appLogs) {
		  	        appCal.setTimeInMillis((log.getTimeUsec() / 1000) + 8*3600*1000);
		  	        String logMessageTableRow;
		  	        try{
		  	          logMessageTableRow= helper.parseLogMessage(sdf.format(appCal.getTime()), log.getLogMessage());
		  	        } catch (Exception e){
		  	          logMessageTableRow= "<tr>Error trying to parse Log Message<br>" + log.getLogMessage() + "</tr>";  
		  	        }

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