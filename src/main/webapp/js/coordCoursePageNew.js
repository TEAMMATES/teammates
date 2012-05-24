var COURSE_ID = "courseid";
var COURSE_NAME = "coursename";

//Add course status codes
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

//server responses
var COURSE_RESPONSE_EXISTS = "course exists";
var COURSE_RESPONSE_ADDED = "course added";
var COURSE_RESPONSE_DELETED = "course deleted";

//status messages
var DISPLAY_COURSE_ADDED = "The course has been added. Click the 'Enroll' link in the table below to add students to the course.";
var DISPLAY_COURSE_EXISTS = "The course already exists.";
var DISPLAY_COURSE_EMPTY = "Course ID and Course Name are compulsory fields.";
var DISPLAY_COURSE_LONG_ID = "Course ID should not exceed " + COURSE_ID_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_LONG_NAME = "Course name should not exceed " + COURSE_NAME_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";
var DISPLAY_COURSE_INVALID_NAME = "Course name is invalid.";

//------------------------------Add Course Validation-----------------------------
function prepareAddCourseParams(courseID, courseName) {
	courseID = trim(courseID);
	courseName = trim(courseName);
}

function doAddCourse() {
	var courseID = $("#"+COURSE_ID).val();
	var courseName = $("#"+COURSE_NAME).val();
	prepareAddCourseParams(courseID, courseName);

	// client-side validation
	var statusCode = checkAddCourseParam(courseID, courseName);

	if(statusCode != COURSE_STATUS_VALID_INPUT) {
		setStatusMessage(courseStatusToMessage(statusCode),true);
		return false;
	}
	// When valid, the message will be displayed by the server
	return true;
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
	if(courseID.length > COURSE_ID_MAX_LENGTH) {
		return COURSE_STATUS_LONG_ID;
	}

	// long courseName
	if(courseName.length > COURSE_NAME_MAX_LENGTH) {
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

function toggleSortCoursesByID(divElement,colIdx) {
	sortTable(divElement,colIdx);
	courseSortStatus = courseSort.ID;
	$(".buttonSortAscending").attr("class","buttonSortNone");
	$("#button_sortcourseid").attr("class","buttonSortAscending");
}

function toggleSortCoursesByName(divElement,colIdx) {
	sortTable(divElement,colIdx);
	courseSortStatus = courseSort.name;
	$(".buttonSortAscending").attr("class","buttonSortNone");
	$("#button_sortcoursename").attr("class","buttonSortAscending");
}

/**
 * Coordinator Delete Course
 */
function toggleDeleteCourseConfirmation(courseID) {
	return confirm("Are you sure you want to delete the course, \"" + courseID + "\"? This operation will delete all evaluations and students in this course.");
}