//AJAX
var xmlhttp = new getXMLObject();

//DATE OBJECT
var cal = new CalendarPopup();

/*-----------------------------------------------------------CONSTANTS-------------------------------------------------------*/

//DISPLAY
var DISPLAY_COURSE_ARCHIVED = "The course has been archived.";
var DISPLAY_COURSE_DELETEDALLSTUDENTS = "All students have been removed from the course.";
var DISPLAY_COURSE_DELETEDSTUDENT = "The student has been removed from the course.";
var DISPLAY_COURSE_NOTEAMS = "<font color=\"#F00\">The course does not have any teams.</font>";
var DISPLAY_COURSE_SENTREGISTRATIONKEY = "Registration key has been sent to ";
var DISPLAY_COURSE_SENTREGISTRATIONKEYS = "Registration keys are sent to the students.";
var DISPLAY_COURSE_UNARCHIVED = "The course has been unarchived.";
var DISPLAY_EDITSTUDENT_FIELDSEMPTY = "<font color=\"#F00\">Please fill in all fields marked with an *.</font>";
var DISPLAY_EVALUATION_ADDED_WITH_EMPTY_TEAMS = "The evaluation has been added. <font color=\"#F00\">Some students are without teams.</font>";
var DISPLAY_EVALUATION_ARCHIVED = "The evaluation has been archived.";
var DISPLAY_EVALUATION_UNARCHIVED = "The evaluation has been unarchived.";
var DISPLAY_FIELDS_EMPTY = "<font color=\"#F00\">Please fill in all the relevant fields.</font>";
var DISPLAY_LOADING = "<img src=/images/ajax-loader.gif /><br />";

var DISPLAY_STUDENT_DELETED = "The student has been removed.";
var DISPLAY_STUDENT_EDITED = "The student's details have been edited.";
var DISPLAY_STUDENT_EDITEDEXCEPTTEAM = "The student's details have been edited, except for his team<br /> as there is an ongoing evaluation.";

/***********************************************************EVALUATION RESULT PAGE***********************************************************/
/*----------------------------------------------------------EVALUATION RESULT PAGE----------------------------------------------------------*/
function displayEvaluationResults(evaluationList, loop) {
	var courseID = evaluationList[loop].courseID;
	var name = evaluationList[loop].name;
	var start = evaluationList[loop].start;
	var deadline = evaluationList[loop].deadline;
	var status = evaluationList[loop].status;
	var activated = evaluationList[loop].activated;
	var commentsEnabled = evaluationList[loop].commentsEnabled;
	var published = evaluationList[loop].published;

	clearDisplay();

	printEvaluationHeaderForm(courseID, name, start, deadline, status, activated, published);
	evaluationResultsViewStatus = evaluationResultsView.reviewer;

	doGetSubmissionResultsList(courseID, name, status, commentsEnabled);

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}



