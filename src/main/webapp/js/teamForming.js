// AJAX
var xmlhttp = new getXMLObject();

// DATE OBJECT
var cal = new CalendarPopup();

//DISPLAY
var DISPLAY_NO_STUDENTS = "<font color=\"#F00\">The course does not have any students.</font>";
var DISPLAY_STUDENTTEAMCHANGED = "Student team has been changed.";
var DISPLAY_TEAMFORMINGSESSION_SCHEDULEINVALID = "<font color=\"#F00\">The team forming session schedule (start/deadline) is not valid.</font>";
var DISPLAY_TEAMFORMINGSESSION_DELETED = "The team forming session has been deleted.";
var DISPLAY_TEAMFORMINGSESSION_EDITED = "The team forming session has been edited.";
var DISPLAY_TEAMFORMINGSESSION_ADDED = "The team forming session has been added.";
var DISPLAY_TEAMPROFILE_SAVED = "The team profile has been saved.";
var DISPLAY_TEAMFORMINGSESSION_EXISTS = "<font color=\"#F00\">The team forming session exists already.</font>";
var DISPLAY_TEAMPROFILE_EXISTS = "<font color=\"#F00\">Same team profile exists already.</font>";
var DISPLAY_TEAMFORMINGSESSION_INFORMEDSTUDENTSOFCHANGES = "E-mails have been sent out to inform the students of the changes to the team forming session.";

//DIV
var DIV_TEAMFORMINGSESSION_MANAGEMENT = "coordinatorTeamFormingSessionManagement";
var DIV_TEAMFORMINGSESSION_TABLE = "coordinatorTeamFormingSessionTable";

// GLOBAL VARIABLES FOR GUI
var teamFormingSessionSort = { courseID:0 };
var teamFormingSessionSortStatus = teamFormingSessionSort.courseID;

// MESSAGES
var MSG_STUDENTJOINTEAM = "student has joined the team";
var MSG_TEAMFORMINGSESSION_EXISTS = "team forming session exists";
var MSG_TEAMFORMINGSESSION_EDITED = "team forming session edited";
var MSG_TEAMPROFILE_SAVED = "team profile saved";
var MSG_TEAMPROFILE_EXISTS = "team profile exists";

// OPERATIONS
var OPERATION_COORDINATOR_CREATETEAMFORMINGSESSION = "coordinator_createteamformingsession";
var OPERATION_COORDINATOR_DELETETEAMPROFILES = "coordinator_deleteteamprofiles";
var OPERATION_COORDINATOR_DELETETEAMPROFILE = "coordinator_deleteteamprofile";
var OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION = "coordinator_deleteteamformingsession";
var OPERATION_COORDINATOR_EDITSTUDENTTEAM = "coordinator_editstudentteam";
var OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION = "coordinator_editteamformingsession";
var OPERATION_COORDINATOR_CREATETEAMPROFILE = "coordinator_createteamprofile";
var OPERATION_COORDINATOR_EDITTEAMPROFILE = "coordinator_editteamprofile";
var OPERATION_COORDINATOR_GETSTUDENTSOFCOURSETEAM = "coordinator_getstudentsofcourseteam";
var OPERATION_COORDINATOR_GETSTUDENTSWITHOUTTEAM = "coordinator_getstudentswithoutteam";
var OPERATION_COORDINATOR_GETTEAMSOFCOURSE = "coordinator_getteamsofcourse";
var OPERATION_COORDINATOR_GETTEAMDETAIL = "coordinator_getteamdetail";
var OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST = "coordinator_getteamformingsessionlist";
var OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES = "coordinator_informstudentsofteamformingsessionchanges";
var OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING = "coordinator_remindstudentsteamforming";
var OPERATION_GETSTUDENTTEAMNAME = "getstudentteamname";
var OPERATION_JOINTEAM = "jointeam";
var OPERATION_SHOW_TEAMFORMING = "coordinator_teamforming";

// PARAMETERS
var TEAMCHANGE_NEWTEAM = "teamchange_newteam";
var TEAMFORMING_ACTIVATED = "activated";
var TEAMFORMING_DEADLINE = "deadline";
var TEAMFORMING_DEADLINETIME = "deadlinetime";
var TEAMFORMING_GRACEPERIOD = "graceperiod";
var TEAMFORMING_INSTRUCTIONS = "instr";
var TEAMFORMING_START = "start";
var TEAMFORMING_STARTTIME = "starttime";
var TEAMFORMING_TIMEZONE = "timezone";
var TEAMFORMING_PROFILETEMPLATE = "profile_template";
var TEAM_NAME = "teamName";
var NEW_TEAM_NAME = "newteamName";
var TEAM_PROFILE = "teamProfile";

function clearAllDisplay()
{
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = ""; 
	document.getElementById(DIV_COURSE_ENROLLMENT).innerHTML = "";
	document.getElementById(DIV_COURSE_ENROLLMENTBUTTONS).innerHTML = "";
	document.getElementById(DIV_COURSE_ENROLLMENTRESULTS).innerHTML = "";
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = ""; 
	document.getElementById(DIV_COURSE_TABLE).innerHTML = ""; 
	document.getElementById(DIV_EVALUATION_EDITBUTTONS).innerHTML = ""; 
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = "";
	document.getElementById(DIV_EVALUATION_MANAGEMENT).innerHTML = "";
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = "";
	document.getElementById(DIV_EVALUATION_TABLE).innerHTML = "";
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = ""; 
	document.getElementById(DIV_STATUS_EDITEVALUATIONRESULTS).innerHTML = "";
	document.getElementById(DIV_STUDENT_EDITBUTTONS).innerHTML = ""; 
	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = "";
	document.getElementById(DIV_STUDENT_TABLE).innerHTML = "";
	clearStatusMessage();
}

function changeStudentTeam(courseID, teamName, name, email)
{
	clearAllDisplay();
	var teams = getTeamsOfCourse(courseID);
	printChangeTeam(courseID, teamName, name, teams, email);	
}

function createProfileOfExistingTeams(courseID)
{
	var teams = getTeamsOfCourse(courseID);
	var courseInfo = getCourse(courseID);
	var teamProfile = "Please enter your team profile here.";
	
	for(a=0; a<teams.length; a++){
		teams[a].teamName = teams[a].teamName.replace(/^\s*|\s*$/,"");
		if(teams[a].teamName!="")
			createTeamProfile(courseID, courseInfo.name, teams[a].teamName, teamProfile);
	}
}

function createTeamProfile(courseId, courseName, teamName, teamProfile)
{
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_CREATETEAMPROFILE + 
				"&" + "courseId" + "=" + encodeURIComponent(courseId) +
				"&" + "courseName" + "=" + encodeURIComponent(courseName) +
				"&" + TEAM_NAME + "=" + encodeURIComponent(teamName) +
				"&" + TEAM_PROFILE + "=" + encodeURIComponent(teamProfile));
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: team forming session schedule invalid 
 * 4: team forming session exists
 * 
 */
