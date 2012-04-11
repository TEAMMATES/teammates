// AJAX
var xmlhttp = new getXMLObject();

// DATE OBJECT
var cal = new CalendarPopup();

/*-----------------------------------------------------------CONSTANTS-------------------------------------------------------*/

// DISPLAY
var DISPLAY_COURSE_ARCHIVED = "The course has been archived.";
var DISPLAY_COURSE_DELETED = "The course has been deleted.";
var DISPLAY_COURSE_DELETEDALLSTUDENTS = "All students have been removed from the course.";
var DISPLAY_COURSE_DELETEDSTUDENT = "The student has been removed from the course.";
var DISPLAY_COURSE_NOTEAMS = "<font color=\"#F00\">The course does not have any teams.</font>";
var DISPLAY_COURSE_SENTREGISTRATIONKEY = "Registration key has been sent to ";
var DISPLAY_COURSE_SENTREGISTRATIONKEYS = "Registration keys are sent to the students.";
var DISPLAY_COURSE_UNARCHIVED = "The course has been unarchived.";
var DISPLAY_EDITSTUDENT_FIELDSEMPTY = "<font color=\"#F00\">Please fill in all fields marked with an *.</font>";
var DISPLAY_ENROLLMENT_FIELDSEXTRA = "<font color=\"#F00\">There are too many fields.</font>";
var DISPLAY_ENROLLMENT_FIELDSMISSING = "<font color=\"#F00\">There are missing fields.</font>";
var DISPLAY_EVALUATION_ADDED = "The evaluation has been added.";
var DISPLAY_EVALUATION_ADDED_WITH_EMPTY_TEAMS = "The evaluation has been added. <font color=\"#F00\">Some students are without teams.</font>";
var DISPLAY_EVALUATION_ARCHIVED = "The evaluation has been archived.";
var DISPLAY_EVALUATION_DELETED = "The evaluation has been deleted.";
var DISPLAY_EVALUATION_EDITED = "The evaluation has been edited.";
var DISPLAY_EVALUATION_EXISTS = "<font color=\"#F00\">The evaluation exists already.</font>";
var DISPLAY_EVALUATION_INFORMEDSTUDENTSOFCHANGES = "E-mails have been sent out to inform the students of the changes to the evaluation.";
var DISPLAY_EVALUATION_NAMEINVALID = "<font color=\"#F00\">Please use only alphabets, numbers and whitespace in evaluation name.</font>";
var DISPLAY_EVALUATION_NAME_LENGTHINVALID = "<font color=\"#F00\">Evaluation name should not exceed 38 characters.</font>";
var DISPLAY_EVALUATION_PUBLISHED = "The evaluation has been published.";
var DISPLAY_EVALUATION_UNPUBLISHED = "The evaluation has been unpublished.";
var DISPLAY_EVALUATION_REMINDERSSENT = "Reminder e-mails have been sent out to those students.";
var DISPLAY_EVALUATION_RESULTSEDITED = "The particular evaluation results have been edited.";
var DISPLAY_EVALUATION_SCHEDULEINVALID = "<font color=\"#F00\">The evaluation schedule (start/deadline) is not valid.</font>";
var DISPLAY_EVALUATION_UNARCHIVED = "The evaluation has been unarchived.";
var DISPLAY_FIELDS_EMPTY = "<font color=\"#F00\">Please fill in all the relevant fields.</font>";
var DISPLAY_LOADING = "<img src=/images/ajax-loader.gif /><br />";
var DISPLAY_SERVERERROR = "Connection to the server has timed out. Please refresh the page.";
var DISPLAY_ERROR_UNDEFINED_HTTPREQUEST = "Error: Undefined XMLHttpRequest.";
var DISPLAY_STUDENT_DELETED = "The student has been removed.";
var DISPLAY_STUDENT_EDITED = "The student's details have been edited.";
var DISPLAY_STUDENT_EDITEDEXCEPTTEAM = "The student's details have been edited, except for his team<br /> as there is an ongoing evaluation.";
var DISPLAY_STUDENT_EMAILINVALID = "<font color=\"#F00\">E-mail address should contain less than 40 characters and be of a valid syntax.</font>";
var DISPLAY_STUDENT_NAMEINVALID = "<font color=\"#F00\">Name should only consist of alphabets and numbers and not<br />be more than 40 characters.</font>";
var DISPLAY_STUDENT_TEAMNAMEINVALID = "<font color=\"#F00\">Team name should contain less than 25 characters.</font>";

// DIV
var DIV_COURSE_INFORMATION = "coordinatorCourseInformation";
var DIV_COURSE_ENROLLMENT = "coordinatorCourseEnrollment";
var DIV_COURSE_ENROLLMENTBUTTONS = "coordinatorCourseEnrollmentButtons";
var DIV_COURSE_ENROLLMENTRESULTS = "coordinatorCourseEnrollmentResults";
var DIV_COURSE_MANAGEMENT = "coordinatorCourseManagement";
var DIV_COURSE_TABLE = "coordinatorCourseTable";
var DIV_EVALUATION_EDITBUTTONS = "coordinatorEditEvaluationButtons";
var DIV_EVALUATION_EDITRESULTS = "coordinatorEditEvaluationResults";
var DIV_EVALUATION_EDITRESULTSBUTTON = "coordinatorEditEvaluationResultsButtons";
var DIV_EVALUATION_INFORMATION = "coordinatorEvaluationInformation";
var DIV_EVALUATION_MANAGEMENT = "coordinatorEvaluationManagement";
var DIV_EVALUATION_SUMMARYTABLE = "coordinatorEvaluationSummaryTable";
var DIV_EVALUATION_TABLE = "coordinatorEvaluationTable";
var DIV_HEADER_OPERATION = "headerOperation";
var DIV_STUDENT_EDITBUTTONS = "coordinatorEditStudentButtons";
var DIV_STUDENT_INFORMATION = "coordinatorStudentInformation";
var DIV_STUDENT_TABLE = "coordinatorStudentTable";
var DIV_STATUS_EDITEVALUATIONRESULTS = "coordinatorEditEvaluationResultsStatusMessage";
var DIV_TOPOFPAGE = "topOfPage";

// GLOBAL VARIABLES FOR GUI
var courseSort = {
	ID : 0,
	name : 1
};
var courseSortStatus = courseSort.ID;

var evaluationSort = {
	courseID : 0,
	name : 1
};
var evaluationSortStatus = evaluationSort.courseID;

var studentSort = {
	name : 0,
	teamName : 1,
	status : 2
};
var studentSortStatus = studentSort.name;

var courseViewArchived = {
	show : 0,
	hide : 1
};
var courseViewArchivedStatus = courseViewArchived.hide;

var evaluationResultsView = {
	reviewee : 0,
	reviewer : 1
};
var evaluationResultsViewStatus = evaluationResultsView.reviewee;

var evaluationResultsSummaryListSort = {
	teamName : 0,
	name : 1,
	average : 2,
	submitted : 3,
	diff : 4
};
var evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.teamName;

// MESSAGES
var MSG_COURSE_EXISTS = "course exists";
var MSG_COURSE_NOTEAMS = "course has no teams";

var MSG_EVALUATION_ADDED = "evaluation added";
var MSG_EVALUATION_EDITED = "evaluation edited";
var MSG_EVALUATION_EXISTS = "evaluation exists";
var MSG_EVALUATION_UNABLETOCHANGETEAMS = "evaluation ongoing unable to change teams";

// OPERATIONS
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
var OPERATION_COORDINATOR_ENROLSTUDENTS = "coordinator_enrolstudents";
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

// PARAMETERS
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

/**
 * XMLHttpRequest Constants
 * 
 * */
var SERVERERROR = 1;
var CONNECTION_OK = 200;

/**
 * Add Course Constants
 * 
 */
var COURSEID_MAX_LENGTH = 21;
var COURSENAME_MAX_LENGTH = 38;

/*
 * Add Course Status Code
 * 
 */
var COURSE_STATUS_SERVERERROR = -1;
var COURSE_STATUS_VALID_INPUT = 0;
var COURSE_STATUS_SUCCESSFUL = 1;
var COURSE_STATUS_EXISTS = 2;
var COURSE_STATUS_EMPTY = 3;
var COURSE_STATUS_LONG_ID = 4;
var COURSE_STATUS_LONG_NAME = 5;
var COURSE_STATUS_INVALID_ID = 6;
var COURSE_STATUS_INVALID_NAME = 7;
var COURSE_STATUS_DELETED = 8;
/*
 * Enrol Student Status Code
 * 
 */
var ENROL_STUDENT_SUCCESSFUL = 1;
var ENROL_STUDENT_ERROR = 2;
var ENROL_STUDENT_EMPTY = 3;

/*
 * Add Course Server Response
 * */
var COURSE_RESPONSE_EXISTS = "course exists";
var COURSE_RESPONSE_ADDED = "course added";

var COURSE_RESPONSE_DELETED = "course deleted";

/*
 * Add Course Status Message
 *  
 */
var DISPLAY_COURSE_ADDED = "The course has been added. Click the 'Enrol' link in the table below to add students to the course.";
var DISPLAY_COURSE_EXISTS = "<font color=\"#F00\">The course already exists.</font>";
var DISPLAY_COURSE_EMPTY = "<font color=\"#F00\">Course ID and Course Name are compulsory fields.</font>";
var DISPLAY_COURSE_LONG_ID = "<font color=\"#F00\">Course ID should not exceed " + COURSEID_MAX_LENGTH + " characters.</font>";
var DISPLAY_COURSE_LONG_NAME = "<font color=\"#F00\">Course name should not exceed " + COURSENAME_MAX_LENGTH + " characters.</font>";
var DISPLAY_COURSE_INVALID_ID = "<font color=\"#F00\">Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.</font>";
var DISPLAY_COURSE_INVALID_NAME = "<font color=\"#F00\">Course name is invalid.</font>";

/***
 * Submission Constants
 */
var NA = "N/A";
var NA_POINTS = -101;
var NOTSURE = "NOT SURE";
var NOTSURE_POINTS = -999;
var YES = "YES";
var NO = "NO";

/***********************************************************LANDING PAGE**********************************************************/
/*----------------------------------------------------------LANDING PAGE---------------------------------------------------------*/
/**
 * Coordinator Click Home Tab
 * 
 * */
function displayHomeTab() {
	clearDisplay();
	
	setStatusMessageToLoading();
	printCoordinatorLandingPage();
	clearStatusMessage();
	
	scrollToTop(DIV_TOPOFPAGE);
}

