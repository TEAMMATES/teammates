//TODO: group function by page, group helper function by UI element
/*------------------------------------------Evaluation Report UI------------------------------------------*/
/* CONSTANTS */

var DIV_TOPOFPAGE = "topOfPage";
var DIV_HEADER_OPERATION = "headerOperation";

var COURSE_ID_MAX_LENGTH = 21;
var COURSE_NAME_MAX_LENGTH = 38;
var EVAL_NAME_MAX_LENGTH = 38;

//PARAMETERS
var COURSE_ID = "courseid";
var COURSE_NAME = "coursename";
var COURSE_NUMBEROFTEAMS = "coursenumberofteams";
var COURSE_TOTALSTUDENTS = "coursetotalstudents";
var COURSE_UNREGISTERED = "courseunregistered";
var COURSE_STATUS = "coursestatus";

var EVALUATION_ACTIVATED = "activated";
var EVALUATION_ARCHIVED = "evaluationarchived";
var EVALUATION_COMMENTSENABLED = "commentsstatus";
var EVALUATION_DEADLINE = "deadline";
var EVALUATION_DEADLINETIME = "deadlinetime";
var EVALUATION_GRACEPERIOD = "graceperiod";
var EVALUATION_INSTRUCTIONS = "instr";
var EVALUATION_NAME = "evaluationname";
var EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS = "numberofevaluations";
var EVALUATION_NUMBEROFEVALUATIONS = "numberofcompletedevaluations";
var EVALUATION_PUBLISHED = "published";
var EVALUATION_START = "start";
var EVALUATION_STARTTIME = "starttime";
var EVALUATION_TIMEZONE = "timezone";
var EVALUATION_TYPE = "evaluationtype";

var STUDENT_COMMENTS = "comments";
var STUDENT_COMMENTSEDITED = "commentsedited";
var STUDENT_COMMENTSTOSTUDENT = "commentstostudent";
var STUDENT_COURSEID = "courseid";
var STUDENT_EDITCOMMENTS = "editcomments";
var STUDENT_EDITEMAIL = "editemail";
var STUDENT_EDITGOOGLEID = "editgoogleid";
var STUDENT_EDITNAME = "editname";
var STUDENT_EDITTEAMNAME = "editteamname";
var STUDENT_EMAIL = "email";
var STUDENT_FROMSTUDENT = "fromemail";
var STUDENT_FROMSTUDENTCOMMENTS = "fromstudentcomments";
var STUDENT_FROMSTUDENTNAME = "fromname";
var STUDENT_ID = "id";
var STUDENT_INFORMATION = "information";
var STUDENT_JUSTIFICATION = "justification";
var STUDENT_NAME = "name";
var STUDENT_NAMEEDITED = "nameedited";
var STUDENT_NUMBEROFSUBMISSIONS = "numberofsubmissions";
var STUDENT_POINTS = "points";
var STUDENT_POINTSBUMPRATIO = "pointsbumpratio";
var STUDENT_REGKEY = "regkey";
var STUDENT_STATUS = "status";
var STUDENT_TEAMNAME = "teamname";
var STUDENT_TEAMNAMEEDITED = "teamnameedited";
var STUDENT_TOSTUDENT = "toemail";
var STUDENT_TOSTUDENTCOMMENTS = "tostudentcomments";
var STUDENT_TOSTUDENTNAME = "toname";

//MESSAGES
var MSG_COURSE_EXISTS = "course exists";
var MSG_COURSE_NOTEAMS = "course has no teams";
var MSG_EVALUATION_ADDED = "evaluation added";
var MSG_EVALUATION_EDITED = "evaluation edited";
var MSG_EVALUATION_EXISTS = "evaluation exists";
var MSG_EVALUATION_UNABLETOCHANGETEAMS = "evaluation ongoing unable to change teams";

//RESPONSE TAG
var TAG_COORDINATOR_COURSE = "courses";

//user type
var STUDENT = "Student";
var COORDINATOR = "Coordinator";
//report type
var SUMMARY = "Summary";
var DETAIL = "Detail";
var REVIEWER = "Reviewer";
var REVIEWEE = "Reviewee";

//point options
var NA = "N/A";
var NA_POINTS = -101;
var NOTSURE = "NOT SURE";
var NOTSURE_POINTS = -999;
var YES = "YES";
var NO = "NO";

//title names:
var EVALUATION_PENDING = "Pending Evaluations:";
var EVALUATION_PAST = "Past Evaluations:";

//evaluation contents:
var REVIEWEE_TITLE_INDIVIDUAL = "Individual Submission - By Reviewee";
var REVIEWER_TITLE_INDIVIDUAL = "Individual Submission - By Reviewer";
var REVIEWEE_TITLE_DETAIL = "Detailed Evaluation Results - By Reviewee";
var REVIEWER_TITLE_DETAIL = "Detailed Evaluation Results - By Reviewer";

//attributes names:
var TEAM = "Team";
var COURSE = "Course ID";
var EVALUATION = "Evaluation Name";
var OPENING = "Opening Time";
var CLOSING = "Closing Time";
var CLAIMED = "Claimed Contributions";
var PERCEIVED = "Perceived Contributions";
var FEEDBACK_FROM = "Feedback From Other Students";
var FEEDBACK_TO = "Feedback To Other Students";
var FROM_STUDENT = "From Student";
var TO_STUDENT = "To Student";
var CONTRIBUTION = "Contribution";
var COMMENTS = "Comments";
var MESSAGES = "Messages";

/**
 * XMLHttpRequest Constants
 * 
 * */
var SERVERERROR = 1;
var CONNECTION_OK = 200;

//OPERATIONS
var OPERATION_COORDINATOR_ADDCOURSE = "coordinator_addcourse";
var OPERATION_COORDINATOR_ADDEVALUATION = "coordinator_addevaluation";
var OPERATION_COORDINATOR_ARCHIVECOURSE = "coordinator_archivecourse";
var OPERATION_COORDINATOR_ARCHIVEEVALUATION = "coordinator_archiveevaluation";
var OPERATION_COORDINATOR_DELETEALLSTUDENTS = "coordinator_deleteallstudents";
var OPERATION_COORDINATOR_DELETECOURSE = "coordinator_deletecourse";
var OPERATION_COORDINATOR_DELETEEVALUATION = "coordinator_deleteevaluation";
var OPERATION_COORDINATOR_DELETESTUDENT = "coordinator_deletestudent";
var OPERATION_COORDINATOR_EDITEVALUATION = "coordinator_editevaluation";
var OPERATION_COORDINATOR_EDITEVALUATIONRESULTS = "coordinator_editevaluationresults";
var OPERATION_COORDINATOR_EDITSTUDENT = "coordinator_editstudent";
var OPERATION_COORDINATOR_ENROLLSTUDENTS = "coordinator_enrollstudents";
var OPERATION_COORDINATOR_GETCOURSE = "coordinator_getcourse";
var OPERATION_COORDINATOR_GETCOURSELIST = "coordinator_getcourselist";
var OPERATION_COORDINATOR_GETEVALUATIONLIST = "coordinator_getevaluationlist";
var OPERATION_COORDINATOR_GETEVALUATIONLISTOFCOURSE = "coordinator_getevaluationlistofcourse";
var OPERATION_COORDINATOR_GETSTUDENTLIST = "coordinator_getstudentlist";
var OPERATION_COORDINATOR_GETSUBMISSIONLIST = "coordinator_getsubmissionlist";
var OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES = "coordinator_informstudentsofevaluationchanges";
var OPERATION_COORDINATOR_LOGOUT = "coordinator_logout";
var OPERATION_COORDINATOR_PUBLISHEVALUATION = "coordinator_publishevaluation";
var OPERATION_COORDINATOR_UNPUBLISHEVALUATION = "coordinator_unpublishevaluation";
var OPERATION_COORDINATOR_REMINDSTUDENTS = "coordinator_remindstudents";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEY = "coordinator_sendregistrationkey";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEYS = "coordinator_sendregistrationkeys";
var OPERATION_COORDINATOR_UNARCHIVECOURSE = "coordinator_unarchivecourse";
var OPERATION_COORDINATOR_UNARCHIVEEVALUATION = "coordinator_unarchiveevaluation";

//messages:
var DISPLAY_ERROR_UNDEFINED_HTTPREQUEST = "Error: Undefined XMLHttpRequest.";
var DISPLAY_SERVERERROR = "Connection to the server has timed out. Please refresh the page.";

var COORDINATOR_MESSAGE_NO_COURSE = "You have not created any courses yet. Use the form above to create a course.";
var COORDINATOR_MESSAGE_NO_EVALUATION = "You have not created any evaluations yet. Use the form above to create a new evaluation.";
var COORDINATOR_MESSAGE_NO_TEAMFORMINGSESSION = "You have not created any team forming sessions yet. Use the form above to create a new team forming session.";

var HOVER_MESSAGE_ENROLL = 'Enroll student into the course';
var HOVER_MESSAGE_VIEW_COURSE = 'View, edit and send registration keys to the students in the course';
var HOVER_MESSAGE_DELETE_COURSE = 'Delete the course and its corresponding students and evaluations';
var HOVER_MESSAGE_ADD_EVALUATION = 'Add an evaluation for the course';
var HOVER_MESSAGE_CLAIMED = "This is student own estimation of his/her contributions to the project";
var HOVER_MESSAGE_PERCEIVED = "This is the average of what other team members think this student contributed to the project";
var HOVER_MESSAGE_PERCEIVED_CLAIMED = "Difference between claimed and perceived contribution points";
var HOVER_MESSAGE_STUDENT_VIEW_COURSE = 'View course details';
/*------------------------------------------PRINT COMMON PAGE------------------------------------------*/
/*
 * View course list User: Student, Coordinator
 */