function createTeamFormingSession(courseID, start, startTime, deadline, deadlineTime, timeZone, gracePeriod, instructions, profileTemplate)
{
	if(courseID == "" || start == "" || startTime == "" || deadline == "" || deadlineTime == "" ||
			timeZone == "" || gracePeriod == "" || instructions == "" || profileTemplate == "")
	{
		return 2;
	}
	
	else if(!isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime))
	{
		return 3;
	}
	
	else
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_CREATETEAMFORMINGSESSION + 
				"&" + COURSE_ID + "=" + encodeURIComponent(courseID) +
				"&" + TEAMFORMING_START + "=" + encodeURIComponent(start) + 
				"&" + TEAMFORMING_STARTTIME + "=" + encodeURIComponent(startTime) + 
				"&" + TEAMFORMING_DEADLINE + "=" + encodeURIComponent(deadline) + 
				"&" + TEAMFORMING_DEADLINETIME + "=" + encodeURIComponent(deadlineTime) +
				"&" + TEAMFORMING_TIMEZONE + "=" + encodeURIComponent(timeZone) +
				"&" + TEAMFORMING_GRACEPERIOD + "=" + encodeURIComponent(gracePeriod) +
				"&" + TEAMFORMING_INSTRUCTIONS + "=" + encodeURIComponent(instructions) + 
				"&" + TEAMFORMING_PROFILETEMPLATE + "=" + encodeURIComponent(profileTemplate));
	
		return handleCreateTeamFormingSession();
	}	
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function deleteTeamFormingSession(courseID, deadlineDate, deadlineTime)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION + "&" + COURSE_ID + "=" 
				+ encodeURIComponent(courseID) + "&" + TEAMFORMING_DEADLINE + "=" + encodeURIComponent(deadlineDate)
				+ "&" + TEAMFORMING_DEADLINETIME + "=" + encodeURIComponent(deadlineTime));
		
		return handleDeleteTeamFormingSession();
	}
}

function deleteTeamProfiles(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETETEAMPROFILES 
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	}
}

function deleteTeamProfile(courseID, teamName)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETETEAMPROFILE 
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID)
				+ "&" + TEAM_NAME + "=" + encodeURIComponent(teamName));
	}
}

function displayManageTeamFormingSession(teamFormingSessionList, loop)
{
	var courseID = teamFormingSessionList[loop].courseID;
	var start =  teamFormingSessionList[loop].start;
	var deadline =  teamFormingSessionList[loop].deadline;
	var gracePeriod =  teamFormingSessionList[loop].gracePeriod;
	var instructions =  teamFormingSessionList[loop].instructions;
	var profileTemplate =  teamFormingSessionList[loop].profileTemplate;
	var activated =  teamFormingSessionList[loop].activated;
	var status =  teamFormingSessionList[loop].status;

	clearAllDisplay();
	printEditTeams(courseID, start, deadline, gracePeriod, instructions, profileTemplate, 
			activated, status);
	
	//printing teams that are formed
	var teams = getTeamsOfCourse(courseID);
	var output = displayTeams(courseID, teams);
	
	//printing students without team
	var students = getStudentsWithoutTeam(courseID);
	output = displayStudentWithoutTeams(output, students);
	
	if(teams.length!=0 || students.length!=0){
		output = output
		+ "<br /><br />"
		+ "<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Update\" />";
	}
	document.getElementById(DIV_COURSE_TABLE).innerHTML = output;
	
	//for viewing team profiles
	for (pos=0; pos<teams.length; pos++) {
		if (document.getElementById('viewTeamProfile' + pos) != null 
				&& document.getElementById('viewTeamProfile' + pos).onclick == null) {
			document.getElementById('viewTeamProfile' + pos).onclick = function() {
				var current = this.id.substring(15,this.id.length);
				teams[current].teamName = teams[current].teamName.replace(/^\s*|\s*$/,"");
				if(teams[current].teamName!=""){
					var teamName = document.getElementById('viewTeamProfile'+current).innerHTML;
					manageTeamProfile(courseID, teamName);
				}
			};
		}
	}
	
	//for change team of students
	for (pos=0; pos<teams.length; pos++) {
		var studentsOfTeam = getStudentsOfCourseTeam(courseID, teams[pos].teamName);
		for(j=0; j<studentsOfTeam.length; j++){
			if (document.getElementById('changeStudentTeam' + pos + j) != null 
					&& document.getElementById('changeStudentTeam' + pos + j).onclick == null) {
				document.getElementById('changeStudentTeam' + pos + j).onclick = function() {
					var teamIndex = this.id.substring(this.id.length-2,this.id.length-1);
					var studentIndex = this.id.substring(this.id.length-1,this.id.length);
					studentsOfTeam = getStudentsOfCourseTeam(courseID, teams[teamIndex].teamName);
					changeStudentTeam(courseID, teams[teamIndex].teamName, 
							studentsOfTeam[studentIndex].name, studentsOfTeam[studentIndex].email);
				};
			}
		}
	}
	
	//for allocate team of students
	for (pos=0; pos<students.length; pos++) {
		if (document.getElementById('allocateStudentTeam' + pos) != null 
				&& document.getElementById('allocateStudentTeam' + pos).onclick == null) {
			document.getElementById('allocateStudentTeam' + pos).onclick = function() {
				var index = this.id.substring(this.id.length-1,this.id.length);
				moveToTeam(courseID, students[index].email, students[index].name);
			};
		}
	}
	
	//for with team student full profile
//	for (pos=0; pos<teams.length; pos++) {
//		var allStudentsOfTeam = getStudentsOfCourseTeam(courseID, teams[pos].teamName);
//		for(j=0; j<allStudentsOfTeam.length; j++){
//			if (document.getElementById('withTeamStudent' + pos + j) != null 
//					&& document.getElementById('withTeamStudent' + pos + j).onclick == null) {
//				document.getElementById('withTeamStudent' + pos + j).onclick = function() {
//					var teamIndex = this.id.substring(this.id.length-2,this.id.length-1);
//					var studentIndex = this.id.substring(this.id.length-1,this.id.length);
//					allStudentsOfTeam = getStudentsOfCourseTeam(courseID, teams[teamIndex].teamName);
//					displayStudentFullProfile(courseID, teams[teamIndex].teamName, 
//							allStudentsOfTeam[studentIndex].email);
//				};
//			}
//		}
//	}
	
	//for without team student full profile
//	for (pos=0; pos<students.length; pos++) {
//		if (document.getElementById('withoutTeamStudent' + pos) != null 
//				&& document.getElementById('withoutTeamStudent' + pos).onclick == null) {
//			document.getElementById('withoutTeamStudent' + pos).onclick = function() {
//				var index = this.id.substring(this.id.length-1,this.id.length);
//				displayStudentFullProfile(courseID, "", students[index].email);
//			};
//		}
//	}
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayStudentFullProfile(courseID, teamName, studentEmail)
{
	var student = getStudent(courseID, studentEmail);
	clearAllDisplay();
	printStudentProfileDetail(courseID, student.name, student.teamName, student.profileDetail);
}

function displayStudentWithoutTeams(output, students){
	if(students.length!=0)
		output = printStudentsWithoutTeam(output, students);
	return output;	
}

function displayTeams(courseID, teams){
	var output="";
	var validTeam;
	
	for(loop=0; loop<teams.length; loop++){
		teams[loop].teamName = teams[loop].teamName.replace(/^\s*|\s*$/,"");
		if(teams[loop].teamName!="")
			validTeam = 1;
	}
	if(validTeam == 1)
		output = output + "<div><h1>TEAMS FORMED</h1></div>";
	
	for(i=0; i<teams.length; i++){
		teams[i].teamName = teams[i].teamName.replace(/^\s*|\s*$/,"");
		if(teams[i].teamName!=""){
			var students = getStudentsOfCourseTeam(courseID, teams[i].teamName);
			output = printTeams(output, teams[i].teamName, students, i, courseID);
		}
	}
	return output;
}

function displayTeamFormingTab(){
	clearAllDisplay();
	initializetooltip();
	setStatusMessage(DISPLAY_LOADING);
	printCreateTeamFormingSessionForm();
	doGetTeamFormingSessionList();
	clearStatusMessage();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function doCreateTeamFormingSession(courseID, start, startTime, deadline, deadlineTime, timeZone, gracePeriod, instructions, profileTemplate)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results;
	var studentList = getStudentList(courseID);
	if(studentList.length==0)
		results = 5;
	else 
		results = createTeamFormingSession(courseID, start, startTime, deadline, deadlineTime,
			timeZone, gracePeriod, instructions, profileTemplate);
	
	clearStatusMessage();
	
	if(results == 0)
	{
		displayTeamFormingTab();
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_ADDED);
		createProfileOfExistingTeams(courseID);
	}
	
	else if(results == 1)
	{
		alert(DISPLAY_SERVERERROR);
	}
	
	else if(results == 2)
	{
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}
	
	else if(results == 3)
	{
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_SCHEDULEINVALID);
	}
	
	else if(results == 4)
	{
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_EXISTS);
	}
	
	else if(results == 5)
	{
		setStatusMessage(DISPLAY_NO_STUDENTS);
	}
}

