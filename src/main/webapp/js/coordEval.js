var EVALUATION_START = "start";
var EVALUATION_STARTTIME = "starttime";
var EVALUATION_TIMEZONE = "timezone";

var DISPLAY_EVALUATION_PUBLISHED = "The evaluation has been published.";
var DISPLAY_EVALUATION_UNPUBLISHED = "The evaluation has been unpublished.";
var DISPLAY_EVALUATION_REMINDERSSENT = "Reminder e-mails have been sent out to those students.";
var DISPLAY_EVALUATION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in evaluation name.";
var DISPLAY_EVALUATION_NAME_LENGTHINVALID = "Evaluation name should not exceed 38 characters.";
var DISPLAY_EVALUATION_SCHEDULEINVALID = "The evaluation schedule (start/deadline) is not valid.<br />" +
										 "The start time should be in the future, and the deadline should be after start time.";
var DISPLAY_FIELDS_EMPTY = "Please fill in all the relevant fields.";

function isEvaluationNameLengthValid(name) {
	return name.length <= 22;
}

function isEvaluationNameValid(name) {
	if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
			|| name.indexOf("\"") >= 0) {
		return false;
	}
	if (name.match(/^[a-zA-Z0-9 ]*$/) == null) {
		return false;
	}
	return true;
}

function convertDateFromDDMMYYYYToMMDDYYYY(dateString) {
	return dateString.substring(3, 5) + "/" +
			dateString.substring(0, 2) + "/" +
			dateString.substring(6, 10);
}

function isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime) {
	start = convertDateFromDDMMYYYYToMMDDYYYY(start);
	deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);

	var now = new Date();

	start = new Date(start);
	deadline = new Date(deadline);

	// If the hour value is 24, then set time to 23:59
	if (startTime != 24) {
		start.setHours(startTime);
	} else {
		start.setHours(23);
		start.setMinutes(59);
	}
	if (deadlineTime != 24) {
		deadline.setHours(deadlineTime);
	} else {
		deadline.setHours(23);
		deadline.setMinutes(59);
	}

	if (start > deadline) {
		return false;
	} else if (now > start) {
		return false;
	} else if (!(start > deadline || deadline > start)) {
		if (startTime >= deadlineTime) {
			return false;
		}
	}

	return true;
}

function isEditEvaluationScheduleValid(start, startTime, deadline,
		deadlineTime, timeZone, activated, status) {
	start = convertDateFromDDMMYYYYToMMDDYYYY(start);
	deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);

	var now = new Date();

	start = new Date(start);
	deadline = new Date(deadline);

	// If the hour value is 24, then set time to 23:59
	if (startTime != 24) {
		start.setHours(startTime);
	} else {
		start.setHours(23);
		start.setMinutes(59);
	}
	if (deadlineTime != 24) {
		deadline.setHours(deadlineTime);
	} else {
		deadline.setHours(23);
		deadline.setMinutes(59);
	}

	if (start > deadline) {
		return false;
	} else if (status == "AWAITING") {
		// Open evaluation should be done by system only.
		// Thus, coordinator cannot change evaluation ststus from AWAITING to
		// OPEN
		if (start < now) {
			return false;
		}
	}/*else if(!activated && start < now) {
		return false;
	}*/
	
	return true;
}

/**
 * Check whether the evaluation input (which is passed as a form) is valid
 * @param form
 * @returns {Boolean}
 */
function checkAddEvaluation(form){
	var courseID = form.courseid.value;
	var name = form.evaluationname.value;
	var commentsEnabled = form.commentsstatus.value;
	var start = form.start.value;
	var startTime = form.starttime.value;
	var deadline = form.deadline.value;
	var deadlineTime = form.deadlinetime.value;
	var timeZone = form.timezone.value;
	var gracePeriod = form.graceperiod.value;
	var instructions = form.instr.value;

	if (courseID == "" || name == "" || start == "" || startTime == ""
		|| deadline == "" || deadlineTime == "" || timeZone == ""
			|| gracePeriod == "" || instructions == "") {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (!isEvaluationNameValid(name)) {
		setStatusMessage(DISPLAY_EVALUATION_NAMEINVALID, true);
		return false;
	} else if (!isEvaluationNameLengthValid(name)) {
		setStatusMessage(DISPLAY_EVALUATION_NAME_LENGTHINVALID, true);
		return false;
	} else if (!isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime)) {
		setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID, true);
		return false;
	}
	return true;
}

/**
 * To be run on page finish loading, this will select the input: start date,
 * start time, and timezone based on client's time.
 */
function selectDefaultTimeOptions(){
	var now = new Date();
	
	var currentDate = convertDateToDDMMYYYY(now);
	var hours = convertDateToHHMM(now).substring(0, 2);
	var currentTime = (parseInt(hours) + 1) % 24;
	var timeZone = -now.getTimezoneOffset() / 60;

	document.getElementById(EVALUATION_START).value = currentDate;
	document.getElementById(EVALUATION_STARTTIME).value = currentTime;
	document.getElementById(EVALUATION_TIMEZONE).value = ""+timeZone;
}


/**
 * Sends an AJAX request to the server to publish an evaluation
 * and goes to specified URL on success.
 * Just don't provide the URL or give empty URL if no page change is desired
 * @param courseID
 * @param name
 * @param url
 */
function publishEvaluation(courseID, name, url) {
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function(){
			if (xmlhttp.readyState==4) {
				if(xmlhttp.status==200){
					setStatusMessage(DISPLAY_EVALUATION_PUBLISHED);
					if(url){
						window.location = url;
					}
				} else {
					alert(DISPLAY_SERVERERROR);
				}
			}
		};
		xmlhttp.open("POST", "/teammates", true);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_PUBLISHEVALUATION
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));
	} else {
		alert(DISPLAY_BROWSERERROR);
	}
}

/**
 * Sends an AJAX request to the server to unpublish an evaluation
 * and goes to specified URL on success
 * Just don't provide the URL or give empty URL if no page change is desired
 * @param courseID
 * @param name
 * @param url
 */
function unpublishEvaluation(courseID, name, url) {
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function(){
			if (xmlhttp.readyState==4) {
				if(xmlhttp.status==200){
					setStatusMessage(DISPLAY_EVALUATION_UNPUBLISHED);
					if(url){
						window.location = url;
					}
				} else {
					alert(DISPLAY_SERVERERROR);
				}
			}
		};
		xmlhttp.open("POST", "/teammates", true);
		xmlhttp.setRequestHeader("Content-Type",
		"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_UNPUBLISHEVALUATION
				+ "&" + COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(name));
	} else {
		alert(DISPLAY_BROWSERERROR);
	}
}

/**
 * Sends an AJAX request to the server to remind students to do evaluation
 * @param courseID
 * @param evaluationName
 */
function remindStudents(courseID, evaluationName) {
	if (xmlhttp) {
		xmlhttp.onreadystatechange = function(){
			clearStatusMessage();
			if (xmlhttp.readyState==4) {
				if(xmlhttp.status==200){
					setStatusMessage(DISPLAY_EVALUATION_REMINDERSSENT);
				} else {
					alert(DISPLAY_SERVERERROR);
				}
			}
		};
		xmlhttp.open("POST", "/teammates", true);
		xmlhttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_REMINDSTUDENTS + "&"
				+ COURSE_ID + "=" + encodeURIComponent(courseID) + "&"
				+ EVALUATION_NAME + "=" + encodeURIComponent(evaluationName));
	} else {
		alert(DISPLAY_BROWSERERROR);
	}
}

/**
 * Pops up confirmation dialog whether to delete specified evaluation
 * @param courseID
 * @param name
 * @returns
 */
function toggleDeleteEvaluationConfirmation(courseID, name) {
	return confirm("Are you sure you want to delete the evaluation " + name + " in " + courseID + "?");
}

/**
 * Shows the desired evaluation report based on the id.
 * This is for the evaluation results page.
 * @param id
 * 		One of:
 * 		<ul>
 * 		<li>coordinatorEvaluationSummaryTable</li>
 * 		<li>coordinatorEvaluationDetailedReviewerTable</li>
 * 		<li>coordinatorEvaluationDetailedRevieweeTable</li>
 * 		</ul>
 */
function showReport(id){
	$(".evaluation_result").hide();
	$("#"+id).show();
}

/**
 * Pops up confirmation dialog whether to publish or unpublish the specified
 * evaluation, depending on the boolean value in publish.
 * @param courseID
 * @param name
 * @param publish
 * 		true to publish, false to unpublish
 * @param url
 * 		The URL to go to after publishing. If no page change is desired,
 * 		just don't provide the URL or give empty URL 
 */
function togglePublishEvaluation(courseID, name, publish, url) {
	if (publish) {
		var s = confirm("Are you sure you want to publish the evaluation?");
		if (s == true) {
			scrollToTop();
			setStatusMessage(DISPLAY_LOADING);
			publishEvaluation(courseID, name, url);
		}
	} else {
		var s = confirm("Are you sure you want to unpublish the evaluation?");
		if (s == true) {
			scrollToTop();
			setStatusMessage(DISPLAY_LOADING);
			unpublishEvaluation(courseID, name, url);
		}
	}
}

/**
 * Pops up confirmation dialog whether to remind students to fill in a specified
 * evaluation.
 * @param courseID
 * @param evaluationName
 */
function toggleRemindStudents(courseID, evaluationName) {
	var s = confirm("Send e-mails to remind students who have not submitted their evaluations?");
	if (s == true) {
		scrollToTop();
		setStatusMessage(DISPLAY_LOADING);
		remindStudents(courseID, evaluationName);
	}
}