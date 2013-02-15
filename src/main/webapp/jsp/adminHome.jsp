<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminHomeHelper"%>
<%@ page import="teammates.common.datatransfer.InstructorData" %>
<%@ page import="teammates.common.datatransfer.CourseData" %>
<%@ page import="teammates.common.exception.EntityDoesNotExistException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<% AdminHomeHelper helper = (AdminHomeHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Teammates - Administrator</title>
	<link rel="stylesheet" href="/stylesheets/adminHome.css" type="text/css">
	<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">

	<script type="text/javascript" src="/js/googleAnalytics.js"></script>
	<script type="text/javascript" src="/js/jquery-minified.js"></script>
	<script type="text/javascript" src="/js/tooltip.js"></script>
	<script type="text/javascript" src="/js/common.js"></script>
	<script type="text/javascript" src="/js/administrator.js"></script>
	<jsp:include page="../enableJS.jsp"></jsp:include>
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
			<h1>Add New Instructor</h1>
			</div>
			<div id="adminManagement">
				<form action="">
					<table id="addform" class="inputTable">
					<tr>
						<td class="label bold">Google ID:</td>
					</tr>
					<tr>
					   <td><input class="addinput" type="text" name="<%= Common.PARAM_INSTRUCTOR_ID %>"></td>
					</tr>
					<tr>
						<td class="label bold">Name:</td>
					</tr>
					<tr>
						<td><input class="addinput" type="text" name="<%= Common.PARAM_INSTRUCTOR_NAME %>"></td>
				    </tr>
				    <tr>
					    <td class="label bold">Email: </td>
					</tr>
					<tr>
						<td><input class="addinput" type="text" name="<%= Common.PARAM_INSTRUCTOR_EMAIL %>"></td>
				    </tr>
				    <tr>
						<td class="centeralign"><input type="checkbox" name="<%= Common.PARAM_INSTRUCTOR_IMPORT_SAMPLE %>" value="importsample">Import sample data</td>
				    </tr>
				    <tr>
						<td class="centeralign"><input id="btnAddInstructor" class="button" type="submit" value="Add Instructor" onclick="return verifyInstructorData();"></td>
				    </tr>
				    </table>
				</form>
			</div>
			<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
			<br>
			<br>
			<table class="dataTable">
			<tr>
                <th class="bold">Account Info</th>
                <th class="bold">Instructor for</th>
                <th class="bold">Student for</th>
			</tr>
			<%
			   for (InstructorData instructor : helper.instructorList){
			%>
			    <tr>
			         <td><%=instructor.name + " (" + instructor.googleId + ")<br>" + instructor.email %></td>
			         <td>
			         <%
			             HashMap<String, CourseData> courses = helper.server.getCourseDetailsListForInstructor(instructor.googleId);
			             ArrayList<CourseData> list = new ArrayList<CourseData>(courses.values());
			             for(CourseData course: list){
			                 out.print(" - [" + course.id + "] " + course.name + "<br>");
			             }
			         %>
			         </td>
			         <td>
			         <%
			             try{
			                 List<CourseData> student = helper.server.getCourseListForStudent(instructor.googleId);
			                 for (CourseData course : student){
	                             out.print(" - [" + course.id + "] " + course.name + "<br>");
	                         }
			             } catch (EntityDoesNotExistException e) {
			                 out.print("This account is not a student");
			             }
			             
			         %>
			         </td>
			    </tr>
			<%
			   }
			%>
			</table>
			<br>
			<br>
			<br>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>