function doDeleteTeamFormingSession(courseID, deadlineDate, deadlineTime)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = deleteTeamFormingSession(courseID, deadlineDate, deadlineTime);
	
	if(results == 0)
	{
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_DELETED);
		doGetTeamFormingSessionList();
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
	deleteTeamProfiles(courseID);
}

function doEditTeamFormingSession(courseID, editStart, editStartTime, editDeadline, editDeadlineTime,
		editGracePeriod, editInstructions, editProfileTemplate, activated, status)
{
	setStatusMessage(DISPLAY_LOADING);

	var results = editTeamFormingSession(courseID, editStart, editStartTime, editDeadline, editDeadlineTime,
			editGracePeriod, editInstructions, editProfileTemplate, activated, status);
	
	if(results == 0)
	{
		if(activated == true)
		{
			displayTeamFormingTab();
			informStudentsOfTeamFormingSessionChanges(courseID);
		}
		
		else
		{
			displayTeamFormingTab();
			setStatusMessage(DISPLAY_TEAMFORMINGSESSION_EDITED);
		}		
	}
	
	else if(results == 2)
	{
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}
	
	else if(results == 3)
	{
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_SCHEDULEINVALID);
	}
	
	else if(results == 4)
	{
		displayTeamFormingTab();
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_EDITED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetTeamFormingSessionList()
{	
	var results = getTeamFormingSessionList();
	
	if(results != 1) {
		printTeamFormingSessionList(results.sort(sortByCourseID));
		teamFormingSessionSortStatus = teamFormingSessionSort.courseID;
		document.getElementById("button_sortcourseid").setAttribute("class", "buttonSortAscending");
	}	
	
	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doRemindStudentsOfTeamForming(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = remindStudentsOfTeamForming(courseID);
	
	clearStatusMessage();
	
	if(results != 1)
	{
		setStatusMessage(DISPLAY_EVALUATION_REMINDERSSENT);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function editStudentTeam(courseID, email, newTeamName){
	var result;
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_JOINTEAM + 
				"&" + "courseId" + "=" + encodeURIComponent(courseID) +
				"&" + "teamName" + "=" + encodeURIComponent(newTeamName) +
				"&" + "email" + "=" + encodeURIComponent(email));
		result = handleJoinTeam();
	}
	
	if (result==1)
		alert(DISPLAY_SERVERERROR);
	else
	{
		goToTeamFormingSession(courseID);
		setStatusMessage(DISPLAY_STUDENTTEAMCHANGED);
	}	
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: schedule invalid 4: no
 * changes made
 */
function editTeamFormingSession(courseID, editStart, editStartTime, editDeadline, editDeadlineTime,
		editGracePeriod, editInstructions, editProfileTemplate, activated, status)
{
	setStatusMessage(DISPLAY_LOADING);
	startString = convertDateFromDDMMYYYYToMMDDYYYY(editStart);
	deadlineString = convertDateFromDDMMYYYYToMMDDYYYY(editDeadline);
	start = new Date(startString);
	deadline = new Date(deadlineString);
	
	if(courseID == "" || editStart == "" || editStartTime == "" || editDeadline == "" || editDeadlineTime == "" || 
			editGracePeriod == "" || editInstructions == "" || editProfileTemplate == "")
	{
		return 2;
	}
	
	else if(start>deadline || (start==deadline && editStartTime>editDeadlineTime))
	{
		return 3;
	}
	
	else
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION + "&" + COURSE_ID + "=" + encodeURIComponent(courseID) +
				"&" + TEAMFORMING_START + "=" + encodeURIComponent(editStart) + "&" + TEAMFORMING_STARTTIME + "=" + encodeURIComponent(editStartTime) + 
				"&" + TEAMFORMING_DEADLINE + "=" + encodeURIComponent(editDeadline) + "&" + TEAMFORMING_DEADLINETIME + "=" + encodeURIComponent(editDeadlineTime) +
				"&" + TEAMFORMING_GRACEPERIOD + "=" + encodeURIComponent(editGracePeriod) +
				"&" + TEAMFORMING_INSTRUCTIONS + "=" + encodeURIComponent(editInstructions) + 
				"&" + TEAMFORMING_PROFILETEMPLATE + "=" + encodeURIComponent(editProfileTemplate));
		
		return handleEditTeamFormingSession();
	}
}

function editTeamProfile(courseId, courseName,  teamName, newTeamName, newTeamProfile){
	setStatusMessage(DISPLAY_LOADING);
	
	if(teamName == "" || teamProfile == "")
		setStatusMessage(DISPLAY_FIELDS_EMPTY);	
	else
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITTEAMPROFILE + 
				"&" + "courseId" + "=" + encodeURIComponent(courseId) +
				"&" + "courseName" + "=" + encodeURIComponent(courseName) +
				"&" + "oldteamname" + "=" + encodeURIComponent(teamName) +
				"&" + TEAM_NAME + "=" + encodeURIComponent(newTeamName) +
				"&" + TEAM_PROFILE + "=" + encodeURIComponent(newTeamProfile));
		
		var results = handleEditTeamProfile();
		clearStatusMessage();
	}
	
	if(results == 0)
	{
		displayTeamFormingTab();
		//goToTeamFormingSession(courseId);
		setStatusMessage(DISPLAY_TEAMPROFILE_SAVED);
		if(teamName!=newTeamName)
			updateTeamNameInStudentTable(courseId, teamName, newTeamName);
	}
	
	else if(results == 1)
	{
		alert(DISPLAY_SERVERERROR);
	}
	
	else if(results == 4)
	{
		setStatusMessage(DISPLAY_TEAMPROFILE_EXISTS);
	}
}

function getStudent(courseID, studentEmail)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_GETSTUDENTTEAMNAME + 
					"&" + "courseId" + "=" + encodeURIComponent(courseID)+ 
					"&" + "email" + "=" + encodeURIComponent(studentEmail));
		return handleGetStudent();
	}
}

function getStudentsOfCourseTeam(courseID, teamName)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSTUDENTSOFCOURSETEAM + 
					"&" + "courseId" + "=" + encodeURIComponent(courseID)+ 
					"&" + TEAM_NAME + "=" + encodeURIComponent(teamName));
		return handleGetStudentsOfCourseTeam();
	}
}

function getStudentsWithoutTeam(courseID)
{	
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSTUDENTSWITHOUTTEAM + 
					"&" + "courseId" + "=" + encodeURIComponent(courseID));
		return handleGetStudentsWithoutTeam();
	}
}

/*
 * Returns
 * 
 * team list: successful 1: server error
 * 
 */
function getTeamsOfCourse(courseID)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETTEAMSOFCOURSE + 
					"&" + "courseId" + "=" + encodeURIComponent(courseID)); 
		return handleGetTeamsOfCourse();
	}
}

/*
 * Returns
 * 
 * teamProfile object: successful 1: server error
 * 
 */
function getTeamDetail(courseId, teamName)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETTEAMDETAIL + 
					"&" + "courseId" + "=" + encodeURIComponent(courseId) + 
					"&" + TEAM_NAME + "=" + encodeURIComponent(teamName)); 
		return handleGetTeamDetail();
	}
}

