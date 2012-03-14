var OPERATION_ADDSTUDENTTOTEAM = "addstudenttoteam";
var OPERATION_COORDINATOR_GETSTUDENTSOFCOURSETEAM = "coordinator_getstudentsofcourseteam";
var OPERATION_COORDINATOR_GETSTUDENTSWITHOUTTEAM = "coordinator_getstudentswithoutteam";
var OPERATION_COORDINATOR_GETTEAMDETAIL = "coordinator_getteamdetail";
var OPERATION_COORDINATOR_GETTEAMFORMINGSESSION = "coordinator_getteamformingsession";
var OPERATION_COORDINATOR_GETTEAMSOFCOURSE = "coordinator_getteamsofcourse";
var OPERATION_CREATETEAMWITHSTUDENT = "createteamwithstudent";
var OPERATION_GETCURRENTUSER = "getcurrentuser";
var OPERATION_GETSTUDENTTEAMNAME = "getstudentteamname";
var OPERATION_EDITSTUDENTTEAM = "editstudentteam";
var OPERATION_JOINTEAM = "jointeam";
var OPERATION_LEAVETEAM = "leaveteam";
var OPERATION_STUDENT_EDITPROFILE = "student_editprofile";
var OPERATION_STUDENT_EDITTEAMPROFILE = "student_editteamprofile";
var OPERATION_STUDENT_GETCOURSESTUDENTDETAIL = "student_getcoursestudentdetail";

var MSG_STUDENTADDEDTOTEAM = "student is added to the team";
var MSG_STUDENTJOINTEAM = "student has joined the team";
var MSG_TEAMCREATEDWITHSTUDENT = "team has been created with the student";
var MSG_TEAMPROFILE_EXISTS = "team profile exists";

var STUDENT_PROFILE_DETAIL = "studentprofiledetail";
var STUDENT_PROFILE_SUMMARY = "studentprofilesummary";
var STUDENT_TEAM_NAME = "studentteamname";

//PARAMETERS
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
var TEAM_PROFILE = "teamProfile";

var DISPLAY_STUDENTPROFILE_SAVED = "Your profile has been saved.";
var DISPLAY_STUDENTADDEDTOTEAM = "Student has been added to your team.";
var DISPLAY_STUDENTJOINEDTEAM = "You have joined a team.";
var DISPLAY_STUDENTLEFTTEAM = "You have left the team.";
var DISPLAY_TEAMCREATEDWITHSTUDENT = "A new team has been created with the student.";
var DISPLAY_TEAMPROFILE_EXISTS = "<font color=\"#F00\">Same team profile exists already.</font>";
var DISPLAY_TEAMPROFILE_SAVED = "The team profile has been saved.";

function addStudentToTeam(courseID, currentStudentTeamName, studentToAdd)
{
	var result;
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_ADDSTUDENTTOTEAM + 
				"&" + "courseId" + "=" + encodeURIComponent(courseID) +
				"&" + "teamName" + "=" + encodeURIComponent(currentStudentTeamName) +
				"&" + "email" + "=" + encodeURIComponent(studentToAdd.email));
		result = handleAddStudentToTeam();
	}
	
	if (result==1)
		alert(DISPLAY_SERVERERROR);
	else
	{
		displayStudentViewTeams(courseID);
		setStatusMessage(DISPLAY_STUDENTADDEDTOTEAM);
	}		
}

function createTeamWithStudent(courseID, courseName, studentToAdd, currentStudentEmail, currentStudentNickname)
{
	var result;
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_CREATETEAMWITHSTUDENT + 
				"&" + "courseId" + "=" + encodeURIComponent(courseID) +
				"&" + "courseName" + "=" + encodeURIComponent(courseName) +
				"&" + "studentToAddEmail" + "=" + encodeURIComponent(studentToAdd.email) +
				"&" + "currentStudentEmail" + "=" + encodeURIComponent(currentStudentEmail)+
				"&" + "currentStudentNickname" + "=" + encodeURIComponent(currentStudentNickname));
		result = handleCreateTeamWithStudent();
	}
	
	if (result==1)
		alert(DISPLAY_SERVERERROR);
	else
	{
		displayStudentViewTeams(courseID);
		setStatusMessage(DISPLAY_TEAMCREATEDWITHSTUDENT);
	}
}

function displayStudentViewTeams(courseID){
	clearAllDisplay();
	
	var studentDetail = getCourseStudentDetail(courseID);
	var teamFormingSession = getTeamFormingSession(courseID);		
	printCourseStudentDetails(courseID, studentDetail, teamFormingSession);
	
	var active = 1;
	if(teamFormingSession.activated==false)
		active = 0;
	
	var currentStudent = getCurrentUser();	
	var currentStudentTeamName = getStudentTeamName(courseID, currentStudent.email);
	currentStudentTeamName = currentStudentTeamName.replace(/^\s*|\s*$/,"");		
	
	//printing teams that are formed
	var teams = getTeamsOfCourse(courseID);
	var output = displayStudentTeams(courseID, teams, currentStudentTeamName, active);	
	
	//printing students without team
	var studentsWithoutTeam = getStudentsWithoutTeam(courseID);
	if(studentsWithoutTeam.length!=0)
		output = printStudentsWithoutTeam(output, studentsWithoutTeam, currentStudent.email, active);
	
	if(teams.length!=0 || studentsWithoutTeam.length!=0){
		output = output
		+ "<br /><br />"
		+ "<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Update\" /><br /><br />";
	}
	document.getElementById(DIV_COURSE_TABLE).innerHTML = output;
	
	//for team profiles
	for (pos=0; pos<teams.length; pos++) {
		if (document.getElementById('viewTeamProfile' + pos) != null 
				&& document.getElementById('viewTeamProfile' + pos).onclick == null) {
			document.getElementById('viewTeamProfile' + pos).onclick = function() {
				var current = this.id.substring(15,this.id.length);
				teams[current].teamName = teams[current].teamName.replace(/^\s*|\s*$/,"");
				if(teams[current].teamName!=""){
					var teamName = document.getElementById('viewTeamProfile'+current).innerHTML;
					manageTeamProfile(courseID, teamName, currentStudent.email);
				}
			};
		}
	}
	
	//for adding students without team to some team
	for (pos=0; pos<studentsWithoutTeam.length; pos++) {
		if (document.getElementById('buttonAdd' + pos) != null 
				&& document.getElementById('buttonAdd' + pos).onclick == null) {
			document.getElementById('buttonAdd' + pos).onclick = function() {
				var current = this.id.substring(9,this.id.length);				
				//create a new team with these two
				if(currentStudentTeamName=="")
					createTeamWithStudent(courseID, studentDetail.courseName, studentsWithoutTeam[current], currentStudent.email, 
							currentStudent.nickname);			
				//add to the current Student's team
				else
					addStudentToTeam(courseID, currentStudentTeamName, studentsWithoutTeam[current]);			
			};
		}
	}
	
	//for joining a team
	for (pos=0; pos<teams.length; pos++) {
		if (document.getElementById('buttonJoin' + pos) != null 
				&& document.getElementById('buttonJoin' + pos).onclick == null) {
			document.getElementById('buttonJoin' + pos).onclick = function() {
				var current = this.id.substring(10,this.id.length);
				joinTeam(courseID, teams[current].teamName, currentStudent.email);
			};
		}
	}
	
	//for leaving a team
	for (pos=0; pos<teams.length; pos++) {
		if (document.getElementById('buttonLeave' + pos) != null 
				&& document.getElementById('buttonLeave' + pos).onclick == null) {
			document.getElementById('buttonLeave' + pos).onclick = function() {
				var current = this.id.substring(11,this.id.length);
				leaveTeam(courseID, teams[current].teamName, currentStudent.email);
			};
		}
	}
	
	//for with team student full profile
//	for (pos=0; pos<teams.length; pos++) {
//		var students = getStudentsOfCourseTeam(courseID, teams[pos].teamName);
//		for(j=0; j<students.length; j++){
//			if (document.getElementById('withTeamStudent' + pos + j) != null 
//					&& document.getElementById('withTeamStudent' + pos + j).onclick == null) {
//				document.getElementById('withTeamStudent' + pos + j).onclick = function() {
//					var teamIndex = this.id.substring(this.id.length-2,this.id.length-1);
//					var studentIndex = this.id.substring(this.id.length-1,this.id.length);
//					students = getStudentsOfCourseTeam(courseID, teams[teamIndex].teamName);
//					displayStudentFullProfile(courseID, teams[teamIndex].teamName, students[studentIndex].email);
//				};
//			}
//		}
//	}
	
	//for without team student full profile
//	for (pos=0; pos<studentsWithoutTeam.length; pos++) {
//		if (document.getElementById('withoutTeamStudent' + pos) != null 
//				&& document.getElementById('withoutTeamStudent' + pos).onclick == null) {
//			document.getElementById('withoutTeamStudent' + pos).onclick = function() {
//				var index = this.id.substring(this.id.length-1,this.id.length);
//				displayStudentFullProfile(courseID, "", studentsWithoutTeam[index].email);
//			};
//		}
//	}
}

function displayStudentFullProfile(courseID, teamName, studentEmail)
{
	var student = getStudent(courseID, studentEmail);
	clearAllDisplay();
	printStudentProfileDetail(courseID, student.name, student.teamName, student.profileDetail);
}

function displayStudentTeams(courseID, teams, currentStudentTeamName, active)
{
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
			output = printStudentTeams(output, teams[i].teamName, students, i, currentStudentTeamName, active);
		}
	}
	return output;
}

function editTeamProfile(courseId, courseName,  teamName, newTeamName, newTeamProfile){
	setStatusMessage(DISPLAY_LOADING);
	
	if(teamName == "" || teamProfile == "")
		setStatusMessage(DISPLAY_FIELDS_EMPTY);	
	else
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_EDITTEAMPROFILE + 
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
		displayStudentViewTeams(courseId);
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

function getCourseStudentDetail(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	var results;
	
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETCOURSESTUDENTDETAIL + "&" 
				+ COURSE_ID + "=" + encodeURIComponent(courseID)); 
		
		results = handleGetCourseStudentDetail();
	}
	
	if(results == 1)
		alert(DISPLAY_SERVERERROR);
	
	return results;
}

function getCurrentUser()
{
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_GETCURRENTUSER);
		
		return handleGetCurrentUser();
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

function getStudentTeamName(courseID, email)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_GETSTUDENTTEAMNAME + 
					"&" + "courseId" + "=" + encodeURIComponent(courseID)+ 
					"&" + "email" + "=" + encodeURIComponent(email));
		return handleGetStudentTeamName();
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

function getTeamFormingSession(courseID)
{
	var results;
	if(xmlhttp)
	{		
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETTEAMFORMINGSESSION + "&" 
				+ COURSE_ID + "=" + encodeURIComponent(courseID)); 
		results = handleGetTeamFormingSession();
	}
	
	if(results == 1)
		alert(DISPLAY_SERVERERROR);
	
	return results;
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
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function handleAddStudentToTeam()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;			
			if(message == MSG_STUDENTADDEDTOTEAM)
				return 0;
		}
	}
	else
		return 1;
}

function handleCreateTeamWithStudent()
{	
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;			
			if(message == MSG_TEAMCREATEDWITHSTUDENT)
				return 0;
		}
	}
	else
		return 1;
}

function handleEditStudentProfile()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];		
		if(status != null)
			return 0;
	}
	
	else
		return 1;
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
				return 4;			
			else
				return 0;
		}
	}
	
	else
		return 1;
}

/*
 * Returns
 * 
 * course: successful
 * 1: server error
 *
 */
function handleGetCourseStudentDetail()
{
	if (xmlhttp.status == 200) 
	{
		clearStatusMessage();
		
		var course = xmlhttp.responseXML.getElementsByTagName("coursedetails")[0];
		
		var courseID = course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
		var courseName = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
		var coordinatorName = course.getElementsByTagName(COURSE_COORDINATORNAME)[0].firstChild.nodeValue;
		var studentTeamName = course.getElementsByTagName(STUDENT_TEAM_NAME)[0].firstChild.nodeValue;
		var studentName = course.getElementsByTagName("studentname")[0].firstChild.nodeValue;
		var studentEmail = course.getElementsByTagName("studentemail")[0].firstChild.nodeValue;
		
		var teammates = course.getElementsByTagName(STUDENT_TEAMMATES)[0];
		var teammateList = new Array();
		
		var teammatesChildNodesLength = teammates.childNodes.length;
		for(var x = 0; x < teammatesChildNodesLength; x++)
		{
			teammateList[x] = teammates.getElementsByTagName(STUDENT_TEAMMATE)[x].firstChild.nodeValue;
		}
		var studentProfileSummary = course.getElementsByTagName(STUDENT_PROFILE_SUMMARY)[0].firstChild.nodeValue;
		var studentProfileDetail = course.getElementsByTagName(STUDENT_PROFILE_DETAIL)[0].firstChild.nodeValue;
		
		var course = {courseID:courseID, courseName:courseName, coordinatorName:coordinatorName, 
				studentTeamName:studentTeamName, studentName:studentName, studentEmail:studentEmail, 
				teammateList:teammateList, studentProfileSummary:studentProfileSummary, 
				studentProfileDetail:studentProfileDetail};
		
		return course;
			
	}
	
	else
		return 1;
}

function handleGetCurrentUser()
{
	if (xmlhttp.status == 200) 
	{
		var user = xmlhttp.responseXML.getElementsByTagName("currentuser")[0];
		var email;
		var nickname;
		var currentUser;
		
		if(user.firstChild!=null){
			email = user.getElementsByTagName("email")[0].firstChild.nodeValue;
			nickname = user.getElementsByTagName("nickname")[0].firstChild.nodeValue;
			
			currentUser = { email:email, nickname:nickname};
		}
		return currentUser;
	}
	else		
		return 1;
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
		var email;
		var profileSummary;
		var profileDetail;
		
		if(students != null) {
			for(loop = 0; loop < students.childNodes.length; loop++)
			{
				student = students.childNodes[loop];				
				ID = student.getElementsByTagName("studentid")[0].firstChild.nodeValue;
				name = student.getElementsByTagName("studentname")[0].firstChild.nodeValue;
				email = student.getElementsByTagName("studentemail")[0].firstChild.nodeValue;
				profileSummary = student.getElementsByTagName("studentprofilesummary")[0].firstChild.nodeValue;
				profileDetail = student.getElementsByTagName("studentprofiledetail")[0].firstChild.nodeValue;
				
				studentList[loop] = { ID:ID, name:name,	email:email, profileSummary:profileSummary, 
						profileDetail:profileDetail};
			}
		}		
		return studentList;
	}else {		
		return 1;
	}
}

function handleGetStudentTeamName()
{
	if (xmlhttp.status == 200) 
	{
		var student = xmlhttp.responseXML.getElementsByTagName("student")[0];
		var teamName;
		
		if(student.firstChild!=null){
			teamName = student.getElementsByTagName("teamName")[0].firstChild.nodeValue;
		}
		return teamName;
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
				name = student.getElementsByTagName("studentname")[0].firstChild.nodeValue;
				email = student.getElementsByTagName("studentemail")[0].firstChild.nodeValue;
				profileSummary = student.getElementsByTagName("studentprofilesummary")[0].firstChild.nodeValue;
				profileDetail = student.getElementsByTagName("studentprofiledetail")[0].firstChild.nodeValue;
				
				studentList[loop] = { ID:ID, name:name, email:email, 
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

function handleGetTeamFormingSession()
{
	if (xmlhttp.status == 200) 
	{
		var teamFormingSession = xmlhttp.responseXML.getElementsByTagName("teamformingsession")[0];
		var now;
		
		var teamFormingSessionObject;
		var courseID;
		var profileTemplate;
		var instructions;
		var start;
		var deadline;
		var gracePeriod;
		var status;
		var activated;
		
		if(teamFormingSession != null) {				
				courseID = teamFormingSession.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				start = new Date(teamFormingSession.getElementsByTagName(TEAMFORMING_START)[0].firstChild.nodeValue);
				deadline = new Date(teamFormingSession.getElementsByTagName(TEAMFORMING_DEADLINE)[0].firstChild.nodeValue);
				timeZone = parseFloat(teamFormingSession.getElementsByTagName(TEAMFORMING_TIMEZONE)[0].firstChild.nodeValue);
				gracePeriod = parseInt(teamFormingSession.getElementsByTagName(TEAMFORMING_GRACEPERIOD)[0].firstChild.nodeValue);
				activated = (teamFormingSession.getElementsByTagName(TEAMFORMING_ACTIVATED)[0].firstChild.nodeValue.toLowerCase() == "true");
				instructions = teamFormingSession.getElementsByTagName(TEAMFORMING_INSTRUCTIONS)[0].firstChild.nodeValue;
				profileTemplate = teamFormingSession.getElementsByTagName(TEAMFORMING_PROFILETEMPLATE)[0].firstChild.nodeValue;

				now = getDateWithTimeZoneOffset(timeZone);

				// Check if team forming session should be open or closed
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
				
				teamFormingSessionObject = { courseID:courseID, start:start, deadline:deadline, 
						timeZone:timeZone, gracePeriod:gracePeriod, instructions:instructions,
						 activated:activated, profileTemplate:profileTemplate, status:status};
		}		
		return teamFormingSessionObject;
	}
	
	else
		return 1;
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

function joinTeam(courseID, teamName, email)
{
	var result;
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_JOINTEAM + 
				"&" + "courseId" + "=" + encodeURIComponent(courseID) +
				"&" + "teamName" + "=" + encodeURIComponent(teamName) +
				"&" + "email" + "=" + encodeURIComponent(email));
		result = handleJoinTeam();
	}
	
	if (result==1)
		alert(DISPLAY_SERVERERROR);
	else
	{
		displayStudentViewTeams(courseID);
		setStatusMessage(DISPLAY_STUDENTJOINEDTEAM);
	}	
}

function leaveTeam(courseID, teamName, email)
{
	var result;
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_LEAVETEAM + 
				"&" + "courseId" + "=" + encodeURIComponent(courseID) +
				"&" + "teamName" + "=" + encodeURIComponent("") +
				"&" + "email" + "=" + encodeURIComponent(email));
		result = handleJoinTeam();
	}
	
	if (result==1)
		alert(DISPLAY_SERVERERROR);
	else
	{
		displayStudentViewTeams(courseID);
		setStatusMessage(DISPLAY_STUDENTLEFTTEAM);
	}	
}

function manageTeamProfile(courseID, teamName, currentStudentEmail){
	clearAllDisplay();
	var teamDetail = getTeamDetail(courseID, teamName);
	var courseInfo = getCourse(courseID);
	
	var editable = 0;
	var students = getStudentsOfCourseTeam(courseID, teamName);
	for(loop = 0; loop<students.length; loop++)
	{
		if(students[loop].email == currentStudentEmail)
			editable = 1;
	}
	printTeamDetail(courseID, courseInfo.courseName, teamName, teamDetail, editable);
}

function printCourseStudentDetails(courseID, studentDetail, teamFormingSession){
	if(studentDetail.studentProfileSummary == "null")
		studentDetail.studentProfileSummary = "Please enter your profile summary here.";
	if(studentDetail.studentProfileDetail == "null")
		studentDetail.studentProfileDetail = "Please enter your detailed profile here.";
	
	var deadlineString = convertDateToDDMMYYYY(teamFormingSession.deadline);
	var deadlineTimeString = convertDateToHHMM(teamFormingSession.deadline);
	
	var outputHeader = "<h1>FORM TEAMS FOR COURSE "+courseID+"</h1>";

	var outputForm = ""
			+ "<form method=\"post\" action=\"\" name=\"form_studentDetail\">"
			+ "<table class=\"addform round\">" + "<tr/>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course ID:</td>" + "<td>"
			+ studentDetail.courseID
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Your team:</td>"
			+ "<td>"
			+ encodeCharForPrint(studentDetail.studentTeamName)
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course name:</td>"
			+ "<td>"
			+ studentDetail.courseName
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Your name:</td>"
			+ "<td>"
			+ studentDetail.studentName
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Coordinator name:</td>"
			+ "<td>"
			+ studentDetail.coordinatorName
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Your e-mail:</td>"
			+ "<td>"
			+ studentDetail.studentEmail
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Team-Forming Instructions:</td>"
			+ "<td>"+teamFormingSession.instructions+"</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Deadline:</td>"
			+ "<td>"+deadlineString+" "+deadlineTimeString+" hours</td>"
			+ "</tr>" 
//			+ "<tr>"
//			+ "<td class=\"attribute\" >Your Profile Summary:</td>"
//			+ "<td colspan=\"3\">"
//			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
//			+ STUDENT_PROFILE_SUMMARY
//			+ "\" id=\""
//			+ STUDENT_PROFILE_SUMMARY
//			+ "\""
//			+ "onmouseover=\"ddrivetip('Please enter Profile template questions for your students, e.g. Strenths, Schedule, etc.')\""
//			+ "onmouseout=\"hideddrivetip()\" tabindex=8>"+studentDetail.studentProfileSummary+"</textarea>"
//			+ "</td>"
//			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Your Detailed Profile:</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"6\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ STUDENT_PROFILE_DETAIL
			+ "\" id=\""
			+ STUDENT_PROFILE_DETAIL
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter Profile template questions for your students, e.g. Strenths, Schedule, etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>"+studentDetail.studentProfileDetail+"</textarea>"
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td></td>"
			+ "<td colspan=\"3\">"
			+ "<input id='button_savestudentprofile' type=\"button\" class=\"button\""
			+ "value=\"Save\" tabindex=2 />"
			+ "</td>" + "</tr>"
			+ "</table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
	
	document.getElementById('button_savestudentprofile').onclick = function() {
		//var profileSummary = document.getElementById(STUDENT_PROFILE_SUMMARY).value;
		var profileSummary = "Not needed!";
		var profileDetail = document.getElementById(STUDENT_PROFILE_DETAIL).value;
		saveCourseStudentProfile(studentDetail.studentEmail, courseID, profileSummary, profileDetail);
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
			+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayStudentViewTeams('"
			+ courseID+"')\" value=\"Back\" />"
			+ "<br /><br />";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = output;
}

function printStudentTeams(output, teamName, students, position, currentStudentTeamName, active){
	if(teamName == currentStudentTeamName)
		output = output 
		+ "<div class=\"current_team\">";
	else
		output = output 
		+ "<div class=\"result_team\">";
	
	output = output 
		+ "<p align=\"left\">" 
		+ "<a class='t_team_view' id='viewTeamProfile"+position+"' style=color:white; \" href=# "
		+ "onmouseover=\"ddrivetip('Click here to see or edit the team profile.')\""
		+ "onmouseout=\"hideddrivetip()\">"+teamName+"</a>";			
	
	if(active == 1){
		if(currentStudentTeamName=="")
			output = output 
			+ " "
			+ "<input id='buttonJoin"+position+"' type=\"button\" class=\"button\""
			+ "value=\"Join\" tabindex=2 />";
		else if (teamName == currentStudentTeamName)
			output = output
			+ " "
			+ "<input id='buttonLeave"+position+"' type=\"button\" class=\"button\""
			+ "value=\"Leave\" tabindex=2 />";
		else
			output = output
			+ " "
			+ "<input id='buttonJoin"+position+"' type=\"button\" class=\"button\""
			+ "value=\"Join\" tabindex=2 />";
	}
	
	output = output 
		+ "</p>"
		+ "<table id=\"dataform\">"
		+ "<tr>";
	
	output = output + "<th class='centeralign'>STUDENT NAME</th>"
					+ "<th class='centeralign'>PROFILE</th>"
					+ "<th class='centeralign'>EMAIL</th>"
					+ "</tr>";
	
	for(j=0; j<students.length; j++){
		if(students[j].profileDetail=="null")
			students[j].profileDetail = "";
		output = output
		+ "<tr><td class='centeralign' style=\"width: 150px;\">"
//		+ "<a class='t_team_view' id='withTeamStudent"+position+j+"'\" href=# "
//		+ "onmouseover=\"ddrivetip('View full profile of the student.')\""
//		+ "onmouseout=\"hideddrivetip()\">"
		+students[j].name+"</td><td style=\"width: 500px;\">"+students[j].profileDetail+"</td><td style=\"width: 200px;\">"+students[j].email+"</td></tr>";
	}
		
	output = output + "</table><br /></div><br /><br />";
	return output;
}

function printStudentsWithoutTeam(output, students, currentStudentEmail, active){
	output = output + "<br /><div><h1>STUDENTS WITHOUT ANY TEAM</h1></div>"
	+ "<br /><table id=\"dataform\">" + "<tr>";
	output = output + "<th class='centeralign'>STUDENT NAME</th>"
	+ "<th class='centeralign'>PROFILE</th>"
	+ "<th class='centeralign'>EMAIL</th>";
	
	if(active==1)
		output = output + "<th class=\"centeralign\">ACTION(S)</th>";
	
	output = output + "</tr>";

	for(j=0; j<students.length; j++){
		if(students[j].profileDetail=="null")
			students[j].profileDetail = "";
		output = output
		+ "<tr><td class='centeralign' style=\"width: 150px;\">"
		+students[j].name+"</td><td style=\"width: 500px;\">"+students[j].profileDetail+"</td><td style=\"width: 200px;\">"+students[j].email+"</td>";
		
		if(active==1){
			output = output + "<td class='centeralign' style=\"width: 100px;\">";
			if(currentStudentEmail!=students[j].email)
				output = output
				+ "<input id='buttonAdd"+j+"' type=\"button\" class=\"button\""
				+ "value=\"Add to my team\" tabindex=2 />"
				+ "</td>";
		}
		
		output = output	+ "</tr>";		
	}

	output = output + "</table><br />";
	return output;
}

function printTeamDetail(courseID, courseName, teamName, teamDetail, editable){
	if(teamDetail==null){
		if(editable!=1)
			var teamProfile="";
		else
			var teamProfile="Please enter your team profile here.";
	}
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
			+ "</tr>";
	
	if(editable==1)
		outputForm = outputForm
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
		+ "<tr>"
		+ "<td></td>"
		+ "<td colspan=\"3\">"
		+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayStudentViewTeams('"
		+ courseID+"')\" value=\"Back\" />"
		+ "<input id='button_saveTeamProfile' type=\"button\" class=\"button\""
		+ "value=\"Save\" tabindex=2 />"
		+ "</td>" + "</tr>" + "</table>" + "</form>";
	
	else
		outputForm = outputForm
		+ "<tr>"
		+ "<td class=\"attribute\" >Team Name:</td>"
		+ "<td id=\"" + TEAM_NAME + "\">"+teamName+"</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td class=\"attribute\" >Team Profile:</td>"
		+ "<td colspan=\"3\" id=\"" + TEAM_PROFILE + "\">"
		+ teamProfile
		+ "</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td></td>"
		+ "<td colspan=\"3\">"
		+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayStudentViewTeams('"
		+ courseID+"')\" value=\"Back\" />"
		+ "</td>" + "</tr>"
		+ "</table>" + "</form>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
	
	document.getElementById('button_saveTeamProfile').onclick = function() {
		var newTeamName = document.getElementById(TEAM_NAME).value;
		var newTeamProfile = document.getElementById(TEAM_PROFILE).value;
		editTeamProfile(courseID, courseName, teamName, newTeamName, newTeamProfile);
	};
}

function saveCourseStudentProfile(studentEmail, courseID, profileSummary, profileDetail)
{
	setStatusMessage(DISPLAY_LOADING);
	
	if(profileSummary == "" || profileDetail == "")
		setStatusMessage(DISPLAY_FIELDS_EMPTY);	
	else(xmlhttp)
	{	
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_EDITPROFILE + 
				"&" + "courseId" + "=" + encodeURIComponent(courseID) +
				"&" + "studentEmail" + "=" + encodeURIComponent(studentEmail) +
				"&" + "profileSummary" + "=" + encodeURIComponent(profileSummary) +
				"&" + "profileDetail" + "=" + encodeURIComponent(profileDetail));
		
		var results = handleEditStudentProfile();
		clearStatusMessage();
	}
	
	if(results == 0){
		displayStudentViewTeams(courseID);
		setStatusMessage(DISPLAY_STUDENTPROFILE_SAVED);
	}
	
	else if(results == 1)
		alert(DISPLAY_SERVERERROR);
}

function updateTeamNameInStudentTable(courseId, teamName, newTeamName){
	if(xmlhttp){
		xmlhttp.open("POST","/teamforming",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_EDITSTUDENTTEAM + 
				"&" + "courseId" + "=" + encodeURIComponent(courseId) +
				"&" + "oldteamname" + "=" + encodeURIComponent(teamName) +
				"&" + TEAM_NAME + "=" + encodeURIComponent(newTeamName));
	}	
}