$(document).ready(function(){
    if($("#button_sortstudentsection").length){
        toggleSort($("#button_sortstudentsection"),1);
    } else {
        toggleSort($("#button_sortstudentteam"),1);
    }
});

/**
 * Functions to trigger registration key sending to a specific student in the
 * course.
 * Currently no confirmation dialog is shown.
 * @param courseID
 * @param email
 */
function toggleSendRegistrationKey(courseID, email) {
    return confirm("Usually, there is need to use this feature because TEAMMATES " +
            "sends an automatic invite to students at the opening time of each" +
            " session. Send a join request anyway?");
}

/**
 * Function to trigger registration key sending to every unregistered students
 * in the course.
 * @param courseID
 */
function toggleSendRegistrationKeysConfirmation(courseID) {
    return confirm("Usually, there is need to use this feature because TEAMMATES" +
            " sends an automatic invite to students at the opening time of" +
            " each session. Send a join request to all yet-to-join students in " +
            courseID + " anyway?");
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