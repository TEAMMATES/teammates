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