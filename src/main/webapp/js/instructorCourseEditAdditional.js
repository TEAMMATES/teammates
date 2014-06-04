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

function setDefaultAccessControlForInstructor(instrNum, instrRole) {
    if (instrRole === "Co-owner") {
        tickDefaultAccessControlForOwner(instrNum);
    } else if (instrRole === "Manager") {
        tickDefaultAccessControlForManager(instrNum);
    } else if (instrRole === "Observer") {
        tickDefaultAccessControlForObserver(instrNum);
    } else if (instrRole === "Tutor") {
        tickDefaultAccessControlForTutor(instrNum);
    } else if (instrRole === "Helper") {
        tickDefaultAccessControlForHelper(instrNum);
    } else {
        tickDefaultAccessControlForHelper(instrNum);
    }
    tickAllSessionsForInstructor(instrNum);
}

function tickAllSessionsForInstructor(instrNum) {
    $("#sectiongroupforinstructor" + instrNum).children().prop("checked", true);
}

function tickDefaultAccessControlForOwner(instrNum) {
    $("#canmodifycourse" + instrNum).prop('checked', true);
    $("#canmodifyinstructor" + instrNum).prop('checked', true);
    $("#canmodifyowner" + instrNum).prop('checked', true);
    $("#canmodifysession" + instrNum).prop('checked', true);
    $("#canmodifystudent" + instrNum).prop('checked', true);
    $("#canviewstudentinsection" + instrNum).prop('checked', true);
    $("#canviewcommentinsection" + instrNum).prop('checked', true);
    $("#cangivecommentinsection" + instrNum).prop('checked', true);
    $("#canmodifycommentinsection" + instrNum).prop('checked', true);
    $("#canviewsessioninsection" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsection" + instrNum).prop('checked', true);
    $("#canmodifysessioninsection" + instrNum).prop('checked', true);
}

function tickDefaultAccessControlForManager(instrNum) {
    $("#canmodifycourse" + instrNum).prop('checked', false);
    $("#canmodifyinstructor" + instrNum).prop('checked', true);
    $("#canmodifyowner" + instrNum).prop('checked', false);
    $("#canmodifysession" + instrNum).prop('checked', true);
    $("#canmodifystudent" + instrNum).prop('checked', true);
    $("#canviewstudentinsection" + instrNum).prop('checked', true);
    $("#canviewcommentinsection" + instrNum).prop('checked', true);
    $("#cangivecommentinsection" + instrNum).prop('checked', true);
    $("#canmodifycommentinsection" + instrNum).prop('checked', true);
    $("#canviewsessioninsection" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsection" + instrNum).prop('checked', true);
    $("#canmodifysessioninsection" + instrNum).prop('checked', true);
}

function tickDefaultAccessControlForObserver(instrNum) {
    $("#canmodifycourse" + instrNum).prop('checked', false);
    $("#canmodifyinstructor" + instrNum).prop('checked', false);
    $("#canmodifyowner" + instrNum).prop('checked', false);
    $("#canmodifysession" + instrNum).prop('checked', false);
    $("#canmodifystudent" + instrNum).prop('checked', false);
    $("#canviewstudentinsection" + instrNum).prop('checked', true);
    $("#canviewcommentinsection" + instrNum).prop('checked', true);
    $("#cangivecommentinsection" + instrNum).prop('checked', false);
    $("#canmodifycommentinsection" + instrNum).prop('checked', false);
    $("#canviewsessioninsection" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsection" + instrNum).prop('checked', false);
    $("#canmodifysessioninsection" + instrNum).prop('checked', false);
}

function tickDefaultAccessControlForTutor(instrNum) {
    $("#canmodifycourse" + instrNum).prop('checked', false);
    $("#canmodifyinstructor" + instrNum).prop('checked', false);
    $("#canmodifyowner" + instrNum).prop('checked', false);
    $("#canmodifysession" + instrNum).prop('checked', false);
    $("#canmodifystudent" + instrNum).prop('checked', false);
    $("#canviewstudentinsection" + instrNum).prop('checked', true);
    $("#canviewcommentinsection" + instrNum).prop('checked', false);
    $("#cangivecommentinsection" + instrNum).prop('checked', true);
    $("#canmodifycommentinsection" + instrNum).prop('checked', false);
    $("#canviewsessioninsection" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsection" + instrNum).prop('checked', true);
    $("#canmodifysessioninsection" + instrNum).prop('checked', false);
}

function tickDefaultAccessControlForHelper(instrNum) {
    $("#canmodifycourse" + instrNum).prop('checked', false);
    $("#canmodifyinstructor" + instrNum).prop('checked', false);
    $("#canmodifyowner" + instrNum).prop('checked', false);
    $("#canmodifysession" + instrNum).prop('checked', false);
    $("#canmodifystudent" + instrNum).prop('checked', false);
    $("#canviewstudentinsection" + instrNum).prop('checked', false);
    $("#canviewcommentinsection" + instrNum).prop('checked', false);
    $("#cangivecommentinsection" + instrNum).prop('checked', false);
    $("#canmodifycommentinsection" + instrNum).prop('checked', false);
    $("#canviewsessioninsection" + instrNum).prop('checked', false);
    $("#cansubmitsessioninsection" + instrNum).prop('checked', false);
    $("#canmodifysessioninsection" + instrNum).prop('checked', false);
}

$(function() { 
    $("[data-toggle='tooltip']").tooltip({html: true}); 
});

$(function() {
    $("input[name='accesslevelforinstructor']").click(function() {
        var instrNum = parseInt($(this).attr("id").substring(24));
        var instrRole = $(this).val();
        setDefaultAccessControlForInstructor(instrNum, instrRole);
    });
});
