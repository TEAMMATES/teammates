// global parameter to remember settings for custom access level

var INSTRUCTOR_COURSE_EDIT_INSTRUCTOR_ACCESS_LEVEL_WHEN_LOADING_PAGE = [];

/**
 * Enable the user to edit one instructor and disable editting for other instructors
 * @param instructorNum
 * @param totalInstructors
 */
function enableEditInstructor(event) {
	var instructorNum = event.data.instructorIndex;
	var totalInstructors = event.data.total;
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
    $("#btnShowCopyInstructorsForm").hide();
    $('html, body').animate({scrollTop: $('#frameBodyWrapper')[0].scrollHeight}, 1000);
}

function hideNewInstructorForm() {
	$("#panelAddInstructor").hide();
    $("#btnShowNewInstructorForm").show();
    $("#btnShowCopyInstructorsForm").show();
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
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issectiongroup" + sectionNum + "set']").attr("value", "true");
	setAddSectionLevelLink(instrNum);
}

function setAddSectionLevelLink(instrNum) {
	var foundNewLink = false;
	var allSectionSelects = $("#tunePermissionsDivForInstructor" + instrNum + " div[id^='tuneSectionPermissionsDiv']").find("input[type=hidden]").not("[name*='sessions']");
	for (var idx=0;idx < allSectionSelects.length;idx++) {
		var item = $(allSectionSelects[idx]);
		if (item.attr("value") === "false") {
			var sectionNumStr = item.attr("name").substring(14).slice(0, -3);
			$("#addSectionLevelForInstructor" + instrNum).attr("onclick", "showTuneSectionPermissionsDiv(" + instrNum + ", " + sectionNumStr + ")");
			foundNewLink = true;
			break;
		}
	}
	if (!foundNewLink) {
		$("#addSectionLevelForInstructor" + instrNum).hide();
	} else {
		$("#addSectionLevelForInstructor" + instrNum).show();
	}
}

function hideTuneSectionPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).hide();
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issectiongroup" + sectionNum + "set']").attr("value", "false");
	$("#addSectionLevelForInstructor" + instrNum).show();
	setAddSectionLevelLink(instrNum);
}

function showTuneSessionnPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSessionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).show();
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).html("Hide session-level permissions");
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).attr("onclick", "hideTuneSessionnPermissionsDiv(" + instrNum + ", " + sectionNum + ")");
    $("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issectiongroup" + sectionNum + "sessionsset']").attr("value", "true");
}

function hideTuneSessionnPermissionsDiv(instrNum, sectionNum) {
	$("#tuneSessionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum).hide();
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).html("Give different permissions for sessions in this section");
	$("#toggleSessionLevelInSection" + sectionNum + "ForInstructor" + instrNum).attr("onclick", "showTuneSessionnPermissionsDiv(" + instrNum + ", " + sectionNum + ")");
	$("#tuneSectionPermissionsDiv" + sectionNum + "ForInstructor" + instrNum + " input[name='issectiongroup" + sectionNum + "sessionsset']").attr("value", "false");
}

function checkTheRoleThatApplies(instrNum) {
	var instrRole = $("#accessControlInfoForInstr"+instrNum+" div div p span").html();
	$("input[id='instructorroleforinstructor" + instrNum + "']").filter("[value='" + instrRole + "']").prop("checked", true);
	if (instrRole === "Custom") {
		checkPrivilegesOfRoleForInstructor(instrNum, instrRole);
	}
}

function checkPrivilegesOfRoleForInstructor(instrNum, role) {
	if (role === "Co-owner") {
		checkPrivilegesOfCoownerForInstructor(instrNum);
	} else if (role === "Manager") {
		checkPrivilegesOfManagerForInstructor(instrNum);
	} else if (role === "Observer") {
		checkPrivilegesOfObserverForInstructor(instrNum);
	} else if (role === "Tutor") {
		checkPrivilegesOfTutorForInstructor(instrNum);
	} else if (role === "Custom") {
		checkPrivilegesOfCustomForInstructor(instrNum);
	} else {
		console.log(role + " is not properly defined");
	}
}