//----------------------------------------------------------LANDING PAGE FUNCTIONS
function printCoordinatorLandingPage() {
	var outputHeader =
		"<h1>COORDINATOR HOME</h1>																							\
		<br />																												\
		<h2><a href=\"javascript:displayCoursesTab()\" name='addNewCourse' id='addNewCourse'>Add New Course</a></h2>";
	
	var output = "<form method='post' action='' name='form_coursessummary'>";

	var courseID;
	sendGetCourseListRequest();
	var courseList = processGetCourseListResponse();
	var courseListLength = courseList.length;
	
	var evaluationList = getEvaluationList();
	var evaluationList = evaluationList.sort(sortByName);
	evaluationListLength = evaluationList.length;

	for (var loop = 0; loop < courseListLength; loop++) {
		courseID = courseList[loop].ID;

		output = output +
		"<div class='result_team' id='course" + loop + "' name='course" + loop + "'>										\
			<div class='result_homeTitle'>																					\
				<h2>[" + courseID + "] : " + courseList[loop].name + "</h2>													\
			</div>																											\
			<div class='result_homeLinks'>																					\
				<a class='t_course_enrol" + loop + "' href=\"javascript:displayEnrollmentPage('" + courseID + "');			\
					hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_ENROL + "')\"								\
					onmouseout=\"hideddrivetip()\">Enrol</a>																\
				<a class='t_course_view" + loop + "' href=\"javascript:displayCourseInformation('" + courseID + "');		\
					hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_VIEW_COURSE + "')\"						\
					onmouseout=\"hideddrivetip()\">View</a>																	\
				<a class='t_course_add_eval" + loop + "' href=\"javascript:displayEvaluationsTab('" + courseID + "');		\
					hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_ADD_EVALUATION + "')\"					\
					onmouseout=\"hideddrivetip()\">Add Evaluation</a>														\
				<a class='t_course_delete" + loop + "' 																		\
					href=\"javascript:toggleDeleteCourseConfirmation('" + courseID + "'," + true + ");hideddrivetip();\"	\
					onmouseover=\"ddrivetip('" + HOVER_MESSAGE_DELETE_COURSE + "')\"" + "onmouseout=\"hideddrivetip()\">	\
					Delete</a>																								\
			</div>																											\
			<div style='clear: both;'></div>																				\
			<br />";

		var evaluationStatus;
		var evaluationsPresent = false;
		var evaluationOutput = "";
		
		if (evaluationListLength > 0) {			
			for (var i = 0; i < evaluationListLength; i++) {
				if (evaluationList[i].courseID == courseID) {
					evaluationsPresent = true;
					
					if (evaluationList[i].status == "AWAITING")
						evaluationStatus =
					"<td class='t_eval_status centeralign'>																	\
					<span onmouseover=\"ddrivetip('The evaluation is created but has not yet started')\"					\
					onmouseout=\"hideddrivetip()\">" + evaluationList[i].status + "</span></td>";
					else if (evaluationList[i].status == "OPEN")
						evaluationStatus =
					"<td class='t_eval_status centeralign'>																	\
					<span 																									\
					onmouseover=\"ddrivetip('The evaluation has started and students can submit feedback until the closing time')\"\
					onmouseout=\"hideddrivetip()\">" + evaluationList[i].status + "</span></td>";
					else if (evaluationList[i].status == "CLOSED")
						evaluationStatus =
					"<td class='t_eval_status centeralign'>																	\
					<span 																									\
					onmouseover=\"ddrivetip('The evaluation has finished but the results are not yet sent to the students')\"\
					onmouseout=\"hideddrivetip()\">" + evaluationList[i].status + "</span></td>";
					else if (evaluationList[i].status == "PUBLISHED")
						evaluationStatus =
					"<td class='t_eval_status centeralign'>																	\
					<span 																									\
					onmouseover=\"ddrivetip('The evaluation has finished and the results have been sent to students')\"		\
					onmouseout=\"hideddrivetip()\">" + evaluationList[i].status + "</span></td>";

					evaluationOutput = evaluationOutput +
				"<tr id='evaluation" + i + "'>																				\
					<td class='t_eval_name'>" + encodeChar(evaluationList[i].name) + "</td>									\
					" + evaluationStatus + "																				\
					<td class='t_eval_response centeralign'>																\
					" + evaluationList[i].numberOfCompletedEvaluations + " / " + evaluationList[i].numberOfEvaluations + "	\
					</td>";
	
					// display actions:
					evaluationOutput = evaluationOutput +
					"<td class='centeralign'>																				\
					" + printEvaluationActions(evaluationList, i, true) + "													\
					</td>																									\
				</tr>";
				}
			}
		}
		
		if (evaluationsPresent) {
			output = output +
			"<table id='dataform'>																							\
				<tr>																										\
					<th class='leftalign'>EVALUATION NAME</th>																\
					<th class='centeralign'>STATUS</th>																		\
					<th class='centeralign'><span onmouseover=\"ddrivetip('Number of students submitted / Class size')\"	\
					onmouseout=\"hideddrivetip()\">RESPONSE RATE</span></th>												\
					<th class='centeralign'>ACTION(S)</th>																	\
				</tr>																										\
				" + evaluationOutput + "																					\
			</table>																										\
			<br />";;
		}

		output = output +
		"</div>																												\
		<br /><br /><br />";
	}

	output = output + "</form>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_STUDENT_TABLE).innerHTML = output;

	for (var loop = 0; loop < courseListLength; loop++) {
		courseID = courseList[loop].ID;
		
		for (var i = 0; i < evaluationListLength; i++) {
			if (evaluationList[i].courseID == courseID) {
				if (document.getElementById('editEvaluation' + i) != null
						&& document.getElementById('editEvaluation' + i).onclick == null) {
					document.getElementById('editEvaluation' + i).onclick = function() {
						hideddrivetip();
						displayEditEvaluation(evaluationList, this.id.substring(
								14, this.id.length));
					};
				}

				if (document.getElementById('viewEvaluation' + i) != null
						&& document.getElementById('viewEvaluation' + i).onclick == null) {
					document.getElementById('viewEvaluation' + i).onclick = function() {
						hideddrivetip();
						displayEvaluationResults(evaluationList, this.id.substring(
								14, this.id.length));
					};
				}
			}
		}
	}
}

/***********************************************************COURSE PAGE***********************************************************/
/*----------------------------------------------------------COURSE PAGE----------------------------------------------------------*/
/**
 * Coordinator Click Course Tab
 * 
 * */
function displayCoursesTab() {
	clearDisplay();
	
	setStatusMessageToLoading();
	printAddCourseForm();
	getAndPrintCourseList();
	clearStatusMessage();
	
	scrollToTop(DIV_TOPOFPAGE);
}



//----------------------------------------------------------ADD COURSE FUNCTIONS
function printAddCourseForm() {
	var outputHeader = "<h1>ADD NEW COURSE</h1>";

	var outputForm = 
		"<form method='post' action='' name='form_addcourse'>																\
			<table class='addform round'>																					\
				<tr>																										\
					<td><b>Course ID:</b></td>																				\
				</tr>																										\
				<tr>																										\
					<td><input class='addinput' type='text' name='" + COURSE_ID + "' id='" + COURSE_ID + "'					\
					onmouseover=\"ddrivetip('Enter the identifier of the course, e.g.CS3215Sem1.')\"						\
					onmouseout=\"hideddrivetip()\" maxlength=" + COURSEID_INPUT_FIELD_MAX_LENGTH + " tabindex=1 /></td>		\
				</tr>																										\
				<tr>																										\
					<td><b>Course Name:</b></td>																			\
				</tr>																										\
				<tr>																										\
					<td><input class='addinput' type='text' name='" + COURSE_NAME + "' id='" + COURSE_NAME + "'				\
					onmouseover=\"ddrivetip('Enter the name of the course, e.g. Software Engineering.')\"					\
					onmouseout=\"hideddrivetip()\" maxlength=" + COURSENAME_INPUT_FIELD_MAX_LENGTH + " tabindex=2 /></td>	\
				</tr>																										\
				<tr>																										\
					<td><input id='btnAddCourse' type='button' class='button' 												\
					onclick=\"doAddCourse(this.form." + COURSE_ID + ".value, this.form." + COURSE_NAME + ".value)\"			\
					value='Add Course' tabindex='3' /></td>																	\
				</tr>																										\
		</form>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
}

function doAddCourse(courseID, courseName) {
	setStatusMessageToLoading();
	
	prepareAddCourseParams(courseID, courseName);
	
	// client-side validation
	var statusCode = checkAddCourseParam(courseID, courseName);
	
	
	if(statusCode != COURSE_STATUS_VALID_INPUT) {
		setStatusMessage(courseStatusToMessage(statusCode));
		return;
	}
	
	//server-side request and response
	if(!xmlhttp) {
		alert(DISPLAY_ERROR_UNDEFINED_HTTPREQUEST);
		return;
	}
	
	sendAddCourseRequest(courseID, courseName);
	statusCode = processAddCourseResponse();
	
	if(statusCode == COURSE_STATUS_EXISTS) {
		setStatusMessage(courseStatusToMessage(statusCode));
		return;
	}
	
	if(statusCode != COURSE_STATUS_SUCCESSFUL) {
		alert(DISPLAY_SERVERERROR);
		setStatusMessage(courseStatusToMessage(statusCode));
		return;
	}
	
	printAddCourseForm();
	getAndPrintCourseList();
	setStatusMessage(courseStatusToMessage(statusCode));

}

function prepareAddCourseParams(courseID, courseName) {
	courseID = trim(courseID);
	courseName = trim(courseName);
}

function sendAddCourseRequest(courseID, courseName) {
	xmlhttp.open("POST", "/teammates", false);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
	xmlhttp.send("operation=" + OPERATION_COORDINATOR_ADDCOURSE + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ COURSE_NAME + "=" + encodeURIComponent(courseName));
}

function processAddCourseResponse() {

	if (xmlhttp.status != CONNECTION_OK)
		return COURSE_STATUS_SERVERERROR;

	var status = xmlhttp.responseXML.getElementsByTagName("status")[0];

	if (status == null)
		return COURSE_STATUS_SERVERERROR;

	//server response message:
	var response = status.firstChild.nodeValue;
	switch (response) {
		case COURSE_RESPONSE_EXISTS:
			return COURSE_STATUS_EXISTS;
			
		case COURSE_RESPONSE_ADDED:
			return COURSE_STATUS_SUCCESSFUL;
			
		default:
			return COURSE_STATUS_SERVERERROR;
	}

}

function courseStatusToMessage(statusCode) {
	switch (statusCode) {

	case COURSE_STATUS_SUCCESSFUL:
		return DISPLAY_COURSE_ADDED;

	case COURSE_STATUS_EXISTS:
		return DISPLAY_COURSE_EXISTS;

	case COURSE_STATUS_EMPTY:
		return DISPLAY_COURSE_EMPTY;

	case COURSE_STATUS_LONG_ID:
		return DISPLAY_COURSE_LONG_ID;

	case COURSE_STATUS_LONG_NAME:
		return DISPLAY_COURSE_LONG_NAME;

	case COURSE_STATUS_INVALID_ID:
		return DISPLAY_COURSE_INVALID_ID;

	default:
		return DISPLAY_SERVERERROR;
	}
}

function checkAddCourseParam(courseID, courseName) {
	// empty fields
	if (courseID == "" || courseName == "") {
		return COURSE_STATUS_EMPTY;
	}

	// long courseID
	if(courseID.length > COURSEID_MAX_LENGTH) {
		return COURSE_STATUS_LONG_ID;
	}

	// long courseName
	if(courseName.length > COURSENAME_MAX_LENGTH) {
		return COURSE_STATUS_LONG_NAME;
	}
	
	// invalid courseID
	if (!isCourseIDValid(courseID)) {
		return COURSE_STATUS_INVALID_ID;
	}
	
	// valid input
	return COURSE_STATUS_VALID_INPUT;
}

function isCourseIDValid(courseID) {

	return courseID.match(/^[a-zA-Z_$0-9.-]+$/);
}



//----------------------------------------------------------LIST COURSE FUNCTIONS
function getAndPrintCourseList() {
	setStatusMessageToLoading();

	// server-side request and response
	if (!xmlhttp) {
		alert(DISPLAY_ERROR_UNDEFINED_HTTPREQUEST);
		return;
	}

	sendGetCourseListRequest();
	var results = processGetCourseListResponse();

	clearStatusMessage();

	if (results == COURSE_STATUS_SERVERERROR) {
		alertServerError();
		return;
	}

	if (courseSortStatus == courseSort.name) {
		toggleSortCoursesByName(results);
	} else {
		toggleSortCoursesByID(results);
	}
}

function sendGetCourseListRequest() {
	xmlhttp.open("POST", "/teammates", false);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
	xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETCOURSELIST);
}

function processGetCourseListResponse() {
	if (xmlhttp.status != CONNECTION_OK)
		return COURSE_STATUS_SERVERERROR;

	var courses = xmlhttp.responseXML.getElementsByTagName("courses")[0];
	if (courses == null)
		return COURSE_STATUS_SERVERERROR;

	var course;
	var ID;
	var name;
	var numberOfTeams;
	var totalStudents;
	var unregistered;
	var status;
	
	var coursesChildNodesLength = courses.childNodes.length;
	var courseList = new Array();
	for (loop = 0; loop < coursesChildNodesLength; loop++) {
		course = courses.childNodes[loop];
		ID = course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
		name = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
		numberOfTeams = course.getElementsByTagName(COURSE_NUMBEROFTEAMS)[0].firstChild.nodeValue;
		totalStudents = course.getElementsByTagName(COURSE_TOTALSTUDENTS)[0].firstChild.nodeValue;
		unregistered = course.getElementsByTagName(COURSE_UNREGISTERED)[0].firstChild.nodeValue;
		status = course.getElementsByTagName(COURSE_STATUS)[0].firstChild.nodeValue;
		courseList[loop] = {
			ID : ID,
			name : name,
			numberOfTeams : numberOfTeams,
			totalStudents : totalStudents,
			unregistered : unregistered,
			status : status
		};
	}

	return courseList;
}

function toggleSortCoursesByID(courseList) {
	printCourseList(courseList.sort(sortByID));
	courseSortStatus = courseSort.ID;
	document.getElementById("button_sortcourseid").setAttribute("class","buttonSortAscending");
}

function toggleSortCoursesByName(courseList) {
	printCourseList(courseList.sort(sortByName));
	courseSortStatus = courseSort.name;
	document.getElementById("button_sortcoursename").setAttribute("class","buttonSortAscending");
}

function printCourseList(courseList) {
	var courseListLength = courseList.length;
	
	//table header
	var output = 
		"<br /><br />																										\
		<table id='dataform'>																								\
			<tr>																											\
				<th><input class='buttonSortNone' type='button' id='button_sortcourseid'>COURSE ID</input></th>				\
				<th><input class='buttonSortNone' type='button' id='button_sortcoursename'>COURSE NAME</input></th>			\
				<th class='centeralign'>TEAMS</th>																			\
				<th class='centeralign'>TOTAL STUDENTS</th>																	\
				<th class='centeralign'>TOTAL UNREGISTERED</th>																\
				<th class='centeralign'>ACTION(S)</th>																		\
			</tr>";

	//empty table
	if (courseListLength == 0) {
		setStatusMessage(COORDINATOR_MESSAGE_NO_COURSE);
		output = output +
			"<tr>																											\
				<td></td>																									\
				<td></td>																									\
				<td></td>																									\
				<td></td>																									\
				<td></td>																									\
				<td></td>																									\
			</tr>";	}

	// Need counter to take note of archived courses
	var counter = 0;

	for (loop = 0; loop < courseListLength; loop++) {
		if (courseList[loop].status == "false" || courseViewArchivedStatus == courseViewArchived.show) {
			// common view:
			output = output + 
			"<tr>																											\
				<td id='courseID" + counter + "'>" + courseList[loop].ID + "</td>											\
				<td id='courseName" + counter + "'>" + encodeChar(courseList[loop].name) + "</td>							\
				<td class='t_course_teams centeralign'>" + courseList[loop].numberOfTeams + "</td>							\
				<td class='centeralign'>" + courseList[loop].totalStudents + "</td>											\
				<td class='centeralign'>" + courseList[loop].unregistered + "</td>											\
				<td class='centeralign'>																					\
					<a class='t_course_enrol' href=\"javascript:displayEnrollmentPage('" + courseList[loop].ID + "');		\
						hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_ENROL + "')\"							\
						onmouseout=\"hideddrivetip()\">Enrol</a>															\
					<a class='t_course_view' href=\"javascript:displayCourseInformation('" + courseList[loop].ID + "');		\
						hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_VIEW_COURSE + "')\"					\
						onmouseout=\"hideddrivetip()\">View</a>																\
					<a class='t_course_delete'																				\
						href=\"javascript:toggleDeleteCourseConfirmation('" + courseList[loop].ID + "', " + false + ");		\
						hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_DELETE_COURSE + "')\"					\
						onmouseout=\"hideddrivetip()\">Delete</a>															\
				</td>																																		\
			</tr>";

			counter++;
		}
	}

	output = output +
		"</table>																											\
		<br /><br />";
	if (counter == 0) {
		output = output +
		"No records found.																									\
		<br /><br /><br /><br />";	
	}

	document.getElementById(DIV_COURSE_TABLE).innerHTML = output;
	document.getElementById('button_sortcourseid').onclick = function() {
		toggleSortCoursesByID(courseList)
	};
	document.getElementById('button_sortcoursename').onclick = function() {
		toggleSortCoursesByName(courseList)
	};
}



//----------------------------------------------------------DELETE COURSE FUNCTIONS
/**
 * Coordinator Delete Course
 * */
function toggleDeleteCourseConfirmation(courseID,isHome) {
	var s = confirm("Are you sure you want to delete the course, \"" + courseID + "\"? This operation will delete all evaluations and students in this course.");

	if (s == true)
		doDeleteCourse(courseID,isHome);
	else
		clearStatusMessage();

	scrollToTop(DIV_COURSE_MANAGEMENT);
}

function doDeleteCourse(courseID,isHome) {
	setStatusMessageToLoading;

	// server-side request and response
	if (!xmlhttp) {
		alert(DISPLAY_ERROR_UNDEFINED_HTTPREQUEST);
		return;
	}
	
	sendDeleteCourseRequest(courseID);
	var results = processDeleteCourseResponse(courseID);
	
	if(results == COURSE_STATUS_SERVERERROR) {
		alert(DISPLAY_SERVERERROR);
		return;
	}
	
	getAndPrintCourseList();
	if (isHome)
		printCoordinatorLandingPage();
	else
		getAndPrintCourseList();	
	setStatusMessage(DISPLAY_COURSE_DELETED);
}

function sendDeleteCourseRequest(courseID) {
	xmlhttp.open("POST", "/teammates", false);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
	xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETECOURSE + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));
}

function processDeleteCourseResponse() {
	
	if (xmlhttp.status != CONNECTION_OK)
		return COURSE_STATUS_SERVERERROR;

	var status = xmlhttp.responseXML.getElementsByTagName("status")[0];

	if (status == null)
		return COURSE_STATUS_SERVERERROR;

	//server response message:
	var response = status.firstChild.nodeValue;
	if(response == COURSE_RESPONSE_DELETED)
		return COURSE_STATUS_DELETED;
	else
		return COURSE_STATUS_SERVERERROR;

}



/***********************************************************EVALUATION PAGE***********************************************************/
/*----------------------------------------------------------EVALUATION PAGE----------------------------------------------------------*/
function displayEvaluationsTab(courseID) {
	clearDisplay();
	printEvaluationAddForm(courseID);
	//doGetCourseIDList();;
	doGetEvaluationList();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}



//----------------------------------------------------------ADD EVALUATION FUNCTIONS
/**
 * Coordinator Add Evaluation
 *  
 **/
/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: evaluation name invalid 4:
 * evaluation name long 5: evaluation schedule invalid 6: evaluation exists 7:
 * course has no teams
 */
function addEvaluation(courseID, name, instructions, commentsEnabled, start,
		startTime, deadline, deadlineTime, timeZone, gracePeriod) {
	if (courseID == "" || name == "" || start == "" || startTime == ""
			|| deadline == "" || deadlineTime == "" || timeZone == ""
			|| gracePeriod == "" || instructions == "") {
		return 2;
	}

	else if (!isEvaluationNameValid(name)) {
		return 3;
	}

	else if (!isEvaluationNameLengthValid(name)) {
		return 4;
	}

	else if (!isAddEvaluationScheduleValid(start, startTime, deadline,
			deadlineTime)) {
		return 5;
	}

	else {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_ADDEVALUATION + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name) + "&"
				+ EVALUATION_DEADLINE + "=" + encodeURIComponent(deadline)
				+ "&" + EVALUATION_DEADLINETIME + "="
				+ encodeURIComponent(deadlineTime) + "&"
				+ EVALUATION_INSTRUCTIONS + "="
				+ encodeURIComponent(instructions) + "&" + EVALUATION_START
				+ "=" + encodeURIComponent(start) + "&" + EVALUATION_STARTTIME
				+ "=" + encodeURIComponent(startTime) + "&"
				+ EVALUATION_GRACEPERIOD + "="
				+ encodeURIComponent(gracePeriod) + "&" + EVALUATION_TIMEZONE
				+ "=" + encodeURIComponent(timeZone) + "&"
				+ EVALUATION_COMMENTSENABLED + "="
				+ encodeURIComponent(commentsEnabled));

		return handleAddEvaluation();
	}

}



//----------------------------------------------------------LIST EVALUATION FUNCTIONS
function doGetEvaluationList() {
	setStatusMessage(DISPLAY_LOADING);

	var results = getEvaluationList();

	clearStatusMessage();

	if (results != 1) {

		// Toggle calls printEvaluationList too
		if (evaluationSortStatus == evaluationSort.name) {
			toggleSortEvaluationsByName(results);

		}

		else {
			toggleSortEvaluationsByCourseID(results);
		}
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function toggleSortEvaluationsByCourseID(evaluationList) {
	printEvaluationList(evaluationList.sort(sortByCourseID));
	evaluationSortStatus = evaluationSort.courseID;
	document.getElementById("button_sortcourseid").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortEvaluationsByName(evaluationList) {
	printEvaluationList(evaluationList.sort(sortByName));
	evaluationSortStatus = evaluationSort.name;
	document.getElementById("button_sortname").setAttribute("class",
			"buttonSortAscending");
}



/***********************************************************EVALUATION RESULT PAGE***********************************************************/
/*----------------------------------------------------------EVALUATION RESULT PAGE----------------------------------------------------------*/
function displayEvaluationResults(evaluationList, loop) {
	var courseID = evaluationList[loop].courseID;
	var name = evaluationList[loop].name;
	var instructions = evaluationList[loop].instructions;
	var start = evaluationList[loop].start;
	var deadline = evaluationList[loop].deadline;
	var gracePeriod = evaluationList[loop].gracePeriod;
	var status = evaluationList[loop].status;
	var activated = evaluationList[loop].activated;
	var commentsEnabled = evaluationList[loop].commentsEnabled;
	var published = evaluationList[loop].published;

	clearDisplay();

	printEvaluationHeaderForm(courseID, name, start, deadline, status, activated, published);
	evaluationResultsViewStatus = evaluationResultsView.reviewer;
	
	doGetSubmissionResultsList(courseID, name, status, commentsEnabled);

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}



//----------------------------------------------------------DISPLAY EVALUATION DETAILS FUNCTIONS
function printEvaluationHeaderForm(courseID, evaluationName, start, deadline, status, activated, published) {
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
			+

			// new radio button: review type + report type
			"<tr>"
			+ "<td class=\"rightalign\"><b>Report Type:</b> "
			+

			"<input type = \"radio\" name = \"radio_viewall\" id = \"radio_summary\" value = \"summary\" checked = \"checked\" />"
			+ "<label for = \"radio_summary\">Summary</label>&nbsp&nbsp&nbsp"
			+

			"<input type = \"radio\" name = \"radio_viewall\" id = \"radio_detail\" value = \"detail\" />"
			+ "<label for = \"radio_detail\">Detail</label>"
			+ "</td>"
			+ "<td class=\"leftalign\"><b>Review Type:</b> "
			+

			"<input type = \"radio\" name = \"radio_viewbytype\" id = \"radio_reviewer\" value = \"by reviewer\" checked = \"checked\"/>"
			+ "<label for = \"radio_reviewer\">By Reviewer</label>&nbsp&nbsp&nbsp"
			+ "<input type = \"radio\" name = \"radio_viewbytype\" id = \"radio_reviewee\" value = \"by reviewee\"/>"
			+ "<label for = \"radio_reviewee\">By Reviewee</label>" +

			"</td>" + "</tr>" +

			// publish, unpublish button
			"<tr>" + "<td></td>" + "<td>";
	// publish
	if (status != "OPEN") {
		if (published == false && activated == true) {
			output = output
					+ "<input type=\"button\" class=\"button\" id = \"button_publish\" value = \"Publish\" onclick = \"javascript:togglePublishEvaluation('"
					+ courseID + "','" + evaluationName
					+ "', true, false)\" />";
		} else if (published == true) {
			output = output
					+ "<input type=\"button\" class=\"button\" id = \"button_publish\" value = \"Unpublish\" onclick = \"javascript:togglePublishEvaluation('"
					+ courseID + "','" + evaluationName
					+ "', false, false)\" />";

		}
	}
	output = output + "</td>" + "</tr>" + "</table>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output;
}

function doGetEvaluationListOfCourse(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = getEvaluationListOfCourse(courseID);

	clearStatusMessage();
	if (results != 1) {
		return results;
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}


//----------------------------------------------------------LIST EVALUATION RESULTS FUNCTIONS
function doGetSubmissionResultsList(courseID, evaluationName, status, commentsEnabled) {
	setStatusMessageToLoading();
	
	if (!xmlhttp) {
		alert(DISPLAY_ERROR_UNDEFINED_HTTPREQUEST);
		return;
	}

	sendGetSubmissionListRequest(courseID, evaluationName);
	var results = processGetSubmissionListResponse();

	clearStatusMessage();
	
	if(results == SERVERERROR) {
		alert(DISPLAY_SERVERERROR);
		return;
	}

	var compiledResults = compileSubmissionSummaryList(results);
	toggleSortEvaluationSummaryListByTeamName(results, compiledResults, status, commentsEnabled);

}

function sendGetSubmissionListRequest(courseID, evaluationName) {
	xmlhttp.open("POST", "/teammates", false);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
	xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSUBMISSIONLIST
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) 
				+ "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName));
}

function processGetSubmissionListResponse() {
	if (xmlhttp.status != CONNECTION_OK)
		return SERVERERROR;
	
	var submissions = xmlhttp.responseXML.getElementsByTagName("submissions")[0];
	
	var submissionList = new Array();
	if(submissions != null)
		submissionList = complieSubmissionList(submissions);
		
	return submissionList;
}

//util functions to format submission data
function complieSubmissionList(submissions) {
	var submissionList = new Array();
	
	var submission;
	
	var courseID;
	var evaluationName;
	var justification;
	var commentsToStudent;
	var teamName;
	var points;
	var pointsBumpRatio;
	
	var fromStudent;
	var fromStudentName;
	var fromStudentComments;
	
	var toStudent;
	var toStudentName;
	var toStudentComments;
	
	
	var submissionsChildNodesLength = submissions.childNodes.length;
	for (loop = 0; loop < submissionsChildNodesLength; loop++) {
		
		submission = submissions.childNodes[loop];
		
		courseID = submission.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
		evaluationName = submission.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
		justification = submission.getElementsByTagName(STUDENT_JUSTIFICATION)[0].firstChild.nodeValue;
		commentsToStudent = submission.getElementsByTagName(STUDENT_COMMENTSTOSTUDENT)[0].firstChild.nodeValue;
		
		teamName = submission.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
		points = parseInt(submission.getElementsByTagName(STUDENT_POINTS)[0].firstChild.nodeValue);
		pointsBumpRatio = parseFloat(submission.getElementsByTagName(STUDENT_POINTSBUMPRATIO)[0].firstChild.nodeValue);
		
		fromStudentName = submission.getElementsByTagName(STUDENT_FROMSTUDENTNAME)[0].firstChild.nodeValue;
		fromStudent = submission.getElementsByTagName(STUDENT_FROMSTUDENT)[0].firstChild.nodeValue;
		fromStudentComments = submission.getElementsByTagName(STUDENT_FROMSTUDENTCOMMENTS)[0].firstChild.nodeValue;

		toStudentName = submission.getElementsByTagName(STUDENT_TOSTUDENTNAME)[0].firstChild.nodeValue;
		toStudent = submission.getElementsByTagName(STUDENT_TOSTUDENT)[0].firstChild.nodeValue;
		toStudentComments = submission.getElementsByTagName(STUDENT_TOSTUDENTCOMMENTS)[0].firstChild.nodeValue;

		
		submissionList[loop] = {
			fromStudentName : fromStudentName,
			toStudentName : toStudentName,
			fromStudent : fromStudent,
			toStudent : toStudent,
			courseID : courseID,
			evaluationName : evaluationName,
			teamName : teamName,
			justification : justification,
			commentsToStudent : commentsToStudent,
			points : points,
			pointsBumpRatio : pointsBumpRatio,
			fromStudentComments : fromStudentComments,
			toStudentComments : toStudentComments
		};
	}
	return submissionList;
}
/**---------------------------------------------------------added 26 Mar 2012------------------------------------*/
function compileSubmissionSummaryList(submissionList){
	logSubmissionList(submissionList);
	
	//creating summary list (without team normalization)
	var summaryList = new Array();
	var count = 0;
	var i;
	for(i = 0; i < submissionList.length; i++){
		var submission = submissionList[i];

		if(!isStudentInSummaryList(submission.toStudent, summaryList)){
			summaryList[count++] = createSubmissionSummary(submission, submissionList);
		}
	}
	
	//creating team bumpratio
	var normalizedList = new Array();
	count = 0;
	for(i = 0; i < summaryList.length; i++){
		var summary = summaryList[i];
		if(!isTeamInNormalizedList(summary.teamName, normalizedList)){
			var normalizedData = createNormalizedData(summary, summaryList);
			if(normalizedData !== null)
				normalizedList[count++] = normalizedData;
		}
	}

	//normalizing summary list with team bumpratio
	var j;
	for(i = 0; i < normalizedList.length; i++){
		var team = normalizedList[i].teamName;

		for(j = 0; j < summaryList.length; j++){
			if(summaryList[j].teamName == team && summaryList[j].average != NA){
				//normalizing average by team bumpratio
				summaryList[j].average = Math.round(summaryList[j].average * normalizedList[i].pointsBumpRatio);
				//updating difference
				summaryList[j].difference = getDifference(summaryList[j].average, summaryList[j].claimedPoints);
			}
		}
	}
	
	//debug
	logSummaryList(summaryList);
		
	return summaryList;
}

function createNormalizedData(summary, summaryList){
	var output = null;
	var totalPoints = 0;
	var totalPointGivers = 0;
	var pointsBumpRatio;
	var teamName;
	
	var i;
	for(i = 0; i < summaryList.length; i++){
		if(summaryList[i].teamName == summary.teamName && summaryList[i].average != NA){
			totalPoints += summaryList[i].average;
			totalPointGivers++;
		}
	}
	if(totalPointGivers != 0){
		pointsBumpRatio = totalPointGivers * 100 / totalPoints;
		teamName = summary.teamName;
		output = {
			pointsBumpRatio : pointsBumpRatio,
			teamName : teamName
		};
	}
	return output;	
}

function isTeamInNormalizedList(teamName, normalizedList){
	var i;
	for(i = 0; i < normalizedList.length; i++){
		if(normalizedList[i].teamName == teamName)
			return true;
	}
	return false;
}

function isStudentInSummaryList(toStudent, summaryList){
	var i;
	for (i = 0; i < summaryList.length; i++) {
		if (summaryList[i].toStudent == toStudent)
			return true;
	}
	return false;
}

function createSubmissionSummary(submission, submissionList){
	//basic info
	var toStudent = submission.toStudent;
	var toStudentName = submission.toStudentName;
	var teamName = submission.teamName;
	var toStudentComments = submission.toStudentComments;
	//points given by self
	var selfSubmission = getSelfSubmission(toStudent, submissionList);
	var submitted = isSubmitted(selfSubmission);
	var claimedPoints = getClaimedPoints(selfSubmission);
	//points given by others
	var average = getAverage(toStudent, submissionList);
	//difference
	var difference = getDifference(average, claimedPoints);

	var summary = {
		toStudent : toStudent,
		toStudentName : toStudentName,
		teamName : teamName,
		average : average,
		difference : difference,
		toStudentComments : toStudentComments,
		submitted : submitted,
		claimedPoints : claimedPoints
	};
	return summary;
}

function getSelfSubmission(toStudent, submissionList){
	var i;
	for(i = 0; i < submissionList.length; i++){
		if(submissionList[i].toStudent == toStudent 
			&& submissionList[i].fromStudent == toStudent)
			return submissionList[i];
	}
	return null;
}

function isSubmitted(selfSubmission){
	if(selfSubmission !== null && selfSubmission.points != -999)
		return true;
	else
		return false;
}

function getClaimedPoints(selfSubmission){
	if(selfSubmission !== null)
		return claimedPointsToString(selfSubmission.points, selfSubmission.pointsBumpRatio);
	else
		return NA;
}

function claimedPointsToString(points, pointsBumpRatio){
	if(points == -999)
		return NA;
	else if(points == -101)
		return NOTSURE;
	else
		return Math.round(points * pointsBumpRatio);
}

function getAverage(toStudent, submissionList){
	var i;
	var totalPoints = 0;
	var totalPointGivers = 0;
	for(i = 0; i < submissionList.length; i++){
		if(submissionList[i].toStudent == toStudent && submissionList[i].fromStudent != toStudent){
			if(submissionList[i].points != -999 && submissionList[i].points != -101){
				totalPoints += Math.round(submissionList[i].points * submissionList[i].pointsBumpRatio);
				totalPointGivers++;
			}
		}
	}
	if(totalPointGivers == 0)
		return NA;
	else
		return Math.round(totalPoints / totalPointGivers);
}

function getDifference(average, claimedPoints){
	var diff = Math.round(average - claimedPoints);

	return (isNaN(diff))? NA: diff;
}

/**---------------------------------------------------------added 26 Mar 2012------------------------------------*/

//util functions to sort evaluation results
function toggleSortEvaluationSummaryListByAverage(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByAverage), status, commentsEnabled, REVIEWEE);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.average;
	document.getElementById("button_sortaverage").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByDiff(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByDiff), status, commentsEnabled, REVIEWEE);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.diff;
	document.getElementById("button_sortdiff").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByFromStudentName(submissionList, summaryList, status, commentsEnabled) {
	var type;
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;
	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByFromStudentName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name;
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListBySubmitted(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortBySubmitted), status, commentsEnabled, REVIEWER);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.submitted;
	document.getElementById("button_sortsubmitted").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList, status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;

	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByTeamName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.teamName;
	document.getElementById("button_sortteamname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByToStudentName(submissionList, summaryList, status, commentsEnabled) {
	var type;
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;
	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByToStudentName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name;
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

/*
 * UI Element: print evaluation summary form type: reviewer, reviewee
 */
function printEvaluationSummaryForm(submissionList, summaryList, status, commentsEnabled, type) {
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";

	var submitted;
	var output = "";

	// if link is disabled, insert this line to reset style and onclick:
	var disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";

	output = output
			+ "<table id=\"dataform\">"
			+ "<tr>"
			+ "<th class=\"leftalign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortteamname\">TEAM</input></th>"
			+ "<th class=\"leftalign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\">STUDENT</input></th>";

	// thead:
	if (type == REVIEWER) {
		output = output
				+ "<th class=\"centeralign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortsubmitted\">SUBMITTED</input></th>"
				+ "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	} else {
		output = output
				+ "<th class=\"leftalign\"><div onmouseover=\"ddrivetip('" + HOVER_MESSAGE_CLAIMED + "')\" onmouseout=\"hideddrivetip()\">" +
						"<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortaverage\">CLAIMED CONTRIBUTION</input></div></th>"
				+ "<th class=\"centeralign\"><div  onmouseover=\"ddrivetip('" + HOVER_MESSAGE_PERCEIVED_CLAIMED + "')\" onmouseout=\"hideddrivetip()\">" +
						"<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortdiff\">[PERCEIVED - CLAIMED]</input></div></th>"
				+ "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";
	}

	var summaryListLength = summaryList.length;
	for (loop = 0; loop < summaryListLength; loop++) {

		if (summaryList[loop].submitted) {
			submitted = YES;
		} else {
			submitted = NO;
		}

		output = output + "<tr>" + "<td>"
				+ encodeCharForPrint(summaryList[loop].teamName) + "</td>"
				+ "<td>";

		if (encodeChar(summaryList[loop].toStudentComments) != "") {
			output = output + "<a onmouseover=\"ddrivetip('"
					+ encodeCharForPrint(summaryList[loop].toStudentComments)
					+ "')\" onmouseout=\"hideddrivetip()\">"
					+ encodeChar(summaryList[loop].toStudentName) + "</a>"
					+ "</td>";
		} else {
			output = output + encodeChar(summaryList[loop].toStudentName)
					+ "</td>";
		}

		if (type == REVIEWER) {
			var hasEdit = false;

			if (status == "CLOSED") {
				hasEdit = true;
			}

			output = output + "<td class=\"centeralign\" id=\"status_submitted"
					+ loop + "\">" + submitted + "</td>";

			output = output
					+ "<td class=\"centeralign\">"
					+ "<a name=\"viewEvaluationResults"
					+ loop
					+ "\" id=\"viewEvaluationResults"
					+ loop
					+ "\" href=# "
					+ "onmouseover=\"ddrivetip('View feedback from the student for his team')\""
					+ "onmouseout=\"hideddrivetip()\">View</a>"
					+ "<a name=\"editEvaluationResults"
					+ loop
					+ "\" id=\"editEvaluationResults"
					+ loop
					+ "\" href=# "
					+ "onmouseover=\"ddrivetip('Edit feedback from the student for his team')\""
					+ "onmouseout=\"hideddrivetip()\""
					+ (hasEdit ? "" : disabled) + ">Edit</a>";

			output = output + "</td>" + "</tr>";

		} else {
			output = output + "<td>"
					+ displayEvaluationPoints(summaryList[loop].claimedPoints)
					+ "</td>";

			if (summaryList[loop].difference > 0) {
				output = output
						+ "<td class=\"centeralign\"><span class=\"posDiff\">"
						+ summaryList[loop].difference + "</span></td>";
			} else if (summaryList[loop].difference < 0) {
				output = output
						+ "<td class=\"centeralign\"><span class=\"negDiff\">"
						+ summaryList[loop].difference + "</span></td>";
			} else {
				output = output + "<td class=\"centeralign\">"
						+ summaryList[loop].difference + "</td>";
			}

			output = output
					+ "<td class=\"centeralign\">"
					+ "<a name=\"viewEvaluationResults" + loop + "\" id=\"viewEvaluationResults" + loop + "\" href=# "
					+ "onmouseover=\"ddrivetip('View feedback from the team for the student')\""
					+ "onmouseout=\"hideddrivetip()\">View</a>";

			output = output + "</td>" + "</tr>";
		}
	}
	output = output
			+ "</table>"
			+ "<br /><br />"
			+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />"
			+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;

	// catch actions:
	document.getElementById('radio_reviewee').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	document.getElementById('radio_reviewer').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	document.getElementById('radio_summary').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	document.getElementById('radio_detail').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};

	document.getElementById('button_sortteamname').onclick = function() {
		toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList,
				status, commentsEnabled);
	};
	document.getElementById('button_sortname').onclick = function() {
		toggleSortEvaluationSummaryListByToStudentName(submissionList,
				summaryList, status, commentsEnabled);
	};

	for (loop = 0; loop < summaryListLength; loop++) {
		if (document.getElementById('viewEvaluationResults' + loop) != null
				&& document.getElementById('viewEvaluationResults' + loop).onclick == null) {
			document.getElementById('viewEvaluationResults' + loop).onclick = function() {
				hideddrivetip();
				printEvaluationIndividualForm(submissionList, summaryList,
						this.id.substring(21, this.id.length), commentsEnabled,
						status, type);
				clearStatusMessage();
			};
		}
	}

	if (type == REVIEWER) {
		document.getElementById('button_sortsubmitted').onclick = function() {
			toggleSortEvaluationSummaryListBySubmitted(submissionList,
					summaryList, status, commentsEnabled);
		};

		for (loop = 0; loop < summaryListLength; loop++) {
			if (document.getElementById('editEvaluationResults' + loop) != null
					&& document.getElementById('editEvaluationResults' + loop).onclick == null) {
				document.getElementById('editEvaluationResults' + loop).onclick = function() {
					hideddrivetip();

					printEditEvaluationResultsByReviewer(submissionList,
							summaryList, this.id.substring(21, this.id.length),
							commentsEnabled, status);

					document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
					clearStatusMessage();
				};
			}
		}

	} else {
		document.getElementById('button_sortaverage').onclick = function() {
			toggleSortEvaluationSummaryListByAverage(submissionList,
					summaryList, status, commentsEnabled);
		};
		document.getElementById('button_sortdiff').onclick = function() {
			toggleSortEvaluationSummaryListByDiff(submissionList, summaryList,
					status, commentsEnabled);
		};

	}

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

/*
 * UI Element: print evaluation detail form type: reviewer, reviewee TODO:
 * reviewer view show original or normalized points?
 */
function printEvaluationDetailForm(submissionList, summaryList, status,
		commentsEnabled, type) {
	clearStatusMessage();

	var output = (type == REVIEWER) ? helpPrintTitle(REVIEWER_TITLE_DETAIL)
			: helpPrintTitle(REVIEWEE_TITLE_DETAIL);

	output = output + "<div id=\"detail\">";

	var summaryListLength = summaryList.length;
	for (x = 0; x < summaryListLength; x++) {
		// Team Name:
		if (x == 0 || summaryList[x].teamName != summaryList[x - 1].teamName) {
			if (x != 0)
				output = output + "</div><br />";
			output = output + helpPrintResultTeam(summaryList[x].teamName);
		}
		output = output
				+ helpPrintSubmission(submissionList, summaryList, x, status,
						commentsEnabled, type) + "</table>";
	}

	output = output
			+ "</div><br /><br />"
			+ "<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Back\" />"
			+ " <input type=\"button\" class =\"button\" name=\"button_top\" id=\"button_top\" value=\"To Top\" />";

	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;

	document.getElementById('button_top').onclick = function() {
		document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	};
	document.getElementById("button_back").onclick = function() {
		displayEvaluationsTab();
	}

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

/*
 * UI Element: print evaluation submission (individual) type: Reviewer, Reviewee
 */
function printEvaluationIndividualForm(submissionList, summaryList, position,
		commentsEnabled, status, type) {
	var points;
	var justification = "";
	var commentsToStudent = "";
	var student = "";// used to show reviewer or reviewee contents
	var toStudent = summaryList[position].toStudent;
	var output = (type == REVIEWER) ? helpPrintTitle(REVIEWER_TITLE_INDIVIDUAL)
			: helpPrintTitle(REVIEWEE_TITLE_INDIVIDUAL);
	// Team name:
	output = output + helpPrintResultTeam(summaryList[position].teamName);
	// points:
	output = output
			+ helpPrintResultHeader(
					type,
					summaryList[position].toStudentName,
					displayEvaluationPoints(summaryList[position].claimedPoints),
					displayEvaluationPoints(summaryList[position].average));
	console.log("points:" + summaryList[position].claimedPoints + "|"
			+ summaryList[position].average);
	// evaluation to others header:
	student = (type == REVIEWEE) ? FROM_STUDENT : TO_STUDENT;
	outputTemp = helpPrintResultSubheader(student);

	// justification and comments:
	var submissionListLength = submissionList.length;
	for (loop = 0; loop < submissionListLength; loop++) {
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

	// buttons:
	output = output
			+ outputTemp
			+ "</table></div>"
			+ "<br /><br />"
			+ "<input type=\"button\" class =\"button\" value=\"Previous\" name=\"button_previous\" id=\"button_previous\">"
			+ " <input type=\"button\" class =\"button\" value=\"Next\" name=\"button_next\" id=\"button_next\">";

	if (type == REVIEWER && status == "CLOSED") {
		output = output
				+ " <input type=\"button\" class =\"button\" type=\"button\" value=\"Edit\" name=\"button_edit\" id=\"button_edit\">";
	}

	output = output
			+ " <input type=\"button\" class=\"button\" value=\"Back\" name=\"button_back\" id=\"button_back\"><br /><br />";

	// --print page:
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;

	// --catch actions:
	document.getElementById('button_next').onclick = function() {
		clearStatusMessage();
		position++;
		if (position >= summaryList.length) {
			position = 0;
		}
		printEvaluationIndividualForm(submissionList, summaryList, position,
				commentsEnabled, status, type);
	};
	document.getElementById('button_previous').onclick = function() {
		clearStatusMessage();
		if (position == 0) {
			position = summaryList.length - 1;
		} else {
			position--;
		}
		printEvaluationIndividualForm(submissionList, summaryList, position,
				commentsEnabled, status, type);
	};
	document.getElementById('button_back').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	}
	if (type == REVIEWER && status == "CLOSED") {
		document.getElementById('button_edit').onclick = function() {
			printEditEvaluationResultsByReviewer(submissionList, summaryList,
					position, commentsEnabled, status)
		};
	}
}


/*----------------------------------------------------------OLD FUNCTIONS----------------------------------------------------------*/
/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function archiveCourse(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_ARCHIVECOURSE + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleArchiveCourse();
	}
}

function clearDisplay() {
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = "";
	document.getElementById(DIV_COURSE_ENROLLMENT).innerHTML = "";
	document.getElementById(DIV_COURSE_ENROLLMENTBUTTONS).innerHTML = "";
	document.getElementById(DIV_COURSE_ENROLLMENTRESULTS).innerHTML = "";
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = "";
	document.getElementById(DIV_COURSE_TABLE).innerHTML = "";
	document.getElementById(DIV_EVALUATION_EDITBUTTONS).innerHTML = "";
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = "";
	document.getElementById(DIV_EVALUATION_MANAGEMENT).innerHTML = "";
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = "";
	document.getElementById(DIV_EVALUATION_TABLE).innerHTML = "";
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = "";
	document.getElementById(DIV_STATUS_EDITEVALUATIONRESULTS).innerHTML = "";
	document.getElementById(DIV_STUDENT_EDITBUTTONS).innerHTML = "";
	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = "";
	document.getElementById(DIV_STUDENT_TABLE).innerHTML = "";
	clearStatusMessage();
}

function checkEditStudentInput(editName, editTeamName, editEmail, editGoogleID) {
	if (editName == "" || editTeamName == "" || editEmail == "") {
		setStatusMessage(DISPLAY_EDITSTUDENT_FIELDSEMPTY);
	}

	if (!isStudentNameValid(editName)) {
		setStatusMessage(DISPLAY_STUDENT_NAMEINVALID);
	}

	else if (!isStudentEmailValid(editEmail)) {
		setStatusMessage(DISPLAY_STUDENT_EMAILINVALID);
	}

	else if (!isStudentTeamNameValid(editTeamName)) {
		setStatusMessage(DISPLAY_STUDENT_TEAMNAMEINVALID);
	}
}

function checkEnrollmentInput(input) {
	input = replaceAll(input, "|", "\t");

	var entries = input.split("\n");
	var fields;

	var entriesLength = entries.length;
	for ( var x = 0; x < entriesLength; x++) {
		// Ignore blank line
		if (entries[x] != "") {
			// Separate the fields
			fields = entries[x].split("\t");
			var fieldsLength = fields.length;

			// Make sure that all fields are present
			if (fieldsLength < 3) {
				setStatusMessage("<font color=\"#F00\">Line " + (x + 1)
						+ ":</font> " + DISPLAY_ENROLLMENT_FIELDSMISSING);
			}

			else if (fieldsLength > 4) {
				setStatusMessage("<font color=\"#F00\">Line " + (x + 1)
						+ ":</font> " + DISPLAY_ENROLLMENT_FIELDSEXTRA);
			}

			// Check that fields are correct
			if (!isStudentNameValid(trim(fields[1]))) {
				setStatusMessage("<font color=\"#F00\">Line " + (x + 1)
						+ ":</font> " + DISPLAY_STUDENT_NAMEINVALID);
			}

			else if (!isStudentEmailValid(trim(fields[2]))) {
				setStatusMessage("<font color=\"#F00\">Line " + (x + 1)
						+ ":</font> " + DISPLAY_STUDENT_EMAILINVALID);
			}

			else if (!isStudentTeamNameValid(trim(fields[0]))) {
				setStatusMessage("<font color=\"#F00\">Line " + (x + 1)
						+ ":</font> " + DISPLAY_STUDENT_TEAMNAMEINVALID);
			}
		}
	}

}

function convertDateFromDDMMYYYYToMMDDYYYY(dateString) {
	var newDateString = dateString.substring(3, 5) + "/"
			+ dateString.substring(0, 2) + "/" + dateString.substring(6, 10);

	return newDateString;
}

function convertDateToDDMMYYYY(date) {
	var string;

	if (date.getDate() < 10) {
		string = "0" + date.getDate();
	}

	else {
		string = date.getDate();
	}

	string = string + "/";

	if (date.getMonth() + 1 < 10) {
		string = string + "0" + (date.getMonth() + 1);
	}

	else {
		string = string + (date.getMonth() + 1);
	}

	string = string + "/" + date.getFullYear();

	return string;
}

function convertDateToHHMM(date) {
	var string;

	if (date.getHours() < 10) {
		string = "0" + date.getHours();
	}

	else {
		string = "" + date.getHours();
	}

	if (date.getMinutes() < 10) {
		string = string + "0" + date.getMinutes();
	}

	else {
		string = string + date.getMinutes();
	}

	return string;
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function deleteAllStudents(courseID) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETEALLSTUDENTS
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleDeleteAllStudents(courseID);
	}
}



