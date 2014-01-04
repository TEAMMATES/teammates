<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.ActivityLogEntry" %>
<%@ page import="teammates.ui.controller.AdminActivityLogPageData"%>

<%
	AdminActivityLogPageData data = (AdminActivityLogPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>TEAMMATES - Administrator</title>
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
	<jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
	</div>
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h2>Admin Activity Log</h2>
			</div>
		    <form method="post" action="" id="activityLogFilter">
		    <table class="inputTable" id="filterForm">
		      <tr>
		          <td class="label bold">Filter:</td>
		          <td><input type="text" id="filterQuery" name="filterQuery" value="<%=data.filterQuery%>"></td>
		          <td><input class="button" type="submit" name="search_submit" value="Filter"></td>
		      </tr>
		      <tr>
		          <td class="label bold">Reference:</td>
		          <td colspan="2"><a href="#" onclick="toggleReference()">Show/Hide Reference</a></td>
		      </tr>
		      <tr>
		          <td></td>
		          <td colspan="2" id="filterReference">
		          <p>
		              A query is formed by a list of filters. Each filter is in the format <span class="bold">[filter label]: [value1, value2, value3....]</span> <br>
		              Combine filters with the " AND " keyword or the '|' separator. <br><br>
		              
		              <span class="bold">Sample Queries:</span><br>
		              E.g. role: Instructor AND request: InstructorCourse, InstructorEval AND from: 15/03/13<br>
		              E.g. from: 13/3/13 AND to: 17/3/13 AND person: teammates.test AND response: Pageload, System Error Report, Servlet Action Failure<br><br>
		              <span class = "bold"> Possible Labels: </span>from, to, person, role, request, response<br>
		              E.g. from: 13/03/13<br>
		              E.g. to: 13/03/13<br>
		              E.g. person: teammates.coord<br>
		              E.g. role: Instructor, Student<br>
		              E.g. request: InstructorEval, StudentHome, evaluationclosingreminders<br>
		              E.g. response: Pageload, System Error Report, Delete Course<br><br>
		              
		              
		              <span class = "bold"> Possible Roles: </span> Instructor, Student, Unknown<br><br>
		              
		              <span class = "bold"> Possible Servlets Requests: </span> <br>
		              <table>
		                  <tr>
		                      <td>instructorHome</td>
		                      <td>instructorCourse</td>
		                      <td>instructorCourseEnroll</td>
		                      <td>instructorCourseEdit</td>
		                      <td>instructorCourseRemind</td>
		                  </tr>
		                  <tr>
		                      <td>instructorCourseDelete</td>
		                      <td>instructorCourseDetails</td>
		                      <td>instructorCourseStudentEdit</td>
		                      <td>instructorCourseStudentDelete</td>
		                      <td>instructorCourseStudentDetails</td>
		                  </tr>
		                  <tr>
		                      <td>instructorEval</td>
		                      <td>instructorEvalExport</td>
		                      <td>instructorEvalEdit</td>
		                      <td>instructorEvalDelete</td>
		                      <td>instructorEvalRemind</td>
		                  </tr>
		                  <tr>
		                      <td>instructorEvalPublish</td>
		                      <td>instructorEvalUnpublish</td> 
		                      <td>instructorEvalResults</td>
		                      <td>instructorEvalSubmissionEditHandler</td>
		                      <td>instructorEvalSubmissionEdit</td>
		                  </tr>
		                  <tr>
		                      <td>instructorEvalSubmissionView</td>
		                  </tr>
		                  <tr>
		                      <td>studentHome</td>
		                      <td>studentCourseDetails</td>
		                      <td>studentCourseJoin</td>
		                      <td>studentEvalEditHandler</td>
		                      <td>studentEvalEdit</td>
		                  </tr>
		                  <tr>
		                      <td>studentEvalResults</td>
		                  </tr>
		                  <tr>
		                      <td>evaluationclosingreminders</td>
		                      <td>evaluationopeningreminders</td>
		                  </tr>
		              </table><br><br>
		              <span class = "bold"> Possible Responses: </span> <br>
		              <table>
		                  <tr>
		                      <td>Add New Course</td>
		                      <td>Enroll Students</td>
		                      <td>Edit Course Info</td>
		                      <td>Delete Course</td>
		                      <td>Edit Student Details</td>
		                  </tr>
		                  <tr>
                              <td>Delete Student</td>
                              <td>Send Registration</td>
                              <td>Create New Evaluation</td>
                              <td>Edit Evaluation Info</td>
                              <td>Delete Evaluation</td>
                          </tr>
                          <tr>
                              <td>Remind Students About Evaluation</td>
                              <td>Publish Evaluation</td>
                              <td>Unpublish Evaluation</td>
                              <td>Edit Submission</td>
                          </tr>
                          <tr>
                            <td>Student Joining Course</td>
                            <td>Edit Submission</td>
                          </tr>
                          <tr>
                            <td>Pageload</td>
                            <td>Send Evaluation Closing reminders</td>
                            <td>Send Evaluation Opening reminders</td>
                            <td>System Error Report</td>
                            <td>Servlet Action Failure</td>
                          </tr>
		              </table>
		              
		          </p>    
		          </td>
		      </tr>
		      <%
		      	if (data.queryMessage != null){
		      		      		                          out.println("<tr><td colspan=\"3\" class=\"color_red bold\">" + data.queryMessage + "</td></tr>");
		      		      		                      }
		      %>
		    </table>
		    <input type="hidden" name="offset" value="<%=data.offset%>">
		    <input type="hidden" name="pageChange" value="false">
		    
		    </form>
		    <br>
		    <br>
			<%
				List<ActivityLogEntry> appLogs = data.logs;
									  if (appLogs != null) {
			%>
			<table class="dataTable">
		        <tr>
		          <th width="10%">Date</th><th>[Role][Google ID][Name][Email][Action]</th>
		        </tr>
		    <%
		    	if (appLogs.isEmpty()) {
		    %>
		        <tr>
		          <td colspan='2'><i>No application logs found</i></td>
		        </tr>
		    <%
		    	} else {
		    		    		    		    		            for (ActivityLogEntry log : appLogs){
		    %>
		        <tr>
		          <td><%=log.getDateInfo()%></td>
		          <td><%=log.getRoleInfo()%>&nbsp;&nbsp;<%=log.getPersonInfo()%>&nbsp;&nbsp;<%=log.getActionInfo()%> 
		          <br><%=log.getMessageInfo()%>
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
			    
			<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE_WITHOUT_FOCUS%>" />
			<br>
			<div class="rightalign"><a href="#frameBodyWrapper">Back To Top</a></div>
			<br>
			<br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>