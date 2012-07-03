// AJAX
var xmlhttp = new getXMLObject();

// DISPLAY
var DISPLAY_COURSE_DELETED = "The course has been deleted.";
var DISPLAY_COURSE_ARCHIVED = "The course has been archived.";
var DISPLAY_COURSE_UNARCHIVED = "The course has been unarchived.";
var DISPLAY_EVALUATION_DEADLINEPASSED = "The evaluation deadline has passed.";
var DISPLAY_EVALUATION_SUBMITTED = "The evaluation has been submitted.";
var DISPLAY_FIELDS_EMPTY = "<FONT color=\"red\">Please fill in all the relevant fields.</font>";
var DISPLAY_LOADING = "<img src=/images/ajax-loader.gif /><br />";

var DISPLAY_STUDENT_GOOGLEIDEXISTSINCOURSE = "<FONT color=\"red\">You are already registered in the course.</font>";
var DISPLAY_STUDENT_JOINEDCOURSE = "You have successfully joined the course.";
var DISPLAY_STUDENT_REGISTRATIONKEYINVALID = "<FONT color=\"red\">Registration key is invalid.</font>";
var DISPLAY_STUDENT_REGISTRATIONKEYTAKEN = "<FONT color=\"red\">Registration key has been taken by another student.</font>";

// DIV
var DIV_COURSE_INFORMATION = "studentCourseInformation";
var DIV_COURSE_MANAGEMENT = "studentCourseManagement";
var DIV_COURSE_TABLE = "studentCourseTable";
var DIV_EVALUATION_INFORMATION = "studentEvaluationInformation";
var DIV_EVALUATION_PAST = "studentPastEvaluations";
var DIV_EVALUATION_PENDING = "studentPendingEvaluations";
var DIV_EVALUATION_RESULTS = "studentEvaluationResults";
var DIV_EVALUATION_SUBMISSIONS = "studentEvaluationSubmissions";
var DIV_EVALUATION_SUBMISSIONBUTTONS = "studentEvaluationSubmissionButtons";
var DIV_HEADER_OPERATION = "headerOperation";
var DIV_STATUS = "statusMessage";
var DIV_TOPOFPAGE = "topOfPage";

// GLOBAL VARIABLES FOR GUI
var courseSort = { ID:0, name:1 }
var courseSortStatus = courseSort.name; 

var courseViewArchived = { show:0, hide:1 }
var courseViewArchivedStatus = courseViewArchived.hide; 

var evaluationSort = { courseID:0, name:1 }
var evaluationSortStatus = evaluationSort.courseID; 

// MESSAGES
var MSG_EVALUATION_DEADLINEPASSED = "evaluation deadline passed";

var MSG_STUDENT_COURSEJOINED = "course joined";
var MSG_STUDENT_GOOGLEIDEXISTSINCOURSE = "googleid exists in course";
var MSG_STUDENT_REGISTRATIONKEYINVALID = "registration key invalid";
var MSG_STUDENT_REGISTRATIONKEYTAKEN = "registration key taken";

// OPERATIONS
var OPERATION_STUDENT_ARCHIVECOURSE = "student_archivecourse";
var OPERATION_STUDENT_DELETECOURSE = "student_deletecourse";
var OPERATION_STUDENT_GETCOURSE = "student_getcourse";
var OPERATION_STUDENT_GETCOURSELIST = "student_getcourselist";
var OPERATION_STUDENT_GETPASTEVALUATIONLIST = "student_getpastevaluationlist";
var OPERATION_STUDENT_GETPASTEVALUATIONLISTOFCOURSE = "student_getpastevaluationlistcourse";
var OPERATION_STUDENT_GETPENDINGEVALUATIONLIST = "student_getpendingevaluationlist";
var OPERATION_STUDENT_GETPENDINGEVALUATIONLISTOFCOURSE = "student_getpendingevaluationlistofcourse";
var OPERATION_STUDENT_GETSUBMISSIONLIST = "student_getsubmissionlist";
var OPERATION_STUDENT_GETSUBMISSIONRESULTSLIST = "student_getsubmissionresultslist";
var OPERATION_STUDENT_JOINCOURSE = "student_joincourse";
var OPERATION_STUDENT_LOGOUT = "student_logout";
var OPERATION_STUDENT_SUBMITEVALUATION = "student_submitevaluation";
var OPERATION_STUDENT_UNARCHIVECOURSE = "student_unarchivecourse";

// PARAMETERS
var COURSE_COORDINATORNAME = "coordinatorname";
var COURSE_ID = "courseid";
var COURSE_NAME = "coursename";
var COURSE_STATUS = "coursestatus";

var EVALUATION_COMMENTSENABLED = "commentsstatus";
var EVALUATION_DEADLINE = "deadline";
var EVALUATION_DEADLINETIME = "deadlinetime";
var EVALUATION_GRACEPERIOD = "graceperiod";
var EVALUATION_INSTRUCTIONS = "instr";
var EVALUATION_NAME = "evaluationname";
var EVALUATION_PUBLISHED = "published";
var EVALUATION_START = "start";
var EVALUATION_STARTTIME = "starttime";
var EVALUATION_TIMEZONE = "timezone";

var STUDENT_COMMENTSTOSTUDENT = "commentstostudent";
var STUDENT_EMAIL = "email";
var STUDENT_FROMSTUDENT = "fromemail";
var STUDENT_FROMSTUDENTCOMMENTS = "fromstudentcomments";
var STUDENT_FROMSTUDENTNAME = "fromname";
var STUDENT_JUSTIFICATION = "justification";
var STUDENT_NAME = "name";
var STUDENT_NUMBEROFSUBMISSIONS = "numberofsubmissions";
var STUDENT_POINTS = "points";
var STUDENT_POINTSBUMPRATIO = "pointsbumpratio";
var STUDENT_REGKEY = "regkey";
var STUDENT_TEAMMATE = "teammate";
var STUDENT_TEAMMATES = "teammates";
var STUDENT_TEAMNAME = "teamname";
var STUDENT_TOSTUDENT = "toemail";
var STUDENT_TOSTUDENTCOMMENTS = "tostudentcomments";
var STUDENT_TOSTUDENTNAME = "toname";
/***********************************************************LANDING PAGE**********************************************************/
/*----------------------------------------------------------LANDING PAGE---------------------------------------------------------*/
/**
 * Student Click Home Tab
 * 
 * */
function displayHomeTab() {
	clearAllDisplay();
	
	setStatusMessageToLoading();
	printStudentLandingPage();
	clearStatusMessage();
	
	scrollToTop(DIV_TOPOFPAGE);
}

//----------------------------------------------------------LANDING PAGE FUNCTIONS
function printStudentLandingPage() {
	var outputHeader =
		"<h1>STUDENT HOME</h1>																								\
		<br />																												\
		<div class='result_team'>																							\
			<div class='result_addOrJoinCourse'>																			\
				<a href=\"javascript:displayCoursesTab()\" name='joinNewCourse' id='joinNewCourse'>Join New Course</a>		\
			</div>																											\
		</div>";

	var output = "<form method='post' action='' name='form_coursessummary'>";

	var courseID;
	var courseList = getCourseList();
	var courseListLength = courseList.length;
	
	var pendingEvaluationList = getPendingEvaluationList();
	pendingEvaluationList = pendingEvaluationList.sort(sortByName);
	var pendingEvaluationListLength = pendingEvaluationList.length;
	
	var pastEvaluationList = getPastEvaluationList();
	pastEvaluationList = pastEvaluationList.sort(sortByName);
	var pastEvaluationListLength = pastEvaluationList.length;

	for (var loop = 0; loop < courseListLength; loop++) {
		courseID = courseList[loop].ID;

		output = output +
		"<div class='result_team' id='course" + loop + "' name='course" + loop + "'>										\
			<div class='result_homeTitle'>																					\
				<h2>[" + courseID + "] : " + courseList[loop].name + "</h2>													\
			</div>																											\
			<div class='result_homeLinks'>																					\
				<a class='t_course_view" + loop + "' href=\"javascript:displayCourseInformation('" + courseID + "');		\
				hideddrivetip();\" onmouseover=\"ddrivetip('" + HOVER_MESSAGE_STUDENT_VIEW_COURSE + "')\"					\
				onmouseout=\"hideddrivetip()\">View</a>																		\
			</div>																											\
			<div style='clear: both;'></div>																				\
			<br />";

		var evaluationStatus;
		var evaluationsPresent = false;
		var evaluationOutput = "";
		
		for (var i = 0; i < pendingEvaluationListLength; i++) {
			if (pendingEvaluationList[i].courseID == courseID) {
				evaluationsPresent = true;
				
				evaluationStatus =
					"<td class='t_eval_status centeralign'><span 															\
					onmouseover=\"ddrivetip('The evaluation is yet to be completed by you')\" onmouseout=\"hideddrivetip()\">\
					" + "PENDING</span></td>";

				evaluationOutput = evaluationOutput +
				"<tr>																										\
					<td>" + encodeChar(pendingEvaluationList[i].name) + "</td>												\
					<td class='centeralign'>" + convertDateToDDMMYYYY(pendingEvaluationList[i].deadline) + " " + "			\
					" + convertDateToHHMM(pendingEvaluationList[i].deadline) + "H</td>										\
					" + evaluationStatus + "																				\
					<td class='centeralign'><a id='doEvaluation" + i + "' href=#											\
					onmouseover=\"ddrivetip('Start Evaluation')\" onmouseout=\"hideddrivetip()\">Do Evaluation</a></td>		\
				</tr>";
			}
		}
		
		// if link is disabled, insert this line to reset style and onclick:
		var disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";

		var now = new Date();
		for (var i = 0; i < pastEvaluationListLength; i++) {
			if (pastEvaluationList[i].courseID == courseID) {
				evaluationsPresent = true;
				
				evaluationOutput = evaluationOutput +
				"<tr>																										\
					<td>" + encodeChar(pastEvaluationList[i].name) + "</td>													\
					<td class='centeralign'>" + convertDateToDDMMYYYY(pastEvaluationList[i].deadline) + " " + "				\
					" + convertDateToHHMM(pastEvaluationList[i].deadline) + "H</td>";
	
				var hasView = false;
				var hasEdit = false;

				now = getDateWithTimeZoneOffset(pastEvaluationList[i].timeZone);
				if (pastEvaluationList[i].published == true) {
					evaluationStatus = "PUBLISHED";
					hasView = true;
					evaluationOutput = evaluationOutput +
					"<td class='centeralign t_eval_status t_eval_status_" + i + "'><span 									\
					onmouseover=\"ddrivetip('The evaluation has finished and you can check the results')\" 					\
					onmouseout=\"hideddrivetip()\">" + evaluationStatus + "</span></td>";
				}

				else if (now < pastEvaluationList[i].deadline) {
					evaluationStatus = "SUBMITTED";
					evaluationOutput = evaluationOutput +
					"<td class='centeralign t_eval_status t_eval_status_" + i + "'><span 									\
					onmouseover=\"ddrivetip('You have submitted your feedback for this evaluation')\" 						\
					onmouseout=\"hideddrivetip()\">" + evaluationStatus + "</span></td>";
				}

				else {
					evaluationStatus = "CLOSED";
					evaluationOutput = evaluationOutput +
					"<td class='centeralign t_eval_status t_eval_status_" + i + "'><span 									\
					onmouseover=\"ddrivetip('The evaluation has finished but the coordinator has not published the results yet')\"\
					onmouseout=\"hideddrivetip()\">" + evaluationStatus + "</span></td>";
				}

				evaluationOutput = evaluationOutput +
					"<td class='centeralign'>																				\
					<a href=# name='viewEvaluation" + i + "' id='viewEvaluation" + i + "' 									\
					onmouseover=\"ddrivetip('View evaluation results')\" onmouseout=\"hideddrivetip()\"						\
					" + (hasView ? "" : disabled) + ">View Results</a>";

				if (!(now > pastEvaluationList[i].deadline)) {
					hasEdit = true;
				}

				evaluationOutput = evaluationOutput +
					"<a href=# name='editEvaluation" + i + "' id='editEvaluation" + i + "' 									\
					onmouseover=\"ddrivetip('Edit evaluation')\" onmouseout=\"hideddrivetip()\"								\
					" + (hasEdit ? "" : disabled) + ">Edit</a>																\
					</td>																									\
				</tr>";
			}
		}
		
		if (evaluationsPresent) {
			output = output +
			"<table id='dataform'>																							\
				<tr>																										\
					<th class='leftalign'>EVALUATION NAME</th>																\
					<th class='centeralign'>DEADLINE</span></th>															\
					<th class='centeralign'>STATUS</th>																		\
					<th class='centeralign'>ACTION(S)</th>																	\
				</tr>																										\
				" + evaluationOutput + "																					\
			</table>																										\
			<br />";
		}

		output = output +
		"</div>																												\
		<br /><br /><br />";
	}

	output = output + "</form>";

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_PENDING).innerHTML = output;

	for (var loop = 0; loop < courseListLength; loop++) {
		courseID = courseList[loop].ID;
		
		for (var i = 0; i < pendingEvaluationListLength; i++) {
			if (pendingEvaluationList[i].courseID == courseID) {
				if (document.getElementById('doEvaluation' + i) != null
						&& document.getElementById('doEvaluation' + i).onclick == null) {
					document.getElementById('doEvaluation' + i).onclick = function() {
						displayEvaluationSubmission(pendingEvaluationList, this.id.substring(
								12, this.id.length));
					};
				}
			}
		}
		
		for (var i = 0; i < pastEvaluationListLength; i++) {
			if (pastEvaluationList[i].courseID == courseID) {
				if (document.getElementById('editEvaluation' + i) != null
						&& document.getElementById('editEvaluation' + i).onclick == null) {
					document.getElementById('editEvaluation' + i).onclick = function() {
						hideddrivetip();
						displayEvaluationSubmission(pastEvaluationList, this.id.substring(
								14, this.id.length));
					};
				}
	
				if (document.getElementById('viewEvaluation' + i) != null
						&& document.getElementById('viewEvaluation' + i).onclick == null) {
					document.getElementById('viewEvaluation' + i).onclick = function() {
						hideddrivetip();
						displayEvaluationResults(pastEvaluationList, this.id.substring(14,
								this.id.length));
					};
				}
			}
		}
	}
}

/***********************************************************______ PAGE***********************************************************/
/*----------------------------------------------------------______ PAGE----------------------------------------------------------*/

/**
 * XMLHttpRequest Constants
 * 
 * */
var SERVERERROR = 1;
var CONNECTION_OK = 200;

/*----------------------------------------------------------EVALUATION PAGE----------------------------------------------------------*/
/***
 * page assemply
 * 
 * @page: Evaluations
 */
function displayEvaluationsTab()
{
	clearAllDisplay();
	setStatusMessage(DISPLAY_LOADING);
	doGetPendingEvaluationList();
	doGetPastEvaluationList();
	clearStatusMessage();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

/*----------------------------------------------------------EVALUATION RESULTS PAGE----------------------------------------------------------*/
/***
 * page assembly
 * 
 * @page: EvaluationResults
 */
function displayEvaluationResults(evaluationList, loop)
{
	clearAllDisplay();

	var courseID = evaluationList[loop].courseID;
	var evaluationName =  evaluationList[loop].name;
	var start =  evaluationList[loop].start;
	var deadline =  evaluationList[loop].deadline;
	doGetSubmissionResultsList(courseID, evaluationName, start, deadline);
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function doGetSubmissionResultsList(courseID, evaluationName, start, deadline)
{
	setStatusMessageToLoading();
	
	if(!xmlhttp) {
		alert(DISPLAY_ERROR_UNDEFINED_HTTPREQUEST);
		return;
	}
	
	sendGetSubmissionResultsListRequest(courseID, evaluationName);
	var results = processGetSubmissionResultsListResponse(); 
	
	clearStatusMessage();
	
	if(results == SERVERERROR){
		alert(DISPLAY_SERVERERROR);
		return;
	}
	
	var submissionList = complieStudentSubmissionList(results);
	var summaryList = compileStudentSubmissionSummaryList(submissionList);
	printEvaluationResultStudentForm(summaryList, submissionList, start, deadline);
}

function sendGetSubmissionResultsListRequest(courseID, evaluationName)
{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETSUBMISSIONRESULTSLIST 
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) 
				+ "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName)); 
}

function processGetSubmissionResultsListResponse()
{
	if(xmlhttp.status != CONNECTION_OK)
		return SERVERERROR;

	var submissions = xmlhttp.responseXML.getElementsByTagName("submissions")[0];

	return submissions;
}

function complieStudentSubmissionList(submissions){
	var submissionList = new Array();
	var submission;
	
	var fromStudentName;
	var toStudentName;
	var fromStudent;
	var toStudent;
	var fromStudentComments;
	var toStudentComments;
	var courseID;
	var evaluationName;
	var teamName;
	var points;
	var pointsBumpRatio;
	var justification;
	var commentsToStudent;
	
	if(submissions == null)
		return submissionList;
	
	var submissionsChildNodesLength = submissions.childNodes.length;
	for ( var loop = 0; loop < submissionsChildNodesLength; loop++) {
		submission = submissions.childNodes[loop];
		
		fromStudentName = submission.getElementsByTagName(STUDENT_FROMSTUDENTNAME)[0].firstChild.nodeValue;
		fromStudent = submission.getElementsByTagName(STUDENT_FROMSTUDENT)[0].firstChild.nodeValue;
		
		toStudentName = submission.getElementsByTagName(STUDENT_TOSTUDENTNAME)[0].firstChild.nodeValue;
		toStudent = submission.getElementsByTagName(STUDENT_TOSTUDENT)[0].firstChild.nodeValue;
		
		fromStudentComments = submission.getElementsByTagName(STUDENT_FROMSTUDENTCOMMENTS)[0].firstChild.nodeValue;
		toStudentComments = submission.getElementsByTagName(STUDENT_TOSTUDENTCOMMENTS)[0].firstChild.nodeValue;
		
		courseID = submission.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
		evaluationName = submission.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
		teamName = submission.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
		points = parseInt(submission.getElementsByTagName(STUDENT_POINTS)[0].firstChild.nodeValue);
		pointsBumpRatio = parseFloat(submission.getElementsByTagName(STUDENT_POINTSBUMPRATIO)[0].firstChild.nodeValue);
		justification = submission.getElementsByTagName(STUDENT_JUSTIFICATION)[0].firstChild.nodeValue;
		commentsToStudent = submission.getElementsByTagName(STUDENT_COMMENTSTOSTUDENT)[0].firstChild.nodeValue;

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
//	logSubmissionList(submissionList);
	return submissionList;
}

function compileStudentSubmissionSummaryList(submissionList){
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
		return points;
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

/*
 * Student view evaluation results
 */
function printEvaluationResultStudentForm(summaryList, submissionList, start,
		deadline) {

	logSummaryList(summaryList);
	var claimedPoints = summaryList[0].claimedPoints;
	var perceivedPoints = summaryList[0].average;

	if (isNaN(perceivedPoints))
		perceivedPoints = "N/A";

	var output = "<br />" + "<div>" + "<h1>Evaluation Results</h1>" + "</div>"
			+ "<table class=\"result_studentform\">" + "<tr>"
			+ "<td width=\"20%\">"
			+ STUDENT
			+ "</td>"
			+ "<td width=\"30%\">"
			+ summaryList[0].toStudentName
			+ "</td>"
			+ "<td width=\"20%\">"
			+ COURSE
			+ "</td>"
			+ "<td width=\"30%\">"
			+ submissionList[0].courseID
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ TEAM
			+ "</td>"
			+ "<td>"
			+ encodeCharForPrint(submissionList[0].teamName)
			+ "</td>"
			+ "<td>"
			+ EVALUATION
			+ "</td>"
			+ "<td>"
			+ submissionList[0].evaluationName
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ "<span onmouseover=\"ddrivetip('Claimed contribution is what you claimed you contributed to the project')\" onmouseout=\"hideddrivetip()\">"
			+ CLAIMED
			+ "</span>"
			+ "</td>"
			+ "<td>"
			+ displayEvaluationPoints(claimedPoints)
			+ "</td>"
			+ "<td>"
			+ OPENING
			+ "</td>"
			+ "<td>"
			+ start
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ "<span onmouseover=\"ddrivetip('Perceived contribution is the average of what the other team members think you contributed to the project')\" onmouseout=\"hideddrivetip()\">"
			+ PERCEIVED
			+ "</span></td>"
			+ "<td>"
			+ displayEvaluationPoints(perceivedPoints)
			+ "</td>"
			+ "<td>"
			+ CLOSING
			+ "</td>"
			+ "<td>"
			+ deadline
			+ "</td>"
			+ "</tr>"
			+ "</table>"
			+ "<table class = \"result_table\" id = \"\">"
			+ "<tr class = \"result_subheader\">"
			+ "<td>"
			+ FEEDBACK_FROM
			+ "</td>" + "</tr>";

	submissionList.splice(0, 1);

	var targetStudent = summaryList[0].toStudent;
	var ctr = 0;
	var submissionListLength = submissionList.length;
	for (loop = 0; loop < submissionListLength; loop++) {
		var sp = loop + 1;
		if (submissionList[loop].toStudent == targetStudent
				&& submissionList[loop].fromStudent != targetStudent) {
			if (encodeChar(submissionList[loop].commentsToStudent) == "") {
				output = output + "<tr><td>" + NA + "</td></tr>";
			} else {
				output = output
						+ "<tr><td id=\"com"
						+ ctr
						+ "\">"
						+ encodeCharForPrint(submissionList[loop].commentsToStudent)
						+ "</td></tr>";
			}
		}
	}

	output = output
			+ "</tr></table><br /><br />"
			+ "<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />"
			+ "<br /><br />";

	document.getElementById(DIV_EVALUATION_RESULTS).innerHTML = output;
}

/*----------------------------------------------------------TODO: REFACTOR OLD FUNCTIONS----------------------------------------------------------*/

/*
 * Returns
 * 
 * 0: successful
 * 1: server error
 *
 */
function archiveCourse(courseID)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_ARCHIVECOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	
		return handleArchiveCourse();
	}
}

function clearAllDisplay()
{
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = "";
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = "";
	document.getElementById(DIV_COURSE_TABLE).innerHTML = "";
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = "";
	document.getElementById(DIV_EVALUATION_PAST).innerHTML = "";
	document.getElementById(DIV_EVALUATION_PENDING).innerHTML = "";
	document.getElementById(DIV_EVALUATION_RESULTS).innerHTML = ""; 
	document.getElementById(DIV_EVALUATION_SUBMISSIONBUTTONS).innerHTML = ""; 
	document.getElementById(DIV_EVALUATION_SUBMISSIONS).innerHTML = ""; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = "";
	document.getElementById(DIV_STATUS).innerHTML = ""; 
	document.getElementById(DIV_TOPOFPAGE).innerHTML = "";
}

function convertDateToDDMMYYYY(date)
{
	var string;
	
	if(date.getDate() < 10)
	{
		string = "0" + date.getDate();
	}
	
	else
	{
		string = date.getDate();
	}
	
	string = string + "/";
	
	if(date.getMonth()+1 < 10)
	{
		string = string + "0" + (date.getMonth()+1);
	}
	
	else
	{
		string = string + (date.getMonth()+1);
	}
	
	string = string + "/" + date.getFullYear();
	
	return string;
}

function convertDateToHHMM(date)
{
	var string;
	
	if(date.getHours() < 10)
	{
		string = "0" + date.getHours();
	}
	
	else
	{
		string = "" + date.getHours();
	}
	
	if(date.getMinutes() < 10)
	{
		string = string + "0" + date.getMinutes();
	}
	
	else
	{
		string = string + date.getMinutes();
	}
	
	return string;
}

function displayCourseInformation(courseID)
{
	clearAllDisplay();
	doGetCourse(courseID);
}

function displayCoursesTab()
{
	clearAllDisplay();
	setStatusMessage(DISPLAY_LOADING);
	printJoinCourse();
	doGetCourseList();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	
}

function displayEvaluationSubmission(evaluationList, loop)
{
	var courseID = evaluationList[loop].courseID;
	var courseName = evaluationList[loop].courseName;
	var evaluationName =  evaluationList[loop].name;
	var instructions =  evaluationList[loop].instructions;
	var start =  evaluationList[loop].start;
	var deadline =  evaluationList[loop].deadline;
	var gracePeriod =  evaluationList[loop].gracePeriod;
	var commentsEnabled =  evaluationList[loop].commentsEnabled;

	clearAllDisplay();
	
	printEvaluationHeader(courseID, evaluationName, start, deadline, gracePeriod, instructions, commentsEnabled);
	doGetSubmissionList(courseID, evaluationName, commentsEnabled);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function doArchiveCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = archiveCourse(courseID);
	
	if(results == 0)
	{
		doGetCourseList();
		setStatusMessage(DISPLAY_COURSE_ARCHIVED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetCompletedEvaluation(courseID, name)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getEvaluation(courseID, name);
}

function doGetCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getCourse(courseID);
	
	if(results != 1)
	{
		printCourseStudentForm(results);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetCourseList()
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getCourseList();
	
	clearStatusMessage();
	
	if(results != 1)
	{
		// toggleSortCoursesByID calls printCourseList too
		printCourseList(results, STUDENT);
		
		if(courseSortStatus == courseSort.name)
		{
			toggleSortCoursesByName(results);
		}
		
		else
		{
			toggleSortCoursesByID(results);
		}
		
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetPastEvaluationList()
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getPastEvaluationList();
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printPastEvaluationList(results);
		
		// Toggle calls printPastEvaluationList too
		if(evaluationSortStatus == evaluationSort.name)
		{
			toggleSortPastEvaluationsByName(results);
		}	

		else
		{
			toggleSortPastEvaluationsByCourseID(results);
		}
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}
function doGetPastEvaluationListOfCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getPastEvaluationListOfCourse(courseID);
	
	clearStatusMessage();
	
	if(results != 1)
	{
		return results;
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetPendingEvaluationList()
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getPendingEvaluationList();
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printPendingEvaluationList(results);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}
/*returns
* evaluationList: successful
 * 1: server error
 * 
 */
function getPendingEvaluationListOfCourse(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETPENDINGEVALUATIONLISTOFCOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
		
		return handleGetEvaluationList();
	}
}


function doGetPendingEvaluationListOfCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getPendingEvaluationListOfCourse(courseID);
	
	clearStatusMessage();
	
	if(results != 1)
	{
		return results;
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetSubmissionList(courseID, evaluationName, commentsEnabled)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getSubmissionList(courseID, evaluationName); 
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printSubmissionForm(results, commentsEnabled);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doJoinCourse(registrationKey)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = joinCourse(registrationKey);
	
	displayCoursesTab();
	
	if(results == 0)
	{
		setStatusMessage(DISPLAY_STUDENT_JOINEDCOURSE);
	}
	
	else if(results == 1)
	{
		alert(DISPLAY_SERVERERROR);
	}
	
	else if(results == 2)
	{
		setStatusMessage(DISPLAY_STUDENT_GOOGLEIDEXISTSINCOURSE);
	}
	
	else if(results == 3)
	{
		setStatusMessage(DISPLAY_STUDENT_REGISTRATIONKEYINVALID);
	}
	
	else if(results == 4)
	{
		setStatusMessage(DISPLAY_STUDENT_REGISTRATIONKEYTAKEN);
	}
}

function doLeaveCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = leaveCourse(courseID);
	
	if(results == 0)
	{
		doGetCourseList();
		setStatusMessage(DISPLAY_COURSE_DELETED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doSubmitEvaluation(form, length, commentsEnabled)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var submissionList = extractSubmissionList(form, length);
	var results = submitEvaluation(submissionList, commentsEnabled);
	
	if(results == 0)
	{
		displayEvaluationsTab();
		setStatusMessage(DISPLAY_EVALUATION_SUBMITTED);
	}
	
	else if(results == 2)
	{
		displayEvaluationsTab();
		setStatusMessage(DISPLAY_EVALUATION_DEADLINEPASSED);
	}
	
	else if(results == 3)
	{
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}
	
	else 
	{
		alert(DISPLAY_SERVERERROR);
	}

}

function doUnarchiveCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = unarchiveCourse(courseID);
	
	if(results == 0)
	{
		doGetCourseList();
		setStatusMessage(DISPLAY_COURSE_UNARCHIVED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function extractSubmissionList(form, length)
{
	var submissionList = [];
	
	var fromStudent;
	var toStudent;
	var courseID;
	var evaluationName;
	var teamName;
	var points;
	var justification;
	var commentsToStudent;

	var counter = 0;
	
	for(loop = 0; loop < length*8; loop++)
	{
		
		fromStudent = form.elements[loop++].value;
		toStudent = form.elements[loop++].value;
		courseID = form.elements[loop++].value;
		evaluationName = form.elements[loop++].value;
		teamName = form.elements[loop++].value;
		points = form.elements[loop++].value;

		if(form.elements[loop].disabled == false)
		{
			justification = form.elements[loop++].value;
		}
		
		else
		{
			justification = "";
			loop++;
		}
		
		if(form.elements[loop].disabled == false)
		{
			commentsToStudent = form.elements[loop].value;
		}
		
		else
		{
			commentsToStudent = "";
		}
		
		
		
		submissionList[counter++] = {fromStudent:fromStudent, toStudent:toStudent, courseID:courseID,
				evaluationName:evaluationName, teamName:teamName, points:points,
				justification:justification, commentsToStudent:commentsToStudent};
		
	}
	
	return submissionList;
}

/*
 * Returns
 * 
 * course: successful
 * 1: server error
 *
 */
function getCourse(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETCOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID)); 
		
		return handleGetCourse();
	}
}

/*
 * Returns
 * 
 * courseList: successful
 * 1: server error
 *
 */
function getCourseList()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETCOURSELIST); 
		
		return handleGetCourseList();
	}
}

function getDateWithTimeZoneOffset(timeZone)
{
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

/*
 * Returns
 * 
 * evaluationList: successful
 * 1: server error
 * 
 */
function getPastEvaluationList()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETPASTEVALUATIONLIST);
		
		return handleGetEvaluationList();
	}
}
function getPastEvaluationListOfCourse(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETPASTEVALUATIONLISTOFCOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
		
		return handleGetEvaluationList();
	}
}

/*
 * Returns
 * 
 * evaluationList: successful
 * 1: server error
 * 
 */
function getPendingEvaluationList()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETPENDINGEVALUATIONLIST);
		
		return handleGetEvaluationList();
	}
}

/*
 * Returns
 * 
 * submissionList: successful
 * 1: server error
 * 
 */
function getSubmissionList(courseID, evaluationName)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETSUBMISSIONLIST + "&" + COURSE_ID + "=" +
				encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName)); 
		
		return handleGetSubmissionList();
	}
}


function getXMLObject()  
{
	var xmlHttp = false;
	
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")  
	}
	catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")  
		}
		catch (e2) {
			xmlHttp = false  
		}
	}
	if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
		xmlHttp = new XMLHttpRequest();        
	}
	
	return xmlHttp; 
}

/*
 * Returns
 * 
 * 0: successful
 * 1: server error
 *
 */