/*
 * Returns
 * 
 * teamFormingSessionList: successful 1: server error
 * 
 */
function getTeamFormingSessionList()
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST); 
		return handleGetTeamFormingSessionList();
	}
}

function goToCoordinatorTeamForming(){
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_SHOW_TEAMFORMING);
	}
	
	handleCoordinatorTeamForming();
}

function goToTeamFormingSession(courseID){
	var teamFormingSessionList = getTeamFormingSessionList();
	var index;
	for(loop=0; loop<teamFormingSessionList.length; loop++)
	{
		if(teamFormingSessionList[loop].courseID == courseID)
			index = loop;
	}
	displayManageTeamFormingSession(teamFormingSessionList, index);
}

function handleCoordinatorTeamForming()
{
	if (xmlhttp.status == 200) 
	{
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 4: team forming session exists
 * 
 */
function handleCreateTeamFormingSession()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		var message;
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			if(message == MSG_TEAMFORMINGSESSION_EXISTS)
			{
				return 4;
			}
			
			else
			{
				return 0;
			}
		}
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleDeleteTeamFormingSession()
{
	if(xmlhttp)
	{
		return 0;
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 4: no changes made
 * 
 */
function handleEditTeamFormingSession()
{
	if(xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			
			if(message == MSG_EVALUATION_EDITED)
			{
				return 0;
			}
			
			else
			{
				return 4;
			}
		}
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 4: team forming session exists
 * 
 */
function handleEditTeamProfile()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		var message;
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			if(message == MSG_TEAMPROFILE_EXISTS)
			{
				return 4;
			}
			
			else
			{
				return 0;
			}
		}
	}
	
	else
	{
		return 1;
	}
}

function handleGetStudent()
{
	if (xmlhttp.status == 200) 
	{
		var student = xmlhttp.responseXML.getElementsByTagName("student")[0];
		var profileDetail;
		var teamName;
		var name;
		var studentObject;
		
		if(student.firstChild!=null){
			teamName = student.getElementsByTagName("teamName")[0].firstChild.nodeValue;
			profileDetail = student.getElementsByTagName("studentprofiledetail")[0].firstChild.nodeValue;
			name = student.getElementsByTagName("studentname")[0].firstChild.nodeValue;
			
			studentObject = {teamName:teamName, profileDetail:profileDetail, name:name};
		}
		return studentObject;
	}
	else
		return 1;
}

/*
 * Returns
 * 
 * student ID and name list: successful 1: server error
 * 
 */
function handleGetStudentsOfCourseTeam()
{
	if (xmlhttp.status == 200) 
	{
		var students = xmlhttp.responseXML.getElementsByTagName("students")[0];
		var studentList = new Array(); 
		var student;
		var ID;		
		var name;
		var courseID;
		var email;
		var profileDetail;
		var profileSummary;
		
		if(students != null) {
			for(loop = 0; loop < students.childNodes.length; loop++)
			{
				student = students.childNodes[loop];				
				ID = student.getElementsByTagName("studentid")[0].firstChild.nodeValue;
				email = student.getElementsByTagName("studentemail")[0].firstChild.nodeValue;
				courseID = student.getElementsByTagName("courseid")[0].firstChild.nodeValue;
				name = student.getElementsByTagName("studentname")[0].firstChild.nodeValue;
				profileSummary = student.getElementsByTagName("studentprofilesummary")[0].firstChild.nodeValue;
				profileDetail = student.getElementsByTagName("studentprofiledetail")[0].firstChild.nodeValue;
				
				studentList[loop] = { ID:ID, email:email, courseID:courseID, name:name, 
						profileSummary:profileSummary, profileDetail:profileDetail};
			}
		}		
		return studentList;
	}else {		
		return 1;
	}
}

/*
 * Returns
 * 
 * student ID and name list: successful 1: server error
 * 
 */
function handleGetStudentsWithoutTeam()
{
	if (xmlhttp.status == 200) 
	{
		var students = xmlhttp.responseXML.getElementsByTagName("students")[0];
		var studentList = new Array(); 
		var student;
		var ID;		
		var name;
		var email;
		var profileSummary;
		var profileDetail;
		
		if(students != null) {
			for(loop = 0; loop < students.childNodes.length; loop++)
			{
				student = students.childNodes[loop];				
				ID = student.getElementsByTagName("studentid")[0].firstChild.nodeValue;
				email = student.getElementsByTagName("studentemail")[0].firstChild.nodeValue;
				name = student.getElementsByTagName("studentname")[0].firstChild.nodeValue;
				profileSummary = student.getElementsByTagName("studentprofilesummary")[0].firstChild.nodeValue;
				profileDetail = student.getElementsByTagName("studentprofiledetail")[0].firstChild.nodeValue;
				
				studentList[loop] = { ID:ID, email:email, name:name, profileSummary:profileSummary,
						profileDetail:profileDetail};
			}
		}		
		return studentList;
	}else {		
		return 1;
	}
}

/*
 * Returns
 * 
 * team list: successful 1: server error
 * 
 */
function handleGetTeamsOfCourse()
{
	if (xmlhttp.status == 200) 
	{
		var allTeamsOfCourse = xmlhttp.responseXML.getElementsByTagName("teams")[0];
		var teams = new Array(); 
		var teamName;
		
		if(allTeamsOfCourse != null) {
			for(loop = 0; loop < allTeamsOfCourse.childNodes.length; loop++)
			{
				teamName = allTeamsOfCourse.getElementsByTagName("team")[loop].firstChild.nodeValue;
				teams[loop] = { teamName:teamName};
			}
		}
		return teams;
	}else {		
		return 1;
	}
}

/*
 * Returns
 * 
 * teamProfile object: successful 1: server error
 * 
 */
function handleGetTeamDetail()
{
	if (xmlhttp.status == 200) 
	{
		var teamDetail = xmlhttp.responseXML.getElementsByTagName("teamdetail")[0];
		var courseID;
		var courseName;
		var teamName;
		var teamProfile;
		var teamDetailObject;
		
		if(teamDetail.firstChild!=null){
			courseID = teamDetail.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
			courseName = teamDetail.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
			teamName = teamDetail.getElementsByTagName(TEAM_NAME)[0].firstChild.nodeValue;
			teamProfile = teamDetail.getElementsByTagName(TEAM_PROFILE)[0].firstChild.nodeValue;

			teamDetailObject = { courseID:courseID, courseName:courseName, teamName:teamName, 
					teamProfile:teamProfile};
		}
		return teamDetailObject;
		
	}else {		
		return 1;
	}	
}

/*
 * Returns
 * 
 * teamFormingSessionList: successful 1: server error
 * 
 */
function handleGetTeamFormingSessionList()
{
	if (xmlhttp.status == 200) 
	{
		var teamFormingSessions = xmlhttp.responseXML.getElementsByTagName("teamformingsessions")[0];
		var teamFormingSessionList = new Array(); 
		var now;
		
		var teamFormingSession;
		var courseID;
		var profileTemplate;
		var instructions;
		var start;
		var deadline;
		var gracePeriod;
		var status;
		var activated;
		
		if(teamFormingSessions != null) {
			for(loop = 0; loop < teamFormingSessions.childNodes.length; loop++)
			{
				teamFormingSession = teamFormingSessions.childNodes[loop];
				
				courseID = teamFormingSession.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				start = new Date(teamFormingSession.getElementsByTagName(TEAMFORMING_START)[0].firstChild.nodeValue);
				deadline = new Date(teamFormingSession.getElementsByTagName(TEAMFORMING_DEADLINE)[0].firstChild.nodeValue);
				timeZone = parseFloat(teamFormingSession.getElementsByTagName(TEAMFORMING_TIMEZONE)[0].firstChild.nodeValue);
				gracePeriod = parseInt(teamFormingSession.getElementsByTagName(TEAMFORMING_GRACEPERIOD)[0].firstChild.nodeValue);
				activated = (teamFormingSession.getElementsByTagName(TEAMFORMING_ACTIVATED)[0].firstChild.nodeValue.toLowerCase() == "true");
				instructions = teamFormingSession.getElementsByTagName(TEAMFORMING_INSTRUCTIONS)[0].firstChild.nodeValue;
				profileTemplate = teamFormingSession.getElementsByTagName(TEAMFORMING_PROFILETEMPLATE)[0].firstChild.nodeValue;

				now = getDateWithTimeZoneOffset(timeZone);

				// Check if evaluation should be open or closed
				if(now > start && deadline > now)
				{
					status = "OPEN";
				}
				
				else if(now > deadline || activated)
				{
					status = "CLOSED";
				}
				
				else if (now < start && !activated)
				{
					status = "AWAITING";
				}
				
				teamFormingSessionList[loop] = { courseID:courseID, start:start, deadline:deadline, 
						timeZone:timeZone, gracePeriod:gracePeriod, instructions:instructions,
						 activated:activated, profileTemplate:profileTemplate, status:status};
			}
		}		
		return teamFormingSessionList;
	}
	else {		
		return 1;
	}
}

function handleJoinTeam()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;			
			if(message == MSG_STUDENTJOINTEAM)
				return 0;
		}
	}
	else
		return 1;
}

function informStudentsOfTeamFormingSessionChanges(courseID)
{
	alert("inside inform");
	var s = confirm("Do you want to send e-mails to the students to inform them of changes to the team forming session?");
	if (s == true) {
		
		setStatusMessage(DISPLAY_LOADING);
		
		if(xmlhttp)
		{
			xmlhttp.open("POST","/teamforming",false); 
			xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES 
					+ "&" + COURSE_ID + "="	+ encodeURIComponent(courseID));
		}		
		
		var results = handleInformStudentsOfEvaluationChanges();		
		clearStatusMessage();
		
		if(results != 1)
		{
			setStatusMessage(DISPLAY_TEAMFORMINGSESSION_INFORMEDSTUDENTSOFCHANGES);
		}
		
		else
		{
			alert(DISPLAY_SERVERERROR);
		}
	} else {
		clearStatusMessage();
	}
	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);	
}

function manageTeamProfile(courseID, teamName){
	clearAllDisplay();
	var teamDetail = getTeamDetail(courseID, teamName);
	var courseInfo = getCourse(courseID);
	printTeamDetail(courseID, courseInfo.name, teamName, teamDetail);
}

function moveToTeam(courseID, email, name)
{
	clearAllDisplay();
	var teams = getTeamsOfCourse(courseID);
	var teamName = "";
	printChangeTeam(courseID, teamName, name, teams, email);
}

function printChangeTeam(courseID, teamName, name, teams, email){
	var outputHeader = "<h1>CHANGE TEAM OF "+name+"</h1>";

	var outputForm = ""
			+ "<form method=\"post\" action=\"\" name=\"form_changeteam\">"
			+ "<table class=\"addform round\">" + "<tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course ID:</td>"
			+ "<td id=\"courseId\">"+courseID+"</td>"
			+ "</tr>";
	
	if(teamName!="")
		outputForm = outputForm
		+ "<tr>"
		+ "<td class=\"attribute\" >Current team:</td>"
		+ "<td id=\"courseName\">"+teamName+"</td>"
		+ "</tr>";
	
	outputForm = outputForm
			+ "<tr>"
			+ "<td class=\"attribute\" >" 
			+ "<input type=\"radio\" name=\""
			+ TEAMCHANGE_NEWTEAM
			+ "\" id=\""
			+ TEAMCHANGE_NEWTEAM
			+ "\" value=\"true\" CHECKED "
			+ "onmouseover=\"ddrivetip('Enable this if you want to add this student to an existing team.')\""
			+ "onmouseout=\"hideddrivetip()\" >"
			+ "Choose an existing team:</td>"
			+ "<td>"
			+ "<select style=\"width: 260px;\" name=\""
			+ TEAM_NAME
			+ "\" id=\""
			+ TEAM_NAME
			+ "\""
			+ "onmouseover=\"ddrivetip('Please select the new team for the student.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=1>";
	
	for(x = 0; x < teams.length; x++)
	{
		teams[x].teamName = teams[x].teamName.replace(/^\s*|\s*$/,"");
		if(teams[x].teamName!="")
			outputForm = outputForm + "<option value=\""+teams[x].teamName + "\">"+teams[x].teamName+"</option>";
	}
		
	outputForm = outputForm
			+ "</select></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >" 
			+ "<input type=\"radio\" name=\""
			+ TEAMCHANGE_NEWTEAM
			+ "\" id=\""
			+ TEAMCHANGE_NEWTEAM
			+ "\" value=\"false\" "
			+ "onmouseover=\"ddrivetip('Enable this if you want to add this student to a new team.')\""
			+ "onmouseout=\"hideddrivetip()\" >"
			+ "Enter a new team:</td>"
			+ "<td><input id=\"" + NEW_TEAM_NAME + "\" style=\"width: 100px;\" type=\"text\"/></td>"
			+ "</tr>"
			+ "<tr><td></td><td>"
			+ "<input id='button_back' type=\"button\" class=\"button\" onClick=\"goToTeamFormingSession('"
			+ courseID + "')\" value=\"Back\" tabindex=2 />"
			+ "<input id='button_saveTeamChange' type=\"button\" class=\"button\""
			+ "value=\"Save\" tabindex=2 />"
			+ "</td></tr></table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
	
	document.getElementById('button_saveTeamChange').onclick = function() {
		var val = getCheckedValue(document.getElementById(TEAMCHANGE_NEWTEAM));
		var teamName = document.getElementById(TEAM_NAME).value;
		if(val=="true")
			editStudentTeam(courseID, email, teamName);
		else{
			var newTeamName = document.getElementById(NEW_TEAM_NAME).value;
			var teamProfile = "Please enter your team profile here.";			
			var courseInfo = getCourse(courseID);
			//change student's team name
			editStudentTeam(courseID, email, newTeamName);
			//create team profile
			createTeamProfile(courseID, courseInfo.name, newTeamName, teamProfile);
		}
	};
}

