// BROWSER DETECT
var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			   string: navigator.userAgent,
			   subString: "iPhone",
			   identity: "iPhone/iPod"
	    },
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};


BrowserDetect.init();

// AJAX
var xmlhttp = new getXMLObject();

// DATE OBJECT
var cal = new CalendarPopup();

// DISPLAY
var DISPLAY_COURSE_ADDED = "The course has been added. Click the 'Enrol' link in the table below to add students to the course.";
var DISPLAY_COURSE_ARCHIVED = "The course has been archived.";
var DISPLAY_COURSE_DELETED = "The course has been deleted."
var DISPLAY_COURSE_DELETEDALLSTUDENTS = "All students have been removed from the course.";
var DISPLAY_COURSE_EXISTS = "<font color=\"#F00\">The course already exists.</font>";
var DISPLAY_COURSE_INVALIDID = "<font color=\"#F00\">Please use only alphabets, numbers, dots and hyphens in COURSE ID.</font>";
var DISPLAY_COURSE_NOTEAMS = "<font color=\"#F00\">The course does not have any teams.</font>";
var DISPLAY_COURSE_SENTREGISTRATIONKEY = "Registration key has been sent to ";
var DISPLAY_COURSE_SENTREGISTRATIONKEYS = "Registration keys are sent to the students.";
var DISPLAY_COURSE_INVALIDNAME = "<font color=\"#F00\">Course name should not exceed 30 characters.</font>";
var DISPLAY_COURSE_UNARCHIVED = "The course has been unarchived.";
var DISPLAY_EDITSTUDENT_FIELDSEMPTY = "<font color=\"#F00\">Please fill in all fields marked with an *.</font>";
var DISPLAY_ENROLLMENT_FIELDSEXTRA = "<font color=\"#F00\">There are too many fields.</font>";
var DISPLAY_ENROLLMENT_FIELDSMISSING = "<font color=\"#F00\">There are missing fields.</font>";
var DISPLAY_EVALUATION_ADDED = "The evaluation has been added.";
var DISPLAY_EVALUATION_ARCHIVED = "The evaluation has been archived.";
var DISPLAY_EVALUATION_DELETED = "The evaluation has been deleted.";
var DISPLAY_EVALUATION_EDITED = "The evaluation has been edited.";
var DISPLAY_EVALUATION_EXISTS = "<font color=\"#F00\">The evaluation exists already.</font>";
var DISPLAY_EVALUATION_INFORMEDSTUDENTSOFCHANGES = "E-mails have been sent out to inform the students of the changes to the evaluation.";
var DISPLAY_EVALUATION_NAMEINVALID = "<font color=\"#F00\">Please use only alphabets, numbers and whitespace in EVALUATION NAME.</font>";
var DISPLAY_EVALUATION_PUBLISHED = "The evaluation has been published.";
var DISPLAY_EVALUATION_UNPUBLISHED = "The evaluation has been unpublished.";
var DISPLAY_EVALUATION_REMINDERSSENT = "Reminder e-mails have been sent out to those students.";
var DISPLAY_EVALUATION_RESULTSEDITED = "The particular evaluation results have been edited.";
var DISPLAY_EVALUATION_SCHEDULEINVALID = "<font color=\"#F00\">The evaluation schedule (start/deadline) is not valid.</font>";
var DISPLAY_EVALUATION_UNARCHIVED = "The evaluation has been unarchived.";
var DISPLAY_FIELDS_EMPTY = "<font color=\"#F00\">Please fill in all the relevant fields.</font>";
var DISPLAY_LOADING = "<img src=images/ajax-loader.gif /><br />";
var DISPLAY_SERVERERROR = "Connection to the server has timed out. Please refresh the page.";
var DISPLAY_STUDENT_DELETED = "The student has been removed.";
var DISPLAY_STUDENT_EDITED = "The student's details have been edited.";
var DISPLAY_STUDENT_EDITEDEXCEPTTEAM = "The student's details have been edited, except for his team<br /> as there is an ongoing evaluation."
var DISPLAY_STUDENT_EMAILINVALID = "<font color=\"#F00\">E-mail address should contain less than 40 characters and be of a valid syntax.</font>";
var DISPLAY_STUDENT_NAMEINVALID = "<font color=\"#F00\">Name should only consist of alphabets and numbers and not<br />be more than 40 characters.</font>";
var DISPLAY_STUDENT_TEAMNAMEINVALID = "<font color=\"#F00\">Team name should contain less than 25 characters.</font>";

// DIV
var DIV_COURSE_INFORMATION = "coordinatorCourseInformation";
var DIV_COURSE_ENROLLMENT = "coordinatorCourseEnrollment";
var DIV_COURSE_ENROLLMENTBUTTONS = "coordinatorCourseEnrollmentButtons";
var DIV_COURSE_ENROLLMENTRESULTS = "coordinatorCourseEnrollmentResults";
var DIV_COURSE_MANAGEMENT = "coordinatorCourseManagement";
var DIV_COURSE_TABLE = "coordinatorCourseTable";
var DIV_EVALUATION_EDITBUTTONS = "coordinatorEditEvaluationButtons";
var DIV_EVALUATION_EDITRESULTS = "coordinatorEditEvaluationResults";
var DIV_EVALUATION_EDITRESULTSBUTTON = "coordinatorEditEvaluationResultsButtons";
var DIV_EVALUATION_INFORMATION = "coordinatorEvaluationInformation";
var DIV_EVALUATION_MANAGEMENT = "coordinatorEvaluationManagement";
var DIV_EVALUATION_SUMMARYTABLE = "coordinatorEvaluationSummaryTable";
var DIV_EVALUATION_TABLE = "coordinatorEvaluationTable";
var DIV_HEADER_OPERATION = "headerOperation";
var DIV_STUDENT_EDITBUTTONS = "coordinatorEditStudentButtons";
var DIV_STUDENT_INFORMATION = "coordinatorStudentInformation";
var DIV_STUDENT_TABLE = "coordinatorStudentTable";
var DIV_STATUS_EDITEVALUATIONRESULTS = "coordinatorEditEvaluationResultsStatusMessage";
var DIV_TOPOFPAGE = "topOfPage";

// GLOBAL VARIABLES FOR GUI
var courseSort = { ID:0, name:1 }
var courseSortStatus = courseSort.ID; 

var evaluationSort = { courseID:0, name:1 }
var evaluationSortStatus = evaluationSort.courseID; 

var studentSort = { name:0, teamName:1, status: 2}
var studentSortStatus = studentSort.name; 

var courseViewArchived = { show:0, hide:1 }
var courseViewArchivedStatus = courseViewArchived.hide; 

var evaluationResultsView = { reviewee:0, reviewer:1 }
var evaluationResultsViewStatus = evaluationResultsView.reviewee;

var evaluationResultsSummaryListSort = { teamName:0, name:1, average:2, submitted:3, diff:4 }
var evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.teamName; 

// MESSAGES
var MSG_COURSE_EXISTS = "course exists";
var MSG_COURSE_NOTEAMS = "course has no teams";

var MSG_EVALUATION_ADDED = "evaluation added";
var MSG_EVALUATION_EDITED = "evaluation edited";
var MSG_EVALUATION_EXISTS = "evaluation exists";
var MSG_EVALUATION_UNABLETOCHANGETEAMS = "evaluation ongoing unable to change teams";

// OPERATIONS
var OPERATION_COORDINATOR_ADDCOURSE = "coordinator_addcourse";
var OPERATION_COORDINATOR_ADDEVALUATION = "coordinator_addevaluation";
var OPERATION_COORDINATOR_ARCHIVECOURSE = "coordinator_archivecourse";
var OPERATION_COORDINATOR_ARCHIVEEVALUATION = "coordinator_archiveevaluation";
var OPERATION_COORDINATOR_DELETEALLSTUDENTS = "coordinator_deleteallstudents";
var OPERATION_COORDINATOR_DELETECOURSE = "coordinator_deletecourse";
var OPERATION_COORDINATOR_DELETEEVALUATION = "coordinator_deleteevaluation";
var OPERATION_COORDINATOR_DELETESTUDENT = "coordinator_deletestudent";
var OPERATION_COORDINATOR_EDITEVALUATION = "coordinator_editevaluation";
var OPERATION_COORDINATOR_EDITEVALUATIONRESULTS = "coordinator_editevaluationresults";
var OPERATION_COORDINATOR_EDITSTUDENT = "coordinator_editstudent";
var OPERATION_COORDINATOR_ENROLSTUDENTS = "coordinator_enrolstudents";
var OPERATION_COORDINATOR_GETCOURSE = "coordinator_getcourse";
var OPERATION_COORDINATOR_GETCOURSELIST = "coordinator_getcourselist";
var OPERATION_COORDINATOR_GETEVALUATIONLIST = "coordinator_getevaluationlist";
var OPERATION_COORDINATOR_GETSTUDENTLIST = "coordinator_getstudentlist";
var OPERATION_COORDINATOR_GETSUBMISSIONLIST = "coordinator_getsubmissionlist";
var OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES = "coordinator_informstudentsofevaluationchanges";
var OPERATION_COORDINATOR_LOGOUT = "coordinator_logout";
var OPERATION_COORDINATOR_PUBLISHEVALUATION = "coordinator_publishevaluation";
var OPERATION_COORDINATOR_UNPUBLISHEVALUATION = "coordinator_unpublishevaluation";
var OPERATION_COORDINATOR_REMINDSTUDENTS = "coordinator_remindstudents";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEY = "coordinator_sendregistrationkey";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEYS = "coordinator_sendregistrationkeys";
var OPERATION_COORDINATOR_UNARCHIVECOURSE = "coordinator_unarchivecourse";
var OPERATION_COORDINATOR_UNARCHIVEEVALUATION = "coordinator_unarchiveevaluation";

// PARAMETERS
var COURSE_ID = "courseid";
var COURSE_NAME = "coursename";
var COURSE_NUMBEROFTEAMS = "coursenumberofteams";
var COURSE_STATUS = "coursestatus";

var EVALUATION_ACTIVATED = "activated";
var EVALUATION_ARCHIVED = "evaluationarchived";
var EVALUATION_COMMENTSENABLED = "commentsstatus";
var EVALUATION_DEADLINE = "deadline";
var EVALUATION_DEADLINETIME = "deadlinetime";
var EVALUATION_GRACEPERIOD = "graceperiod";
var EVALUATION_INSTRUCTIONS = "instr";
var EVALUATION_NAME = "evaluationname";
var EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS = "numberofevaluations";
var EVALUATION_NUMBEROFEVALUATIONS = "numberofcompletedevaluations";
var EVALUATION_PUBLISHED = "published";
var EVALUATION_START = "start";
var EVALUATION_STARTTIME = "starttime";
var EVALUATION_TIMEZONE = "timezone";
var EVALUATION_TYPE = "evaluationtype";

var STUDENT_COMMENTS = "comments";
var STUDENT_COMMENTSEDITED = "commentsedited";
var STUDENT_COMMENTSTOSTUDENT = "commentstostudent";
var STUDENT_COURSEID = "courseid";
var STUDENT_EDITCOMMENTS = "editcomments";
var STUDENT_EDITEMAIL = "editemail";
var STUDENT_EDITGOOGLEID = "editgoogleid";
var STUDENT_EDITNAME = "editname";
var STUDENT_EDITTEAMNAME = "editteamname";
var STUDENT_EMAIL = "email";
var STUDENT_FROMSTUDENT = "fromemail";
var STUDENT_FROMSTUDENTCOMMENTS = "fromstudentcomments";
var STUDENT_FROMSTUDENTNAME = "fromname";
var STUDENT_ID = "id";
var STUDENT_INFORMATION = "information";
var STUDENT_JUSTIFICATION = "justification";
var STUDENT_NAME = "name";
var STUDENT_NAMEEDITED = "nameedited";
var STUDENT_NUMBEROFSUBMISSIONS = "numberofsubmissions";
var STUDENT_POINTS = "points";
var STUDENT_POINTSBUMPRATIO = "pointsbumpratio";
var STUDENT_REGKEY = "regkey";
var STUDENT_STATUS = "status";
var STUDENT_TEAMNAME = "teamname";
var STUDENT_TEAMNAMEEDITED = "teamnameedited";
var STUDENT_TOSTUDENT = "toemail";
var STUDENT_TOSTUDENTCOMMENTS = "tostudentcomments";
var STUDENT_TOSTUDENTNAME = "toname";


/*
 * Returns
 * 
 * 0: successful 1: server error 2: field(s) empty 3: courseID invalid 4: name
 * invalid 5: course exists
 * 
 */
function addCourse(courseID, courseName)
{
	if(xmlhttp)
	{
		courseID = trim(courseID);
		courseName = trim(courseName);
		
		if(courseID == "" || courseName == "")
		{
			return 2;
		}

		else if(!isCourseIDValid(courseID))
		{
			return 3;
		}
		
		else if(!isCourseNameValid(courseName))
		{
			return 4;
		}
		
		else
		{
			xmlhttp.open("POST","teammates",false); 
			xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_ADDCOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID) +
					"&" + COURSE_NAME + "=" + encodeURIComponent(courseName));
			
			
			var results = handleAddCourse();
			
			return results;
			
		}
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: evaluation name invalid 4:
 * evaluation schedule invalid 5: evaluation exists 6: course has no teams
 * 
 */
function addEvaluation(courseID, name, instructions, commentsEnabled, start, startTime, deadline, deadlineTime, timeZone, gracePeriod)
{
	if(courseID == "" || name == "" || start == "" || startTime == "" || deadline == "" || deadlineTime == "" ||
			timeZone == "" || gracePeriod == "" || instructions == "")
	{
		return 2;
	}
	
	else if(!isEvaluationNameValid(name))
	{
		return 3;
	}
	
	else if(!isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime))
	{
		return 4;
	}
	
	else
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_ADDEVALUATION + "&" + COURSE_ID + "=" + encodeURIComponent(courseID) +
				"&" + EVALUATION_NAME + "=" + encodeURIComponent(name) + "&" + EVALUATION_DEADLINE + "=" + encodeURIComponent(deadline) + 
				"&" + EVALUATION_DEADLINETIME + "=" + encodeURIComponent(deadlineTime) +
				"&" + EVALUATION_INSTRUCTIONS + "=" + encodeURIComponent(instructions) + "&" + 
				EVALUATION_START + "=" + encodeURIComponent(start) + "&" + EVALUATION_STARTTIME + "=" + 
				encodeURIComponent(startTime) + "&" + EVALUATION_GRACEPERIOD + "=" + encodeURIComponent(gracePeriod) +
				"&" + EVALUATION_TIMEZONE + "=" + encodeURIComponent(timeZone) +
				"&" + EVALUATION_COMMENTSENABLED + "=" + encodeURIComponent(commentsEnabled));
	
		return handleAddEvaluation();
	}
	
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function archiveCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	if(xmlhttp)
	{		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_ARCHIVECOURSE + 
				"&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	
		return handleArchiveCourse();
	}
}

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

function checkEditStudentInput(editName, editTeamName, editEmail, editGoogleID)
{
	if(editName == "" || editTeamName == "" || editEmail == "")
	{
		setStatusMessage(DISPLAY_EDITSTUDENT_FIELDSEMPTY);
	}
	
	if(!isStudentNameValid(editName))
	{
		setStatusMessage(DISPLAY_STUDENT_NAMEINVALID);
	}
	
	else if(!isStudentEmailValid(editEmail))
	{
		setStatusMessage(DISPLAY_STUDENT_EMAILINVALID);
	}
	
	else if(!isStudentTeamNameValid(editTeamName))
	{
		setStatusMessage(DISPLAY_STUDENT_TEAMNAMEINVALID);
	}
}

function checkEnrollmentInput(input)
{
	var entries = input.split("\n");
	var fields;
	
	for(var x = 0; x < entries.length; x++)
	{
		//ignore blank line
		if(entries[x] != ""){
			// Separate the fields
			fields = entries[x].split("|");
			
			// Make sure that all fields are present
			if(fields.length < 3)
			{
				setStatusMessage("<font color=\"#F00\">Line " + (x+1) + ":</font> " + DISPLAY_ENROLLMENT_FIELDSMISSING);
			}
			
			else if(fields.length > 4)
			{
				setStatusMessage("<font color=\"#F00\">Line " + (x+1) + ":</font> " + DISPLAY_ENROLLMENT_FIELDSEXTRA);
			}
			
			// Check that fields are correct
			if(!isStudentNameValid(trim(fields[1])))
			{
				setStatusMessage("<font color=\"#F00\">Line " + (x+1) + ":</font> " + DISPLAY_STUDENT_NAMEINVALID);
			}
			
			else if(!isStudentEmailValid(trim(fields[2])))
			{
				setStatusMessage("<font color=\"#F00\">Line " + (x+1) + ":</font> " + DISPLAY_STUDENT_EMAILINVALID);
			}
			
			else if(!isStudentTeamNameValid(trim(fields[0])))
			{
				setStatusMessage("<font color=\"#F00\">Line " + (x+1) + ":</font> " + DISPLAY_STUDENT_TEAMNAMEINVALID);
			}
		}
	}
	
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
							claimedPoints = Math.round(submissionList[y].points * submissionList[y].pointsBumpRatio);
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
			console.log("******"+toStudent+"|"+toStudentName+"|"+teamName+"|"+average+"|"+difference+"|"+toStudentComments+"|"+submitted+"|"+claimedPoints);

		}
	}
	
	// Find normalizing points bump ratio for averages
	var teamsNormalized = new Array();
	count = 0;
	
	for(loop = 0; loop < summaryList.length; loop++)
	{
		teamName = summaryList[loop].teamName;
		console.log("find normalizing bumpRatio loop index: " + loop + summaryList[loop].teamName);
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

function convertDateFromDDMMYYYYToMMDDYYYY(dateString)
{
	var newDateString = dateString.substring(3,5) + "/" + dateString.substring(0,2) + "/" + 
						dateString.substring(6,10);

	return newDateString;
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

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function deleteAllStudents(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETEALLSTUDENTS + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
		
		return handleDeleteAllStudents(courseID);
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function deleteCourse(courseID)
{
	if(xmlhttp)
	{		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETECOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	
		return handleDeleteCourse();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function deleteEvaluation(courseID, name)
{
	if(xmlhttp)
	{
		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETEEVALUATION + "&" + COURSE_ID + "=" 
				+ encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(name));
		
		return handleDeleteEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 */
function deleteStudent(courseID, studentEmail)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_DELETESTUDENT + "&" + COURSE_ID + "=" + 
				encodeURIComponent(courseID) + "&" + STUDENT_EMAIL + "=" + encodeURIComponent(studentEmail));
		
		return handleDeleteStudent();
	}
}

function displayCourseInformation(courseID)
{
	clearAllDisplay();
	doGetCourse(courseID);
	doGetStudentList(courseID);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayCoursesTab()
{
	clearAllDisplay();
	setStatusMessage(DISPLAY_LOADING);
	printAddCourse();
	doGetCourseList();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	
}

function displayEditEvaluation(evaluationList, loop)
{
	var courseID = evaluationList[loop].courseID;
	var name =  evaluationList[loop].name;
	var instructions =  evaluationList[loop].instructions;
	var start =  evaluationList[loop].start;
	var deadline =  evaluationList[loop].deadline;
	var timeZone =  evaluationList[loop].timeZone;
	var gracePeriod =  evaluationList[loop].gracePeriod;
	var status =  evaluationList[loop].status;
	var activated =  evaluationList[loop].activated;
	var commentsEnabled =  evaluationList[loop].commentsEnabled;

	clearAllDisplay();
	printEditEvaluation(courseID, name, instructions, commentsEnabled, start, deadline, timeZone, gracePeriod, status, activated);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEditStudent(courseID, email, name, teamName, googleID, registrationKey, comments)
{
	clearAllDisplay();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	printEditStudent(courseID, email, name, teamName, googleID, registrationKey, comments);
}

function displayEnrollmentPage(courseID)
{
	clearAllDisplay();
	printEnrollmentPage(courseID);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEnrollmentResultsPage(reports)
{
	clearAllDisplay();
	printEnrollmentResultsPage(reports);
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEvaluationResults(evaluationList, loop)
{
	var courseID = evaluationList[loop].courseID;
	var name =  evaluationList[loop].name;
	var instructions =  evaluationList[loop].instructions;
	var start =  evaluationList[loop].start;
	var deadline =  evaluationList[loop].deadline;
	var gracePeriod =  evaluationList[loop].gracePeriod;
	var status =  evaluationList[loop].status;
	var activated =  evaluationList[loop].activated;
	var commentsEnabled =  evaluationList[loop].commentsEnabled;

	// xl: new added
	var published = evaluationList[loop].published;
	
	clearAllDisplay();
	
	printEvaluationResultsHeader(courseID, name, start, deadline, status, activated, published);
	evaluationResultsViewStatus = evaluationResultsView.reviewer;
	
	doGetSubmissionResultsList(courseID, name, status, commentsEnabled);
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayEvaluationsTab()
{
	clearAllDisplay();
	printAddEvaluation();
	doGetCourseIDList();
	doGetEvaluationList();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function displayStudentInformation(courseID, email, name, teamName, googleID, registrationKey, comments)
{
	clearAllDisplay();
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	printStudent(courseID, email, name, teamName, googleID, registrationKey, comments);
}

function doAddCourse(courseID, name)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = addCourse(courseID, name);
	
	if(results == 0)
	{
		printAddCourse();
		doGetCourseList();
		setStatusMessage(DISPLAY_COURSE_ADDED);
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
		setStatusMessage(DISPLAY_COURSE_INVALIDID);
	}
	
	else if(results == 4)
	{
		setStatusMessage(DISPLAY_COURSE_INVALIDNAME);
	}
	
	else if(results == 5)
	{
		setStatusMessage(DISPLAY_COURSE_EXISTS);
	}
}

function doAddEvaluation(courseID, name, instructions, commentsEnabled, start, startTime, deadline, deadlineTime, timeZone, gracePeriod)
{
	setStatusMessage(DISPLAY_LOADING);

	var results = addEvaluation(courseID, name, instructions, commentsEnabled, start, startTime, deadline, 
			deadlineTime, timeZone, gracePeriod);
	
	clearStatusMessage();
	
	if(results == 0)
	{
		displayEvaluationsTab();
		setStatusMessage(DISPLAY_EVALUATION_ADDED);
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
		setStatusMessage(DISPLAY_EVALUATION_NAMEINVALID);
	}
	
	else if(results == 4)
	{
		setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID);
	}
	
	else if(results == 5)
	{
		setStatusMessage(DISPLAY_EVALUATION_EXISTS);
	}
	
	else if(results == 6)
	{
		setStatusMessage(DISPLAY_COURSE_NOTEAMS);
	}
	
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

function doDeleteCourse(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = deleteCourse(courseID);
	
	if(results != 1)
	{
		doGetCourseList();
		setStatusMessage(DISPLAY_COURSE_DELETED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doDeleteEvaluation(courseID, name)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = deleteEvaluation(courseID, name);
	
	if(results == 0)
	{
		doGetEvaluationList();
		setStatusMessage(DISPLAY_EVALUATION_DELETED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doDeleteAllStudents(courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = deleteAllStudents(courseID);
	
	if(results != 1)
	{
		doGetStudentList(courseID);
		doGetCourse(courseID);
		setStatusMessage(DISPLAY_COURSE_DELETEDALLSTUDENTS);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doDeleteStudent(courseID, email)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = deleteStudent(courseID, email);
	
	if(results != 1)
	{
		displayCourseInformation(courseID);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditEvaluation(courseID, name, editStart, editStartTime, editDeadline, editDeadlineTime, timeZone,
		editGracePeriod, editInstructions, editCommentsEnabled, activated, status)
{
	setStatusMessage(DISPLAY_LOADING);

	var results = editEvaluation(courseID, name, editStart, editStartTime, editDeadline, editDeadlineTime,
			timeZone, editGracePeriod, editInstructions, editCommentsEnabled, activated, status)
	
	if(results == 0)
	{
		if(activated == true)
		{
			displayEvaluationsTab();
			toggleInformStudentsOfEvaluationChanges(courseID, name);
		}
		
		else
		{
			displayEvaluationsTab();
			setStatusMessage(DISPLAY_EVALUATION_EDITED);
		}
		
	}
	
	else if(results == 2)
	{
		setStatusMessage(DISPLAY_FIELDS_EMPTY);
	}
	
	else if(results == 3)
	{
		setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID);
	}
	
	else if(results == 4)
	{
		displayEvaluationsTab();
		setStatusMessage(DISPLAY_EVALUATION_EDITED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doEditEvaluationResultsByReviewer(form, summaryList, position, commentsEnabled, status)
{
	toggleEditEvaluationResultsStatusMessage(DISPLAY_LOADING);
	
	var submissionList = extractSubmissionList(form);
	
	var results = editEvaluationResults(submissionList, commentsEnabled);
	
	if(results == 0)
	{
		// submissionList get from extractSubmissionList(form) is not complete
		submissionList = getSubmissionList(submissionList[0].courseID, submissionList[0].evaluationName);
		
		if(submissionList != 1){
			printEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status);
			document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
			toggleEditEvaluationResultsStatusMessage("");
			setStatusMessage(DISPLAY_EVALUATION_RESULTSEDITED);
		}
		
		else{
			alert(DISPLAY_SERVERERROR);
		}
		
		
	}
	
	else if(results == 2)
	{
		toggleEditEvaluationResultsStatusMessage(DISPLAY_FIELDS_EMPTY);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
	
}

function doEditStudent(courseID, email, editName, editTeamName, editEmail, editGoogleID, editComments)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = editStudent(courseID, email, editName, editTeamName, editEmail, editGoogleID, editComments);
	
	if(results == 0)
	{
		displayCourseInformation(courseID);
		setStatusMessage(DISPLAY_STUDENT_EDITED);
	}
	
	else if(results == 2)
	{
		displayCourseInformation(courseID);
		setStatusMessage("Duplicated Email found. Cannot edit student information");
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}



function doEnrolStudents(input, courseID)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = enrolStudents(input, courseID);
	
	clearStatusMessage();
	
	if(results == 1)
	{
		alert(DISPLAY_SERVERERROR);
	}
	
	else if(results == 2)
	{
		
	}
	
	else if(results == 3)
	{
		checkEnrollmentInput(input)
	}
	
	else
	{
		displayEnrollmentResultsPage(results);
	}
		
}

function doGetCourse(courseID) {
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getCourse(courseID);
	
	clearStatusMessage();
	
	if (results != 1) {
		printCourse(results);
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetCourseIDList()
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getCourseList();
	
	if(results != 1)
	{
		populateCourseIDOptions(results);
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

function doGetEvaluationList()
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getEvaluationList();
	
	clearStatusMessage();
	
	if(results != 1)
	{
		printEvaluationList(results);

		// Toggle calls printEvaluationList too
		if(evaluationSortStatus == evaluationSort.name)
		{
			toggleSortEvaluationsByName(results);
			
		}	

		else
		{
			toggleSortEvaluationsByCourseID(results);
		}
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}
	

function doGetStudentList(courseID) {
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getStudentList(courseID);
	
	clearStatusMessage();
	
	if (results != 1) {
		// toggleSortStudentsByName calls printStudentList too
		if (studentSortStatus == studentSort.name) {
			toggleSortStudentsByName(results, courseID);
		} else if(studentSortStatus == studentSort.status) {
			toggleSortStudentsByStatus(results, courseID);
		} else {
			toggleSortStudentsByTeamName(results, courseID);
		}
	} else {
		alert(DISPLAY_SERVERERROR);
	}
}

function doGetSubmissionResultsList(courseID, evaluationName, status, commentsEnabled)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = getSubmissionList(courseID, evaluationName); 
	
	clearStatusMessage();
	
	if(results != 1)
	{
		var compiledResults = compileSubmissionsIntoSummaryList(results);
		
		toggleSortEvaluationSummaryListByTeamName(results, compiledResults, status, commentsEnabled);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doInformStudentsOfEvaluationChanges(courseID, name)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = informStudentsOfEvaluationChanges(courseID, name);
	
	clearStatusMessage();
	
	if(results != 1)
	{
		setStatusMessage(DISPLAY_EVALUATION_INFORMEDSTUDENTSOFCHANGES);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
}

function doPublishEvaluation(courseID, name, reload)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = publishEvaluation(courseID, name);
	
	clearStatusMessage();
	
	if(results != 1)
	{
		if(reload){
			doGetEvaluationList();
		}
		else{
			document.getElementById('button_publish').value = "Unpublish";
			document.getElementById('button_publish').onclick = function() {
				togglePublishEvaluation(courseID, name, false, false);
			};
		}
		
		setStatusMessage(DISPLAY_EVALUATION_PUBLISHED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
	
}

function doUnpublishEvaluation(courseID, name, reload)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = unpublishEvaluation(courseID, name);
	
	clearStatusMessage();
	
	if(results != 1)
	{
		if(reload){
			doGetEvaluationList();
		}
		else{
			document.getElementById('button_publish').value = "Publish";
			document.getElementById('button_publish').onclick = function() {
				togglePublishEvaluation(courseID, name, true, false);
			};
		}
		
		setStatusMessage(DISPLAY_EVALUATION_UNPUBLISHED);
	}
	
	else
	{
		alert(DISPLAY_SERVERERROR);
	}
	
}

function doRemindStudents(courseID, evaluationName)
{
	setStatusMessage(DISPLAY_LOADING);
	
	var results = remindStudents(courseID, evaluationName);
	
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

/*
 * Returns
 * 
 * 0: successful 1: server error 2: fields empty 3: schedule invalid 4: no
 * changes made
 */
function editEvaluation(courseID, name, editStart, editStartTime, editDeadline, editDeadlineTime, timeZone, editGracePeriod, 
		editInstructions, editCommentsEnabled, activated, status)
{
	setStatusMessage(DISPLAY_LOADING);
	
	if(courseID == "" || name == "" || editStart == "" || editStartTime == "" || editDeadline == "" || 
			editDeadlineTime == "" || editGracePeriod == "" || editInstructions == "" || editCommentsEnabled == "")
	{
		return 2;
	}
	
	else if(!isEditEvaluationScheduleValid(editStart, editStartTime, editDeadline, editDeadlineTime, timeZone, activated, status))
	{
		return 3;
	}
	
	else
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITEVALUATION + "&" + COURSE_ID + "=" + encodeURIComponent(courseID) +
				"&" + EVALUATION_NAME + "=" + encodeURIComponent(name) + "&" + EVALUATION_DEADLINE + "=" + encodeURIComponent(editDeadline) + 
				"&" + EVALUATION_DEADLINETIME + "=" + encodeURIComponent(editDeadlineTime) +
				"&" + EVALUATION_INSTRUCTIONS + "=" + encodeURIComponent(editInstructions) + "&" + 
				EVALUATION_START + "=" + encodeURIComponent(editStart) + "&" + EVALUATION_STARTTIME + "=" + 
				encodeURIComponent(editStartTime) + "&" + EVALUATION_GRACEPERIOD + "=" + encodeURIComponent(editGracePeriod) +
				"&" + EVALUATION_COMMENTSENABLED + "=" + editCommentsEnabled);
		
		return handleEditEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: deadline passed 3: fields missing
 * 
 */
function editEvaluationResults(submissionList, commentsEnabled)
{
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].commentsToStudent == "" && commentsEnabled == true)
		{
			return 2;
		}
		
		if(submissionList[loop].justification == "")
		{
			return 2;
		}
		
		if( submissionList[loop].points == -999)
		{
			return 2;
		}
		
		if(!commentsEnabled)
		{
			submissionList[loop].commentsToStudent = "";
		}
	}
	
	var request = "operation=" + OPERATION_COORDINATOR_EDITEVALUATIONRESULTS + "&" + STUDENT_NUMBEROFSUBMISSIONS +
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
		
		return handleEditEvaluationResults();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function editStudent(courseID, email, editName, editTeamName, editEmail, editGoogleID, editComments)
{
	editName = trim(editName);
	editTeamName = trim(editTeamName);
	editEmail = trim(editEmail);
	editGoogleID = trim(editGoogleID);
	

	if(isEditStudentInputValid(editName, editTeamName, editEmail, editGoogleID))
	{
		if(xmlhttp)
		{
			xmlhttp.open("POST","teammates",false); 
			xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_EDITSTUDENT + "&" + COURSE_ID + "=" + 
				encodeURIComponent(courseID) + "&" + STUDENT_EMAIL + "=" + encodeURIComponent(email) +
				"&" + STUDENT_EDITNAME + "=" + encodeURIComponent(editName) + "&" + STUDENT_EDITTEAMNAME + "=" + encodeURIComponent(editTeamName) +
				"&" + STUDENT_EDITEMAIL + "=" + encodeURIComponent(editEmail) + "&" + STUDENT_EDITGOOGLEID + "=" + encodeURIComponent(editGoogleID) +
				"&" + STUDENT_EDITCOMMENTS + "=" + encodeURIComponent(editComments));
			return handleEditStudent();
		}
	}
}

/*
 * Returns
 * 
 * reports: successful 1: server error 2: input empty 3: input invalid
 * 
 */
function enrolStudents(input, courseID)
{
	input = replaceAll(input,"|","\t");
	
	if(xmlhttp)
	{
		// Remove trailing "\n"
		if(input.lastIndexOf("\n") == input.length-1)
		{
			input = input.substring(0, input.length-1);
		}
		
		if(input == "")
		{
			return 2;
		}
		
		else if(isEnrollmentInputValid(input))
		{	
			xmlhttp.open("POST","teammates",false); 
			xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
			xmlhttp.send("operation=" + OPERATION_COORDINATOR_ENROLSTUDENTS + "&" + STUDENT_INFORMATION
				+ "=" + encodeURIComponent(input) + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
			
			return handleEnrolStudents();
		}
		
		else
		{
			return 3;
		}
	}
}

function extractSubmissionList(form)
{
	var submissionList = [];
	
	var counter = 0;
	var fromStudent;
	var toStudent;
	var courseID;
	var evaluationName;
	var teamName;
	var points;
	var justification;
	var commentsToStudent;
	
	for(loop = 0; loop < form.length; loop++)
	{
		fromStudent = form.elements[loop++].value;
		toStudent = form.elements[loop++].value;
		teamName = form.elements[loop++].value;
		courseID = form.elements[loop++].value;
		evaluationName = form.elements[loop++].value;
		
		points = form.elements[loop++].value;
		justification = form.elements[loop++].value;
		commentsToStudent = form.elements[loop].value;
		
		
		submissionList[counter++] = {fromStudent:fromStudent, toStudent:toStudent, courseID:courseID,
				evaluationName:evaluationName, teamName:teamName, points:points,
				justification:justification, commentsToStudent:commentsToStudent};
	}
	
	return submissionList;
}

/*
 * Returns
 * 
 * courseInfo: successful 1: server error
 * 
 */
function getCourse(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETCOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
		
		return handleGetCourse();
	}
}

/*
 * Returns
 * 
 * courseList: successful 1: server error
 * 
 */
function getCourseList()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETCOURSELIST); 
		
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
 * evaluationList: successful 1: server error
 * 
 */
function getEvaluationList()
{
	if(xmlhttp)
	{
		OPERATION_CURRENT = OPERATION_COORDINATOR_GETEVALUATIONLIST;
		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETEVALUATIONLIST); 
		
		return handleGetEvaluationList();
	}
}

// return the value of the radio button that is checked
// return an empty string if none are checked, or
// there are no radio buttons
function getCheckedValue(radioObj) {
	if(!radioObj)
		return "";
	var radioLength = radioObj.length;
	if(radioLength == undefined)
		if(radioObj.checked)
			return radioObj.value;
		else
			return "";
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
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
function getStudentList(courseID)
{
	if(xmlhttp)
	{
		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSTUDENTLIST + "&" + COURSE_ID + "=" + encodeURIComponent(courseID)); 
		
		return handleGetStudentList();
	}
}

/*
 * Returns
 * 
 * submissionList: successful 1: server error
 * 
 */
function getSubmissionList(courseID, evaluationName)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSUBMISSIONLIST + "&" + COURSE_ID + "=" +
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
 * 0: successful 1: server error 5: course exists
 * 
 */
function handleAddCourse()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		var message;
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			
			if(message == MSG_COURSE_EXISTS)
			{
				return 5;
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
 * 0: successful 1: server error 5: evaluation exists 6: course has no teams
 * 
 */
function handleAddEvaluation()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		var message;
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			
			if(message == MSG_EVALUATION_EXISTS)
			{
				return 5;
			}
			
			else if(message == MSG_COURSE_NOTEAMS)
			{
				return 6;
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
function handleArchiveCourse()
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
 * 0: successful 1: server error
 * 
 */
function handleDeleteAllStudents(courseID)
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
 * 0: successful 1: server error
 * 
 */
function handleDeleteCourse()
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
 * 0: successful 1: server error
 * 
 */
function handleDeleteEvaluation()
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
 * 0: successful 1: server error
 * 
 */
function handleDeleteStudent()
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
 * 0: successful 1: server error 4: no changes made
 * 
 */
function handleEditEvaluation()
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
 * 0: successful 1: server error
 * 
 */
function handleEditEvaluationResults()
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
 * 0: successful 1: server error 2: unable to change teams
 * 
 */
function handleEditStudent()
{
	if (xmlhttp.status == 200) 
	{
		var status = xmlhttp.responseXML.getElementsByTagName("status")[0];
		
		if(status != null)
		{
			var message = status.firstChild.nodeValue;
			
			if(message == MSG_EVALUATION_UNABLETOCHANGETEAMS)
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
 * reports: successful 1: server error
 * 
 */
function handleEnrolStudents()
{
	if (xmlhttp.status == 200) 
	{
		var enrollmentReports = xmlhttp.responseXML.getElementsByTagName("enrollmentreports")[0]; 
		
		if(enrollmentReports != null)
		{
			var enrollmentReport;
			var studentName;
			var studentEmail;
			var status;
			var nameEdited;
			var teamNameEdited;
			var commentsEdited;
			
			var reports = [];
			
			for(loop = 0; loop < enrollmentReports.childNodes.length; loop++) 
			{ 
				enrollmentReport = enrollmentReports.childNodes[loop]; 
				
				studentName = enrollmentReport.getElementsByTagName(STUDENT_NAME)[0].firstChild.nodeValue;
				studentEmail = enrollmentReport.getElementsByTagName(STUDENT_EMAIL)[0].firstChild.nodeValue;
				status = enrollmentReport.getElementsByTagName(STUDENT_STATUS)[0].firstChild.nodeValue;
				nameEdited = enrollmentReport.getElementsByTagName(STUDENT_NAMEEDITED)[0].firstChild.nodeValue;
				teamNameEdited = enrollmentReport.getElementsByTagName(STUDENT_TEAMNAMEEDITED)[0].firstChild.nodeValue;
				commentsEdited = enrollmentReport.getElementsByTagName(STUDENT_COMMENTSEDITED)[0].firstChild.nodeValue;
				
				enrollmentReport = {studentName:studentName, studentEmail:studentEmail, 
						nameEdited:nameEdited, teamNameEdited:teamNameEdited, commentsEdited:commentsEdited, status:status}; 
				

				reports.push(enrollmentReport);

			}
		}
		
		return reports;
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
function handleInformStudentsOfEvaluationChanges()
{
	if (xmlhttp.status == 200) 
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
 * courseInfo: successful 1: server error
 * 
 */
function handleGetCourse()
{
	if (xmlhttp.status == 200) 
	{
		var courses = xmlhttp.responseXML.getElementsByTagName("courses")[0]; 
		var courseInfo;
		
		if(courses != null) 
		{ 
			var course; 
			var ID; 
			var name; 
			var numberofteams;
			var status;
			
			course = courses.childNodes[0]; 
			ID =  course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
			name = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
			numberOfTeams = course.getElementsByTagName(COURSE_NUMBEROFTEAMS)[0].firstChild.nodeValue;
			status = course.getElementsByTagName(COURSE_STATUS)[0].firstChild.nodeValue;
			courseInfo = {ID:ID, name:name, numberOfTeams:numberOfTeams, status:status}; 
			
			return courseInfo;
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
 * courseIDList: successful 1: server error
 * 
 */
function handleGetCourseIDList()
{
	if (xmlhttp.status == 200) 
	{
		clearStatusMessage();
		
		var courses = xmlhttp.responseXML.getElementsByTagName("courses")[0]; 
		var course;
		var courseID;
		var courseIDList = new Array();
		
		for(loop = 0; loop < courses.childNodes.length; loop++) 
		{ 
			course = courses.childNodes[loop]; 
			courseID =  course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
			courseIDList[loop] = {courseID:courseID};
		}
		
		return courseIDList;
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * courseList: successful 1: server error
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
			var ID; 
			var name; 
			var numberOfTeams;
			var status;
			
			for(loop = 0; loop < courses.childNodes.length; loop++) 
			{ 
				course = courses.childNodes[loop]; 
				ID =  course.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				name = course.getElementsByTagName(COURSE_NAME)[0].firstChild.nodeValue;
				numberOfTeams = course.getElementsByTagName(COURSE_NUMBEROFTEAMS)[0].firstChild.nodeValue;
				status = course.getElementsByTagName(COURSE_STATUS)[0].firstChild.nodeValue;
				courseList[loop] = {ID:ID, name:name, numberOfTeams:numberOfTeams, status:status}; 
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
 * evaluationList: successful 1: server error
 * 
 */
function handleGetEvaluationList()
{
	if (xmlhttp.status == 200) 
	{
		var evaluations = xmlhttp.responseXML.getElementsByTagName("evaluations")[0];
		var evaluationList = new Array(); 
		var now;
		var nowMilliS;
		var nowTime;
		var localOffset;
		var UTC;
		
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
		var status;
		var activated;
		
		if(evaluations != null)
		{
			for(loop = 0; loop < evaluations.childNodes.length; loop++)
			{
				evaluation = evaluations.childNodes[loop];
				
				courseID = evaluation.getElementsByTagName(COURSE_ID)[0].firstChild.nodeValue;
				name = evaluation.getElementsByTagName(EVALUATION_NAME)[0].firstChild.nodeValue;
				commentsEnabled = (evaluation.getElementsByTagName(EVALUATION_COMMENTSENABLED)[0].firstChild.nodeValue.toLowerCase() == "true");
				instructions = evaluation.getElementsByTagName(EVALUATION_INSTRUCTIONS)[0].firstChild.nodeValue;
				start = new Date(evaluation.getElementsByTagName(EVALUATION_START)[0].firstChild.nodeValue);
				deadline = new Date(evaluation.getElementsByTagName(EVALUATION_DEADLINE)[0].firstChild.nodeValue);
				timeZone = parseFloat(evaluation.getElementsByTagName(EVALUATION_TIMEZONE)[0].firstChild.nodeValue);
				gracePeriod = parseInt(evaluation.getElementsByTagName(EVALUATION_GRACEPERIOD)[0].firstChild.nodeValue);
				published = (evaluation.getElementsByTagName(EVALUATION_PUBLISHED)[0].firstChild.nodeValue.toLowerCase() == "true");
				activated = (evaluation.getElementsByTagName(EVALUATION_ACTIVATED)[0].firstChild.nodeValue.toLowerCase() == "true");
				numberOfCompletedEvaluations = parseInt(evaluation.getElementsByTagName(EVALUATION_NUMBEROFCOMPLETEDEVALUATIONS)[0].firstChild.nodeValue);
				numberOfEvaluations = parseInt(evaluation.getElementsByTagName(EVALUATION_NUMBEROFEVALUATIONS)[0].firstChild.nodeValue);

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
				
				if(published == true)
				{
					status = "PUBLISHED";
				}
				
				evaluationList[loop] = { courseID:courseID, name:name, commentsEnabled:commentsEnabled, instructions:instructions,
						start:start, deadline:deadline, timeZone:timeZone, gracePeriod:gracePeriod, published:published, 
						published:published, activated:activated, numberOfCompletedEvaluations:numberOfCompletedEvaluations, 
						numberOfEvaluations:numberOfEvaluations, status:status};
			}
		}
		
		return evaluationList;
	}
	else
	{
		1;
	}
}

/*
 * Returns
 * 
 * studentList: successful 1: server error
 * 
 */
function handleGetStudentList()
{
	if (xmlhttp.status == 200) 
	{
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
		
		for(var loop = 0; loop < students.childNodes.length; loop++)
		{
			student = students.childNodes[loop];
		
			name = student.getElementsByTagName(STUDENT_NAME)[0].firstChild.nodeValue;
			teamName = student.getElementsByTagName(STUDENT_TEAMNAME)[0].firstChild.nodeValue;
			email = student.getElementsByTagName(STUDENT_EMAIL)[0].firstChild.nodeValue;
			registrationKey = student.getElementsByTagName(STUDENT_REGKEY)[0].firstChild.nodeValue;
			googleID = student.getElementsByTagName(STUDENT_ID)[0].firstChild.nodeValue;
			comments = student.getElementsByTagName(STUDENT_COMMENTS)[0].firstChild.nodeValue;
			courseID = student.getElementsByTagName(STUDENT_COURSEID)[0].firstChild.nodeValue;
			studentList[loop] = {name:name, teamName:teamName, email:email, registrationKey:registrationKey, googleID:googleID,
									comments:comments, courseID:courseID};
		}
		
		return studentList;
	}
	
	else
	{
		return 1;
	}
}

/*
 * Returns
 * 
 * submissionList: successful 1: server error
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
 * 0: successful 1: server error
 * 
 */
function handlePublishEvaluation()
{
	if (xmlhttp.status == 200) 
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
 * 0: successful 1: server error
 * 
 */
 function handleUnpublishEvaluation()
{
	if (xmlhttp.status == 200) 
	{
		return 0;
	}
	
	else
	{
		return 1;
	}	
}

function handleRemindStudents()
{
	if (xmlhttp.status == 200) 
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
 * 0: successful 1: server error
 * 
 */
function handleSendRegistrationKey()
{
	if (xmlhttp.status == 200) 
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
 * 0: successful 1: server error
 */
function handleSendRegistrationKeys()
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
 * 0: successful 1: server error
 * 
 */
function handleUnarchiveCourse()
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

function informStudentsOfEvaluationChanges(courseID, name)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES 
				+ "&" + COURSE_ID + "="	+ encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" 
				+ encodeURIComponent(name));
	}
	
	return handleInformStudentsOfEvaluationChanges();
}

function isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime)
{
	var start = convertDateFromDDMMYYYYToMMDDYYYY(start);
	var deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);
	
	var now = new Date();
	
	start = new Date(start);
	deadline = new Date(deadline);
	
	if(startTime != "24")
	{
		start.setHours(parseInt(startTime));
	}
	
	else
	{
		start.setHours(23);
		start.setMinutes(59);
	}
	
	if(deadlineTime != "24")
	{
		deadline.setHours(parseInt(deadlineTime));
	}
	
	else
	{
		deadline.setHours(23);
		deadline.setMinutes(59);
	}
	
	if(start > deadline)
	{
		return false;
	}
	
	else if(now > start)
	{
		return false;
	}

	else if(!(start > deadline || deadline > start)) 
	{
		if(parseInt(startTime) >= parseInt(deadlineTime))
		{
			return false;
		}
	}
	
	return true;
}

function isCourseIDValid(courseID)
{
	if(courseID.indexOf("\\") >= 0 || courseID.indexOf("'") >= 0 || courseID.indexOf("\"") >= 0)
	{
		return false;
	}
	
	if(courseID.match(/^[a-zA-Z0-9.-]*$/) == null)
	{
		return false;
	}
	
	if(courseID.length > 20)
	{
		return false;
	}
	
	return true;

}

function isCourseNameValid(courseName)
{
	if(courseName.length > 38)
	{
		return false;
	}
	
	return true;

}

function isEditEvaluationScheduleValid(start, startTime, deadline, deadlineTime, timeZone, activated, status)
{
	var startString = convertDateFromDDMMYYYYToMMDDYYYY(start);
	var deadlineString = convertDateFromDDMMYYYYToMMDDYYYY(deadline);
	
	var now = getDateWithTimeZoneOffset(timeZone);

	start = new Date(startString);
	deadline = new Date(deadlineString);

	if(startTime != "24")
	{
		start.setHours(parseInt(startTime));
	}
	
	else
	{
		start.setHours(23);
		start.setMinutes(59);
	}
	
	if(deadlineTime != "24")
	{
		deadline.setHours(parseInt(deadlineTime));
	}
	
	else
	{
		deadline.setHours(23);
		deadline.setMinutes(59);
	}
	
	if(start > deadline)
	{
		return false;
	}
	
	else if(status == "AWAITING"){
		// Open evaluation should be done by system only.
		// Thus, coordinator cannot change evaluation ststus from AWAITING to
		// OPEN
		if(start < now){
			return false;
		}
	}
	
// else if(now > deadline)
// {
// return false;
// }
//
// else if(!(start > deadline || deadline > start))
// {
// if(parseInt(startTime) >= parseInt(deadlineTime))
// {
// return false;
// }
// }
//	
// else if(!activated && start < now)
// {
// return false;
// }
//	
	return true;
}

function isEditStudentInputValid(editName, editTeamName, editEmail, editGoogleID)
{
	if(editName == "" || editTeamName == "" || editEmail == "")
	{
		return false;
	}
	
	if(!isStudentNameValid(editName))
	{
		return false;
	}
	
	else if(!isStudentEmailValid(editEmail))
	{
		return false;
	}
	
	else if(!isStudentTeamNameValid(editTeamName))
	{
		return false;
	}
	
	return true;
}

function isEnrollmentInputValid(input)
{
	var entries = input.split("\n");
	var fields;
	
	for(var x = 0; x < entries.length; x++)
	{
		if(entries[x] != ""){
			// Separate the fields
			fields = entries[x].split("\t");
			
			// Make sure that all fields are present
			if(fields.length < 3)
			{
				return false;
			}
			
			else if(fields.length > 4)
			{
				return false;
			}
			
			// Check that fields are correct
			if(!isStudentNameValid(trim(fields[1])))
			{
				return false;
			}
			
			else if(!isStudentEmailValid(trim(fields[2])))
			{
				return false;
			}
			
			else if(!isStudentTeamNameValid(trim(fields[0])))
			{
				return false;
			}
		}
	}
	
	return true;
}

function isEvaluationNameValid(name)
{
	if(name.indexOf("\\") >= 0 || name.indexOf("'") >= 0 || name.indexOf("\"") >= 0)
	{
		return false;
	}
	
	if(name.match(/^[a-zA-Z0-9 ]*$/) == null)
	{
		return false;
	}
	
	if(name.length > 22)
	{
		return false;
	}
	
	return true;
}

function isStudentEmailValid(email)
{
	if(email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i) != null && email.length <= 40)
	{
		return true;
	}

	return false;
}

function isStudentNameValid(name)
{
	if(name.indexOf("\\") >= 0 || name.indexOf("'") >= 0 || name.indexOf("\"") >= 0)
	{
		return false;
	}
	
	else if(name.match(/^.[^\t]*$/) == null)
	{
		return false;
	}
	
	else if(name.length > 40)
	{
		return false;
	}
	
	return true;
}

function isStudentTeamNameValid(teamName)
{
	if(teamName.length > 24)
	{
		return false;
	}
	
	return true;
}

function logout()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_LOGOUT);
	}
	
	handleLogout();
}

function populateCourseIDOptions(courseList)
{
	var option = document.createElement("OPTION");

	for(x = 0; x < courseList.length; x++)
	{
		option = document.createElement("OPTION");
		option.text = courseList[x].ID;
		option.value = courseList[x].ID;
		document.form_addevaluation.courseid.options.add(option);
	}
}

function populateEditEvaluationResultsPointsForm(form, submissionList, commentsEnabled)
{
	var points;
	
	for(x = 0; x < form.elements.length / 8; x++)
	{
		for(y = 0; y < submissionList.length; y++)
		{
			if(submissionList[y].fromStudent == form.elements[x*8].value && 
					submissionList[y].toStudent == form.elements[x*8+1].value)
			{
				points = submissionList[y].points;
				break;
			}
		}
		
		setSelectedIndex(form.elements[x*8+5], points);
	}
}

function printAddCourse() {
	var outputHeader = "<h1>ADD NEW COURSE</h1>";
	
	var outputForm = "" +
	"<form method=\"post\" action=\"\" name=\"form_addcourse\">" +
	"<table id=\"data\">" +
	"<tr>" +
	"<td class=\"fieldname\">Course ID:</td>" +
	"<td><input class=\"fieldvalue\" type=\"text\" name=\"" + COURSE_ID + "\" id=\"" + COURSE_ID + "\"" +
	"onmouseover=\"ddrivetip('Enter the identifier of the course. For e.g., CS3215Sem1.')\"" +
	"onmouseout=\"hideddrivetip()\" maxlength=20 tabindex=1 /></td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"fieldname\">Course Name:</td>" +
	"<td><input class = \"fieldvalue\" type=\"text\" name=\"" + COURSE_NAME + "\" id=\"" + COURSE_NAME + "\"" +
	"onmouseover=\"ddrivetip('Enter the name of the course. For e.g., Software Engineering.')\"" +
	"onmouseout=\"hideddrivetip()\" maxlength=38 tabindex=2 /></td>" +
	"</tr>" +
    "<tr>" +
	"<td>&nbsp;</td>" +
	"<td><input id='btnAddCourse' type=\"button\" class=\"button\" onclick=\"doAddCourse(this.form." + COURSE_ID + ".value, this.form." + COURSE_NAME + ".value);\" value=\"Add course\" tabindex=\"3\" /></td>" +
	"</tr>" +
	"</table>" +
	"</form>";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_COURSE_MANAGEMENT).innerHTML = outputForm;
}

function printAddEvaluation() {
	var outputHeader;
	outputHeader = "<h1>ADD NEW EVALUATION</h1>";
	
	var outputForm = "" +
	"<form method=\"post\" action=\"\" name=\"form_addevaluation\">" +
	"<table id=\"data\">" +
	"<tr>" +
	"<td class=\"fieldname\">Course ID:</td>" +
	"<td class=\"inputField\"><select style=\"width: 255px;\" name=\"" + COURSE_ID + "\" id=\"" + COURSE_ID + "\"" +
	"onmouseover=\"ddrivetip('Please select the course for which the evaluation is to be created.')\"" +
	"onmouseout=\"hideddrivetip()\" tabindex=1></select></td>" +
	"<td class=\"fieldname\" style=\"width: 100px;\">Opening time:</td>" +
	"<td class=\"inputField\"><input style=\"width: 100px;\" type=\"text\" name=\"" + EVALUATION_START + "\" id=\"" + EVALUATION_START + "\" + " +
	"onClick =\"cal.select(document.forms['form_addevaluation']." + EVALUATION_START + ",'" + EVALUATION_START + "','dd/MM/yyyy')\"" + 
	"onmouseover=\"ddrivetip('Please enter the start date for the evaluation.')\"" +
	"onmouseout=\"hideddrivetip()\" READONLY tabindex=3> @ " +
	"<select style=\"width: 70px;\" name=\"" + EVALUATION_STARTTIME + "\" id=\"" + EVALUATION_STARTTIME + "\" tabindex=4>" + 
	getTimeOptionString() +
	"</select></td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"fieldname\">Evaluation name:</td>" +
	"<td class=\"inputField\"><input class = \"fieldvalue\" type=\"text\" name=\"" + EVALUATION_NAME + "\" id=\"" + EVALUATION_NAME + 
	"\" onmouseover=\"ddrivetip('Enter the name of the evaluation e.g. Mid-term.')\"" +
	"onmouseout=\"hideddrivetip()\" maxlength = 22 tabindex=2> </td>" + 
	"<td class=\"fieldname\" style=\"width: 100px;\">Closing time:</td>" +
	"<td class=\"inputField\"> <input style=\"width: 100px;\" type=\"text\" name=\"" + EVALUATION_DEADLINE + "\" id=\"" + EVALUATION_DEADLINE + "\" + " +
	"onClick =\"cal.select(document.forms['form_addevaluation']." + EVALUATION_DEADLINE + ",'" + EVALUATION_DEADLINE + "','dd/MM/yyyy')\"" + 
	"onmouseover=\"ddrivetip('Please enter deadline for the evaluation.')\"" +
	"onmouseout=\"hideddrivetip()\" READONLY tabindex=5> @ " +
	"<select style=\"width: 70px;\" name=\"" + EVALUATION_DEADLINETIME + "\" id=\"" + EVALUATION_DEADLINETIME + "\" tabindex=6>" + 
	"<option value=\"1\">0100H</option>" +
	"<option value=\"2\">0200H</option>" +
	"<option value=\"3\">0300H</option>" +
	"<option value=\"4\">0400H</option>" +
	"<option value=\"5\">0500H</option>" +
	"<option value=\"6\">0600H</option>" +
	"<option value=\"7\">0700H</option>" +
	"<option value=\"8\">0800H</option>" +
	"<option value=\"9\">0900H</option>" +
	"<option value=\"10\">1000H</option>" +
	"<option value=\"11\">1100H</option>" +
	"<option value=\"12\">1200H</option>" +
	"<option value=\"13\">1300H</option>" +
	"<option value=\"14\">1400H</option>" +
	"<option value=\"15\">1500H</option>" +
	"<option value=\"16\">1600H</option>" +
	"<option value=\"17\">1700H</option>" +
	"<option value=\"18\">1800H</option>" +
	"<option value=\"19\">1900H</option>" +
	"<option value=\"20\">2000H</option>" +
	"<option value=\"21\">2100H</option>" +
	"<option value=\"22\">2200H</option>" +
	"<option value=\"23\">2300H</option>" +
	"<option value=\"24\" SELECTED>2359H</option>" +
	"</select></td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"fieldname\">Peer feedback:</td>" +
	"<td class=\"inputField\">" +
	"<input type=\"radio\" name=\"" + EVALUATION_COMMENTSENABLED + "\" id=\"" + EVALUATION_COMMENTSENABLED + "\" value=\"true\" CHECKED " +
	"onmouseover=\"ddrivetip('Enable this if you want students to give anonymous feedback to team members. You can moderate those peer feedback before publishing it to the team.')\"" +
	"onmouseout=\"hideddrivetip()\" >Enabled&nbsp;&nbsp;" +
	"<input type=\"radio\" name=\"" + EVALUATION_COMMENTSENABLED + "\" id=\"" + EVALUATION_COMMENTSENABLED + "\" value=\"false\" " +
	"onmouseover=\"ddrivetip('Enable this if you want students to give anonymous feedback to team members. You can moderate those peer feedback before publishing it to the team')\"" +
	"onmouseout=\"hideddrivetip()\" >Disabled" +
	"</td>" +
	"<td class=\"fieldname\" style=\"width: 100px;\">Time zone:</td>" +
	"<td class=\"inputField\">" +
	"<select style=\"width: 100px;\" name=\"" + EVALUATION_TIMEZONE + "\" id=\"" + EVALUATION_TIMEZONE + 
	"\" onmouseover=\"ddrivetip('Daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is daylight saving,<br /> you should choose UTC -7:00 and its corresponding timings.')\"" +
	"onmouseout=\"hideddrivetip()\" tabindex=7>" +
	getTimezoneOptionString() +
	"</select></td>" +
	"</tr>" +
	"<tr>" +
	"<td>&nbsp;</td>" +
    "<td>&nbsp;</td>" +
    "<td class=\"fieldname\" style=\"width: 100px;\">Grace Period:</td>" +
	"<td class=\"inputField\">" +
	"<select style=\"width: 70px;\" name=\"" + EVALUATION_GRACEPERIOD + "\" id=\"" + EVALUATION_GRACEPERIOD + 
	"\" onmouseover=\"ddrivetip('Please select the amount of time that the system will continue accepting <br />submissions after" +
	" the specified deadline.')\" onmouseout=\"hideddrivetip()\" tabindex=7>" +
	getGracePeriodOptionString() +
	"</select></td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"fieldname\">Instructions to students:</td>" +
    "<td colspan=\"3\">" +
    "<textarea rows=\"2\" cols=\"100\" class=\"textvalue\"type=\"text\" name=\"" + EVALUATION_INSTRUCTIONS + 
	"\" id=\"" + EVALUATION_INSTRUCTIONS + "\"" +
	"onmouseover=\"ddrivetip('Please enter instructions for your students. For e.g., Avoid comments which are too critical.')\"" +
	"onmouseout=\"hideddrivetip()\" tabindex=8>Please submit your peer evaluation based on the overall contribution of your teammates so far.</textarea>" +
    "</td>" +
    "</tr>" +
    "<tr>" +
	"<td class=\"fieldname\">&nbsp;</td>" +
    "<td colspan=\"3\">" +
 	"<input id='t_btnAddEvaluation' type=\"button\" class=\"button\" onclick=\"doAddEvaluation(this.form." + COURSE_ID + ".value, " +
 			"this.form." + EVALUATION_NAME + ".value, this.form." + EVALUATION_INSTRUCTIONS + ".value, " +
 			"getCheckedValue(this.form." + EVALUATION_COMMENTSENABLED + "), this.form." + EVALUATION_START + ".value, " +
 			"this.form." + EVALUATION_STARTTIME + ".value, this.form." + EVALUATION_DEADLINE + ".value, " +
 			"this.form." + EVALUATION_DEADLINETIME + ".value, this.form." + EVALUATION_TIMEZONE + ".value, " +
 			"this.form." + EVALUATION_GRACEPERIOD + ".value);\" value=\"Create Evaluation\" tabindex=9 />" +
    "</td>" +
    "</tr>" +
	"</table>" +
	"</form>";
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_EVALUATION_MANAGEMENT).innerHTML = outputForm;
	
	var now = new Date();
	
	var currentDate = convertDateToDDMMYYYY(now);
	
	var hours = convertDateToHHMM(now).substring(0,2);
	var currentTime;
	
	if (hours.substring(0,1) == "0") {
		currentTime = (parseInt(hours.substring(1,2)) + 1) % 24;
	} else {
		currentTime = (parseInt(hours.substring(0,2)) + 1) % 24;
	}
	
	var timeZone = -now.getTimezoneOffset() / 60;
	
	document.getElementById(EVALUATION_START).value = currentDate;
	document.getElementById(EVALUATION_STARTTIME).value = currentTime;
	document.getElementById(EVALUATION_TIMEZONE).value = timeZone;
}

function printAllEvaluationResultsByReviewee(submissionList, summaryList, status, commentsEnabled) {
// document.getElementById('button_viewbytype').onclick = function() {
// document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// };
//	  
// document.getElementById('button_viewbytype').value = "Back to summary";
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	clearStatusMessage();
	
	var output = "";
	
	for (x = 0; x < summaryList.length; x++) {
		var toStudent = summaryList[x].toStudent;

		// Team Field Set
		if (x == 0 || summaryList[x].teamName != summaryList[x-1].teamName) {
			output = output +
							"<table id=\"data\">" +
								"<thead>" +
									"<p class=\"splinfo2\">TEAM: " + summaryList[x].teamName + "</p>" +
								"</thead>";
		} else {
			output = output +
							"<tr>" +
							"<td colspan=\"4\" style=\"background: #fff;\">&nbsp</td>" +
							"</tr>";
		}
		// ...Self Evaluation
		output = output +
						"<tr>" +
							"<td colspan=\"2\" class=\"reportheader\">" + summaryList[x].toStudentName.toUpperCase() + "'s Self Evaluation</td>" +
						"</tr>" +
						"<tr>" +
							"<td class=\"lhs\">Claimed contribution:</td>" +
							"<td>" + summaryList[x].claimedPoints + "</td>" +
						"</tr>" +
						"<tr>" +
							"<td class=\"lhs\">Perceived contribution:</td>" +
							"<td>" + summaryList[x].average + "</td>" +
						"</tr>";
		// ...Peer Evaluation
		var outputTemp = "<tr>" + 
						 "<td colspan = \"2\" class = \"reportheader\"> Peer Evaluations From Other Team Members</td>" +
						 "</tr>" +
						 
						 "<tr>" +
						 "<td colspan = \"2\"><table id=\"dataform\">" +
							"<tr>" +
								"<th class=\"centeralign\">From Student&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
								"<th class=\"leftalign\">Contribution&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcontribution\"></th>" +
								"<th class=\"leftalign\">Comments&nbsp;&nbsp;&nbsp;</th>" + 
								"<th class=\"leftalign\">Message&nbsp;&nbsp;&nbsp;</th>" +
							"</tr>";
		var points;
		var justification = "";
		var commentsToStudent = "";
		
		for (y = 0; y < submissionList.length; y++) {
			if (submissionList[y].toStudent == toStudent) {
				// Extract data
				if (submissionList[y].points == -999) {
					points = "N/A";
				} else if (submissionList[y].points == -101) {
					points = "Unsure";
				} else {
					points = Math.round(submissionList[y].points * submissionList[y].pointsBumpRatio);
				}
				
				if (submissionList[y].justification == "") {
					justification = "N/A";
				} else {
					justification = submissionList[y].justification;
				}
				
				if (commentsEnabled == true) {
					if (submissionList[y].commentsToStudent == "") {
						commentsToStudent = "N/A";
					} else {
						commentsToStudent = submissionList[y].commentsToStudent;
					}
				} else {
					commentsToStudent = "Disabled";
				}
				
				// Print data
				if (submissionList[y].fromStudent == submissionList[y].toStudent) {
					outputTemp = "<tr>" +
									"<td class=\"lhs\">Self evaluation:</td>" +
									"<td>" + sanitize(justification) + "</td>" +
									"</tr>" +
									"<td class=\"lhs\">Comments about team:</td>" +
									"<td>" + sanitize(commentsToStudent) + "</td>" +
									"</tr>" +
									outputTemp;
				} else {
					outputTemp = outputTemp +
											"<tr>" +
												"<td class=\"reportheader\">" + submissionList[y].fromStudentName.toUpperCase() + "</td>" +
												"<td>" + points + "</td>" +
												"<td>" + sanitize(justification) + "</td>" +
												"<td>" + sanitize(commentsToStudent) + "</td>" +
											"</tr>";
				}
			}
		}
		output = output + outputTemp + "</table></td></tr>";
	}
	
	output = output +
					"</table>" +
					"<br /><br />" +
					"<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Back\" />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_back').onclick = function() { 
		printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled);
	};
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function printAllEvaluationResultsByReviewer(submissionList, summaryList, status, commentsEnabled) {
// document.getElementById('button_viewbytype').onclick = function() {
// document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// };
//	  
// document.getElementById('button_viewbytype').value = "Back to summary";
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	clearStatusMessage();
	
	var output = "";
	
	for (x = 0; x < summaryList.length; x++) {
		var toStudent = summaryList[x].toStudent;

		// Team Field Set
		if (x == 0 || summaryList[x].teamName != summaryList[x-1].teamName) {
			output = output +
							"<table id=\"data\">" +
								"<thead>" +
									"<p class = \"splinfo2\">TEAM: " + summaryList[x].teamName + "</p>" +
								"</thead>";
		} else {
			output = output +
							"<tr>" +
							"<td colspan=\"4\" style=\"background: #fff;\">&nbsp</td>" +
							"</tr>";
		}
		
		// ...Self Evaluation
		output = output + "<tr>" +
							"<td colspan=\"2\" class=\"reportheader\">" + summaryList[x].toStudentName.toUpperCase() + "'s Self Evaluation" + "</td>" +
						  "</tr>" +
						  "<tr>" +
							"<td class=\"lhs\">Claimed contribution:</td>" +
							"<td>" + summaryList[x].claimedPoints + "</td>" +
						  "</tr>" +
						  "<tr>" +
							"<td class=\"lhs\">Perceived contribution:</td>" +
							"<td>" + summaryList[x].average + "</td>" +
						  "</tr>";

		// ...Peer Evaluations For Other Team Members
		var outputTemp = "<tr>" + 
						 "<td colspan = \"2\" class = \"reportheader\"> Peer Evaluations For Other Team Members</td>" +
						 "</tr>" +
						 
						 "<tr>" +
						 "<td colspan = \"2\"><table id=\"dataform\">" +
							"<tr>" +
								"<th class=\"centeralign\">For Student&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
								"<th class=\"leftalign\">Contribution&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcontribution\"></th>" +
								"<th class=\"leftalign\">Comments&nbsp;&nbsp;&nbsp;</th>" + 
								"<th class=\"leftalign\">Message&nbsp;&nbsp;&nbsp;</th>" +
							"</tr>";
		var points;
		var justification = "";
		var commentsToStudent = "";
		
		for (loop = 0; loop < submissionList.length; loop++) {	
			if (submissionList[loop].fromStudent == toStudent) {
				// Extract data
				if (submissionList[loop].points == -999) {
					points = "N/A";
				} else if (submissionList[loop].points == -101) {
					points = "Unsure";
				} else {
					points = Math.round(submissionList[loop].points * submissionList[loop].pointsBumpRatio);
				}
				
				if (submissionList[loop].justification == "") {
					justification = "N/A";
				} else {
					justification = submissionList[loop].justification;
				}
				
				if (commentsEnabled == true) {
					if (submissionList[loop].commentsToStudent == "") {
						commentsToStudent = "N/A";
					} else {
						commentsToStudent = submissionList[loop].commentsToStudent;
					}
				} else {
					commentsToStudent = "Disabled";
				}
				
				// Print data
				if (submissionList[loop].fromStudent == submissionList[loop].toStudent) {
					outputTemp = "<tr>" +
									"<td class=\"lhs\">Self evaluation:</td>" +
									"<td>" + sanitize(justification) + "</td>" +
									"</tr>" +
									"<td class=\"lhs\">Comments about team:</td>" +
									"<td>" + sanitize(commentsToStudent) + "</td>" +
									"</tr>" +
									outputTemp;
				} else {
					outputTemp = outputTemp +
											"<tr>" +
												"<td class=\"reportheader\">" + submissionList[loop].toStudentName.toUpperCase() + "</td>" +
												"<td>" + points + "</td>" +
												"<td>" + sanitize(justification) + "</td>" +
												"<td>" + sanitize(commentsToStudent) + "</td>" +
											"</tr>";
				}
			}
		}
		output = output + outputTemp + "</table></td></tr>";
	}
	
	output = output +
					"</table>" +
					"<br /><br />" +
					"<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Back\" />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_back').onclick = function() { 
		printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled);
	};
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function printEvaluationDetailByRevieweeList(submissionList, summaryList, status, commentsEnabled) {
	
	clearStatusMessage();
	
	var output = "";
	
	for (x = 0; x < summaryList.length; x++) {
		var toStudent = summaryList[x].toStudent;

		// Team Field Set
		if (x == 0 || summaryList[x].teamName != summaryList[x-1].teamName) {
			output = output +
							"<table id=\"data\">" +
								"<thead>" +
									"<p class=\"splinfo2\">TEAM: " + summaryList[x].teamName + "</p>" +
								"</thead>";
		} else {
			output = output +
							"<tr>" +
							"<td colspan=\"4\" style=\"background: #fff;\">&nbsp</td>" +
							"</tr>";
		}
		// ...Self Evaluation
		output = output +
						"<tr>" +
							"<td colspan=\"2\" class=\"reportheader\">" + summaryList[x].toStudentName.toUpperCase() + "'s Self Evaluation</td>" +
						"</tr>" +
						"<tr>" +
							"<td class=\"lhs\">Claimed contribution:</td>" +
							"<td>" + summaryList[x].claimedPoints + "</td>" +
						"</tr>" +
						"<tr>" +
							"<td class=\"lhs\">Perceived contribution:</td>" +
							"<td>" + summaryList[x].average + "</td>" +
						"</tr>";
		// ...Peer Evaluation
		var outputTemp = "<tr>" + 
						 "<td colspan = \"2\" class = \"reportheader\"> Peer Evaluations From Other Team Members</td>" +
						 "</tr>" +
						 
						 "<tr>" +
						 "<td colspan = \"2\"><table id=\"dataform\">" +
							"<tr>" +
								"<th class=\"centeralign\">From Student&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
								"<th class=\"leftalign\">Contribution&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcontribution\"></th>" +
								"<th class=\"leftalign\">Comments&nbsp;&nbsp;&nbsp;</th>" + 
								"<th class=\"leftalign\">Message&nbsp;&nbsp;&nbsp;</th>" +
							"</tr>";
		var points;
		var justification = "";
		var commentsToStudent = "";
		
		for (y = 0; y < submissionList.length; y++) {
			if (submissionList[y].toStudent == toStudent) {
				// Extract data
				if (submissionList[y].points == -999) {
					points = "N/A";
				} else if (submissionList[y].points == -101) {
					points = "Unsure";
				} else {
					points = Math.round(submissionList[y].points * submissionList[y].pointsBumpRatio);
				}
				
				if (submissionList[y].justification == "") {
					justification = "N/A";
				} else {
					justification = submissionList[y].justification;
				}
				
				if (commentsEnabled == true) {
					if (submissionList[y].commentsToStudent == "") {
						commentsToStudent = "N/A";
					} else {
						commentsToStudent = submissionList[y].commentsToStudent;
					}
				} else {
					commentsToStudent = "Disabled";
				}
				
				// Print data
				if (submissionList[y].fromStudent == submissionList[y].toStudent) {
					outputTemp = "<tr>" +
									"<td class=\"lhs\">Self evaluation:</td>" +
									"<td>" + sanitize(justification) + "</td>" +
									"</tr>" +
									"<td class=\"lhs\">Comments about team:</td>" +
									"<td>" + sanitize(commentsToStudent) + "</td>" +
									"</tr>" +
									outputTemp;
				} else {
					outputTemp = outputTemp +
											"<tr>" +
												"<td class=\"reportheader\">" + submissionList[y].fromStudentName.toUpperCase() + "</td>" +
												"<td>" + points + "</td>" +
												"<td>" + sanitize(justification) + "</td>" +
												"<td>" + sanitize(commentsToStudent) + "</td>" +
											"</tr>";
				}
			}
		}
		output = output + outputTemp + "</table></td></tr>";
	}
	
	output = output +
					"</table>" +
					"<br /><br />" +
					"<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Back\" />" +
					"<input type=\"button\" class =\"button\" name=\"button_top\" id=\"button_top\" value=\"To Top\" />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_top').onclick = function() { 
// printEvaluationReportByAction(submissionList, summaryList, status,
// commentsEnabled);
		document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	};
	document.getElementById("button_back").onclick = function() {
		displayEvaluationsTab();
	}
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function printEvaluationDetailByReviewerList(submissionList, summaryList, status, commentsEnabled) {
	
	clearStatusMessage();
	
	var output = "";
	
	for (x = 0; x < summaryList.length; x++) {
		var toStudent = summaryList[x].toStudent;

		// Team Field Set
		if (x == 0 || summaryList[x].teamName != summaryList[x-1].teamName) {
			output = output +
							"<table id=\"data\">" +
								"<thead>" +
									"<p class = \"splinfo2\">TEAM: " + summaryList[x].teamName + "</p>" +
								"</thead>";
		} else {
			output = output +
							"<tr>" +
							"<td colspan=\"4\" style=\"background: #fff;\">&nbsp</td>" +
							"</tr>";
		}
		
		// ...Self Evaluation
		output = output + "<tr>" +
							"<td colspan=\"2\" class=\"reportheader\">" + summaryList[x].toStudentName.toUpperCase() + "'s Self Evaluation" + "</td>" +
						  "</tr>" +
						  "<tr>" +
							"<td class=\"lhs\">Claimed contribution:</td>" +
							"<td>" + summaryList[x].claimedPoints + "</td>" +
						  "</tr>" +
						  "<tr>" +
							"<td class=\"lhs\">Perceived contribution:</td>" +
							"<td>" + summaryList[x].average + "</td>" +
						  "</tr>";

		// ...Peer Evaluations For Other Team Members
		var outputTemp = "<tr>" + 
						 "<td colspan = \"2\" class = \"reportheader\"> Peer Evaluations For Other Team Members</td>" +
						 "</tr>" +
						 
						 "<tr>" +
						 "<td colspan = \"2\"><table id=\"dataform\">" +
							"<tr>" +
								"<th class=\"centeralign\">For Student&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
								"<th class=\"leftalign\">Contribution&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcontribution\"></th>" +
								"<th class=\"leftalign\">Comments&nbsp;&nbsp;&nbsp;</th>" + 
								"<th class=\"leftalign\">Message&nbsp;&nbsp;&nbsp;</th>" +
							"</tr>";
		var points;
		var justification = "";
		var commentsToStudent = "";
		
		for (loop = 0; loop < submissionList.length; loop++) {	
			if (submissionList[loop].fromStudent == toStudent) {
				// Extract data
				if (submissionList[loop].points == -999) {
					points = "N/A";
				} else if (submissionList[loop].points == -101) {
					points = "Unsure";
				} else {
					points = Math.round(submissionList[loop].points * submissionList[loop].pointsBumpRatio);
				}
				
				if (submissionList[loop].justification == "") {
					justification = "N/A";
				} else {
					justification = submissionList[loop].justification;
				}
				
				if (commentsEnabled == true) {
					if (submissionList[loop].commentsToStudent == "") {
						commentsToStudent = "N/A";
					} else {
						commentsToStudent = submissionList[loop].commentsToStudent;
					}
				} else {
					commentsToStudent = "Disabled";
				}
				
				// Print data
				if (submissionList[loop].fromStudent == submissionList[loop].toStudent) {
					outputTemp = "<tr>" +
									"<td class=\"lhs\">Self evaluation:</td>" +
									"<td>" + sanitize(justification) + "</td>" +
									"</tr>" +
									"<td class=\"lhs\">Comments about team:</td>" +
									"<td>" + sanitize(commentsToStudent) + "</td>" +
									"</tr>" +
									outputTemp;
				} else {
					outputTemp = outputTemp +
											"<tr>" +
												"<td class=\"reportheader\">" + submissionList[loop].toStudentName.toUpperCase() + "</td>" +
												"<td>" + points + "</td>" +
												"<td>" + sanitize(justification) + "</td>" +
												"<td>" + sanitize(commentsToStudent) + "</td>" +
											"</tr>";
				}
			}
		}
		output = output + outputTemp + "</table></td></tr>";
	}
	
	output = output +
					"</table>" +
					"<br /><br />" +
					"<input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Back\" />" +
					"<input type=\"button\" class =\"button\" name=\"button_top\" id=\"button_top\" value=\"To Top\" />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_top').onclick = function() { 
// printEvaluationReportByAction(submissionList, summaryList, status,
// commentsEnabled);
		document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	};
	document.getElementById("button_back").onclick = function() {
		displayEvaluationsTab();
	}
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function printCourse(course) {
	var studentList = getStudentList(course.ID);
	
	var outputHeader = "<h1>COURSE DETAILS</h1>";
	var output = "" +
	"<table id=\"data\">" + 
	"<tr>" + 
	"<td class=\"fieldname\">Course ID:</td>" + 
	"<td>" + course.ID + "</td>" +
	"</tr>" + 
	"<tr>" + 
	"<td class=\"fieldname\">Course name:</td>" + 
	"<td>" + sanitize(course.name) + "</td>" +
	"</tr>" + 
	"<tr>" + 
	"<td class=\"fieldname\">Teams:</td>" + 
	"<td>" + course.numberOfTeams + "</td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"fieldname\">Total students:</td>" + 
	"<td>" + studentList.length + "</td>" +
	"</tr>";
	
	if ((studentList != 1) && (studentList.length > 0)) {
		output = output +
						"<tr>" +
						"<td class=\"centeralign\" colspan=\"2\">" +
						"<input type=\"button\" class=\"button t_remind_students\" onmouseover=\"ddrivetip('Send a reminder to all students yet to join the class');\" " +
						"onmouseout=\"hideddrivetip();\" " +
						"onClick=\"toggleSendRegistrationKeysConfirmation('" + course.ID + "');hideddrivetip();\" value=\"Remind to join\" tabindex=1 />" +
						" <input type=\"button\" class=\"button t_delete_students\" onmouseover=\"ddrivetip('Delete all students in this course');\"" +
						"onmouseout=\"hideddrivetip();\"" +
						"onclick=\"toggleDeleteAllStudentsConfirmation('" + course.ID + "')\" value=\"Delete all students\" />" +
						"</td>" +
						"</tr>";
	}
	
	output = output +
					"</table>";
	
	document.getElementById(DIV_COURSE_INFORMATION).innerHTML = output; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
}


function printCourseList(courseList)
{
	var output = "" +
	"<table id=\"dataform\">" + 
	"<tr>" + 
	"<th class=\"leftalign\">COURSE ID&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\" /></th>" + 
	"<th class=\"leftalign\">COURSE NAME&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcoursename\" /></th>" + 
	"<th class=\"centeralign\">TEAMS</th>" + 
	"<th class=\"centeralign\">ACTION(S)</th>" +
	"</tr>";
	
	// Fix for empty course list
	if (courseList.length == 0) {
		setStatusMessage("You have not created any courses yet. Use the form above to create a course.");
		output = output + "<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>" +
						"<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>";
	}
	
	// Need counter to take note of archived courses
	var counter = 0;
	
	for(loop = 0; loop < courseList.length; loop++) 
	{ 
		if(courseList[loop].status == "false" || courseViewArchivedStatus == courseViewArchived.show)
		{
			output = output + "<tr>";
			output = output + "<td class='t_course_code'>" + courseList[loop].ID + "</td>"; 
			output = output + "<td class='t_course_name'>" + sanitize(courseList[loop].name) + "</td>"; 
			output = output + "<td class=\"t_course_teams centeralign\">" +  courseList[loop].numberOfTeams + "</td>"; 
			output = output + "<td class=\"centeralign\">" + 
					  "<a class='t_course_enrol' href=\"javascript:displayEnrollmentPage('" + courseList[loop].ID + "');hideddrivetip();\"" +
					  "onmouseover=\"ddrivetip('Enrol students into the course')\"" +
					  "onmouseout=\"hideddrivetip()\">Enrol</a>" + " / " +
				      "<a class='t_course_view' href=\"javascript:displayCourseInformation('" + courseList[loop].ID + "');hideddrivetip();\"" +
				      "onmouseover=\"ddrivetip('View, edit and send registration keys to the students in the course')\"" +
					  "onmouseout=\"hideddrivetip()\">View</a>" + " / ";

			// Archive is going to be revamped
			/*
			 * if(courseList[loop].status == "true") { output = output + "<a
			 * href=\"javascript:doUnarchiveCourse('" + courseList[loop].ID +
			 * "');\">Unarchive</a>" + " / "; } else { output = output + "<a
			 * href=\"javascript:doArchiveCourse('" + courseList[loop].ID +
			 * "');\">Archive</a>" + " / "; }
			 */
				     
			output = output + "<a class='t_course_delete' href=\"javascript:toggleDeleteCourseConfirmation('" + courseList[loop].ID + "');hideddrivetip();\"" +
					"onmouseover=\"ddrivetip('Delete the course and its corresponding students and evaluations')\"" +
					"onmouseout=\"hideddrivetip()\">Delete</a>" +
					"</td></tr>";
			
			counter++;
		}
	} 
	
	output = output + "</table><br /><br />";
	
	// Archive is going to be revamped
	/*
	 * if(courseViewArchivedStatus == courseViewArchived.show) { output = output + "<input
	 * class=\"buttonViewArchived\" type=\"button\" value=\" HIDE \nARCHIVED\"" +
	 * "onmouseover=\"this.className='buttonViewArchivedSelected'\"
	 * onmouseout=\"this.className='buttonViewArchived'\"" + "onClick=\"\"
	 * id=\"button_viewarchived\" name=\"button_viewarchived\" tabindex=5 /><br /><br />"; }
	 * 
	 * else { output = output + "<input class=\"buttonViewArchived\"
	 * type=\"button\" value=\" SHOW \nARCHIVED\"" +
	 * "onmouseover=\"this.className='buttonViewArchivedSelected'\"
	 * onmouseout=\"this.className='buttonViewArchived'\"" +
	 * "id=\"button_viewarchived\" name=\"button_viewarchived\" tabindex=5 /><br /><br />"; }
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
	 * else { toggleSortCoursesByID(courseList); } };
	 */
	
}

function printEditEvaluation(courseID, name, instructions, commentsEnabled, start, deadline, timeZone, gracePeriod, status, activated)
{	
	var outputHeader = "<h1>EDIT EVALUATION</h1>";

	var startString = convertDateToDDMMYYYY(start);
	var deadlineString = convertDateToDDMMYYYY(deadline);
	
	isDisabled = (status == "CLOSED" || status == "OPEN")? true : false;
	
	var output = "<form name=\"form_editevaluation\">" + 
				 "<table id=\"data\">" +
				 "<tr>" +
				 "<td class=\"fieldname\">Course ID:</td>" +
				 "<td>" + courseID + "</td></tr>" +
				 "<tr>" +
				 "<td class=\"fieldname\">Evaluation Name:</td>" +
				 "<td>" + name + "</td></tr>";

	output = output +
				 "<tr>" +
				 "<td class=\"fieldname\">Opening time:</td>" +
				 "<td>" + 
				 "<input style=\"width: 100px;\" type=\"text\" name=\"" + EVALUATION_START + "\" id=\"" + EVALUATION_START + "\"" +
				 "onClick =\"cal.select(document.forms['form_editevaluation']." + EVALUATION_START + ",'" + EVALUATION_START + "','dd/MM/yyyy');\"" +
				 "value=\"" + startString + "\" READONLY tabindex=1> @ " +
				 "<select style=\"width: 70px;\" name=\"" + EVALUATION_STARTTIME + "\" id=\"" + EVALUATION_STARTTIME + "\" tabindex=2>" + 
				 getTimeOptionString() +
				 "</select>" +
				 "</td></tr>" +
				 "<tr>" +
				 "<td class=\"fieldname\">Closing time:</td>" +
				 "<td>" + 
				 "<input style=\"width: 100px;\" type=\"text\" name=\"" + EVALUATION_DEADLINE + "\" id=\"" + EVALUATION_DEADLINE + "\" + " +
				 "onClick =\"cal.select(document.forms['form_editevaluation']." + EVALUATION_DEADLINE + ",'" + EVALUATION_DEADLINE + "','dd/MM/yyyy');\"" +
				 "value=\"" + deadlineString + "\" READONLY tabindex=3> @ " +
				 "<select style=\"width: 70px;\" name=\"" + EVALUATION_DEADLINETIME + "\" id=\"" + EVALUATION_DEADLINETIME + "\" tabindex=4>" + 
				 getTimeOptionString() +
				 "</select>" +
				 "</td></tr>" +
				 "<tr>" +
				 "<td class=\"fieldname\">Grace period:</td>" +
				 "<td>" + 
				 "<select style=\"width: 70px;\" name=\"" + EVALUATION_GRACEPERIOD + "\" id=\"" + EVALUATION_GRACEPERIOD + "\" tabindex=5>" +
				 getGracePeriodOptionString() +
				 "</select></td></tr>" +
				 "<tr>";
		
			 if (activated == true) {
				 output = output + "<td class=\"fieldname\">Peer feedback:</td>" +
				 "<td>";
					 
				 if (commentsEnabled) {
					 output = output + "Enabled" +
					 "<input type=\"hidden\" name=\"" + EVALUATION_COMMENTSENABLED + "\" id=\"" +
					 EVALUATION_COMMENTSENABLED + "\" value=\"true\">";
				 } else {
					 output = output + "Disabled" +
					 "<input type=\"hidden\" name=\"" + EVALUATION_COMMENTSENABLED + "\" id=\"" +
					 EVALUATION_COMMENTSENABLED + "\" value=\"false\">";
				 }
			 } else {
				 output = output +
					 "<td class=\"fieldname\">Peer feedback:</td>" +
					 "<td><input type=\"radio\" name=\"" + EVALUATION_COMMENTSENABLED + "\" id=\"" +
					 EVALUATION_COMMENTSENABLED + "\" value=\"true\" tabindex=6 >Enabled&nbsp;&nbsp;<input type=\"radio\" name=\"" + EVALUATION_COMMENTSENABLED + "\" id=\"" + 
					 EVALUATION_COMMENTSENABLED + "\" value=\"false\" tabindex=7 >Disabled";
			 }				 
				 
			 output = output +
			 "</td>" +
			 "</tr>" +
			 "<tr>" +
			 "<td class=\"fieldname\">Instructions:</td>" +
			 "<td><textarea rows=\"2\" cols=\"80\" class=\"textvalue\" type=\"text\" name=\"" + EVALUATION_INSTRUCTIONS + 
			 "\" id=\"" + EVALUATION_INSTRUCTIONS + "\" tabindex=8>" + sanitize(instructions) + "</textarea>" +
			 "</td></tr></table></form>";
			 
	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_editevaluation\" id=\"button_editevaluation\" value=\"Save Changes\" tabindex=9 />" +
								" <input type=\"button\" class=\"t_back button\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />" +
						"<br /><br />";
	
	document.getElementById(DIV_EVALUATION_EDITBUTTONS).innerHTML = outputButtons;
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
	
	document.getElementById(EVALUATION_STARTTIME).disabled = isDisabled;
	document.getElementById(EVALUATION_START).disabled = isDisabled;
	
	// TODO:save changes button update database
	document.getElementById('button_editevaluation').onclick = function() { 
	 	var editStart = document.getElementById(EVALUATION_START).value;
	 	var editStartTime = document.getElementById(EVALUATION_STARTTIME).value;
	 	var editDeadline = document.getElementById(EVALUATION_DEADLINE).value;
	 	var editDeadlineTime = document.getElementById(EVALUATION_DEADLINETIME).value;
	 	var editGracePeriod = document.getElementById(EVALUATION_GRACEPERIOD).value;
	 	var editInstructions = document.getElementById(EVALUATION_INSTRUCTIONS).value;
	 	var editCommentsEnabled = getCheckedValue(document.forms['form_editevaluation'].elements[EVALUATION_COMMENTSENABLED]);
		
	 	if (editCommentsEnabled == "") {
	 		editCommentsEnabled = document.getElementById(EVALUATION_COMMENTSENABLED).value;
	 	}
	 	
	 	doEditEvaluation(courseID, name, editStart, editStartTime, editDeadline, editDeadlineTime, 
				timeZone, editGracePeriod, editInstructions, editCommentsEnabled, activated, status); 
	};
		
	if (start.getMinutes() > 0) {
		document.getElementById(EVALUATION_STARTTIME).value = 24;
	} else {
		document.getElementById(EVALUATION_STARTTIME).value = start.getHours();
	}
	
	if (deadline.getMinutes() > 0) {
		document.getElementById(EVALUATION_DEADLINETIME).value = 24;
	} else {
		document.getElementById(EVALUATION_DEADLINETIME).value = deadline.getHours();
	}
	
	document.getElementById(EVALUATION_GRACEPERIOD).value = gracePeriod;

	if (activated == false) {
		if (commentsEnabled == true) {
			setCheckedValue(document.forms['form_editevaluation'].elements[EVALUATION_COMMENTSENABLED], true);
		} else {
			setCheckedValue(document.forms['form_editevaluation'].elements[EVALUATION_COMMENTSENABLED], false);
		}
	}		 
}

function printEditEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status)
{
	var fromStudent = summaryList[position].toStudent;
	var output;
	
	output = "<form name=\"form_submitevaluation\" id=\"form_submitevaluation\">" + 
	"<p class=\"splinfo2\">TEAM: " + summaryList[position].teamName + "</p><br /><br />" +
	"<table id=\"data\">" + 
	"<tr style=\"display:none\"><td>" +
	"<input type=\"text\" value=\"" + fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + 0 +
	"\" id=\"" + STUDENT_FROMSTUDENT + 0 + "\">" +
	"<input type=\"text\" value=\"" + fromStudent + "\" name=\"" + STUDENT_TOSTUDENT + 0 +
	"\" id=\"" + STUDENT_TOSTUDENT + 0 + "\">" +
	"</td></tr>" +
	"<tr style=\"display:none\"><td>" +
	"<input type=\"text\" value=\"" + summaryList[position].teamName + "\" name=\"" + STUDENT_TEAMNAME + 0 +
	"\" id=\"" + STUDENT_TEAMNAME + 0 + "\">" +
	"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + 0 +
	"\" id=\"" + COURSE_ID + 0 + "\">" +
	"</td></tr>" +
	"<tr style=\"display:none\"><td>" +
	"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + 0 +
	"\" id=\"" + EVALUATION_NAME + 0 + "\">" +
	"</td></tr>" +
	"<tr>" + 
	"<td colspan=\"2\" class=\"reportheader\">PEER EVALUATION FOR " + summaryList[position].toStudentName.toUpperCase() + "</td>" +
	"</tr>" +
	"<tr>" +
	"<td class=\"lhs\">" +
	"Estimated contribution:" +
	"</td>" +
	"<td>" +
	"<select style=\"width: 150px;\" name=\"" + 
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
	"<option value=\"-999\" selected>N/A</option>" +
	"</select>" +
	"</td>" +
	"</tr>";
	
	var outputTemp = "";
	var points;
	var justification = "";
	var commentsToStudent = "";
	var counter = 0;
	
	for(loop = 0; loop < submissionList.length; loop++)
	{	
		if(submissionList[loop].fromStudent == fromStudent)
		{
			// Extract data
			if(submissionList[loop].points == -999)
			{
				points = "N/A";
			}
			
			else if(submissionList[loop].points == -101)
			{
				points = "Unsure";
			}
			
			else
			{
				points = Math.round(submissionList[loop].points * submissionList[loop].pointsBumpRatio);
			}
			
			if(submissionList[loop].justification == "")
			{
				justification = "";
			}
			
			else
			{
				justification = submissionList[loop].justification;
			}
			
			if(commentsEnabled == true)
			{
				if(submissionList[loop].commentsToStudent == "")
				{
					commentsToStudent = "";
				}
				
				else
				{
					commentsToStudent = submissionList[loop].commentsToStudent;
				}
			}
			
			else
			{
				commentsToStudent = "Disabled";
			}
			
			// Print data
			if(submissionList[loop].fromStudent == submissionList[loop].toStudent)
			{
				if(commentsToStudent != "Disabled")
				{
					outputTemp = "" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Comments about your contribution:" + 
					"</td>" +
					"<td>" +
					"<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_JUSTIFICATION + 0 +
					"\" id=\"" + STUDENT_JUSTIFICATION + 0 + "\">" + sanitize(justification) +
					"</textarea>" +
					"</td></tr>" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Comments about team dynamics:" + 
					"</td>" +
					"<td>" +
					"<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_COMMENTSTOSTUDENT + 0 +
					"\" id=\"" + STUDENT_COMMENTSTOSTUDENT + 0 + "\">" + sanitize(commentsToStudent) + 
					"</textarea>" +
					"</td></tr>" + outputTemp;
				}
				
				else
				{
					outputTemp = "" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Comments about your contribution:" + 
					"</td>" +
					"<td>" +
					"<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_JUSTIFICATION + 0 +
					"\" id=\"" + STUDENT_JUSTIFICATION + 0 + "\">" + sanitize(justification) +
					"</textarea>" +
					"</td></tr>" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Comments about team dynamics:" + 
					"</td>" +
					"<td> Disabled" +
					// "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\"
					// name=\"" + STUDENT_COMMENTSTOSTUDENT + 0 +
					// "\" id=\"" + STUDENT_COMMENTSTOSTUDENT + 0 + "\"
					// disabled=\"true\">" + sanitize(commentsToStudent) +
					// "</textarea>" +
					"</td></tr>" + outputTemp;
				}
				
			}
			
			else
			{
				if(commentsToStudent != "Disabled")
				{
					outputTemp = outputTemp +
					"<tr style=\"display:none\"><td>" +
					"<input type=\"text\" value=\"" + submissionList[loop].fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + counter +
					"\" id=\"" + STUDENT_FROMSTUDENT + counter + "\">" +
					"<input type=\"text\" value=\"" + submissionList[loop].toStudent + "\" name=\"" + STUDENT_TOSTUDENT + counter +
					"\" id=\"" + STUDENT_TOSTUDENT + counter + "\">" +
					"</td></tr>" +
					"<tr style=\"display:none\"><td>" +
					"<input type=\"text\" value=\"" + summaryList[position].teamName + "\" name=\"" + STUDENT_TEAMNAME + 0 +
					"\" id=\"" + STUDENT_TEAMNAME + 0 + "\">" +
					"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + 0 +
					"\" id=\"" + COURSE_ID + 0 + "\">" +
					"</td></tr>" +
					"<tr style=\"display:none\"><td>" +
					"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + 0 +
					"\" id=\"" + EVALUATION_NAME + 0 + "\">" +
					"</td></tr>" +
					"<tr>" +
					"<td colspan=\"2\" class=\"reportheader\">Evaluation To " +
					submissionList[loop].toStudentName.toUpperCase() +
					"</td>" +
					"</tr>" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Estimated contribution:" +
					"</td>" +
					"<td>" +
					"<select style=\"width: 150px;\" name=\"" +
					STUDENT_POINTS + counter + "\" id=\"" + STUDENT_POINTS + counter + "\" >" + 
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
					"<option value=\"-999\" selected>N/A</option>" +
					"</select>" +
					"</td></tr>" + 
					"<tr>" +
					"<td class=\"lhs\">" +
					"Comments about this teammate:<br />(not shown to the teammate)" +
					"</td>" + 
					"<td>" +
					"<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_JUSTIFICATION + counter +
					"\" id=\"" + STUDENT_JUSTIFICATION + counter + "\">" + sanitize(justification) +
					"</textarea>" +
					"</td></tr>" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Message to this teammate:<br />(shown anonymously to the teammate)" + 
					"</td>" +
					"<td>" +
					"<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_COMMENTSTOSTUDENT + counter +
					"\" id=\"" + STUDENT_COMMENTSTOSTUDENT + counter + "\">" + sanitize(commentsToStudent) + 
					"</textarea>" +
					"</td></tr>";
				}
				
				else
				{
					outputTemp = outputTemp +
					"<tr style=\"display:none\"><td>" +
					"<input type=\"text\" value=\"" + submissionList[loop].fromStudent + "\" name=\"" + STUDENT_FROMSTUDENT + counter +
					"\" id=\"" + STUDENT_FROMSTUDENT + counter + "\">" +
					"<input type=\"text\" value=\"" + submissionList[loop].toStudent + "\" name=\"" + STUDENT_TOSTUDENT + counter +
					"\" id=\"" + STUDENT_TOSTUDENT + counter + "\">" +
					"</td></tr>" +
					"<tr style=\"display:none\"><td>" +
					"<input type=\"text\" value=\"" + summaryList[position].teamName + "\" name=\"" + STUDENT_TEAMNAME + 0 +
					"\" id=\"" + STUDENT_TEAMNAME + 0 + "\">" +
					"<input type=\"text\" value=\"" + submissionList[0].courseID + "\" name=\"" + COURSE_ID + 0 +
					"\" id=\"" + COURSE_ID + 0 + "\">" +
					"</td></tr>" +
					"<tr style=\"display:none\"><td>" +
					"<input type=\"text\" value=\"" + submissionList[0].evaluationName + "\" name=\"" + EVALUATION_NAME + 0 +
					"\" id=\"" + EVALUATION_NAME + 0 + "\">" +
					"</td></tr>" +
					"<tr>" +
					"<td colspan=\"2\" class=\"reportheader\">Evaluation To " +
					submissionList[loop].toStudentName.toUpperCase() +
					"</td>" +
					"</tr>" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Estimated contribution:" +
					"</td>" +
					"<td>" +
					"<select style=\"width: 150px;\" name=\"" +
					STUDENT_POINTS + counter + "\" id=\"" + STUDENT_POINTS + counter + "\" >" + 
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
					"<option value=\"-999\" selected>N/A</option>" +
					"</select>" +
					"</td></tr>" + 
					"<tr>" +
					"<td class=\"lhs\">" +
					"Comments about this teammate:<br />(not shown to the teammate)" +
					"</td>" + 
					"<td>" +
					"<textarea class=\"textvalue\" rows=\"8\" cols=\"100\" name=\"" + STUDENT_JUSTIFICATION + counter +
					"\" id=\"" + STUDENT_JUSTIFICATION + counter + "\">" + sanitize(justification) +
					"</textarea>" +
					"</td></tr>" +
					"<tr>" +
					"<td class=\"lhs\">" +
					"Message to this teammate:<br >(shown anonymously to the teammate)" + 
					"</td>" +
					"<td>" +
					// "<textarea class=\"textvalue\" rows=\"8\" cols=\"100\"
					// name=\"" + STUDENT_COMMENTSTOSTUDENT + counter +
					// "\" id=\"" + STUDENT_COMMENTSTOSTUDENT + counter + "\"
					// disabled=\"true\">" + sanitize(commentsToStudent) +
					// "</textarea>" +
					"</td></tr>";
				}
			}
			
			counter++;
		}
	}
	
	output = output + outputTemp + "</table></form><br /><br />";
	
	var outputButtons = "<input type=\"button\" class =\"button\" name=\"button_editevaluationresultsbyreviewee\"" +
							"id=\"button_editevaluationresultsbyreviewee\" value=\"Submit\" />" +
						" <input type=\"button\" class =\"button\" name=\"button_back\" id=\"button_back\" value=\"Cancel\" />" +
						"<br /><br />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = outputButtons;
	
	
	populateEditEvaluationResultsPointsForm(document.forms[0], submissionList, commentsEnabled);
	
	document.getElementById('button_editevaluationresultsbyreviewee').onclick = function() {
// doEditEvaluationResultsByReviewer(document.forms[0], status,
// commentsEnabled);
		doEditEvaluationResultsByReviewer(document.forms[0], summaryList, position, commentsEnabled, status);
	};
	
	document.getElementById('button_back').onclick = function() { 
		document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
		printEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status);
	};
	
}

function printEditStudent(courseID, email, name, teamName, googleID, registrationKey, comments) {
	var outputHeader = "<h1>EDIT STUDENT</h1>";
	var output = "<form>" +
				 "<table id=\"data\">" +
				 "<tr>" +
				 "<td class=\"fieldname\">Student Name*:</td>" +
				 "<td><input class=\"fieldvalue\" type=\"text\" value=\"" + name + "\" name=\"editname\" id=\"editname\"/></td>" +
				 "</tr>" +
				 "<tr>" +
				 "<td class=\"fieldname\">Team Name*:</td>" +
				 "<td><input class=\"fieldvalue\" type=\"text\" value=\"" + sanitize(teamName) + "\" name=\"editteamname\" id=\"editteamname\"/></td>" +
				 "</tr>" +
				 "<tr>" +
				 "<td class=\"fieldname\">E-mail Address*:</td>" +

						 "<td><input class=\"fieldvalue\" type=\"text\" value=\"" + email + "\" name=\"editemail\" id=\"editemail\"/></td>" 
						+

				 "</tr>" + 
				 "<tr>" +
				 "<td class=\"fieldname\">Google ID:</td>" +

				 (googleID == "" ?
						 "<td><input class=\"fieldvalue\" type=\"text\" value=\"" + sanitize(googleID) + "\" name=\"editgoogleid\" id=\"editgoogleid\"/></td>"
						 : "<td><input class=\"fieldvalue\" type=\"text\" value=\"" + sanitize(googleID) + "\" name=\"editgoogleid\" id=\"editgoogleid\" disabled=\"true\" /></td>"
			)+
				 

				 "</tr>" +
			 	 "<tr>" +
			 	 "<td class=\"fieldname\">Comments:</td>" +
			 	 "<td><textarea class =\"textvalue\" name=\"editcomments\" id=\"editcomments\" rows=\"6\" cols=\"80\">" + sanitize(comments) + "</textarea></td>" +
			 	 "</tr>" +
			 	 "</table>";
	
	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_editstudent\" id=\"button_editstudent\" value=\"Save Changes\" />" +
			 	 	 	"<input type=\"button\" class=\"button\" onClick=\"displayCourseInformation('" + courseID + "')\" value=\"Back\" />" +
			 	 	 	"</form>" +
			 	 	 	"<br /><br />";
	
	document.getElementById(DIV_STUDENT_EDITBUTTONS).innerHTML = outputButtons;
	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = output; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 

	document.getElementById('button_editstudent').onclick = function() { 
	 	var editName = document.getElementById('editname').value;
	 	var editTeamName = document.getElementById('editteamname').value;
	 	var editEmail = document.getElementById('editemail').value;
	 	var editGoogleID = document.getElementById('editgoogleid').value;
	 	var editComments = document.getElementById('editcomments').value;

		doEditStudent(courseID, email, editName, editTeamName, editEmail, editGoogleID, editComments); 
	};
}

function printEnrollmentPage(courseID) {
	var outputHeader = "<h1>ENROL STUDENTS for " + courseID + "</h1>";
	
	var output = "<img src=\"/images/enrolInstructions.png\" border=\"0\" />" +
				 "<p class=\"info\" style=\"text-align: center;\">Enrol no more than 100 students at a time.</p>" +
				 "<br />" +
				 "<form>" +
				 "<table id=\"data\">" +
				 "<tr>" +
                 "<td class=\"fieldname\" style=\"width: 250px;\">Student details:</td>" +
                 "<td><textarea rows=\"6\" cols=\"135\" class =\"textvalue\" name=\"information\" id=\"information\"></textarea></td>" +
                 "</tr>" +
                 "</table>" +
                 "</form>";

	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_enrol\" id=\"button_enrol\" value=\"Enrol students\" />" +
			" <input type=\"button\" class=\"t_back button\" onclick=\"displayCoursesTab();\" value=\"Back\" />";
		
	document.getElementById(DIV_COURSE_ENROLLMENT).innerHTML = output; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_COURSE_ENROLLMENTBUTTONS).innerHTML = outputButtons;
	
	document.getElementById('button_enrol').onclick = function() { 
		doEnrolStudents(document.getElementById('information').value, courseID);
	};	
}

function printEnrollmentResultsPage(reports)
{	
	var arrayAdd = [];
	
	for(var x = 0; x < reports.length; x++)
	{
		if(reports[x].status == "ADDED")
		{
			arrayAdd.push(reports[x]);
		}
	}
	
	var arrayEdit = [];
	
	for(var x = 0; x < reports.length; x++)
	{
		if(reports[x].status == "EDITED")
		{
			arrayEdit.push(reports[x]);
		}
	}
	
	var outputHeader = "<h1>ENROLLMENT RESULTS</h1>";
	var output = "<table id=\"data\">" +
					"<tr>" +
					"<td>" +
					"<input class=\"plusButton\" type=\"button\" id=\"button_viewaddedstudents\" " +
							"name=\"button_viewaddedstudents\" onclick=\"toggleViewAddedStudents();\" />Number of Students " +
							"Added: <span id='t_studentsAdded'>" + arrayAdd.length  +
					"</span></td>" +
					"</tr>";
	
	output = output +
					"<tr style=\"display:none\" name=\"rowAddedStudents\" id=\"rowAddedStudents\">" +
					"<td>";
	
	for(var x = 0; x < arrayAdd.length; x++)
	{
		output = output + "- " + arrayAdd[x].studentName + " (" + arrayAdd[x].studentEmail + ")<br />";
	}
	
	output = output +
					"</td>" +
					"</tr>" +
					"<tr>" +
					"<br />" +
					"</tr>" +
					"<tr>" +
					"<td>" +
					"<input class=\"plusButton\" type=\"button\" id=\"button_vieweditedstudents\" " +
							"name=\"button_vieweditedstudents\" onclick=\"toggleViewEditedStudents();\" />Number of Students " +
							"Edited:</b> <span id='t_studentsEdited'>" + arrayEdit.length +
					"</span></td>" +
					"</tr>";
	
	output = output +
					"<tr style=\"display:none\" name=\"rowEditedStudents\" id=\"rowEditedStudents\">" +
					"<td>";
	
	for (var x = 0; x < arrayEdit.length; x++) {
		output = output + "- " + arrayEdit[x].studentName + " (" + arrayEdit[x].studentEmail + ") : ";
		
		if (arrayEdit[x].nameEdited == "true") {
			output = output + "NAME ";
		}
		
		if (arrayEdit[x].teamNameEdited == "true") {
			output = output + "TEAMNAME ";
		}
		
		if (arrayEdit[x].commentsEdited == "true") {
			output = output + "COMMENTS ";
		}
		
		output = output +
					"<br />";
	}
	
	output = output +
					"</td>" +
					"</tr>" +
					"</table>" +
					"<br /><br /><br />" +
					"<input type=\"button\" class=\"t_back button\" onclick=\"displayCoursesTab();\" value=\"Back\" />" +
					"<br /><br />";
	
	document.getElementById(DIV_COURSE_ENROLLMENTRESULTS).innerHTML = output; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
}

function printEvaluationResultsByReviewee(submissionList, summaryList, position, commentsEnabled, status){
// document.getElementById('button_viewbytype').onclick = function()
// {
// clearStatusMessage();
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
//		
// };
//	 
// document.getElementById('button_viewbytype').value = "Back to summary";
	var toStudent = summaryList[position].toStudent;
	var output;

	// ...Self Evaluation
	output = "<p class=\"splinfo2\">TEAM: " + summaryList[position].teamName + "</p>" +
			 "<br /><br />" +
			 "<table id=\"data\">" +
				"<tr>" +
					"<tr>" +
					"<td colspan=\"2\" class=\"reportheader\">" + summaryList[position].toStudentName.toUpperCase() + "'s Self Evaluation</td>" +
					"</tr>" +
					
					"<tr>" +
					"<td class=\"lhs\">Claimed contribution:</td>" +
					"<td>" + summaryList[position].claimedPoints + "</td>" +
					"</tr>" +
					
					"<tr>" +
					"<td class=\"lhs\">Perceived contribution:</td>" +
					"<td>" + summaryList[position].average + "</td>" +
					"</tr>";
	// ...Peer Evaluations
	var outputTemp = "<tr>" + 
					 "<td colspan = \"2\" class = \"reportheader\"> Peer Evaluations From Other Team Members</td>" +
					 "</tr>" +
					 
					 "<tr>" +
					 "<td colspan = \"2\"><table id=\"dataform\">" +
						"<tr>" +
							"<th class=\"centeralign\">From Student&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
							"<th class=\"leftalign\">Contribution&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcontribution\"></th>" +
							"<th class=\"leftalign\">Comments&nbsp;&nbsp;&nbsp;</th>" + 
							"<th class=\"leftalign\">Message&nbsp;&nbsp;&nbsp;</th>" +
						"</tr>";
	var points;
	var justification = "";
	var commentsToStudent = "";
	
	for(loop = 0; loop < submissionList.length; loop++)
	{	
		if(submissionList[loop].toStudent == toStudent)
		{
			// Extract data
			if(submissionList[loop].points == -999)
			{
				points = "N/A";
			}
			
			else if(submissionList[loop].points == -101)
			{
				points = "Unsure";
			}
			
			else
			{
				points = Math.round(submissionList[loop].points * submissionList[loop].pointsBumpRatio);
			}
			
			if(submissionList[loop].justification == "")
			{
				justification = "N/A";
			}
			
			else
			{
				justification = submissionList[loop].justification;
			}
			
			if(commentsEnabled == true)
			{
				if(submissionList[loop].commentsToStudent == "")
				{
					commentsToStudent = "N/A";
				}
				
				else
				{
					commentsToStudent = submissionList[loop].commentsToStudent;
				}
			}
			
			else
			{
				commentsToStudent = "Disabled";
			}
			
			// Print data
			if(submissionList[loop].fromStudent == submissionList[loop].toStudent)
			{
				outputTemp = "" +
					"<tr>" +
						"<td class=\"lhs\">" +
						"Self evaluation:" + 
						"</td>" +
						"<td>" +
						sanitize(justification) + 
						"</td>" +
					"</tr>" +
					"<tr>" + 
						"<td class=\"lhs\">" +
						"Comments about team:" + 
						"</td>" +
						"<td>" +
						sanitize(commentsToStudent) + 
						"</td>" +
					"</tr></tr>" + outputTemp;
			}
			
			else
			{
				outputTemp = outputTemp +
				"<tr>" +
					"<td class=\"reportheader\">" + submissionList[loop].fromStudentName + "</td>" +
					"<td>" + points + "</td>" +
					"<td>" + sanitize(justification) + "</td>" +
					"<td>" + sanitize(commentsToStudent) + "</td>" +
				"</tr>";
			}
		}
	}
	
	output = output + 
			 	outputTemp + "</table></td></tr></table></td></tr>" +
			 	"<br /><br />" + 
			 "</table><br /><br />" +
			 "<input type=\"button\" class =\"button\" value=\"Previous\" name=\"button_previous\" id=\"button_previous\">" +
			 "<input type=\"button\" class =\"button\" value=\"Next\" name=\"button_next\" id=\"button_next\">" +
			 "<input type=\"button\" class =\"button\" value=\"Back\" name=\"button_back\" id=\"button_back\"><br /><br />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_next').onclick = 
		function() { 
			position ++;
// var next = position % summaryList.length;
// printEvaluationResultsByReviewee(submissionList, summaryList, next,
// commentsEnabled, status);
		
			if(position >= summaryList.length){
				position = 0;
			}
			printEvaluationResultsByReviewee(submissionList, summaryList, position, commentsEnabled, status); 
			
		};
	
	document.getElementById('button_previous').onclick = 
		function() 
		{ 	
			if(position == 0)
			{
				position = summaryList.length - 1;
			}
				
			else
			{
				position--;
			}
			
			printEvaluationResultsByReviewee(submissionList, summaryList, position, commentsEnabled, status); 
		};
	
	document.getElementById('button_back').onclick =
		function(){
			printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled);
		}
					
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
	
}

function printEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status) {
// document.getElementById('button_viewbytype').onclick = function() {
// clearStatusMessage();
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// toggleEvaluationSummaryListViewByType(submissionList, summaryList, status,
// commentsEnabled);
// };
//	  
// document.getElementById('button_viewbytype').value = "Back to summary";
	
	var toStudent = summaryList[position].toStudent;
	var output;

	// ...Self Evaluation
	output = "<p class=\"splinfo2\">TEAM: " + summaryList[position].teamName + "</p>" +
				"<br /><br />" +
				"<table id=\"data\">" +
					"<tr>" +
						"<td colspan=\"2\" class=\"reportheader\">" + summaryList[position].toStudentName.toUpperCase() + "'s Self Evaluation" +
						"</td>" +
					"</tr>" +
					"<tr>" +
						"<td class=\"lhs\">Claimed contribution:</td>" +
						"<td>" + summaryList[position].claimedPoints + "</td>" +
					"</tr>" +
					"<tr>" +
						"<td class=\"lhs\">Perceived contribution:</td>" +
						"<td>" + summaryList[position].average + "</td>" +
					"</tr>";
	
	// ...Peer Evaluations For Other Team Members
	var outputTemp = "<tr>" + 
					 "<td colspan = \"2\" class = \"reportheader\"> Peer Evaluations For Other Team Members</td>" +
					 "</tr>" +
					 
					 "<tr>" +
					 "<td colspan = \"2\"><table id=\"dataform\">" +
						"<tr>" +
							"<th class=\"centeralign\">For Student&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
							"<th class=\"leftalign\">Contribution&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcontribution\"></th>" +
							"<th class=\"leftalign\">Comments&nbsp;&nbsp;&nbsp;</th>" + 
							"<th class=\"leftalign\">Message&nbsp;&nbsp;&nbsp;</th>" +
						"</tr>";
	var points;
	var justification = "";
	var commentsToStudent = "";
	
	for (loop = 0; loop < submissionList.length; loop++) {	
		if (submissionList[loop].fromStudent == toStudent) {
			// Extract data
			if (submissionList[loop].points == -999) {
				points = "N/A";
			} else if (submissionList[loop].points == -101) {
				points = "Unsure";
			} else {
				points = Math.round(submissionList[loop].points * submissionList[loop].pointsBumpRatio);
			}
			
			if (submissionList[loop].justification == "") {
				justification = "N/A";
			} else {
				justification = submissionList[loop].justification;
			}
			
			if (commentsEnabled == true) {
				if (submissionList[loop].commentsToStudent == "") {
					commentsToStudent = "N/A";
				} else {
					commentsToStudent = submissionList[loop].commentsToStudent;
				}
			} else {
				commentsToStudent = "Disabled";
			}
			
			// Print data
			if (submissionList[loop].fromStudent == submissionList[loop].toStudent) {
				outputTemp = "<tr>" +
								"<td class=\"lhs\">Self evaluation:</td>" +
								"<td>" + sanitize(justification) + "</td>" +
							 "</tr>" +
							 "<tr>" +
								"<td class=\"lhs\">Comments about team:</td>" +
								"<td>" + sanitize(commentsToStudent) + "</td>" +
							 "</tr>" +
								outputTemp;
			} else {
				outputTemp = outputTemp +
								"<tr>" +
									"<td class=\"reportheader\">" + submissionList[loop].toStudentName + "</td>" +
									"<td>" + points + "</td>" +
									"<td>" + sanitize(justification) + "</td>" +
									"<td>" + sanitize(commentsToStudent) + "</td>" +
								"</tr>";
			}
		}
	}

	output = output + outputTemp + "</table></td></tr></table><br /><br />" +
	"<input type = \"button\" class = \"button\" value = \"Previous\" name = \"button_previous\" id = \"button_previous\">" +
	"<input type = \"button\" class = \"button\" value = \"Next\" name = \"button_next\" id = \"button_next\">";
	
	if (status == "CLOSED") {
		output = output +
						"&nbsp;&nbsp;<input type=\"button\" class =\"button\" type=\"button\" value=\"Edit\" name=\"button_edit\" id=\"button_edit\">";
	} else {
		output = output + " ";
	}
	
	output = output +
					"<input type=\"button\" class=\"button\" value=\"Back\" name=\"button_back\" id=\"button_back\"><br /><br />";
	
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 

	document.getElementById('button_next').onclick = function() { 
		clearStatusMessage();
// printEvaluationResultsByReviewer(submissionList, summaryList,
// (position+1)%summaryList.length, commentsEnabled, status);
		position ++;
// var next = position % summaryList.length;
// printEvaluationResultsByReviewer(submissionList, summaryList, next,
// commentsEnabled, status);
		if(position >= summaryList.length){
			position = 0;
		}
		printEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status); 
		
	};
	
	
	document.getElementById('button_previous').onclick = function() {
		clearStatusMessage();
		if (position == 0) {
			
			position = summaryList.length - 1;
			
		} else {
			
			position--;
		}

		printEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status);
	};
	
	if (status == "CLOSED") {
		document.getElementById('button_edit').onclick = function() {
			printEditEvaluationResultsByReviewer(submissionList, summaryList, position, commentsEnabled, status) 
		};
	}
	
	document.getElementById('button_back').onclick = function(){
		printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled);
	}

	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}


function printEvaluationResultsHeader(courseID, evaluationName, start, deadline, status, activated, published)
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
		
	    // new radio button: review type + report type
	    "<tr>" +
			"<td align = \"right\"><b>Report Type:</b> " +
				
				"<input type = \"radio\" name = \"radio_viewall\" id = \"radio_summary\" value = \"summary\" checked = \"checked\" />" +
				"<label for = \"radio_summary\">Summary</label>&nbsp&nbsp&nbsp" +
				
				"<input type = \"radio\" name = \"radio_viewall\" id = \"radio_detail\" value = \"detail\" />" +
				"<label for = \"radio_detail\">Detail</label>" +
			"</td>" +
	    	"<td><b>Review Type:</b> " +
		    	
	    		"<input type = \"radio\" name = \"radio_viewbytype\" id = \"radio_reviewer\" value = \"by reviewer\" checked = \"checked\"/>" +
	    		"<label for = \"radio_reviewer\">By Reviewer</label>&nbsp&nbsp&nbsp" +
	    		"<input type = \"radio\" name = \"radio_viewbytype\" id = \"radio_reviewee\" value = \"by reviewee\"/>" +
	    		"<label for = \"radio_reviewee\">By Reviewee</label>" +
	    		
	    	"</td>" +
	    "</tr>" +
	    
	    // publish, unpublish button
		"<tr>" +
		    "<td></td>" +
		    "<td>";
				// publish
				if(status != "OPEN"){
					if (published == false && activated == true){
						output = output + 
						"<input type=\"button\" class=\"button\" id = \"button_publish\" value = \"Publish\" onclick = \"javascript:togglePublishEvaluation('" + courseID + "','" + evaluationName + "', true, false)\" />";
				    }
					else if (published == true){
						output = output +
						"<input type=\"button\" class=\"button\" id = \"button_publish\" value = \"Unpublish\" onclick = \"javascript:togglePublishEvaluation('" + courseID + "','" + evaluationName + "', false, false)\" />";
					    
// "<a class='t_eval_unpublish' href=\"javascript:togglePublishEvaluation('" +
// evaluationList[loop].courseID + "','" + evaluationList[loop].name +
// "', false);hideddrivetip();\"" +
// "onmouseover=\"ddrivetip('Close the evaluation results')\"" +
// "onmouseout=\"hideddrivetip()\">Unpublish</a> / ";
					 }
				}
				
			output = output + 
			"</td>" +
	    "</tr>" +

    "</table>";
	
	
	
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
	document.getElementById(DIV_EVALUATION_INFORMATION).innerHTML = output; 
}


// xl: new added
function printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled){
	// clean page:
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	clearStatusMessage();
	
	// case 1: [x]reviewee [x]summary..............case handler............
	if(document.getElementById('radio_reviewee').checked && document.getElementById('radio_summary').checked){
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
		printEvaluationSummaryByRevieweeList(submissionList, summaryList.sort(sortByTeamName), status, commentsEnabled);
	}
	
	// case 2: [x]reviewer [x]summary
	else if(document.getElementById('radio_reviewer').checked && document.getElementById('radio_summary').checked){
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
		printEvaluationSummaryByReviewerList(submissionList, summaryList.sort(sortByTeamName), status, commentsEnabled);
	}
	
	// case 3: [x]reviewee [x]detail
	else if(document.getElementById('radio_reviewee').checked && document.getElementById('radio_detail').checked){
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
		printEvaluationDetailByRevieweeList(submissionList, summaryList, status, commentsEnabled);
	}
	
	// case 4: [x]reviewer [x]detail
	else if(document.getElementById('radio_reviewer').checked && document.getElementById('radio_detail').checked){
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
		printEvaluationDetailByReviewerList(submissionList, summaryList, status, commentsEnabled);
	}
	
	// else:
	else{
		// do nothing
	}

}

function printEvaluationSummaryByRevieweeList(submissionList, summaryList, status, commentsEnabled) {
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	
	var output = "<table id=\"dataform\">" +
					"<tr>" +
					"<th class=\"leftalign\">TEAM&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortteamname\"></th>" +
					"<th class=\"leftalign\">STUDENT&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
					"<th class=\"leftalign\">CLAIMED CONTRIBUTION&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortaverage\"></th>" + 
					"<th class=\"centeralign\">[PERCEIVED - CLAIMED]&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortdiff\"></th>" +
					"<th class=\"centeralign\">ACTION(S)</th>" +
					"</tr>";
	
	for (loop = 0; loop < summaryList.length; loop++) {
		output = output +
						"<tr>" +
						"<td>" + sanitize(summaryList[loop].teamName) + "</td>" +
						"<td>";
		
		if (sanitize(summaryList[loop].toStudentComments) != "") {
			output = output +
							"<a onmouseover=\"ddrivetip('" + sanitize(summaryList[loop].toStudentComments) + "')\" onmouseout=\"hideddrivetip()\">" +
							sanitize(summaryList[loop].toStudentName) +
							"</a>" +
							"</td>";
		} else {
			output = output +
							sanitize(summaryList[loop].toStudentName) +
							"</td>";
		}
		
		output = output +
						"<td>" + summaryList[loop].claimedPoints + "</td>";
		
		if (summaryList[loop].difference > 0) {
			output = output +
							"<td class=\"centeralign\"><span class=\"posDiff\">" + summaryList[loop].difference + "</span></td>";
		} else if (summaryList[loop].difference < 0){
			output = output +
							"<td class=\"centeralign\"><span class=\"negDiff\">" + summaryList[loop].difference + "</span></td>";
		} else {
			output = output +
							"<td class=\"centeralign\">" + summaryList[loop].difference + "</td>";
		}
		
		output = output +
						"<td class=\"centeralign\">" +
						"<a name=\"viewEvaluationResults" + loop + "\" id=\"viewEvaluationResults" + loop + "\" href=# " +
						"onmouseover=\"ddrivetip('View feedback from the team for the student')\"" +
						"onmouseout=\"hideddrivetip()\">View</a>";

		output = output +
						"</td>" +
						"</tr>";
	}
	
	output = output +
					"</table>" +
					"<br /><br />" +
					"<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />" +
					"<br /><br />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_sortteamname').onclick = 
		function() { toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('button_sortname').onclick = 
		function() { toggleSortEvaluationSummaryListByToStudentName(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('button_sortaverage').onclick = 
		function() { toggleSortEvaluationSummaryListByAverage(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('button_sortdiff').onclick = 
		function() { toggleSortEvaluationSummaryListByDiff(submissionList, summaryList, status, commentsEnabled); };
		
	//new: radio button for report format		
	document.getElementById('radio_reviewee').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('radio_reviewer').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('radio_summary').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('radio_detail').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };

	for (loop = 0; loop < summaryList.length; loop++) {
		if (document.getElementById('viewEvaluationResults' + loop) != null) {
			document.getElementById('viewEvaluationResults' + loop).onclick = function()  { 
				hideddrivetip();
				printEvaluationResultsByReviewee(submissionList, summaryList, this.id.substring(21, this.id.length), commentsEnabled, status);
				clearStatusMessage();
			};
		}
	}
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function printEvaluationSummaryByReviewerList(submissionList, summaryList, status, commentsEnabled) {
	document.getElementById(DIV_EVALUATION_EDITRESULTSBUTTON).innerHTML = "";
	
	var output = "<table id=\"dataform\">" +
				"<tr>" +
				"<th class=\"leftalign\">TEAM&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortteamname\"></th>" +
				"<th class=\"leftalign\">STUDENT&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" +
				"<th class=\"centeralign\">SUBMITTED&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortsubmitted\"></th>" + 
				"<th class=\"centeralign\">ACTION(S)</th>" +
				"</tr>";

	var submitted;
	
	for (loop = 0; loop < summaryList.length; loop++) {
		if (summaryList[loop].submitted) {
			submitted = "YES";
		} else {
			submitted = "NO";
		}
		
		output = output + 
						"<tr>" +
						"<td>" + sanitize(summaryList[loop].teamName) + "</td>" +
						"<td>";
		
		if (sanitize(summaryList[loop].toStudentComments) != "") {
			output = output +
							"<a onmouseover=\"ddrivetip('" + sanitize(summaryList[loop].toStudentComments) + "')\"" +
							"onmouseout=\"hideddrivetip()\">" +
							sanitize(summaryList[loop].toStudentName) +
							"</a>" +
							"</td>";
		} else {
			output = output + sanitize(summaryList[loop].toStudentName) + "</td>";
		}
		
		output = output +
						"<td class=\"centeralign\" id=\"status_submitted" + loop + "\">" + submitted + "</td>" +
						"<td class=\"centeralign\">" +
						"<a name=\"viewEvaluationResults" + loop + "\" id=\"viewEvaluationResults" + loop + "\" href=# " +
						"onmouseover=\"ddrivetip('View feedback from the student for his team')\"" +
						"onmouseout=\"hideddrivetip()\">View</a>";
		
		if (status == "CLOSED") {
			output = output +
							" / <a name=\"editEvaluationResults" + loop + "\" id=\"editEvaluationResults" + loop + "\" href=# " +
							"onmouseover=\"ddrivetip('Edit feedback from the student for his team')\"" +
							"onmouseout=\"hideddrivetip()\">Edit</a>";
		}

		output = output +
						"</td>" +
						"</tr>";
	}
	
	output = output +
					"</table>" +
					"<br /><br />" +
					"<input type=\"button\" class=\"button\" id=\"button_back\" onclick=\"displayEvaluationsTab();\" value=\"Back\" />" +
					"<br /><br />";
	
	document.getElementById(DIV_EVALUATION_SUMMARYTABLE).innerHTML = output; 
	
	document.getElementById('button_sortteamname').onclick = 
		function() { toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('button_sortname').onclick = 
		function() { toggleSortEvaluationSummaryListByToStudentName(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('button_sortsubmitted').onclick = 
		function() { toggleSortEvaluationSummaryListBySubmitted(submissionList, summaryList, status, commentsEnabled); };
	// new: radio button for report format
	document.getElementById('radio_reviewee').onchange =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('radio_reviewer').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('radio_summary').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
	document.getElementById('radio_detail').onclick =
		function(){ printEvaluationReportByAction(submissionList, summaryList, status, commentsEnabled); };
		
	for (loop = 0; loop < summaryList.length; loop++) {
		if (document.getElementById('viewEvaluationResults' + loop) != null) {
			document.getElementById('viewEvaluationResults' + loop).onclick = function() { 
				hideddrivetip();
				printEvaluationResultsByReviewer(submissionList, summaryList, this.id.substring(21, this.id.length), commentsEnabled, status);
				
				document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
				clearStatusMessage();
			};
		}
	}
	
	for (loop = 0; loop < summaryList.length; loop++) {
		if (document.getElementById('editEvaluationResults' + loop) != null) {
			document.getElementById('editEvaluationResults' + loop).onclick = function() { 
				hideddrivetip();
				
				printEditEvaluationResultsByReviewer(submissionList, summaryList, this.id.substring(21, this.id.length), commentsEnabled, status);
				
				document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
				clearStatusMessage();
			};
		}
	}
	
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

function printEvaluationList(evaluationList) {
	var output;
	  
	output = "<table id=\"dataform\">" + 
				"<tr>" + 
				"<th class=\"leftalign\">COURSE ID&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortcourseid\"></th>" + 
				"<th class=\"leftalign\">EVALUATION&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortname\"></th>" + 
				"<th class=\"centeralign\">STATUS</th>" +
				"<th class=\"centeralign\"><span onmouseover=\"ddrivetip('Number of students submitted / Class size')\" onmouseout=\"hideddrivetip()\">RESPONSE RATE</span></th>" +
				"<th class=\"centeralign\">ACTION(S)</th>" + 
				"</tr>";
	
	// Fix for empty evaluation list
	if (evaluationList.length == 0) {
		setStatusMessage("You have not created any evaluations yet. Use the form above to create a new evaluation.");
		
		output = output + "<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>" +
						"<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>";
	}
	
	var counter = 0;
	
	for (loop = 0; loop < evaluationList.length; loop++) { 
		output = output +
						"<tr>" +
						"<td class='t_eval_coursecode'>" + sanitize(evaluationList[loop].courseID) + "</td>" +
						"<td class='t_eval_name'>" + sanitize(evaluationList[loop].name) + "</td>" +
						"<td class=\"t_eval_status centeralign\">" + evaluationList[loop].status + "</td>" +
						"<td class=\"t_eval_response centeralign\">" +
						evaluationList[loop].numberOfCompletedEvaluations + " / " + evaluationList[loop].numberOfEvaluations +
						"</td>"; 
		
		output = output +
						"<td class=\"centeralign\">";
		
		if (evaluationList[loop].status != "AWAITING") {
			output = output + 
							"<a class='t_eval_view' name=\"viewEvaluation" + loop + "\" id=\"viewEvaluation" + loop + "\" href=# " +
							"onmouseover=\"ddrivetip('View the current results of the evaluation')\"" +
							"onmouseout=\"hideddrivetip()\">View Results</a> / ";
		}
		
		if (evaluationList[loop].published == false) {
			output = output +
							"<a class='t_eval_edit' name=\"editEvaluation" + loop + "\" id=\"editEvaluation" + loop + "\" href=# " +
							"onmouseover=\"ddrivetip('Edit evaluation details')\"" +
							"onmouseout=\"hideddrivetip()\">Edit</a> / ";
		}
		
		if (evaluationList[loop].status == "OPEN") {
			output = output +
							"<a class='t_eval_remind' href=\"javascript:toggleRemindStudents('" +
							evaluationList[loop].courseID + "','" + evaluationList[loop].name +
							"');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('Send e-mails to remind students who have not submitted their evaluations to do so')\"" +
							"onmouseout=\"hideddrivetip()\">Remind</a> / ";
		} 
		
		else {
			
			// closed, unpublished
			if (evaluationList[loop].published == false && evaluationList[loop].activated == true) {
				output = output +
								"<a class='t_eval_publish' href=\"javascript:togglePublishEvaluation('" +
								evaluationList[loop].courseID + "','" + evaluationList[loop].name +
								"', true, true);hideddrivetip();\"" +
								"onmouseover=\"ddrivetip('Publish evaluation results for students to view')\"" +
								"onmouseout=\"hideddrivetip()\">Publish</a> / ";
			}
			// closed, published
			if (evaluationList[loop].published == true) {
				output = output +
			
								"<a class='t_eval_unpublish' href=\"javascript:togglePublishEvaluation('" +
								evaluationList[loop].courseID + "','" + evaluationList[loop].name +
								"', false, true);hideddrivetip();\"" +
								"onmouseover=\"ddrivetip('Close the evaluation results')\"" +
								"onmouseout=\"hideddrivetip()\">Unpublish</a> / ";
			 }
		}
		
		output = output +
						"<a class='t_eval_delete' href=\"javascript:toggleDeleteEvaluationConfirmation('" +
						evaluationList[loop].courseID + "','" + evaluationList[loop].name +
						"');hideddrivetip();\"" +
						"onmouseover=\"ddrivetip('Delete the evaluation')\"" +
						"onmouseout=\"hideddrivetip()\">Delete</a></td></tr>";
			
		counter++;		
	}
	
	output = output +
					"</table>" +
					"<br /><br />";
	
	document.getElementById(DIV_EVALUATION_TABLE).innerHTML = output; 
	
	document.getElementById('button_sortcourseid').onclick = function() { toggleSortEvaluationsByCourseID(evaluationList) };
	document.getElementById('button_sortname').onclick = function() { toggleSortEvaluationsByName(evaluationList) };
	
	for (loop = 0; loop < evaluationList.length; loop++) {
		if (document.getElementById('editEvaluation' + loop) != null) {
			document.getElementById('editEvaluation' + loop).onclick = function() { 
				hideddrivetip();
				displayEditEvaluation(evaluationList, this.id.substring(14, this.id.length));
			};
		}
	}
	
	for (loop = 0; loop < evaluationList.length; loop++) {
		if (document.getElementById('viewEvaluation' + loop) != null) {
			document.getElementById('viewEvaluation' + loop).onclick = function() { 
				hideddrivetip();
				displayEvaluationResults(evaluationList, this.id.substring(14, this.id.length));
			};
		}
	}
}

function printStudent(courseID, email, name, teamName, googleID, registrationKey, comments) {
	var outputHeader = "<h1>STUDENT DETAILS</h1>";
	var output = "<table id=\"data\">" +
				 "<tr>" +
				 "<td class=\"fieldname\">Student Name:</td>" +
				 "<td>" + name + "</td>" +
				 "</tr>" +
				 "<tr>" +
				 "<td class=\"fieldname\">Team Name:</td>" +
				 "<td>" + sanitize(teamName) + "</td>" +
				 "</tr>" +
				 "<tr><" +
				 "td class=\"fieldname\">E-mail Address:</td>" +
				 "<td>" + email + "</td>" +
				 "</tr>" + 
				 "<tr>" +
				 "<td class=\"fieldname\">Google ID:</td>" +
				 "<td>";
	
	if (googleID == "") {
		output = output + "-";
	} else {
		output = output + sanitize(googleID);
	}
		
	output = output +
					"</td>" +
					"</tr>" +
					"<tr>" +
					"<td class=\"fieldname\">Registration Key:</td>" +
					"<td id='t_courseKey'>" + registrationKey + "</td>" +
					"</tr>" +
					"<tr>" +
					"<td class=\"fieldname\">Comments:</td>" +
					"<td>";
	
	if (comments == "") {
		output = output + "-";
	} else {
		output = output + sanitize(comments);
	}
	
	output = output +
					"</div>" +
					"</td>" +
					"</tr>" +
					"</table>" +
					"<br /><br /><br />" +
					"<input type =\"button\" class=\"t_back button\" onClick=\"displayCourseInformation('" + courseID + "');\" value=\"Back\"/>" +
					"<br /><br />";
	
	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = output; 
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader; 
}

function printStudentList(studentList, courseID) {
	clearStatusMessage();
	
	var output;
	var unregisteredCount = 0;
	
	output = "<table id=\"dataform\">" +
				"<tr>" +
				"<th class=\"leftalign\">STUDENT NAME&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortstudentname\"></th>" +
				"<th class=\"leftalign\">TEAM&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortstudentteam\"></th>" +
				"<th class=\"centeralign\">STATUS&nbsp;&nbsp;&nbsp;<input class=\"buttonSortNone\" type=\"button\" id=\"button_sortstudentstatus\"></th>" +
				"<th class=\"centeralign\">ACTION(S)</th>" +
				"</tr>";
	
	// Fix for empty student list
	if (studentList.length == 0) {
		setStatusMessage("No students enrolled in this course yet. Click <a class='t_course_enrol' href=\"javascript:displayEnrollmentPage('" + courseID + "');\">here</a> to enrol students.");
		
		output = output + "<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>" +
						"<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>";
	}
	
	for (loop = 0; loop < studentList.length; loop++) {	
		output = output +
						"<tr>" +
						"<td>" + studentList[loop].name + "</td>" +
						"<td>" + sanitize(studentList[loop].teamName) + "</td>" +
						"<td class=\"centeralign\">";
		
		if (studentList[loop].googleID == "") {
			output =  output +
							"YET TO JOIN" +
							"</td>" +
							"<td class=\"centeralign\">" +
							"<a class='t_student_view' href=\"javascript:displayStudentInformation('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"', '" + escape( studentList[loop].name ) + "','" + escape(studentList[loop].teamName) + "','" +
							studentList[loop].googleID + "','" + studentList[loop].registrationKey + "','" + 
							escape(studentList[loop].comments) + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('View the details of the student')\"" +
							"onmouseout=\"hideddrivetip()\">View</a>" + " / " +
							"<a class='t_student_edit' href=\"javascript:displayEditStudent('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"', '" + escape(studentList[loop].name) + "','" + escape(studentList[loop].teamName) + "','" +
							studentList[loop].googleID + "','" + studentList[loop].registrationKey + "','" + 
							escape(studentList[loop].comments) + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('Edit the details of the student')\"" +
							"onmouseout=\"hideddrivetip()\">Edit</a>" + " / " +
							"<a class='t_student_resend' href=\"javascript:doSendRegistrationKey('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"','" + studentList[loop].name + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('E-mail the registration key to the student')\"" +
							"onmouseout=\"hideddrivetip()\">Resend Invite</a>" + " / " +
							"<a class='t_student_delete' href=\"javascript:toggleDeleteStudentConfirmation('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"', '" + studentList[loop].name + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('Delete the student and the corresponding evaluations from the course')\"" +
							"onmouseout=\"hideddrivetip()\">Delete</a>" +
							"</td>" +
							"</tr>";
			
			unregisteredCount++;
		} else {
			output = output +
							"JOINED" +
							"</td>" +
							"<td class=\"centeralign\">" +
							"<a href=\"javascript:displayStudentInformation('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"', '" + escape(studentList[loop].name) + "','" + escape(studentList[loop].teamName) + "','" +
							studentList[loop].googleID + "','" + studentList[loop].registrationKey + "','" + 
							escape(studentList[loop].comments) + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('View the details of the student')\"" +
							"onmouseout=\"hideddrivetip()\">View</a>" + " / " +
							"<a href=\"javascript:displayEditStudent('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"', '" + escape(studentList[loop].name) + "','" + escape(studentList[loop].teamName) + "','" +
							studentList[loop].googleID + "','" + studentList[loop].registrationKey + "','" + 
							escape(studentList[loop].comments) + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('Edit the details of the student')\"" +
							"onmouseout=\"hideddrivetip()\">Edit</a>" + " / " +
							"<a href=\"javascript:toggleDeleteStudentConfirmation('" + 
							studentList[loop].courseID  + "', '" + studentList[loop].email + 
							"', '" + escape( studentList[loop].name ) + "');hideddrivetip();\"" +
							"onmouseover=\"ddrivetip('Delete the student and the corresponding evaluations from the course')\"" +
							"onmouseout=\"hideddrivetip()\">Delete</a>" +
							"</td></tr>";
		}
	}	
	
	output = output +
					"</table>" +
					"<br />";
	
	output = output +
					"<br /><br />" +
					"<input type=\"button\" class=\"button\" onclick=\"displayCoursesTab();\" value=\"Back\" />" +
					"<br /><br />";

	document.getElementById(DIV_STUDENT_TABLE).innerHTML = output; 
	document.getElementById('button_sortstudentname').onclick = function() { toggleSortStudentsByName(studentList) };
	document.getElementById('button_sortstudentteam').onclick = function() { toggleSortStudentsByTeamName(studentList) };
	document.getElementById('button_sortstudentstatus').onclick = function() { toggleSortStudentsByStatus(studentList) };
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function publishEvaluation(courseID, name)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_PUBLISHEVALUATION + "&" + COURSE_ID + "=" + 
				encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(name));
		
		return handlePublishEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function unpublishEvaluation(courseID, name)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_UNPUBLISHEVALUATION + "&" + COURSE_ID + "=" + 
				encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(name));
		
		return handleUnpublishEvaluation();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function remindStudents(courseID, evaluationName)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_REMINDSTUDENTS + "&" +
				COURSE_ID + "=" + encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + 
				encodeURIComponent(evaluationName)); 
		
		handleRemindStudents();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function sendRegistrationKey(courseID, email)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEY + "&" + COURSE_ID + "=" + 
				encodeURIComponent(courseID) + "&" + STUDENT_EMAIL + "=" + encodeURIComponent(email));
		
		return handleSendRegistrationKey();
	}
}

/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function sendRegistrationKeys(courseID)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_SENDREGISTRATIONKEYS + "&" + COURSE_ID + "=" + 
				encodeURIComponent(courseID));
		
		return handleSendRegistrationKeys();
	}
}

// set the radio button with the given value as being checked
// do nothing if there are no radio buttons
// if the given value does not exist, all the radio buttons
// are reset to unchecked
function setCheckedValue(radioObj, newValue) {
	if(!radioObj)
		return;
	var radioLength = radioObj.length;
	if(radioLength == undefined) {
		radioObj.checked = (radioObj.value == newValue.toString());
		return;
	}
	for(var i = 0; i < radioLength; i++) {
		radioObj[i].checked = false;
		if(radioObj[i].value == newValue.toString()) {
			radioObj[i].checked = true;
		}
	}
}

function setSelectedIndex(s, v) 
{    
	for ( var i = 0; i < s.options.length; i++ ) 
	{        
		if ( s.options[i].value == v ) 
		{            
			s.options[i].selected = true;            
			return;        
		}    
	}
}

function sortByAverage(a, b) 
{
    var x = a.average;
    var y = b.average;
    
    if(x == "N/A")
    {
    	x = 1000;
    }
    
    if(y == "N/A")
    {
    	y = 1000;
    }
    
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByCourseID(a, b) 
{
    var x = a.courseID.toLowerCase();
    var y = b.courseID.toLowerCase();

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByDiff(a, b) 
{
    var x = a.difference;
    var y = b.difference;
    
    if(x == "N/A")
    {
    	x = 1000;
    }
    
    if(y == "N/A")
    {
    	y = 1000;
    }
    
    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByFromStudentName(a, b) 
{
    var x = a.fromStudentName;
    var y = b.fromStudentName;

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

function sortByGoogleID(a, b) 
{
    var x = a.googleID;
    var y = b.googleID;

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortBySubmitted(a, b) 
{
    var x = a.submitted;
    var y = b.submitted;

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByTeamName(a, b) 
{
    var x = a.teamName;
    var y = b.teamName;

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function sortByToStudentName(a, b) 
{
    var x = a.toStudentName;
    var y = b.toStudentName;

    return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

function toggleDeleteCourseConfirmation(courseID) {
	var s = confirm("Are you sure you want to delete the course, \"" + courseID + "\"?");
	if (s == true) {
		doDeleteCourse(courseID);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_COURSE_MANAGEMENT).scrollIntoView(true);
}

function toggleDeleteEvaluationConfirmation(courseID, name) {
	var s = confirm("Are you sure you want to delete the evaluation?");
	if (s == true) {
		doDeleteEvaluation(courseID, name);
	} else {
		clearStatusMessage();
	}
	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
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
	var s = confirm("Are you sure you want to remove " + studentName + " from the course?");
	if (s == true) {
		doDeleteStudent(courseID, studentEmail);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_COURSE_INFORMATION).scrollIntoView(true);
}

function toggleEditEvaluationResultsStatusMessage(statusMsg) {
	document.getElementById(DIV_STATUS_EDITEVALUATIONRESULTS).innerHTML = statusMsg; 
}

function toggleEvaluationSummaryListViewByType(submissionList, summaryList, status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		evaluationResultsViewStatus = evaluationResultsView.reviewer;
// document.getElementById('button_viewbytype').value = "View by reviewee";
	}
	
	else
	{
		evaluationResultsViewStatus = evaluationResultsView.reviewee;
// document.getElementById('button_viewbytype').value = "View by reviewer";
	}
	
	toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList, status, commentsEnabled)
}


function toggleInformStudentsOfEvaluationChanges(courseID, name)
{
	var s = confirm("Do you want to send e-mails to the students to inform them of changes to the evaluation?");
	if (s == true) {
		doInformStudentsOfEvaluationChanges(courseID, name);
	} else {
		clearStatusMessage();
	}

	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function togglePublishEvaluation(courseID, name, publish, reload) {
// var s = confirm("Are you sure you want to publish the evaluation?");
// if (s == true) {
// doPublishEvaluation(courseID, name);
// } else {
// clearStatusMessage();
// }

	
	if (publish) {
	  var s = confirm("Are you sure you want to publish the evaluation?");
	  if (s == true) {
		doPublishEvaluation(courseID, name, reload);
	  } else {
		clearStatusMessage();
	  }
	}
	else {
	  var s = confirm("Are you sure you want to unpublish the evaluation?");
	  if(s == true) {
		doUnpublishEvaluation(courseID, name, reload);
	  } else {
	    clearStatusMessage();
	  }
	}
	
	document.getElementById(DIV_EVALUATION_MANAGEMENT).scrollIntoView(true);
}

function toggleRemindStudents(courseID, evaluationName) {
	var s = confirm("Send e-mails to remind students who have not submitted their evaluations?");
	if (s == true) {
		doRemindStudents(courseID, evaluationName);
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

function toggleSortCoursesByID(courseList) {
	printCourseList(courseList.sort(sortByID));
	courseSortStatus = courseSort.ID;
	document.getElementById("button_sortcourseid").setAttribute("class", "buttonSortAscending");
}

function toggleSortCoursesByName(courseList) {
	printCourseList(courseList.sort(sortByName));
	courseSortStatus = courseSort.name;
	document.getElementById("button_sortcoursename").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByAverage(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryByRevieweeList(submissionList, summaryList.sort(sortByAverage), status, commentsEnabled);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.average; 
	document.getElementById("button_sortaverage").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByDiff(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryByRevieweeList(submissionList, summaryList.sort(sortByDiff), status, commentsEnabled);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.diff; 
	document.getElementById("button_sortdiff").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByFromStudentName(submissionList, summaryList, status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		printEvaluationSummaryByRevieweeList(submissionList, summaryList.sort(sortByFromStudentName), status, commentsEnabled);
	} else {
		printEvaluationSummaryByReviewerList(submissionList, summaryList.sort(sortByFromStudentName), status, commentsEnabled);
	}
	
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name; 
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListBySubmitted(submissionList, summaryList, status, commentsEnabled) {
	printEvaluationSummaryByReviewerList(submissionList, summaryList.sort(sortBySubmitted), status, commentsEnabled);
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.submitted; 
	document.getElementById("button_sortsubmitted").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByTeamName(submissionList, summaryList, status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		printEvaluationSummaryByRevieweeList(submissionList, summaryList.sort(sortByTeamName), status, commentsEnabled);
	} else {
		printEvaluationSummaryByReviewerList(submissionList, summaryList.sort(sortByTeamName), status, commentsEnabled);
	}
	
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.teamName; 
	document.getElementById("button_sortteamname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationSummaryListByToStudentName(submissionList, summaryList, status, commentsEnabled) {
	if (evaluationResultsViewStatus == evaluationResultsView.reviewee) {
		printEvaluationSummaryByRevieweeList(submissionList, summaryList.sort(sortByToStudentName), status, commentsEnabled);
	} else {
		printEvaluationSummaryByReviewerList(submissionList, summaryList.sort(sortByToStudentName), status, commentsEnabled);
	}
	
	evaluationResultsSummaryListSortStatus = evaluationResultsSummaryListSort.name; 
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationsByCourseID(evaluationList) {
	printEvaluationList(evaluationList.sort(sortByCourseID));
	evaluationSortStatus = evaluationSort.courseID;
	document.getElementById("button_sortcourseid").setAttribute("class", "buttonSortAscending");
}

function toggleSortEvaluationsByName(evaluationList) {
	printEvaluationList(evaluationList.sort(sortByName));
	evaluationSortStatus = evaluationSort.name;
	document.getElementById("button_sortname").setAttribute("class", "buttonSortAscending");
}

function toggleSortStudentsByName(studentList, courseID) {
	printStudentList(studentList.sort(sortByName), courseID);
	studentSortStatus = studentSort.name;
	document.getElementById("button_sortstudentname").setAttribute("class", "buttonSortAscending");
}

function toggleSortStudentsByStatus(studentList, courseID) {
	printStudentList(studentList.sort(sortByGoogleID), courseID);
	studentSortStatus = studentSort.status;
	document.getElementById("button_sortstudentstatus").setAttribute("class", "buttonSortAscending");
}

function toggleSortStudentsByTeamName(studentList, courseID) {
	printStudentList(studentList.sort(sortByTeamName), courseID);
	studentSortStatus = studentSort.teamName;
	document.getElementById("button_sortstudentteam").setAttribute("class", "buttonSortAscending");
}


function toggleViewAddedStudents() {
	var currentClass = document.getElementById('button_viewaddedstudents').getAttribute("class");
	
	if (currentClass == "plusButton") {
		document.getElementById("button_viewaddedstudents").setAttribute("class", "minusButton");
		document.getElementById("rowAddedStudents").style.display = "";
	} else {
		document.getElementById("button_viewaddedstudents").setAttribute("class", "plusButton");
		document.getElementById("rowAddedStudents").style.display = "none";
	}
}

function toggleViewEditedStudents() {
	var currentClass = document.getElementById('button_vieweditedstudents').getAttribute("class");
	
	if (currentClass == "plusButton") {
		document.getElementById("button_vieweditedstudents").setAttribute("class", "minusButton");
		document.getElementById("rowEditedStudents").style.display = "";
	} else {
		document.getElementById("button_vieweditedstudents").setAttribute("class", "plusButton");
		document.getElementById("rowEditedStudents").style.display = "none";
	}
}


/*
 * Returns
 * 
 * 0: successful 1: server error
 * 
 */
function unarchiveCourse(courseID)
{	
	if(xmlhttp)
	{		
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_UNARCHIVECOURSE + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	
		return handleUnarchiveCourse();
	}
}

window.onload=function()
{
	displayCoursesTab();
	initializetooltip();
}

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip