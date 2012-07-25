/**
 * Functions to trigger registration key sending to a specific student in the
 * course.
 * Currently no confirmation dialog is shown.
 * @param courseID
 * @param email
 */
function toggleSendRegistrationKey(courseID, email) {
	return true;
}

/**
 * Function to trigger registration key sending to every unregistered students
 * in the course.
 * @param courseID
 */
function toggleSendRegistrationKeysConfirmation(courseID) {
	return confirm("Are you sure you want to send registration keys to all " +
			"the unregistered students in " + courseID + " for them to " +
			"join your course?");
}

/**
 * Function that shows confirmation dialog for removing a student from a course
 * @param studentName
 * @returns
 */
function toggleDeleteStudentConfirmation(studentName) {
	return confirm("Are you sure you want to remove " + studentName + " from " +
			"the course?");
}