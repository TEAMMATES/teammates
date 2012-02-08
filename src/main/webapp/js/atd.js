// AJAX
var xmlhttp = new getXMLObject();

//OPERATIONS
var OPERATION_ADMINISTRATOR_LOGOUT = "administrator_logout";

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
var OPERATION_COORDINATOR_REMINDSTUDENTS = "coordinator_remindstudents";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEY = "coordinator_sendregistrationkey";
var OPERATION_COORDINATOR_SENDREGISTRATIONKEYS = "coordinator_sendregistrationkeys";
var OPERATION_COORDINATOR_UNARCHIVECOURSE = "coordinator_unarchivecourse";
var OPERATION_COORDINATOR_UNARCHIVEEVALUATION = "coordinator_unarchiveevaluation";

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

//PARAMETERS
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

// MESSAGES
var MSG_COURSE_EXISTS = "course exists";
var MSG_COURSE_NOTEAMS = "course has no teams";

var MSG_EVALUATION_ADDED = "evaluation added";
var MSG_EVALUATION_DEADLINEPASSED = "evaluation deadline passed";
var MSG_EVALUATION_EDITED = "evaluation edited";
var MSG_EVALUATION_EXISTS = "evaluation exists";
var MSG_EVALUATION_UNABLETOCHANGETEAMS = "evaluation ongoing unable to change teams";

var MSG_STUDENT_COURSEJOINED = "course joined";
var MSG_STUDENT_GOOGLEIDEXISTSINCOURSE = "googleid exists in course";
var MSG_STUDENT_REGISTRATIONKEYINVALID = "registration key invalid";
var MSG_STUDENT_REGISTRATIONKEYTAKEN = "registration key taken";

// GLOBAL VARIABLE
var courseID = "ATD-TESTING.1.2.3";
var courseName = "ATD TESTING IN PROGRESS";
var evaluationName = "ATD TEST";

/*
 * Returns
 * 
 * 0: successful
 * 1: server error
 * 2: field(s) empty
 * 3: courseID invalid
 * 4: name invalid
 * 5: course exists
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
 * 0: successful
 * 1: server error
 * 2: fields empty
 * 3: evaluation name invalid
 * 4: evaluation schedule invalid
 * 5: evaluation exists
 * 6: course has no teams
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

function cleanUp()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_CLEANUP + "&" + COURSE_ID + "=" + encodeURIComponent(courseID));
	}
	
}
function clearATD()
{
	deleteCourse(courseID);
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

function createEnrollmentInput(studentList)
{
	var input = "";
	
	for(var x = 0; x < studentList.length; x++)
	{
		if(x % 2 == 0)
		{
			if(comments == "")
			{
				input = input + studentList[x].name + "\t" + studentList[x].email + "\t" 
						+ studentList[x].teamName + "\n";
			}
		
			else
			{
				input = input + studentList[x].name + "\t" + studentList[x].email + "\t" 
						+ studentList[x].teamName + "\t" + studentList[x].comments + "\n";
			}
		}
		
		else
		{
			if(comments == "")
			{
				input = input + studentList[x].name + "|" + studentList[x].email + "|" 
						+ studentList[x].teamName + "\n";
			}
		
			else
			{
				input = input + studentList[x].name + "|" + studentList[x].email + "|" 
						+ studentList[x].teamName + "|" + studentList[x].comments + "\n";
			}
		}
	}
	
	return input;
}

function createInvalidCourseList()
{
	var courseList = new Array();
	
	var ID;
	var name;
	
	ID = "Invalid>";
	name = "Valid name";
	
	courseList[0] = {ID:ID, name:name};
	
	ID = "InvalidIDthisistoolongtobeacourseidomgveryveryveryverylong";
	name = "Valid";
	
	courseList[1] = {ID:ID, name:name};
	
	ID = "ValidID";
	name = "Invalid name this might be a tad too long to be a valid course name";
	
	courseList[2] = {ID:ID, name:name};
	
	ID = "Invalid%";
	name = "Valid Name";
	
	courseList[3] = {ID:ID, name:name};
	
	ID = "Invalid\"\"";
	name = "Valid Name";
	
	courseList[4] = {ID:ID, name:name};
	
	ID = "Invalid'";
	name = "Valid Name :;";
	
	courseList[5] = {ID:ID, name:name};
	
	ID = "Invalid@";
	name = "Valid Name :;";
	
	courseList[6] = {ID:ID, name:name};
	
	ID = "";
	name = "Valid Name :;";
	
	courseList[7] = {ID:ID, name:name};
	
	ID = "Invalid space";
	name = "Valid Name :;";
	
	courseList[8] = {ID:ID, name:name};
	
	ID = "<b>Invalid";
	name = "Valid Name :;";
	
	courseList[9] = {ID:ID, name:name};
	
	ID = "<b>Invalid";
	name = "";
	
	courseList[10] = {ID:ID, name:name};
	
	return courseList;
}

function createInvalidEvaluationList()
{
	var name;
	var instructions;
	var commentsEnabled;
	var start;
	var startTime;
	var deadline;
	var deadlineTime;
	var gracePeriod;
	
	var evaluationList = new Array();
	
	name = "Invalid Name This is Way too Long to be an Evaluation Name";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "true";
	start = "01/01/2018";
	startTime = "0100";
	deadline = "01/01/2018";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[0] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "<hi> '";
	instructions = "Valid instructions ' &s <hi> dwdwadw wadwadawdwaaaaaawaaaaaaaaaaa";
	commentsEnabled = "false";
	start = "01/01/2018";
	startTime = "0100";
	deadline = "01/01/2018";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[1] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "%s invalid";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "false";
	start = "01/01/2018";
	startTime = "0100";
	deadline = "01/01/2018";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[2] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name11";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "false";
	start = "01/12/2018";
	startTime = "0100";
	deadline = "01/01/2018";
	deadlineTime = "2359";
	gracePeriod = "5";
	
	evaluationList[3] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name22";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "true";
	start = "21/11/2018";
	startTime = "0100";
	deadline = "10/01/2018";
	deadlineTime = "2359";
	gracePeriod = "30";
	
	evaluationList[4] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name33";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "false";
	start = "21/11/2019";
	startTime = "1100";
	deadline = "21/11/2019";
	deadlineTime = "1000";
	gracePeriod = "25";
	
	evaluationList[5] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name44";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "false";
	start = "21/11/2019";
	startTime = "1100";
	deadline = "21/11/2019";
	deadlineTime = "1100";
	gracePeriod = "20";
	
	evaluationList[6] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};

	/* PHANTOM
	name = "Valid Name";
	instructions = "Valid instructionsk k' &s <hi>";
	commentsEnabled = "false";
	start = "22/01/2017";
	startTime = "0100";
	deadline = "21/01/2017";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[7] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	*/
	
	return evaluationList;
}

