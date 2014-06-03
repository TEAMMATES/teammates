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
    $("#instructorTable"+number).find(":input").not(".immutable").prop("disabled", false);
    $("#instrEditLink"+number).hide();
    $("#btnSaveInstructor"+number).show();
}

function disableFormEditInstructor(number) {
    $("#instructorTable"+number).find(":input").not(".immutable").prop("disabled", true);
    $("#instrEditLink"+number).show();
    $("#btnSaveInstructor"+number).hide();
}