function printCourseList(courseList, user) {
	var courseListLength = courseList.length;
	var output = "<br /><br />"
		+ "<table id=\"dataform\">"
		+ "<tr>"
		+ "<th><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\">COURSE ID</input></th>"
		+ "<th><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcoursename\">COURSE NAME</input></th>";
	if (user == STUDENT) {
		output = output + "<th>TEAM NAME</th>";
	} else {
		output = output + "<th class='centeralign'>TEAMS</th>"
		+ "<th class='centeralign'>TOTAL STUDENTS</th>"
		+ "<th class='centeralign'>TOTAL UNREGISTERED</th>";
	}
	output = output + "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	if (courseListLength == 0) {
		if (user == COORDINATOR) {
			setStatusMessage(COORDINATOR_MESSAGE_NO_COURSE);
			output = output
			+ "<tr><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
		} else {
			output = output + "<tr><td></td><td></td><td></td><td></td></tr>";
		}
	}

	// Need counter to take note of archived courses
	var counter = 0;

	for (var loop = 0; loop < courseListLength; loop++) {
		if (courseList[loop].status == "false"
			|| courseViewArchivedStatus == courseViewArchived.show) {
			// common view:
			output = output + "<tr>";
			output = output + "<td id=\"courseID" + counter + "\">"
			+ courseList[loop].ID + "</td>";
			output = output + "<td id=\"courseName" + counter + "\">"
			+ encodeChar(courseList[loop].name) + "</td>";
			// student view:
			if (user == STUDENT) {
				output = output + "<td>"
				+ encodeCharForPrint(courseList[loop].teamName)
				+ "</td>";
				output = output + "<td class='centeralign'>"
//				+ "<a href=\"javascript:displayCourseInformation('"
//				+ courseList[loop].ID + "');hideddrivetip();\""
//				+ " onmouseover=\"ddrivetip('"+ HOVER_MESSAGE_STUDENT_VIEW_COURSE +"')\""
//				+ " onmouseout=\"hideddrivetip()\">View</a>"
				+ "<a id=\"viewTeams" + loop + "\" href=# "
				+ "onmouseover=\"ddrivetip('View/Create the teams for this course.')\""
				+ "onmouseout=\"hideddrivetip()\""
				+ ">View Teams</a>"
				+ "</td>";
			}
			// coordinator view:
			else {
				output = output + "<td class=\"t_course_teams centeralign\">"
				+ courseList[loop].numberOfTeams + "</td>"
				+ "<td class='centeralign'>"
				+ courseList[loop].totalStudents + "</td>"
				+ "<td class='centeralign'>"
				+ courseList[loop].unregistered + "</td>";
				output = output
				+ "<td class='centeralign'>"
				+ "<a class='t_course_enroll' href=\"javascript:displayEnrollmentPage('"
				+ courseList[loop].ID
				+ "');hideddrivetip();\""
				+ "onmouseover=\"ddrivetip('"
				+ HOVER_MESSAGE_ENROLL
				+ "')\""
				+ "onmouseout=\"hideddrivetip()\">Enroll</a>"
				+ "<a class='t_course_view' href=\"javascript:displayCourseInformation('"
				+ courseList[loop].ID + "');hideddrivetip();\""
				+ "onmouseover=\"ddrivetip('"
				+ HOVER_MESSAGE_VIEW_COURSE + "')\""
				+ "onmouseout=\"hideddrivetip()\">View</a>"
				+ "<a class='t_course_delete' href=\"javascript:toggleDeleteCourseConfirmation('"
				+ courseList[loop].ID + "'," + false+ ");hideddrivetip();\""
				+ "onmouseover=\"ddrivetip('"
				+ HOVER_MESSAGE_DELETE_COURSE + "')\""
				+ "onmouseout=\"hideddrivetip()\">Delete</a>"
				+ "</td></tr>";
			}

			counter++;
		}
	}

	output = output + "</table><br /><br />";

	if (counter == 0) {
		output = output + "No records found.<br /><br /><br /><br />";
	}

	document.getElementById(DIV_COURSE_TABLE).innerHTML = output;
	document.getElementById('button_sortcourseid').onclick = function() {
		toggleSortCoursesByID(courseList);
	};
	document.getElementById('button_sortcoursename').onclick = function() {
		toggleSortCoursesByName(courseList);
	};

	for (loop = 0; loop < courseList.length; loop++) {
		if (document.getElementById('viewTeams' + loop) != null
				&& document.getElementById('viewTeams' + loop).onclick == null) {
			document.getElementById('viewTeams' + loop).onclick = function() {
				hideddrivetip();
				var courseIndex = this.id.substring(9, this.id.length);
				displayStudentViewTeams(courseList[courseIndex].ID);
			};
		}
	}
}

/*------------------------------------------PRINT COORDINATOR PAGE------------------------------------------*/
function printEnrollmentPage(courseID) {
	var outputHeader = "<h1>ENROLL STUDENTS for " + courseID + "</h1>";

	var output = "<img src=\"/images/enrollInstructions.png\" style=\"width:1012,height:324\" border=\"0\" />"
		+ "<p class=\"info\" style=\"text-align: center;\">Recommended maximum class size : 100 students</p>"
		+ "<br />"
		+ "<form>"
		+ "<table class=\"headerform\">"
		+ "<tr>"
		+ "<td class=\"fieldname\" style=\"width: 250px;\">Student details:</td>"
		+ "<td><textarea rows=\"6\" cols=\"135\" class =\"textvalue\" name=\"information\" id=\"information\"></textarea></td>"
		+ "</tr>" + "</table>" + "</form>";

	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_enroll\" id=\"button_enroll\" value=\"Enroll students\" />"
		+ " <input type=\"button\" class=\"t_back button\" onclick=\"displayCoursesTab();\" value=\"Back\" />";

	document.getElementById(DIV_COURSE_ENROLLMENT).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_ENROLLMENTBUTTONS).innerHTML = outputButtons;

	document.getElementById('button_enroll').onclick = function() {
		doEnrollStudents(document.getElementById('information').value, courseID);
	};
}

function printEnrollmentResultsPage(reports) {
	var arrayAdd = [];
	var reportsLength = reports.length;

	for ( var x = 0; x < reportsLength; x++) {
		if (reports[x].status == "ADDED") {
			arrayAdd.push(reports[x]);
		}
	}

	var arrayEdit = [];

	for ( var x = 0; x < reportsLength; x++) {
		if (reports[x].status == "EDITED") {
			arrayEdit.push(reports[x]);
		}
	}

	var arrayAddLength = arrayAdd.length;

	var outputHeader = "<h1>ENROLLMENT RESULTS</h1>";
	var output = "<table id=\"data\">"
		+ "<tr>"
		+ "<td>"
		+ "<input class=\"plusButton\" type=\"button\" id=\"button_viewaddedstudents\" "
		+ "name=\"button_viewaddedstudents\" onclick=\"toggleViewAddedStudents();\" />Number of Students "
		+ "Added: <span id='t_studentsAdded'>" + arrayAddLength
		+ "</span></td>" + "</tr>";

	output = output
	+ "<tr style=\"display:none\" name=\"rowAddedStudents\" id=\"rowAddedStudents\">"
	+ "<td>";

	for ( var x = 0; x < arrayAddLength; x++) {
		output = output + "- " + arrayAdd[x].studentName + " ("
		+ arrayAdd[x].studentEmail + ")<br />";
	}

	var arrayEditLength = arrayEdit.length;

	output = output
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<br />"
	+ "</tr>"
	+ "<tr>"
	+ "<td>"
	+ "<input class=\"plusButton\" type=\"button\" id=\"button_vieweditedstudents\" "
	+ "name=\"button_vieweditedstudents\" onclick=\"toggleViewEditedStudents();\" />Number of Students "
	+ "Edited:</b> <span id='t_studentsEdited'>" + arrayEditLength
	+ "</span></td>" + "</tr>";

	output = output
	+ "<tr style=\"display:none\" name=\"rowEditedStudents\" id=\"rowEditedStudents\">"
	+ "<td>";

	for ( var x = 0; x < arrayEditLength; x++) {
		output = output + "- " + arrayEdit[x].studentName + " ("
		+ arrayEdit[x].studentEmail + ") : ";

		if (arrayEdit[x].nameEdited == "true") {
			output = output + "NAME ";
		}

		if (arrayEdit[x].teamNameEdited == "true") {
			output = output + "TEAMNAME ";
		}

		if (arrayEdit[x].commentsEdited == "true") {
			output = output + "COMMENTS ";
		}

		output = output + "<br />";
	}

	output = output
	+ "</td>"
	+ "</tr>"
	+ "</table>"
	+ "<br /><br /><br />"
	+ "<input type=\"button\" class=\"t_back button\" onclick=\"displayCoursesTab();\" value=\"Back\" />"
	+ "<br /><br />";

	document.getElementById(DIV_COURSE_ENROLLMENTRESULTS).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
}

/*
 * Coordinator view course details
 */
function printCourseCoordinatorForm(course) {
	var studentList = getStudentList(course.ID);

	var outputHeader = "<h1>COURSE DETAILS</h1>";
	var output = "" + "<table class=\"headerform\">" + "<tr>"
	+ "<td class=\"fieldname\">Course ID:</td>" + "<td>" + course.ID
	+ "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Course name:</td>" + "<td>"
	+ encodeChar(course.name) + "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Teams:</td>" + "<td>"
	+ course.numberOfTeams + "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Total students:</td>" + "<td>"
	+ studentList.length + "</td>" + "</tr>";

	if ((studentList != 1) && (studentList.length > 0)) {
		output = output
		+ "<tr>"
		+ "<td class=\"centeralign\" colspan=\"2\">"
		+ "<input type=\"button\" class=\"button t_remind_students\" id='button_remind' onmouseover=\"ddrivetip('Send a reminder to all students yet to join the class');\" "
		+ "onmouseout=\"hideddrivetip();\" "
		+ "onClick=\"toggleSendRegistrationKeysConfirmation('"
		+ course.ID
		+ "');hideddrivetip();\" value=\"Remind to Join\" tabindex=1 />"
		+ " <input type=\"button\" class=\"button t_delete_students\" id='button_delete' onmouseover=\"ddrivetip('Delete all students in this course');\""
		+ "onmouseout=\"hideddrivetip();\""
		+ "onclick=\"toggleDeleteAllStudentsConfirmation('" + course.ID
		+ "')\" value=\"Delete all students\" />" + "</td>" + "</tr>";
	}

	output = output + "</table>";

	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
}

/*
 * helper: print course student list TODO: Merge into printCourseCoordinatorForm
 */
function printStudentList(studentList, courseID) {
	clearStatusMessage();

	var output;
	var unregisteredCount = 0;
	var studentListLength = studentList.length;

	output = "<table id=\"dataform\">"
		+ "<tr>"
		+ "<th><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortstudentname\">STUDENT NAME</input></th>"
		+ "<th><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortstudentteam\">TEAM</input></th>"
		+ "<th class='centeralign'><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortstudentstatus\">STATUS</input></th>"
		+ "<th class='centeralign'>ACTION(S)</th>" + "</tr>";

	// Fix for empty student list
	if (studentListLength == 0) {
		setStatusMessage("No students enrolled in this course yet. Click <a class='t_course_enroll' href=\"javascript:displayEnrollmentPage('"
				+ courseID + "');\">here</a> to enroll students.");

		output = output + "<tr>" + "<td></td>" + "<td></td>" + "<td></td>"
		+ "<td></td>" + "</tr>";
	}

	for (var loop = 0; loop < studentListLength; loop++) {
		output = output + "<tr>" + "<td>" + studentList[loop].name + "</td>"
		+ "<td>" + encodeCharForPrint(studentList[loop].teamName)
		+ "</td>" + "<td class='centeralign'>";

		if (studentList[loop].googleID == "")
			output = output + "YET TO JOIN";
		else
			output = output + "JOINED";

		output = output
		+ "</td>"
		+ "<td class='centeralign'>"
		+ "<a class='t_student_view' href=\"javascript:displayStudentInformation('"
		+ studentList[loop].courseID
		+ "', '"
		+ studentList[loop].email
		+ "', '"
		+ escape(studentList[loop].name)
		+ "','"
		+ escape(studentList[loop].teamName)
		+ "','"
		+ studentList[loop].googleID
		+ "','"
		+ studentList[loop].registrationKey
		+ "','"
		+ encodeChar(studentList[loop].comments)
		+ "');hideddrivetip();\""
		+ "onmouseover=\"ddrivetip('View the details of the student')\""
		+ "onmouseout=\"hideddrivetip()\">View</a>"
		+ "<a class='t_student_edit' href=\"javascript:displayEditStudent('"
		+ studentList[loop].courseID
		+ "', '"
		+ studentList[loop].email
		+ "', '"
		+ escape(studentList[loop].name)
		+ "','"
		+ escape(studentList[loop].teamName)
		+ "','"
		+ studentList[loop].googleID
		+ "','"
		+ studentList[loop].registrationKey
		+ "','"
		+ encodeChar(studentList[loop].comments)
		+ "');hideddrivetip();\""
		+ "onmouseover=\"ddrivetip('Edit the details of the student')\""
		+ "onmouseout=\"hideddrivetip()\">Edit</a>";

		if (studentList[loop].googleID == "") {
			output = output
			+ "<a class='t_student_resend' href=\"javascript:doSendRegistrationKey('"
			+ studentList[loop].courseID
			+ "', '"
			+ studentList[loop].email
			+ "','"
			+ studentList[loop].name
			+ "');hideddrivetip();\""
			+ "onmouseover=\"ddrivetip('E-mail the registration key to the student')\""
			+ "onmouseout=\"hideddrivetip()\">Resend Invite</a>";
		}

		output = output
		+ "<a class='t_student_delete' href=\"javascript:toggleDeleteStudentConfirmation('"
		+ studentList[loop].courseID
		+ "', '"
		+ studentList[loop].email
		+ "', '"
		+ escape(studentList[loop].name)
		+ "');hideddrivetip();\""
		+ "onmouseover=\"ddrivetip('Delete the student and the corresponding evaluations from the course')\""
		+ "onmouseout=\"hideddrivetip()\">Delete</a>" + "</td>"
		+ "</tr>";

		if (studentList[loop].googleID == "")
			unregisteredCount++;
	}

	output = output + "</table>" + "<br />";

	output = output
	+ "<br /><br />"
	+ "<input type=\"button\" class=\"button\" onclick=\"displayCoursesTab();\" value=\"Back\" />"
	+ "<br /><br />";

	document.getElementById(DIV_STUDENT_TABLE).innerHTML = output;
	document.getElementById('button_sortstudentname').onclick = function() {
		toggleSortStudentsByName(studentList);
	};
	document.getElementById('button_sortstudentteam').onclick = function() {
		toggleSortStudentsByTeamName(studentList);
	};
	document.getElementById('button_sortstudentstatus').onclick = function() {
		toggleSortStudentsByStatus(studentList);
	};
}