//----------------------------------------------------------DISPLAY EVALUATION DETAILS FUNCTIONS
function printEvaluationHeaderForm(courseID, evaluationName, start, deadline, status, activated, published) {
	var outputHeader = "<h1>EVALUATION RESULTS</h1>";

	var output = "" + "<table class=\"headerform\">" + "<tr>"
	+ "<td class=\"fieldname\">Course ID:</td>" + "<td>"
	+ encodeChar(courseID)
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Evaluation name:</td>"
	+ "<td>"
	+ encodeChar(evaluationName)
	+ "</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Opening time:</td>"
	+ "<td>"
	+ convertDateToDDMMYYYY(new Date(start))
	+ " "
	+ convertDateToHHMM(new Date(start))
	+ "H</td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Closing time:</td>"
	+ "<td>"
	+ convertDateToDDMMYYYY(new Date(deadline))
	+ " "
	+ convertDateToHHMM(new Date(deadline))
	+ "H</td>"
	+ "</tr>"
	+

	// new radio button: review type + report type
	"<tr>"
	+ "<td class=\"rightalign\"><b>Report Type:</b> "
	+

	"<input type = \"radio\" name = \"radio_viewall\" id = \"radio_summary\" value = \"summary\" checked = \"checked\" />"
	+ "<label for = \"radio_summary\">Summary</label>&nbsp&nbsp&nbsp"
	+

	"<input type = \"radio\" name = \"radio_viewall\" id = \"radio_detail\" value = \"detail\" />"
	+ "<label for = \"radio_detail\">Detail</label>"
	+ "</td>"
	+ "<td class=\"leftalign\"><b>Review Type:</b> "
	+

	"<input type = \"radio\" name = \"radio_viewbytype\" id = \"radio_reviewer\" value = \"by reviewer\" checked = \"checked\"/>"
	+ "<label for = \"radio_reviewer\">By Reviewer</label>&nbsp&nbsp&nbsp"
	+ "<input type = \"radio\" name = \"radio_viewbytype\" id = \"radio_reviewee\" value = \"by reviewee\"/>"
	+ "<label for = \"radio_reviewee\">By Reviewee</label>" +

	"</td>" + "</tr>" +

	// publish, unpublish button
	"<tr>" + "<td></td>" + "<td>";
	// publish
	if (status != "OPEN") {
		if (published == false && activated == true) {
			output = output
			+ "<input type=\"button\" class=\"button\" id = \"button_publish\" value = \"Publish\" onclick = \"javascript:togglePublishEvaluation('"
			+ courseID + "','" + evaluationName
			+ "', true, false)\" />";
		} else if (published == true) {
			output = output
			+ "<input type=\"button\" class=\"button\" id = \"button_publish\" value = \"Unpublish\" onclick = \"javascript:togglePublishEvaluation('"
			+ courseID + "','" + evaluationName
			+ "', false, false)\" />";

		}
	}
	output = output + "</td>" + "</tr>" + "</table>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output;
}

//----------------------------------------------------------LIST EVALUATION RESULTS FUNCTIONS
function doGetSubmissionResultsList(courseID, evaluationName, status, commentsEnabled) {
	setStatusMessageToLoading();

	if (!xmlhttp) {
		alert(DISPLAY_BROWSERERROR);
		return;
	}

	sendGetSubmissionListRequest(courseID, evaluationName);
	var results = processGetSubmissionListResponse();

	clearStatusMessage();

	if(results == SERVERERROR) {
		alert(DISPLAY_SERVERERROR);
		return;
	}

	var compiledResults = compileSubmissionSummaryList(results);
	toggleSortEvaluationSummaryListByTeamName(results, compiledResults, status, commentsEnabled);

}

function sendGetSubmissionListRequest(courseID, evaluationName) {
	xmlhttp.open("POST", "/teammates", false);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
	xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSUBMISSIONLIST
			+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) 
			+ "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName));
}

function processGetSubmissionListResponse() {
	if (xmlhttp.status != CONNECTION_OK)
		return SERVERERROR;

	var submissions = xmlhttp.responseXML.getElementsByTagName("submissions")[0];

	var submissionList = new Array();
	if(submissions != null)
		submissionList = complieSubmissionList(submissions);

	return submissionList;
}