function createInvalidStudentList()
{
	var studentList = new Array();
	
	name = "Name is longer than forty two forty two forty two characters";
	email = "validemail@gmail.com";
	teamName = "Good Team";
	comments = "";
	
	studentList[0] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Name has % chars";
	email = "validemail@gmail.com";
	teamName = "Good Team";
	comments = "";
	
	studentList[1] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Name has <b> chars";
	email = "validemail@gmail.com";
	teamName = "Good Team";
	comments = "Good comments";
	
	studentList[2] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Name has &d chars";
	email = "validemail@gmail.com";
	teamName = "Good Team";
	comments = "Good comments";
	
	studentList[3] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Name has &d chars";
	email = "validemail@gmail.com";
	teamName = "Good Team";
	comments = "Good comments";
	
	studentList[4] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Name has ^%@ chars";
	email = "validemail@gmail.com";
	teamName = "Good Team";
	comments = "";
	
	studentList[5] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "emailhasnoatgmail.com";
	teamName = "Good Team";
	comments = "";
	
	studentList[6] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "emailiswaytoolongmorethanfortycharacters@gmailveryveryverylong.com";
	teamName = "Good Team";
	comments = "Good Comments";
	
	studentList[7] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "emailhas;@gmail.com";
	teamName = "Good Team";
	comments = "Good Comments";
	
	studentList[8] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "emailhasa space@gmail.com";
	teamName = "Good Team";
	comments = "";
	
	studentList[9] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "emailhasadollarsign@gmail$.com";
	teamName = "Good Team";
	comments = "";
	
	studentList[10] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "validemail@gmail.com";
	teamName = "Team Name has more than twenty five characters which is a lot";
	comments = "Good comments";
	
	studentList[11] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Bad ' Name";
	email = "emailiswaytoolongmorethanfortycharacters@gmailveryveryverylong.com";
	teamName = "Good Team";
	comments = "Good Comments";
	
	studentList[12] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Good Name";
	email = "emailiswaytoolongmorethanfortycharacters@gmailveryveryverylong.com";
	teamName = "Team Name has more than twenty five characters which is a lot";
	comments = "Good Comments";
	
	studentList[13] = {name:name, teamName:teamName, email:email, comments:comments};
	
	name = "Bad \" Name";
	email = "validemail@gmail.com";
	teamName = "Team Name has more than twenty five characters which is a lot";
	comments = "";
	
	studentList[14] = {name:name, email:email, teamName:teamName, comments:comments};
	
	return studentList;
}

function createValidCourseList()
{
	var courseList = new Array();
	
	var ID;
	var name;
	
	ID = "Valid-ID";
	name = "<b>Valid name</b>";
	
	courseList[0] = {ID:ID, name:name};
	
	ID = "ValidID-12345678901";
	name = "Valid name 1234567890123456789";
	
	courseList[1] = {ID:ID, name:name};
	
	ID = "ValidID.11-";
	name = "Valid name 1234567890123456789";
	
	courseList[2] = {ID:ID, name:name};
	
	return courseList;
}

function createValidEvaluationList()
{
	var name;
	var instructions;
	var commentsEnabled;
	var start;
	var startTime;
	var deadline;
	var deadlineTime;
	var gracePeriod;
	
	// Make sure evaluation names differ
	var evaluationList = new Array();
	
	name = "Valid Name1";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "true";
	start = "01/01/2019";
	startTime = "0100";
	deadline = "01/03/2019";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[0] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name2";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "false";
	start = "01/01/2019";
	startTime = "0100";
	deadline = "02/01/2019";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[1] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name3";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "true";
	start = "20/01/2019";
	startTime = "0100";
	deadline = "02/02/2019";
	deadlineTime = "2359";
	gracePeriod = "10";
	
	evaluationList[2] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name4";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "true";
	start = "20/03/2018";
	startTime = "0100";
	deadline = "02/02/2019";
	deadlineTime = "2359";
	gracePeriod = "30";
	
	evaluationList[3] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name5";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "false";
	start = "20/03/2018";
	startTime = "0100";
	deadline = "20/03/2019";
	deadlineTime = "2359";
	gracePeriod = "15";
	
	evaluationList[4] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	name = "Valid Name6";
	instructions = "Valid instructions ' &s <hi>";
	commentsEnabled = "true";
	start = "20/01/2018";
	startTime = "2300";
	deadline = "20/01/2018";
	deadlineTime = "2359";
	gracePeriod = "20";
	
	evaluationList[5] = { name:name, instructions:instructions, commentsEnabled:commentsEnabled, start:start,
			startTime:startTime, deadline:deadline, deadlineTime:deadlineTime, gracePeriod:gracePeriod};
	
	return evaluationList;
}

function createValidStudentList(numberOfStudents)
{
	var studentList = new Array();
	
	for(var x = 0; x < numberOfStudents; x++)
	{
		name = "Good Name-.," + x;
		email = "validemail" + x + "@gmail.com";
		teamName = "Good Team" + (x%40);
		
		if(x%2 == 0)
		{
			comments = "Good Comments" + x;
		}
		
		else
		{
			comments = "";
		}
		
		googleID = "";
		
		studentList[x] = {name:name, email:email, teamName:teamName, googleID:googleID, comments:comments};
		
	}
	
	return studentList;
}

/*
 * Returns
 * 
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error
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

/*
 * Returns
 * 
 * 0: successful
 * 1: server error 
 * 2: deadline passed
 * 3: fields missing
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
 * 0: successful
 * 1: server error
 * 2: unable to change teams
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

function editStudentListNameTeamComments(studentList)
{
	for(var x = 0; x < studentList.length; x++)
	{
		if(x % 2 == 0)
		{
			studentList[x].name = studentList[x].name + x;
		}
		
		if(x % 3 == 0)
		{
			studentList[x].teamName = studentList[x].teamName + x;
		}
		
		if(x % 4 == 0)
		{
			studentList[x].comments = studentList[x].comments + x;
		}
	}
	
	return studentList;
}

function editStudentListNameTeamCommentsGoogleIDEmail(studentList)
{
	for(var x = 0; x < studentList.length; x++)
	{
		if(x % 2 == 0)
		{
			studentList[x].name = studentList[x].name + x;
		}
		
		if(x % 3 == 0)
		{
			studentList[x].teamName = studentList[x].teamName + x;
		}
		
		if(x % 4 == 0)
		{
			studentList[x].comments = studentList[x].comments + x;
		}
		
		if(x % 5 == 0)
		{
			studentList[x].googleID = studentList[x].googleID + x;
		}
		
		if(x % 6 == 0)
		{
			studentList[x].email = x + studentList[x].email;
		}
	}
	
	return studentList;
}

/*
 * Returns
 * 
 * reports: successful
 * 1: server error
 * 2: input empty
 * 3: input invalid
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
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETCOURSELIST); 
		
		return handleGetCourseList();
	}
}

/*
 * Returns
 * 
 * evaluationList: successful
 * 1: server error
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

/*
 * Returns
 * 
 * studentList: successful
 * 1: server error
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
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_GETSUBMISSIONLIST + "&" + COURSE_ID + "=" +
				encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName)); 
		
		return handleGetSubmissionList();
	}
}

// IMPORTANT: DUE TO SAME FUNCTION NAME IN BOTH COORDINATOR AND STUDENT JS FILES, THE GETSUBMISSIONLIST
// AND HANDLEGETSUBMISSIONLIST FUNCTIONS FOR STUDENTS HAVE BEEN RENAMED WITH A POSTFIX OF "Student". 
// MOREOVER, PLEASE EDIT THE METHOD TO CALL THE CORRECT HANDLER FUNCTION

/*
 * Returns
 * 
 * submissionList: successful
 * 1: server error
 * 
 */
function getSubmissionListStudent(courseID, evaluationName)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_GETSUBMISSIONLIST + "&" + COURSE_ID + "=" +
				encodeURIComponent(courseID) + "&" + EVALUATION_NAME + "=" + encodeURIComponent(evaluationName)); 
		
		return handleGetSubmissionListStudent();
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
 * 5: course exists
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
 * 0: successful
 * 1: server error
 * 5: evaluation exists
 * 6: course has no teams
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
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error
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
 * 0: successful
 * 1: server error 
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
 * 0: successful
 * 1: server error
 * 2: unable to change teams
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
 * reports: successful
 * 1: server error
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
 * evaluationList: successful
 * 1: server error
 * 
 */
function handleGetEvaluationList()
{
	if (xmlhttp.status == 200) 
	{
		var evaluations = xmlhttp.responseXML.getElementsByTagName("evaluations")[0];
		var evaluationList = new Array(); 
		var now = new Date();
		
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

				// Convert local time zone to ms
				var nowTime = now.getTime();
				
				// Obtain local time zone offset
				var localOffset = now.getTimezoneOffset() * 60000;
				
				// Obtain UTC time
				var UTC = nowTime + localOffset;
				
				// Add the time zone of evaluation
				var nowMilliS = UTC + (timeZone * 60 * 60 * 1000);
				
				now.setTime(nowMilliS);
				
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
 * studentList: successful
 * 1: server error
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
 * submissionList: successful
 * 1: server error
 * 
 */
function handleGetSubmissionListStudent()
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

function isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime)
{
	start = convertDateFromDDMMYYYYToMMDDYYYY(start);
	deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);
	
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
	
	else if(name.match(/^[a-zA-Z0-9 ,.-]*$/) == null)
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
		xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_LOGOUT);
	}
	
	handleLogout();
}

function replaceAll(source,stringToFind,stringToReplace)
{
	return source.split(stringToFind).join(stringToReplace);

}

