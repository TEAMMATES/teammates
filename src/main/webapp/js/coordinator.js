// AJAX
var xmlhttp = new getXMLObject();

// DATE OBJECT
var cal = new CalendarPopup();

/*-----------------------------------------------------------CONSTANTS-------------------------------------------------------*/

// DISPLAY
var DISPLAY_COURSE_ARCHIVED = "The course has been archived.";
var DISPLAY_COURSE_DELETED = "The course has been deleted."
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
var DISPLAY_STUDENT_EDITEDEXCEPTTEAM = "The student's details have been edited, except for his team<br /> as there is an ongoing evaluation."
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
}
var courseSortStatus = courseSort.ID;

var evaluationSort = {
	courseID : 0,
	name : 1
}
var evaluationSortStatus = evaluationSort.courseID;

var studentSort = {
	name : 0,
	teamName : 1,
	status : 2
}
var studentSortStatus = studentSort.name;

var courseViewArchived = {
	show : 0,
	hide : 1
}
var courseViewArchivedStatus = courseViewArchived.hide;

var evaluationResultsView = {
	reviewee : 0,
	reviewer : 1
}
var evaluationResultsViewStatus = evaluationResultsView.reviewee;

var evaluationResultsSummaryListSort = {
	teamName : 0,
	name : 1,
	average : 2,
	submitted : 3,
	diff : 4
}
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
var CONNECTION_OK = 200

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
var COURSE_STATUS_EMPTY = 3
var COURSE_STATUS_LONG_ID = 4;
var COURSE_STATUS_LONG_NAME = 5;
var COURSE_STATUS_INVALID_ID = 6;
var COURSE_STATUS_INVALID_NAME = 7;

var COURSE_STATUS_DELETED = 8;

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
		"<form method='post' action='' name='form_addcourse'>														\
			<table class='addform round'>																			\
				<tr>																								\
					<td><b>Course ID:</b></td>																		\
				</tr>																								\
				<tr>																								\
					<td><input class='addinput' type='text' name='" + COURSE_ID + "' id='" + COURSE_ID + "'			\
					onmouseover='ddrivetip('Enter the identifier of the course, e.g.CS3215Sem1.')'					\
					onmouseout='hideddrivetip()' maxlength=" + COURSEID_INPUT_FIELD_MAX_LENGTH + " tabindex=1 />	\
					</td>																							\
				</tr>																								\
				<tr>																								\
					<td><b>Course Name:</b></td>																	\
				</tr>																								\
				<tr>																								\
					<td><input class='addinput' type='text' name='" + COURSE_NAME + "' id='" + COURSE_NAME + "'		\
					onmouseover='ddrivetip('Enter the name of the course, e.g. Software Engineering.')'				\
					onmouseout='hideddrivetip()' maxlength=" + COURSENAME_INPUT_FIELD_MAX_LENGTH + " tabindex=2 />	\
					</td>																							\
				</tr>																								\
				<tr>																								\
					<td><input id='btnAddCourse' type='button' class='button' 										\
					onclick='doAddCourse(this.form." + COURSE_ID + ".value, this.form." + COURSE_NAME + ".value);'	\
					value='Add Course' tabindex='3' />																\
					</td>																							\
				</tr>																								\
			</table>																								\
		</form>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
}

function doAddCourse(courseID, courseName) {
	setStatusMessageToLoading();
	
	preapareAddCourseParams(courseID, courseName);
	
	//client-side validation
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

function preapareAddCourseParams(courseID, courseName) {
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
		"<br /><br />																																\
			<table id='dataform'>																													\
				<tr>																																\
					<th><input class='buttonSortNone' type='button' id='button_sortcourseid'>COURSE ID</input></th>									\
					<th><input class='buttonSortNone' type='button' id='button_sortcoursename'>COURSE NAME</input></th>								\
					<th class='centeralign'>TEAMS</th>																								\
					<th class='centeralign'>TOTAL STUDENTS</th>																						\
					<th class='centeralign'>TOTAL UNREGISTERED</th>																					\
					<th class='centeralign'>ACTION(S)</th>																							\
				</tr>";

	//empty table
	if (courseListLength == 0) {
		setStatusMessage(COORDINATOR_MESSAGE_NO_COURSE);
		output = output + "<tr><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
	}

	// Need counter to take note of archived courses
	var counter = 0;

	for (loop = 0; loop < courseListLength; loop++) {
		if (courseList[loop].status == "false" || courseViewArchivedStatus == courseViewArchived.show) {
			// common view:
			output = output + 
			"<tr>																																		\
				<td id='courseID" + counter + "'>" + courseList[loop].ID + "</td>																		\
				<td id='courseName" + counter + "'>" + encodeChar(courseList[loop].name) + "</td>														\
				<td class='t_course_teams centeralign'>" + courseList[loop].numberOfTeams + "</td>														\
				<td class='centeralign'>" + courseList[loop].totalStudents + "</td>																		\
				<td class='centeralign'>" + courseList[loop].unregistered + "</td>																		\
				<td class='centeralign'>																												\
						<a class='t_course_enrol' href=\"javascript:displayEnrollmentPage('" + courseList[loop].ID + "');hideddrivetip();\"				\
							onmouseover=\"ddrivetip('" + HOVER_MESSAGE_ENROL + "')\"																	\
							onmouseout=\"hideddrivetip()\">Enrol</a>																					\
						<a class='t_course_view' href=\"javascript:displayCourseInformation('" + courseList[loop].ID + "');hideddrivetip();\"			\
							onmouseover=\"ddrivetip('" + HOVER_MESSAGE_VIEW_COURSE + "')\"																\
							onmouseout=\"hideddrivetip()\">View</a>																						\
						<a class='t_course_delete' href=\"javascript:toggleDeleteCourseConfirmation('" + courseList[loop].ID + "');hideddrivetip();\"	\
							onmouseover=\"ddrivetip('" + HOVER_MESSAGE_DELETE_COURSE + "')\"															\
							onmouseout=\"hideddrivetip()\">Delete</a>																					\
				</td>																																	\
			</tr>";

			counter++;
		}
	}

	output = output + "</table><br /><br />";

	if (counter == 0) {
		output = output + "No records found.<br /><br /><br /><br />";
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
function toggleDeleteCourseConfirmation(courseID) {
	var s = confirm("Are you sure you want to delete the course, \"" + courseID + "\"? This operation will delete all evaluations and students in this course.");

	if (s == true)
		doDeleteCourse(courseID);
	else
		clearStatusMessage();

	scrollToTop(DIV_COURSE_MANAGEMENT);
}

function doDeleteCourse(courseID) {
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

function compileSubmissionsIntoSummaryList(submissionList) {
	var summaryList = new Array();

	var exists = false;

	var toStudent;
	var toStudentName;
	var toStudentComments;
	var totalPoints;
	var totalPointGivers;
	var claimedPoints;
	var teamName;
	var average;
	var difference;
	var submitted;
	var pointsBumpRatio;

	var count = 0;
	var submissionListLength = submissionList.length;

	for ( var loop = 0; loop < submissionListLength; loop++) {
		logSubmission(submissionList[loop]);
	}

	for (loop = 0; loop < submissionListLength; loop++) {
		exists = false;
		submitted = false;

		var summaryListLength = summaryList.length;
		for (x = 0; x < summaryListLength; x++) {

			if (summaryList[x].toStudent == submissionList[loop].toStudent) {
				exists = true;
			}
		}

		if (exists == false) {
			toStudent = submissionList[loop].toStudent;
			toStudentName = submissionList[loop].toStudentName;
			toStudentComments = submissionList[loop].toStudentComments;
			teamName = submissionList[loop].teamName;
			totalPoints = 0;
			totalPointGivers = 0;

			for (y = loop; y < submissionListLength; y++) {
				if (submissionList[y].toStudent == toStudent) {
					if (submissionList[y].fromStudent == toStudent) {
						if (submissionList[y].points == -999) {
							claimedPoints = NA;
						} else if (submissionList[y].points == -101) {
							claimedPoints = NOTSURE;
						}

						else {

							claimedPoints = Math.round(submissionList[y].points
									* submissionList[y].pointsBumpRatio);

						}

						if (submissionList[y].points != -999) {
							submitted = true;
						}
					}

					else {
						if (submissionList[y].points != -999
								&& submissionList[y].points != -101) {
							totalPoints += Math.round(submissionList[y].points
									* submissionList[y].pointsBumpRatio);
							totalPointGivers++;
						}
					}
				}
			}
			if (totalPointGivers != 0) {
				average = Math.round(totalPoints / totalPointGivers);
			}

			else {
				average = NA;
			}

			difference = Math.round(average - claimedPoints);
			if (isNaN(difference)) {
				difference = NA;
			}

			summaryList[count++] = {
				toStudent : toStudent,
				toStudentName : toStudentName,
				teamName : teamName,
				average : average,
				difference : difference,
				toStudentComments : toStudentComments,
				submitted : submitted,
				claimedPoints : claimedPoints
			};
			console.log("******" + toStudent + "|" + toStudentName + "|"
					+ teamName + "|" + average + "|" + difference + "|" + "|"
					+ submitted + "|" + claimedPoints);

		}
	}

	// Find normalizing points bump ratio for averages
	var teamsNormalized = new Array();
	count = 0;
	logSummaryList(summaryList);
	var summaryListLength = summaryList.length;
	for (loop = 0; loop < summaryListLength; loop++) {
		teamName = summaryList[loop].teamName;
		// Reset variables
		exists = false;
		totalPoints = 0;
		totalGivers = 0;
		pointsBumpRatio = 0;

		// Check if the team is added
		var teamsNormalizedLength = teamsNormalized.length;
		for (y = 0; y < teamsNormalizedLength; y++) {
			if (summaryList[loop].teamName == teamsNormalized[y].teamName) {
				exists = true;
				break;
			}
		}

		if (exists == false) {
			// Tabulate the perceived scores
			for (y = loop; y < summaryListLength; y++) {
				console.log(summaryList[y].teamName + "[0]"
						+ summaryList[y].average);
				if (summaryList[y].teamName == summaryList[loop].teamName
						&& summaryList[y].average != NA) {
					console.log(summaryList[y].teamName + "[1]"
							+ summaryList[y].average);

					totalPoints += summaryList[y].average;
					totalGivers += 1;
				}
			}
			console.log("totalgiver: " + totalGivers + " | totalPoints:"
					+ totalPoints);

			if (totalGivers != 0) {

				pointsBumpRatio = totalGivers * 100 / totalPoints;

				// Store the bump ratio
				teamsNormalized[count++] = {
					pointsBumpRatio : pointsBumpRatio,
					teamName : teamName
				};
				console.log("teamNormalized:" + pointsBumpRatio + "|"
						+ teamName);
			}

		}
	}

	var teamsNormalizedLength = teamsNormalized.length;
	// Do the normalization
	for (loop = 0; loop < teamsNormalizedLength; loop++) // number of teams
	{
		for (y = 0; y < summaryListLength; y++) // number of members
		{
			if (summaryList[y].teamName == teamsNormalized[loop].teamName
					&& summaryList[y].average != NA) {
				summaryList[y].average = Math.round(summaryList[y].average
						* teamsNormalized[loop].pointsBumpRatio);

				summaryList[y].difference = Math.round(summaryList[y].average
						- summaryList[y].claimedPoints);

				if (isNaN(summaryList[y].difference)) {
					summaryList[y].difference = NA;
				}
			}
		}
		logSummaryList(summaryList);

		console.log("team normalized: " + loop + teamsNormalized[loop].teamName
				+ "|" + teamsNormalized[loop].pointsBumpRatio);
	}

	return summaryList;
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

	// xl: new added
	var published = evaluationList[loop].published;

	clearDisplay();

	printEvaluationHeaderForm(courseID, name, start, deadline, status,
			activated, published);
	evaluationResultsViewStatus = evaluationResultsView.reviewer;

	doGetSubmissionResultsList(courseID, name, status, commentsEnabled);

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEvaluationsTab() {
	clearDisplay();
	printEvaluationAddForm();
	doGetCourseIDList();
	doGetEvaluationList();
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



function doDeleteEvaluation(courseID, name) {
	setStatusMessage(DISPLAY_LOADING);

	var results = deleteEvaluation(courseID, name);

	if (results == 0) {
		doGetEvaluationList();
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
		submissionList = getSubmissionList(submissionList[0].courseID,
				submissionList[0].evaluationName);

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
		populateCourseIDOptions(results);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}



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

function doGetSubmissionResultsList(courseID, evaluationName, status,
		commentsEnabled) {
	setStatusMessage(DISPLAY_LOADING);

	var results = getSubmissionList(courseID, evaluationName);

	clearStatusMessage();

	if (results != 1) {
		var compiledResults = compileSubmissionsIntoSummaryList(results);

		toggleSortEvaluationSummaryListByTeamName(results, compiledResults,
				status, commentsEnabled);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

/*
 * Returns
 * 
 * submissionList: successful 1: server error
 * 
 */
function getSubmissionList(courseID, evaluationName) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSUBMISSIONLIST
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(evaluationName));

		return handleGetSubmissionList();
	}
}

/*
 * Returns
 * 
 * submissionList: successful 1: server error
 * 
 */
function handleGetSubmissionList() {
	if (xmlhttp.status == 200) {
		var submissions = xmlhttp.responseXML
				.getElementsByTagName("submissions")[0];
		var submissionList = new Array();
		var submission;

		var fromStudentName;
		var toStudentName;
		var fromStudent;
		var toStudent;
		var fromStudentComments;
		var toStudentComments;
		var courseID;
		var evaluationName;
		var teamName;
		var points;
		var pointsBumpRatio;
		var justification;
		var commentsToStudent;

		if (submissions != null) {
			var submissionsChildNodesLength = submissions.childNodes.length;
			for (loop = 0; loop < submissionsChildNodesLength; loop++) {
				submission = submissions.childNodes[loop];
				courseID = submission.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				evaluationName = submission
						.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
				teamName = submission.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;

				fromStudentName = submission
						.getElementsByTagName(STUDENT_FROMSTUDENTNAME)[0].firstChild.nodeValue;
				fromStudent = submission
						.getElementsByTagName(STUDENT_FROMSTUDENT)[0].firstChild.nodeValue;
				fromStudentComments = submission
						.getElementsByTagName(STUDENT_FROMSTUDENTCOMMENTS)[0].firstChild.nodeValue;

				toStudentName = submission
						.getElementsByTagName(STUDENT_TOSTUDENTNAME)[0].firstChild.nodeValue;
				toStudent = submission.getElementsByTagName(STUDENT_TOSTUDENT)[0].firstChild.nodeValue;
				toStudentComments = submission
						.getElementsByTagName(STUDENT_TOSTUDENTCOMMENTS)[0].firstChild.nodeValue;

				points = parseInt(submission
						.getElementsByTagName(STUDENT_POINTS)[0].firstChild.nodeValue);
				pointsBumpRatio = parseFloat(submission
						.getElementsByTagName(STUDENT_POINTSBUMPRATIO)[0].firstChild.nodeValue);
				justification = submission
						.getElementsByTagName(STUDENT_JUSTIFICATION)[0].firstChild.nodeValue;
				commentsToStudent = submission
						.getElementsByTagName(STUDENT_COMMENTSTOSTUDENT)[0].firstChild.nodeValue;

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
		}

		return submissionList;
	}

	else {
		return 1;
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

function doPublishEvaluation(courseID, name, reload) {
	setStatusMessage(DISPLAY_LOADING);

	var results = publishEvaluation(courseID, name);

	clearStatusMessage();

	if (results != 1) {
		if (reload) {
			doGetEvaluationList();
		} else {
			document.getElementById('button_publish').value = "Unpublish";
			document.getElementById('button_publish').onclick = function() {
				togglePublishEvaluation(courseID, name, false, false);
			};
		}

		setStatusMessage(DISPLAY_EVALUATION_PUBLISHED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}

}

function doUnpublishEvaluation(courseID, name, reload) {
	setStatusMessage(DISPLAY_LOADING);

	var results = unpublishEvaluation(courseID, name);

	clearStatusMessage();

	if (results != 1) {
		if (reload) {
			doGetEvaluationList();
		} else {
			document.getElementById('button_publish').value = "Publish";
			document.getElementById('button_publish').onclick = function() {
				togglePublishEvaluation(courseID, name, true, false);
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
			xmlhttp.open("POST", "/teammates", false);
			xmlhttp.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_ENROLSTUDENTS
					+ "&" + STUDENT_INFORMATION + "="
					+ encodeURIComponent(input) + "&" + COURSE_ID + "="
					+ encodeURIComponent(courseID));

			return handleEnrolStudents();
		}

		else {
			return 3;
		}
	}
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
function handleEnrolStudents() {
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
		return 0;
	}

	else {
		return 1;
	}
}

function handleRemindStudents() {
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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
	if (xmlhttp.status == 200) {
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

function populateCourseIDOptions(courseList) {
	var option = document.createElement("OPTION");

	var courseListLength = courseList.length;
	for (x = 0; x < courseListLength; x++) {
		option = document.createElement("OPTION");
		option.text = courseList[x].ID;
		option.value = courseList[x].ID;
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



function toggleDeleteEvaluationConfirmation(courseID, name) {
	var s = confirm("Are you sure you want to delete the evaluation?");
	if (s == true) {
		doDeleteEvaluation(courseID, name);
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

function togglePublishEvaluation(courseID, name, publish, reload) {
	// var s = confirm("Are you sure you want to publish the evaluation?");
	// if (s == true) {
	// doPublishEvaluation(courseID, name);
	// } else {
	// clearStatusMessage();
	// }

	if (publish) {
		var s = confirm("Are you sure you want to publish the evaluation?");
		if (s == true) {
			doPublishEvaluation(courseID, name, reload);
		} else {
			clearStatusMessage();
		}
	} else {
		var s = confirm("Are you sure you want to unpublish the evaluation?");
		if (s == true) {
			doUnpublishEvaluation(courseID, name, reload);
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



function toggleSortEvaluationSummaryListByAverage(submissionList, summaryList,
		status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByAverage),
			status, commentsEnabled, REVIEWEE);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.average;
	document.getElementById("button_sortaverage").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortEvaluationSummaryListByDiff(submissionList, summaryList,
		status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByDiff),
			status, commentsEnabled, REVIEWEE);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.diff;
	document.getElementById("button_sortdiff").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortEvaluationSummaryListByFromStudentName(submissionList,
		summaryList, status, commentsEnabled) {
	var type;
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;
	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList
			.sort(sortByFromStudentName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name;
	document.getElementById("button_sortname").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortEvaluationSummaryListBySubmitted(submissionList,
		summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList
			.sort(sortBySubmitted), status, commentsEnabled, REVIEWER);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.submitted;
	document.getElementById("button_sortsubmitted").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList,
		status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;

	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList,
			summaryList.sort(sortByTeamName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.teamName;
	document.getElementById("button_sortteamname").setAttribute("class",
			"buttonSortAscending");
}

function toggleSortEvaluationSummaryListByToStudentName(submissionList,
		summaryList, status, commentsEnabled) {
	var type;
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;
	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList
			.sort(sortByToStudentName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name;
	document.getElementById("button_sortname").setAttribute("class",
			"buttonSortAscending");
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
	displayCoursesTab();
	initializetooltip();
}

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip;