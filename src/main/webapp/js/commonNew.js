/* CONSTANTS */

var DIV_TOPOFPAGE = "topOfPage";
var DIV_HEADER_OPERATION = "headerOperation";

var COURSE_ID_MAX_LENGTH = 21;
var COURSE_NAME_MAX_LENGTH = 38;
var EVAL_NAME_MAX_LENGTH = 38;

//PARAMETERS
var COURSE_ID = "courseid";

var EVALUATION_NAME = "evaluationname";

var STUDENT_EMAIL = "email";
var STUDENT_ID = "id";

/**
 * XMLHttpRequest Constants
 * 
 * */
var SERVERERROR = 1;
var CONNECTION_OK = 200;

//OPERATIONS
var OPERATION_COORDINATOR_INFORMSTUDENTSOFEVALUATIONCHANGES = "coordinator_informstudentsofevaluationchanges";
var OPERATION_COORDINATOR_PUBLISHEVALUATION = "coordinator_publishevaluation";
var OPERATION_COORDINATOR_UNPUBLISHEVALUATION = "coordinator_unpublishevaluation";
var OPERATION_COORDINATOR_REMINDSTUDENTS = "coordinator_remindstudents";

//messages:
var DISPLAY_ERROR_UNDEFINED_HTTPREQUEST = "Error: Undefined XMLHttpRequest.";
var DISPLAY_SERVERERROR = "Connection to the server has timed out. Please refresh the page.";
/*------------------------------------------PRINT COORDINATOR PAGE------------------------------------------*/

/**
 * Coordinator view student detail (individual record) TODO: Improve UI,
 * abstract print_detail function:: printCourseStudentForm
 */
function printStudent(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	var outputHeader = "<h1>STUDENT DETAILS</h1>";
	var output = "<table class=\"detailform\">" + "<tr>"
	+ "<td class=\"fieldname\">Student Name:</td>" + "<td>" + name
	+ "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Team Name:</td>" + "<td>"
	+ encodeCharForPrint(teamName) + "</td>" + "</tr>" + "<tr><"
	+ "td class=\"fieldname\">E-mail Address:</td>" + "<td>" + email
	+ "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Google ID:</td>" + "<td>";

	if (googleID == "") {
		output = output + "-";
	} else {
		output = output + encodeChar(googleID);
	}

	output = output + "</td>" + "</tr>" + "<tr>"
	+ "<td class=\"fieldname\">Registration Key:</td>"
	+ "<td id='t_courseKey'>" + registrationKey + "</td>" + "</tr>"
	+ "<tr>" + "<td class=\"fieldname\">Comments:</td>" + "<td>";

	if (comments == "") {
		output = output + "-";
	} else {
		output = output + encodeCharForPrint(comments);
	}

	output = output
	+ "</div>"
	+ "</td>"
	+ "</tr>"
	+ "</table>"
	+ "<br /><br /><br />"
	+ "<input type =\"button\" class=\"t_back button\" onClick=\"displayCourseInformation('"
	+ courseID + "');\" value=\"Back\"/>" + "<br /><br />";

	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;
}

/**
 * Coordinator edit student detail TODO: Merge into printStudent
 */
function printEditStudent(courseID, email, name, teamName, googleID,
		registrationKey, comments) {
	var outputHeader = "<h1>EDIT STUDENT</h1>";
	var output = "<form>" + "<table class=\"headerform\">" + "<tr>"
	+ "<td class=\"fieldname\">Student Name*:</td>"
	+ "<td><input class=\"fieldvalue\" type=\"text\" value=\""
	+ name
	+ "\" name=\"editname\" id=\"editname\"/></td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Team Name*:</td>"
	+ "<td><input class=\"fieldvalue\" type=\"text\" value=\""
	+ encodeCharForPrint(teamName)
	+ "\" name=\"editteamname\" id=\"editteamname\"/></td>"
	+ "</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">E-mail Address*:</td>"
	+

	"<td><input class=\"fieldvalue\" type=\"text\" value=\""
	+ email
	+ "\" name=\"editemail\" id=\"editemail\"/></td>"
	+

	"</tr>"
	+ "<tr>"
	+ "<td class=\"fieldname\">Google ID:</td>"
	+

	(googleID == "" ? "<td><input class=\"fieldvalue\" type=\"text\" value=\""
			+ encodeChar(googleID)
			+ "\" name=\"editgoogleid\" id=\"editgoogleid\"/></td>"
			: "<td><input class=\"fieldvalue\" type=\"text\" value=\""
				+ encodeChar(googleID)
				+ "\" name=\"editgoogleid\" id=\"editgoogleid\" disabled=\"true\" /></td>")
				+

				"</tr>"
				+ "<tr>"
				+ "<td class=\"fieldname\">Comments:</td>"
				+ "<td><textarea class =\"textvalue\" name=\"editcomments\" id=\"editcomments\" rows=\"6\" cols=\"80\">"
				+ encodeCharForPrint(comments)
				+ "</textarea></td>"
				+ "</tr>"
				+ "</table>";

	var outputButtons = "<input type=\"button\" class=\"button\" name=\"button_editstudent\" id=\"button_editstudent\" value=\"Save Changes\" />"
		+ "<input type=\"button\" class=\"button\" onClick=\"displayCourseInformation('"
		+ courseID + "')\" value=\"Back\" />" + "</form>" + "<br /><br />";

	document.getElementById(DIV_STUDENT_EDITBUTTONS).innerHTML = outputButtons;
	document.getElementById(DIV_STUDENT_INFORMATION).innerHTML = output;
	document.getElementById(DIV_HEADER_OPERATION).innerHTML = outputHeader;

	document.getElementById('button_editstudent').onclick = function() {
		var editName = document.getElementById('editname').value;
		var editTeamName = document.getElementById('editteamname').value;
		var editEmail = document.getElementById('editemail').value;
		var editGoogleID = document.getElementById('editgoogleid').value;
		var editComments = document.getElementById('editcomments').value;

		doEditStudent(courseID, email, editName, editTeamName, editEmail,
				editGoogleID, editComments);
	};
}

function isStudentEmailValid(email) {
	return email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i)!=null;
}

function isStudentNameValid(name) {
	if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
			|| name.indexOf("\"") >= 0) {
		return false;
	} else if (name.match(/^.[^\t]*$/) == null) {
		return false;
	} else if (name.length > 40) {
		return false;
	}
	return true;
}

function isStudentTeamNameValid(teamName) {
	return teamName.length<=24;
}

function getXMLObject() {
	var xmlHttp = null;
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			xmlHttp = null;
		}
	}
	if (xmlHttp===null) {
		xmlHttp = new XMLHttpRequest();
	}
	return xmlHttp;
}