//util functions to format submission data
function complieSubmissionList(submissions) {
	var submissionList = new Array();

	var submission;

	var courseID;
	var evaluationName;
	var justification;
	var commentsToStudent;
	var teamName;
	var points;
	var pointsBumpRatio;

	var fromStudent;
	var fromStudentName;
	var fromStudentComments;

	var toStudent;
	var toStudentName;
	var toStudentComments;


	var submissionsChildNodesLength = submissions.childNodes.length;
	for (var loop = 0; loop < submissionsChildNodesLength; loop++) {

		submission = submissions.childNodes[loop];

		courseID = submission.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
		evaluationName = submission.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
		justification = submission.getElementsByTagName(STUDENT_JUSTIFICATION)[0].firstChild.nodeValue;
		commentsToStudent = submission.getElementsByTagName(STUDENT_COMMENTSTOSTUDENT)[0].firstChild.nodeValue;

		teamName = submission.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
		points = parseInt(submission.getElementsByTagName(STUDENT_POINTS)[0].firstChild.nodeValue);
		pointsBumpRatio = parseFloat(submission.getElementsByTagName(STUDENT_POINTSBUMPRATIO)[0].firstChild.nodeValue);

		fromStudentName = submission.getElementsByTagName(STUDENT_FROMSTUDENTNAME)[0].firstChild.nodeValue;
		fromStudent = submission.getElementsByTagName(STUDENT_FROMSTUDENT)[0].firstChild.nodeValue;
		fromStudentComments = submission.getElementsByTagName(STUDENT_FROMSTUDENTCOMMENTS)[0].firstChild.nodeValue;

		toStudentName = submission.getElementsByTagName(STUDENT_TOSTUDENTNAME)[0].firstChild.nodeValue;
		toStudent = submission.getElementsByTagName(STUDENT_TOSTUDENT)[0].firstChild.nodeValue;
		toStudentComments = submission.getElementsByTagName(STUDENT_TOSTUDENTCOMMENTS)[0].firstChild.nodeValue;


		submissionList[loop] = {
				fromStudentName : fromStudentName,
				toStudentName : toStudentName,
				fromStudent : fromStudent,
				toStudent : toStudent,
				courseID : courseID,
				evaluationName : evaluationName,
				teamName : teamName,
				justification : justification,
				commentsToStudent : commentsToStudent,
				points : points,
				pointsBumpRatio : pointsBumpRatio,
				fromStudentComments : fromStudentComments,
				toStudentComments : toStudentComments
		};
	}
	return submissionList;
}
/**---------------------------------------------------------added 26 Mar 2012------------------------------------*/
function compileSubmissionSummaryList(submissionList){
	logSubmissionList(submissionList);

	//creating summary list (without team normalization)
	var summaryList = new Array();
	var count = 0;
	var i;
	for(i = 0; i < submissionList.length; i++){
		var submission = submissionList[i];

		if(!isStudentInSummaryList(submission.toStudent, summaryList)){
			summaryList[count++] = createSubmissionSummary(submission, submissionList);
		}
	}

	//creating team bumpratio
	var normalizedList = new Array();
	count = 0;
	for(i = 0; i < summaryList.length; i++){
		var summary = summaryList[i];
		if(!isTeamInNormalizedList(summary.teamName, normalizedList)){
			var normalizedData = createNormalizedData(summary, summaryList);
			if(normalizedData !== null)
				normalizedList[count++] = normalizedData;
		}
	}

	//normalizing summary list with team bumpratio
	var j;
	for(i = 0; i < normalizedList.length; i++){
		var team = normalizedList[i].teamName;

		for(j = 0; j < summaryList.length; j++){
			if(summaryList[j].teamName == team && summaryList[j].average != NA){
				//normalizing average by team bumpratio
				summaryList[j].average = Math.round(summaryList[j].average * normalizedList[i].pointsBumpRatio);
				//updating difference
				summaryList[j].difference = getDifference(summaryList[j].average, summaryList[j].claimedPoints);
			}
		}
	}

	//debug
	logSummaryList(summaryList);

	return summaryList;
}

function createNormalizedData(summary, summaryList){
	var output = null;
	var totalPoints = 0;
	var totalPointGivers = 0;
	var pointsBumpRatio;
	var teamName;

	var i;
	for(i = 0; i < summaryList.length; i++){
		if(summaryList[i].teamName == summary.teamName && summaryList[i].average != NA){
			totalPoints += summaryList[i].average;
			totalPointGivers++;
		}
	}
	if(totalPointGivers != 0){
		pointsBumpRatio = totalPointGivers * 100 / totalPoints;
		teamName = summary.teamName;
		output = {
				pointsBumpRatio : pointsBumpRatio,
				teamName : teamName
		};
	}
	return output;	
}

function isTeamInNormalizedList(teamName, normalizedList){
	var i;
	for(i = 0; i < normalizedList.length; i++){
		if(normalizedList[i].teamName == teamName)
			return true;
	}
	return false;
}

function isStudentInSummaryList(toStudent, summaryList){
	var i;
	for (i = 0; i < summaryList.length; i++) {
		if (summaryList[i].toStudent == toStudent)
			return true;
	}
	return false;
}

function createSubmissionSummary(submission, submissionList){
	//basic info
	var toStudent = submission.toStudent;
	var toStudentName = submission.toStudentName;
	var teamName = submission.teamName;
	var toStudentComments = submission.toStudentComments;
	//points given by self
	var selfSubmission = getSelfSubmission(toStudent, submissionList);
	var submitted = isSubmitted(selfSubmission);
	var claimedPoints = getClaimedPoints(selfSubmission);
	//points given by others
	var average = getAverage(toStudent, submissionList);
	//difference
	var difference = getDifference(average, claimedPoints);

	var summary = {
			toStudent : toStudent,
			toStudentName : toStudentName,
			teamName : teamName,
			average : average,
			difference : difference,
			toStudentComments : toStudentComments,
			submitted : submitted,
			claimedPoints : claimedPoints
	};
	return summary;
}

function getSelfSubmission(toStudent, submissionList){
	var i;
	for(i = 0; i < submissionList.length; i++){
		if(submissionList[i].toStudent == toStudent 
				&& submissionList[i].fromStudent == toStudent)
			return submissionList[i];
	}
	return null;
}

function isSubmitted(selfSubmission){
	if(selfSubmission !== null && selfSubmission.points != -999)
		return true;
	else
		return false;
}

function getClaimedPoints(selfSubmission){
	if(selfSubmission !== null)
		return claimedPointsToString(selfSubmission.points, selfSubmission.pointsBumpRatio);
	else
		return NA;
}

function claimedPointsToString(points, pointsBumpRatio){
	if(points == -999)
		return NA;
	else if(points == -101)
		return NOTSURE;
	else
		return Math.round(points * pointsBumpRatio);
}

function getAverage(toStudent, submissionList){
	var i;
	var totalPoints = 0;
	var totalPointGivers = 0;
	for(i = 0; i < submissionList.length; i++){
		if(submissionList[i].toStudent == toStudent && submissionList[i].fromStudent != toStudent){
			if(submissionList[i].points != -999 && submissionList[i].points != -101){
				totalPoints += Math.round(submissionList[i].points * submissionList[i].pointsBumpRatio);
				totalPointGivers++;
			}
		}
	}
	if(totalPointGivers == 0)
		return NA;
	else
		return Math.round(totalPoints / totalPointGivers);
}

function getDifference(average, claimedPoints){
	var diff = Math.round(average - claimedPoints);

	return (isNaN(diff))? NA: diff;
}

/**---------------------------------------------------------added 26 Mar 2012------------------------------------*/

//util functions to sort evaluation results
function toggleSortEvaluationSummaryListByAverage(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByAverage), status, commentsEnabled, REVIEWEE);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.average;
	document.getElementById("button_sortaverage").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByDiff(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByDiff), status, commentsEnabled, REVIEWEE);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.diff;
	document.getElementById("button_sortdiff").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByFromStudentName(submissionList, summaryList, status, commentsEnabled) {
	var type;
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;
	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByFromStudentName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name;
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListBySubmitted(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortBySubmitted), status, commentsEnabled, REVIEWER);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.submitted;
	document.getElementById("button_sortsubmitted").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList, status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;

	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByTeamName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.teamName;
	document.getElementById("button_sortteamname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByToStudentName(submissionList, summaryList, status, commentsEnabled) {
	var type;
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		type = REVIEWEE;
	} else {
		type = REVIEWER;
	}
	printEvaluationSummaryForm(submissionList, summaryList.sort(sortByToStudentName), status, commentsEnabled, type);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name;
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

/*
 * UI Element: print evaluation summary form type: reviewer, reviewee
 */
function printEvaluationSummaryForm(submissionList, summaryList, status, commentsEnabled, type) {
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";

	var submitted;
	var output = "";

	// if link is disabled, insert this line to reset style and onclick:
	var disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";

	output = output
	+ "<table id=\"dataform\">"
	+ "<tr>"
	+ "<th class=\"leftalign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortteamname\">TEAM</input></th>"
	+ "<th class=\"leftalign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\">STUDENT</input></th>";

	// thead:
	if (type == REVIEWER) {
		output = output
		+ "<th class=\"centeralign\"><input class=\"buttonSortNone\" type=\"button\" id=\"button_sortsubmitted\">SUBMITTED</input></th>"
		+ "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	} else {
		output = output
		+ "<th class=\"leftalign\"><div onmouseover=\"ddrivetip('" + HOVER_MESSAGE_CLAIMED + "')\" onmouseout=\"hideddrivetip()\">" +
		"<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortaverage\">CLAIMED CONTRIBUTION</input></div></th>"
		+ "<th class=\"centeralign\"><div  onmouseover=\"ddrivetip('" + HOVER_MESSAGE_PERCEIVED_CLAIMED + "')\" onmouseout=\"hideddrivetip()\">" +
		"<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortdiff\">[PERCEIVED - CLAIMED]</input></div></th>"
		+ "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";
	}

	var summaryListLength = summaryList.length;
	for (var loop = 0; loop < summaryListLength; loop++) {

		if (summaryList[loop].submitted) {
			submitted = YES;
		} else {
			submitted = NO;
		}

		output = output + "<tr>" + "<td>"
		+ encodeCharForPrint(summaryList[loop].teamName) + "</td>"
		+ "<td>";

		if (encodeChar(summaryList[loop].toStudentComments) != "") {
			output = output + "<a onmouseover=\"ddrivetip('"
			+ encodeCharForPrint(summaryList[loop].toStudentComments)
			+ "')\" onmouseout=\"hideddrivetip()\">"
			+ encodeChar(summaryList[loop].toStudentName) + "</a>"
			+ "</td>";
		} else {
			output = output + encodeChar(summaryList[loop].toStudentName)
			+ "</td>";
		}

		if (type == REVIEWER) {
			var hasEdit = false;

			if (status == "CLOSED") {
				hasEdit = true;
			}

			output = output + "<td class=\"centeralign\" id=\"status_submitted"
			+ loop + "\">" + submitted + "</td>";

			output = output
			+ "<td class=\"centeralign\">"
			+ "<a name=\"viewEvaluationResults"
			+ loop
			+ "\" id=\"viewEvaluationResults"
			+ loop
			+ "\" href=# "
			+ "onmouseover=\"ddrivetip('View feedback from the student for his team')\""
			+ "onmouseout=\"hideddrivetip()\">View</a>"
			+ "<a name=\"editEvaluationResults"
			+ loop
			+ "\" id=\"editEvaluationResults"
			+ loop
			+ "\" href=# "
			+ "onmouseover=\"ddrivetip('Edit feedback from the student for his team')\""
			+ "onmouseout=\"hideddrivetip()\""
			+ (hasEdit ? "" : disabled) + ">Edit</a>";

			output = output + "</td>" + "</tr>";

		} else {
			output = output + "<td>"
			+ displayEvaluationPoints(summaryList[loop].claimedPoints)
			+ "</td>";

			if (summaryList[loop].difference > 0) {
				output = output
				+ "<td class=\"centeralign\"><span class=\"posDiff\">"
				+ summaryList[loop].difference + "</span></td>";
			} else if (summaryList[loop].difference < 0) {
				output = output
				+ "<td class=\"centeralign\"><span class=\"negDiff\">"
				+ summaryList[loop].difference + "</span></td>";
			} else {
				output = output + "<td class=\"centeralign\">"
				+ summaryList[loop].difference + "</td>";
			}

			output = output
			+ "<td class=\"centeralign\">"
			+ "<a name=\"viewEvaluationResults" + loop + "\" id=\"viewEvaluationResults" + loop + "\" href=# "
			+ "onmouseover=\"ddrivetip('View feedback from the team for the student')\""
			+ "onmouseout=\"hideddrivetip()\">View</a>";

			output = output + "</td>" + "</tr>";
		}
	}
	output = output
	+ "</table>"
	+ "<br /><br />"
	+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />"
	+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;

	// catch actions:
	document.getElementById('radio_reviewee').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	document.getElementById('radio_reviewer').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	document.getElementById('radio_summary').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};
	document.getElementById('radio_detail').onclick = function() {
		printEvaluationReportByAction(submissionList, summaryList, status,
				commentsEnabled);
	};

	document.getElementById('button_sortteamname').onclick = function() {
		toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList,
				status, commentsEnabled);
	};
	document.getElementById('button_sortname').onclick = function() {
		toggleSortEvaluationSummaryListByToStudentName(submissionList,
				summaryList, status, commentsEnabled);
	};

	for (loop = 0; loop < summaryListLength; loop++) {
		if (document.getElementById('viewEvaluationResults' + loop) != null
				&& document.getElementById('viewEvaluationResults' + loop).onclick == null) {
			document.getElementById('viewEvaluationResults' + loop).onclick = function() {
				hideddrivetip();
				printEvaluationIndividualForm(submissionList, summaryList,
						this.id.substring(21, this.id.length), commentsEnabled,
						status, type);
				clearStatusMessage();
			};
		}
	}

	if (type == REVIEWER) {
		document.getElementById('button_sortsubmitted').onclick = function() {
			toggleSortEvaluationSummaryListBySubmitted(submissionList,
					summaryList, status, commentsEnabled);
		};

		for (loop = 0; loop < summaryListLength; loop++) {
			if (document.getElementById('editEvaluationResults' + loop) != null
					&& document.getElementById('editEvaluationResults' + loop).onclick == null) {
				document.getElementById('editEvaluationResults' + loop).onclick = function() {
					hideddrivetip();

					printEditEvaluationResultsByReviewer(submissionList,
							summaryList, this.id.substring(21, this.id.length),
							commentsEnabled, status);

					document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
					clearStatusMessage();
				};
			}
		}

	} else {
		document.getElementById('button_sortaverage').onclick = function() {
			toggleSortEvaluationSummaryListByAverage(submissionList,
					summaryList, status, commentsEnabled);
		};
		document.getElementById('button_sortdiff').onclick = function() {
			toggleSortEvaluationSummaryListByDiff(submissionList, summaryList,
					status, commentsEnabled);
		};

	}

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

/*
 * UI Element: print evaluation detail form type: reviewer, reviewee TODO:
 * reviewer view show original or normalized points?
 */
function printEvaluationDetailForm(submissionList, summaryList, status,
		commentsEnabled, type) {
	clearStatusMessage();

	var output = (type == REVIEWER) ? helpPrintTitle(REVIEWER_TITLE_DETAIL)
			: helpPrintTitle(REVIEWEE_TITLE_DETAIL);

	output = output + "<div id=\"detail\">";

	var summaryListLength = summaryList.length;
	for (var x = 0; x < summaryListLength; x++) {
		// Team Name:
		if (x == 0 || summaryList[x].teamName != summaryList[x - 1].teamName) {
			if (x != 0)
				output = output + "</div><br />";
			output = output + helpPrintResultTeam(summaryList[x].teamName);
		}
		output = output
		+ helpPrintSubmission(submissionList, summaryList, x, status,
				commentsEnabled, type) + "</table>";
	}

	output = output
	+ "</div><br /><br />"
	+ "<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Back\" />"
	+ " <input type=\"button\" class =\"button\" name=\"button_top\" id=\"button_top\" value=\"To Top\" />";

	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output;

	document.getElementById('button_top').onclick = function() {
		document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	};
	document.getElementById("button_back").onclick = function() {
		displayEvaluationsTab();
	};

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
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

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function deleteAllStudents(courseID) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETEALLSTUDENTS
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID));

		return handleDeleteAllStudents(courseID);
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function deleteStudent(courseID, studentEmail) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETESTUDENT + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ STUDENT_EMAIL + "=" + encodeURIComponent(studentEmail));

		return handleDeleteStudent();
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

function doDeleteAllStudents(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = deleteAllStudents(courseID);

	if (results != 1) {
		doGetCourse(courseID);
		setStatusMessage(DISPLAY_COURSE_DELETEDALLSTUDENTS
				+ " Click <a class='t_course_enroll' href=\"javascript:displayEnrollmentPage('"
				+ courseID + "');\">here</a> to enroll students.");
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doDeleteStudent(courseID, email) {
	setStatusMessage(DISPLAY_LOADING);

	var student = getStudent(courseID, email);

	var results = deleteStudent(courseID, email);

	if (results != 1) {
		displayCourseInformation(courseID);
		setStatusMessage(DISPLAY_COURSE_DELETEDSTUDENT);

		// deleting team profile if all students of a particular team are
		// deleted
		var teams = getTeamsOfCourse(courseID);
		var loop;
		var teamNameExists = 0;
		for (loop = 0; loop < teams.length; loop++) {
			if (teams[loop].teamName == student.teamName)
				teamNameExists = 1;
		}
		if (teamNameExists == 0)
			deleteTeamProfile(courseID, student.teamName);
	}

	else {
		alert(DISPLAY_SERVERERROR);
	}
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

function doSendRegistrationKey(courseID, email, name) {
	setStatusMessage(DISPLAY_LOADING);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);

	var results = sendRegistrationKey(courseID, email);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_COURSE_SENTREGISTRATIONKEY + name + ".");
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doSendRegistrationKeys(courseID) {
	setStatusMessage(DISPLAY_LOADING);

	var results = sendRegistrationKeys(courseID);

	clearStatusMessage();

	if (results != 1) {
		setStatusMessage(DISPLAY_COURSE_SENTREGISTRATIONKEYS);
	} else {
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
 * evaluationList: successful 1: server error
 * 
 */
function handleGetEvaluationList() {
	if (xmlhttp.status == CONNECTION_OK) {
		var evaluations = xmlhttp.responseXML
		.getElementsByTagName("evaluations")[0];
		var evaluationList = new Array();
		var now;
		var evaluation;
		var courseID;
		var name;
		var commentsEnabled;
		var instructions;
		var start;
		var deadline;
		var gracePeriod;
		var numberOfCompletedEvaluations;
		var numberOfEvaluations;
		var published;
		var status = "";
		var activated;

		if (evaluations != null) {
			var evaluationsChildNodesLength = evaluations.childNodes.length;
			for (var loop = 0; loop < evaluationsChildNodesLength; loop++) {
				evaluation = evaluations.childNodes[loop];

				courseID = evaluation.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				name = evaluation.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
				commentsEnabled = (evaluation
						.getElementsByTagName(EVALUATION_COMMENTSENABLED)[0].firstChild.nodeValue
						.toLowerCase() == "true");
				instructions = evaluation
				.getElementsByTagName(EVALUATION_INSTRUCTIONS)[0].firstChild.nodeValue;
				start = new Date(
						evaluation.getElementsByTagName(EVALUATION_START)[0].firstChild.nodeValue);
				deadline = new Date(
						evaluation.getElementsByTagName(EVALUATION_DEADLINE)[0].firstChild.nodeValue);
				timeZone = parseFloat(evaluation
						.getElementsByTagName(EVALUATION_TIMEZONE)[0].firstChild.nodeValue);
				gracePeriod = parseInt(evaluation
						.getElementsByTagName(EVALUATION_GRACEPERIOD)[0].firstChild.nodeValue);
				published = (evaluation
						.getElementsByTagName(EVALUATION_PUBLISHED)[0].firstChild.nodeValue
						.toLowerCase() == "true");
				activated = (evaluation
						.getElementsByTagName(EVALUATION_ACTIVATED)[0].firstChild.nodeValue
						.toLowerCase() == "true");
				numberOfCompletedEvaluations = parseInt(evaluation
						.getElementsByTagName(EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS)[0].firstChild.nodeValue);
				numberOfEvaluations = parseInt(evaluation
						.getElementsByTagName(EVALUATION_NUMBEROFEVALUATIONS)[0].firstChild.nodeValue);

				now = getDateWithTimeZoneOffset(timeZone);

				// Check if evaluation should be open or closed
				if (now > start && deadline > now) {
					status = "OPEN";
				}

				else if (now > deadline || activated) {
					status = "CLOSED";
				}

				else if (now < start && !activated) {
					status = "AWAITING";
				}

				if (published == true) {
					status = "PUBLISHED";
				}

				evaluationList[loop] = {
						courseID : courseID,
						name : name,
						commentsEnabled : commentsEnabled,
						instructions : instructions,
						start : start,
						deadline : deadline,
						timeZone : timeZone,
						gracePeriod : gracePeriod,
						published : published,
						published : published,
						activated : activated,
						numberOfCompletedEvaluations : numberOfCompletedEvaluations,
						numberOfEvaluations : numberOfEvaluations,
						status : status
				};
			}
		}

		return evaluationList;
	} else {
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

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function handleSendRegistrationKey() {
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
 * 0: successful 1: server error
 */
function handleSendRegistrationKeys() {
	if (xmlhttp.status == CONNECTION_OK) {
		return 0;
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

//xl: new added
function printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled) {
	// clean page:
	
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	clearStatusMessage();

	// case 1: [x]reviewee [x]summary..............case handler............
	if (document.getElementById('radio_reviewee').checked
			&& document.getElementById('radio_summary').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
		printEvaluationSummaryForm(submissionList, summaryList
				.sort(sortByTeamName), status, commentsEnabled, REVIEWEE);
	}

	// case 2: [x]reviewer [x]summary
	else if (document.getElementById('radio_reviewer').checked
			&& document.getElementById('radio_summary').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
		printEvaluationSummaryForm(submissionList, summaryList
				.sort(sortByTeamName), status, commentsEnabled, REVIEWER);
	}

	// case 3: [x]reviewee [x]detail
	else if (document.getElementById('radio_reviewee').checked
			&& document.getElementById('radio_detail').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
		printEvaluationDetailForm(submissionList, summaryList, status,
				commentsEnabled, REVIEWEE);
	}

	// case 4: [x]reviewer [x]detail
	else if (document.getElementById('radio_reviewer').checked
			&& document.getElementById('radio_detail').checked) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
		printEvaluationDetailForm(submissionList, summaryList, status,
				commentsEnabled, REVIEWER);
	}

	// else:
	else {
		// do nothing
	}

}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function sendRegistrationKey(courseID, email) {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEY
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ STUDENT_EMAIL + "=" + encodeURIComponent(email));

		return handleSendRegistrationKey();
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

function toggleDeleteAllStudentsConfirmation(courseID) {
	var s = confirm("Are you sure you want to remove all students from this course?");
	if (s == true) {
		doDeleteAllStudents(courseID);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_COURSE_INFORMATION).scrollIntoView(true);
}

function toggleDeleteStudentConfirmation(courseID, studentEmail, studentName) {
	var s = confirm("Are you sure you want to remove " + studentName
			+ " from the course?");
	if (s == true) {
		doDeleteStudent(courseID, studentEmail);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_COURSE_INFORMATION).scrollIntoView(true);
}

function toggleEvaluationSummaryListViewByType(submissionList, summaryList,
		status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
	} else {
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
	}

	toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList,
			status, commentsEnabled);
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

function toggleSendRegistrationKeysConfirmation(courseID) {
	var s = confirm("Are you sure you want to send registration keys to all the unregistered students for them to join your course?");
	if (s == true) {
		doSendRegistrationKeys(courseID);
		setStatusMessage("Emails have been sent to unregistered students.");
	} else {
		clearStatusMessage();
	}
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