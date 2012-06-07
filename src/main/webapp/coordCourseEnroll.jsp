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
	
	<script language="JavaScript" src="/js/coordCourseEnroll.js"></script>
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="coordHeader.jsp" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<% if(helper.isResult){ %>
				<div id="headerOperation">
					<h1>Enrollment Results for <%= helper.courseID %></h1>
				</div>
				<div id="coordinatorCourseEnrollmentResults">
					<%	List<StudentData> students = helper.studentsError;
						if(students.size()>0){ %>
						<p>There were errors on <%= students.size() %> student(s):</p>
						<table id="dataform">
						<tr>
							<th>Student Name</th>
							<th>E-mail address</th>
							<th>Team</th>
							<th>Status</th>
							<th>Comments</th>
						</tr>
						<% for(StudentData student: students){ %>
							<tr>
								<td><%= student.name %></td>
								<td><%= student.email %></td>
								<td><%= student.team %></td>
								<td><%= CoordCourseEnrollHelper.getEnrollmentStatus(student.updateStatus) %></td>
								<td><%= student.comments %></td>
							</tr>
						<% 	} %>
						</table>
						<br />
					<%	} %>
					<%	students = helper.studentsNew;
						if(students.size()>0){ %>
						<p>There are <%= students.size() %> student(s) added:</p>
						<table id="dataform">
						<tr>
							<th>Student Name</th>
							<th>E-mail address</th>
							<th>Team</th>
							<th>Status</th>
							<th>Comments</th>
						</tr>
						<% for(StudentData student: students){ %>
							<tr>
								<td><%= student.name %></td>
								<td><%= student.email %></td>
								<td><%= student.team %></td>
								<td><%= CoordCourseEnrollHelper.getEnrollmentStatus(student.updateStatus) %></td>
								<td><%= student.comments %></td>
							</tr>
						<% 	} %>
						</table>
						<br />
					<%	} %>
					<%	students = helper.studentsModified;
						if(students.size()>0){ %>
						<p>There are <%= students.size() %> student(s) modified:</p>
						<table id="dataform">
						<tr>
							<th>Student Name</th>
							<th>E-mail address</th>
							<th>Team</th>
							<th>Status</th>
							<th>Comments</th>
						</tr>
						<% for(StudentData student: students){ %>
							<tr>
								<td><%= student.name %></td>
								<td><%= student.email %></td>
								<td><%= student.team %></td>
								<td><%= CoordCourseEnrollHelper.getEnrollmentStatus(student.updateStatus) %></td>
								<td><%= student.comments %></td>
							</tr>
						<% 	} %>
						</table>
						<br />
					<%	} %>
					<%	students = helper.studentsUnmodified;
						if(students.size()>0){ %>
						<p>There are <%= students.size() %> student(s) unmodified:</p>
						<table id="dataform">
						<tr>
							<th>Student Name</th>
							<th>E-mail address</th>
							<th>Team</th>
							<th>Status</th>
							<th>Comments</th>
						</tr>
						<% for(StudentData student: students){ %>
							<tr>
								<td><%= student.name %></td>
								<td><%= student.email %></td>
								<td><%= student.team %></td>
								<td><%= CoordCourseEnrollHelper.getEnrollmentStatus(student.updateStatus) %></td>
								<td><%= student.comments %></td>
							</tr>
						<% 	} %>
						</table>
						<br />
					<%	} %>
					<%	students = helper.studentsOld;
						if(students.size()>0){ %>
						<p>There are <%= students.size() %> other student(s) previously in the course:</p>
						<table id="dataform">
						<tr>
							<th>Student Name</th>
							<th>E-mail address</th>
							<th>Team</th>
							<th>Status</th>
							<th>Comments</th>
						</tr>
						<% for(StudentData student: students){ %>
							<tr>
								<td><%= student.name %></td>
								<td><%= student.email %></td>
								<td><%= student.team %></td>
								<td><%= CoordCourseEnrollHelper.getEnrollmentStatus(student.updateStatus) %></td>
								<td><%= student.comments %></td>
							</tr>
						<% 	} %>
						</table>
						<br />
					<%	} %>
				</div>
				<div id="coordinatorCourseEnrollmentButtons">
					<input type="button" class="t_back button" onclick="location.href='<%= Common.JSP_COORD_COURSE %>'" value="Back" />
				</div>
			<% } else { %>
				<div id="headerOperation">
					<h1>Enroll Students for <%= helper.courseID %></h1>
				</div>
				<form action="<%= Common.JSP_COORD_COURSE_ENROLL %>" method="post">
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
				<br />
				<%	if(helper.statusMessage!=null) { %>
					<div id="statusMessage"
						style="display:block;<% if(helper.error) out.println("background:#FF9999"); %>">
						<%= helper.statusMessage %></div>
				<%	} else { %>
					<div id="statusMessage" style="display: none"></div>
				<%	} %>
				<div id="coordinatorCourseEnrollmentButtons">
					<input type="submit" class="button" name="button_enroll" id="button_enroll" value="Enroll students"
						onclick="return checkEnrollmentInput(document.getElementById('information').value)"/>
					<input type="button" class="t_back button" onclick="location.href='<%= Common.JSP_COORD_COURSE %>'" value="Back" />
				</div>
				</form>
			<% } %>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="footer.jsp" />
	</div>
</body>
</html>