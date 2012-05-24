<%@ page import="java.util.*"%>
<%@ page import="teammates.*"%>
<%@ page import="teammates.jdo.*" %>
<%@ page import="teammates.exception.*" %>

<%	
	// See if user is logged in, if not we redirect them to the login page
	Accounts accounts = Accounts.inst();
	if (accounts.getUser() == null) {
		response.sendRedirect( accounts.getLoginPage("/coordinator.jsp") );
		return ;
	}
	
	APIServlet server = new APIServlet();
	
	String coordID = accounts.getUser().getNickname().toLowerCase();
	String statusMessage = null;
	boolean error = false;
%>
<%
	String newCourseID = request.getParameter(Common.COURSE_ID);
	String newCourseName = request.getParameter(Common.COURSE_NAME);
	if(newCourseID!=null && newCourseName!=null){
		try{
			server.createCourse(coordID, newCourseID, newCourseName);
			statusMessage = Common.MESSAGE_COURSE_ADDED;
		} catch (EntityAlreadyExistsException e){
			statusMessage = Common.MESSAGE_COURSE_EXISTS;
			error = true;
		} catch (InvalidParametersException e){
			statusMessage = e.getMessage();
			error = true;
		}
	}
	// If the course add was successful, do not display it again in the input boxes
	if(error==false){
		newCourseID = null;
		newCourseName = null;
	}
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
	<%
		// Check if user is allowed to view this page
		if (!accounts.isCoordinator()) {
	%>
	<p>
		You are not authorized to view this page.
		<br /><br />
		<a href="javascript:logout();">Logout and return to main page.</a>
	</p>
	<%
		} else {
	%>

	<div id="frameTop">
		<jsp:include page="/header.jsp" />
	</div>
	
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>ADD NEW COURSE</h1>
			</div>
			<div id="coordinatorCourseManagement">
			<%
				out.println(
					"<form method='post' action='coordCourse.jsp' name='form_addcourse'>" +
						"<table class='addform round'>" +
						"<tr>" +
							"<td><b>Course ID:</b></td>" +
						"</tr>" +
						"<tr>" +
							"<td><input class='addinput' type='text' name='" + Common.COURSE_ID + "' id='" + Common.COURSE_ID + "'" +
							"value=\"" + (newCourseID==null?"":newCourseID) + "\"" +
							"onmouseover=\"ddrivetip('Enter the identifier of the course, e.g.CS3215Sem1.')\"" +
							"onmouseout=\"hideddrivetip()\" maxlength=" + Common.COURSE_ID_MAX_LENGTH + " tabindex=1 /></td>" +
						"</tr>" +
						"<tr>" +
							"<td><b>Course Name:</b></td>" +
						"</tr>" +
						"<tr>" +
							"<td><input class='addinput' type='text' name='" + Common.COURSE_NAME + "' id='" + Common.COURSE_NAME + "'" +
							"value=\"" + (newCourseName==null?"":newCourseName) + "\"" +
							"onmouseover=\"ddrivetip('Enter the name of the course, e.g. Software Engineering.')\"" +
							"onmouseout=\"hideddrivetip()\" maxlength=" + Common.COURSE_NAME_MAX_LENGTH + " tabindex=2 /></td>" +
						"</tr>" +
						"<tr>" +
							"<td><input id='btnAddCourse' type='submit' class='button'" +
							"onclick=\"return doAddCourse();\"" +
							"value='Add Course' tabindex='3' /></td>" +
						"</tr>" +
						"</table>" +
					"</form>");
				%>
			</div>
			<br />
			<%  if(statusMessage!=null) {%>
				<div id="statusMessage" style="display:block;<% if(error) out.println("background:#FF9999"); %>" >
					<% out.println(statusMessage); %>
				</div>
				<%} else { %>
				<div id="statusMessage" style="display:none"></div>
			<%	} %>
			<div id="coordinatorCourseTable">
				<%
				out.println(
					"<br /><br />" +
					"<table id='dataform'>" +
						"<tr>" +
							"<th><input class='buttonSortAscending' type='button' id='button_sortcourseid'" + 
							"onclick=\"javascript:toggleSortCoursesByID(this,1);\"" +
							">COURSE ID</input></th>" +
							"<th><input class='buttonSortNone' type='button' id='button_sortcoursename'" +
							"onclick=\"javascript:toggleSortCoursesByName(this,2);\"" +
							">COURSE NAME</input></th>" +
							"<th class='centeralign'>TEAMS</th>" +
							"<th class='centeralign'>TOTAL STUDENTS</th>" +
							"<th class='centeralign'>TOTAL UNREGISTERED</th>" +
							"<th class='centeralign'>ACTION(S)</th>" +
						"</tr>"
				);
				HashMap<String, CourseSummaryForCoordinator> courses = server.getCourseListForCoord(coordID);
				CourseSummaryForCoordinator[] summary = courses.values().toArray(new CourseSummaryForCoordinator[]{});
				Arrays.sort(summary,new Comparator<CourseSummaryForCoordinator>(){
					public int compare(CourseSummaryForCoordinator obj1, CourseSummaryForCoordinator obj2){
						return obj1.getID().compareTo(obj2.getID());
					}
				});
				int idx = 0;
				for(idx=0; idx<summary.length; idx++){
					CourseSummaryForCoordinator course = summary[idx];
					out.println(
						"<tr>" +
							"<td id='courseID" + idx + "'>" + course.getID() + "</td>" +
							"<td id='courseName" + idx + "'>" + course.getName() + "</td>" +
							"<td class='t_course_teams centeralign'>" + course.getNumberOfTeams() + "</td>" +
							"<td class='centeralign'>" + course.getTotalStudents() + "</td>" +
							"<td class='centeralign'>" + course.getUnregistered() + "</td>" +
							"<td class='centeralign'>" +
								"<a class='t_course_enroll' href=\"coordCourseEnroll.jsp?courseid=" + course.getID() + "\"" +
									"hideddrivetip();\" onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_ENROLL + "')\"" +
									"onmouseout=\"hideddrivetip()\">Enroll</a>" +
								"<a class='t_course_view' href=\"coordCourseDetails.jsp?courseid=" + course.getID() + "\"" +
									"hideddrivetip();\" onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_VIEW_COURSE + "')\"" +
									"onmouseout=\"hideddrivetip()\">View</a>" +
								"<a class='t_course_delete'" +
									"href=\"coordDeleteCourse.jsp?next=coordCourse.jsp&courseid=" + course.getID() + "\"" +
									"onclick=\"hideddrivetip(); return toggleDeleteCourseConfirmation('" + course.getID() + "');" +
									"\" onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_DELETE_COURSE + "')\"" +
									"onmouseout=\"hideddrivetip()\">Delete</a>" +
							"</td>" +
						"</tr>"
					);
				}
				out.println("</table><br /><br /><br />");
				if(idx==0){
					out.println("No records found.<br /><br /><br /><br />");
				}
				%>
			</div>
		</div>
	</div>
	
	<div id="frameBottom">
		<jsp:include page="/footer.jsp" />
	</div>
	<%
		}
	%>
</body>
</html>