function printCreateTeamFormingSessionForm() {
	var outputHeader = "<h1>CREATE TEAM-FORMING SESSION</h1>";

	var outputForm = ""
			+ "<form method=\"post\" action=\"\" name=\"form_createTeamFormingSession\">"
			+ "<table class=\"addform round\">" + "<tr>"
			+ "<td class=\"attribute\" >Course ID:</td>"
			+ "<td><select style=\"width: 260px;\" name=\""
			+ COURSE_ID
			+ "\" id=\""
			+ COURSE_ID
			+ "\""
			+ "onmouseover=\"ddrivetip('Please select the course for which the Team-Forming session is to be created.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=1>";
	
	var courseList = getCourseList();	
	if(courseList != 1){
		for(x = 0; x < courseList.length; x++)
			outputForm = outputForm + "<option value=\""+courseList[x].ID + "\">"+courseList[x].ID+"</option>";
	}
	else{
		alert(DISPLAY_SERVERERROR);
	}
	
	outputForm = outputForm + "</select></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Start time:</td>"
			+ "<td><input style=\"width: 100px;\" type=\"text\" name=\""
			+ TEAMFORMING_START
			+ "\" id=\""
			+ TEAMFORMING_START
			+ "\" + "
			+ "onClick =\"cal.select(document.forms['form_createTeamFormingSession']."
			+ TEAMFORMING_START
			+ ",'"
			+ TEAMFORMING_START
			+ "','dd/MM/yyyy')\""
			+ "onmouseover=\"ddrivetip('Please enter the start date for the Team-Forming session.')\""
			+ "onmouseout=\"hideddrivetip()\" READONLY tabindex=3> @ "
			+ "<select style=\"width: 70px;\" name=\""
			+ TEAMFORMING_STARTTIME
			+ "\" id=\""
			+ TEAMFORMING_STARTTIME
			+ "\" tabindex=4>"
			+ getTimeOptionString()
			+ "</select></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >End time:</td>"
			+ "<td><input style=\"width: 100px;\" type=\"text\" name=\""
			+ TEAMFORMING_DEADLINE
			+ "\" id=\""
			+ TEAMFORMING_DEADLINE
			+ "\" + "
			+ "onClick =\"cal.select(document.forms['form_createTeamFormingSession']."
			+ TEAMFORMING_DEADLINE
			+ ",'"
			+ TEAMFORMING_DEADLINE
			+ "','dd/MM/yyyy')\""
			+ "onmouseover=\"ddrivetip('Please enter deadline for the Team-Forming session.')\""
			+ "onmouseout=\"hideddrivetip()\" READONLY tabindex=5> @ "
			+ "<select style=\"width: 70px;\" name=\""
			+ TEAMFORMING_DEADLINETIME
			+ "\" id=\""
			+ TEAMFORMING_DEADLINETIME
			+ "\" tabindex=6>"
			+ getTimeOptionString()
			+ "</select></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Time zone:</td>"
			+ "<td>"
			+ "<select style=\"width: 100px;\" name=\""
			+ TEAMFORMING_TIMEZONE
			+ "\" id=\""
			+ TEAMFORMING_TIMEZONE
			+ "\" onmouseover=\"ddrivetip('Daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is daylight saving,<br /> you should choose UTC -7:00 and its corresponding timings.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=7>"
			+ getTimezoneOptionString()
			+ "</select>"
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Grace Period:</td>"
			+ "<td class=\"inputField\">"
			+ "<select style=\"width: 70px;\" name=\""
			+ TEAMFORMING_GRACEPERIOD
			+ "\" id=\""
			+ TEAMFORMING_GRACEPERIOD
			+ "\" onmouseover=\"ddrivetip('Please select the amount of time that the system will continue accepting <br />team changes after"
			+ " the specified deadline.')\" onmouseout=\"hideddrivetip()\" tabindex=7>"
			+ getGracePeriodOptionString()
			+ "</select></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Instructions to students:</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ TEAMFORMING_INSTRUCTIONS
			+ "\" id=\""
			+ TEAMFORMING_INSTRUCTIONS
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter instructions for your students, e.g. Max/Min team size etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>Max team size:\nMin team size:</textarea>"
			+ "</td>"
			+ "</tr>" 
			+ "<tr>"
			+ "<td class=\"attribute\" >Profile Template:</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ TEAMFORMING_PROFILETEMPLATE
			+ "\" id=\""
			+ TEAMFORMING_PROFILETEMPLATE
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter Profile template questions for your students, e.g. Strenths, Schedule, etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>Some past projects (if any):\nStrengths:</textarea>"
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td></td>"
			+ "<td colspan=\"3\">"
			+ "<input id='t_btnCreateTeamFormingSession' type=\"button\" class=\"button\" onclick=\"doCreateTeamFormingSession(this.form."
			+ COURSE_ID
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_START
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_STARTTIME
			+ ".value, this.form."
			+ TEAMFORMING_DEADLINE
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_DEADLINETIME
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_TIMEZONE
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_GRACEPERIOD
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_INSTRUCTIONS
			+ ".value, "
			+ "this.form."
			+ TEAMFORMING_PROFILETEMPLATE
			+ ".value);\" value=\"Create Session\" tabindex=2 />"
			+ "</td>" 
			+ "</tr>" + "</table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
	
	var now = new Date();

	var currentDate = convertDateToDDMMYYYY(now);

	var hours = convertDateToHHMM(now).substring(0, 2);
	var currentTime;

	if (hours.substring(0, 1) == "0") {
		currentTime = (parseInt(hours.substring(1, 2)) + 1) % 24;
	} else {
		currentTime = (parseInt(hours.substring(0, 2)) + 1) % 24;
	}
	
	var timeZone = -now.getTimezoneOffset() / 60;

	document.getElementById(TEAMFORMING_START).value = currentDate;
	document.getElementById(TEAMFORMING_STARTTIME).value = currentTime;
	document.getElementById(TEAMFORMING_TIMEZONE).value = timeZone;
}

function printEditTeams(courseID, start, deadline, gracePeriod, instructions, profileTemplate, activated, status)
{	
	var outputHeader = "<h1>MANAGE TEAM-FORMING SESSION</h1>";
	var startString = convertDateToDDMMYYYY(start);
	var deadlineString = convertDateToDDMMYYYY(deadline);
	
	isDisabled = (status == "CLOSED" || status == "OPEN") ? true : false;
	
	var outputForm = ""
			+ "<form name=\"form_manageteamformingsession\">"
			+ "<table class=\"addform round\">" + "<tr>"
			+ "<td class=\"attribute\" >Course ID:</td>"
			+ "<td>"+ courseID +"</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Start time:</td>"
			+ "<td><input style=\"width: 100px;\" type=\"text\" name=\""
			+ TEAMFORMING_START + "\" id=\"" + TEAMFORMING_START + "\""
			+ "onClick =\"cal.select(document.forms['form_manageteamformingsession']."
			+ TEAMFORMING_START + ",'" + TEAMFORMING_START + "','dd/MM/yyyy')\""
			+ "value=\"" + startString + "\" READONLY tabindex=1> @ "
			+ "<select style=\"width: 70px;\" name=\"" + TEAMFORMING_STARTTIME
			+ "\" id=\"" + TEAMFORMING_STARTTIME	+ "\" tabindex=4>"
			+ getTimeOptionString() + "</select></td></tr>" + "<tr>"
			+ "<td class=\"attribute\" >End time:</td>"
			+ "<td> <input style=\"width: 100px;\" type=\"text\" name=\""
			+ TEAMFORMING_DEADLINE + "\" id=\"" + TEAMFORMING_DEADLINE + "\" + "
			+ "onClick =\"cal.select(document.forms['form_manageteamformingsession']."
			+ TEAMFORMING_DEADLINE + ",'" + TEAMFORMING_DEADLINE
			+ "','dd/MM/yyyy')\"" + "value=\"" + deadlineString
			+ "\" READONLY tabindex=5> @ "
			+ "<select style=\"width: 70px;\" name=\""
			+ TEAMFORMING_DEADLINETIME + "\" id=\"" + TEAMFORMING_DEADLINETIME
			+ "\" tabindex=6>" + getTimeOptionString() + "</select></td>" + "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Grace Period:</td>"
			+ "<td class=\"inputField\">"
			+ "<select style=\"width: 70px;\" name=\"" + TEAMFORMING_GRACEPERIOD + "\" id=\""
			+ TEAMFORMING_GRACEPERIOD + "\" tabindex=5>"
			+ getGracePeriodOptionString() + "</select></td></tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Instructions:</td>"
			+ "<td colspan=\"3\"><textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ TEAMFORMING_INSTRUCTIONS + "\" id=\"" + TEAMFORMING_INSTRUCTIONS
			+ "\" tabindex=8>" + encodeChar(instructions) + "</textarea>"
			+ "</td></tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Profile Template:</td>"
			+ "<td colspan=\"3\"><textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ TEAMFORMING_PROFILETEMPLATE + "\" id=\"" + TEAMFORMING_PROFILETEMPLATE
			+ "\" tabindex=8>" + profileTemplate + "</textarea>"
			+ "</td></tr>"
			+ "<tr><td></td>"
			+ "<td colspan=\"3\">"
			+ "<input id='button_editteamformingsession' type=\"button\" class=\"button\""
			+ "value=\"Save Changes\" tabindex=2 />"
			+ "</td>" + "</tr>" + "</table>" + "</form>";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
	
	document.getElementById(TEAMFORMING_STARTTIME).disabled = isDisabled;
	document.getElementById(TEAMFORMING_START).disabled = isDisabled;
	
	document.getElementById('button_editteamformingsession').onclick = function() {
		var editStart = document.getElementById(TEAMFORMING_START).value;
		var editStartTime = document.getElementById(TEAMFORMING_STARTTIME).value;
		var editDeadline = document.getElementById(TEAMFORMING_DEADLINE).value;
		var editDeadlineTime = document.getElementById(TEAMFORMING_DEADLINETIME).value;
		var editGracePeriod = document.getElementById(TEAMFORMING_GRACEPERIOD).value;
		var editInstructions = document.getElementById(TEAMFORMING_INSTRUCTIONS).value;
		var editProfileTemplate = document.getElementById(TEAMFORMING_PROFILETEMPLATE).value;		

		doEditTeamFormingSession(courseID, editStart, editStartTime,
				editDeadline, editDeadlineTime, editGracePeriod,
				editInstructions, editProfileTemplate, activated, status);
	};
	
	
	document.getElementById(TEAMFORMING_GRACEPERIOD).value = gracePeriod;
	
	if (deadline.getMinutes() > 0) {
		document.getElementById(TEAMFORMING_DEADLINETIME).value = 24;
	} else {
		document.getElementById(TEAMFORMING_DEADLINETIME).value = deadline
		.getHours();
	}
	
	if (start.getMinutes() > 0) 
	{
		document.getElementById(TEAMFORMING_STARTTIME).value = 24;
	} else {
		document.getElementById(TEAMFORMING_STARTTIME).value = start.getHours();
	}
}

function printStudentProfileDetail(courseID, name, teamName, profileDetail){
	if(profileDetail=="null")
		profileDetail = "";
	var outputHeader = "<h1>STUDENT DETAIL</h1>";

	var output = "<table width=\"600\" class=\"detailform\">" + "<tr>"
			+ "<td>Course ID:</td>" + "<td>"
			+ courseID
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>Student's name:</td>"
			+ "<td>"
			+ name
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>Student's team:</td>"
			+ "<td>"
			+ encodeCharForPrint(teamName)
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>Student's detailed profile:</td>"
			+ "<td>"
			+ profileDetail
			+ "</td>"
			+ "</tr>";

	output = output
			+ "</table>"
			+ "<br /><br />"
			+ "<input type=\"button\" class=\"button\" id=\"button_back\" onClick=\"goToTeamFormingSession('"
			+ courseID + "')\" value=\"Back\" />"
			+ "<br /><br />";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = output;
}

function printStudentsWithoutTeam(output, students){
	output = output + "<br /><div><h1>STUDENTS WITHOUT ANY TEAM</h1></div>"
	output = output +  "<br /><table id=\"dataform\">" + "<tr>";
	output = output + "<th class='centeralign'>STUDENT</th>"
				+ "<th class='centeralign'>PROFILE</th>";
	output = output + "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	for(j=0; j<students.length; j++){
		if(students[j].profileDetail=="null")
			students[j].profileDetail = "";
		output = output
		+ "<tr><td class='centeralign' style=\"width: 150px;\">"
//		+ "<a class='t_team_view' id='withoutTeamStudent"+j+"'\" href=# "
//		+ "onmouseover=\"ddrivetip('View full profile of the student.')\""
//		+ "onmouseout=\"hideddrivetip()\">"
		+students[j].name+"</td><td style=\"width: 500px;\">"+students[j].profileDetail+"</td>";

		output = output + "<td class='centeralign' style=\"width: 150px;\">"
		+ "<a class='t_manageteam_view' onclick=\"manageTeamSession();\"\" href=# "
		+ "onmouseover=\"ddrivetip('Send an email to the student about his/her team.')\""
		+ "onmouseout=\"hideddrivetip()\">Notify</a>"
		+ "<span style=padding-left:20px /><a class='t_manageteam_view' id='allocateStudentTeam"+j+"' \" href=# "
		+ "onmouseover=\"ddrivetip('Click here to change the team of this student.')\""
		+ "onmouseout=\"hideddrivetip()\">Move to team</a>"
		+ "</td>" + "</tr>";
	}
	
	output = output + "</table><br />";
	return output;
}

function printTeams(output, teamName, students, position, courseID){
	output = output
		+ "<div class=\"result_team\">" 
		+ "<p>" 
		+ "<a class='t_team_view' id='viewTeamProfile"+position+"' style=color:white; \" href=# "
		+ "onmouseover=\"ddrivetip('Click here to see or edit the team profile.')\""
		+ "onmouseout=\"hideddrivetip()\">"+teamName+"</a>"
		+ "</p>"
		+ "<table id=\"dataform\">"
		+ "<tr>";					
	
	output = output + "<th class='centeralign'>STUDENT NAME</th>"
					+ "<th class='centeralign'>PROFILE</th>";
	output = output + "<th class=\"centeralign\">ACTION</th>" + "</tr>";
	
	for(j=0; j<students.length; j++){
		if(students[j].profileDetail=="null")
			students[j].profileDetail = "";
		output = output
		+ "<tr><td class='centeralign' style=\"width: 150px;\">"
//		+ "<a class='t_team_view' id='withTeamStudent"+position+j+"'\" href=# "
//		+ "onmouseover=\"ddrivetip('View full profile of the student.')\""
//		+ "onmouseout=\"hideddrivetip()\">"
		+students[j].name+"</td><td style=\"width: 500px;\">"+students[j].profileDetail+"</td>";

		output = output + "<td class='centeralign' style=\"width: 150px;\">"
		+ "<a class='t_manageteam_view' onclick=\"manageTeamSession();\"\" href=# "
		+ "onmouseover=\"ddrivetip('Send an email to the student about his/her team.')\""
		+ "onmouseout=\"hideddrivetip()\">Notify</a>"
		+ "<span style=padding-left:20px /><a class='t_manageteam_view' id='changeStudentTeam"+position+j+"' \" href=# "
		+ "onmouseover=\"ddrivetip('Click here to change the team of this student.')\""
		+ "onmouseout=\"hideddrivetip()\">Change Team</a>"
		+ "</td>" + "</tr>";
	}
	
	output = output + "</table><br /></div><br /><br />";
	return output;	
}

function printTeamDetail(courseID, courseName, teamName, teamDetail){
	if(teamDetail==null)
		var teamProfile="Please enter your team profile here.";
	else
		teamProfile = teamDetail.teamProfile;
	var outputHeader = "<h1>TEAM DETAIL</h1>";

	var outputForm = ""
			+ "<form method=\"post\" action=\"\" name=\"form_addevaluation\">"
			+ "<table class=\"addform round\">" + "<tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course ID:</td>"
			+ "<td id=\"courseId\">"+courseID+"</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course Name:</td>"
			+ "<td id=\"courseName\">"+courseName+"</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Team Name:</td>"
			+ "<td><input id=\"" + TEAM_NAME + "\" style=\"width: 100px;\" type=\"text\" value=\""+teamName+"\"/></td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Team Profile:</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ TEAM_PROFILE
			+ "\" id=\""
			+ TEAM_PROFILE
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter your team profile/proposal etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>"+teamProfile+"</textarea>"
			+ "</td>"
			+ "</tr>"
			+ "<tr><td></td><td>"
			+ "<input id='button_back' type=\"button\" class=\"button\" onClick=\"goToTeamFormingSession('"
			+ courseID + "')\" value=\"Back\" tabindex=2 />"
			+ "<input id='button_saveTeamProfile' type=\"button\" class=\"button\""
			+ "value=\"Save\" tabindex=2 />"
			+ "</td></tr></table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
	
	document.getElementById('button_saveTeamProfile').onclick = function() {
		var newTeamName = document.getElementById(TEAM_NAME).value;
		var newTeamProfile = document.getElementById(TEAM_PROFILE).value;
		editTeamProfile(courseID, courseName, teamName, newTeamName, newTeamProfile);
	};
}

/*
 * helper: print team forming session actions: 1. Manage 2. Delete 3. Remind 4. View Log
 */
function printTeamFormingSessionActions(teamFormingSessionList, position) {

	var output = "";
	// if link is disabled, insert this line to reset style and onclick:
	var disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";

	var status = teamFormingSessionList[position].status;
	
	// action flag:
	var hasRemind = false;
	if (status == 'OPEN') 
		hasRemind = true;
	
	// 1.MANAGE:
	output = output + "<a class='t_team_manage' name=\"manageTeamFormingSession" + position
			+ "\" id=\"manageTeamFormingSession" + position + "\" href=# "
			+ "onmouseover=\"ddrivetip('Manage the teams or Edit the team-forming session details')\""
			+ "onmouseout=\"hideddrivetip()\""
			+ ">Manage</a>";
	// 2.DELETE:
	output = output
			+ "<a class='t_team_delete' name=\"deleteTeamFormingSession" + position
			+ "\" id=\"deleteTeamFormingSession" + position + "\" href=\"javascript:toggleDeleteTeamFormingSessionConfirmation('"
			+ teamFormingSessionList[position].courseID + "','"
			+ convertDateToDDMMYYYY(teamFormingSessionList[position].deadline) + "','"
			+ convertDateToHHMM(teamFormingSessionList[position].deadline) + "');hideddrivetip();\""
			+ "onmouseover=\"ddrivetip('Delete this team forming session')\""
			+ "onmouseout=\"hideddrivetip()\">Delete</a>";
	// 3.REMIND:
	output = output
			+ "<a class='t_team_remind' name=\"remindTeamFormingSession"
			+ position
			+ "\" id=\"remindTeamFormingSession"
			+ position
			+ "\" href=\"javascript:toggleRemindStudentsOfTeamForming('"
			+ teamFormingSessionList[position].courseID
			+ "');hideddrivetip();\""
			+ "onmouseover=\"ddrivetip('Send e-mails to remind the students to form the team before the specified deadline')\""
			+ "onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : disabled)
			+ ">Remind</a>";
	// 4.VIEW LOG:
	output = output
			+ "<a class='t_team_viewLog' name=\"viewLogTeamFormingSession"
			+ position + "\" id=\"viewLogTeamFormingSession" + position
			+ "\" href=# "
			+ "onmouseover=\"ddrivetip('View the time log of the actions of every students')\""
			+ "onmouseout=\"hideddrivetip()\">View Log</a>"; 
	return output;
}

function printTeamFormingSessionList(teamFormingSessionList) {
	var output;

	output = "<br /><br />" + "<table id=\"dataform\">"
			+ "<tr>"
			+ "<th class=\"centeralign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\">COURSE ID</input></th>"
			+ "<th class=\"centeralign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\">DEADLINE</input></th>"
			+ "<th class=\"centeralign\">STATUS</th>"
			+ "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	// Fix for empty evaluation list
	if (teamFormingSessionList.length == 0) {
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_DELETED);

		output = output + "<tr>" + "<td></td>" + "<td></td>" + "<td></td>"
				+ "<td></td>" + "</tr>";
	}

	var counter = 0;
	var teamFormingSessionStatus;
	for (loop = 0; loop < teamFormingSessionList.length; loop++) {
		if (teamFormingSessionList[loop].status == "AWAITING")
			teamFormingSessionStatus = "<td class=\"t_team_status centeralign\"><span onmouseover=\"ddrivetip('The team forming session is created but has not yet started')\" onmouseout=\"hideddrivetip()\">"
					+ teamFormingSessionList[loop].status + "</span></td>";
		if (teamFormingSessionList[loop].status == "OPEN")
			teamFormingSessionStatus = "<td class=\"t_team_status centeralign\"><span onmouseover=\"ddrivetip('The team forming session has started and students can make teams until the closing time')\" onmouseout=\"hideddrivetip()\">"
					+ teamFormingSessionList[loop].status + "</span></td>";
		if (teamFormingSessionList[loop].status == "CLOSED")
			teamFormingSessionStatus = "<td class=\"t_team_status centeralign\"><span onmouseover=\"ddrivetip('The team forming session has finished but the team details are not yet sent to the students')\" onmouseout=\"hideddrivetip()\">"
					+ teamFormingSessionList[loop].status + "</span></td>";

		output = output + "<tr id=\"teamFormingSession"+loop+"\">" + "<td class='t_team_coursecode centeralign'>"
				+ encodeChar(teamFormingSessionList[loop].courseID) + "</td>"
				+ "<td class='t_team_deadline centeralign'>"
				+ convertDateToDDMMYYYY(teamFormingSessionList[loop].deadline) + " "
				+ convertDateToHHMM(teamFormingSessionList[loop].deadline) + "H</td>"
				+ teamFormingSessionStatus;
	
		// display actions:
		output = output + "<td class=\"centeralign\">"
				+ printTeamFormingSessionActions(teamFormingSessionList, loop);
		counter++;
	}

	output = output + "</td></tr></table>" + "<br /><br />";
	document.getElementById(DIV_EVALUATION_TABLE).innerHTML = output;

	/*
	// catch actions:
	document.getElementById('button_sortcourseid').onclick = function() {
		toggleSortEvaluationsByCourseID(evaluationList)
	};
	document.getElementById('button_sortname').onclick = function() {
		toggleSortEvaluationsByName(evaluationList)
	};*/

	for (loop = 0; loop < teamFormingSessionList.length; loop++) {
		if (document.getElementById('manageTeamFormingSession' + loop) != null
				&& document.getElementById('manageTeamFormingSession' + loop).onclick == null) {
			document.getElementById('manageTeamFormingSession' + loop).onclick = function() {
				hideddrivetip();
				displayManageTeamFormingSession(teamFormingSessionList, this.id.substring(24,
						this.id.length));
			};
		}
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function remindStudentsOfTeamForming(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING + "&" +
				COURSE_ID + "=" + encodeURIComponent(courseID)); 
		
		handleRemindStudents();
	}
}

function toggleDeleteTeamFormingSessionConfirmation(courseID, deadlineDate, deadlineTime){
	var s = confirm("Are you sure you want to delete the team forming session?");
	if (s == true) {
		doDeleteTeamFormingSession(courseID, deadlineDate, deadlineTime);
	} else {
		clearStatusMessage();
	}
	document.getElementById(DIV_TEAMFORMINGSESSION_MANAGEMENT).scrollIntoView(true);
}

function toggleRemindStudentsOfTeamForming(courseID) {
	var s = confirm("Send e-mails to remind students who have not formed teams?");
	if (s == true) {
		doRemindStudentsOfTeamForming(courseID);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function updateTeamNameInStudentTable(courseId, teamName, newTeamName){
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITSTUDENTTEAM + 
				"&" + "courseId" + "=" + encodeURIComponent(courseId) +
				"&" + "oldteamname" + "=" + encodeURIComponent(teamName) +
				"&" + TEAM_NAME + "=" + encodeURIComponent(newTeamName));
	}	
}

//DynamicDrive JS mouse-hover
document.onmousemove = positiontip;