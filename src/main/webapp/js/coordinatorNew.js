//AJAX
var xmlhttp = new getXMLObject();

//DATE OBJECT
var cal = new CalendarPopup();

/*-----------------------------------------------------------CONSTANTS-------------------------------------------------------*/

//DISPLAY
var DISPLAY_EDITSTUDENT_FIELDSEMPTY = "Please fill in all fields marked with an *.";
var DISPLAY_FIELDS_EMPTY = "Please fill in all the relevant fields.";
var DISPLAY_LOADING = "<img src=/images/ajax-loader.gif /><br />";

var DISPLAY_STUDENT_DELETED = "The student has been removed.";
var DISPLAY_STUDENT_EDITED = "The student's details have been edited.";
var DISPLAY_STUDENT_EDITEDEXCEPTTEAM = "The student's details have been edited, except for his team<br /> as there is an ongoing evaluation.";

/***********************************************************EVALUATION RESULT PAGE***********************************************************/

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
		return false;
	}

	if (!isStudentNameValid(editName)) {
		setStatusMessage(DISPLAY_STUDENT_NAMEINVALID);
		return false;
	}

	else if (!isStudentEmailValid(editEmail)) {
		setStatusMessage(DISPLAY_STUDENT_EMAILINVALID);
		return false;
	}

	else if (!isStudentTeamNameValid(editTeamName)) {
		setStatusMessage(DISPLAY_STUDENT_TEAMNAMEINVALID);
		return false;
	}
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

	if (checkEditStudentInput(editName, editTeamName, editEmail, editGoogleID)) {
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

function toggleInformStudentsOfEvaluationChanges(courseID, name) {
	var s = confirm("Do you want to send e-mails to the students to inform them of changes to the evaluation?");
	if (s == true) {
		doInformStudentsOfEvaluationChanges(courseID, name);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

window.onload = function() {
	initializetooltip();
	if(typeof doPageSpecificOnload !== 'undefined'){
		doPageSpecificOnload();
	};
};

//DynamicDrive JS mouse-hover
document.onmousemove = positiontip;