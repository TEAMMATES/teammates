<%@ page import="java.util.*"%>
<%@ page import="teammates.*"%>
<%@ page import="teammates.jdo.*" %>
<%@ page import="teammates.jsp.*" %>

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
		} else { // Is coordinator
	%>

	<div id="frameTop">
		<jsp:include page="/coordHeader.jsp" />
	</div>
	
	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>COORDINATOR HOME</h1>
				<br />
				<div class='result_team'>
					<div class='result_addOrJoinCourse'>
						<a href="coordCourse.jsp" name='addNewCourse' id='addNewCourse'>Add New Course</a>
					</div>
				</div>
			</div>
			<br />
			<%  if(statusMessage!=null) {%>
				<div id="statusMessage" style="display:block;<% if(error) out.println("background:#FF9999"); %>" >
					<% out.println(statusMessage); %>
				</div>
				<%} else { %>
				<div id="statusMessage" style="display:none"></div>
			<%	} %>
			<div id="coordinatorStudentTable">
				<form method='post' action='' name='form_coursescourse'>
				<%
				HashMap<String, CourseSummaryForCoordinator> courses = server.getCourseListForCoord(coordID);
				ArrayList<EvaluationDetailsForCoordinator> evaluations = server.getEvaluationsListForCoord(coordID);
				
				CourseSummaryForCoordinator[] summary = courses.values().toArray(new CourseSummaryForCoordinator[]{});
				int idx = 0;
				for(idx=0; idx<summary.length; idx++){
					// This will print the latest one first since it is already ordered with the latest one at index 0
					CourseSummaryForCoordinator course = summary[idx];
					out.println(
					"<div class='result_team' id='course" + idx + "' name='course" + idx + "'>"+
					"<div class='result_homeTitle'>"+
						"<h2>[" + course.getID() + "] : " + course.getName() + "</h2>"+
					"</div>"+
					"<div class='result_homeLinks'>"+
						"<a class='t_course_enroll" + idx + "' " +
							"href=\"" + Helper.getCourseEnrollLink(course.getID()) + "\"" +
							"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_ENROLL + "')\""+
							"onmouseout=\"hideddrivetip()\">Enroll</a>"+
						"<a class='t_course_view" + idx + "' " +
							"href=\"" + Helper.getCourseDetailsLink(course.getID()) + "\"" +
							"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_VIEW_COURSE + "')\""+
							"onmouseout=\"hideddrivetip()\">View</a>"+
						"<a class='t_course_add_eval" + idx + "' " +
							"href=\"coordEval.jsp\"" +
							"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_ADD_EVALUATION + "')\""+
							"onmouseout=\"hideddrivetip()\">Add Evaluation</a>"+
						"<a class='t_course_delete" + idx + "' " +
							"href=\"" + Helper.getCourseDeleteLink(course.getID(), "coordHome.jsp") + "\"" +
							"onclick=\"hideddrivetip(); return toggleDeleteCourseConfirmation('" + course.getID() + "');" +
							"\""+"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_DELETE_COURSE + "')\"" +
							"onmouseout=\"hideddrivetip()\">Delete</a>"+
					"</div>"+
					"<div style='clear: both;'></div>"+
					"<br />");
					
					StringBuffer evaluationResult = new StringBuffer();
					int evalIdx = 0;
					for(EvaluationDetailsForCoordinator eval: evaluations){
						if(eval.getCourseID().equals(course.getID())){
							evaluationResult.append(
								"<tr id='evaluation" + evalIdx + "'>" +
								"<td class='t_eval_name'>" + eval.getName() + "</td>" +
								"<td class='t_eval_status centeralign'>" +
								"<span onmouseover=\"ddrivetip('" + Helper.getHoverMessageForEval(eval) + "')\"" +
								"onmouseout=\"hideddrivetip()\">" + Helper.getStatusForEval(eval) + "</span></td>" +
								"<td class='t_eval_response centeralign'>" +
								eval.getNumberOfCompletedEvaluations() + " / " + eval.getNumberOfEvaluations() +
								"</td>" +
								"<td class='centeralign'>" +
								Helper.getEvaluationActions(eval, evalIdx, true) +
								"</td>" +
								"</tr>"						
							);
						}
					}
					if(evaluationResult.length()>0){
						out.println(
							"<table id='dataform'>" +
							"<tr>" +
							"<th class='leftalign'>EVALUATION NAME</th>" +
							"<th class='centeralign'>STATUS</th>" +
							"<th class='centeralign'><span onmouseover=\"ddrivetip('Number of students submitted / Class size')\"" +
							"onmouseout=\"hideddrivetip()\">RESPONSE RATE</span></th>" +
							"<th class='centeralign'>ACTION(S)</th>" +
							"</tr>" +
							evaluationResult +
							"</table>" +
							"<br />"
						);
						out.flush();
					}
					
					out.println("</div><br /><br /><br />");
				}
				%>
				</form>
			</div>
		</div>
	</div>
	
	<div id="frameBottom">
		<jsp:include page="/footer.jsp" />
	</div>
	<%
		} // Endif (isCoordinator)
	%>
</body>
</html>