/*
 * Coordinator view student detail (individual record) TODO: Improve UI,
 * abstract print_detail function:: printCourseStudentForm
 */
function printStudent(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	var outputHeader = "<h1>STUDENT DETAILS</h1>";
	var output = "<table class=\"detailform\">" + "<tr>"
	+ "<td class=\"fieldname\">Student Name:</td>" + "<td>" + name
	+ "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Team Name:</td>" + "<td>"
	+ encodeCharForPrint(teamName) + "</td>" + "</tr>" + "<tr><"
	+ "td class=\"fieldname\">E-mail Address:</td>" + "<td>" + email
	+ "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Google ID:</td>" + "<td>";

	if (googleID == "") {
		output = output + "-";
	} else {
		output = output + encodeChar(googleID);
	}

	output = output + "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Registration Key:</td>"
	+ "<td id='t_courseKey'>" + registrationKey + "</td>" + "</tr>"
	+ "<tr>" + "<td class=\"fieldname\">Comments:</td>" + "<td>";

	if (comments == "") {
		output = output + "-";
	} else {
		output = output + encodeCharForPrint(comments);
	}

	output = output
	+ "</div>"
	+ "</td>"
	+ "</tr>"
	+ "</table>"
	+ "<br /><br /><br />"
	+ "<input type =\"button\" class=\"t_back button\" onClick=\"displayCourseInformation('"
	+ courseID + "');\" value=\"Back\"/>" + "<br /><br />";

	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
}

/*
 * Coordinator edit student detail TODO: Merge into printStudent
 */
function printEditStudent(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	var outputHeader = "<h1>EDIT STUDENT</h1>";
	var output = "<form>" + "<table class=\"headerform\">" + "<tr>"
	+ "<td class=\"fieldname\">Student Name*:</td>"
	+ "<td><input class=\"fieldvalue\" type=\"text\" value=\""
	+ name
	+ "\" name=\"editname\" id=\"editname\"/></td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Team Name*:</td>"
	+ "<td><input class=\"fieldvalue\" type=\"text\" value=\""
	+ encodeCharForPrint(teamName)
	+ "\" name=\"editteamname\" id=\"editteamname\"/></td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">E-mail Address*:</td>"
	+

	"<td><input class=\"fieldvalue\" type=\"text\" value=\""
	+ email
	+ "\" name=\"editemail\" id=\"editemail\"/></td>"
	+

	"</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Google ID:</td>"
	+

	(googleID == "" ? "<td><input class=\"fieldvalue\" type=\"text\" value=\""
			+ encodeChar(googleID)
			+ "\" name=\"editgoogleid\" id=\"editgoogleid\"/></td>"
			: "<td><input class=\"fieldvalue\" type=\"text\" value=\""
				+ encodeChar(googleID)
				+ "\" name=\"editgoogleid\" id=\"editgoogleid\" disabled=\"true\" /></td>")
				+

				"</tr>"
				+ "<tr>"
				+ "<td class=\"fieldname\">Comments:</td>"
				+ "<td><textarea class =\"textvalue\" name=\"editcomments\" id=\"editcomments\" rows=\"6\" cols=\"80\">"
				+ encodeCharForPrint(comments)
				+ "</textarea></td>"
				+ "</tr>"
				+ "</table>";

	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_editstudent\" id=\"button_editstudent\" value=\"Save Changes\" />"
		+ "<input type=\"button\" class=\"button\" onClick=\"displayCourseInformation('"
		+ courseID + "')\" value=\"Back\" />" + "</form>" + "<br /><br />";

	document.getElementById(DIV_STUDENT_EDITBUTTONS).innerHTML = outputButtons;
	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;

	document.getElementById('button_editstudent').onclick = function() {
		var editName = document.getElementById('editname').value;
		var editTeamName = document.getElementById('editteamname').value;
		var editEmail = document.getElementById('editemail').value;
		var editGoogleID = document.getElementById('editgoogleid').value;
		var editComments = document.getElementById('editcomments').value;

		doEditStudent(courseID, email, editName, editTeamName, editEmail,
				editGoogleID, editComments);
	};
}

/*
 * Add evaluation Edit evaluation
 */
function printEvaluationAddForm(courseID) {
	var outputHeader = "<h1>ADD NEW EVALUATION</h1>";
	var outputForm = ""
		+ "<form method=\"post\" action=\"\" name=\"form_addevaluation\">"
		+ "<table class=\"headerform\">" + "<tr>"
		+ "<td class=\"attribute\" >Course ID:</td>"
		+ "<td><select style=\"width: 260px;\" name=\""
		+ COURSE_ID
		+ "\" id=\""
		+ COURSE_ID
		+ "\""
		+ "onmouseover=\"ddrivetip('Please select the course for which the evaluation is to be created.')\""
		+ "onmouseout=\"hideddrivetip()\" tabindex=1></select></td>"
		+ "<td class=\"attribute\" >Opening time:</td>"
		+ "<td><input style=\"width: 100px;\" type=\"text\" name=\""
		+ EVALUATION_START
		+ "\" id=\""
		+ EVALUATION_START
		+ "\" + "
		+ "onClick =\"cal.select(document.forms['form_addevaluation']."
		+ EVALUATION_START
		+ ",'"
		+ EVALUATION_START
		+ "','dd/MM/yyyy')\""
		+ "onmouseover=\"ddrivetip('Please enter the start date for the evaluation.')\""
		+ "onmouseout=\"hideddrivetip()\" READONLY tabindex=3> @ "
		+ "<select style=\"width: 70px;\" name=\""
		+ EVALUATION_STARTTIME
		+ "\" id=\""
		+ EVALUATION_STARTTIME
		+ "\" tabindex=4>"
		+ getTimeOptionString()
		+ "</select></td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td class=\"attribute\" >Evaluation name:</td>"
		+ "<td><input style=\"width: 260px;\" type=\"text\" name=\""
		+ EVALUATION_NAME
		+ "\" id=\""
		+ EVALUATION_NAME
		+ "\" onmouseover=\"ddrivetip('Enter the name of the evaluation e.g. Mid-term.')\""
		+ "onmouseout=\"hideddrivetip()\" maxlength ="+EVAL_NAME_MAX_LENGTH+" tabindex=2> </td>"
		+ "<td class=\"attribute\" >Closing time:</td>"
		+ "<td> <input style=\"width: 100px;\" type=\"text\" name=\""
		+ EVALUATION_DEADLINE
		+ "\" id=\""
		+ EVALUATION_DEADLINE
		+ "\" + "
		+ "onClick =\"cal.select(document.forms['form_addevaluation']."
		+ EVALUATION_DEADLINE
		+ ",'"
		+ EVALUATION_DEADLINE
		+ "','dd/MM/yyyy')\""
		+ "onmouseover=\"ddrivetip('Please enter deadline for the evaluation.')\""
		+ "onmouseout=\"hideddrivetip()\" READONLY tabindex=5> @ "
		+ "<select style=\"width: 70px;\" name=\""
		+ EVALUATION_DEADLINETIME
		+ "\" id=\""
		+ EVALUATION_DEADLINETIME
		+ "\" tabindex=6>"
		+ getTimeOptionString()
		+ "</select></td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td class=\"attribute\" >Peer feedback:</td>"
		+ "<td>"
		+ "<input type=\"radio\" name=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" id=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" value=\"true\" CHECKED "
		+ "onmouseover=\"ddrivetip('Enable this if you want students to give anonymous feedback to team members. You can moderate those peer feedback before publishing it to the team.')\""
		+ "onmouseout=\"hideddrivetip()\" >Enabled&nbsp;&nbsp;"
		+ "<input type=\"radio\" name=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" id=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" value=\"false\" "
		+ "onmouseover=\"ddrivetip('Enable this if you want students to give anonymous feedback to team members. You can moderate those peer feedback before publishing it to the team')\""
		+ "onmouseout=\"hideddrivetip()\" >Disabled"
		+ "</td>"
		+ "<td class=\"attribute\" >Time zone:</td>"
		+ "<td>"
		+ "<select style=\"width: 100px;\" name=\""
		+ EVALUATION_TIMEZONE
		+ "\" id=\""
		+ EVALUATION_TIMEZONE
		+ "\" onmouseover=\"ddrivetip('Daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is daylight saving,<br /> you should choose UTC -7:00 and its corresponding timings.')\""
		+ "onmouseout=\"hideddrivetip()\" tabindex=7>"
		+ getTimezoneOptionString()
		+ "</select>"
		+ "</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td></td>"
		+ "<td></td>"
		+ "<td class=\"attribute\" >Grace Period:</td>"
		+ "<td class=\"inputField\">"
		+ "<select style=\"width: 70px;\" name=\""
		+ EVALUATION_GRACEPERIOD
		+ "\" id=\""
		+ EVALUATION_GRACEPERIOD
		+ "\" onmouseover=\"ddrivetip('Please select the amount of time that the system will continue accepting <br />submissions after"
		+ " the specified deadline.')\" onmouseout=\"hideddrivetip()\" tabindex=7>"
		+ getGracePeriodOptionString()
		+ "</select></td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td class=\"attribute\" >Instructions to students:</td>"
		+ "<td colspan=\"3\">"
		+ "<textarea rows=\"2\" cols=\"100\" class=\"textvalue\"type=\"text\" name=\""
		+ EVALUATION_INSTRUCTIONS
		+ "\" id=\""
		+ EVALUATION_INSTRUCTIONS
		+ "\""
		+ "onmouseover=\"ddrivetip('Please enter instructions for your students, e.g. Avoid comments which are too critical.')\""
		+ "onmouseout=\"hideddrivetip()\" tabindex=8>Please submit your peer evaluation based on the overall contribution of your teammates so far.</textarea>"
		+ "</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td></td>"
		+ "<td colspan=\"3\">"
		+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\" onclick=\"doAddEvaluation(this.form."
		+ COURSE_ID
		+ ".value, "
		+ "this.form."
		+ EVALUATION_NAME
		+ ".value, this.form."
		+ EVALUATION_INSTRUCTIONS
		+ ".value, "
		+ "getCheckedValue(this.form."
		+ EVALUATION_COMMENTSENABLED
		+ "), this.form."
		+ EVALUATION_START
		+ ".value, "
		+ "this.form."
		+ EVALUATION_STARTTIME
		+ ".value, this.form."
		+ EVALUATION_DEADLINE
		+ ".value, "
		+ "this.form."
		+ EVALUATION_DEADLINETIME
		+ ".value, this.form."
		+ EVALUATION_TIMEZONE
		+ ".value, "
		+ "this.form."
		+ EVALUATION_GRACEPERIOD
		+ ".value);\" value=\"Create Evaluation\" tabindex=9 />"
		+ "</td>"
		+ "</tr>" + "</table>" + "</form>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_MANAGEMENT).innerHTML = outputForm;

	var now = new Date();
	var currentDate = convertDateToDDMMYYYY(now);

	var hours = convertDateToHHMM(now).substring(0, 2);
	var currentTime;

	if (hours.substring(0, 1) == "0") {
		currentTime = (parseInt(hours.substring(1, 2)) + 1) % 24;
	} else {
		currentTime = (parseInt(hours.substring(0, 2)) + 1) % 24;
	}
	doGetCourseIDOptions(courseID);
	var timeZone = -now.getTimezoneOffset() / 60;

	document.getElementById(EVALUATION_START).value = currentDate;
	document.getElementById(EVALUATION_STARTTIME).value = currentTime;
	document.getElementById(EVALUATION_TIMEZONE).value = timeZone;
}

/*
 * helper: print edit evaluation form TODO: Merge into printEvaluationAddForm
 */
function printEditEvaluation(courseID, name, instructions, commentsEnabled,
		start, deadline, timeZone, gracePeriod, status, activated) {
	var outputHeader = "<h1>EDIT EVALUATION</h1>";

	var startString = convertDateToDDMMYYYY(start);
	var deadlineString = convertDateToDDMMYYYY(deadline);

	isDisabled = (status == "CLOSED" || status == "OPEN") ? true : false;

	var output = "<form name=\"form_editevaluation\">"
		+ "<table class=\"headerform\">" + "<tr>"
		+ "<td class=\"fieldname\">Course ID:</td>" + "<td>" + courseID
		+ "</td></tr>" + "<tr>"
		+ "<td class=\"fieldname\">Evaluation Name:</td>" + "<td>" + name
		+ "</td></tr>";

	output = output + "<tr>" + "<td class=\"fieldname\">Opening time:</td>"
	+ "<td>" + "<input style=\"width: 100px;\" type=\"text\" name=\""
	+ EVALUATION_START + "\" id=\"" + EVALUATION_START + "\""
	+ "onClick =\"cal.select(document.forms['form_editevaluation']."
	+ EVALUATION_START + ",'" + EVALUATION_START + "','dd/MM/yyyy');\""
	+ "value=\"" + startString + "\" READONLY tabindex=1> @ "
	+ "<select style=\"width: 70px;\" name=\"" + EVALUATION_STARTTIME
	+ "\" id=\"" + EVALUATION_STARTTIME + "\" tabindex=2>"
	+ getTimeOptionString() + "</select>" + "</td></tr>" + "<tr>"
	+ "<td class=\"fieldname\">Closing time:</td>" + "<td>"
	+ "<input style=\"width: 100px;\" type=\"text\" name=\""
	+ EVALUATION_DEADLINE + "\" id=\"" + EVALUATION_DEADLINE + "\" + "
	+ "onClick =\"cal.select(document.forms['form_editevaluation']."
	+ EVALUATION_DEADLINE + ",'" + EVALUATION_DEADLINE
	+ "','dd/MM/yyyy');\"" + "value=\"" + deadlineString
	+ "\" READONLY tabindex=3> @ "
	+ "<select style=\"width: 70px;\" name=\""
	+ EVALUATION_DEADLINETIME + "\" id=\"" + EVALUATION_DEADLINETIME
	+ "\" tabindex=4>" + getTimeOptionString() + "</select>"
	+ "</td></tr>" + "<tr>"
	+ "<td class=\"fieldname\">Grace period:</td>" + "<td>"
	+ "<select style=\"width: 70px;\" name=\"" + EVALUATION_GRACEPERIOD
	+ "\" id=\"" + EVALUATION_GRACEPERIOD + "\" tabindex=5>"
	+ getGracePeriodOptionString() + "</select></td></tr>" + "<tr>";

	if (activated == true) {
		output = output + "<td class=\"fieldname\">Peer feedback:</td>"
		+ "<td>";

		if (commentsEnabled) {
			output = output + "Enabled" + "<input type=\"hidden\" name=\""
			+ EVALUATION_COMMENTSENABLED + "\" id=\""
			+ EVALUATION_COMMENTSENABLED + "\" value=\"true\">";
		} else {
			output = output + "Disabled" + "<input type=\"hidden\" name=\""
			+ EVALUATION_COMMENTSENABLED + "\" id=\""
			+ EVALUATION_COMMENTSENABLED + "\" value=\"false\">";
		}
	} else {
		output = output
		+ "<td class=\"fieldname\">Peer feedback:</td>"
		+ "<td><input type=\"radio\" name=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" id=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" value=\"true\" tabindex=6 >Enabled&nbsp;&nbsp;<input type=\"radio\" name=\""
		+ EVALUATION_COMMENTSENABLED + "\" id=\""
		+ EVALUATION_COMMENTSENABLED
		+ "\" value=\"false\" tabindex=7 >Disabled";
	}

	output = output
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Instructions:</td>"
	+ "<td><textarea rows=\"2\" cols=\"80\" class=\"textvalue\" type=\"text\" name=\""
	+ EVALUATION_INSTRUCTIONS + "\" id=\"" + EVALUATION_INSTRUCTIONS
	+ "\" tabindex=8>" + encodeCharForPrint(instructions)
	+ "</textarea>" + "</td></tr></table></form>";

	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_editevaluation\" id=\"button_editevaluation\" value=\"Save Changes\" tabindex=9 />"
		+ " <input type=\"button\" class=\"t_back button\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />"
		+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_EDITBUTTONS).innerHTML = outputButtons;
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;

	document.getElementById(EVALUATION_STARTTIME).disabled = isDisabled;
	document.getElementById(EVALUATION_START).disabled = isDisabled;

	// TODO:save changes button update database
	document.getElementById('button_editevaluation').onclick = function() {
		var editStart = document.getElementById(EVALUATION_START).value;
		var editStartTime = document.getElementById(EVALUATION_STARTTIME).value;
		var editDeadline = document.getElementById(EVALUATION_DEADLINE).value;
		var editDeadlineTime = document.getElementById(EVALUATION_DEADLINETIME).value;
		var editGracePeriod = document.getElementById(EVALUATION_GRACEPERIOD).value;
		var editInstructions = document.getElementById(EVALUATION_INSTRUCTIONS).value;
		var editCommentsEnabled = getCheckedValue(document.forms['form_editevaluation'].elements[EVALUATION_COMMENTSENABLED]);

		if (editCommentsEnabled == "") {
			editCommentsEnabled = document
			.getElementById(EVALUATION_COMMENTSENABLED).value;
		}

		doEditEvaluation(courseID, name, editStart, editStartTime,
				editDeadline, editDeadlineTime, timeZone, editGracePeriod,
				editInstructions, editCommentsEnabled, activated, status);
	};

	if (start.getMinutes() > 0) {
		document.getElementById(EVALUATION_STARTTIME).value = 24;
	} else {
		document.getElementById(EVALUATION_STARTTIME).value = start.getHours();
	}

	if (deadline.getMinutes() > 0) {
		document.getElementById(EVALUATION_DEADLINETIME).value = 24;
	} else {
		document.getElementById(EVALUATION_DEADLINETIME).value = deadline
		.getHours();
	}

	document.getElementById(EVALUATION_GRACEPERIOD).value = gracePeriod;

	if (activated == false) {
		if (commentsEnabled == true) {
			setCheckedValue(
					document.forms['form_editevaluation'].elements[EVALUATION_COMMENTSENABLED],
					true);
		} else {
			setCheckedValue(
					document.forms['form_editevaluation'].elements[EVALUATION_COMMENTSENABLED],
					false);
		}
	}
}

/*
 * helper: print evaluation list TODO: abstract print dataform
 */
function printEvaluationList(evaluationList) {
	var output;
	var evaluationListLength = evaluationList.length;

	output = "<table id=\"dataform\">"
		+ "<tr>"
		+ "<th class=\"leftalign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\">COURSE ID</input></th>"
		+ "<th class=\"leftalign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\">EVALUATION</input></th>"
		+ "<th class=\"centeralign\">STATUS</th>"
		+ "<th class=\"centeralign\"><span onmouseover=\"ddrivetip('Number of students submitted / Class size')\" onmouseout=\"hideddrivetip()\">RESPONSE RATE</span></th>"
		+ "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	// Fix for empty evaluation list
	if (evaluationListLength == 0) {
		setStatusMessage(COORDINATOR_MESSAGE_NO_EVALUATION);

		output = output + "<tr>" + "<td></td>" + "<td></td>" + "<td></td>"
		+ "<td></td>" + "<td></td>" + "</tr>";
	}

	var counter = 0;
	var evaluationStatus = "";
	for (var loop = 0; loop < evaluationListLength; loop++) {
		if (evaluationList[loop].status == "AWAITING")
			evaluationStatus = "<td class=\"t_eval_status centeralign\"><span onmouseover=\"ddrivetip('The evaluation is created but has not yet started')\" onmouseout=\"hideddrivetip()\">"
				+ evaluationList[loop].status + "</span></td>";
		if (evaluationList[loop].status == "OPEN")
			evaluationStatus = "<td class=\"t_eval_status centeralign\"><span onmouseover=\"ddrivetip('The evaluation has started and students can submit feedback until the closing time')\" onmouseout=\"hideddrivetip()\">"
				+ evaluationList[loop].status + "</span></td>";
		if (evaluationList[loop].status == "CLOSED")
			evaluationStatus = "<td class=\"t_eval_status centeralign\"><span onmouseover=\"ddrivetip('The evaluation has finished but the results are not yet sent to the students')\" onmouseout=\"hideddrivetip()\">"
				+ evaluationList[loop].status + "</span></td>";
		if (evaluationList[loop].status == "PUBLISHED")
			evaluationStatus = "<td class=\"t_eval_status centeralign\"><span onmouseover=\"ddrivetip('The evaluation has finished and the results have been sent to students')\" onmouseout=\"hideddrivetip()\">"
				+ evaluationList[loop].status + "</span></td>";

		output = output + "<tr id=\"evaluation" + loop + "\">"
		+ "<td class='t_eval_coursecode'>"
		+ encodeChar(evaluationList[loop].courseID) + "</td>"
		+ "<td class='t_eval_name'>"
		+ encodeChar(evaluationList[loop].name) + "</td>"
		+ evaluationStatus
		+ "<td class=\"t_eval_response centeralign\">"
		+ evaluationList[loop].numberOfCompletedEvaluations + " / "
		+ evaluationList[loop].numberOfEvaluations + "</td>";

		// display actions:
		output = output + "<td class=\"centeralign\">"
		+ printEvaluationActions(evaluationList, loop);
		counter++;
	}

	output = output + "</td></tr></table>" + "<br /><br />";
	document.getElementById(DIV_EVALUATION_TABLE).innerHTML = output;

	// catch actions:
	document.getElementById('button_sortcourseid').onclick = function() {
		toggleSortEvaluationsByCourseID(evaluationList);
	};
	document.getElementById('button_sortname').onclick = function() {
		toggleSortEvaluationsByName(evaluationList);
	};

	for (loop = 0; loop < evaluationListLength; loop++) {
		if (document.getElementById('editEvaluation' + loop) != null
				&& document.getElementById('editEvaluation' + loop).onclick == null) {
			document.getElementById('editEvaluation' + loop).onclick = function() {
				hideddrivetip();
				displayEditEvaluation(evaluationList, this.id.substring(14,
						this.id.length));
			};
		}
		if (document.getElementById('viewEvaluation' + loop) != null
				&& document.getElementById('viewEvaluation' + loop).onclick == null) {
			document.getElementById('viewEvaluation' + loop).onclick = function() {
				hideddrivetip();
				displayEvaluationResults(evaluationList, this.id.substring(14,
						this.id.length));
			};
		}
	}
}

/*
 * helper: print evaluation actions: 1. view 2. edit 3. remind 4. delete 5.
 * publish/unpublish
 */
function printEvaluationActions(evaluationList, position,isHome) {
	var output = "";

	// if link is disabled, insert this line to reset style and onclick:
	var disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";

	var status = evaluationList[position].status;
	var activated = evaluationList[position].activated;
	var published = evaluationList[position].published;

	// action flag:
	var hasView = false;
	var hasEdit = false;
	var hasRemind = false;
	var hasPublish = false;
	var hasUnpublish = false;

	if (status == 'AWAITING') {
		hasEdit = true;
	} else if (status == 'OPEN') {
		hasView = true;
		hasEdit = true;
		hasRemind = true;
	} else if (status == 'CLOSED') {
		hasView = true;

		// unpublished:
		if (!published) {
			hasEdit = true;
			hasPublish = true;
		}
	} else if (status == "PUBLISHED") {
		hasView = true;
		// published:
		if (published && activated) {
			hasUnpublish = true;
		}
	} else {
		hasView = true;
	}

	// 1.VIEW:
	output = output +
	"<a class='t_eval_view' name='viewEvaluation" + position + "' id='viewEvaluation"+ position + "' href=# 	\
	onmouseover=\"ddrivetip('View the current results of the evaluation')\" 									\
	onmouseout=\"hideddrivetip()\"" + (hasView ? "" : disabled) + ">View Results</a>";
	// 2.EDIT:
	output = output +
	"<a class='t_eval_edit' name='editEvaluation" + position + "' id='editEvaluation" + position + "' href=# 	\
	onmouseover=\"ddrivetip('Edit evaluation details')\" onmouseout=\"hideddrivetip()\"							\
	" + (hasEdit ? "" : disabled) + ">Edit</a>";
	// 3.DELETE:
	output = output +
	"<a class='t_eval_delete' name='deleteEvaluation" + position + "' id='deleteEvaluation" + position + "' 	\
	href=\"javascript:toggleDeleteEvaluationConfirmation('" + evaluationList[position].courseID + "','			\
	" + evaluationList[position].name + "'," + isHome + ");hideddrivetip();\"									\
	onmouseover=\"ddrivetip('Delete the evaluation')\" onmouseout=\"hideddrivetip()\">Delete</a>";
	// 4.REMIND:
	output = output +
	"<a class='t_eval_remind' name='remindEvaluation" + position + "' id='remindEvaluation" + position + "' 	\
	href=\"javascript:toggleRemindStudents('" + evaluationList[position].courseID + "','						\
	" + evaluationList[position].name + "');hideddrivetip();\"													\
	onmouseover=\"ddrivetip('Send e-mails to remind students who have not submitted their evaluations to do so')\"\
	onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : disabled) + ">Remind</a>";
	// 5. PUBLISH, UNPUBLISH:
	if (hasUnpublish) {
		output = output +
		"<a class='t_eval_unpublish' name='publishEvaluation" + position + "' id='publishEvaluation" + position + "' \
		href=\"javascript:togglePublishEvaluation('" + evaluationList[position].courseID + "','					\
		" + evaluationList[position].name + "'," + false + "," + true + "," + isHome + ");hideddrivetip();\"	\
		onmouseover=\"ddrivetip('Make results not visible to students')\" onmouseout=\"hideddrivetip()\">		\
		Unpublish</a>";
	} else {
		output = output +
		"<a class='t_eval_publish' name='unpublishEvaluation" + position + "' id='publishEvaluation" + position + "' \
		href=\"javascript:togglePublishEvaluation('" + evaluationList[position].courseID + "','					\
		" + evaluationList[position].name + "'," + true + "," + true + "," + isHome + ");hideddrivetip();\"		\
		onmouseover=\"ddrivetip('Publish evaluation results for students to view')\"							\
		onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : disabled) + ">Publish</a>";
	}

	// actions end-----------------------------------

	return output;
}




/*
 * Coordinator edit evaluation results
 */
function printEditEvaluationResultsByReviewer(submissionList, summaryList,
		position, commentsEnabled, status) {
	var fromStudent = summaryList[position].toStudent;
	var output;
	var outputTemp = "";
	var justification = "";
	var commentsToStudent = "";
	var counter = 0;

	output = "<form name=\"form_submitevaluation\" id=\"form_submitevaluation\">"
		+ "<p class=\"splinfo2\">TEAM: "
		+ encodeCharForPrint(summaryList[position].teamName)
		+ "</p><br /><br />"
		+ "<table class=\"headerform\">"
		+ "<tr style=\"display:none\">"
		+ "<td>"
		+ "<input type=\"text\" value=\""
		+ fromStudent
		+ "\" name=\""
		+ STUDENT_FROMSTUDENT
		+ 0
		+ "\" id=\""
		+ STUDENT_FROMSTUDENT
		+ 0
		+ "\">"
		+ "<input type=\"text\" value=\""
		+ fromStudent
		+ "\" name=\""
		+ STUDENT_TOSTUDENT
		+ 0
		+ "\" id=\""
		+ STUDENT_TOSTUDENT
		+ 0
		+ "\">"
		+ "</td>"
		+ "</tr>"
		+ "<tr style=\"display:none\">"
		+ "<td>"
		+ "<input type=\"text\" value=\""
		+ encodeCharForPrint(summaryList[position].teamName)
		+ "\" name=\""
		+ STUDENT_TEAMNAME
		+ 0
		+ "\" id=\""
		+ STUDENT_TEAMNAME
		+ 0
		+ "\">"
		+ "<input type=\"text\" value=\""
		+ submissionList[0].courseID
		+ "\" name=\""
		+ COURSE_ID
		+ 0
		+ "\" id=\""
		+ COURSE_ID
		+ 0
		+ "\">"
		+ "</td>"
		+ "</tr>"
		+ "<tr style=\"display:none\">"
		+ "<td>"
		+ "<input type=\"text\" value=\""
		+ submissionList[0].evaluationName
		+ "\" name=\""
		+ EVALUATION_NAME
		+ 0
		+ "\" id=\""
		+ EVALUATION_NAME
		+ 0
		+ "\">"
		+ "</td>"
		+ "</tr>"
		+

		"<tr>"
		+ "<td colspan=\"2\" class=\"reportheader\">"
		+ summaryList[position].toStudentName.toUpperCase()
		+ "'s Evaluation Submission</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td class=\"lhs\">"
		+ "Estimated contribution:"
		+ "</td>"
		+ "<td>"
		+ "<select style=\"width: 150px;\" name=\""
		+ STUDENT_POINTS
		+ 0
		+ "\" id=\""
		+ STUDENT_POINTS
		+ 0
		+ "\">"
		+ "<option value=\"200\">Equal share + 100%</option>"
		+ "<option value=\"190\">Equal share + 90%</option>"
		+ "<option value=\"180\">Equal share + 80%</option>"
		+ "<option value=\"170\">Equal share + 70%</option>"
		+ "<option value=\"160\">Equal share + 60%</option>"
		+ "<option value=\"150\">Equal share + 50%</option>"
		+ "<option value=\"140\">Equal share + 40%</option>"
		+ "<option value=\"130\">Equal share + 30%</option>"
		+ "<option value=\"120\">Equal share + 20%</option>"
		+ "<option value=\"110\">Equal share + 10%</option>"
		+ "<option value=\"100\">Equal Share</option>"
		+ "<option value=\"90\">Equal share - 10%</option>"
		+ "<option value=\"80\">Equal share - 20%</option>"
		+ "<option value=\"70\">Equal share - 30%</option>"
		+ "<option value=\"60\">Equal share - 40%</option>"
		+ "<option value=\"50\">Equal share - 50%</option>"
		+ "<option value=\"40\">Equal share - 60%</option>"
		+ "<option value=\"30\">Equal share - 70%</option>"
		+ "<option value=\"20\">Equal share - 80%</option>"
		+ "<option value=\"10\">Equal share - 90%</option>"
		+ "<option value=\"0\">0%</option>"
		+ "<option value=\"-101\">Not Sure</option>"
		+ "<option value=\"-999\" SELECTED>N/A</option>"
		+ "</select>"
		+ "</td>" + "</tr>";

	var submissionListLength = submissionList.length;
	for (var loop = 0; loop < submissionListLength; loop++) {
		if (submissionList[loop].fromStudent == fromStudent) {
			// Extract data: points
			points = helpPrintPoints(submissionList[loop]);
			justification = helpPrintJustification(submissionList[loop]);
			commentsToStudent = helpPrintComments(submissionList[loop],
					commentsEnabled);

			// Print data: reviewer self-evaluation
			if (submissionList[loop].fromStudent == submissionList[loop].toStudent) {
				if (commentsToStudent != "Disabled") {
					outputTemp = ""
						+ "<tr>"
						+ "<td class=\"lhs\">"
						+ "Comments about your contribution:"
						+ "</td>"
						+ "<td>"
						+ "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\""
						+ STUDENT_JUSTIFICATION
						+ 0
						+ "\" id=\""
						+ STUDENT_JUSTIFICATION
						+ 0
						+ "\">"
						+ encodeCharForPrint(justification)
						+ "</textarea>"
						+ "</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td class=\"lhs\">"
						+ "Comments about team dynamics:<br />(confidential)"
						+ "</td>"
						+ "<td>"
						+ "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\""
						+ STUDENT_COMMENTSTOSTUDENT
						+ 0
						+ "\" id=\""
						+ STUDENT_COMMENTSTOSTUDENT
						+ 0
						+ "\">"
						+ encodeCharForPrint(commentsToStudent)
						+ "</textarea>" + "</td>" + "</tr>" + outputTemp;
				}

				else {
					outputTemp = ""
						+ "<tr>"
						+ "<td class=\"lhs\">"
						+ "Comments about your contribution:"
						+ "</td>"
						+ "<td>"
						+ "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\""
						+ STUDENT_JUSTIFICATION
						+ 0
						+ "\" id=\""
						+ STUDENT_JUSTIFICATION
						+ 0
						+ "\">"
						+ encodeCharForPrint(justification)
						+ "</textarea>"
						+ "</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td class=\"lhs\">"
						+ "Comments about team dynamics:<br />(confidential)"
						+ "</td>"
						+ "<td>"
						+ "<textarea class=\"textvalue\" rows=\"2\" cols=\"100\" name=\""
						+ STUDENT_COMMENTSTOSTUDENT
						+ 0
						+ "\" id=\""
						+ STUDENT_COMMENTSTOSTUDENT
						+ 0
						+ "\" disabled=\"disabled\">"
						+ "Disabled"
						+ "</textarea>" + "</td>" + "</tr>" + outputTemp;
				}
			}
			// Print data: evaluations to other team members
			else {
				outputTemp = outputTemp
				+ "<tr style=\"display:none\">"
				+ "<td>"
				+ "<input type=\"text\" value=\""
				+ submissionList[loop].fromStudent
				+ "\" name=\""
				+ STUDENT_FROMSTUDENT
				+ counter
				+ "\" id=\""
				+ STUDENT_FROMSTUDENT
				+ counter
				+ "\">"
				+ "<input type=\"text\" value=\""
				+ submissionList[loop].toStudent
				+ "\" name=\""
				+ STUDENT_TOSTUDENT
				+ counter
				+ "\" id=\""
				+ STUDENT_TOSTUDENT
				+ counter
				+ "\">"
				+ "</td>"
				+ "</tr>"
				+ "<tr style=\"display:none\">"
				+ "<td>"
				+ "<input type=\"text\" value=\""
				+ encodeCharForPrint(summaryList[position].teamName)
				+ "\" name=\""
				+ STUDENT_TEAMNAME
				+ counter
				+ "\" id=\""
				+ STUDENT_TEAMNAME
				+ counter
				+ "\">"
				+ "<input type=\"text\" value=\""
				+ submissionList[0].courseID
				+ "\" name=\""
				+ COURSE_ID
				+ counter
				+ "\" id=\""
				+ COURSE_ID
				+ counter
				+ "\">"
				+ "</td>"
				+ "</tr>"
				+ "<tr style=\"display:none\">"
				+ "<td>"
				+ "<input type=\"text\" value=\""
				+ submissionList[0].evaluationName
				+ "\" name=\""
				+ EVALUATION_NAME
				+ counter
				+ "\" id=\""
				+ EVALUATION_NAME
				+ counter
				+ "\">"
				+ "</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td colspan=\"2\" class=\"reportheader\">Evaluation To "
				+ submissionList[loop].toStudentName.toUpperCase()
				+ "</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td class=\"lhs\">"
				+ "Estimated contribution:"
				+ "</td>"
				+ "<td>"
				+ "<select style=\"width: 150px;\" name=\""
				+ STUDENT_POINTS
				+ counter
				+ "\" id=\""
				+ STUDENT_POINTS
				+ counter
				+ "\">"
				+ "<option value=\"200\">Equal share + 100%</option>"
				+ "<option value=\"190\">Equal share + 90%</option>"
				+ "<option value=\"180\">Equal share + 80%</option>"
				+ "<option value=\"170\">Equal share + 70%</option>"
				+ "<option value=\"160\">Equal share + 60%</option>"
				+ "<option value=\"150\">Equal share + 50%</option>"
				+ "<option value=\"140\">Equal share + 40%</option>"
				+ "<option value=\"130\">Equal share + 30%</option>"
				+ "<option value=\"120\">Equal share + 20%</option>"
				+ "<option value=\"110\">Equal share + 10%</option>"
				+ "<option value=\"100\">Equal Share</option>"
				+ "<option value=\"90\">Equal share - 10%</option>"
				+ "<option value=\"80\">Equal share - 20%</option>"
				+ "<option value=\"70\">Equal share - 30%</option>"
				+ "<option value=\"60\">Equal share - 40%</option>"
				+ "<option value=\"50\">Equal share - 50%</option>"
				+ "<option value=\"40\">Equal share - 60%</option>"
				+ "<option value=\"30\">Equal share - 70%</option>"
				+ "<option value=\"20\">Equal share - 80%</option>"
				+ "<option value=\"10\">Equal share - 90%</option>"
				+ "<option value=\"0\">0%</option>"
				+ "<option value=\"-101\">Not Sure</option>"
				+ "<option value=\"-999\" SELECTED>N/A</option>"
				+ "</select>"
				+ "</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td class=\"lhs\">"
				+ "Comments about this teammate:<br />(not shown to the teammate)"
				+ "</td>"
				+ "<td>"
				+ "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\""
				+ STUDENT_JUSTIFICATION
				+ counter
				+ "\" id=\""
				+ STUDENT_JUSTIFICATION
				+ counter
				+ "\">"
				+ encodeCharForPrint(justification)
				+ "</textarea>"
				+ "</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td class=\"lhs\">"
				+ "Message to this teammate:<br />(shown anonymously to the teammate)"
				+ "</td>" + "<td>";

				if (commentsToStudent != "Disabled") {
					outputTemp = outputTemp
					+ "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\""
					+ STUDENT_COMMENTSTOSTUDENT + counter + "\" id=\""
					+ STUDENT_COMMENTSTOSTUDENT + counter + "\">"
					+ encodeCharForPrint(commentsToStudent)
					+ "</textarea>" + "</td>" + "</tr>";
				} else {
					outputTemp = outputTemp
					+ "<textarea class=\"textvalue\" rows=\"2\" cols=\"100\" name=\""
					+ STUDENT_COMMENTSTOSTUDENT + counter + "\" id=\""
					+ STUDENT_COMMENTSTOSTUDENT + counter
					+ "\" disabled=\"disabled\">" + "Disabled"
					+ "</textarea>" + "</td>" + "</tr>";
				}
			}
			counter++;
		}
	}

	output = output + outputTemp + "</table></form><br /><br />";

	var outputButtons = "<input type=\"button\" class =\"button\" name=\"button_editevaluationresultsbyreviewee\" id=\"button_editevaluationresultsbyreviewee\" value=\"Submit\" />"
		+ " <input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Cancel\" />"
		+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = outputButtons;

	populateEditEvaluationResultsPointsForm(document.forms[0], submissionList);
	document.getElementById('button_editevaluationresultsbyreviewee').onclick = function() {
		doEditEvaluationResultsByReviewer(document.forms[0], summaryList,
				position, commentsEnabled, status);
	};

	document.getElementById('button_back').onclick = function() {
		document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
		printEvaluationIndividualForm(submissionList, summaryList, position,
				commentsEnabled, status, REVIEWER);
	};
}

/*------------------------------------------PRINT STUDENT PAGE------------------------------------------*/

/*
 * Student view course info
 */
function printCourseStudentForm(course) {
	var outputHeader = "<h1>TEAM DETAIL FOR "+course.courseID+"</h1>";

	var output = "<table width=\"600\" class=\"detailform\">" + "<tr>"
	+ "<td>Course ID:</td>" + "<td>"
	+ course.courseID
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td>Your team:</td>"
	+ "<td>"
	+ encodeCharForPrint(course.studentTeamName)
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td>Course name:</td>"
	+ "<td>"
	+ course.courseName
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td>Your name:</td>"
	+ "<td>"
	+ course.studentName
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td>Coordinator name:</td>"
	+ "<td>"
	+ course.coordinatorName
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td>Your e-mail:</td>"
	+ "<td>"
	+ course.studentEmail
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td>Your teammates:</td>" + "<td>";

	var courseTeammateListLength = course.teammateList.length;
	if (courseTeammateListLength == 0) {
		output = output + "Nil";
	}

	else {
		for ( var x = 0; x < courseTeammateListLength; x++) {
			output = output + (x + 1) + ") " + course.teammateList[x]
			+ "<br /><br />";
		}
	}

	output = output
	+ "</td>"
	+ "</tr>"
	+ "</table>"
	+ "<br /><br />"
	+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayCoursesTab();\" value=\"Back\" />"
	+ "<br /><br />";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = output;

}

/*
 * Student view pending evaluation list
 */
function printPendingEvaluationList(evaluationList) {
	var evaluationListLength = evaluationList.length;

	var outputHeader = "<h1>EVALUATIONS</h1>";
	var output = "";

	// No pending evaluation
	if (evaluationListLength == 0) {
		output = output + "No pending evaluations.<br /><br />";
	}
	// List pending evaluations
	else {
		output = output + "<table id=\"dataform\">" + "<p>"
		+ EVALUATION_PENDING + "<p>" + "<thead>" + "<th>Course ID</th>"
		+ "<th>Evaluation Name</th>"
		+ "<th class='centeralign'>Deadline</th>"
		+ "<th class='centeralign'>Action</th>" + "</thead>";

		for (var x = 0; x < evaluationListLength; x++) {
			output = output + "<tr>" + "<td>"
			+ encodeChar(evaluationList[x].courseID) + "</td>" + "<td>"
			+ encodeChar(evaluationList[x].name) + "</td>"
			+ "<td class='centeralign'>"
			+ convertDateToDDMMYYYY(evaluationList[x].deadline) + " "
			+ convertDateToHHMM(evaluationList[x].deadline) + "H</td>"
			+ "<td class='centeralign'>" + "<a id=\"doEvaluation" + x
			+ "\" href=\"#\""
			+ " onmouseover=\"ddrivetip('Start Evaluation')\""
			+ " onmouseout=\"hideddrivetip()\">Do Evaluation</a>"
			+ "</td>" + "</tr>";
		}
	}
	output = output + "</table>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_PENDING).innerHTML = output;

	for (var loop = 0; loop < evaluationListLength; loop++) {
		document.getElementById('doEvaluation' + loop).onclick = function() {
			displayEvaluationSubmission(evaluationList, this.id.substring(
					12, this.id.length));
		};

	}
}

/*
 * Student view past evaluation list
 */
function printPastEvaluationList(evaluationList) {
	var output = "";
	var status;
	var now = new Date();
	var evaluationListLength = evaluationList.length;

	output = output
	+ "<br />"
	+ "<p>"
	+ EVALUATION_PAST
	+ "</p>"
	+ "<table id=\"dataform\">"
	+ "<tr>"
	+ "<th><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\">COURSE ID</input></th>"
	+ "<th><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\">EVALUATION</input></th>"
	+ "<th class='centeralign'>DEADLINE</th>"
	+ "<th class='centeralign'>STATUS</th>"
	+ "<th class='centeralign'>ACTION(S)</th>" + "</tr>";

	// if link is disabled, insert this line to reset style and onclick:
	var disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";

	for (var loop = 0; loop < evaluationListLength; loop++) {
		output = output + "<tr>";
		output = output + "<td>" + encodeChar(evaluationList[loop].courseID)
		+ "</td>";
		output = output + "<td>" + encodeChar(evaluationList[loop].name)
		+ "</td>";
		output = output + "<td class='centeralign'>"
		+ convertDateToDDMMYYYY(evaluationList[loop].deadline) + " "
		+ convertDateToHHMM(evaluationList[loop].deadline) + "H</td>";

		var hasView = false;
		var hasEdit = false;

		now = getDateWithTimeZoneOffset(evaluationList[loop].timeZone);
		if (evaluationList[loop].published == true) {
			status = "PUBLISHED";
			hasView = true;
			output = output
			+ "<td class=\"centeralign t_eval_status t_eval_status_"
			+ loop
			+ "\"><span onmouseover=\"ddrivetip('The evaluation has finished and you can check the results')\" onmouseout=\"hideddrivetip()\">"
			+ status + "</span></td>";
		}

		else if (now < evaluationList[loop].deadline) {
			status = "SUBMITTED";
			output = output
			+ "<td class=\"centeralign t_eval_status t_eval_status_"
			+ loop
			+ "\"><span onmouseover=\"ddrivetip('You have submitted your feedback for this evaluation')\" onmouseout=\"hideddrivetip()\">"
			+ status + "</span></td>";
		}

		else {
			status = "CLOSED";
			output = output
			+ "<td class=\"centeralign t_eval_status t_eval_status_"
			+ loop
			+ "\"><span onmouseover=\"ddrivetip('The evaluation has finished but the coordinator has not published the results yet')\" onmouseout=\"hideddrivetip()\">"
			+ status + "</span></td>";
		}

		output = output
		+ "<td class=\"centeralign\">"
		+ "<a href=# \"name=\"viewEvaluation"
		+ loop + "\" id=\"viewEvaluation" + loop + "\""
		+ "onmouseover=\"ddrivetip('View evaluation results')\""
		+ "onmouseout=\"hideddrivetip()\"" + (hasView ? "" : disabled)
		+ ">View Results</a>";

		if (!(now > evaluationList[loop].deadline)) {
			hasEdit = true;
		}

		output = output
		+ "<a href=# name=\"editEvaluation"
		+ loop + "\" id=\"editEvaluation" + loop + "\""
		+ "onmouseover=\"ddrivetip('Edit evaluation')\""
		+ "onmouseout=\"hideddrivetip()\"" + (hasEdit ? "" : disabled)
		+ ">Edit</a>";

		output = output + "</td></tr>";
	}

	output = output + "</table><br /><br />";

	if (evaluationListLength == 0) {
		output = output + "<br />No records found.<br /><br />";
	}

	document.getElementById(DIV_EVALUATION_PAST).innerHTML = output;
	document.getElementById('button_sortcourseid').onclick = function() {
		toggleSortPastEvaluationsByCourseID(evaluationList);
	};
	document.getElementById('button_sortname').onclick = function() {
		toggleSortPastEvaluationsByName(evaluationList);
	};

	for (loop = 0; loop < evaluationListLength; loop++) {
		if (document.getElementById('editEvaluation' + loop) != null
				&& document.getElementById('editEvaluation' + loop).onclick == null) {
			document.getElementById('editEvaluation' + loop).onclick = function() {
				hideddrivetip();
				displayEvaluationSubmission(evaluationList, this.id.substring(
						14, this.id.length));
			};
		}

		if (document.getElementById('viewEvaluation' + loop) != null
				&& document.getElementById('viewEvaluation' + loop).onclick == null) {
			document.getElementById('viewEvaluation' + loop).onclick = function() {
				hideddrivetip();
				displayEvaluationResults(evaluationList, this.id.substring(14,
						this.id.length));
			};
		}
	}
}

/*
 * Print evaluation header User type: Student && Coordinator
 */
function printEvaluationHeader(courseID, evaluationName, start, deadline,
		gracePeriod, instructions) {
	var outputHeader = "<h1>EVALUATION RESULTS</h1>";

	var output = "" + "<table class=\"headerform\">" + "<tr>"
	+ "<td class=\"fieldname\">Course ID:</td>" + "<td>"
	+ encodeChar(courseID)
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Evaluation name:</td>"
	+ "<td>"
	+ encodeChar(evaluationName)
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Opening time:</td>"
	+ "<td>"
	+ convertDateToDDMMYYYY(new Date(start))
	+ " "
	+ convertDateToHHMM(new Date(start))
	+ "H</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Closing time:</td>"
	+ "<td>"
	+ convertDateToDDMMYYYY(new Date(deadline))
	+ " "
	+ convertDateToHHMM(new Date(deadline))
	+ "H</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Instructions:</td>"
	+ "<td>"
	+ encodeCharForPrint(instructions) + "</td>" + "</tr>" + "</table>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output;
}

/*
 * Student do evaluation Student edit evaluation submission TODO: Improve UI
 */
function printSubmissionForm(submissionList, commentsEnabled) {
	var output = "<form name=\"form_submitevaluation\" id=\"form_submitevaluation\">"
		+ "<table class=\"headerform\">";

	output = output
	+ "<tr style=\"display:none\"><td>"
	+ "<input type=\"text\" value=\""
	+ submissionList[0].fromStudent
	+ "\" name=\""
	+ STUDENT_FROMSTUDENT
	+ loop
	+ "\" id=\""
	+ STUDENT_FROMSTUDENT
	+ loop
	+ "\">"
	+ "<input type=\"text\" value=\""
	+ submissionList[0].toStudent
	+ "\" name=\""
	+ STUDENT_TOSTUDENT
	+ loop
	+ "\" id=\""
	+ STUDENT_TOSTUDENT
	+ loop
	+ "\">"
	+ "</td></tr>"
	+ "<tr style=\"display:none\"><td>"
	+ "<input type=\"text\" value=\""
	+ submissionList[0].courseID
	+ "\" name=\""
	+ COURSE_ID
	+ loop
	+ "\" id=\""
	+ COURSE_ID
	+ loop
	+ "\">"
	+ "<input type=\"text\" value=\""
	+ submissionList[0].evaluationName
	+ "\" name=\""
	+ EVALUATION_NAME
	+ loop
	+ "\" id=\""
	+ EVALUATION_NAME
	+ loop
	+ "\">"
	+ "</td></tr>"
	+ "<tr style=\"display:none\"><td>"
	+ "<input type=\"text\" value=\""
	+ encodeCharForPrint(submissionList[0].teamName)
	+ "\" name=\""
	+ STUDENT_TEAMNAME
	+ loop
	+ "\" id=\""
	+ STUDENT_TEAMNAME
	+ loop
	+ "\">"
	+ "</td></tr>"
	+ "<tr>"
	+ "<td class=\"reportheader\" colspan=\"2\">Self evaluation in Team ["
	+ encodeCharForPrint(submissionList[0].teamName)
	+ "]</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"lhs\">Estimated contribution:</td>"
	+ "<td><select style=\"width: 150px\" name=\""
	+ STUDENT_POINTS
	+ 0
	+ "\" id=\""
	+ STUDENT_POINTS
	+ 0
	+ "\">"
	+ getEvaluationOptionString()
	+ "</select></td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"lhs\">Comments about your contribution:</td>"
	+ "<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\""
	+ STUDENT_JUSTIFICATION + 0 + "\" id=\"" + STUDENT_JUSTIFICATION
	+ 0 + "\"></textarea></td>" + "</tr>" + "<tr>"
	+ "<td class=\"lhs\">"
	+ "Comments about team dynamics:<br />(confidential)" + "</td>";

	if (commentsEnabled == true) {
		output = output
		+ "<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\""
		+ STUDENT_COMMENTSTOSTUDENT + 0 + "\" id=\""
		+ STUDENT_COMMENTSTOSTUDENT + 0 + "\"></textarea></td>"
		+ "</tr>" + "<tr>" + "<td colspan=\"2\"></td>" + "</tr>";
	} else {
		output = output + "<td>" + getDisabledString() + "</td>" + "</tr>"
		+ "<tr>" + "<td colspan=\"2\"></td>" + "</tr>";
	}

	var submissionListLength = submissionList.length;
	for (var loop = 1; loop < submissionListLength; loop++) {
		output = output + "<tr style=\"display:none\"><td>"
		+ "<input type=text value=\""
		+ submissionList[loop].fromStudent + "\" name=\""
		+ STUDENT_FROMSTUDENT + loop + "\" id=\"" + STUDENT_FROMSTUDENT
		+ loop + "\">" + "<input type=text value=\""
		+ submissionList[loop].toStudent + "\" name=\""
		+ STUDENT_TOSTUDENT + loop + "\" id=\"" + STUDENT_TOSTUDENT
		+ loop + "\">" + "</td></tr>"
		+ "<tr style=\"display:none\"><td>"
		+ "<input type=\"text\" value=\"" + submissionList[0].courseID
		+ "\" name=\"" + COURSE_ID + loop + "\" id=\"" + COURSE_ID
		+ loop + "\">" + "<input type=\"text\" value=\""
		+ submissionList[0].evaluationName + "\" name=\""
		+ EVALUATION_NAME + loop + "\" id=\"" + EVALUATION_NAME + loop
		+ "\">" + "</td></tr>" + "<tr style=\"display:none\"><td>"
		+ "<input type=\"text\" value=\""
		+ encodeCharForPrint(submissionList[0].teamName) + "\" name=\""
		+ STUDENT_TEAMNAME + loop + "\" id=\"" + STUDENT_TEAMNAME
		+ loop + "\">" + "</td></tr>" + "<tr>"
		+ "<td class=\"reportheader\" colspan=\"2\">Evaluation for "
		+ submissionList[loop].toStudentName + "</td>" + "</tr>"
		+ "<tr>" + "<td class=\"lhs\">Estimated contribution:</td>"
		+ "<td><select style=\"width: 150px\" name=\"" + STUDENT_POINTS
		+ 0 + "\" id=\"";

		if (commentsEnabled == true) {
			output = output + STUDENT_POINTS + loop /*
			 * huy change from 0 to loop
			 */
			+ "\" >" + getEvaluationOptionString();
		} else {
			output = output + STUDENT_POINTS + 0 + "\" >"
			+ getEvaluationOptionString();
		}

		output = output
		+ "</select></td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td class=\"lhs\">Confidential comments about this teammate:<br />(not shown to the teammate)</td>"
		+ "<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\""
		+ STUDENT_JUSTIFICATION + loop + "\" id=\""
		+ STUDENT_JUSTIFICATION + loop + "\"></textarea></td>"
		+ "</tr>" + "<tr>";

		if (commentsEnabled == true) {
			output = output
			+ "<td class=\"lhs\">Your feedback to this teammate:<br />(shown anonymously to the teammate)</td>"
			+ "<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\""
			+ STUDENT_COMMENTSTOSTUDENT + loop + "\" id=\""
			+ STUDENT_COMMENTSTOSTUDENT + loop + "\"></textarea></td>"
			+ "</tr>";
		} else {
			output = output
			+ "<tr><td>Your feedback to this teammate:<br/>(shown anonymously to the teammate)</td>"
			+ "<td>" + getDisabledString() + "</td>" + "</tr>";
		}

		if (loop != submissionListLength - 1) {
			output = output + "<tr>" + "<td colspan=\"2\"></td>" + "</tr>";
		}
	}

	output = output + "</table>" + "</form>" + "<br />";

	outputButtons = "<input type=\"button\" class=\"button\" name=\"submitEvaluation\" id=\"submitEvaluation\" value=\"Submit Evaluation\" />"
		+ " <input type=\"button\" class=\"button t_back\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />"
		+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_SUBMISSIONS).innerHTML = output;
	document.getElementById(DIV_EVALUATION_SUBMISSIONBUTTONS).innerHTML = outputButtons;

	populateEvaluationSubmissionForm(document.forms[0], submissionList,
			commentsEnabled);

	document.getElementById("submitEvaluation").onclick = function() {
		doSubmitEvaluation(document.forms[0], submissionListLength,
				commentsEnabled);
	};
}

/*--------------------------PRINT FUNCTION HELPER---------------------------*/
function helpPrintResultTeam(teamName) {
	var output = "<div class=\"result_team\">" + "<p>"
	+ encodeCharForPrint(teamName) + "</p>";

	return output;
}
function helpPrintResultHeader(type, name, claimedPoints, perceivedPoints) {
	console.log("header :" + type + " " + name + " " + claimedPoints + " "
			+ perceivedPoints);

	var output = "<br /><table class=\"result_table\">" + "<thead>"
	+ "<th colspan=\"2\" width=\"10%\"><span class=\"fontcolor\">"
	+ type + ": </span>" + name + "</th>"
	+ "<th><span class=\"fontcolor\" onmouseover=\"ddrivetip('"
	+ HOVER_MESSAGE_CLAIMED + "')\" onmouseout=\"hideddrivetip('')\">"
	+ CLAIMED + ":</span> " + claimedPoints + "</th>"
	+ "<th><span class=\"fontcolor\" onmouseover=\"ddrivetip('"
	+ HOVER_MESSAGE_PERCEIVED
	+ "')\" onmouseout=\"hideddrivetip('')\">" + PERCEIVED
	+ ": </span> " + perceivedPoints + "</th>" + "</thead>";
	return output;
}
function helpPrintResultSelfComments(justification, commentsToStudent) {
	var output = "<tr>" + "<td colspan=\"4\"><b>Self evaluation:</b><br />"
	+ encodeCharForPrint(justification) + "</td>" + "</tr>" + "<tr>"
	+ "<td colspan=\"4\"><b>Comments about team:</b><br />"
	+ encodeCharForPrint(commentsToStudent) + "</td>" + "</tr>";
	return output;
}

