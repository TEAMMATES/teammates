//AJAX
var xmlhttp = new getXMLObject();

//DATE OBJECT
var cal = new CalendarPopup();

/*-----------------------------------------------------------CONSTANTS-------------------------------------------------------*/

//DISPLAY
var DISPLAY_COURSE_DELETEDALLSTUDENTS = "All students have been removed from the course.";
var DISPLAY_COURSE_DELETEDSTUDENT = "The student has been removed from the course.";
var DISPLAY_COURSE_NOTEAMS = "<font color=\"#F00\">The course does not have any teams.</font>";
var DISPLAY_COURSE_SENTREGISTRATIONKEY = "Registration key has been sent to ";
var DISPLAY_COURSE_SENTREGISTRATIONKEYS = "Registration keys are sent to the students.";
var DISPLAY_EDITSTUDENT_FIELDSEMPTY = "<font color=\"#F00\">Please fill in all fields marked with an *.</font>";
var DISPLAY_EVALUATION_ADDED_WITH_EMPTY_TEAMS = "The evaluation has been added. <font color=\"#F00\">Some students are without teams.</font>";
var DISPLAY_FIELDS_EMPTY = "<font color=\"#F00\">Please fill in all the relevant fields.</font>";
var DISPLAY_LOADING = "<img src=/images/ajax-loader.gif /><br />";

var DISPLAY_STUDENT_DELETED = "The student has been removed.";
var DISPLAY_STUDENT_EDITED = "The student's details have been edited.";
var DISPLAY_STUDENT_EDITEDEXCEPTTEAM = "The student's details have been edited, except for his team<br /> as there is an ongoing evaluation.";

/***********************************************************EVALUATION RESULT PAGE***********************************************************/
function isStudentInSummaryList(toStudent, summaryList){
	var i;
	for (i = 0; i < summaryList.length; i++) {
		if (summaryList[i].toStudent == toStudent)
			return true;
	}
	return false;
}

/*
 * UI Element: print evaluation submission (individual) type: Reviewer, Reviewee
 */
function printEvaluationIndividualForm(submissionList, summaryList, position,
		commentsEnabled, status, type) {
	var points;
	var justification = "";
	var commentsToStudent = "";
	var student = "";// used to show reviewer or reviewee contents
	var toStudent = summaryList[position].toStudent;
	var output = (type == REVIEWER) ? helpPrintTitle(REVIEWER_TITLE_INDIVIDUAL)
			: helpPrintTitle(REVIEWEE_TITLE_INDIVIDUAL);
	// Team name:
	output = output + helpPrintResultTeam(summaryList[position].teamName);
	// points:
	output = output
	+ helpPrintResultHeader(
			type,
			summaryList[position].toStudentName,
			displayEvaluationPoints(summaryList[position].claimedPoints),
			displayEvaluationPoints(summaryList[position].average));
	console.log("points:" + summaryList[position].claimedPoints + "|"
			+ summaryList[position].average);
	// evaluation to others header:
	student = (type == REVIEWEE) ? FROM_STUDENT : TO_STUDENT;
	outputTemp = helpPrintResultSubheader(student);

	// justification and comments:
	var submissionListLength = submissionList.length;
	for (var loop = 0; loop < submissionListLength; loop++) {
		if ((type == REVIEWEE && submissionList[loop].toStudent == toStudent)
				|| (type == REVIEWER && submissionList[loop].fromStudent == toStudent)) {
			// Extract data
			points = helpPrintPoints(submissionList[loop]);
			justification = helpPrintJustification(submissionList[loop]);
			commentsToStudent = helpPrintComments(submissionList[loop],
					commentsEnabled);

			// Print data
			if (submissionList[loop].fromStudent == submissionList[loop].toStudent) {
				outputTemp = helpPrintResultSelfComments(justification,
						commentsToStudent)
						+ outputTemp;

			} else {
				student = (type == REVIEWEE) ? submissionList[loop].fromStudentName
						: submissionList[loop].toStudentName;
				outputTemp = outputTemp
				+ helpPrintResultOtherComments(student, points,
						justification, commentsToStudent);
			}
		}
	}

	// buttons:
	output = output
	+ outputTemp
	+ "</table></div>"
	+ "<br /><br />"
	+ "<input type=\"button\" class =\"button\" value=\"Previous\" name=\"button_previous\" id=\"button_previous\">"
	+ " <input type=\"button\" class =\"button\" value=\"Next\" name=\"button_next\" id=\"button_next\">";

	if (type == REVIEWER && status == "CLOSED") {
		output = output
		+ " <input type=\"button\" class =\"button\" type=\"button\" value=\"Edit\" name=\"button_edit\" id=\"button_edit\">";
	}

	output = output
	+ " <input type=\"button\" class=\"button\" value=\"Back\" name=\"button_back\" id=\"button_back\"><br /><br />";

	// --print page:
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;

	// --catch actions:
	document.getElementById('button_next').onclick = function() {
		clearStatusMessage();
		position++;
		if (position >= summaryList.length) {
			position = 0;
		}
		printEvaluationIndividualForm(submissionList, summaryList, position,
				commentsEnabled, status, type);
	};
	document.getElementById('button_previous').onclick = function() {
		clearStatusMessage();
		if (position == 0) {
			position = summaryList.length - 1;
		} else {
			position--;
		}
		printEvaluationIndividualForm(submissionList, summaryList, position,
				commentsEnabled, status, type);
	};
	document.getElementById('button_back').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	if (type == REVIEWER && status == "CLOSED") {
		document.getElementById('button_edit').onclick = function() {
			printEditEvaluationResultsByReviewer(submissionList, summaryList,
					position, commentsEnabled, status);
		};
	}
}


/*----------------------------------------------------------OLD FUNCTIONS----------------------------------------------------------*/
function checkEditStudentInput(editName, editTeamName, editEmail, editGoogleID) {
	if (editName == "" || editTeamName == "" || editEmail == "") {
		setStatusMessage(DISPLAY_EDITSTUDENT_FIELDSEMPTY);
	}

	if (!isStudentNameValid(editName)) {
		setStatusMessage(DISPLAY_STUDENT_NAMEINVALID);
	}

	else if (!isStudentEmailValid(editEmail)) {
		setStatusMessage(DISPLAY_STUDENT_EMAILINVALID);
	}

	else if (!isStudentTeamNameValid(editTeamName)) {
		setStatusMessage(DISPLAY_STUDENT_TEAMNAMEINVALID);
	}
}

function formatDigit(num){
	return (num<10?"0":"")+num;
}

function convertDateToDDMMYYYY(date) {
	return formatDigit(date.getDate()) + "/" +
			formatDigit(date.getMonth()+1) + "/" +
			date.getFullYear();
}

function convertDateToHHMM(date) {
	return formatDigit(date.getHours()) + formatDigit(date.getMinutes());
}

function displayEditEvaluation(evaluationList, loop) {
	var courseID = evaluationList[loop].courseID;
	var name = evaluationList[loop].name;
	var instructions = evaluationList[loop].instructions;
	var start = evaluationList[loop].start;
	var deadline = evaluationList[loop].deadline;
	var timeZone = evaluationList[loop].timeZone;
	var gracePeriod = evaluationList[loop].gracePeriod;
	var status = evaluationList[loop].status;
	var activated = evaluationList[loop].activated;
	var commentsEnabled = evaluationList[loop].commentsEnabled;

	clearDisplay();
	printEditEvaluation(courseID, name, instructions, commentsEnabled, start,
			deadline, timeZone, gracePeriod, status, activated);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEditStudent(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	clearDisplay();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	printEditStudent(courseID, email, name, teamName, googleID,
			registrationKey, comments);
}

function displayStudentInformation(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	clearDisplay();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	printStudent(courseID, email, name, teamName, googleID, registrationKey,
			comments);
}

function doEditEvaluation(courseID, name, editStart, editStartTime,
		editDeadline, editDeadlineTime, timeZone, editGracePeriod,
		editInstructions, editCommentsEnabled, activated, status) {
	setStatusMessage(DISPLAY_LOADING);

	var results = editEvaluation(courseID, name, editStart, editStartTime,
			editDeadline, editDeadlineTime, timeZone, editGracePeriod,
			editInstructions, editCommentsEnabled, activated, status);

	if (results == 0) {
		if (activated == true) {
			displayEvaluationsTab();
			toggleInformStudentsOfEvaluationChanges(courseID, name);
		}

		else {
			displayEvaluationsTab();
			setStatusMessage(DISPLAY_EVALUATION_EDITED);
		}

	}

	else if (results == 2) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}

	else if (results == 3) {
		setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID);
	}

	else if (results == 4) {
		displayEvaluationsTab();
		setStatusMessage(DISPLAY_EVALUATION_EDITED);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditEvaluationResultsByReviewer(form, summaryList, position,
		commentsEnabled, status) {
	toggleEditEvaluationResultsStatusMessage(DISPLAY_LOADING);

	var submissionList = extractSubmissionList(form);

	var results = editEvaluationResults(submissionList, commentsEnabled);

	if (results == 0) {
		if(xmlhttp){
			sendGetSubmissionListRequest(submissionList[0].courseID, submissionList[0].evaluationName);
			submissionList = processGetSubmissionListResponse();
		}

		if (submissionList != 1) {
			printEvaluationIndividualForm(submissionList, summaryList,
					position, commentsEnabled, status, REVIEWER);
			document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
			toggleEditEvaluationResultsStatusMessage("");
			setStatusMessage(DISPLAY_EVALUATION_RESULTSEDITED);
		} else {
			alert(DISPLAY_SERVERERROR);
		}
	}

	else if (results == 2) {
		toggleEditEvaluationResultsStatusMessage(DISPLAY_FIELDS_EMPTY);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditStudent(courseID, email, editName, editTeamName, editEmail,
		editGoogleID, editComments) {
	setStatusMessage(DISPLAY_LOADING);

	var results = editStudent(courseID, email, editName, editTeamName,
			editEmail, editGoogleID, editComments);

	if (results == 0) {
		displayCourseInformation(courseID);
		setStatusMessage(DISPLAY_STUDENT_EDITED);
	}

	else if (results == 2) {
		displayCourseInformation(courseID);
		setStatusMessage("Duplicated Email found. Cannot edit student information");
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doInformStudentsOfEvaluationChanges(courseID, name) {
	setStatusMessage(DISPLAY_LOADING);

	var results = informStudentsOfEvaluationChanges(courseID, name);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_EVALUATION_INFORMEDSTUDENTSOFCHANGES);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: schedule invalid 4: no
 * changes made
 */
function editEvaluation(courseID, name, editStart, editStartTime, editDeadline,
		editDeadlineTime, timeZone, editGracePeriod, editInstructions,
		editCommentsEnabled, activated, status) {
	setStatusMessage(DISPLAY_LOADING);

	if (courseID == "" || name == "" || editStart == "" || editStartTime == ""
		|| editDeadline == "" || editDeadlineTime == ""
			|| editGracePeriod == "" || editInstructions == ""
				|| editCommentsEnabled == "") {
		return 2;
	}

	else if (!isEditEvaluationScheduleValid(editStart, editStartTime,
			editDeadline, editDeadlineTime, timeZone, activated, status)) {
		return 3;
	}

	else {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITEVALUATION + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name) + "&"
				+ EVALUATION_DEADLINE + "=" + encodeURIComponent(editDeadline)
				+ "&" + EVALUATION_DEADLINETIME + "="
				+ encodeURIComponent(editDeadlineTime) + "&"
				+ EVALUATION_INSTRUCTIONS + "="
				+ encodeURIComponent(editInstructions) + "&" + EVALUATION_START
				+ "=" + encodeURIComponent(editStart) + "&"
				+ EVALUATION_STARTTIME + "="
				+ encodeURIComponent(editStartTime) + "&"
				+ EVALUATION_GRACEPERIOD + "="
				+ encodeURIComponent(editGracePeriod) + "&"
				+ EVALUATION_COMMENTSENABLED + "=" + editCommentsEnabled);

		return handleEditEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields missing
 * 
 */
function editEvaluationResults(submissionList, commentsEnabled) {
	var submissionListLength = submissionList.length;
	for (var loop = 0; loop < submissionListLength; loop++) {
		if (submissionList[loop].points == -999) {
			return 2;
		}

		if (!commentsEnabled) {
			submissionList[loop].commentsToStudent = "";
		}
	}

	var request = "operation=" + OPERATION_COORDINATOR_EDITEVALUATIONRESULTS
	+ "&" + STUDENT_NUMBEROFSUBMISSIONS + "=" + submissionListLength
	+ "&" + COURSE_ID + "=" + submissionList[0].courseID + "&"
	+ EVALUATION_NAME + "=" + submissionList[0].evaluationName + "&"
	+ STUDENT_TEAMNAME + "=" + submissionList[0].teamName;

	for (var loop = 0; loop < submissionListLength; loop++) {
		request = request + "&" + STUDENT_FROMSTUDENT + loop + "="
		+ encodeURIComponent(submissionList[loop].fromStudent) + "&"
		+ STUDENT_TOSTUDENT + loop + "="
		+ encodeURIComponent(submissionList[loop].toStudent) + "&"
		+ STUDENT_POINTS + loop + "="
		+ encodeURIComponent(submissionList[loop].points) + "&"
		+ STUDENT_JUSTIFICATION + loop + "="
		+ encodeURIComponent(submissionList[loop].justification) + "&"
		+ STUDENT_COMMENTSTOSTUDENT + loop + "="
		+ encodeURIComponent(submissionList[loop].commentsToStudent);
	}

	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send(request);

		return handleEditEvaluationResults();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function editStudent(courseID, email, editName, editTeamName, editEmail,
		editGoogleID, editComments) {
	editName = editName.trim();
	editTeamName = editTeamName.trim();
	editEmail = editEmail.trim();
	editGoogleID = editGoogleID.trim();

	if (isEditStudentInputValid(editName, editTeamName, editEmail, editGoogleID)) {
		if (xmlhttp) {
			xmlhttp.open("POST", "/teammates", false);
			xmlhttp.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITSTUDENT + "&"
					+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
					+ STUDENT_EMAIL + "=" + encodeURIComponent(email) + "&"
					+ STUDENT_EDITNAME + "=" + encodeURIComponent(editName)
					+ "&" + STUDENT_EDITTEAMNAME + "="
					+ encodeURIComponent(editTeamName) + "&"
					+ STUDENT_EDITEMAIL + "=" + encodeURIComponent(editEmail)
					+ "&" + STUDENT_EDITGOOGLEID + "="
					+ encodeURIComponent(editGoogleID) + "&"
					+ STUDENT_EDITCOMMENTS + "="
					+ encodeURIComponent(editComments));
			return handleEditStudent();
		}
	}
}

function extractSubmissionList(form) {
	var submissionList = [];

	var counter = 0;

	var fromStudent;
	var toStudent;
	var teamName;
	var courseID;
	var evaluationName;
	var points;
	var justification;
	var commentsToStudent;

	var formLength = form.length;
	for (var loop = 0; loop < formLength; loop++) {
		fromStudent = form.elements[loop++].value;
		toStudent = form.elements[loop++].value;
		teamName = form.elements[loop++].value;
		courseID = form.elements[loop++].value;
		evaluationName = form.elements[loop++].value;

		points = form.elements[loop++].value;
		justification = form.elements[loop++].value;
		commentsToStudent = form.elements[loop].value;

		submissionList[counter++] = {
				fromStudent : fromStudent,
				toStudent : toStudent,
				courseID : courseID,
				evaluationName : evaluationName,
				teamName : teamName,
				points : points,
				justification : justification,
				commentsToStudent : commentsToStudent
		};
	}

	return submissionList;
}



function getDateWithTimeZoneOffset(timeZone) {
	var now = new Date();

	// Convert local time zone to ms
	var nowTime = now.getTime();

	// Obtain local time zone offset
	var localOffset = now.getTimezoneOffset() * 60000;

	// Obtain UTC time
	var UTC = nowTime + localOffset;

	// Add the time zone of evaluation
	var nowMilliS = UTC + (timeZone * 60 * 60 * 1000);

	now.setTime(nowMilliS);

	return now;
}

//return the value of the radio button that is checked
//return an empty string if none are checked, or
//there are no radio buttons
function getCheckedValue(radioObj) {
	if (!radioObj)
		return "";
	var radioLength = radioObj.length;
	if (radioLength == undefined)
		if (radioObj.checked)
			return radioObj.value;
		else
			return "";
	for ( var i = 0; i < radioLength; i++) {
		if (radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	return "";
}

/*
 * Returns
 * 
 * studentList: successful 1: server error
 * 
 */
function getStudentList(courseID) {
	if (xmlhttp) {

		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSTUDENTLIST + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleGetStudentList();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleDeleteAllStudents(courseID) {
	if (xmlhttp) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleDeleteStudent() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 4: no changes made
 * 
 */
function handleEditEvaluation() {
	if (xmlhttp.status == CONNECTION_OK) {
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];

		if (status != null) {
			var message = status.firstChild.nodeValue;

			if (message == MSG_EVALUATION_EDITED) {
				return 0;
			}

			else {
				return 4;
			}
		}
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleEditEvaluationResults() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function handleEditStudent() {
	if (xmlhttp.status == CONNECTION_OK) {
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];

		if (status != null) {
			var message = status.firstChild.nodeValue;

			if (message == MSG_EVALUATION_UNABLETOCHANGETEAMS) {
				return 2;
			}

			else {
				return 0;
			}
		}
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleInformStudentsOfEvaluationChanges() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
	}

	else {
		return 1;
	}
}

/*
 * Returns
 * 
 * studentList: successful 1: server error
 * 
 */
function handleGetStudentList() {
	if (xmlhttp.status == CONNECTION_OK) {
		var students = xmlhttp.responseXML.getElementsByTagName("students")[0];
		var studentList = new Array();

		var student;
		var name;
		var teamName;
		var email;
		var registrationKey;
		var comments;
		var courseID;
		var googleID;

		var studentsChildNodesLength = students.childNodes.length;
		for ( var loop = 0; loop < studentsChildNodesLength; loop++) {
			student = students.childNodes[loop];

			name = student.getElementsByTagName(STUDENT_NAME)[0].firstChild.nodeValue;
			teamName = student.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
			email = student.getElementsByTagName(STUDENT_EMAIL)[0].firstChild.nodeValue;
			registrationKey = student.getElementsByTagName(STUDENT_REGKEY)[0].firstChild.nodeValue;
			googleID = student.getElementsByTagName(STUDENT_ID)[0].firstChild.nodeValue;
			comments = student.getElementsByTagName(STUDENT_COMMENTS)[0].firstChild.nodeValue;
			courseID = student.getElementsByTagName(STUDENT_COURSEID)[0].firstChild.nodeValue;
			studentList[loop] = {
					name : name,
					teamName : teamName,
					email : email,
					registrationKey : registrationKey,
					googleID : googleID,
					comments : comments,
					courseID : courseID
			};
		}

		return studentList;
	}

	else {
		return 1;
	}
}

function informStudentsOfEvaluationChanges(courseID, name) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation="
				+ OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));
	}

	return handleInformStudentsOfEvaluationChanges();
}

function isEditStudentInputValid(editName, editTeamName, editEmail,
		editGoogleID) {
	if (editName == "" || editTeamName == "" || editEmail == "") {
		return false;
	}

	if (!isStudentNameValid(editName)) {
		return false;
	}

	else if (!isStudentEmailValid(editEmail)) {
		return false;
	}

	else if (!isStudentTeamNameValid(editTeamName)) {
		return false;
	}

	return true;
}

function populateEditEvaluationResultsPointsForm(form, submissionList) {
	var points = 0;
	var submissionListLength = submissionList.length;

	var len = form.elements.length / 8;
	for (var x = 0; x < len; x++) {
		for (var y = 0; y < submissionListLength; y++) {
			if (submissionList[y].fromStudent == form.elements[x * 8].value
					&& submissionList[y].toStudent == form.elements[x * 8 + 1].value) {
				points = submissionList[y].points;
				break;
			}
		}

		setSelectedOption(form.elements[x * 8 + 5], points);
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function sendRegistrationKeys(courseID) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEYS
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleSendRegistrationKeys();
	}
}

//set the radio button with the given value as being checked
//do nothing if there are no radio buttons
//if the given value does not exist, all the radio buttons
//are reset to unchecked
function setCheckedValue(radioObj, newValue) {
	if (!radioObj)
		return;
	var radioLength = radioObj.length;
	if (radioLength == undefined) {
		radioObj.checked = (radioObj.value == newValue.toString());
		return;
	}
	for ( var i = 0; i < radioLength; i++) {
		radioObj[i].checked = false;
		if (radioObj[i].value == newValue.toString()) {
			radioObj[i].checked = true;
		}
	}
}

function setSelectedOption(s, v) {
	var sOptionsLength = s.options.length;
	for ( var i = 0; i < sOptionsLength; i++) {
		if (s.options[i].value == v) {
			s.options[i].selected = true;
			return;
		}
	}
}

function toggleInformStudentsOfEvaluationChanges(courseID, name) {
	var s = confirm("Do you want to send e-mails to the students to inform them of changes to the evaluation?");
	if (s == true) {
		doInformStudentsOfEvaluationChanges(courseID, name);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function toggleSortStudentsByName(studentList, courseID) {
	printStudentList(studentList.sort(sortByName), courseID);
	studentSortStatus = studentSort.name;
	document.getElementById("button_sortstudentname").setAttribute("class",
	"buttonSortAscending");
}

function toggleSortStudentsByStatus(studentList, courseID) {
	printStudentList(studentList.sort(sortByGoogleID), courseID);
	studentSortStatus = studentSort.status;
	document.getElementById("button_sortstudentstatus").setAttribute("class",
	"buttonSortAscending");
}

function toggleSortStudentsByTeamName(studentList, courseID) {
	printStudentList(studentList.sort(sortByTeamName), courseID);
	studentSortStatus = studentSort.teamName;
	document.getElementById("button_sortstudentteam").setAttribute("class",
	"buttonSortAscending");
}

window.onload = function() {
	initializetooltip();
	if(typeof doPageSpecificOnload !== 'undefined'){
		doPageSpecificOnload();
	};
};

//DynamicDrive JS mouse-hover
document.onmousemove = positiontip;