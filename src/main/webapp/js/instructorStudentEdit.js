$(document).ready(function() {
    readyInstructorStudentEditPage();
});

/*
 * Binds an event handler to the team field so that the change of the team name is identified
 * and the confirmation dialog is sent
 */
function readyInstructorStudentEditPage() {
    
    $("#button_submit").click(function() {
        if ($('#button_submit').attr('editStatus') === 'mustDeleteResponses') {
            return confirm('Editing these fields will result in some existing responses ' + 
                    'from this student to be deleted. You may download the data before ' + 
                    'you make the changes. Are you sure you want to continue?');
        }
    })
    
    $('#teamname').change(function() {
        $('#button_submit').attr('editStatus', 'mustDeleteResponses');
    }); 
}