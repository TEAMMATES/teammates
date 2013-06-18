
/**
 * Pre-processes and validates the user's input.
 * @returns {Boolean} True if it is OK to proceed with the form submission.
 */
function verifyCourseData() {
	var courseID = $("#"+COURSE_ID).val();
	var courseName = $("#"+COURSE_NAME).val();
	var instructorList = $("#" + COURSE_INSTRUCTOR_LIST).val();
	
	var allErrorMessage = "";
	
	allErrorMessage += checkAddCourseParam(courseID, courseName, instructorList);
	if(allErrorMessage.length>0){
		setStatusMessage(allErrorMessage,true);
		return false;
	}
	
	var proceedWithoutUser = true;
	if (!doesInstructorListIncludesLoggedInUser($("#" + COURSE_INSTRUCTOR_ID).val(), instructorList)){
		proceedWithoutUser = 	confirm(MESSAGE_INSTRUCTOR_NOT_WHTHIN_INSTRUCTOR_LIST);
		if (!proceedWithoutUser) return false;
	}
	
	return true;
	
}


/**
 * Checks if current logged in person appears in the instructor list.
 * @returns {Boolean}
 */
function doesInstructorListIncludesLoggedInUser(instructorId, instructorList){
	var entries = instructorList.split("\n");
	for (var i = 0; i < entries.length; i++){
		fields = entries[i].trim().split("|");
		if(fields[0].trim() === instructorId.trim()){
			return true;
		} 
	}
	return false;
}


/**
 * Checks the validity of the three parameters: course ID, course name, instructor list.
 * @returns {String} A message describing reasons why the input is invalid.
 * Returns an empty string if all three inputs are valid.
 */
function checkAddCourseParam(courseId, courseName, instructorList) {
	var errorMessages ="";
	errorMessages += getCourseIdInvalidityInfo(courseId);
	errorMessages += getCourseNameInvalidityInfo(courseName);
	errorMessages += getInstructorListInvalidityInfo(instructorList);
	return errorMessages;	
}


function getCourseIdInvalidityInfo(courseId){
	
	var invalidityInfo = "";
	
	courseId = courseId.trim();
	
	if (courseId == "") {
		return  DISPLAY_COURSE_COURSE_ID_EMPTY + "<br>";
	}

	// long courseId
	if(courseId.length > COURSE_ID_MAX_LENGTH) {
		invalidityInfo +=  DISPLAY_COURSE_LONG_ID + "<br>";
	}
	
	// invalid courseId
	if (!isCourseIDValidChars(courseId)) {
		invalidityInfo +=  DISPLAY_COURSE_INVALID_ID + "<br>";
	}
	
	return invalidityInfo;
}


function getCourseNameInvalidityInfo(courseName){
	
	courseName = courseName.trim();
	
	if (courseName == "") {
		return  DISPLAY_COURSE_COURSE_NAME_EMPTY + "<br>";
	}

	if(courseName.length > COURSE_NAME_MAX_LENGTH) {
		return  DISPLAY_COURSE_LONG_NAME + "<br>";
	}
	
	return "";
}


function getInstructorListInvalidityInfo(instructorList){
	
	invalidityInfo = "";
	
	instructorList = instructorList.trim();
	
	if (instructorList.trim() == ""){
		return  DISPLAY_COURSE_INSTRUCTOR_LIST_EMPTY + "<br>";
	}
	
	var entries = instructorList.split("\n");
	for ( var x = 0; x < entries.length ; x++) {
			 invalidityInfo += getInstructorLineInvalidityInfo(entries[x]);
	}
	
	return invalidityInfo;
}


function getInstructorLineInvalidityInfo(instructorLine) {
	
	invalidityInfo = "";
	instructorLine = instructorLine.trim();
	
	if (instructorLine == "") {
		return "";  // ignore empty lines
	}
	
	// Separate the fields
	fields = instructorLine.trim().split("|");
	var fieldsLength = fields.length;
	
	// Make sure that all fields are present and valid
	if (fieldsLength<3) {
		invalidityInfo = DISPLAY_INPUT_FIELDS_MISSING;
	} else if(fieldsLength>3){
		invalidityInfo = DISPLAY_INPUT_FIELDS_EXTRA;
	} else if (!isValidGoogleId(sanitizeGoogleId(fields[0].trim()))) {
		invalidityInfo = DISPLAY_GOOGLEID_INVALID;
	} else if (!isNameValid(fields[1].trim())) {
		invalidityInfo = DISPLAY_NAME_INVALID;
	} else if (!isEmailValid(fields[2].trim())) {
		invalidityInfo = DISPLAY_EMAIL_INVALID;
	} else {
		return ""; //valid input
	}
	
	return invalidityInfo + " Incorrect line : "+ instructorLine + "<br>";
}


/**
 * Checks if the parameter consists of alpha-numerics and characters {@code -._$} only.
 * @returns {Boolean} true if the courseId does not contain any unacceptable characters.
 */
function isCourseIDValidChars(courseId) {
	return courseId.match(/^[a-zA-Z_$0-9.-]+$/) != null;
}


