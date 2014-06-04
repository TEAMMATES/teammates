function enableEditInstructor(instructorNum, totalInstructors) {
    for (var i=1; i<=totalInstructors; i++) {
        if (i == instructorNum) {
            enableFormEditInstructor(i);
        } else {
            disableFormEditInstructor(i);
        }
    }
}

function enableFormEditInstructor(number) {
    $("#instrTable" + number).find(":input").not(".immutable").prop("disabled", false);
    $("#instrEditLink" + number).hide();
    $("#accessControlEditDivForInstr" + number).show();
    $("#btnToSaveInstructor" + number).show();
}

function disableFormEditInstructor(number) {
    $("#instrTable" + number).find(":input").not(".immutable").prop("disabled", true);
    $("#instrEditLink" + number).show();
    $("#accessControlEditDivForInstr" + number).hide();
    $("#btnToSaveInstructor" + number).hide();
}

function toggleSessionsControlTable(number) {
    $("#sessionsControlTableForInstructor" + number).toggle();
}

function toggleTunePermissionsDiv(number) {
    $("#tunePermissionsDivForInstructor" + number).toggle();
}

function showNewInstructorForm() {
    $("#panelToAddInstructor").show();
    $("#btnToShowNewInstructorForm").hide();
}

$(function() { 
    $("[data-toggle='tooltip']").tooltip({html: true}); 
});
