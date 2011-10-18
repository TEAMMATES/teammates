// AJAX
var xmlhttp = new getXMLObject();

// DISPLAY
var DISPLAY_COURSE_DELETED = "The course has been deleted.";
var DISPLAY_COURSE_ARCHIVED = "The course has been archived.";
var DISPLAY_COURSE_UNARCHIVED = "The course has been unarchived.";
var DISPLAY_EVALUATION_DEADLINEPASSED = "The evaluation deadline has passed.";
var DISPLAY_EVALUATION_SUBMITTED = "The evaluation has been submitted.";
var DISPLAY_FIELDS_EMPTY = "<FONT color=\"red\">Please fill in all the relevant fields.</font>";
var DISPLAY_LOADING = "<img src=images/ajax-loader.gif /><br />";
var DISPLAY_SERVERERROR = "Connection to the server has timed out. Please refresh the page.";
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
var OPERATION_STUDENT_GETPENDINGEVALUATIONLIST = "student_getpendingevaluationlist";
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

function compileSubmissionsIntoSummaryList(submissionList)
{
	var summaryList = new Array();
	
	var exists = false;
	
	var toStudent;
	var toStudentName;
	var toStudentComments;
	var totalPoints;
	var totalPointGivers;
	var claimedPoints;
	var teamName;
	var average;
	var difference;
	var submitted;
	var pointsBumpRatio;
	
	var count = 0;
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		exists = false;
		submitted = false;
		
		for(x = 0; x < summaryList.length; x++)
		{
			if(summaryList[x].toStudent == submissionList[loop].toStudent)
			{
				exists = true;
			}
		}
		
		if(exists == false)
		{
			toStudent = submissionList[loop].toStudent;
			toStudentName = submissionList[loop].toStudentName;
			toStudentComments = submissionList[loop].toStudentComments;
			teamName = submissionList[loop].teamName;
			totalPoints = 0;
			totalPointGivers = 0;
			
			for(y = loop; y < submissionList.length; y++)
			{
				if(submissionList[y].toStudent == toStudent)
				{
					if(submissionList[y].fromStudent == toStudent)
					{
						if(submissionList[y].points == -999 || submissionList[y].points == -101)
						{
							claimedPoints = "N/A";
						}
						
						else
						{
							// Should not alter student's own claimed points for student's view
							claimedPoints = submissionList[y].points;
						}
						
						if(submissionList[y].points != -999)
						{
							submitted = true;
						}
					}
					
					else
					{
						if(submissionList[y].points != -999 && submissionList[y].points != -101)
						{
							totalPoints += Math.round(submissionList[y].points * submissionList[y].pointsBumpRatio);
							totalPointGivers++;
						}
					}
				}
			}
			
			if(totalPointGivers != 0)
			{
				average = Math.round(totalPoints / totalPointGivers);
			}
			
			else
			{
				average = "N/A";
			}
			
			if(claimedPoints != "N/A" && average != "N/A")
			{
				difference = Math.round(average-claimedPoints);
			}
			
			else
			{
				difference = "N/A";
			}
			
			summaryList[count++] = { toStudent:toStudent, toStudentName:toStudentName, teamName:teamName,
					average:average, difference:difference, toStudentComments:toStudentComments, submitted:submitted,
					claimedPoints:claimedPoints};
		}
	}
	
	// Find normalizing points bump ratio for averages
	var teamsNormalized = new Array();
	count = 0;
	
	for(loop = 0; loop < summaryList.length; loop++)
	{
		// Reset variables
		exists = false;
		totalPoints = 0;
		totalGivers = 0;
		pointsBumpRatio = 0;
		
		// Check if the team is added
		for(y = 0; y < teamsNormalized.length; y++)
		{
			if(summaryList[loop].teamName == teamsNormalized[y].teamName)
			{
				exists = true;
				break;
			}
		}
		
		if(exists == false)
		{
			// Tabulate the perceived scores
			for(y = loop; y < summaryList.length; y++)
			{
				if(summaryList[y].teamName == summaryList[loop].teamName && summaryList[y].average != "N/A")
				{
					totalPoints += summaryList[y].average;
					totalGivers += 1;
				}
			}
			
			if(totalGivers != 0)
			{
				pointsBumpRatio = totalGivers * 100 / totalPoints; 
				
				// Store the bump ratio
				teamsNormalized[count++] = {pointsBumpRatio:pointsBumpRatio, teamName:teamName};
			}
	
		}
		
	}
	
	// Do the normalization
	for(loop = 0; loop < teamsNormalized.length; loop++)
	{
		for(y = 0; y < summaryList.length; y++)
		{
			if(summaryList[y].teamName == teamsNormalized[loop].teamName && summaryList[y].average != "N/A")
			{
				summaryList[y].average = Math.round(summaryList[y].average * teamsNormalized[loop].pointsBumpRatio);

				if(summaryList[y].claimedPoints != "N/A")
				{
					summaryList[y].difference = Math.round(summaryList[y].average-summaryList[y].claimedPoints);
				}
				
				else
				{
					summaryList[y].difference = "N/A";
				}
			}
		}
	}
	
	return summaryList;
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

function displayEvaluationsTab()
{
	clearAllDisplay();
	setStatusMessage(DISPLAY_LOADING);
	doGetPendingEvaluationList();
	doGetPastEvaluationList();
	clearStatusMessage();
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
	
	printEvaluation(courseID, evaluationName, start, deadline, gracePeriod, instructions, commentsEnabled);
	doGetSubmissionList(courseID, evaluationName, commentsEnabled);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEvaluationResults(evaluationList, loop)
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
	
	printEvaluation(courseID, evaluationName, start, deadline, gracePeriod, instructions, commentsEnabled);
	doGetSubmissionResultsList(courseID, evaluationName);
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
		printCourse(results);
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
		printCourseList(results);
		
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

function doGetSubmissionList(courseID, evaluationName, commentsEnabled)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getSubmissionList(courseID, evaluationName); 
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printSubmissions(results, commentsEnabled);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetSubmissionResultsList(courseID, evaluationName)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getSubmissionResultsList(courseID, evaluationName); 
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printSubmissionResults(compileSubmissionsIntoSummaryList(results), results);
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

/*
 * Returns
 * 
 * submissionList: successful
 * 1: server error
 * 
 */
function getSubmissionResultsList(courseID, evaluationName)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETSUBMISSIONRESULTSLIST + "&" + COURSE_ID + "=" +
				encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName)); 
		
		return handleGetSubmissionResultsList();
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
		
		for(var x = 0; x < teammates.childNodes.length; x++)
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
			
			for(loop = 0; loop < courses.childNodes.length; loop++) 
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

		for(loop = 0; loop < evaluations.childNodes.length; loop++)
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
			for(loop = 0; loop < submissions.childNodes.length; loop++)
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
 * submissionList: successful
 * 1: server error
 * 
 */
function handleGetSubmissionResultsList()
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
		var fromStudentComments;
		var toStudentComments;
		var courseID;
		var evaluationName;
		var teamName;
		var points;
		var pointsBumpRatio;
		var justification;
		var commentsToStudent;

		if(submissions != null)
		{
			for(loop = 0; loop < submissions.childNodes.length; loop++)
			{
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
					
				submissionList[loop] = {fromStudentName:fromStudentName, toStudentName:toStudentName, 
						fromStudent:fromStudent, toStudent:toStudent, courseID:courseID,
						evaluationName:evaluationName, teamName:teamName, justification:justification,
						commentsToStudent:commentsToStudent, points:points, pointsBumpRatio:pointsBumpRatio,
						fromStudentComments:fromStudentComments, toStudentComments:toStudentComments}; 
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
	
	for(loop = 0; loop < submissionList.length; loop++)
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

function printCourse(course)
{
	var outputHeader = "<h1>COURSE DETAILS</h1>";
	
	var output = "<table id=\"data\">" +
				 "<tr>" + 
				 "<td class=\"fieldname\">Course ID:</td>" + 
				 "<td>" + course.courseID + "</td></tr>" + 
				 "<tr>" + 
				 "<td class=\"fieldname\">Course name:</td>" + 
				 "<td>" + course.courseName + "</td></tr>" + 
				 "<tr>" + 
				 "<td class=\"fieldname\">Coordinator name:</td>" + 
				 "<td>" + course.coordinatorName + "</td></tr>" + 
				 "<tr>" + 
				 "<td class=\"fieldname\">Your team:</td>" + 
				 "<td>" + course.studentTeamName + "</td></tr>" + 
				 "<tr>" + 
				 "<td class=\"fieldname\">Your name:</td>" + 
				 "<td>" + course.studentName + "</td></tr>" + 
				 "<tr>" + 
				 "<td class=\"fieldname\">Your e-mail address:</td>" + 
				 "<td>" + course.studentEmail + "</td></tr>" + 
				 "<tr>" + 
				 "<td class=\"fieldname\">Your teammates:</td>" +
				 "<td>";
	
	if(course.teammateList.length == 0)
	{
		output = output + "Nil";
	}
	
	else
	{
		for(var x = 0; x < course.teammateList.length; x++)
		{
			output = output + (x+1) + ") " + course.teammateList[x] + "<br /><br />";
		}
	}
	
	output = output +
				"</td>" +
				"</tr>" +
				"</table>" +
				"<br /><br />" +
				"<input type=\"button\" class=\"button\" onclick=\"displayCoursesTab();\" value=\"Back\" />" +
				"<br /><br />";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = output; 
}

function printCourseList(courseList)
{
	var output = "<br /><br />" +
	"<table id=\"dataform\">" + 
	"<tr>" + 
	"<th class=\"leftalign\">COURSE ID&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\" /></th>" + 
	"<th class=\"leftalign\">COURSE NAME&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcoursename\" /></th>" + 
	"<th class=\"centeralign\">TEAM NAME</th>" + 
	"<th class=\"centeralign\">ACTION(S)</th>" + 
	"</tr>";
	
	// Need counter to take note of archived courses
	var counter = 0;
	
	for(loop = 0; loop < courseList.length; loop++) 
	{ 
		if(courseList[loop].status == "false" || courseViewArchivedStatus == courseViewArchived.show)
		{
			output = output + "<tr>";
			output = output + "<td>" + courseList[loop].ID + "</td>"; 
			output = output + "<td>" + sanitize(courseList[loop].name) + "</td>"; 
			output = output + "<td class=\"centeralign\">" +  courseList[loop].teamName + "</td>"; 
			output = output + "<td class=\"centeralign\">" + 
				      "<a href=\"javascript:displayCourseInformation('" + courseList[loop].ID + "');hideddrivetip();\"" +
				      " onmouseover=\"ddrivetip('View course details.')\"" +
				      " onmouseout=\"hideddrivetip()\">View</a>";

			// Archive is going to be revamped
			/*
			 * if(courseList[loop].status == "true") { output = output + "<a
			 * href=\"javascript:doUnarchiveCourse('" + courseList[loop].ID +
			 * "');\">Unarchive</a>" + " / "; } else { output = output + "<a
			 * href=\"javascript:doArchiveCourse('" + courseList[loop].ID +
			 * "');\">Archive</a>" + " / "; }
			 */
			
			// Delete is going to be revamped
			// output = output + "<a
			// href=\"javascript:toggleLeaveCourseConfirmation('" +
			// courseList[loop].ID + "');\">Delete</a>" +
			// "</td></tr>";
			
			counter++;
		}
	} 
	
	output = output + "</table><br /><br />";
	
	if(counter == 0)
	{
		output = output + "No records found.<br /><br /><br /><br />";
	}
	
	// Archived is going to be revamped
	/*
	 * if(courseViewArchivedStatus == courseViewArchived.show) { output = output + "<input
	 * class=\"buttonViewArchived\" type=\"button\" value=\" HIDE \nARCHIVED\"" +
	 * "onmouseover=\"this.className='buttonViewArchivedSelected'\"
	 * onmouseout=\"this.className='buttonViewArchived'\"" + "onClick=\"\"
	 * id=\"button_viewarchived\" name=\"button_viewarchived\" tabindex=5 /><br /><br />";
	 *  }
	 * 
	 * else { output = output + "<input class=\"buttonViewArchived\"
	 * type=\"button\" value=\" SHOW \nARCHIVED\"" +
	 * "onmouseover=\"this.className='buttonViewArchivedSelected'\"
	 * onmouseout=\"this.className='buttonViewArchived'\"" +
	 * "id=\"button_viewarchived\" name=\"button_viewarchived\" tabindex=5 /><br /><br />";
	 *  }
	 */
	
	document.getElementById(DIV_COURSE_TABLE).innerHTML = output; 
	document.getElementById('button_sortcourseid').onclick = function() { toggleSortCoursesByID(courseList) };
	document.getElementById('button_sortcoursename').onclick = function() { toggleSortCoursesByName(courseList) };
	
	// Archive is going to be revamped
	/*
	 * document.getElementById('button_viewarchived').onclick = function() {
	 * clearStatusMessage();
	 * 
	 * if(courseViewArchivedStatus == courseViewArchived.show) {
	 * document.getElementById('button_viewarchived').setAttribute("value", "
	 * SHOW \nARCHIVED"); courseViewArchivedStatus = courseViewArchived.hide; }
	 * 
	 * else {
	 * document.getElementById('button_viewarchived').setAttribute("value", "
	 * HIDE \nARCHIVED"); courseViewArchivedStatus = courseViewArchived.show; }
	 * 
	 * if(courseSortStatus == courseSort.name) {
	 * toggleSortCoursesByName(courseList); }
	 * 
	 * else { toggleSortCoursesByID(courseList); }
	 *  };
	 */
	
}

function printEvaluation(courseID, evaluationName, start, deadline, gracePeriod, instructions)
{
	var outputHeader = "<h1>EVALUATION RESULTS</h1>";

	var output = "" +
	"<table id=\"data\">" + 
	"<tr>" + 
	"<td class=\"fieldname\">Course ID:</td>" + 
	"<td>" + sanitize(courseID) + "</td>" +
	"</tr>" + 
	"<tr>" + 
	"<td class=\"fieldname\">Evaluation name:</td>" + 
	"<td>" + sanitize(evaluationName) + "</td>" +
	"</tr>" + 
	"<tr>" + 
	"<td class=\"fieldname\">Opening time:</td>" + 
	"<td>" +  
	convertDateToDDMMYYYY(new Date(start)) + " " + 
	convertDateToHHMM(new Date(start)) + 
	"H</td>" +
	"</tr>" +
	"<tr>" + 
	"<td class=\"fieldname\">Closing time:</td>" + 
	"<td>" + 
	convertDateToDDMMYYYY(new Date(deadline)) + " " + 
	convertDateToHHMM(new Date(deadline)) + 
	"H</td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"fieldname\">Instructions:</td>" +
	"<td>" + sanitize(instructions) + "</td>" +
	"</tr>" +
	"</table>";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output; 
}

function printJoinCourse() {
	var outputHeader = "<h1>JOIN NEW COURSE</h1>";
	
	var outputForm = "" +
	"<form method=\"post\" action=\"\" name=\"form_joincourse\">" +
	"<table id=\"data\">" +
	"<tr>" +
	"<td class=\"fieldname\" style=\"width: 400px;\">Key:</td>" +
	"<td><input class=\"keyvalue\" type=\"text\" name=\"" + STUDENT_REGKEY + "\" id=\"" + STUDENT_REGKEY + "\"" +
	"onmouseover=\"ddrivetip('Enter your registration key for the course.')\"" +
	"onmouseout=\"hideddrivetip()\" tabindex=1 /></td>" +
	"</tr>" +
	"<tr>" +
	"<td>&nbsp;</td>" +
	"<td><input id='btnJoinCourse' type=\"button\" class=\"button\" onclick=\"doJoinCourse(this.form." + STUDENT_REGKEY + ".value);\" value=\"Join course\" tabindex=2 /></td>" +
	"</tr>" +
	"</table>" +
	"</form>";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
}

function printPastEvaluationList(evaluationList)
{
	var output = "";

	var now = new Date();
	
	output = output + "<br />" +
	"<table id=\"dataform\">" + 
	"<tr>" + 
	"<th class=\"centeralign\">COURSE ID&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\"></th>" + 
	"<th class=\"centeralign\">EVALUATION&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" + 
	"<th class=\"centeralign\">DEADLINE</th>" + 
	"<th class=\"centeralign\">STATUS</th>" + 
	"<th class=\"centeralign\">ACTION(S)</th>" +
	"</tr>";
	
	var status;
	var now;
	
	for(loop = 0; loop < evaluationList.length; loop++) 
	{ 
		output = output + "<tr>";
		output = output + "<td>" + sanitize(evaluationList[loop].courseID) + "</td>"; 
		output = output + "<td>" + sanitize(evaluationList[loop].name) + "</td>"; 
		output = output + "<td class=\"centeralign\">" +  
						  convertDateToDDMMYYYY(evaluationList[loop].deadline) + " " +
						  convertDateToHHMM(evaluationList[loop].deadline) + 
						  "H</td>";
		
		now = getDateWithTimeZoneOffset(evaluationList[loop].timeZone);
		
		if(evaluationList[loop].published == true)
		{
			status = "PUBLISHED";
		}
		
		else if(now < evaluationList[loop].deadline)
		{
			status = "SUBMITTED";
		}
		
		else
		{
			status = "CLOSED";
		}
		
		output = output + "<td class=\"centeralign t_eval_status t_eval_status_"+ loop + "\">" + status + "</td>"; 
		output = output + "<td class=\"centeralign\">";
		
		if(evaluationList[loop].published == true)
		{
			output = output +  
			"<a href=\"javascript:void(0);\" \"name=\"viewEvaluation" + loop + "\" id=\"viewEvaluation" + loop + "\"" +
			"onmouseover=\"ddrivetip('View evaluation results')\"" +
		    "onmouseout=\"hideddrivetip()\">View Results</a>"; 
		}
			
		else
		{
			if(now > evaluationList[loop].deadline)
			{
				output = output + "N/A";
			}			
			else
			{
				output = output +  
				"<a href=\"javascript:void(0);\" name=\"editEvaluation" + loop + "\" id=\"editEvaluation" + loop + "\"" +
				"onmouseover=\"ddrivetip('Edit evaluation')\"" +
			    "onmouseout=\"hideddrivetip()\">Edit</a>"; 
			}
		}		
		
		output = output + "</td></tr>";
	}
	
	output = output + "</table><br /><br />";
	
	if(evaluationList.length == 0)
	{
		output = output + "<br />No records found.<br /><br />";
	}
	
	
	document.getElementById(DIV_EVALUATION_PAST).innerHTML = output; 
	document.getElementById('button_sortcourseid').onclick = function() { toggleSortPastEvaluationsByCourseID(evaluationList) };
	document.getElementById('button_sortname').onclick = function() { toggleSortPastEvaluationsByName(evaluationList) };

	for(loop = 0; loop < evaluationList.length; loop++)
	{
		if(document.getElementById('editEvaluation' + loop) != null)
		{
			document.getElementById('editEvaluation' + loop).onclick = function() 
			{ 
				hideddrivetip();
				displayEvaluationSubmission(evaluationList, this.id.substring(14, this.id.length));
			};
		}
		
		else if(document.getElementById('viewEvaluation' + loop) != null)
		{
			document.getElementById('viewEvaluation' + loop).onclick = function() 
			{ 
				hideddrivetip();
				displayEvaluationResults(evaluationList, this.id.substring(14, this.id.length));
			};
		}
	}
}

function printPendingEvaluationList(evaluationList)
{
	var outputHeader = "<h1>EVALUATIONS</h1>";
	var output = "";
	
	// No pending evaluations
	if(evaluationList.length == 0)
	{		
		output = output + "No pending evaluations.<br /><br />";
	}
	
	else
	{		
		output = output + "<table id=\"data\">";
		
		for(x = 0; x < evaluationList.length; x++)
		{
			output = output + "<tr>" +
			"<td class=\"fieldname\">Course ID:</td>" +
			"<td>" + sanitize(evaluationList[x].courseID) + "</td>" +
			"</tr>" +
			"<tr>" +
			"<td class=\"fieldname\">Evaluation name:</td>" +
			"<td>" + sanitize(evaluationList[x].name) + "</td>" +
			"</tr>" +
			"<tr>" +
			"<td class=\"fieldname\">Deadline:</td>" +
			"<td>" +
			convertDateToDDMMYYYY(evaluationList[x].deadline) + " " + 
			convertDateToHHMM(evaluationList[x].deadline) + 
			"H</td>" +
			"</tr>" +
			"<tr>" +
			"<td>&nbsp;</td>" +
			"<td><input type=\"button\" class=\"button t_eval_do\" name=\"doEvaluation" + x + "\" id=\"doEvaluation" + x + "\" value=\"Do evaluation\" />" +
			"</td>" +
			"</tr>" +
			"<tr>" +
			"<td colspan=\"2\">&nbsp;</td>" +
			"</tr>";
		}
	}
	output = output + "</table>";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_EVALUATION_PENDING).innerHTML = output;
	
	for(loop = 0; loop < evaluationList.length; loop++)
	{
		document.getElementById('doEvaluation' + loop).onclick = function() 
		{ 
			displayEvaluationSubmission(evaluationList, this.id.charAt(this.id.length-1));
		};
		
	}
}

function printSubmissions(submissionList, commentsEnabled)
{
	var output = "<form name=\"form_submitevaluation\" id=\"form_submitevaluation\">" +
	"<table id=\"data\">";

	if(commentsEnabled == true)
	{
		output = output +
		"<tr style=\"display:none\"><td>" +
		"<input type=\"text\" value=\"" + submissionList[0].fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + loop +
		"\" id=\"" + STUDENT_FROMSTUDENT + loop + "\">" +
		"<input type=\"text\" value=\"" + submissionList[0].toStudent + "\" name=\"" + STUDENT_TOSTUDENT + loop +
		"\" id=\"" + STUDENT_TOSTUDENT + loop + "\">" +
		"</td></tr>" +
		"<tr style=\"display:none\"><td>" +
		"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + loop +
		"\" id=\"" + COURSE_ID + loop + "\">" +
		"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + loop +
		"\" id=\"" + EVALUATION_NAME + loop + "\">" +
		"</td></tr>" +
		"<tr style=\"display:none\"><td>" +
		"<input type=\"text\" value=\"" + submissionList[0].teamName + "\" name=\"" + STUDENT_TEAMNAME + loop +
		"\" id=\"" + STUDENT_TEAMNAME + loop + "\">" +
		"</td></tr>" +
		"<tr>" + 
		"<td class=\"reportheader\" colspan=\"2\">Self evaluation in Team [" + submissionList[0].teamName +"]</td>" +
		"</tr>" +
		"<tr>" +
		"<td class=\"lhs\">Estimated contribution:</td>" +
		"<td><select style=\"width: 150px\" name=\"" + 
		STUDENT_POINTS + 0 + "\" id=\"" + STUDENT_POINTS + 0 + "\">" + 
		getEvalationOptionString() +
		"</select></td>" +
		"</tr>" +
		"<tr>" +
		"<td class=\"lhs\">Comments about your contribution:</td>" +
		"<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\"" + 
		STUDENT_JUSTIFICATION + 0 + "\" id=\"" + STUDENT_JUSTIFICATION + 0 + "\"></textarea></td>" +
		"</tr>" +
		"<tr>" + 
		"<td class=\"lhs\">Comments about team dynamics:</td>" +
		"<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\"" + 
		STUDENT_COMMENTSTOSTUDENT + 0 + "\" id=\"" + STUDENT_COMMENTSTOSTUDENT + 0 + "\"></textarea></td>" +
		"</tr>" +
		"<tr>" + 
		"<td colspan=\"2\"><br /><br /></td>" +
		"</tr>";
		
		for(loop = 1; loop < submissionList.length; loop++)
		{
			output = output +
			"<tr style=\"display:none\"><td>" +
			"<input type=text value=\"" + submissionList[loop].fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + loop +
			"\" id=\"" + STUDENT_FROMSTUDENT + loop + "\">" +
			"<input type=text value=\"" + submissionList[loop].toStudent + "\" name=\"" + STUDENT_TOSTUDENT + loop +
			"\" id=\"" + STUDENT_TOSTUDENT + loop + "\">" +
			"</td></tr>" +
			"<tr style=\"display:none\"><td>" +
			"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + loop +
			"\" id=\"" + COURSE_ID + loop + "\">" +
			"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + loop +
			"\" id=\"" + EVALUATION_NAME + loop + "\">" +
			"</td></tr>" +
			"<tr style=\"display:none\"><td>" +
			"<input type=\"text\" value=\"" + submissionList[0].teamName + "\" name=\"" + STUDENT_TEAMNAME + loop +
			"\" id=\"" + STUDENT_TEAMNAME + loop + "\">" +
			"</td></tr>" +
			"<tr>" + 
			"<td class=\"reportheader\" colspan=\"2\">Evaluation for " + submissionList[loop].toStudentName + "</td>" +
			"</tr>" +
			"<tr>" +
			"<td class=\"lhs\">Estimated contribution:</td>" +
			"<td><select style=\"width: 150px\" name=\"" + 
			STUDENT_POINTS + 0 + "\" id=\"" + STUDENT_POINTS + loop /*
																	 * huy
																	 * change
																	 * from 0 to
																	 * loop
																	 */ + "\" >" + 
																	 getEvalationOptionString() +
			"</select></td>" +
			"</tr>" +
			"<tr>" +
			"<td class=\"lhs\">Confidential comments about this teammate:<br />(not shown to the teammate)</td>" +
			"<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_JUSTIFICATION + loop + 
			"\" id=\"" + STUDENT_JUSTIFICATION + loop + "\"></textarea></td>" +
			"</tr>" +
			"<tr>" + 
			"<td class=\"lhs\">Your feedback to this teammate:<br />(shown anonymously to the teammate)</td>" +
			"<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_COMMENTSTOSTUDENT + loop +
			"\" id=\"" + STUDENT_COMMENTSTOSTUDENT + loop + "\"></textarea></td>" +
			"</tr>";
			
			if(loop != submissionList.length-1)
			{
				output = output + "<tr>" + 
						"<td colspan=\"2\"><br /><br /></td>" +
						"</tr>";
			}
		}
	}
	
	else
	{
		output = output +
		"<tr style=\"display:none\"><td>" +
		"<input type=\"text\" value=\"" + submissionList[0].fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + loop +
		"\" id=\"" + STUDENT_FROMSTUDENT + loop + "\">" +
		"<input type=\"text\" value=\"" + submissionList[0].toStudent + "\" name=\"" + STUDENT_TOSTUDENT + loop +
		"\" id=\"" + STUDENT_TOSTUDENT + loop + "\">" +
		"</td></tr>" +
		"<tr style=\"display:none\"><td>" +
		"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + loop +
		"\" id=\"" + COURSE_ID + loop + "\">" +
		"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + loop +
		"\" id=\"" + EVALUATION_NAME + loop + "\">" +
		"</td></tr>" +
		"<tr style=\"display:none\"><td>" +
		"<input type=\"text\" value=\"" + submissionList[0].teamName + "\" name=\"" + STUDENT_TEAMNAME + loop +
		"\" id=\"" + STUDENT_TEAMNAME + loop + "\">" +
		"</td></tr>" +
		"<tr>" + 
		"<td class=\"reportheader\" colspan=\"2\">Self evaluation in Team [" + submissionList[0].teamName +"]</td>" +
		"</tr>" +
		"<tr>" +
		"<td class=\"lhs\">Estimated contribution:</td>" +
		"<td><select style=\"width: 150px\" name=\"" + 
		STUDENT_POINTS + 0 + "\" id=\"" + STUDENT_POINTS + 0 + "\">" + 
		getEvalationOptionString() +
		"</select></td>" +
		"</tr>" +
		"<tr>" +
		"<td class=\"lhs\">Comments about your contribution:</td>" +
		"<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\"" + 
		STUDENT_JUSTIFICATION + 0 + "\" id=\"" + STUDENT_JUSTIFICATION + 0 + "\">" +
		"</textarea></td>" +
		"</tr>" +
		"<tr>" + 
		"<td class=\"lhs\">Comments about team dynamics:</td>" +
		"<td>"+ getDisabledString() +"</td>" +
		"</tr>" +
		"<tr>" + 
		"<td colspan=\"2\"><br /><br /></td>" +
		"</tr>";
		
		for(loop = 1; loop < submissionList.length; loop++)
		{
			output = output +
			"<tr style=\"display:none\"><td>" +
			"<input type=text value=\"" + submissionList[loop].fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + loop +
			"\" id=\"" + STUDENT_FROMSTUDENT + loop + "\">" +
			"<input type=text value=\"" + submissionList[loop].toStudent + "\" name=\"" + STUDENT_TOSTUDENT + loop +
			"\" id=\"" + STUDENT_TOSTUDENT + loop + "\">" +
			"</td></tr>" +
			"<tr style=\"display:none\"><td>" +
			"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + loop +
			"\" id=\"" + COURSE_ID + loop + "\">" +
			"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + loop +
			"\" id=\"" + EVALUATION_NAME + loop + "\">" +
			"</td></tr>" +
			"<tr style=\"display:none\"><td>" +
			"<input type=\"text\" value=\"" + submissionList[0].teamName + "\" name=\"" + STUDENT_TEAMNAME + loop +
			"\" id=\"" + STUDENT_TEAMNAME + loop + "\">" +
			"</td></tr>" +
			"<tr>" + 
			"<td class=\"reportheader\" colspan=\"2\">Evaluation for " + submissionList[loop].toStudentName +  "</td>" +
			"</tr>" +
			"<tr>" + 
			"<td class=\"lhs\">Estimated contribution:</td>" +
			"<td><select style=\"width: 150px\" name=\"" + 
			STUDENT_POINTS + 0 + "\" id=\"" + STUDENT_POINTS + 0 + "\" >" + 
			"<option value=\"200\">Equal share + 100%</option>" +
			"<option value=\"190\">Equal share + 90%</option>" +
			"<option value=\"180\">Equal share + 80%</option>" +
			"<option value=\"170\">Equal share + 70%</option>" +
			"<option value=\"160\">Equal share + 60%</option>" +
			"<option value=\"150\">Equal share + 50%</option>" +
			"<option value=\"140\">Equal share + 40%</option>" +
			"<option value=\"130\">Equal share + 30%</option>" +
			"<option value=\"120\">Equal share + 20%</option>" +
			"<option value=\"110\">Equal share + 10%</option>" +
			"<option value=\"100\" SELECTED>Equal Share</option>" +
			"<option value=\"90\">Equal share - 10%</option>" +
			"<option value=\"80\">Equal share - 20%</option>" +
			"<option value=\"70\">Equal share - 30%</option>" +
			"<option value=\"60\">Equal share - 40%</option>" +
			"<option value=\"50\">Equal share - 50%</option>" +
			"<option value=\"40\">Equal share - 60%</option>" +
			"<option value=\"30\">Equal share - 70%</option>" +
			"<option value=\"20\">Equal share - 80%</option>" +
			"<option value=\"10\">Equal share - 90%</option>" +
			"<option value=\"0\">0%</option>" +
			"<option value=\"-101\">Not Sure</option>" +
			"</select></td>" +
			"</tr>" +
			"<tr>" +
			"<td class=\"lhs\">Confidential comments about this teammate:<br />(not shown to the teammate)</td>" +
			"<td><textarea class = \"textvalue\" type=\"text\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_JUSTIFICATION + loop + 
			"\" id=\"" + STUDENT_JUSTIFICATION + loop + "\"></textarea></td>" +
			"</tr>" +
			"<tr><td>Your feedback to this teammate:<br/>(shown anonymously to the teammate)</td>" + 
			"<td>"+ getDisabledString() +"</td>" +
			"</tr>";
			
			if (loop != submissionList.length-1) {
				output = output + "<tr>" + 
									"<td colspan=\"2\"><br /><br /></td>" +
									"</tr>";
			}
		}
	}
	
	output = output +
					"</table>" +
					"</form>" +
					"<br />";
	
	outputButtons = "<input type=\"button\" class=\"button\" name=\"submitEvaluation\" id=\"submitEvaluation\" value=\"Submit Evaluation\" />" +
					" <input type=\"button\" class=\"button t_back\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />" +
					"<br /><br />";
	
	document.getElementById(DIV_EVALUATION_SUBMISSIONS).innerHTML = output; 
	document.getElementById(DIV_EVALUATION_SUBMISSIONBUTTONS).innerHTML = outputButtons; 
	
	populateEvaluationSubmissionForm(document.forms[0], submissionList, commentsEnabled);
	
	document.getElementById("submitEvaluation").onclick = function() {
		doSubmitEvaluation(document.forms[0], submissionList.length, commentsEnabled);
	};
}

function printSubmissionResults(summaryList, submissionList)
{
	var perceivedPoints = roundToNearestTen(summaryList[0].average);
	
	if(isNaN(perceivedPoints))
	{
		perceivedPoints = "N/A";
	}
	
	var	claimedPoints = roundToNearestTen(summaryList[0].claimedPoints);
	
	var output = "<br /><br /><table id=\"data\">" + 
	"<tr>" + 
	"<td class=\"reportheader\" colspan=\"2\">PEER EVALUATION FOR " + summaryList[0].toStudentName.toUpperCase() + "</td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"lhs\">The contribution you claimed:</td>" +
	"<td>" + claimedPoints + "</td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"lhs\">Your team\'s perception of your contribution:</td>" +
	"<td>" + perceivedPoints + "</td>" +
	"</tr>" +
	"<tr>" + 
	"<td class=\"reportheader\" colspan=\"2\">Feedback from teammates</td>" +
	"</tr>";

	var targetStudent = summaryList[0].toStudent;
	
	submissionList.splice(0,1);
	submissionList.sort(function() {return 0.5 - Math.random()});
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		var sp = loop + 1;
		if(submissionList[loop].toStudent == targetStudent && 
				submissionList[loop].fromStudent != targetStudent)
		{
			output = output +
			"<tr>" +
			"<td class=\"lhs\">Teammate " + ":</td>" +
			"<td>";
			
			if(sanitize(submissionList[loop].commentsToStudent) == "")
			{
				output = output + "N/A </td></tr>";
			}

			else
			{
				output = output + sanitize(submissionList[loop].commentsToStudent) + "</td></tr>";
			}
		}
	}

	output = output +
					"</table>" +
					"<br /><br /><br />" +
					"<input type=\"button\" class=\"button t_back\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />" +
					"<br /><br />";

	document.getElementById(DIV_EVALUATION_RESULTS).innerHTML = output; 
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
	for(loop = 0; loop < submissionList.length; loop++)
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
				  "=" + submissionList.length + "&" + COURSE_ID + "=" + submissionList[0].courseID +
				  "&" + EVALUATION_NAME + "=" + submissionList[0].evaluationName +
				  "&" + STUDENT_TEAMNAME + "=" + submissionList[0].teamName;
	
	for(loop = 0; loop < submissionList.length; loop++)
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
	var output = "Are you sure you want to remove the course, \"" + courseID + "\"? <a class=\"confirm\" href=\"javascript:doLeaveCourse('" + courseID + "')\">Yes</a>" +
				" /  <a class=\"confirm\" href=\"javascript:setStatusMessage('')\">No</a>";
	
	document.getElementById(DIV_STATUS).innerHTML = output; 
	document.getElementById(DIV_COURSE_MANAGEMENT).scrollIntoView(true);
}

function toggleSortCoursesByID(courseList) {
	printCourseList(courseList.sort(sortByID));
	courseSortStatus = courseSort.ID;
	document.getElementById("button_sortcourseid").setAttribute("class", "buttonSortAscending");
}

function toggleSortCoursesByName(courseList) {
	printCourseList(courseList.sort(sortByCourseName));
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
	displayCoursesTab();
}

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip
