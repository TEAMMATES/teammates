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
var COURSE_TOTALSTUDENTS = "coursetotalstudents";
var COURSE_UNREGISTERED = "courseunregistered";
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

//MESSAGES
var MSG_COURSE_EXISTS = "course exists";
var MSG_COURSE_NOTEAMS = "course has no teams";
var MSG_EVALUATION_ADDED = "evaluation added";
var MSG_EVALUATION_EDITED = "evaluation edited";
var MSG_EVALUATION_EXISTS = "evaluation exists";
var MSG_EVALUATION_UNABLETOCHANGETEAMS = "evaluation ongoing unable to change teams";


//RESPONSE TAG
var TAG_COORDINATOR_COURSE = "courses";