function checkPrivilegesOfCoownerForInstructor(instrNum) {
	hideTunePermissionDiv(instrNum);
	
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifyinstructor']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysession']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifystudent']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewstudentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cangivecommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewcommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycommentinsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cansubmitsessioninsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewsessioninsection']").prop("checked", true);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysessioncommentinsection']").prop("checked", true);
}

function checkPrivilegesOfManagerForInstructor(instrNum) {
	checkPrivilegesOfCoownerForInstructor(instrNum);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", false);
}

function checkPrivilegesOfObserverForInstructor(instrNum) {
	checkPrivilegesOfCoownerForInstructor(instrNum);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifyinstructor']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysession']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifystudent']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cangivecommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cansubmitsessioninsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysessioncommentinsection']").prop("checked", false);
}

function checkPrivilegesOfTutorForInstructor(instrNum) {
	checkPrivilegesOfCoownerForInstructor(instrNum);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifyinstructor']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysession']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifystudent']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewcommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysessioncommentinsection']").prop("checked", false);
}

function checkPrivilegesOfCustomForInstructor(instrNum) {
	var numOfInstr = $("form[id^='formEditInstructor']").length;
	if (instrNum <= numOfInstr && 
			(INSTRUCTOR_COURSE_EDIT_INSTRUCTOR_ACCESS_LEVEL_WHEN_LOADING_PAGE.length >= instrNum 
					&& INSTRUCTOR_COURSE_EDIT_INSTRUCTOR_ACCESS_LEVEL_WHEN_LOADING_PAGE[instrNum-1] === "Custom")) {
		$("#tunePermissionsDivForInstructor" + 1 + " input[checked='checked']").prop("checked", true);
		$("#tunePermissionsDivForInstructor" + 1 + " input[checked!='checked']").prop("checked", false);
	} else {
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycourse']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifyinstructor']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysession']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifystudent']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewstudentinsection']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cangivecommentinsection']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewcommentinsection']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifycommentinsection']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='cansubmitsessioninsection']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canviewsessioninsection']").prop("checked", false);
		$("#tunePermissionsDivForInstructor" + instrNum + " input[name='canmodifysessioncommentinsection']").prop("checked", false);
	}
	showTunePermissionsDiv(instrNum);
}

function showInstructorRoleModal(instrRole) {
	checkPrivilegesOfRoleForModal(instrRole);
	$('#tunePermissionsDivForInstructorAll').modal();
}

function checkPrivilegesOfRoleForModal(role) {
	if (role === "Co-owner") {
		checkPrivilegesOfCoownerForModal();
	} else if (role === "Manager") {
		checkPrivilegesOfManagerForModal();
	} else if (role === "Observer") {
		checkPrivilegesOfObserverForModal();
	} else if (role === "Tutor") {
		checkPrivilegesOfTutorForModal();
	} else {
		console.log(role + " is not properly defined");
	}
}

function checkPrivilegesOfCoownerForModal() {
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

function checkPrivilegesOfManagerForModal() {
	checkPrivilegesOfCoownerForModal();
	
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycourse']").prop("checked", false);
	
	$("#tunePermissionsDivForInstructorAll #instructorRoleModalLabel").html("Permissions for Manager");
}

function checkPrivilegesOfObserverForModal() {
	checkPrivilegesOfCoownerForModal();
	
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

function checkPrivilegesOfTutorForModal() {
	checkPrivilegesOfCoownerForModal();
	
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycourse']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifyinstructor']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysession']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifystudent']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canviewcommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifycommentinsection']").prop("checked", false);
	$("#tunePermissionsDivForInstructorAll input[name='canmodifysessioncommentinsection']").prop("checked", false);
	
	$("#tunePermissionsDivForInstructorAll #instructorRoleModalLabel").html("Permissions for Tutor");
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

function bindChangingRole(index){
	$("input[id^='instructorroleforinstructor" + index + "']").change(function(){
		var idAttr = $(this).attr('id');
		var instrNum = parseInt(idAttr.substring(27));
		var role = $(this).attr("value");
		checkPrivilegesOfRoleForInstructor(instrNum, role);
	});
}

var numRowsSelected = 0;
var copiedInstructorsEmails = [];
var copiedInstructorsNames = [];
var copiedInstructorsDisplayedNames = [];
var copiedInstructorsRoles = [];
var copiedInstructorsPrivileges = [[]];

function updatePrivilegesOfRoleForCopyModal(instructorIndex, role) {
	if (role === "Co-owner") {
		updatePrivilegesForInstructorAsCoowner(instructorIndex);
	} else if (role === "Manager") {
		updatePrivilegesForInstructorAsManager(instructorIndex);
	} else if (role === "Observer") {
		updatePrivilegesForInstructorAsObserver(instructorIndex);
	} else if (role === "Tutor") {
		updatePrivilegesForInstructorAsTutor(instructorIndex);
	} else if (role === "Custom") {
		updatePrivilegesForInstructorAsCustom(instructorIndex);
	} else {
		console.log(role + " is not properly defined");
	}
}

function markAllPrivilegesForInstructorAsBoolean(instructorIndex, bool) {
	var i;
	for(i = 0; i < 11; i++) {
		copiedInstructorsPrivileges[instructorIndex][i] = bool;
	}
}

function updatePrivilegesForInstructorAsCoowner(instructorIndex) {
	markAllPrivilegesForInstructorAsBoolean(instructorIndex, true);
}

function updatePrivilegesForInstructorAsManager(instructorIndex) {
	markAllPrivilegesForInstructorAsBoolean(instructorIndex, true);
	
	copiedInstructorsPrivileges[instructorIndex][0] = false;
}

function updatePrivilegesForInstructorAsObserver(instructorIndex) {
	markAllPrivilegesForInstructorAsBoolean(instructorIndex, true);
	
	copiedInstructorsPrivileges[instructorIndex][4] = false;
	copiedInstructorsPrivileges[instructorIndex][6] = false;
	copiedInstructorsPrivileges[instructorIndex][9] = false;
}

function updatePrivilegesForInstructorAsTutor(instructorIndex) {
	markAllPrivilegesForInstructorAsBoolean(instructorIndex, true);
	
	copiedInstructorsPrivileges[instructorIndex][4] = false;
	copiedInstructorsPrivileges[instructorIndex][5] = false;
	copiedInstructorsPrivileges[instructorIndex][8] = false;
	copiedInstructorsPrivileges[instructorIndex][9] = false;
}

function updateCopyModalFormValues() {
	var modalInstructorEmailsValue = "";
	var modalInstructorNamesValue = "";
	var modalInstructorDisplayedNamesValue = "";
	var modalInstructorRolesValue ="";
	
	var areInstructorsAllowedToModifyCourse = "";
    var areInstructorsAllowedToModifyInstructor = "";
    var areInstructorsAllowedToModifySession = "";
    var areInstructorsAllowedToModifyStudent = "";
    
    var areInstructorsAllowedToViewStudentInSections = "";
    var areInstructorsAllowedToViewCommentInSections = "";
    var areInstructorsAllowedToGiveCommentInSections = "";
    var areInstructorsAllowedToModifyCommentInSections = "";
    
    var areInstructorsAllowedToViewSessionInSections = "";
    var areInstructorsAllowedToSubmitSessionInSections = "";
    var areInstructorsAllowedToModifySessionInSections = "";
	
	var i, j;
	for(i = 0; i < copiedInstructorsEmails.length; i++) {
		if(i === 0) {
			modalInstructorEmailsValue += copiedInstructorsEmails[i];
			modalInstructorNamesValue += copiedInstructorsNames[i];
			modalInstructorDisplayedNamesValue += copiedInstructorsDisplayedNames[i];
			modalInstructorRolesValue += copiedInstructorsRoles[i];
			
			areInstructorsAllowedToModifyCourse += copiedInstructorsPrivileges[i][0];
		    areInstructorsAllowedToModifyInstructor += copiedInstructorsPrivileges[i][1];
		    areInstructorsAllowedToModifySession += copiedInstructorsPrivileges[i][2];
		    areInstructorsAllowedToModifyStudent += copiedInstructorsPrivileges[i][3];
		    
		    areInstructorsAllowedToViewStudentInSections += copiedInstructorsPrivileges[i][4];
		    areInstructorsAllowedToViewCommentInSections += copiedInstructorsPrivileges[i][5];
		    areInstructorsAllowedToGiveCommentInSections += copiedInstructorsPrivileges[i][6];
		    areInstructorsAllowedToModifyCommentInSections += copiedInstructorsPrivileges[i][7];
		    
		    areInstructorsAllowedToViewSessionInSections += copiedInstructorsPrivileges[i][8];
		    areInstructorsAllowedToSubmitSessionInSections += copiedInstructorsPrivileges[i][9];
		    areInstructorsAllowedToModifySessionInSections += copiedInstructorsPrivileges[i][10];
		
		} else {
			modalInstructorEmailsValue += " | " + copiedInstructorsEmails[i];
			modalInstructorNamesValue += " | " + copiedInstructorsNames[i];
			modalInstructorDisplayedNamesValue += " | " + copiedInstructorsDisplayedNames[i];
			modalInstructorRolesValue += " | " + copiedInstructorsRoles[i];
			
			areInstructorsAllowedToModifyCourse += " | " + copiedInstructorsPrivileges[i][0];
		    areInstructorsAllowedToModifyInstructor += " | " + copiedInstructorsPrivileges[i][1];
		    areInstructorsAllowedToModifySession += " | " + copiedInstructorsPrivileges[i][2];
		    areInstructorsAllowedToModifyStudent += " | " + copiedInstructorsPrivileges[i][3];
		    
		    areInstructorsAllowedToViewStudentInSections += " | " + copiedInstructorsPrivileges[i][4];
		    areInstructorsAllowedToViewCommentInSections += " | " + copiedInstructorsPrivileges[i][5];
		    areInstructorsAllowedToGiveCommentInSections += " | " + copiedInstructorsPrivileges[i][6];
		    areInstructorsAllowedToModifyCommentInSections += " | " + copiedInstructorsPrivileges[i][7];
		    
		    areInstructorsAllowedToViewSessionInSections += " | " + copiedInstructorsPrivileges[i][8];
		    areInstructorsAllowedToSubmitSessionInSections += " | " + copiedInstructorsPrivileges[i][9];
		    areInstructorsAllowedToModifySessionInSections += " | " + copiedInstructorsPrivileges[i][10];
		}
	}
	$('#modalInstructorEmails').val(modalInstructorEmailsValue);
	$('#modalInstructorNames').val(modalInstructorNamesValue);
	$('#modalInstructorDisplayedNames').val(modalInstructorDisplayedNamesValue);
	$('#modalInstructorRoles').val(modalInstructorRolesValue);

	$('input[name="caninstructorsmodifycourse"]').val(areInstructorsAllowedToModifyCourse);
	$('input[name="caninstructorsmodifyinstructor"]').val(areInstructorsAllowedToModifyInstructor);
	$('input[name="caninstructorsmodifysession"]').val(areInstructorsAllowedToModifySession);
	$('input[name="caninstructorsmodifystudent"]').val(areInstructorsAllowedToModifyStudent);
	$('input[name="caninstructorsviewstudentinsection"]').val(areInstructorsAllowedToViewStudentInSections);
	$('input[name="caninstructorsgivecommentinsection"]').val(areInstructorsAllowedToViewCommentInSections);
	$('input[name="caninstructorsviewcommentinsection"]').val(areInstructorsAllowedToGiveCommentInSections);
	$('input[name="caninstructorsmodifycommentinsection"]').val(areInstructorsAllowedToModifyCommentInSections);
	$('input[name="caninstructorssubmitsessioninsection"]').val(areInstructorsAllowedToViewSessionInSections);
	$('input[name="caninstructorsviewsessioninsection"]').val(areInstructorsAllowedToSubmitSessionInSections);
	$('input[name="caninstructorsmodifysessioncommentinsection"]').val(areInstructorsAllowedToModifySessionInSections);
}

function bindInstructorNameSelect() {
	$('.instructorNameSelect').change(function(e) {
		var changedInstructor = $(this).parent().siblings('.instructorEmail').text();
		var instructorIndex = copiedInstructorsEmails.indexOf(changedInstructor);
		var newInstructorName = $(this).find('option:selected').val();
		copiedInstructorsNames.splice(instructorIndex,1,newInstructorName);
		updateCopyModalFormValues();
	});
}

function bindDisplaySelect() {
	$('.displaySelect').change(function(e) {
		var changedInstructor = $(this).parent().siblings('.instructorEmail').text();
		var instructorIndex = copiedInstructorsEmails.indexOf(changedInstructor);
		var newInstructorDisplayedName = $(this).find('option:selected').val();
		copiedInstructorsDisplayedNames.splice(instructorIndex,1,newInstructorDisplayedName);
		updateCopyModalFormValues();
	});
}

function bindRolesSelect() {
	$('.roleSelect').change(function(e) {
		var changedInstructor = $(this).parent().siblings('.instructorEmail').text();
		var instructorIndex = copiedInstructorsEmails.indexOf(changedInstructor);
		var newRole = $(this).find('option:selected').val();
		copiedInstructorsRoles.splice(instructorIndex,1,newRole);
		updatePrivilegesOfRoleForCopyModal(instructorIndex, newRole);
		updateCopyModalFormValues();
	});
}

function bindClearSelectionButton() {
	$('#button_clear_selection').on('click', function(e) {
		e.preventDefault();
		$("#copyTableModalBody").find('.row-selected').each(function() {
			$(this).removeClass('row-selected');
			$(this).children('td:first').html('<input type="checkbox">');
			$(this).find('.instructorNameSelect').prop('disabled', true);
			$(this).find('.displaySelect').prop('disabled', true);
			$(this).find('.roleSelect').prop('disabled', true);
		});
		numRowsSelected = 0;
		$('#copySelectAll').prop('checked', false);
		$('#button_copy_submit').prop('disabled', true);
		copiedInstructorsEmails = [];
		copiedInstructorsNames = [];
		copiedInstructorsDisplayedNames = [];
		copiedInstructorsRoles = [];
		copiedInstructorsPrivileges = [[]];
		updateCopyModalFormValues();
	});
}

function bindCopyEvents() {
	$('#copySelectAll').change(function(e){
		if(this.checked) {
	        $('#copyTableModalBody').children().not('.row-selected').each(function(e) {
	        	$(this).addClass('row-selected');
	        	$(this).children('td:first').html('<input type="checkbox" checked="checked">');
	        	$(this).find('.instructorNameSelect').prop('disabled', false);
	        	$(this).find('.displaySelect').prop('disabled', false);
	        	$(this).find('.roleSelect').prop('disabled', false);
	        	var addedInstructor = $(this).children('td:nth-child(4)').text();
	        	copiedInstructorsEmails.push(addedInstructor);
	        	var instructorIndex = copiedInstructorsEmails.indexOf(addedInstructor);
	        	copiedInstructorsNames.push($(this).children('.instructorNames').find('option:selected').val());
	        	copiedInstructorsDisplayedNames.push($(this).children('.displayInformation').find('option:selected').val());
	        	var instructorRole = $(this).children('.instructorRoles').find('option:selected').val();
	            copiedInstructorsRoles.push(instructorRole);
	            var instructorPrivileges = [];
	            copiedInstructorsPrivileges.push(instructorPrivileges);
	            updatePrivilegesOfRoleForCopyModal(instructorIndex, instructorRole);
	        	numRowsSelected++;
	        });
		}
		updateCopyModalFormValues();
		if(numRowsSelected <= 0){
            $('#button_copy_submit').prop('disabled', true);
        } else {
            $('#button_copy_submit').prop('disabled', false);
        }
	});

    $('#copyTableModalBody>tr>td').not(".instructorNames").not(".displayInformation").not(".instructorRoles").on('click', function(e){
        e.preventDefault();
        
        if($(this).parent().hasClass('row-selected')){
            $(this).parent().removeClass('row-selected');
            $(this).parent().children('td:first').html('<input type="checkbox">');
            $(this).siblings().children('.instructorNameSelect').prop('disabled', true);
            $(this).siblings().children('.displaySelect').prop('disabled', true);
            $(this).siblings().children('.roleSelect').prop('disabled', true);
            var removedInstructor = $(this).parent().children('td:nth-child(4)').text();
            var instructorIndex = copiedInstructorsEmails.indexOf(removedInstructor);
            copiedInstructorsEmails.splice(instructorIndex, 1);
            copiedInstructorsNames.splice(instructorIndex, 1);
            copiedInstructorsDisplayedNames.splice(instructorIndex, 1);
            copiedInstructorsRoles.splice(instructorIndex, 1);
            copiedInstructorsPrivileges.splice(instructorIndex, 1);
            numRowsSelected--;
        } else {
            $(this).parent().addClass('row-selected');
            $(this).parent().children('td:first').html('<input type="checkbox" checked="checked">');
            $(this).siblings().children('.instructorNameSelect').prop('disabled', false);
            $(this).siblings().children('.displaySelect').prop('disabled', false);
            $(this).siblings().children('.roleSelect').prop('disabled', false);
            var addedInstructor = $(this).parent().children('td:nth-child(4)').text();
            copiedInstructorsEmails.push(addedInstructor);
            var instructorIndex = copiedInstructorsEmails.indexOf(addedInstructor);
            copiedInstructorsNames.push($(this).siblings('.instructorNames').find('option:selected').val()); 
            copiedInstructorsDisplayedNames.push($(this).siblings('.displayInformation').find('option:selected').val());
            var instructorRole = $(this).siblings('.instructorRoles').find('option:selected').val();
            copiedInstructorsRoles.push(instructorRole);
            var instructorPrivileges = [];
            copiedInstructorsPrivileges.push(instructorPrivileges);
            updatePrivilegesOfRoleForCopyModal(instructorIndex, instructorRole);
            numRowsSelected++;
            console.log(copiedInstructorsPrivileges[instructorIndex]);
        }
        updateCopyModalFormValues();

        if(numRowsSelected <= 0){
            $('#button_copy_submit').prop('disabled', true);
        } else {
            $('#button_copy_submit').prop('disabled', false);
        }
        return false;
    });
}


function bindCopyInstructorsButton() {
	$('#btnShowCopyInstructorsForm').on('click', function(e){
        e.preventDefault();
        $('#copyInstructorsModal').modal('show');
	});
}


function bindCopyButton() {
	$('#button_copy_submit').on('click', function(e){
        e.preventDefault();
        $('#copyModalForm').submit();
        return false;
    });
}

$(function(){
	var numOfInstr = $("form[id^='formEditInstructor']").length;
	for (var i=0; i<numOfInstr;i++) {
		var instrNum = i+1;
		var instrRole = $("#accessControlInfoForInstr"+instrNum+" div div p span").html().trim();
		INSTRUCTOR_COURSE_EDIT_INSTRUCTOR_ACCESS_LEVEL_WHEN_LOADING_PAGE.push(instrRole);
		checkTheRoleThatApplies(i+1);
	}
});

$(document).ready(function(){
	bindCopyInstructorsButton();
	bindCopyButton();
	bindClearSelectionButton();
	bindInstructorNameSelect();
	bindDisplaySelect();
	bindRolesSelect();
	bindCopyEvents();
});
