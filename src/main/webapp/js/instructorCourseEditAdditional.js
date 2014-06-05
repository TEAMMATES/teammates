function enableEditInstructor(instructorNum, totalInstructors) {
    for (var i=1; i<=totalInstructors; i++) {
        if (i == instructorNum) {
            enableFormEditInstructor(i);
        } else {
            disableFormEditInstructor(i);
        }
    }
    hideNewInstructorForm();
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

function hideNewInstructorForm() {
    $("#panelToAddInstructor").hide();
    $("#btnToShowNewInstructorForm").show();
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
    triggerSessionChangeInSection(instrNum);
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
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessioninsectionforinstructor" + instrNum).prop('checked', true);
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
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessioninsectionforinstructor" + instrNum).prop('checked', true);
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
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessioninsectionforinstructor" + instrNum).prop('checked', false);
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
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessioninsectionforinstructor" + instrNum).prop('checked', false);
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
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', false);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessioninsectionforinstructor" + instrNum).prop('checked', false);
}

function triggerSessionChangeInSection(instrNum) {
    $("#canviewsessioninsectionforinstructor" + instrNum).change();
    $("#cansubmitsessioninsectionforinstructor" + instrNum).change();
    $("#canmodifysessioninsectionforinstructor" + instrNum).change();
}

$(function() { 
    $("[data-toggle='tooltip']").tooltip({html: true}); 
});

$(function() {
    $("input[name='accesslevelforinstructor']").click(function() {
        // 24 is position where instrNum starts in id
        var instrNum = parseInt($(this).attr("id").substring(24));
        var instrRole = $(this).val();
        setDefaultAccessControlForInstructor(instrNum, instrRole);
    });
});

$(function() {
    $("input[id^='canviewsessioninsectionforinstructor']").change(function() {
        var instrNum = parseInt($(this).attr("id").substring(36));
        $("input[id^='canviewsessionforinstructor" + instrNum + "']").prop("checked", $(this).prop("checked"));
    });
    $("input[id^='cansubmitsessioninsectionforinstructor']").change(function() {
        var instrNum = parseInt($(this).attr("id").substring(38));
        $("input[id^='cansubmitsessionforinstructor" + instrNum + "']").prop("checked", $(this).prop("checked"));
    });
    $("input[id^='canmodifysessioninsectionforinstructor']").change(function() {
        var instrNum = parseInt($(this).attr("id").substring(38));
        $("input[id^='canmodifysessionforinstructor" + instrNum + "']").prop("checked", $(this).prop("checked"));
    });
});

$(document).ready(function() {
    var numOfInstr = $("form[id^='formEditInstructor']").length + 1;
    for (var i = 0;i < numOfInstr;i++) {
        tickDefaultAccessControlForOwner(i + 1);
        tickAllSessionsForInstructor(i + 1);
        triggerSessionChangeInSection(i + 1);
    }
});