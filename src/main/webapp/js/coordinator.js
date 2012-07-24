/* 
 * This Javascript file is included in all coordinator pages. Functions here 
 * should be common to some/all coordinator pages.
 */



//Initial load-up
//-----------------------------------------------------------------------------
var cal = new CalendarPopup();

window.onload = function() {
	initializetooltip();
	if(typeof doPageSpecificOnload !== 'undefined'){
		doPageSpecificOnload();
	};
};

//DynamicDrive JS mouse-hover
document.onmousemove = positiontip;

//-----------------------------------------------------------------------------


/**
 * Function that shows confirmation dialog for deleting a course
 * @param courseID
 * @returns
 */
function toggleDeleteCourseConfirmation(courseID) {
	return confirm("Are you sure you want to delete the course: " + courseID + "?" +
			"This operation will delete all students and evaluations in this course.");
}


/**
 * Pops up confirmation dialog whether to delete specified evaluation
 * @param courseID
 * @param name
 * @returns
 */
function toggleDeleteEvaluationConfirmation(courseID, name) {
	return confirm("Are you sure you want to delete the evaluation " + name + " in " + courseID + "?");
}

/**
 * Pops up confirmation dialog whether to publish the specified
 * evaluation
 * @param name 
 */
function togglePublishEvaluation(name) {
	return confirm("Are you sure you want to publish the evaluation " + name + "?");
}

/**
 * Pops up confirmation dialog whether to unpublish the specified
 * evaluation
 * @param name 
 */
function toggleUnpublishEvaluation(name){
	return confirm("Are you sure you want to unpublish the evaluation " + name + "?");
}

/**
 * Pops up confirmation dialog whether to remind students to fill in a specified
 * evaluation.
 * @param courseID
 * @param evaluationName
 */
function toggleRemindStudents(evaluationName) {
	return confirm("Send e-mails to remind students who have not submitted their evaluations for " + evaluationName + "?");
}


/**
 * Checks whether an e-mail is valid.
 * Used in coordCourseEnroll page (through coordCourseEnroll.js)
 * @param email
 * @returns {Boolean}
 */
function isStudentEmailValid(email) {
	return email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i)!=null;
}

/**
 * Checks whether a student's name is valid
 * Used in coordCourseEnroll page (through coordCourseEnroll.js)
 * @param name
 * @returns {Boolean}
 */
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

/**
 * Checks whether a team's name is valid
 * Used in coordCourseEnroll page (through coordCourseEnroll.js)
 * @param teamName
 * @returns {Boolean}
 */
function isStudentTeamNameValid(teamName) {
	return teamName.length<=24;
}

/**
 * To check whether a student's name and team name are valid
 * @param editName
 * @param editTeamName
 * @returns {Boolean}
 */
function isStudentInputValid(editName, editTeamName, editEmail) {
	if (editName == "" || editTeamName == "" || editEmail == "") {
		setStatusMessage(DISPLAY_FIELDS_EMPTY,true);
		return false;
	} else if (!isStudentNameValid(editName)) {
		setStatusMessage(DISPLAY_STUDENT_NAME_INVALID,true);
		return false;
	} else if (!isStudentTeamNameValid(editTeamName)) {
		setStatusMessage(DISPLAY_STUDENT_TEAMNAME_INVALID,true);
		return false;
	} else if (!isStudentEmailValid(editEmail)){
		setStatusMessage(DISPLAY_STUDENT_EMAIL_INVALID,true);
		return false;
	}
	return true;
}
