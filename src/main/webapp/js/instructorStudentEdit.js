'use strict';

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

        var newStudentEmail = $('#newstudentemail').val();
        var isEmailFieldChanged = newStudentEmail !== $('#studentemail').val();
        var isOpenOrPublishedEmailSentInThisCourse = $('#openorpublishedemailsent').val();

        if ($(INSTRUCTOR_STUDENT_EDIT_FORM).attr('editStatus') === 'mustDeleteResponses') {
            event.preventDefault();

            var messageText = 'Editing these fields will result in some existing responses from this student '
                              + 'to be deleted. You may download the data before you make the changes. Are '
                              + 'you sure you want to continue?';
            var okCallback = function() {

                if (isEmailFieldChanged && isOpenOrPublishedEmailSentInThisCourse) {
                    sendEmailToNewEmailOption(event, newStudentEmail);
                } else {
                    event.target.submit();
                }
            };

            BootboxWrapper.showModalConfirmation('Confirm Deletion', messageText, okCallback, null,
                    BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.WARNING);
        } else if (isEmailFieldChanged && isOpenOrPublishedEmailSentInThisCourse) {
            sendEmailToNewEmailOption(event, newStudentEmail);
        }
    });

    function sendEmailToNewEmailOption(event, newStudentEmail) {
        event.preventDefault();
        var messageText = 'Do you want to resend past session links of this course to the new email '
                + newStudentEmail + '?';
        var yesCallback = function() {
            $('#isSendEmail').val(true);
            event.target.submit();
        };
        var noCallback = function() {
            $('#isSendEmail').val(false);
            event.target.submit();
        };
        BootboxWrapper.showModalConfirmationWithCancel('Resend past links to the new email?', messageText,
                yesCallback, noCallback, null, 'Yes, save changes and resend links',
                'No, just save the changes', 'Cancel', StatusType.PRIMARY);
    }

    $('#teamname').change(function() {
        $(INSTRUCTOR_STUDENT_EDIT_FORM).attr('editStatus', 'mustDeleteResponses');
    });
}
