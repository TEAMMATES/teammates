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
    $("#canmodifycourseforinstructor" + instrNum).prop('checked', true);
    $("#canmodifyinstructorforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifystudentforinstructor" + instrNum).prop('checked', true);
    $("#canviewstudentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canviewcommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#cangivecommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifycommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessioncommentinsectionforinstructor" + instrNum).prop('checked', true);
}

function tickDefaultAccessControlForManager(instrNum) {
    $("#canmodifycourseforinstructor" + instrNum).prop('checked', false);
    $("#canmodifyinstructorforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifystudentforinstructor" + instrNum).prop('checked', true);
    $("#canviewstudentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canviewcommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#cangivecommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifycommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessioncommentinsectionforinstructor" + instrNum).prop('checked', true);
}

function tickDefaultAccessControlForObserver(instrNum) {
    $("#canmodifycourseforinstructor" + instrNum).prop('checked', false);
    $("#canmodifyinstructorforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifystudentforinstructor" + instrNum).prop('checked', false);
    $("#canviewstudentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canviewcommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#cangivecommentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifycommentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessioncommentinsectionforinstructor" + instrNum).prop('checked', false);
}

function tickDefaultAccessControlForTutor(instrNum) {
    $("#canmodifycourseforinstructor" + instrNum).prop('checked', false);
    $("#canmodifyinstructorforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifystudentforinstructor" + instrNum).prop('checked', false);
    $("#canviewstudentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canviewcommentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#cangivecommentinsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifycommentinsection" + instrNum).prop('checked', false);
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', true);
    $("#canmodifysessioncommentinsectionforinstructor" + instrNum).prop('checked', false);
}

function tickDefaultAccessControlForHelper(instrNum) {
    $("#canmodifycourseforinstructor" + instrNum).prop('checked', false);
    $("#canmodifyinstructorforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifystudentforinstructor" + instrNum).prop('checked', false);
    $("#canviewstudentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#canviewcommentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#cangivecommentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifycommentinsectionforinstructor" + instrNum).prop('checked', false);
    $("#canviewsessioninsectionforinstructor" + instrNum).prop('checked', false);
    $("#cansubmitsessioninsectionforinstructor" + instrNum).prop('checked', false);
    $("#canmodifysessioncommentinsectionforinstructor" + instrNum).prop('checked', false);
}

function triggerSessionChangeInSection(instrNum) {
    $("#canviewsessioninsectionforinstructor" + instrNum).change();
    $("#cansubmitsessioninsectionforinstructor" + instrNum).change();
    $("#canmodifysessioncommentinsectionforinstructor" + instrNum).change();
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
    $("input[id^='canmodifysessioncommentinsectionforinstructor']").change(function() {
        var instrNum = parseInt($(this).attr("id").substring(45));
        $("input[id^='canmodifysessioncommentforinstructor" + instrNum + "']").prop("checked", $(this).prop("checked"));
    });
});

$(document).ready(function() {
    var numOfInstr = $("form[id^='formEditInstructor']").length + 1;
    tickDefaultAccessControlForOwner(numOfInstr);
    tickAllSessionsForInstructor(numOfInstr);
    triggerSessionChangeInSection(numOfInstr);
});