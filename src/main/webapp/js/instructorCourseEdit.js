/**
 * This function will be called when the instructorCourseEdit page is loaded.
 */
function readyCourseEditPage() {
	$("#formAddInstructor").hide();
}

/**
 * Enable the user to edit one instructor and disable editting for other instructors
 * @param instructorNum
 * @param totalInstructors
 */
function enableEditInstructor(instructorNum, totalInstructors) {

	for (var i=1; i<=totalInstructors; i++) {
		if (i == instructorNum) {
			enableFormEditInstructor(i);
		} else {
			disableFormEditInstructor(i);
		}
	}
}

/**
 * Enable formEditInstructor's input fields, display the "Save changes" button,
 * and disable the edit link.
 * @param number
 */
function enableFormEditInstructor(number) {
	$("#instructorTable"+number).find(":input").not(".immutable").prop("disabled", false);
	$("#instrEditLink"+number).hide();
	$("#btnSaveInstructor"+number).show();
}

/**
 * Disable formEditInstructor's input fields, hide the "Save changes" button,
 * and enable the edit link.
 * @param number
 */
function disableFormEditInstructor(number) {
	$("#instructorTable"+number).find(":input").not(".immutable").prop("disabled", true);
	$("#instrEditLink"+number).show();
	$("#btnSaveInstructor"+number).hide();
}

/**
 * Show the form for adding new instructor. Hide the btnShowNewInstructorForm.
 */
function showNewInstructorForm() {
	$("#formAddInstructor").show();
	$("#btnShowNewInstructorForm").hide();
	$('#frameBody').animate({scrollTop: $('#frameBody')[0].scrollHeight}, 1000);
}

/**
 * Function that shows confirmation dialog for deleting a instructor
 * @param courseID
 * @param instructorID
 * @returns
 */
function toggleDeleteInstructorConfirmation(courseID, instructorID, userID) {
	if (instructorID == userID) {
		return confirm("Are you sure you want to delete your instructor role from the course " + courseID + "? " +
		"You will not be able to access the course anymore.");
	} else {
		return confirm("Are you sure you want to delete the instructor " + instructorID + " from " + courseID + "? " +
			"He/she will not be able to access the course anymore.");
	}
}
