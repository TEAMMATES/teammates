<%@ page import="java.util.List" %>
<%@ page import="teammates.api.Common"%>
<%@ page import="teammates.datatransfer.StudentData"%>
<%@ page import="teammates.jsp.CoordCourseEnrollHelper"%>
<%	CoordCourseEnrollHelper helper = (CoordCourseEnrollHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Coordinator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />
	
	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	<script language="JavaScript" src="/js/date.js"></script>
	<script language="JavaScript" src="/js/CalendarPopup.js"></script>
	<script language="JavaScript" src="/js/AnchorPosition.js"></script>
	<script language="JavaScript" src="/js/helperNew.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>
	<script language="JavaScript" src="/js/coordCourseEnroll.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_COORD_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<% if(helper.isResult){ %>
				<div id="headerOperation">
					<h1>Enrollment Results for <%= helper.courseID %></h1>
				</div>
				<div id="coordinatorCourseEnrollmentResults">
					<%	for(int i=0; i<5; i++){
							List<StudentData> students = helper.students[i]; %>
						<%	if(students.size()>0){ %>
							<p><%= helper.getMessageForStudentsListID(i) %></p>
							<table id="dataform">
							<tr>
								<th>Student Name</th>
								<th>E-mail address</th>
								<th>Team</th>
								<th width="40%">Comments</th>
							</tr>
							<% for(StudentData student: students){ %>
								<tr>
									<td><%= student.name %></td>
									<td><%= student.email %></td>
									<td><%= student.team %></td>
									<td><%= student.comments %></td>
								</tr>
							<% 	} %>
							</table>
							<br />
						<%	} %>
					<%	} %>
				</div>
				<div id="coordinatorCourseEnrollmentButtons">
					<input type="button" class="t_back button" onclick="location.href='<%= helper.getCoordCourseLink() %>'" value="Back" />
				</div>
			<% } else { %>
				<div id="headerOperation">
					<h1>Enroll Students for <%= helper.courseID %></h1>
				</div>
				<form action="<%= helper.getCoordCourseEnrollLink(helper.courseID) %>" method="post">
					<input type="hidden" name="courseid" value="<%= helper.courseID %>"></input>
					<div id="coordinatorCourseEnrollment">
						<img src="/images/enrollInstructions.png" style="width:1012,height:324" border="0" />
						<p class="info" style="text-align: center;">Recommended maximum class size : 100 students</p>
						<br />
						<table class="headerform"><tr>
							<td class="fieldname" style="width: 250px;">Student details:</td>
							<td><textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea></td>
						</tr></table>
					</div>
					<jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
					<div id="coordinatorCourseEnrollmentButtons">
						<input type="submit" class="button" name="button_enroll" id="button_enroll" value="Enroll students"
							onclick="return checkEnrollmentInput(document.getElementById('enrollstudents').value)"/>
						<input type="button" class="t_back button" onclick="location.href='<%= helper.getCoordCourseLink() %>'" value="Back" />
						<br /><br /><br /><br />
					</div>
				</form>
			<% } %>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
</body>
</html>