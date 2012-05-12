var COURSE_ID = "courseid";
var COURSE_NAME = "coursename";
var COURSE_NUMBEROFTEAMS = "coursenumberofteams";
var COURSE_TOTALSTUDENTS = "coursetotalstudents";
var COURSE_UNREGISTERED = "courseunregistered";
var COURSE_STATUS = "coursestatus";

var DIV_COURSE_MANAGEMENT = "coordinatorCourseManagement";
var DIV_COURSE_TABLE = "coordinatorCourseTable";

/**
 * Add Course Constants
 * 
 */


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

	//TODO: convert to normal string formatting using "+"
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

