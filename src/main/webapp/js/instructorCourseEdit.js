/**
 * Enable the user to edit one instructor and disable editting for other instructors
 * @param instructorNum
 * @param totalInstructors
 */
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

/**
 * Enable formEditInstructor's input fields, display the "Save changes" button,
 * and disable the edit link.
 * @param number
 */
function enableFormEditInstructor(number) {
    $("#instructorTable" + number).find(":input").not(".immutable").prop("disabled", false);
    $("#instrEditLink" + number).hide();
    $("#accessControlInfoForInstr" + number).hide();
    $("#accessControlEditDivForInstr" + number).show();
    $("#btnSaveInstructor" + number).show();
}

/**
 * Disable formEditInstructor's input fields, hide the "Save changes" button,
 * and enable the edit link.
 * @param number
 */
function disableFormEditInstructor(number) {
    $("#instructorTable" + number).find(":input").not(".immutable").prop("disabled", true);
    $("#instrEditLink" + number).show();
    $("#accessControlInfoForInstr" + number).show();
    $("#accessControlEditDivForInstr" + number).hide();
    $("#btnSaveInstructor" + number).hide();
}

function showNewInstructorForm() {
    $("#panelAddInstructor").show();
    $("#btnShowNewInstructorForm").hide();
    $('html, body').animate({scrollTop: $('#frameBodyWrapper')[0].scrollHeight}, 1000);
}

function hideNewInstructorForm() {
	$("#panelAddInstructor").hide();
    $("#btnShowNewInstructorForm").show();
}

/**
 * Functions to trigger registration key sending to a specific instructor in the
 * course.
 * @param courseID
 * @param email
 */
function toggleSendRegistrationKey(courseID, email) {
    return confirm("Do you wish to re-send the invitation email to this instructor now?");
}

function showTunePermissionsDiv(instrNum) {
    $("#tunePermissionsDivForInstructor" + instrNum).show();
}

function hideTunePermissionDiv(instrNum) {
	$("#tunePermissionsDivForInstructor" + instrNum).hide();
}

function showTuneSectionPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).show();
	var numberOfSections = $("select#section" + sectionNum + "forinstructor" + instrNum + " option").length;
	var numOfVisibleSections = $("#tunePermissionsDivForInstructor" + 1 + " div[id^='tuneSectionPermissionsDiv']").filter(":visible").length;
	
	if (numOfVisibleSections == numberOfSections) {
		$("#addSectionLevelForInstructor" + instrNum).hide();
	}
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "set']").attr("value", "true");
	setAddSectionLevelLink(instrNum);
}

function setAddSectionLevelLink(instrNum) {
	var foundNewLink = false;
	var allSectionSelects = $("#tunePermissionsDivForInstructor" + instrNum + " div[id^='tuneSectionPermissionsDiv']").find("input[type=hidden]").not("[name*='sessions']");
	for (var idx=0;idx < allSectionSelects.length;idx++) {
		var item = $(allSectionSelects[idx]);
		if (item.attr("value") === "false") {
			var sectionNumStr = item.attr("name").substring(9).slice(0, -3);
			$("#addSectionLevelForInstructor" + instrNum).attr("onclick", "showTuneSectionPermissionsDiv(" + instrNum + ", " + sectionNumStr + ")");
			foundNewLink = true;
			break;
		}
	}
	if (!foundNewLink) {
		$("#addSectionLevelForInstructor" + instrNum).hide();
	}
}

function hideTuneSectionPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).hide();
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "set']").attr("value", "false");
	$("#addSectionLevelForInstructor" + instrNum).show();
	setAddSectionLevelLink(instrNum);
}

function showTuneSessionnPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSessionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).show();
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).html("Hide session-level permissions");
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).attr("onclick", "hideTuneSessionnPermissionsDiv(" + instrNum + ", " + sectionNum + ")");
    $("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "sessionsset']").attr("value", "true");
}

function hideTuneSessionnPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSessionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).hide();
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).html("Give different permissions for sessions in this section");
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).attr("onclick", "showTuneSessionnPermissionsDiv(" + instrNum + ", " + sectionNum + ")");
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issection" + sectionNum + "sessionsset']").attr("value", "false");
}

function checkTheRoleThatApplies(instrNum) {
	var instrRole = $("#accessControlInfoForInstr"+instrNum+" div div p").html();
	$("input[id='instructorroleforinstructor" + instrNum + "']").filter("[value='" + instrRole + "']").prop("checked", true);
}

function configurePrivilegesOfRoleForInstructor(instrNum, role) {
	checkPrivilegesOfRoleForInstructor(instrNum, role);
	if (!(role === "Helper")) {
		hideTunePermissionDiv(instrNum);
	}
}

function checkPrivilegesOfRoleForInstructor(instrNum, role) {
	if (role === "Co-owner") {
		checkPrivilegesOfCoownerForInstructor();
	} else if (role === "Manager") {
		checkPrivilegesOfManagerForInstructor();
	} else if (role === "Observer") {
		checkPrivilegesOfObserverForInstructor();
	} else if (role === "Tutor") {
		checkPrivilegesOfTutorForInstructor();
	} else if (role === "Helper") {
		checkPrivilegesOfCustomForInstructor(instrNum);
	} else {
		console.log(role + " is not properly defined");
	}
}

function showInstructorRoleModal(instrNum, instrRole) {
	checkPrivilegesOfRoleForInstructor(instrNum, instrRole);
	$('#tunePermissionsDivForInstructorAll').modal();
}

function checkPrivilegesOfCoownerForInstructor() {
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycourse']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifyinstructor']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysession']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifystudent']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canviewstudentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='cangivecommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canviewcommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='cansubmitsessioninsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canviewsessioninsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysessioncommentinsection']").prop("checked", true);
	
	$("#tunePermissionsDivForInstructorAll #instructorRoleModalLabel").html("Permissions for Co-owner");
}

function checkPrivilegesOfManagerForInstructor() {
	checkPrivilegesOfCoownerForInstructor();
	
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycourse']").prop("checked", false);
	
	$("#tunePermissionsDivForInstructorAll #instructorRoleModalLabel").html("Permissions for Manager");
}

function checkPrivilegesOfObserverForInstructor() {
	checkPrivilegesOfCoownerForInstructor();
	
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycourse']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifyinstructor']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysession']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifystudent']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='cangivecommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='cansubmitsessioninsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysessioncommentinsection']").prop("checked", false);
	
	$("#tunePermissionsDivForInstructorAll #instructorRoleModalLabel").html("Permissions for Observer");
}

function checkPrivilegesOfTutorForInstructor() {
	checkPrivilegesOfCoownerForInstructor();
	
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycourse']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifyinstructor']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysession']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifystudent']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canviewcommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysessioncommentinsection']").prop("checked", false);
	
	$("#tunePermissionsDivForInstructorAll #instructorRoleModalLabel").html("Permissions for Tutor");
}

function checkPrivilegesOfCustomForInstructor(instrNum) {
	showTunePermissionsDiv(instrNum);
}


/**
 * Function that shows confirmation dialog for deleting a instructor
 * @param courseID
 * @param instructorName
 * @param isDeleteOwnself
 * @returns
 */
function toggleDeleteInstructorConfirmation(courseID, instructorName, isDeleteOwnself) {
    if (isDeleteOwnself) {
        return confirm("Are you sure you want to delete your instructor role from the course " + courseID + "? " +
        "You will not be able to access the course anymore.");
    } else {
        return confirm("Are you sure you want to delete the instructor " + instructorName + " from " + courseID + "? " +
            "He/she will not be able to access the course anymore.");
    }
}

$(function(){
	var numOfInstr = $("form[id^='formEditInstructor']").length;
	for (var i=0; i<numOfInstr;i++) {
		checkTheRoleThatApplies(i+1);
	}
	$("input[id^='instructorroleforinstructor']").change(function(){
		var idAttr = $(this).attr('id');
		var instrNum = parseInt(idAttr.substring(27));
		var role = $(this).attr("value");
		configurePrivilegesOfRoleForInstructor(instrNum, role);
	});
});
