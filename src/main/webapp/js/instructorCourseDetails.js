/**
 * Functions to trigger registration key sending to a specific student in the
 * course.
 * Currently no confirmation dialog is shown.
 * @param courseID
 * @param email
 */
function toggleSendRegistrationKey(courseID, email) {
	return confirm("Usually, there is no need to use this feature. " +
			"TEAMMATES sends invitation emails to students automatically at the " +
			"point the first evaluation of the course opens for submission. " +
			"Do you wish to send the invitation email to this student now?");
}

/**
 * Function to trigger registration key sending to every unregistered students
 * in the course.
 * @param courseID
 */
function toggleSendRegistrationKeysConfirmation(courseID) {
	return confirm("Are you sure you want to send invitation emails to all " +
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