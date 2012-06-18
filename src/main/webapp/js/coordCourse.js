var OPERATION_COORDINATOR_SENDREGISTRATIONKEY = "coordinator_sendregistrationkey";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEYS = "coordinator_sendregistrationkeys";

//Add course status codes
var COURSE_STATUS_SERVERERROR = -1;
var COURSE_STATUS_VALID_INPUT = 0;
var COURSE_STATUS_EMPTY = 3;
var COURSE_STATUS_LONG_ID = 4;
var COURSE_STATUS_LONG_NAME = 5;
var COURSE_STATUS_INVALID_ID = 6;

//------------------------------Add Course Validation-----------------------------
/**
 * Do pre-processing on the user's input, and then return the validation result.
 * @returns {Boolean}
 */
function verifyAddCourse() {
	var courseID = $("#"+COURSE_ID).val();
	var courseName = $("#"+COURSE_NAME).val();
	courseID = courseID.trim();
	courseName = courseName.trim();

	// client-side validation
	var statusCode = checkAddCourseParam(courseID, courseName);

	if(statusCode != COURSE_STATUS_VALID_INPUT) {
		setStatusMessage(courseStatusToMessage(statusCode),true);
		return false;
	}
	// When valid, the message will be displayed by the server
	return true;
}

/**
 * Converts error codes into displayable message
 * @param statusCode
 * @returns
 */
function courseStatusToMessage(statusCode) {
	switch (statusCode) {
	case COURSE_STATUS_EMPTY:
		return DISPLAY_COURSE_EMPTY;
	case COURSE_STATUS_LONG_ID:
		return DISPLAY_COURSE_LONG_ID;
	case COURSE_STATUS_LONG_NAME:
		return DISPLAY_COURSE_LONG_NAME;
	case COURSE_STATUS_INVALID_ID:
		return DISPLAY_COURSE_INVALID_ID;
	default:
		return "";
	}
}

/**
 * Function to check the user's input in addCourse page
 * @param courseID
 * @param courseName
 * @returns {Number}
 */
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

/**
 * Functions to trigger registration key sending to a specific student in the
 * course.
 * Currently no confirmation dialog is shown.
 * @param courseID
 * @param email
 * @param name
 */
function toggleSendRegistrationKey(courseID, email, name) {
	scrollToTop();
	setStatusMessage(DISPLAY_LOADING);
	sendRegistrationKey(courseID, email);
}

/**
 * Functions that actually sends the registration key request to server
 * using AJAX (asynchronous)
 * @param courseID
 * @param email
 * @param name
 */
function sendRegistrationKey(courseID, email, name) {
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function(){
			if (xmlhttp.readyState==4) {
				if(xmlhttp.status==200){
					setStatusMessage(DISPLAY_REMINDER_SENT+name);
				} else {
					alert(DISPLAY_SERVERERROR);
				}
			}
		};
		xmlhttp.open("POST", "/teammates", true);
		xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEY
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ STUDENT_EMAIL + "=" + encodeURIComponent(email));
	} else {
		alert(DISPLAY_BROWSERERROR);
	}
}

/**
 * Function to trigger registration key sending to every unregistered students
 * in the course.
 * @param courseID
 */
function toggleSendRegistrationKeysConfirmation(courseID) {
	if(confirm("Are you sure you want to send registration keys to all the unregistered students for them to join your course?"))
		sendRegistrationKeys(courseID);
}

/**
 * Function that actually send the registration key request to the server
 * using AJAX (asynchronous)
 * @param courseID
 */
function sendRegistrationKeys(courseID) {
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function(){
			if (xmlhttp.readyState==4) {
				if(xmlhttp.status==200){
					setStatusMessage(DISPLAY_REMINDERS_SENT);
				} else {
					alert(DISPLAY_SERVERERROR);
				}
			}
		};
		xmlhttp.open("POST", "/teammates", true);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEYS
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	} else {
		alert(DISPLAY_BROWSERERROR);
	}
}

/**
 * Function that shows confirmation dialog for removing a student from a course
 * @param studentName
 * @returns
 */
function toggleDeleteStudentConfirmation(studentName) {
	return confirm("Are you sure you want to remove " + studentName + " from the course?");
}

/**
 * Function that shows confirmation dialog for deleting a course
 * @param courseID
 * @returns
 */
function toggleDeleteCourseConfirmation(courseID) {
	return confirm("Are you sure you want to delete the course: " + courseID + "?" +
			"This operation will delete all students and evaluations in this course.");
}