function startATD(withScalability)
{
	document.getElementById('button_startatd').style.visibility = "hidden";
	document.getElementById('atd').style.display = "inline";
	
	// Test Add/Delete Courses
	var results;
	
	results = testAddDeleteCourses();
	
	if(results == 0)
	{
		document.getElementById('addDeleteCourses').innerHTML = "<br><font color=\"green\">PASSED</font>";
	}
	
	else
	{
		document.getElementById('addDeleteCourses').innerHTML = "<br><font color=\"red\">FAILED</font>";
	}
	
	results = testEnrolStudents();
	
	if(results == 0)
	{
		document.getElementById('enrolStudents').innerHTML = "<br><font color=\"green\">PASSED</font>";
	}
	
	else
	{
		document.getElementById('enrolStudents').innerHTML = "<br><font color=\"red\">FAILED</font>";
	}
	
	results = testEditDeleteStudents();
	
	if(results == 0)
	{
		document.getElementById('editDeleteStudents').innerHTML = "<br><font color=\"green\">PASSED</font>";
	}
	
	else
	{
		document.getElementById('editDeleteStudents').innerHTML = "<br><font color=\"red\">FAILED</font>";
	}
	
	results = testAddDeleteEvaluations();
	
	if(results == 0)
	{
		document.getElementById('addDeleteEvaluations').innerHTML = "<br><font color=\"green\">PASSED</font>";
	}
	
	else
	{
		document.getElementById('addDeleteEvaluations').innerHTML = "<br><font color=\"red\">FAILED</font>";
	}
	
	if(withScalability == true)
	{
		
		results = testScalability(300);
		
		if(results == 0)
		{
		document.getElementById('scalability').innerHTML = "<br><font color=\"green\">PASSED</font>";
		}
	
		else
		{
			document.getElementById('scalability').innerHTML = "<br><font color=\"red\">FAILED</font>";
		}
	}
	
	results = testSubmitEvaluation();
	
	if(results == 0)
	{
		document.getElementById('submitEvaluations').innerHTML = "<br><font color=\"green\">PASSED</font>";
	}
	
	else
	{
		document.getElementById('submitEvaluations').innerHTML = "<br><font color=\"red\">FAILED</font>";
	}
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

/*
 * Returns
 * 
 * 0: pass
 * 1: fail
 * 
 */
function testAddDeleteCourses()
{
	// Attempt to add invalid
	var invalidCourseList = createInvalidCourseList();
	
	for(x = 0; x < invalidCourseList.length; x++)
	{
		addCourse(invalidCourseList[x].ID, invalidCourseList[x].name);
	}
	
	// Verify that they did not get added
	var courseList = getCourseList();
	
	for(x = 0; x < invalidCourseList.length; x++)
	{
		for(y = 0; y < courseList.length; y++)
		{
			if(invalidCourseList[x].ID == courseList[y].ID)
			{
				return 1;
			}
		}
	}
	
	// Attempt to add valid courses	
	var validCourseList = createValidCourseList();
	
	for(x = 0; x < validCourseList.length; x++)
	{
		addCourse(validCourseList[x].ID, validCourseList[x].name);
	}
	
	// Verify that they got added
	courseList = getCourseList();
	var isAdded;
	
	for(x = 0; x < validCourseList.length; x++)
	{
		isAdded = false;
		
		for(y = 0; y < courseList.length; y++)
		{
			if(validCourseList[x].ID == courseList[y].ID)
			{
				isAdded = true;
			}
		}
		
		if(!isAdded)
		{
			return 1;
		}
	}
	
	// Constraint #1: Attempt to add a course bearing an existing course ID
	var previousCourseCount = courseList.length;
	
	addCourse(validCourseList[0].ID, "test");
	
	courseList = getCourseList();
	
	if(previousCourseCount != courseList.length)
	{
		return 1;
	}
	
	// Delete the added courses
	for(x = 0; x < validCourseList.length; x++)
	{
		deleteCourse(validCourseList[x].ID);
	}
	
	// Verify that they got deleted
	courseList = getCourseList();
	
	for(x = 0; x < validCourseList.length; x++)
	{
		for(y = 0; y < courseList.length; y++)
		{
			if(validCourseList[x].ID == courseList[y].ID)
			{
				return 1;
			}
		}
	}
	
	return 0;
}

/*
 * Returns
 * 
 * 0: pass
 * 1: fail
 * 
 */
function testAddDeleteEvaluations()
{
	// Add a course
	addCourse(courseID, courseName);
	
	// Enrol specific valid students 
	var validStudentList = createValidStudentList(20);
	
	for(x = 0; x < validStudentList.length; x++)
	{
		input = validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
		validStudentList[x].email + "\t" + validStudentList[x].comments;
		
		x++;
		
		if(x < validStudentList.length)
		{
			input = input + "\n" + validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
			validStudentList[x].email + "\t" + validStudentList[x].comments;
		}
		
		enrolStudents(input, courseID);
	}
	
	// Attempt to add invalid evaluations
	invalidEvaluationList = createInvalidEvaluationList();
	
	for(x = 0; x < invalidEvaluationList.length; x++)
	{
		addEvaluation(courseID, invalidEvaluationList[x].name, invalidEvaluationList[x].instructions, 
				invalidEvaluationList[x].commentsEnabled, invalidEvaluationList[x].start, invalidEvaluationList[x].startTime, 
				invalidEvaluationList[x].deadline, invalidEvaluationList[x].deadlineTime, 8, invalidEvaluationList[x].gracePeriod)
	}
	
	// Verify that the invalid evaluations were not added
	evaluationList = getEvaluationList(courseID);
	
	for(x = 0; x < invalidEvaluationList.length; x++)
	{
		for(y = 0; y < evaluationList.length; y++)
		{
			if(invalidEvaluationList[x].courseID == evaluationList[y].courseID)
			{
				return 1;
			}
		}
	}
	
	// Attempt to add valid evaluations
	validEvaluationList = createValidEvaluationList();
	
	for(x = 0; x < validEvaluationList.length; x++)
	{
		addEvaluation(courseID, validEvaluationList[x].name, validEvaluationList[x].instructions, 
				validEvaluationList[x].commentsEnabled, validEvaluationList[x].start, validEvaluationList[x].startTime, 
				validEvaluationList[x].deadline, validEvaluationList[x].deadlineTime, 8, validEvaluationList[x].gracePeriod)
	}
	
	// Verify that the valid evaluations were added
	evaluationList = getEvaluationList(courseID);
	
	var added;
	
	for(x = 0; x < validEvaluationList.length; x++)
	{
		added = false;
		
		for(y = 0; y < evaluationList.length; y++)
		{
			if(courseID == evaluationList[y].courseID && 
					validEvaluationList[x].name == evaluationList[y].name)
			{
				added = true;
				break;
			}
		}
		
		if(!added)
		{
			return 1;
		}
	}
	
	// Delete the evaluations
	for(x = 0; x < validEvaluationList.length; x++)
	{
		deleteEvaluation(courseID, validEvaluationList[x].name);
	}
	
	// Verify the deletion - no evaluations for the particular courseID should exist
	evaluationList = getEvaluationList(courseID);
	
	for(x = 0; x < evaluationList.length; x++)
	{
		if(evaluationList[x].courseID == courseID)
		{
			return 1;
		}
	}
	
	// Attempt to add a valid evaluation
	addEvaluation(courseID, evaluationName, "Testing", true, "01/01/2019", "2359", "01/02/2019", "2359", "8", "25");
	
	// Verify that the valid evaluation was added
	evaluationList = getEvaluationList(courseID);
	
	var isAdded = false;
	
	for(x = 0; x < evaluationList.length; x++)
	{
		if(evaluationList[x].courseID == courseID)
		{
			isAdded = true;
			break;
		}
	}
	
	if(!isAdded)
	{
		return 1;
	}
	
	// Verify that the submission entries were created 
	var fromTeammates;
	var toTeammates;
	var teamName;
	var count;
	var student;
	var exists;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		student = validStudentList[x].fromStudent;
		teamName = validStudentList[x].teamName;
		count = 0;
		
		fromTeammates = new Array();
		toTeammates = new Array();
		
		// Find the teammates
		for(y = 0; y < validStudentList.length; y++)
		{
			if(validStudentList[y].fromStudent == student && validStudentList[y].teamName == teamName)
			{
				toTeammates[count++] = validStudentList[y].toStudent;
			}
		}
		
		// Make sure there are teammates -> student too
		count = 0;
		
		for(y = 0; y < validStudentList.length; y++)
		{
			if(validStudentList[y].toStudent == student && validStudentList[y].teamName == teamName)
			{
				// Make sure the student is in the team
				fromTeammates[count++] = validStudentList[y].fromStudent;
			}
		}
		
		// Check that toTeammates and fromTeammates correspond
		for(y = 0; y < fromTeammates.length; y++)
		{
			exists = false;
			
			for(z = 0; z < toTeammates.length; z++)
			{
				if(fromTeammates[y] == toTeammates[z])
				{
					exists = true;
					break;
				}
			}
			
			if(!exists)
			{
				return 1;
			}
		}
	}
	
	// Edit the student information
	validStudentList = editStudentListNameTeamComments(validStudentList);
	
	for(x = 0; x < validStudentList.length; x++)
	{
		student = validStudentList[x].fromStudent;
		teamName = validStudentList[x].teamName;
		count = 0;
		
		fromTeammates = new Array();
		toTeammates = new Array();
		
		// Find the teammates
		for(y = 0; y < validStudentList.length; y++)
		{
			if(validStudentList[y].fromStudent == student && validStudentList[y].teamName == teamName)
			{
				toTeammates[count++] = validStudentList[y].toStudent;
			}
		}
		
		// Make sure there are teammates -> student too
		count = 0;
		
		for(y = 0; y < validStudentList.length; y++)
		{
			if(validStudentList[y].toStudent == student && validStudentList[y].teamName == teamName)
			{
				// Make sure the student is in the team
				fromTeammates[count++] = validStudentList[y].fromStudent;
			}
		}
		
		// Check that toTeammates and fromTeammates correspond
		for(y = 0; y < fromTeammates.length; y++)
		{
			exists = false;
			
			for(z = 0; z < toTeammates.length; z++)
			{
				if(fromTeammates[y] == toTeammates[z])
				{
					exists = true;
					break;
				}
			}
			
			if(!exists)
			{
				return 1;
			}
		}
	}
	
	deleteCourse(courseID);
	
	return 0;
}

/*
 * Returns
 * 
 * 0: pass
 * 1: fail
 * 
 */
function testEditDeleteStudents()
{
	// Add a course
	addCourse(courseID, courseName);
	
	// Enrol students 
	var validStudentList = createValidStudentList(10);
	
	for(x = 0; x < validStudentList.length; x++)
	{
		input = validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
		validStudentList[x].email + "\t" + validStudentList[x].comments;
		
		x++;
		
		if(x < validStudentList.length)
		{
			input = input + "\n" + validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
			validStudentList[x].email + "\t" + validStudentList[x].comments;
		}
		
		enrolStudents(input, courseID);
	}
	
	// Edit students
	validStudentList = editStudentListNameTeamComments(validStudentList);
	
	for(x = 0; x < validStudentList.length; x++)
	{
		editStudent(courseID, validStudentList[x].email, validStudentList[x].name, validStudentList[x].teamName, 
				validStudentList[x].email, ("" + x), validStudentList[x].comments);
	}
	
	// Verify that the students have been edited
	var studentList = getStudentList(courseID);
	var exists;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		exists = false;
		
		for(y = 0; y < studentList.length; y++)
		{
			if(studentList[y].email == validStudentList[x].email && studentList[y].name == validStudentList[x].name &&
					studentList[y].teamName == validStudentList[x].teamName && studentList[y].comments == validStudentList[x].comments)
			{
				exists = true;
				break;
			}
		}
		
		if(!exists)
		{
			return 1;
		}
	}
	
	// Delete some students
	var deletedStudentList = [];
	var counter = 0;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		if(x % 4 == 0)
		{
			deletedStudentList[counter++] = validStudentList[x];
			deleteStudent(courseID, validStudentList[x].email);
		}
	}
	
	// Verify the deletion
	studentList = getStudentList(courseID);
	var deleted;
	
	
	for(x = 0; x < validStudentList.length; x++)
	{
		deleted = false;
		
		for(y = 0; y < deletedStudentList.length; y++)
		{
			if(deletedStudentList[y].email == validStudentList[x].email)
			{
				deleted = true;
				break;
			}
		}
		
		exists = false;
		
		for(y = 0; y < studentList.length; y++)
		{
			if(studentList[y].email == validStudentList[x].email)
			{
				exists = true;
				break;
			}
		}
		
		if(deleted && exists || !deleted && !exists)
		{
			return 1;
		}
	}
	
	// Delete all students remaining in course
	deleteAllStudents(courseID);
	
	// Verify no students remain in the course
	studentList = getStudentList(courseID);
	
	if(studentList.length != 0)
	{
		return 1;
	}
	
	deleteCourse(courseID);
	
	return 0;
	
}

/*
 * Returns
 * 
 * 0: pass
 * 1: fail
 * 
 */
function testEnrolStudents()
{
	// Add a course
	addCourse(courseID, courseName);
	
	// Verify that course has been added
	var courseList = getCourseList();
	var isAdded = false;
	
	for(x = 0; x < courseList.length; x++)
	{
		if(courseList[x].ID == courseID)
		{
			isAdded = true;
			break;
		}
	}
	
	if(!isAdded)
	{
		return 1;
	}
	
	// Attempt to enrol invalid students
	var invalidStudentList = createInvalidStudentList();
	var input;
	var reports;
	
	for(x = 0; x < invalidStudentList.length; x++)
	{
		input = invalidStudentList[x].teamName + "\t" + invalidStudentList[x].name + "\t" +
		invalidStudentList[x].email + "\t" + invalidStudentList[x].comments;
		
		enrolStudents(input, courseID);
	}
	
	// Verify that the invalid students were not added
	var studentList = getStudentList(courseID);
	
	if(studentList.length != 0)
	{
		return 1;
	}

	// Attempt to enrol valid students
	var validStudentList = createValidStudentList(30);
	
	for(x = 0; x < validStudentList.length; x++)
	{
		input = validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
		validStudentList[x].email + "\t" + validStudentList[x].comments;
		
		x++;
		
		if(x < validStudentList.length)
		{
			input = input + "\n" + validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
			validStudentList[x].email + "\t" + validStudentList[x].comments;
		}
		
		enrolStudents(input, courseID);
	}

	// Verify that the valid students were added
	studentList = getStudentList(courseID);
	var previousStudentListCount = studentList.length;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		isAdded = false;
		
		for(y = 0; y < studentList.length; y++)
		{
			if(validStudentList[x].email == studentList[y].email)
			{
				isAdded = true;
			}
		}
		
		if(!isAdded)
		{
			return 1;
		}
	}
	
	// Edit the students with enrollment function
	validStudentList = editStudentListNameTeamComments(validStudentList);
	
	for(x = 0; x < validStudentList.length; x++)
	{
		input = validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
		validStudentList[x].email + "\t" + validStudentList[x].comments;
		
		x++;
		
		if(x < validStudentList.length)
		{
			input = input + "\n" + validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
			validStudentList[x].email + "\t" + validStudentList[x].comments;
		}
		
		enrolStudents(input, courseID);
	}
	
	// Verify that they were edited correctly
	studentList = getStudentList(courseID);
	var isFound;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		isFound = false;
		
		for(y = 0; y < studentList.length; y++)
		{
			if(studentList[y].email == validStudentList[x].email && studentList[y].name == validStudentList[x].name &&
					studentList[y].teamName == validStudentList[x].teamName && studentList[y].comments == validStudentList[x].comments)
			{
				isFound = true;
			}
		}
		
		if(!isFound)
		{
			return 1;
		}
	}
	
	// Constraint Check #1: Make sure that no additional students with same e-mail were added
	if(studentList.length != previousStudentListCount)
	{
		return 1;
	}
	
	deleteCourse(courseID);
	
	// Verify that the course has been deleted
	courseList = getCourseList();
	
	for(x = 0; x < courseList.length; x++)
	{
		if(courseList[x].ID == courseID)
		{
			return 1;
		}
	}
	
	// Verify that the students have been deleted
	studentList = getStudentList(courseID);
	
	if(studentList.length != 0)
	{
		return 1;
	}
	
	return 0;
		
}

/*
 * Returns
 * 
 * 0: pass
 * 1: fail
 * 
 */
function testScalability(numberOfStudents)
{
	// Add a course
	addCourse(courseID, courseName);
	
	// Enrol students 
	var validStudentList = createValidStudentList(numberOfStudents);
	var input;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		input = validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
		validStudentList[x].email + "\t" + validStudentList[x].comments;
		
		x++;
		
		if(x < validStudentList.length)
		{
			input = input + "\n" + validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
			validStudentList[x].email + "\t" + validStudentList[x].comments;
		}
		
		enrolStudents(input, courseID);
	}
	
	// Create an evaluation for the course
	var now = new Date();
	
	var currentDate = convertDateToDDMMYYYY(now);
	var currentTime = (parseInt(convertDateToHHMM(now).substring(0,2)) + 1)% 24;
	
	if(addEvaluation(courseID, "test", "this is a sample test instructions", 
			true, "11/11/2018", currentTime, "11/11/2019", currentTime, 8, 10) == 1)
	{
		return 1;
	}
	
	
	// Try to see if the submissions can be retrieved
	if(getSubmissionList(courseID, courseName) == 1)
	{
		return 1;
	}
	
	// Cleanup
	deleteCourse(courseID);
	
	return 0;
}

function testSubmitEvaluation()
{
	// Add a course
	addCourse(courseID, courseName);
	
	// Enrol students 
	var validStudentList = createValidStudentList(2);
	var input;
	
	for(x = 0; x < validStudentList.length; x++)
	{
		input = validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
		validStudentList[x].email + "\t" + validStudentList[x].comments;
		
		x++;
		
		if(x < validStudentList.length)
		{
			input = input + "\n" + validStudentList[x].teamName + "\t" + validStudentList[x].name + "\t" +
			validStudentList[x].email + "\t" + validStudentList[x].comments;
		}
		
		enrolStudents(input, courseID);
	}
	
	// Enrol specific students to test submission and results
	input = "Test Team" + "\t" + "CC" + "\t" + "cctest@thisisfakeemail.com" + "\t" + "nah" + "\n" +
		"Test Team" + "\t" + "HY" + "\t" + "hytest@thisisfakeemail.com" + "\t" + "nothing" + "\n" +
		"Test Team" + "\t" + "GG" + "\t" + "ggtest@thisisfakeemail.com" + "\n";
	
	if(enrolStudents(input, courseID) == 1)
	{
		return 1;
	}
	
	var studentList = getStudentList(courseID);
	
	// Get the registration keys of the 3 students
	var registrationKeyForCC;
	var registrationKeyForHY;
	var registrationKeyForGG;

	for(loop = 0; loop < studentList.length; loop++)
	{
		if(studentList[loop].email == "cctest@thisisfakeemail.com")
		{
			registrationKeyForCC = studentList[loop].registrationKey;
		}
		
		else if(studentList[loop].email == "hytest@thisisfakeemail.com")
		{
			registrationKeyForHY = studentList[loop].registrationKey;
		}
		
		else if(studentList[loop].email == "ggtest@thisisfakeemail.com")
		{
			registrationKeyForGG = studentList[loop].registrationKey;
		}
	}
	
	// Scenario: 3 students in the same team for an evaluation
	var now = new Date();
	
	var currentDate = convertDateToDDMMYYYY(now);
	var currentTime = (parseInt(convertDateToHHMM(now).substring(0,2)) + 1)% 24 + 1;
	
	if(addEvaluation(courseID, "test", "this is a sample test instructions", 
			true, "11/11/2018", currentTime, "11/11/2019", currentTime, 8, 10) == 1)
	{
		return 1;
	}
	
	var submissionList;
	
	// Verify if join course works as intended
	// Attempt to join with invalid key	
	if(joinCourse("123invalid") != 3)
	{
		return 1;
	}
	
	if(joinCourse(registrationKeyForCC) == 1)
	{
		return 1;
	}
	
	// Attempt to join a course twice
	if(joinCourse(registrationKeyForHY) != 2)
	{
		return 1;
	}
	
	// Do submission for CC
	submissionList = getSubmissionListStudent(courseID, "test");

	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
		{
			submissionList[loop].points = 100;
			submissionList[loop].justification = "cc to cc justi";
			submissionList[loop].commentsToStudent = "cc to cc";
		}
		
		else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
		{
			submissionList[loop].points = 90;
			submissionList[loop].justification = "cc to hy justi";
			submissionList[loop].commentsToStudent = "cc to hy";
		}
		
		else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
		{
			submissionList[loop].points = 110;
			submissionList[loop].justification = "cc to gg justi";
			submissionList[loop].commentsToStudent = "cc to gg";
		}
	}
	
	if(submitEvaluation(submissionList, true) == 1)
	{
		return 1;
	}
	
	// Verify submission done
	submissionList = getSubmissionListStudent(courseID, "test");
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 100 ||
					submissionList[loop].justification != "cc to cc justi" ||
					submissionList[loop].commentsToStudent != "cc to cc")
			{
				return 1;
			}
		}
		
		else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 90 ||
					submissionList[loop].justification != "cc to hy justi" ||
					submissionList[loop].commentsToStudent != "cc to hy")
			{
				return 1;
			}
		}
		
		else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 110 ||
					submissionList[loop].justification != "cc to gg justi" ||
					submissionList[loop].commentsToStudent != "cc to gg")
			{
				return 1;
			}
		}
	}
	
	leaveCourse(courseID);
	
	// Do submission for HY
	if(joinCourse(registrationKeyForHY) == 1)
	{
		return 1;
	}
	
	submissionList = getSubmissionListStudent(courseID, "test");
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
		{
			submissionList[loop].points = 110;
			submissionList[loop].justification = "hy to cc justi";
			submissionList[loop].commentsToStudent = "hy to cc";
		}
		
		else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
		{
			submissionList[loop].points = 110;
			submissionList[loop].justification = "hy to hy justi";
			submissionList[loop].commentsToStudent = "hy to hy";
		}
		
		else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
		{
			submissionList[loop].points = 100;
			submissionList[loop].justification = "hy to gg justi";
			submissionList[loop].commentsToStudent = "hy to gg";
		}
	}
	
	if(submitEvaluation(submissionList, true) == 1)
	{
		return 1;
	}
	
	// Verify submission done
	submissionList = getSubmissionListStudent(courseID, "test");

	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 110 ||
					submissionList[loop].justification != "hy to cc justi" ||
					submissionList[loop].commentsToStudent != "hy to cc")
			{
				return 1;
			}
		}
		
		else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 110 || 
					submissionList[loop].justification != "hy to hy justi" ||
					submissionList[loop].commentsToStudent != "hy to hy")
			{
				return 1;
			}
		}
		
		else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 100 ||
					submissionList[loop].justification != "hy to gg justi" ||
					submissionList[loop].commentsToStudent != "hy to gg")
			{
				return 1;
			}
		}
	}
	
	leaveCourse(courseID);
	
	// Do submission for GG
	if(joinCourse(registrationKeyForGG) == 1)
	{
		return 1;
	}
	
	submissionList = getSubmissionListStudent(courseID, "test");
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
		{
			submissionList[loop].points = 100;
			submissionList[loop].justification = "gg to cc justi";
			submissionList[loop].commentsToStudent = "gg to cc";
		}
		
		else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
		{
			submissionList[loop].points = 50;
			submissionList[loop].justification = "gg to hy justi";
			submissionList[loop].commentsToStudent = "gg to hy";
		}
		
		else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
		{
			submissionList[loop].points = 90;
			submissionList[loop].justification = "gg to gg justi";
			submissionList[loop].commentsToStudent = "gg to gg";
		}
	}
	
	if(submitEvaluation(submissionList, true) == 1)
	{
		return 1;
	}
	
	// Verify submission done
	submissionList = getSubmissionListStudent(courseID, "test");
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 100 ||
					submissionList[loop].justification != "gg to cc justi" ||
					submissionList[loop].commentsToStudent != "gg to cc")
			{
				return 1;
			}
		}
		
		else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 50 ||
					submissionList[loop].justification != "gg to hy justi" ||
					submissionList[loop].commentsToStudent != "gg to hy")
			{
				return 1;
			}
		}
		
		else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
		{
			if(submissionList[loop].points != 90 ||
					submissionList[loop].justification != "gg to gg justi" ||
					submissionList[loop].commentsToStudent != "gg to gg")
			{
				return 1;
			}
		}
	}
	
	leaveCourse(courseID);
	
	// Verify that the points scaling is working for student
	// Check for CC
	if(joinCourse(registrationKeyForCC) == 1)
	{
		return 1;
	}
	
	submissionList = getSubmissionResultsList(courseID, "test");
	
	for(loop = 0; loop < submissionList; loop++)
	{
		if(submissionList.fromStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList.points != 100)
			{
				return 1;
			}
		}
		
		else if(submissionList.fromStudent == "hytest@thisisfakeemail.com")
		{
			if(submissionList.points != 103.125)
			{
				return 1;
			}
		}
		
		else if(submissionList.fromStudent == "ggtest@thisisfakeemail.com")
		{
			if(submissionList.points != 125)
			{
				return 1;
			}
		}
	}
	
	leaveCourse(courseID);
	
	// Check for HY
	if(joinCourse(registrationKeyForHY) == 1)
	{
		return 1;
	}
	
	submissionList = getSubmissionResultsList(courseID, "test");
	
	for(loop = 0; loop < submissionList; loop++)
	{
		if(submissionList.fromStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList.points != 90)
			{
				return 1;
			}
		}
		
		else if(submissionList.fromStudent == "hytest@thisisfakeemail.com")
		{
			if(submissionList.points != 103.125)
			{
				return 1;
			}
		}
		
		else if(submissionList.fromStudent == "ggtest@thisisfakeemail.com")
		{
			if(submissionList.points != 62.5)
			{
				return 1;
			}
		}
	}
	
	leaveCourse(courseID);
	
	// Check for GG
	if(joinCourse(registrationKeyForGG) == 1)
	{
		return 1;
	}
	
	submissionList = getSubmissionResultsList(courseID, "test");
	
	for(loop = 0; loop < submissionList; loop++)
	{
		if(submissionList.fromStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList.points != 110)
			{
				return 1;
			}
		}
		
		else if(submissionList.fromStudent == "hytest@thisisfakeemail.com")
		{
			if(submissionList.points != 93.75)
			{
				return 1;
			}
		}
		
		else if(submissionList.fromStudent == "ggtest@thisisfakeemail.com")
		{
			if(submissionList.points != 112.5)
			{
				return 1;
			}
		}
	}
	
	leaveCourse(courseID);
	
	// Ensure that coordinator gets the correct results
	submissionList = getSubmissionList(courseID, "test");
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].fromStudent == "cctest@thisisfakeemail.com")
		{
			// Verify points bump ratio
			if(submissionList[loop].pointsBumpRatio != 1)
			{
				return 1;
			}
			
			if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 100 ||
						submissionList[loop].justification != "cc to cc justi" ||
						submissionList[loop].commentsToStudent != "cc to cc")
				{
					return 1;
				}
			}
			
			else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 90 ||
						submissionList[loop].justification != "cc to hy justi" ||
						submissionList[loop].commentsToStudent != "cc to hy")
				{
					return 1;
				}
			}
			
			else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 110 ||
						submissionList[loop].justification != "cc to gg justi" ||
						submissionList[loop].commentsToStudent != "cc to gg")
				{
					return 1;
				}
			}
		}
		
		else if(submissionList[loop].fromStudent == "hytest@thisisfakeemail.com")
		{
			// Verify points bump ratio
			if(submissionList[loop].pointsBumpRatio != 0.9375)
			{
				return 1;
			}
			
			if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 110 ||
						submissionList[loop].justification != "hy to cc justi" ||
						submissionList[loop].commentsToStudent != "hy to cc")
				{
					return 1;
				}
			}
			
			else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 110 || 
						submissionList[loop].justification != "hy to hy justi" ||
						submissionList[loop].commentsToStudent != "hy to hy")
				{
					return 1;
				}
			}
			
			else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 100 ||
						submissionList[loop].justification != "hy to gg justi" ||
						submissionList[loop].commentsToStudent != "hy to gg")
				{
					return 1;
				}
			}
		}
		
		else if(submissionList[loop].fromStudent == "ggtest@thisisfakeemail.com")
		{
			// Verify points bump ratio
			if(submissionList[loop].pointsBumpRatio != 1.25)
			{
				return 1;
			}
			
			if(submissionList[loop].toStudent == "cctest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 100 ||
						submissionList[loop].justification != "gg to cc justi" ||
						submissionList[loop].commentsToStudent != "gg to cc")
				{
					return 1;
				}
			}
			
			else if(submissionList[loop].toStudent == "hytest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 50 ||
						submissionList[loop].justification != "gg to hy justi" ||
						submissionList[loop].commentsToStudent != "gg to hy")
				{
					return 1;
				}
			}
			
			else if(submissionList[loop].toStudent == "ggtest@thisisfakeemail.com")
			{
				if(submissionList[loop].points != 90 ||
						submissionList[loop].justification != "gg to gg justi" ||
						submissionList[loop].commentsToStudent != "gg to gg")
				{
					return 1;
				}
			}
		}
	}
	
	// Coordinator edit evaluation results
	submissionList = getSubmissionList(courseID, "test");
	
	var editSubmissionList = new Array();
	var count = 0;
	
	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].fromStudent == "cctest@thisisfakeemail.com")
		{
			editSubmissionList[count++] = submissionList[loop];
		}
	}
	
	for(loop = 0; loop < editSubmissionList.length; loop++)
	{
		editSubmissionList[loop].commentsToStudent = "cc!";
		editSubmissionList[loop].points = 100;
	}
	
	if(editEvaluationResults(editSubmissionList, true) == 1)
	{
		return 1;
	}
	
	// Verify results are edited
	submissionList = getSubmissionList(courseID, "test");

	for(loop = 0; loop < submissionList.length; loop++)
	{
		if(submissionList[loop].fromStudent == "cctest@thisisfakeemail.com")
		{
			if(submissionList[loop].commentsToStudent != "cc!" || submissionList[loop].points != 100)
			{
				return 1;
			}
		}
	}
	
	deleteCourse(courseID);
	
	return 0;
	
}

function trim(stringToTrim) 
{
	return stringToTrim.replace(/\s+$/,"");
}
