$(document).ready(function() {
    readyStudentEditPage();
});

function readyStudentEditPage() {
    
    $("#button_submit").click(function() {
        if ($('#button_submit').attr('editStatus') === 'mustDeleteResponses') {
            return confirm('Editing these fields will result in some existing responses from this student to be deleted. You may download the data before you make the changes. Are you sure you want to continue?');
        }
    })
    
    $('#teamname').change(function() {
        $('#button_submit').attr('editStatus', 'mustDeleteResponses');
    }); 
}