/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function deleteEvaluation(courseID, name) {
	if (xmlhttp) {

		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETEEVALUATION
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));

		return handleDeleteEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function deleteStudent(courseID, studentEmail) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETESTUDENT + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ STUDENT_EMAIL + "=" + encodeURIComponent(studentEmail));

		return handleDeleteStudent();
	}
}

function displayCourseInformation(courseID) {
	clearDisplay();
	doGetCourse(courseID);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}



function displayEditEvaluation(evaluationList, loop) {
	var courseID = evaluationList[loop].courseID;
	var name = evaluationList[loop].name;
	var instructions = evaluationList[loop].instructions;
	var start = evaluationList[loop].start;
	var deadline = evaluationList[loop].deadline;
	var timeZone = evaluationList[loop].timeZone;
	var gracePeriod = evaluationList[loop].gracePeriod;
	var status = evaluationList[loop].status;
	var activated = evaluationList[loop].activated;
	var commentsEnabled = evaluationList[loop].commentsEnabled;

	clearDisplay();
	printEditEvaluation(courseID, name, instructions, commentsEnabled, start,
			deadline, timeZone, gracePeriod, status, activated);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEditStudent(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	clearDisplay();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	printEditStudent(courseID, email, name, teamName, googleID,
			registrationKey, comments);
}

function displayEnrollmentPage(courseID) {
	clearDisplay();
	printEnrollmentPage(courseID);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEnrollmentResultsPage(reports) {
	clearDisplay();
	printEnrollmentResultsPage(reports);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}


function displayStudentInformation(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	clearDisplay();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	printStudent(courseID, email, name, teamName, googleID, registrationKey,
			comments);
}

function doAddEvaluation(courseID, name, instructions, commentsEnabled, start,
		startTime, deadline, deadlineTime, timeZone, gracePeriod) {
	setStatusMessage(DISPLAY_LOADING);

	var results = addEvaluation(courseID, name, instructions, commentsEnabled,
			start, startTime, deadline, deadlineTime, timeZone, gracePeriod);

	clearStatusMessage();
	var emptyTeam = 0;

	if (results == 0) {
		displayEvaluationsTab();
		var studentList = getStudentList(courseID);
		if (studentList != 1) {
			for (i = 0; i < studentList.length; i++) {
				studentList[i].teamName = studentList[i].teamName.replace(
						/^\s*|\s*$/, "");
				if (studentList[i].teamName == "")
					emptyTeam = 1;
			}
		}
		if (emptyTeam == 1)
			setStatusMessage(DISPLAY_EVALUATION_ADDED_WITH_EMPTY_TEAMS);
		else
			setStatusMessage(DISPLAY_EVALUATION_ADDED);
	}

	else if (results == 1) {
		alert(DISPLAY_SERVERERROR);
	}

	else if (results == 2) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}

	else if (results == 3) {
		setStatusMessage(DISPLAY_EVALUATION_NAMEINVALID);
	}

	else if (results == 4) {
		setStatusMessage(DISPLAY_EVALUATION_NAME_LENGTHINVALID);
	}

	else if (results == 5) {
		setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID);
	}

	else if (results == 6) {
		setStatusMessage(DISPLAY_EVALUATION_EXISTS);
	}

	else if (results == 7) {
		setStatusMessage(DISPLAY_COURSE_NOTEAMS);
	}

}

