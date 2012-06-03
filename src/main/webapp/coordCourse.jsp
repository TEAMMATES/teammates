<%@ page import="java.util.*"%>
<%@ page import="teammates.api.*"%>
<%@ page import="teammates.datatransfer.*"%>
<%@ page import="teammates.jsp.*"%>

<%	
	CoordCourseAddHelper helper = new CoordCourseAddHelper();
	// See if user is logged in, if not we redirect them to the login page
	if (!helper.isUserLoggedIn()) {
		response.sendRedirect(helper.getLoginUrl(request));
		return ;
	}

	helper.init(request);

%>
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
	<script language="JavaScript" src="/js/constants.js"></script>
	<script language="JavaScript" src="/js/commonNew.js"></script>
	
	<script language="JavaScript" src="/js/coordCoursePageNew.js"></script>
	<script language="JavaScript" src="/js/coordinatorNew.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<%	// Check if user is allowed to view this page
		if ((!helper.loggedInUser.isAdmin()) && (!helper.loggedInUser.isCoord())) {
	%>
	<p>
		You are not authorized to view this page. <br /> <br />
		<a href="logout.jsp">Logout and return to main page.</a>
	</p>
	<%	} else { // AUTHENTICATED USER %>

	<div id="frameTop">
		<jsp:include page="coordHeader.jsp" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>ADD NEW COURSE</h1>
			</div>
			<div id="coordinatorCourseManagement">
				<form method='get' action='coordCourse.jsp' name='form_addcourse'>
					<table class='addform round'>
						<tr>
							<td><b>Course ID:</b></td>
						</tr>
						<tr>
							<td><input class='addinput' type='text'
								name='<%= Common.COURSE_ID %>' id='<%= Common.COURSE_ID %>'
								value='<%= (helper.newCourseID==null?"":helper.newCourseID) %>'
								onmouseover='ddrivetip("Enter the identifier of the course, e.g.CS3215Sem1.")'
								onmouseout='hideddrivetip()'
								maxlength=<%= Common.COURSE_ID_MAX_LENGTH %> tabindex=1 /></td>
						</tr>
						<tr>
							<td><b>Course Name:</b></td>
						</tr>
						<tr>
							<td><input class='addinput' type='text'
								name='<%= Common.COURSE_NAME %>' id='<%= Common.COURSE_NAME %>'
								value='<%=(helper.newCourseName==null?"":helper.newCourseName)%>'
								onmouseover='ddrivetip("Enter the name of the course, e.g. Software Engineering.")'
								onmouseout='hideddrivetip()'
								maxlength=<%= Common.COURSE_NAME_MAX_LENGTH %> tabindex=2 /></td>
						</tr>
						<tr>
							<td><input id='btnAddCourse' type='submit' class='button'
								onclick='return doAddCourse();' value='Add Course' tabindex='3' /></td>
						</tr>
					</table>
				</form>
			</div>
			<br />
			<%	if(helper.statusMessage!=null) { %>
				<div id="statusMessage"
					style="display:block;<% if(helper.error) out.println("background:#FF9999"); %>">
					<% out.println(helper.statusMessage); %></div>
			<%	} else { %>
				<div id="statusMessage" style="display: none"></div>
			<%	} %>
			<div id="coordinatorCourseTable">
				<br />
				<br />
				<table id='dataform'>
					<tr>
						<th><input class='buttonSortAscending' type='button'
							id='button_sortcourseid'
							onclick='javascript:toggleSortCoursesByID(this,1);' />
							COURSE ID</th>
						<th><input class='buttonSortNone' type='button'
							id='button_sortcoursename'
							onclick='javascript:toggleSortCoursesByName(this,2);' />
							COURSE NAME</th>
						<th class='centeralign'>TEAMS</th>
						<th class='centeralign'>TOTAL STUDENTS</th>
						<th class='centeralign'>TOTAL UNREGISTERED</th>
						<th class='centeralign'>ACTION(S)</th>
					</tr>
					<%	
						int idx = 0;
						for(idx=0; idx<helper.summary.length; idx++){
							CourseData course = helper.summary[idx];
					%>
						<tr class='courses_row'>
							<td id='courseID<%= idx %>'><%= course.id %></td>
							<td id='courseName<%= idx %>'><%= course.name %></td>
							<td class='t_course_teams centeralign'><%= course.teamsTotal %></td>
							<td class='centeralign'><%= course.studentsTotal %></td>
							<td class='centeralign'><%= course.unregisteredTotal %></td>
							<td class='centeralign'>
								<a class='t_course_enroll'
									href='<%= helper.getCourseEnrollLink(course.id) %>'
									onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_ENROLL %>")'
									onmouseout='hideddrivetip()'>Enroll</a>
								<a class='t_course_view'
									href='<%= helper.getCourseViewLink(course.id) %>'
									onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_VIEW_COURSE %>")'
									onmouseout='hideddrivetip()'>View</a>
								<a class='t_course_delete'
									href='<%= helper.getCourseDeleteLink(course.id,"coordCourse.jsp") %>'
									onclick='hideddrivetip(); return toggleDeleteCourseConfirmation("<%= course.id %>");'
									onmouseover='ddrivetip("<%= Common.HOVER_MESSAGE_DELETE_COURSE %>")'
									onmouseout='hideddrivetip()'>Delete</a>
							</td>
						</tr>
					<%	}
						if(idx==0){ // Print empty row
					%>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					<%	} %>
				</table>
				<br />
				<br />
				<br />
				<% if(idx==0){ %>
					No records found. <br />
					<br />
					<br />
					<br />
				<% } %>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="footer.jsp" />
	</div>
	<%	} // END OF AUTHENTICATED USER %>
</body>
</html>