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
	
	var submissionListLength = submissionList.length;
	for(loop = 0; loop < submissionListLength; loop++)
	{
		exists = false;
		submitted = false;
		
		var summaryListLength = summaryList.length;
		for(x = 0; x < summaryListLength; x++)
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
			
			for(y = loop; y < submissionListLength; y++)
			{
				if(submissionList[y].toStudent == toStudent)
				{
					if(submissionList[y].fromStudent == toStudent)
					{
						if(submissionList[y].points == -999)
						{
							claimedPoints = NA;
						}
						else if(submissionList[y].points == -101) {
							claimedPoints = NOTSURE;
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
				average = NA;
			}
			
			difference = Math.round(average-claimedPoints);

			if(isNaN(difference)) {
				difference = NA;
			}
			
			summaryList[count++] = { toStudent:toStudent, toStudentName:toStudentName, teamName:teamName,
					average:average, difference:difference, toStudentComments:toStudentComments, submitted:submitted,
					claimedPoints:claimedPoints};
		}
	}
	
	// Find normalizing points bump ratio for averages
	var teamsNormalized = new Array();
	count = 0;
	
	var summaryListLength = summaryList.length;
	for(loop = 0; loop < summaryListLength; loop++)
	{
		// Reset variables
		exists = false;
		totalPoints = 0;
		totalGivers = 0;
		pointsBumpRatio = 0;
		
		// Check if the team is added
		var teamsNormalizedLength = teamsNormalized.length;
		for(y = 0; y < teamsNormalizedLength; y++)
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
			for(y = loop; y < summaryListLength; y++)
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
	var teamsNormalizedLength = teamsNormalized.length;
	for(loop = 0; loop < teamsNormalizedLength; loop++)
	{
		for(y = 0; y < summaryListLength; y++)
		{
			if(summaryList[y].teamName == teamsNormalized[loop].teamName && summaryList[y].average != NA)
			{
				summaryList[y].average = Math.round(summaryList[y].average * teamsNormalized[loop].pointsBumpRatio);
				summaryList[y].difference = Math.round(summaryList[y].average-summaryList[y].claimedPoints);
				if(isNaN(summaryList[y].difference))
				{
					summaryList[y].difference = NA;
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

//by kalpit
function studentViewCreateTeams(){
	clearAllDisplay();
	printFormTeams();
	printStudentTeams();
}

function printFormTeams(){
	var outputHeader = "<h1>FORM TEAMS FOR COURSE cs2103testing</h1>";

	var outputForm = ""
			+ "<form method=\"post\" action=\"\" name=\"form_addevaluation\">"
			+ "<table class=\"addform round\">" + "<tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Profile (Detail):</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ EVALUATION_INSTRUCTIONS
			+ "\" id=\""
			+ EVALUATION_INSTRUCTIONS
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter Profile template questions for your students, e.g. Strenths, Schedule, etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>Mention some of your past projects and your weekly schedule.\nI have created a job database site before and have also created a few social games for facebook. I live on campus in PGPR and I am studying 5 courses this semester.</textarea>"
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Profile (Summary):</td>"
			+ "<td colspan=\"3\">"
			+ "<textarea rows=\"3\" cols=\"50\" class=\"textvalue\"type=\"text\" name=\""
			+ EVALUATION_INSTRUCTIONS
			+ "\" id=\""
			+ EVALUATION_INSTRUCTIONS
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter Profile template questions for your students, e.g. Strenths, Schedule, etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>Mention the summary of the above.\nProficient in Java, C# and C++; did ATAP last semester.</textarea>"
			+ "</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Instructions:</td>"
			+ "<td>Max team size: 6<br />Min team size: 4</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td class=\"attribute\" >Deadline:</td>"
			+ "<td>09/12/2011, 2359 hours</td>"
			+ "</tr>" 
			+ "<tr>"
			+ "<td></td>"
			+ "<td colspan=\"3\">"
			+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
			+ "value=\"Save\" tabindex=2 />"
			+ "</td>" + "</tr>" + "</table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
}

function manageTeamProfile(){
	clearAllDisplay();
	printTeamDetail();
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
			+ EVALUATION_INSTRUCTIONS
			+ "\" id=\""
			+ EVALUATION_INSTRUCTIONS
			+ "\""
			+ "onmouseover=\"ddrivetip('Please enter your team profile/proposal etc.')\""
			+ "onmouseout=\"hideddrivetip()\" tabindex=8>Please enter your team profile/proposal etc.</textarea>"
			+ "</td>"
			+ "</tr>" 
			+ "<tr>"
			+ "<td></td>"
			+ "<td colspan=\"3\">"
			+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
			+ "value=\"Save\" tabindex=2 />"
			+ "</td>" + "</tr>" + "</table>" + "</form>";	

	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
}

function printStudentTeams(){
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
					+ "<th class='centeralign'>PROFILE</th>"
					+ "<th class='centeralign'>EMAIL</th>";
	output = output + "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";
	
	//student 1
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">ALICE</td><td>Proficient in Java, C# and C++; did ATAP last semester</td><td>alice.tmms@gmail.com</td>";
	
	output = output + "<td class='centeralign'>"
				+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
				+ "value=\"Leave\" tabindex=2 />"
				+ "</td>" + "</tr>";
	
	//student 2
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">BENNY</td><td>I did an internship in Facebook last semester; have good understanding of software engineering principles and database design.</td><td>benny.tmms@gmail.com</td>";

	output = output + "<td class='centeralign'>"
				+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
				+ "value=\"Remove\" tabindex=2 />"
				+ "</td>" + "</tr>";
		
	output = output + "</table><br /></div>";
	
	output = output + "<br /><div><h1>STUDENTS WITHOUT ANY TEAM</h1></div>"
				+ "<br /><table id=\"dataform\">" + "<tr>";
	output = output + "<th class='centeralign'>STUDENT NAME</th>"
				+ "<th class='centeralign'>PROFILE</th>"
				+ "<th class='centeralign'>EMAIL</th>";
	output = output + "<th class=\"centeralign\">ACTION(S)</th>" + "</tr>";

	//student 3
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">CHARLIE</td><td>Passionate about Game Development; Prior courses include CS2103, CS2102S, CS1102.</td><td>charlie.tmms@gmail.com</td>";

	output = output + "<td class='centeralign' style=\"width: 150px;\">"
				+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
				+ "value=\"Join\" tabindex=2 />"
				+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
				+ "value=\"Send Invite\" tabindex=2 />"
				+ "</td>" + "</tr>";

	//student 4
	output = output
				+ "<tr><td class='centeralign' style=\"width: 150px;\">DANNY</td><td>Live on campus in Prince George Park Residences; Taking only 4 courses this semester; Looking for teammates with Java background.</td><td>danny.tmms@gmail.com</td>";

	output = output + "<td class='centeralign' style=\"width: 150px;\">"
				+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
				+ "value=\"Join\" tabindex=2 />"
				+ "<input id='t_btnAddEvaluation' type=\"button\" class=\"button\""
				+ "value=\"Send Invite\" tabindex=2 />"
				+ "</td>" + "</tr>";
	
	output = output + "</table><br />";
	
	output = output
				+ "<br /><br />"
				+ "<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Update\" /><br /><br />"
	
	document.getElementById(DIV_COURSE_TABLE).innerHTML = output;
}
//end by kalpit

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
	
	printEvaluationHeader(courseID, evaluationName, start, deadline, gracePeriod, instructions, commentsEnabled);
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
	
	doGetSubmissionResultsList(courseID, evaluationName, start, deadline);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayHelpTab()
{
	clearAllDisplay();
	clearStatusMessage();
	printStudentHelp();
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
		printSubmissionForm(results, commentsEnabled);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetSubmissionResultsList(courseID, evaluationName, start, deadline)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getSubmissionResultsList(courseID, evaluationName); 
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printEvaluationResultStudentForm(compileSubmissionsIntoSummaryList(results), results, start, deadline);
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
			var submissionsChildNodesLength = submissions.childNodes.length;
			for(loop = 0; loop < submissionsChildNodesLength; loop++)
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
		logSubmissionList(submissionList);
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
	displayCoursesTab();
}

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip
