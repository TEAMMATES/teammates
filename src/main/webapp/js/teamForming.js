// AJAX
var xmlhttp = new getXMLObject();

// DATE OBJECT
var cal = new CalendarPopup();

//DISPLAY
var DISPLAY_TEAMFORMINGSESSION_SCHEDULEINVALID = "<font color=\"#F00\">The team forming session schedule (start/deadline) is not valid.</font>";
var DISPLAY_TEAMFORMINGSESSION_DELETED = "The team forming session has been deleted.";
var DISPLAY_TEAMFORMINGSESSION_EDITED = "The team forming session has been edited.";
var DISPLAY_TEAMFORMINGSESSION_ADDED = "The team forming session has been added.";
var DISPLAY_TEAMFORMINGSESSION_EXISTS = "<font color=\"#F00\">The team forming session exists already.</font>";
var DISPLAY_TEAMFORMINGSESSION_INFORMEDSTUDENTSOFCHANGES = "E-mails have been sent out to inform the students of the changes to the team forming session.";

//DIV
var DIV_TEAMFORMINGSESSION_MANAGEMENT = "coordinatorTeamFormingSessionManagement";
var DIV_TEAMFORMINGSESSION_TABLE = "coordinatorTeamFormingSessionTable";

// GLOBAL VARIABLES FOR GUI
var teamFormingSessionSort = { courseID:0 }
var teamFormingSessionSortStatus = teamFormingSessionSort.courseID;

// MESSAGES
var MSG_TEAMFORMINGSESSION_EXISTS = "team forming session exists";
var MSG_TEAMFORMINGSESSION_EDITED = "team forming session edited";

// OPERATIONS
var OPERATION_COORDINATOR_CREATETEAMFORMINGSESSION = "coordinator_createteamformingsession";
var OPERATION_COORDINATOR_DELETETEAMFORMINGSESSION = "coordinator_deleteteamformingsession";
var OPERATION_COORDINATOR_EDITTEAMFORMINGSESSION = "coordinator_editteamformingsession";
var OPERATION_COORDINATOR_GETTEAMFORMINGSESSIONLIST = "coordinator_getteamformingsessionlist";
var OPERATION_COORDINATOR_INFORMSTUDENTSOFTEAMFORMINGSESSIONCHANGES = "coordinator_informstudentsofteamformingsessionchanges";
var OPERATION_COORDINATOR_REMINDSTUDENTS_TEAMFORMING = "coordinator_remindstudentsteamforming";
var OPERATION_SHOW_TEAMFORMING = "coordinator_teamforming";

// PARAMETERS
var TEAMFORMING_ACTIVATED = "activated";
var TEAMFORMING_DEADLINE = "deadline";
var TEAMFORMING_DEADLINETIME = "deadlinetime";
var TEAMFORMING_GRACEPERIOD = "graceperiod";
var TEAMFORMING_INSTRUCTIONS = "instr";
var TEAMFORMING_START = "start";
var TEAMFORMING_STARTTIME = "starttime";
var TEAMFORMING_TIMEZONE = "timezone";
var TEAMFORMING_PROFILETEMPLATE = "profile_template";

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
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayTeamFormingTab(){
	clearAllDisplay();
	setStatusMessage(DISPLAY_LOADING);
	printCreateTeamFormingSessionForm();
	doGetTeamFormingSessionList();
	clearStatusMessage();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function doCreateTeamFormingSession(courseID, start, startTime, deadline, deadlineTime, timeZone, gracePeriod, instructions, profileTemplate)
{
	setStatusMessage(DISPLAY_LOADING);
	var results = createTeamFormingSession(courseID, start, startTime, deadline, deadlineTime,
			timeZone, gracePeriod, instructions, profileTemplate);
	
	clearStatusMessage();
	
	if(results == 0)
	{
		displayTeamFormingTab();
		setStatusMessage(DISPLAY_TEAMFORMINGSESSION_ADDED);
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
		
		return handleEditEvaluation();
	}
}

function editTeamProfile(){
	//setStatusMessage(DISPLAY_LOADING);
	//alert("saving..");
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
		alert("returning 1");
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
 * evaluationList: successful 1: server error
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

function manageTeamProfile(){
	clearAllDisplay();
	printTeamDetail();
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
			+ "\" tabindex=8>" + sanitize(instructions) + "</textarea>"
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
	printTeams();
}

function printTeams(){
	var output = "<div><h1>TEAMS FORMED</h1></div>"
		+ "<div class=\"result_team\">" 
		+ "<p>" 
		+ "<a class='t_eval_view'  style=color:white; onclick=\"manageTeamProfile();\"\" href=# "
		+ "onmouseover=\"ddrivetip('Click here to see or edit the team profile.')\""
		+ "onmouseout=\"hideddrivetip()\">TEAM 1</a>"
		+ "</p>"
		+ "<table id=\"dataform\">"
		+ "<tr>";					
	
	output = output + "<th class='centeralign'>STUDENT NAME</th>"
					+ "<th class='centeralign'>PROFILE</th>";
	output = output + "<th class=\"centeralign\">ACTION</th>" + "</tr>";
	
	//student 1
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">ALICE</td><td>Proficient in Java, C# and C++; did ATAP last semester</td>";
	
	output = output + "<td class='centeralign' style=\"width: 150px;\">"
				+ "<a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Send an email to the student about his/her team.')\""
				+ "onmouseout=\"hideddrivetip()\">Notify</a>"
				+ "<span style=padding-left:20px /><a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Click here to change the team of this student.')\""
				+ "onmouseout=\"hideddrivetip()\">Change Team</a>"
				+ "</td>" + "</tr>";
	
	//student 2
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">BENNY</td><td>I did an internship in Facebook last semester; have good understanding of software engineering principles and database design.</td>";

	output = output + "<td class='centeralign' style=\"width: 150px;\">"
				+ "<a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Send an email to the student about his/her team.')\""
				+ "onmouseout=\"hideddrivetip()\">Notify</a>"
				+ "<span style=padding-left:20px /><a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Click here to change the team of this student.')\""
				+ "onmouseout=\"hideddrivetip()\">Change Team</a>"
				+ "</td>" + "</tr>";
		
	output = output + "</table><br /></div>";
	
	
	output = output + "<br /><div><h1>STUDENTS WITHOUT ANY TEAM</h1></div>"
	output = output +  "<br /><table id=\"dataform\">" + "<tr>";
	output = output + "<th class='centeralign'>STUDENT</th>"
				+ "<th class='centeralign'>PROFILE</th>";
	output = output + "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	//student 3
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">CHARLIE</td><td>Passionate about Game Development; Prior courses include CS2103, CS2102S, CS1102.</td>";

	output = output + "<td class='centeralign' style=\"width: 150px;\">"
				+ "<a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Send an email to the student about his/her team.')\""
				+ "onmouseout=\"hideddrivetip()\">Notify</a>"
				+ "<span style=padding-left:20px /><a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Click here to change the team of this student.')\""
				+ "onmouseout=\"hideddrivetip()\">Change Team</a>"
				+ "</td>" + "</tr>";

	//student 4
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">DANNY</td><td>Live on campus in Prince George Park Residences; Taking only 4 courses this semester; Looking for teammates with Java background.</td>";

	output = output + "<td class='centeralign' style=\"width: 150px;\">"
				+ "<a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Send an email to the student about his/her team.')\""
				+ "onmouseout=\"hideddrivetip()\">Notify</a>"
				+ "<span style=padding-left:20px /><a class='t_eval_view' onclick=\"manageTeamSession();\"\" href=# "
				+ "onmouseover=\"ddrivetip('Click here to change the team of this student.')\""
				+ "onmouseout=\"hideddrivetip()\">Change Team</a>"
				+ "</td>" + "</tr>";
	
	output = output + "</table><br />";
	
	output = output
				+ "<br /><br />"
				+ "<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Update\" />"
	
	document.getElementById(DIV_COURSE_TABLE).innerHTML = output;
}

function printTeamDetail(){
	var outputHeader = "<h1>TEAM DETAIL</h1>";

	var outputForm = ""
			+ "<form method=\"post\" action=\"\" name=\"form_addevaluation\">"
			+ "<table class=\"addform round\">" + "<tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course ID:</td>"
			+ "<td>cs2103testing</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Course Name:</td>"
			+ "<td>software engineering</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Team Name:</td>"
			+ "<td><input style=\"width: 100px;\" type=\"text\" value=\"TEAM 1\"</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Team Profile:</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ TEAMFORMING_INSTRUCTIONS
			+ "\" id=\""
			+ TEAMFORMING_INSTRUCTIONS
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter your team profile/proposal etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>Please enter your team profile/proposal etc.</textarea>"
			+ "</td>"
			+ "</tr>" 
			+ "<tr>"
			+ "<td></td>"
			+ "<td colspan=\"3\">"
			+ "<input id='t_btnAddEvaluation' onclick=\"editTeamProfile();\" type=\"button\" class=\"button\""
			+ "value=\"Save\" tabindex=2 />"
			+ "</td>" + "</tr>" + "</table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
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
				+ sanitize(teamFormingSessionList[loop].courseID) + "</td>"
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

//DynamicDrive JS mouse-hover
document.onmousemove = positiontip;