function doArchiveCourse(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = archiveCourse(courseID);

	if (results == 0) {
		getAndPrintCourseList();
		setStatusMessage(DISPLAY_COURSE_ARCHIVED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}



function doDeleteEvaluation(courseID, name, isHome) {
	setStatusMessage(DISPLAY_LOADING);

	var results = deleteEvaluation(courseID, name);

	if (results == 0) {
		if (isHome) {
			printCoordinatorLandingPage();
		} else {
			doGetEvaluationList();
		}

		setStatusMessage(DISPLAY_EVALUATION_DELETED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doDeleteAllStudents(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = deleteAllStudents(courseID);

	if (results != 1) {
		doGetCourse(courseID);
		setStatusMessage(DISPLAY_COURSE_DELETEDALLSTUDENTS
				+ " Click <a class='t_course_enrol' href=\"javascript:displayEnrollmentPage('"
				+ courseID + "');\">here</a> to enrol students.");
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doDeleteStudent(courseID, email) {
	setStatusMessage(DISPLAY_LOADING);

	var student = getStudent(courseID, email);

	var results = deleteStudent(courseID, email);

	if (results != 1) {
		displayCourseInformation(courseID);
		setStatusMessage(DISPLAY_COURSE_DELETEDSTUDENT);

		// deleting team profile if all students of a particular team are
		// deleted
		var teams = getTeamsOfCourse(courseID);
		var loop;
		var teamNameExists = 0;
		for (loop = 0; loop < teams.length; loop++) {
			if (teams[loop].teamName == student.teamName)
				teamNameExists = 1;
		}
		if (teamNameExists == 0)
			deleteTeamProfile(courseID, student.teamName);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditEvaluation(courseID, name, editStart, editStartTime,
		editDeadline, editDeadlineTime, timeZone, editGracePeriod,
		editInstructions, editCommentsEnabled, activated, status) {
	setStatusMessage(DISPLAY_LOADING);

	var results = editEvaluation(courseID, name, editStart, editStartTime,
			editDeadline, editDeadlineTime, timeZone, editGracePeriod,
			editInstructions, editCommentsEnabled, activated, status)

	if (results == 0) {
		if (activated == true) {
			displayEvaluationsTab();
			toggleInformStudentsOfEvaluationChanges(courseID, name);
		}

		else {
			displayEvaluationsTab();
			setStatusMessage(DISPLAY_EVALUATION_EDITED);
		}

	}

	else if (results == 2) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}

	else if (results == 3) {
		setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID);
	}

	else if (results == 4) {
		displayEvaluationsTab();
		setStatusMessage(DISPLAY_EVALUATION_EDITED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditEvaluationResultsByReviewer(form, summaryList, position,
		commentsEnabled, status) {
	toggleEditEvaluationResultsStatusMessage(DISPLAY_LOADING);

	var submissionList = extractSubmissionList(form);

	var results = editEvaluationResults(submissionList, commentsEnabled);

	if (results == 0) {
		if(xmlhttp){
			sendGetSubmissionListRequest(submissionList[0].courseID, submissionList[0].evaluationName);
			submissionList = processGetSubmissionListResponse();
		}
		
		if (submissionList != 1) {
			printEvaluationIndividualForm(submissionList, summaryList,
					position, commentsEnabled, status, REVIEWER);
			document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
			toggleEditEvaluationResultsStatusMessage("");
			setStatusMessage(DISPLAY_EVALUATION_RESULTSEDITED);
		} else {
			alert(DISPLAY_SERVERERROR);
		}
	}

	else if (results == 2) {
		toggleEditEvaluationResultsStatusMessage(DISPLAY_FIELDS_EMPTY);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditStudent(courseID, email, editName, editTeamName, editEmail,
		editGoogleID, editComments) {
	setStatusMessage(DISPLAY_LOADING);

	var results = editStudent(courseID, email, editName, editTeamName,
			editEmail, editGoogleID, editComments);

	if (results == 0) {
		displayCourseInformation(courseID);
		setStatusMessage(DISPLAY_STUDENT_EDITED);
	}

	else if (results == 2) {
		displayCourseInformation(courseID);
		setStatusMessage("Duplicated Email found. Cannot edit student information");
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doEnrolStudents(input, courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = enrolStudents(input, courseID);

	clearStatusMessage();

	if (results == 1) {
		alert(DISPLAY_SERVERERROR);
	}

	else if (results == 2) {

	}

	else if (results == 3) {
		checkEnrollmentInput(input)
	}

	else {
		displayEnrollmentResultsPage(results);
		createProfileOfExistingTeams(courseID);
	}

}

function doGetCourse(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var courseInfo = getCourse(courseID);
	var studentInfo = getStudentList(courseID);

	clearStatusMessage();

	if (courseInfo != 1) {
		printCourseCoordinatorForm(courseInfo);
	} else {
		alert(DISPLAY_SERVERERROR);
	}

	if (studentInfo != 1) {
		// toggleSortStudentsByName calls printStudentList too
		if (studentSortStatus == studentSort.name) {
			toggleSortStudentsByName(studentInfo, courseID);
		} else if (studentSortStatus == studentSort.status) {
			toggleSortStudentsByStatus(studentInfo, courseID);
		} else {
			toggleSortStudentsByTeamName(studentInfo, courseID);
		}
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetCourseIDList() {
	setStatusMessage(DISPLAY_LOADING);

	sendGetCourseListRequest();
	var results = processGetCourseListResponse();

	if (results != 1) {
		var resultsLength = results.length;
		var courseIDList = new Array();
		
		for (var loop = 0; loop < resultsLength; loop++) {
			courseIDList[loop] = {
				courseID : results[loop].ID
			};
		}
				
		return courseIDList;
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}
function doGetCourseIDOptions(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	sendGetCourseListRequest();
	var results = processGetCourseListResponse();

	if (results != 1) {
		populateCourseIDOptions(courseID, results);
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doInformStudentsOfEvaluationChanges(courseID, name) {
	setStatusMessage(DISPLAY_LOADING);

	var results = informStudentsOfEvaluationChanges(courseID, name);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_EVALUATION_INFORMEDSTUDENTSOFCHANGES);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doPublishEvaluation(courseID, name, reload, isHome) {
	setStatusMessage(DISPLAY_LOADING);

	var results = publishEvaluation(courseID, name);

	clearStatusMessage();

	if (results != 1) {
		if (isHome) {
			printCoordinatorLandingPage();
		} else if (reload) {
			doGetEvaluationList();
		} else {
			document.getElementById('button_publish').value = "Unpublish";
			document.getElementById('button_publish').onclick = function() {
				togglePublishEvaluation(courseID, name, false, false,isHome);
			};
		}

		setStatusMessage(DISPLAY_EVALUATION_PUBLISHED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}

}

function doUnpublishEvaluation(courseID, name, reload,isHome) {
	setStatusMessage(DISPLAY_LOADING);

	var results = unpublishEvaluation(courseID, name);

	clearStatusMessage();

	if (results != 1) {
		if (isHome) {
			printCoordinatorLandingPage();
		} else if (reload) {
			doGetEvaluationList();
		} else {
			document.getElementById('button_publish').value = "Publish";
			document.getElementById('button_publish').onclick = function() {
				togglePublishEvaluation(courseID, name, true, false,isHome);
			};
		}

		setStatusMessage(DISPLAY_EVALUATION_UNPUBLISHED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}

}

function doRemindStudents(courseID, evaluationName) {
	setStatusMessage(DISPLAY_LOADING);

	var results = remindStudents(courseID, evaluationName);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_EVALUATION_REMINDERSSENT);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doSendRegistrationKey(courseID, email, name) {
	setStatusMessage(DISPLAY_LOADING);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);

	var results = sendRegistrationKey(courseID, email);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_COURSE_SENTREGISTRATIONKEY + name + ".");
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doSendRegistrationKeys(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = sendRegistrationKeys(courseID);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_COURSE_SENTREGISTRATIONKEYS);
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doUnarchiveCourse(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = unarchiveCourse(courseID);

	if (results == 0) {
		getAndPrintCourseList();
		setStatusMessage(DISPLAY_COURSE_UNARCHIVED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: schedule invalid 4: no
 * changes made
 */
function editEvaluation(courseID, name, editStart, editStartTime, editDeadline,
		editDeadlineTime, timeZone, editGracePeriod, editInstructions,
		editCommentsEnabled, activated, status) {
	setStatusMessage(DISPLAY_LOADING);

	if (courseID == "" || name == "" || editStart == "" || editStartTime == ""
			|| editDeadline == "" || editDeadlineTime == ""
			|| editGracePeriod == "" || editInstructions == ""
			|| editCommentsEnabled == "") {
		return 2;
	}

	else if (!isEditEvaluationScheduleValid(editStart, editStartTime,
			editDeadline, editDeadlineTime, timeZone, activated, status)) {
		return 3;
	}

	else {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITEVALUATION + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name) + "&"
				+ EVALUATION_DEADLINE + "=" + encodeURIComponent(editDeadline)
				+ "&" + EVALUATION_DEADLINETIME + "="
				+ encodeURIComponent(editDeadlineTime) + "&"
				+ EVALUATION_INSTRUCTIONS + "="
				+ encodeURIComponent(editInstructions) + "&" + EVALUATION_START
				+ "=" + encodeURIComponent(editStart) + "&"
				+ EVALUATION_STARTTIME + "="
				+ encodeURIComponent(editStartTime) + "&"
				+ EVALUATION_GRACEPERIOD + "="
				+ encodeURIComponent(editGracePeriod) + "&"
				+ EVALUATION_COMMENTSENABLED + "=" + editCommentsEnabled);

		return handleEditEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields missing
 * 
 */
function editEvaluationResults(submissionList, commentsEnabled) {
	var submissionListLength = submissionList.length;
	for (loop = 0; loop < submissionListLength; loop++) {
		if (submissionList[loop].points == -999) {
			return 2;
		}

		if (!commentsEnabled) {
			submissionList[loop].commentsToStudent = "";
		}
	}

	var request = "operation=" + OPERATION_COORDINATOR_EDITEVALUATIONRESULTS
			+ "&" + STUDENT_NUMBEROFSUBMISSIONS + "=" + submissionListLength
			+ "&" + COURSE_ID + "=" + submissionList[0].courseID + "&"
			+ EVALUATION_NAME + "=" + submissionList[0].evaluationName + "&"
			+ STUDENT_TEAMNAME + "=" + submissionList[0].teamName;

	for (loop = 0; loop < submissionListLength; loop++) {
		var toStudent;

		request = request + "&" + STUDENT_FROMSTUDENT + loop + "="
				+ encodeURIComponent(submissionList[loop].fromStudent) + "&"
				+ STUDENT_TOSTUDENT + loop + "="
				+ encodeURIComponent(submissionList[loop].toStudent) + "&"
				+ STUDENT_POINTS + loop + "="
				+ encodeURIComponent(submissionList[loop].points) + "&"
				+ STUDENT_JUSTIFICATION + loop + "="
				+ encodeURIComponent(submissionList[loop].justification) + "&"
				+ STUDENT_COMMENTSTOSTUDENT + loop + "="
				+ encodeURIComponent(submissionList[loop].commentsToStudent);
	}

	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send(request);

		return handleEditEvaluationResults();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function editStudent(courseID, email, editName, editTeamName, editEmail,
		editGoogleID, editComments) {
	editName = trim(editName);
	editTeamName = trim(editTeamName);
	editEmail = trim(editEmail);
	editGoogleID = trim(editGoogleID);

	if (isEditStudentInputValid(editName, editTeamName, editEmail, editGoogleID)) {
		if (xmlhttp) {
			xmlhttp.open("POST", "/teammates", false);
			xmlhttp.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITSTUDENT + "&"
					+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
					+ STUDENT_EMAIL + "=" + encodeURIComponent(email) + "&"
					+ STUDENT_EDITNAME + "=" + encodeURIComponent(editName)
					+ "&" + STUDENT_EDITTEAMNAME + "="
					+ encodeURIComponent(editTeamName) + "&"
					+ STUDENT_EDITEMAIL + "=" + encodeURIComponent(editEmail)
					+ "&" + STUDENT_EDITGOOGLEID + "="
					+ encodeURIComponent(editGoogleID) + "&"
					+ STUDENT_EDITCOMMENTS + "="
					+ encodeURIComponent(editComments));
			return handleEditStudent();
		}
	}
}

/*
 * Returns
 * 
 * reports: successful 1: server error 2: input empty 3: input invalid
 * 
 */
function enrolStudents(input, courseID) {
	input = replaceAll(input, "|", "\t");

	if (xmlhttp) {
		// Remove trailing "\n"
		if (input.lastIndexOf("\n") == input.length - 1) {
			input = input.substring(0, input.length - 1);
		}

		if (input == "") {
			return 2;
		}

		else if (isEnrollmentInputValid(input)) {
			sendEnrolStudentsRequest(input,courseID);
			return processEnrolStudentsResponse();
		}

		else {
			return 3;
		}
	}
}

function sendEnrolStudentsRequest(input,courseID)
{
	xmlhttp.open("POST", "/teammates", false);
	xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded;");
	xmlhttp.send("operation=" + OPERATION_COORDINATOR_ENROLSTUDENTS
			+ "&" + STUDENT_INFORMATION + "="
			+ encodeURIComponent(input) + "&" + COURSE_ID + "="
			+ encodeURIComponent(courseID));
	
}


function extractSubmissionList(form) {
	var submissionList = [];

	var counter = 0;

	var fromStudent;
	var toStudent;
	var teamName;
	var courseID;
	var evaluationName;
	var points;
	var justification;
	var commentsToStudent;

	var formLength = form.length;
	for (loop = 0; loop < formLength; loop++) {
		fromStudent = form.elements[loop++].value;
		toStudent = form.elements[loop++].value;
		teamName = form.elements[loop++].value;
		courseID = form.elements[loop++].value;
		evaluationName = form.elements[loop++].value;

		points = form.elements[loop++].value;
		justification = form.elements[loop++].value;
		commentsToStudent = form.elements[loop].value;

		submissionList[counter++] = {
			fromStudent : fromStudent,
			toStudent : toStudent,
			courseID : courseID,
			evaluationName : evaluationName,
			teamName : teamName,
			points : points,
			justification : justification,
			commentsToStudent : commentsToStudent
		};
	}

	return submissionList;
}

/*
 * Returns
 * 
 * courseInfo: successful 1: server error
 * 
 */
function getCourse(courseID) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETCOURSE + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleGetCourse();
	}
}



function getDateWithTimeZoneOffset(timeZone) {
	var now = new Date();

	// Convert local time zone to ms
	var nowTime = now.getTime();

	// Obtain local time zone offset
	var localOffset = now.getTimezoneOffset() * 60000;

	// Obtain UTC time
	var UTC = nowTime + localOffset;

	// Add the time zone of evaluation
	var nowMilliS = UTC + (timeZone * 60 * 60 * 1000);

	now.setTime(nowMilliS);

	return now;
}

/*
 * Returns
 * 
 * evaluationList: successful 1: server error
 * 
 */
function getEvaluationList() {
	if (xmlhttp) {
		OPERATION_CURRENT = OPERATION_COORDINATOR_GETEVALUATIONLIST;

		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETEVALUATIONLIST);

		return handleGetEvaluationList();
	}
}
/*
 * Returns
 * 
 * evaluationList: successful 1: server error
 */
function getEvaluationListOfCourse(courseID) {
	if (xmlhttp) {
		OPERATION_CURRENT = OPERATION_COORDINATOR_GETEVALUATIONLISTOFCOURSE;

		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETEVALUATIONLISTOFCOURSE + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleGetEvaluationList();
	}
}

// return the value of the radio button that is checked
// return an empty string if none are checked, or
// there are no radio buttons
function getCheckedValue(radioObj) {
	if (!radioObj)
		return "";
	var radioLength = radioObj.length;
	if (radioLength == undefined)
		if (radioObj.checked)
			return radioObj.value;
		else
			return "";
	for ( var i = 0; i < radioLength; i++) {
		if (radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	return "";
}

/*
 * Returns
 * 
 * studentList: successful 1: server error
 * 
 */
function getStudentList(courseID) {
	if (xmlhttp) {

		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSTUDENTLIST + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleGetStudentList();
	}
}



function getXMLObject() {
	var xmlHttp = false;
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")
	} catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")
		} catch (e2) {
			xmlHttp = false
		}
	}
	if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
		xmlHttp = new XMLHttpRequest();
	}
	return xmlHttp;
}



/*
 * Returns
 * 
 * 0: successful 1: server error 6: evaluation exists 7: course has no teams
 * 
 */
function handleAddEvaluation() {
	if (xmlhttp.status == CONNECTION_OK) {
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		var message;

		if (status != null) {
			var message = status.firstChild.nodeValue;

			if (message == MSG_EVALUATION_EXISTS) {
				return 6;
			}

			else if (message == MSG_COURSE_NOTEAMS) {
				return 7;
			}

			else {
				return 0;
			}
		}
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleArchiveCourse() {
	if (xmlhttp) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleDeleteAllStudents(courseID) {
	if (xmlhttp) {
		return 0;
	}

	else {
		return 1;
	}
}


/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleDeleteEvaluation() {
	if (xmlhttp) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleDeleteStudent() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 4: no changes made
 * 
 */
function handleEditEvaluation() {
	if (xmlhttp.status == CONNECTION_OK) {
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];

		if (status != null) {
			var message = status.firstChild.nodeValue;

			if (message == MSG_EVALUATION_EDITED) {
				return 0;
			}

			else {
				return 4;
			}
		}
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleEditEvaluationResults() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function handleEditStudent() {
	if (xmlhttp.status == CONNECTION_OK) {
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];

		if (status != null) {
			var message = status.firstChild.nodeValue;

			if (message == MSG_EVALUATION_UNABLETOCHANGETEAMS) {
				return 2;
			}

			else {
				return 0;
			}
		}
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * reports: successful 1: server error
 * 
 */
function processEnrolStudentsResponse() {
	if (xmlhttp.status == CONNECTION_OK) {
		var enrollmentReports = xmlhttp.responseXML
				.getElementsByTagName("enrollmentreports")[0];

		if (enrollmentReports != null) {
			var enrollmentReport;
			var studentName;
			var studentEmail;
			var status;
			var nameEdited;
			var teamNameEdited;
			var commentsEdited;

			var reports = [];

			var enrollmentReportsChildNodesLength = enrollmentReports.childNodes.length;
			for (loop = 0; loop < enrollmentReportsChildNodesLength; loop++) {
				enrollmentReport = enrollmentReports.childNodes[loop];

				studentName = enrollmentReport
						.getElementsByTagName(STUDENT_NAME)[0].firstChild.nodeValue;
				studentEmail = enrollmentReport
						.getElementsByTagName(STUDENT_EMAIL)[0].firstChild.nodeValue;
				status = enrollmentReport.getElementsByTagName(STUDENT_STATUS)[0].firstChild.nodeValue;
				nameEdited = enrollmentReport
						.getElementsByTagName(STUDENT_NAMEEDITED)[0].firstChild.nodeValue;
				teamNameEdited = enrollmentReport
						.getElementsByTagName(STUDENT_TEAMNAMEEDITED)[0].firstChild.nodeValue;
				commentsEdited = enrollmentReport
						.getElementsByTagName(STUDENT_COMMENTSEDITED)[0].firstChild.nodeValue;

				enrollmentReport = {
					studentName : studentName,
					studentEmail : studentEmail,
					nameEdited : nameEdited,
					teamNameEdited : teamNameEdited,
					commentsEdited : commentsEdited,
					status : status
				};

				reports.push(enrollmentReport);

			}
		}

		return reports;
	}

	else {
		return 1;
	}

}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleInformStudentsOfEvaluationChanges() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * courseInfo: successful 1: server error
 * 
 */
function handleGetCourse() {
	if (xmlhttp.status == CONNECTION_OK) {
		var courses = xmlhttp.responseXML.getElementsByTagName("courses")[0];
		var courseInfo;

		if (courses != null) {
			var course;
			var ID;
			var name;
			var numberofteams;
			var status;

			course = courses.childNodes[0];
			ID = course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
			name = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
			numberOfTeams = course.getElementsByTagName(COURSE_NUMBEROFTEAMS)[0].firstChild.nodeValue;
			status = course.getElementsByTagName(COURSE_STATUS)[0].firstChild.nodeValue;
			courseInfo = {
				ID : ID,
				name : name,
				numberOfTeams : numberOfTeams,
				status : status
			};

			return courseInfo;
		}

	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * courseIDList: successful 1: server error
 * 
 */
function handleGetCourseIDList() {
	if (xmlhttp.status == CONNECTION_OK) {
		clearStatusMessage();

		var courses = xmlhttp.responseXML.getElementsByTagName("courses")[0];
		var course;
		var courseID;
		var courseIDList = new Array();

		var coursesChildNodesLength = courses.childNodes.length;
		for (loop = 0; loop < coursesChildNodesLength; loop++) {
			course = courses.childNodes[loop];
			courseID = course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
			courseIDList[loop] = {
				courseID : courseID
			};
		}

		return courseIDList;
	}

	else {
		return 1;
	}
}



/*
 * Returns
 * 
 * evaluationList: successful 1: server error
 * 
 */
function handleGetEvaluationList() {
	if (xmlhttp.status == CONNECTION_OK) {
		var evaluations = xmlhttp.responseXML
				.getElementsByTagName("evaluations")[0];
		var evaluationList = new Array();
		var now;
		var nowMilliS;
		var nowTime;
		var localOffset;
		var UTC;

		var evaluation;
		var courseID;
		var name;
		var commentsEnabled;
		var instructions;
		var start;
		var deadline;
		var gracePeriod;
		var numberOfCompletedEvaluations;
		var numberOfEvaluations;
		var published;
		var status;
		var activated;

		if (evaluations != null) {
			var evaluationsChildNodesLength = evaluations.childNodes.length;
			for (loop = 0; loop < evaluationsChildNodesLength; loop++) {
				evaluation = evaluations.childNodes[loop];

				courseID = evaluation.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				name = evaluation.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
				commentsEnabled = (evaluation
						.getElementsByTagName(EVALUATION_COMMENTSENABLED)[0].firstChild.nodeValue
						.toLowerCase() == "true");
				instructions = evaluation
						.getElementsByTagName(EVALUATION_INSTRUCTIONS)[0].firstChild.nodeValue;
				start = new Date(
						evaluation.getElementsByTagName(EVALUATION_START)[0].firstChild.nodeValue);
				deadline = new Date(
						evaluation.getElementsByTagName(EVALUATION_DEADLINE)[0].firstChild.nodeValue);
				timeZone = parseFloat(evaluation
						.getElementsByTagName(EVALUATION_TIMEZONE)[0].firstChild.nodeValue);
				gracePeriod = parseInt(evaluation
						.getElementsByTagName(EVALUATION_GRACEPERIOD)[0].firstChild.nodeValue);
				published = (evaluation
						.getElementsByTagName(EVALUATION_PUBLISHED)[0].firstChild.nodeValue
						.toLowerCase() == "true");
				activated = (evaluation
						.getElementsByTagName(EVALUATION_ACTIVATED)[0].firstChild.nodeValue
						.toLowerCase() == "true");
				numberOfCompletedEvaluations = parseInt(evaluation
						.getElementsByTagName(EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS)[0].firstChild.nodeValue);
				numberOfEvaluations = parseInt(evaluation
						.getElementsByTagName(EVALUATION_NUMBEROFEVALUATIONS)[0].firstChild.nodeValue);

				now = getDateWithTimeZoneOffset(timeZone);

				// Check if evaluation should be open or closed
				if (now > start && deadline > now) {
					status = "OPEN";
				}

				else if (now > deadline || activated) {
					status = "CLOSED";
				}

				else if (now < start && !activated) {
					status = "AWAITING";
				}

				if (published == true) {
					status = "PUBLISHED";
				}

				evaluationList[loop] = {
					courseID : courseID,
					name : name,
					commentsEnabled : commentsEnabled,
					instructions : instructions,
					start : start,
					deadline : deadline,
					timeZone : timeZone,
					gracePeriod : gracePeriod,
					published : published,
					published : published,
					activated : activated,
					numberOfCompletedEvaluations : numberOfCompletedEvaluations,
					numberOfEvaluations : numberOfEvaluations,
					status : status
				};
			}
		}

		return evaluationList;
	} else {
		return 1;
	}
}

/*
 * Returns
 * 
 * studentList: successful 1: server error
 * 
 */
function handleGetStudentList() {
	if (xmlhttp.status == CONNECTION_OK) {
		var students = xmlhttp.responseXML.getElementsByTagName("students")[0];
		var studentList = new Array();

		var student;
		var name;
		var teamName;
		var email;
		var registrationKey;
		var comments;
		var courseID;
		var googleID;

		var studentsChildNodesLength = students.childNodes.length;
		for ( var loop = 0; loop < studentsChildNodesLength; loop++) {
			student = students.childNodes[loop];

			name = student.getElementsByTagName(STUDENT_NAME)[0].firstChild.nodeValue;
			teamName = student.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
			email = student.getElementsByTagName(STUDENT_EMAIL)[0].firstChild.nodeValue;
			registrationKey = student.getElementsByTagName(STUDENT_REGKEY)[0].firstChild.nodeValue;
			googleID = student.getElementsByTagName(STUDENT_ID)[0].firstChild.nodeValue;
			comments = student.getElementsByTagName(STUDENT_COMMENTS)[0].firstChild.nodeValue;
			courseID = student.getElementsByTagName(STUDENT_COURSEID)[0].firstChild.nodeValue;
			studentList[loop] = {
				name : name,
				teamName : teamName,
				email : email,
				registrationKey : registrationKey,
				googleID : googleID,
				comments : comments,
				courseID : courseID
			};
		}

		return studentList;
	}

	else {
		return 1;
	}
}



function handleLogout() {
	if (xmlhttp.status == CONNECTION_OK) {
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handlePublishEvaluation() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleUnpublishEvaluation() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

function handleRemindStudents() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleSendRegistrationKey() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function handleSendRegistrationKeys() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleUnarchiveCourse() {
	if (xmlhttp) {
		return 0;
	}

	else {
		return 1;
	}
}

function informStudentsOfEvaluationChanges(courseID, name) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation="
				+ OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));
	}

	return handleInformStudentsOfEvaluationChanges();
}

function isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime) {
	var start = convertDateFromDDMMYYYYToMMDDYYYY(start);
	var deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);

	var now = new Date();

	start = new Date(start);
	deadline = new Date(deadline);

	if (startTime != "24") {
		start.setHours(parseInt(startTime));
	}

	else {
		start.setHours(23);
		start.setMinutes(59);
	}

	if (deadlineTime != "24") {
		deadline.setHours(parseInt(deadlineTime));
	}

	else {
		deadline.setHours(23);
		deadline.setMinutes(59);
	}

	if (start > deadline) {
		return false;
	}

	else if (now > start) {
		return false;
	}

	else if (!(start > deadline || deadline > start)) {
		if (parseInt(startTime) >= parseInt(deadlineTime)) {
			return false;
		}
	}

	return true;
}



function isEditEvaluationScheduleValid(start, startTime, deadline,
		deadlineTime, timeZone, activated, status) {
	var startString = convertDateFromDDMMYYYYToMMDDYYYY(start);
	var deadlineString = convertDateFromDDMMYYYYToMMDDYYYY(deadline);

	var now = getDateWithTimeZoneOffset(timeZone);

	start = new Date(startString);
	deadline = new Date(deadlineString);

	if (startTime != "24") {
		start.setHours(parseInt(startTime));
	}

	else {
		start.setHours(23);
		start.setMinutes(59);
	}

	if (deadlineTime != "24") {
		deadline.setHours(parseInt(deadlineTime));
	}

	else {
		deadline.setHours(23);
		deadline.setMinutes(59);
	}

	if (start > deadline) {
		return false;
	}

	else if (status == "AWAITING") {
		// Open evaluation should be done by system only.
		// Thus, coordinator cannot change evaluation ststus from AWAITING to
		// OPEN
		if (start < now) {
			return false;
		}
	}

	// else if(now > deadline)
	// {
	// return false;
	// }
	//
	// else if(!(start > deadline || deadline > start))
	// {
	// if(parseInt(startTime) >= parseInt(deadlineTime))
	// {
	// return false;
	// }
	// }
	//	
	// else if(!activated && start < now)
	// {
	// return false;
	// }
	//	
	return true;
}

function isEditStudentInputValid(editName, editTeamName, editEmail,
		editGoogleID) {
	if (editName == "" || editTeamName == "" || editEmail == "") {
		return false;
	}

	if (!isStudentNameValid(editName)) {
		return false;
	}

	else if (!isStudentEmailValid(editEmail)) {
		return false;
	}

	else if (!isStudentTeamNameValid(editTeamName)) {
		return false;
	}

	return true;
}

function isEnrollmentInputValid(input) {
	var entries = input.split("\n");
	var fields;

	var entriesLength = entries.length;
	for ( var x = 0; x < entriesLength; x++) {
		if (entries[x] != "") {
			// Separate the fields
			fields = entries[x].split("\t");
			var fieldsLength = fields.length;

			// Make sure that all fields are present
			if (fieldsLength < 3) {
				return false;
			}

			else if (fieldsLength > 4) {
				return false;
			}

			// Check that fields are correct
			if (!isStudentNameValid(trim(fields[1]))) {
				return false;
			}

			else if (!isStudentEmailValid(trim(fields[2]))) {
				return false;
			}

			else if (!isStudentTeamNameValid(trim(fields[0]))) {
				return false;
			}
		}
	}

	return true;
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

function isEvaluationNameLengthValid(name) {
	if (name.length > 22) {
		return false;
	}

	return true;
}

function isStudentEmailValid(email) {
	if (email
			.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i) != null
			&& email.length <= 40) {
		return true;
	}

	return false;
}

function isStudentNameValid(name) {
	if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
			|| name.indexOf("\"") >= 0) {
		return false;
	}

	else if (name.match(/^.[^\t]*$/) == null) {
		return false;
	}

	else if (name.length > 40) {
		return false;
	}

	return true;
}

function isStudentTeamNameValid(teamName) {
	if (teamName.length > 24) {
		return false;
	}

	return true;
}

function logout() {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_LOGOUT);
	}

	handleLogout();
}

function populateCourseIDOptions(courseID,courseList) {
	var option = document.createElement("OPTION");

	var courseListLength = courseList.length;
	for (x = 0; x < courseListLength; x++) {
		option = document.createElement("OPTION");
		option.text = courseList[x].ID;
		option.value = courseList[x].ID;
        if (courseID != "") {
        	if (courseID == courseList[x].ID) {
        			option.selected = true;
        	}
        }
		document.form_addevaluation.courseid.options.add(option);
	}
}

function populateEditEvaluationResultsPointsForm(form, submissionList) {
	var points;
	var submissionListLength = submissionList.length;

	var len = form.elements.length / 8;
	for (x = 0; x < len; x++) {
		for (y = 0; y < submissionListLength; y++) {
			if (submissionList[y].fromStudent == form.elements[x * 8].value
					&& submissionList[y].toStudent == form.elements[x * 8 + 1].value) {
				points = submissionList[y].points;
				break;
			}
		}

		setSelectedOption(form.elements[x * 8 + 5], points)
	}
}

// xl: new added
function printEvaluationReportByAction(submissionList, summaryList, status,
		commentsEnabled) {
	// clean page:
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	clearStatusMessage();

	// case 1: [x]reviewee [x]summary..............case handler............
	if (document.getElementById('radio_reviewee').checked
			&& document.getElementById('radio_summary').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
		printEvaluationSummaryForm(submissionList, summaryList
				.sort(sortByTeamName), status, commentsEnabled, REVIEWEE);
	}

	// case 2: [x]reviewer [x]summary
	else if (document.getElementById('radio_reviewer').checked
			&& document.getElementById('radio_summary').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
		printEvaluationSummaryForm(submissionList, summaryList
				.sort(sortByTeamName), status, commentsEnabled, REVIEWER);
	}

	// case 3: [x]reviewee [x]detail
	else if (document.getElementById('radio_reviewee').checked
			&& document.getElementById('radio_detail').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
		printEvaluationDetailForm(submissionList, summaryList, status,
				commentsEnabled, REVIEWEE);
	}

	// case 4: [x]reviewer [x]detail
	else if (document.getElementById('radio_reviewer').checked
			&& document.getElementById('radio_detail').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
		printEvaluationDetailForm(submissionList, summaryList, status,
				commentsEnabled, REVIEWER);
	}

	// else:
	else {
		// do nothing
	}

}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function publishEvaluation(courseID, name) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_PUBLISHEVALUATION
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));

		return handlePublishEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function unpublishEvaluation(courseID, name) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_UNPUBLISHEVALUATION
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));

		return handleUnpublishEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function remindStudents(courseID, evaluationName) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_REMINDSTUDENTS + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(evaluationName));

		handleRemindStudents();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function sendRegistrationKey(courseID, email) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEY
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ STUDENT_EMAIL + "=" + encodeURIComponent(email));

		return handleSendRegistrationKey();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function sendRegistrationKeys(courseID) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEYS
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleSendRegistrationKeys();
	}
}

// set the radio button with the given value as being checked
// do nothing if there are no radio buttons
// if the given value does not exist, all the radio buttons
// are reset to unchecked
function setCheckedValue(radioObj, newValue) {
	if (!radioObj)
		return;
	var radioLength = radioObj.length;
	if (radioLength == undefined) {
		radioObj.checked = (radioObj.value == newValue.toString());
		return;
	}
	for ( var i = 0; i < radioLength; i++) {
		radioObj[i].checked = false;
		if (radioObj[i].value == newValue.toString()) {
			radioObj[i].checked = true;
		}
	}
}

function setSelectedOption(s, v) {
	var sOptionsLength = s.options.length;
	for ( var i = 0; i < sOptionsLength; i++) {
		if (s.options[i].value == v) {
			s.options[i].selected = true;
			return;
		}
	}
}

function sortBase(x, y) {
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByAverage(a, b) {
	var x = a.average;
	var y = b.average;

	if (x == "N/A") {
		x = 1000;
	}

	if (y == "N/A") {
		y = 1000;
	}

	return sortBase(x, y);
}

function sortByCourseID(a, b) {
	var x = a.courseID.toLowerCase();
	var y = b.courseID.toLowerCase();

	return sortBase(x, y);
}

function sortByDiff(a, b) {
	var x = a.difference;
	var y = b.difference;

	if (x == "N/A") {
		x = 1000;
	}

	if (y == "N/A") {
		y = 1000;
	}

	return sortBase(x, y);
}

function sortByFromStudentName(a, b) {
	var x = a.fromStudentName;
	var y = b.fromStudentName;

	return sortBase(x, y);
}

function sortByID(a, b) {
	var x = a.ID.toLowerCase();
	var y = b.ID.toLowerCase();

	return sortBase(x, y);
}

function sortByName(a, b) {
	var x = a.name.toLowerCase();
	var y = b.name.toLowerCase();

	return sortBase(x, y);
}

function sortByGoogleID(a, b) {
	var x = a.googleID;
	var y = b.googleID;

	return sortBase(x, y);
}

function sortBySubmitted(a, b) {
	var x = a.submitted;
	var y = b.submitted;

	return sortBase(x, y);
}

function sortByTeamName(a, b) {
	var x = a.teamName;
	var y = b.teamName;

	return sortBase(x, y);
}

function sortByToStudentName(a, b) {
	var x = a.toStudentName;
	var y = b.toStudentName;

	return sortBase(x, y);
}



function toggleDeleteEvaluationConfirmation(courseID, name,isHome) {
	var s = confirm("Are you sure you want to delete the evaluation?");
	if (s == true) {
		doDeleteEvaluation(courseID, name,isHome);
	} else {
		clearStatusMessage();
	}
	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function toggleDeleteAllStudentsConfirmation(courseID) {
	var s = confirm("Are you sure you want to remove all students from this course?");
	if (s == true) {
		doDeleteAllStudents(courseID);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_COURSE_INFORMATION).scrollIntoView(true);
}

function toggleDeleteStudentConfirmation(courseID, studentEmail, studentName) {
	var s = confirm("Are you sure you want to remove " + studentName
			+ " from the course?");
	if (s == true) {
		doDeleteStudent(courseID, studentEmail);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_COURSE_INFORMATION).scrollIntoView(true);
}

function toggleEvaluationSummaryListViewByType(submissionList, summaryList,
		status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
	} else {
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
	}

	toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList,
			status, commentsEnabled)
}

function toggleInformStudentsOfEvaluationChanges(courseID, name) {
	var s = confirm("Do you want to send e-mails to the students to inform them of changes to the evaluation?");
	if (s == true) {
		doInformStudentsOfEvaluationChanges(courseID, name);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function togglePublishEvaluation(courseID, name, publish, reload, isHome) {
	if (publish) {
		var s = confirm("Are you sure you want to publish the evaluation?");
		if (s == true) {
			doPublishEvaluation(courseID, name, reload,isHome);
		} else {
			clearStatusMessage();
		}
	} else {
		var s = confirm("Are you sure you want to unpublish the evaluation?");
		if (s == true) {
			doUnpublishEvaluation(courseID, name, reload,isHome);
		} else {
			clearStatusMessage();
		}
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function toggleRemindStudents(courseID, evaluationName) {
	var s = confirm("Send e-mails to remind students who have not submitted their evaluations?");
	if (s == true) {
		doRemindStudents(courseID, evaluationName);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function toggleSendRegistrationKeysConfirmation(courseID) {
	var s = confirm("Are you sure you want to send registration keys to all the unregistered students for them to join your course?");
	if (s == true) {
		doSendRegistrationKeys(courseID);
		setStatusMessage("Emails have been sent to unregistered students.");
	} else {
		clearStatusMessage();
	}
}

function toggleSortStudentsByName(studentList, courseID) {
	printStudentList(studentList.sort(sortByName), courseID);
	studentSortStatus = studentSort.name;
	document.getElementById("button_sortstudentname").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortStudentsByStatus(studentList, courseID) {
	printStudentList(studentList.sort(sortByGoogleID), courseID);
	studentSortStatus = studentSort.status;
	document.getElementById("button_sortstudentstatus").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortStudentsByTeamName(studentList, courseID) {
	printStudentList(studentList.sort(sortByTeamName), courseID);
	studentSortStatus = studentSort.teamName;
	document.getElementById("button_sortstudentteam").setAttribute("class",
			"buttonSortAscending");
}

function toggleViewAddedStudents() {
	var currentClass = document.getElementById('button_viewaddedstudents')
			.getAttribute("class");

	if (currentClass == "plusButton") {
		document.getElementById("button_viewaddedstudents").setAttribute(
				"class", "minusButton");
		document.getElementById("rowAddedStudents").style.display = "";
	} else {
		document.getElementById("button_viewaddedstudents").setAttribute(
				"class", "plusButton");
		document.getElementById("rowAddedStudents").style.display = "none";
	}
}

function toggleViewEditedStudents() {
	var currentClass = document.getElementById('button_vieweditedstudents')
			.getAttribute("class");

	if (currentClass == "plusButton") {
		document.getElementById("button_vieweditedstudents").setAttribute(
				"class", "minusButton");
		document.getElementById("rowEditedStudents").style.display = "";
	} else {
		document.getElementById("button_vieweditedstudents").setAttribute(
				"class", "plusButton");
		document.getElementById("rowEditedStudents").style.display = "none";
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function unarchiveCourse(courseID) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_UNARCHIVECOURSE + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleUnarchiveCourse();
	}
}

window.onload = function() {
	displayHomeTab();
	initializetooltip();
}

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip;