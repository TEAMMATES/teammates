var INSTRUCTOR_STUDENT_EDIT_FORM = '#instructor-student-edit-form';

$(document).ready(function() {
    readyInstructorStudentEditPage();
});

/*
 * Binds an event handler to the team field so that the change of the team name is identified
 * and the confirmation dialog is sent
 */
function readyInstructorStudentEditPage() {
    
    $(INSTRUCTOR_STUDENT_EDIT_FORM).on('submit', function(event) {
        if ($(INSTRUCTOR_STUDENT_EDIT_FORM).attr('editStatus') === 'mustDeleteResponses') {
            event.preventDefault();

            var messageText = 'Editing these fields will result in some existing responses from this student '
                              + 'to be deleted. You may download the data before you make the changes. Are '
                              + 'you sure you want to continue?';
            var okCallback = function() {
                event.target.submit();
            };

            BootboxWrapper.showModalConfirmation('Confirm Deletion', messageText, okCallback, null,
                    BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.WARNING);
        }
    });
    
    $('#teamname').change(function() {
        $(INSTRUCTOR_STUDENT_EDIT_FORM).attr('editStatus', 'mustDeleteResponses');
    });
}
