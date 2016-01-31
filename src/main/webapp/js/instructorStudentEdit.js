$(document).ready(function(){
    $("#button_submit").on('click', changeStudnetInformationConfirmation);
});

function changeStudnetInformationConfirmation() {
    return confirm('Editing these fields will result in some existing responses from this student to be deleted. You may download the data before you make the changes. Are you sure you want to continue?');
}