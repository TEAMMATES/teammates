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
    $("#instructorTable" + number).find(":input").not(".immutable").prop("disabled", false);
    $("#instrEditLink" + number).hide();
    $("#accessControlEditDivForInstr" + number).show();
    $("#btnSaveInstructor" + number).show();
}

function disableFormEditInstructor(number) {
    $("#instructorTable" + number).find(":input").not(".immutable").prop("disabled", true);
    $("#instrEditLink" + number).show();
    $("#accessControlEditDivForInstr" + number).hide();
    $("#btnSaveInstructor" + number).hide();
}

function toggleSessionsControlTable(number) {
    $("#sessionsControlTableForInstr" + number).toggle();
}

function toggleTunePermissionsDiv(number) {
    $("#tunePermissionsDivForInstr" + number).toggle();
}

function showNewInstructorForm() {
    $("#panelAddInstructor").show();
    $("#btnShowNewInstructorForm").hide();
}