function handleArchiveCourse()
{
	if(xmlhttp.status == 200) 
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
 * course: successful
 * 1: server error
 *
 */
function handleGetCourse()
{
	if (xmlhttp.status == 200) 
	{
		clearStatusMessage();
		
		var course = xmlhttp.responseXML.getElementsByTagName("coursedetails")[0];
		
		var courseID = course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
		var courseName = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
		var coordinatorName = course.getElementsByTagName(COURSE_COORDINATORNAME)[0].firstChild.nodeValue;
		var studentTeamName = course.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
		var studentName = course.getElementsByTagName(STUDENT_NAME)[0].firstChild.nodeValue;
		var studentEmail = course.getElementsByTagName(STUDENT_EMAIL)[0].firstChild.nodeValue;
		
		var teammates = course.getElementsByTagName(STUDENT_TEAMMATES)[0];
		var teammateList = new Array();
		
		var teammatesChildNodesLength = teammates.childNodes.length;
		for(var x = 0; x < teammatesChildNodesLength; x++)
		{
			teammateList[x] = teammates.getElementsByTagName(STUDENT_TEAMMATE)[x].firstChild.nodeValue;
		}
		
		var course = {courseID:courseID, courseName:courseName, coordinatorName:coordinatorName, 
				studentTeamName:studentTeamName, studentName:studentName, studentEmail:studentEmail, teammateList:teammateList};
		
		return course;
			
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * courseList: successful
 * 1: server error
 * 
 */
function handleGetCourseList()
{
	if (xmlhttp.status == 200) 
	{
		var courses = xmlhttp.responseXML.getElementsByTagName("courses")[0]; 
		var courseList = new Array(); 
		
		
		if(courses != null) 
		{ 
			var course; 
			var id; 
			var name; 
			var teamName;
			var status;
			
			var coursesChildNodesLength = courses.childNodes.length;
			for(loop = 0; loop < coursesChildNodesLength; loop++) 
			{ 
				course = courses.childNodes[loop]; 
				ID =  course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				name = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
				teamName = course.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
				status = course.getElementsByTagName(COURSE_STATUS)[0].firstChild.nodeValue;

				courseList[loop] = {ID:ID, name:name, teamName:teamName, status:status}; 
			}
		}
		
		return courseList;
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * evaluationList: successful
 * 1: server error
 * 
 */
function handleGetEvaluationList()
{
	if(xmlhttp.status == 200)
	{
		var evaluations = xmlhttp.responseXML.getElementsByTagName("evaluations")[0];
		
		var evaluationList = [];
		var evaluation;
		var courseID;
		var name;
		var startString;
		var start;
		var deadlineString;
		var deadline;
		var timeZone;
		var gracePeriod;
		var instructions;
		var commentsEnabled;
		var published;
		
		var evaluationsChildNodesLength = evaluations.childNodes.length;
		for(loop = 0; loop < evaluationsChildNodesLength; loop++)
		{
			evaluation = evaluations.childNodes[loop];
			courseID = evaluation.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
			name = evaluation.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
			startString = evaluation.getElementsByTagName(EVALUATION_START)[0].firstChild.nodeValue;
			deadlineString = evaluation.getElementsByTagName(EVALUATION_DEADLINE)[0].firstChild.nodeValue;
			timeZone = evaluation.getElementsByTagName(EVALUATION_TIMEZONE)[0].firstChild.nodeValue;
			gracePeriod = evaluation.getElementsByTagName(EVALUATION_GRACEPERIOD)[0].firstChild.nodeValue;
			instructions = evaluation.getElementsByTagName(EVALUATION_INSTRUCTIONS)[0].firstChild.nodeValue;
			commentsEnabled = (evaluation.getElementsByTagName(EVALUATION_COMMENTSENABLED)[0].firstChild.nodeValue == "true");
			published = (evaluation.getElementsByTagName(EVALUATION_PUBLISHED)[0].firstChild.nodeValue == "true");
			
			deadline = new Date(deadlineString);
			start = new Date(startString);

			evaluationList[loop] = {courseID:courseID, name:name, start:start, deadline:deadline, timeZone:timeZone,
					gracePeriod:gracePeriod, instructions:instructions, commentsEnabled:commentsEnabled,
					published:published};
		}

		return evaluationList;
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * submissionList: successful
 * 1: server error
 * 
 */
function handleGetSubmissionList()
{
	if(xmlhttp.status == 200)
	{
		var submissions = xmlhttp.responseXML.getElementsByTagName("submissions")[0];
		var submissionList = new Array();
		var submission;
		
		var fromStudentName;
		var toStudentName;
		var fromStudent;
		var toStudent;
		var courseID;
		var evaluationName;
		var teamName;
		var points;
		var justification;
		var commentsToStudent;
		
		if(submissions != null)
		{
			var submissionsChildNodesLength = submissions.childNodes.length;
			for(loop = 0; loop < submissionsChildNodesLength; loop++)
			{
				submission = submissions.childNodes[loop];
				fromStudentName = submission.getElementsByTagName(STUDENT_FROMSTUDENTNAME)[0].firstChild.nodeValue;
				fromStudent = submission.getElementsByTagName(STUDENT_FROMSTUDENT)[0].firstChild.nodeValue;
				toStudentName = submission.getElementsByTagName(STUDENT_TOSTUDENTNAME)[0].firstChild.nodeValue;
				toStudent = submission.getElementsByTagName(STUDENT_TOSTUDENT)[0].firstChild.nodeValue;
				courseID = submission.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				evaluationName = submission.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
				teamName = submission.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
				points = parseInt(submission.getElementsByTagName(STUDENT_POINTS)[0].firstChild.nodeValue);
				justification = submission.getElementsByTagName(STUDENT_JUSTIFICATION)[0].firstChild.nodeValue;
				commentsToStudent = submission.getElementsByTagName(STUDENT_COMMENTSTOSTUDENT)[0].firstChild.nodeValue;
					

				submissionList[loop] = {fromStudentName:fromStudentName, toStudentName:toStudentName, 
						fromStudent:fromStudent, toStudent:toStudent, courseID:courseID,
						evaluationName:evaluationName, teamName:teamName, justification:justification,
						commentsToStudent:commentsToStudent, points:points}; 
			}
		}
		
		return submissionList;
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * 0: successful
 * 1: server error
 * 2: google ID already exists in course
 * 3: registration key invalid
 * 4: registration key taken
 * 
 */
function handleJoinCourse()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			
			if(message == MSG_STUDENT_COURSEJOINED)
			{
				return 0;
			}
			
			else if(message == MSG_STUDENT_GOOGLEIDEXISTSINCOURSE)
			{
				return 2;
			}
			
			else if(message == MSG_STUDENT_REGISTRATIONKEYINVALID)
			{
				return 3;
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
 * 0: successful
 * 1: server error
 *
 */
function handleLeaveCourse()
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

function handleLogout()
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
 * 0: successful
 * 1: server error 
 * 2: deadline passed
 * 
 */
function handleSubmitEvaluation()
{
	if(xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		var message;

		if(status != null)
		{

			message = status.firstChild.nodeValue;

			if(message == MSG_EVALUATION_DEADLINEPASSED)
			{
				return 2;
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
 * 0: successful
 * 1: server error
 *
 */
function handleUnarchiveCourse()
{
	if(xmlhttp.status == 200) 
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
 * 0: successful
 * 1: server error
 * 2: google ID already exists in course
 * 3: registration key invalid
 * 
 */
function joinCourse(registrationKey)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_JOINCOURSE + "&" + STUDENT_REGKEY + "=" + 
				encodeURIComponent(registrationKey));
	
		return handleJoinCourse();
	}
}

/*
 * Returns
 * 
 * 0: successful
 * 1: server error
 *
 */
function leaveCourse(courseID)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_DELETECOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	
		return handleLeaveCourse();
	}
}

function logout()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_LOGOUT);
	}
	
	handleLogout();
}

function populateEvaluationSubmissionForm(form, submissionList, commentsEnabled)
{
	var counter = 0;
	
	var submissionListLength = submissionList.length;
	for(loop = 0; loop < submissionListLength; loop++)
	{
		counter += 5;
		
		form.elements[counter++].value = parseInt(submissionList[loop].points);
		form.elements[counter++].value = submissionList[loop].justification;
		
		if(commentsEnabled == true)
		{
			form.elements[counter++].value = submissionList[loop].commentsToStudent;
		}
		
		else
		{
			counter += 1;
		}
	}
}



function sortByCourseID(a, b) 
{
    var x = a.courseID.toLowerCase();
    var y = b.courseID.toLowerCase();

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByCourseName(a, b) 
{
    var x = a.name.toLowerCase();
    var y = b.name.toLowerCase();

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByID(a, b) 
{
    var x = a.ID.toLowerCase();
    var y = b.ID.toLowerCase();

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}


function sortByName(a, b) 
{
    var x = a.name.toLowerCase();
    var y = b.name.toLowerCase();

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

/*
 * Returns
 * 
 * 0: successful
 * 1: server error 
 * 2: deadline passed
 * 3: fields missing
 * 4: fields too long
 * 
 */
function submitEvaluation(submissionList, commentsEnabled)
{
	var submissionListLength = submissionList.length;
	for(loop = 0; loop < submissionListLength; loop++)
	{
		if(submissionList[loop].justification == "" || (submissionList[loop].commentsToStudent == "" &&
				commentsEnabled == true))
		{
			return 3;
		}
		
		if(submissionList[loop].justification.length > 50000 || submissionList[loop].commentsToStudent.length > 50000)
		{
			return 4;
		}
	}
	
	var request = "operation=" + OPERATION_STUDENT_SUBMITEVALUATION + "&" + STUDENT_NUMBEROFSUBMISSIONS +
				  "=" + submissionListLength + "&" + COURSE_ID + "=" + submissionList[0].courseID +
				  "&" + EVALUATION_NAME + "=" + submissionList[0].evaluationName +
				  "&" + STUDENT_TEAMNAME + "=" + submissionList[0].teamName;
	
	for(loop = 0; loop < submissionListLength; loop++)
	{
		request = request + "&" + STUDENT_FROMSTUDENT +  loop + "=" + 
				  encodeURIComponent(submissionList[loop].fromStudent) + "&" +
				  STUDENT_TOSTUDENT + loop + "=" +
				  encodeURIComponent(submissionList[loop].toStudent) + "&" +
				  STUDENT_POINTS + loop + "=" +
				  encodeURIComponent(submissionList[loop].points) + "&" +
				  STUDENT_JUSTIFICATION + loop + "=" +
				  encodeURIComponent(submissionList[loop].justification) + "&" +
				  STUDENT_COMMENTSTOSTUDENT + loop + "=" +
				  encodeURIComponent(submissionList[loop].commentsToStudent);
	}
	
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send(request); 
		
	}
	
	return handleSubmitEvaluation();
	
}

function toggleLeaveCourseConfirmation(courseID) {
	var s = confirm("Are you sure you want to leave the course " + courseID + "?");
	if (s == true) {
		doLeaveCourse(courseID);
	} else {
		clearStatusMessage();
	}
	
	document.getElementById(DIV_STATUS).innerHTML = output; 
	document.getElementById(DIV_COURSE_MANAGEMENT).scrollIntoView(true);
}

function toggleSortCoursesByID(courseList) {
	printCourseList(courseList.sort(sortByID), STUDENT);
	courseSortStatus = courseSort.ID;
	document.getElementById("button_sortcourseid").setAttribute("class", "buttonSortAscending");
}

function toggleSortCoursesByName(courseList) {
	printCourseList(courseList.sort(sortByCourseName), STUDENT);
	courseSortStatus = courseSort.name;
	document.getElementById("button_sortcoursename").setAttribute("class", "buttonSortAscending");
}

function toggleSortPastEvaluationsByCourseID(evaluationList) {
	printPastEvaluationList(evaluationList.sort(sortByCourseID));
	evaluationSortStatus = evaluationSort.courseID;
	document.getElementById("button_sortcourseid").setAttribute("class", "buttonSortAscending");
}

function toggleSortPastEvaluationsByName(evaluationList) {
	printPastEvaluationList(evaluationList.sort(sortByName));
	evaluationSortStatus = evaluationSort.name;
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");		
}



/*
 * Returns
 * 
 * 0: successful
 * 1: server error
 *
 */
function unarchiveCourse(courseID) {
	if (xmlhttp) {		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_UNARCHIVECOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	
		return handleUnarchiveCourse();
	}
}

window.onload = function() {
	initializetooltip();
	displayHomeTab();
}

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip
