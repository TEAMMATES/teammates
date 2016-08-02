$(document).ready(function() {
    readyInstructorStudentEditPage();
});

/*
 * Binds an event handler to the team field so that the change of the team name is identified
 * and the confirmation dialog is sent
 */
function readyInstructorStudentEditPage() {
    
    $('#button_submit').click(function(event) {
        if ($('#button_submit').attr('editStatus') === 'mustDeleteResponses') {
            return confirm('Editing these fields will result in some existing responses '
                           + 'from this student to be deleted. You may download the data before '
                           + 'you make the changes. Are you sure you want to continue?');
        }
        
        if ($('#newstudentemail').val() !== $('#studentemail').val() && $('#isAnyEmailSentForTheCourse').val()) {
            event.preventDefault();
            var $form = $(this).parents('form');
            var messageText = 'Do you want to resend past session links of this course to the new email '
                              + $('#newstudentemail').val() + '?';
            var okCallback = function() {
                $('#isSendEmail').val(true);
                $form.submit();
            };
            var cancelCallback = function() {
                $('#isSendEmail').val(false);
                $form.submit();
            };

            BootboxWrapper.showModalConfirmationWithCancel('Resend past links to the new email?', messageText,
                    okCallback, cancelCallback, null, 'Yes, save changes and resend links',
                    'No, just save the changes', 'Canel', StatusType.INFO);
        }
    });
    
    $('#teamname').change(function() {
        $('#button_submit').attr('editStatus', 'mustDeleteResponses');
    });
}
