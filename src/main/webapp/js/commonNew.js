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

//RESPONSE TAG
var TAG_COORDINATOR_COURSE = "courses";

//user type
var STUDENT = "Student";
var COORDINATOR = "Coordinator";

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
var OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES = "coordinator_informstudentsofevaluationchanges";
var OPERATION_COORDINATOR_PUBLISHEVALUATION = "coordinator_publishevaluation";
var OPERATION_COORDINATOR_UNPUBLISHEVALUATION = "coordinator_unpublishevaluation";
var OPERATION_COORDINATOR_REMINDSTUDENTS = "coordinator_remindstudents";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEY = "coordinator_sendregistrationkey";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEYS = "coordinator_sendregistrationkeys";

//messages:
var DISPLAY_ERROR_UNDEFINED_HTTPREQUEST = "Error: Undefined XMLHttpRequest.";
var DISPLAY_SERVERERROR = "Connection to the server has timed out. Please refresh the page.";

var COORDINATOR_MESSAGE_NO_COURSE = "You have not created any courses yet. Use the form above to create a course.";
var COORDINATOR_MESSAGE_NO_EVALUATION = "You have not created any evaluations yet. Use the form above to create a new evaluation.";
var COORDINATOR_MESSAGE_NO_TEAMFORMINGSESSION = "You have not created any team forming sessions yet. Use the form above to create a new team forming session.";

var HOVER_MESSAGE_CLAIMED = "This is student own estimation of his/her contributions to the project";
var HOVER_MESSAGE_PERCEIVED = "This is the average of what other team members think this student contributed to the project";
var HOVER_MESSAGE_PERCEIVED_CLAIMED = "Difference between claimed and perceived contribution points";
/*------------------------------------------PRINT COORDINATOR PAGE------------------------------------------*/

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

/*------------------------------------------OTHERS------------------------------------------*/

function getDisabledString() {
	return "<font color=\"red\"><textarea class = \"textvalue\" type=\"text\" rows=\"1\" cols=\"100\" name=\""
	+ STUDENT_JUSTIFICATION
	+ 0
	+ "\" id=\""
	+ STUDENT_JUSTIFICATION
	+ 0 + "\" disabled=\"true\">" + "N.A.</textarea></font>";
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
