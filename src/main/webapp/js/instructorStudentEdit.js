$(document).ready(function(){
    $("#button_submit").on('click', changeStudnetInformationConfirmation);
});

function changeStudnetInformationConfirmation() {
    return confirm('Please note that the students responses for the team will be deleted if the team information is changed. You can choose to download the data before you make the changes. Are you sure you want to change the student information?');
}