function helpPrintResultOtherComments(student, points, justification,
		commentsToStudent) {
	var output = "<tr>" + "<td><b>" + student + "</b></td>";
	// ws
	var idx = points.indexOf("-");
	if (idx == 0) {
		idx = points.indexOf("+");
	}
	if (idx > 0) {
		points = points.slice(0, idx) + "<br />" + points.slice(idx);
	}
	output = output + "<td>" + points + "</td>";

	output = output + "<td>" + encodeCharForPrint(justification) + "</td>"
	+ "<td>" + encodeCharForPrint(commentsToStudent) + "</td>"
	+ "</tr>";
	return output;
}

function helpPrintResultSubheader(type) {
	var output = "<tr class='result_subheader'>" +
	"<td width='15%'>" + type + "</td>" +
	"<td width='5%'>" + CONTRIBUTION + "</td>" +
	"<td width='40%'>" + COMMENTS + "</td>" +
	"<td width='40%'>" + MESSAGES + "</td>" +
	"</tr>";
	return output;
}

function helpPrintPoints(submission) {
	var points;
	if (submission.points == -999) {
		points = NA;
	} else if (submission.points == -101) {
		points = NOTSURE;
	} else {
		points = displayEvaluationPoints(Math.round(submission.points * submission.pointsBumpRatio));
	}

	return points;

}
function displayEvaluationPoints(points) {

	delta = null;

	if (points > 100) {
		delta = points - 100;
		return "Equal Share<span class=\"color_positive\"> + " + delta + "%</span>";
	} else if (points == -999) {
		return NA;
	} else if (points == -101) {
		console.log("not sure");
		return NOTSURE;
	} else if (points == -100) {
		return "0%";
	} else if (points < 100) {
		delta = 100 - points;
		return "Equal Share<span class=\"color_negative\"> - " + delta + "%</span>";
	} else if (points == 100) {
		return "Equal Share";
	} else {
		return points;
	}
}
function helpPrintJustification(submission) {
	var justification;
	if (submission.justification == "") {
		justification = "N/A";
	} else {
		justification = submission.justification;
	}

	return justification;

}
function helpPrintComments(submission, commentsEnabled) {
	var commentsToStudent;
	if (commentsEnabled == true) {
		if (submission.commentsToStudent == "") {
			commentsToStudent = "N/A";
		} else {
			commentsToStudent = submission.commentsToStudent;
		}
	} else {
		commentsToStudent = "Disabled";
	}

	return commentsToStudent;
}
function helpPrintSubmission(submissionList, summaryList, position, status, commentsEnabled, type) {
	var output = "";
	var toStudent = summaryList[position].toStudent;
	// points:
	output = output
	+ helpPrintResultHeader(
			type,
			summaryList[position].toStudentName,
			displayEvaluationPoints(summaryList[position].claimedPoints),
			displayEvaluationPoints(summaryList[position].average));

	console.log("points:" + summaryList[position].toStudentName + "|"
			+ summaryList[position].claimedPoints + "|"
			+ summaryList[position].average);

	// evaluation to others header:
	student = (type == REVIEWEE) ? FROM_STUDENT : TO_STUDENT;
	outputTemp = helpPrintResultSubheader(student);

	// justification and comments:
	var submissionListLength = submissionList.length;
	for (var loop = 0; loop < submissionListLength; loop++) {
		if ((type == REVIEWEE && submissionList[loop].toStudent == toStudent)
				|| (type == REVIEWER && submissionList[loop].fromStudent == toStudent)) {
			// Extract data
			points = helpPrintPoints(submissionList[loop]);
			justification = helpPrintJustification(submissionList[loop]);
			commentsToStudent = helpPrintComments(submissionList[loop],
					commentsEnabled);

			// Print data
			if (submissionList[loop].fromStudent == submissionList[loop].toStudent) {
				outputTemp = helpPrintResultSelfComments(justification,
						commentsToStudent)
						+ outputTemp;

			} else {
				student = (type == REVIEWEE) ? submissionList[loop].fromStudentName
						: submissionList[loop].toStudentName;
				outputTemp = outputTemp
				+ helpPrintResultOtherComments(student, points,
						justification, commentsToStudent);
			}

		}
	}

	output = output + outputTemp + "</table>";

	return output;
}

/*------------------------------------------OTHERS------------------------------------------*/

function getDisabledString() {
	return "<font color=\"red\"><textarea class = \"textvalue\" type=\"text\" rows=\"1\" cols=\"100\" name=\""
	+ STUDENT_JUSTIFICATION
	+ 0
	+ "\" id=\""
	+ STUDENT_JUSTIFICATION
	+ 0 + "\" disabled=\"true\">" + "N.A.</textarea></font>";
}

function getEvaluationOptionString() {
	return "<option value=\"200\">Equal share + 100%</option>"
	+ "<option value=\"190\">Equal share + 90%</option>"
	+ "<option value=\"180\">Equal share + 80%</option>"
	+ "<option value=\"170\">Equal share + 70%</option>"
	+ "<option value=\"160\">Equal share + 60%</option>"
	+ "<option value=\"150\">Equal share + 50%</option>"
	+ "<option value=\"140\">Equal share + 40%</option>"
	+ "<option value=\"130\">Equal share + 30%</option>"
	+ "<option value=\"120\">Equal share + 20%</option>"
	+ "<option value=\"110\">Equal share + 10%</option>"
	+ "<option value=\"100\" SELECTED>Equal Share</option>"
	+ "<option value=\"90\">Equal share - 10%</option>"
	+ "<option value=\"80\">Equal share - 20%</option>"
	+ "<option value=\"70\">Equal share - 30%</option>"
	+ "<option value=\"60\">Equal share - 40%</option>"
	+ "<option value=\"50\">Equal share - 50%</option>"
	+ "<option value=\"40\">Equal share - 60%</option>"
	+ "<option value=\"30\">Equal share - 70%</option>"
	+ "<option value=\"20\">Equal share - 80%</option>"
	+ "<option value=\"10\">Equal share - 90%</option>"
	+ "<option value=\"0\">0%</option>"
	+ "<option value=\"-101\">Not Sure</option>";
}

function getTimeOptionString() {
	return "<option value=\"1\">0100H</option>"
	+ "<option value=\"2\">0200H</option>"
	+ "<option value=\"3\">0300H</option>"
	+ "<option value=\"4\">0400H</option>"
	+ "<option value=\"5\">0500H</option>"
	+ "<option value=\"6\">0600H</option>"
	+ "<option value=\"7\">0700H</option>"
	+ "<option value=\"8\">0800H</option>"
	+ "<option value=\"9\">0900H</option>"
	+ "<option value=\"10\">1000H</option>"
	+ "<option value=\"11\">1100H</option>"
	+ "<option value=\"12\">1200H</option>"
	+ "<option value=\"13\">1300H</option>"
	+ "<option value=\"14\">1400H</option>"
	+ "<option value=\"15\">1500H</option>"
	+ "<option value=\"16\">1600H</option>"
	+ "<option value=\"17\">1700H</option>"
	+ "<option value=\"18\">1800H</option>"
	+ "<option value=\"19\">1900H</option>"
	+ "<option value=\"20\">2000H</option>"
	+ "<option value=\"21\">2100H</option>"
	+ "<option value=\"22\">2200H</option>"
	+ "<option value=\"23\">2300H</option>"
	+ "<option value=\"24\" SELECTED>2359H</option>";
}

function getTimezoneOptionString() {
	return "<option value=\"-12\">UTC -12:00</option>"
	+ "<option value=\"-11\">UTC -11:00</option>"
	+ "<option value=\"-10\">UTC -10:00</option>"
	+ "<option value=\"-9\">UTC -09:00</option>"
	+ "<option value=\"-8\">UTC -08:00</option>"
	+ "<option value=\"-7\">UTC -07:00</option>"
	+ "<option value=\"-6\">UTC -06:00</option>"
	+ "<option value=\"-5\">UTC -05:00</option>"
	+ "<option value=\"-4.5\">UTC -04:30</option>"
	+ "<option value=\"-4\">UTC -04:00</option>"
	+ "<option value=\"-3.5\">UTC -03:30</option>"
	+ "<option value=\"-3\">UTC -03:00</option>"
	+ "<option value=\"-2\">UTC -02:00</option>"
	+ "<option value=\"-1\">UTC -01:00</option>"
	+ "<option value=\"0\">UTC </option>"
	+ "<option value=\"1\">UTC +01:00</option>"
	+ "<option value=\"2\">UTC +02:00</option>"
	+ "<option value=\"3\">UTC +03:00</option>"
	+ "<option value=\"3.5\">UTC +03:30</option>"
	+ "<option value=\"4\">UTC +04:00</option>"
	+ "<option value=\"4.5\">UTC +04:30</option>"
	+ "<option value=\"5\">UTC +05:00</option>"
	+ "<option value=\"5.5\">UTC +05:30</option>"
	+ "<option value=\"5.75\">UTC +05:45</option>"
	+ "<option value=\"6\">UTC +06:00</option>"
	+ "<option value=\"6.5\">UTC +06:30</option>"
	+ "<option value=\"7\">UTC +07:00</option>"
	+ "<option value=\"8\">UTC +08:00</option>"
	+ "<option value=\"9\">UTC +09:00</option>"
	+ "<option value=\"9.5\">UTC +09:30</option>"
	+ "<option value=\"10\">UTC +10:00</option>"
	+ "<option value=\"11\">UTC +11:00</option>"
	+ "<option value=\"12\">UTC +12:00</option>"
	+ "<option value=\"13\">UTC +13:00</option>";
}

function getGracePeriodOptionString() {
	return "<option value=\"5\">5 min</option>"
	+ "<option value=\"10\">10 min</option>"
	+ "<option value=\"15\">15 min</option>"
	+ "<option value=\"20\">20 min</option>"
	+ "<option value=\"25\">25 min</option>"
	+ "<option value=\"30\">30 min</option>";
}

function isEvaluationNameLengthValid(name) {
	return name.length<=22;
}

function isEvaluationNameValid(name) {
	if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
			|| name.indexOf("\"") >= 0) {
		return false;
	}
	if (name.match(/^[a-zA-Z0-9 ]*$/) == null) {
		return false;
	}
	return true;
}

function isStudentEmailValid(email) {
	return email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i)!=null;
}

function isStudentNameValid(name) {
	if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
			|| name.indexOf("\"") >= 0) {
		return false;
	} else if (name.match(/^.[^\t]*$/) == null) {
		return false;
	} else if (name.length > 40) {
		return false;
	}
	return true;
}

function isStudentTeamNameValid(teamName) {
	return teamName.length<=24;
}

function getXMLObject() {
	var xmlHttp = null;
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			xmlHttp = null;
		}
	}
	if (xmlHttp===null) {
		xmlHttp = new XMLHttpRequest();
	}
	return xmlHttp;
}
