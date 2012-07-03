/**
 * Helper functions for Teammates Require: jquery
 */

var debugEnabled = true;

function logSubmission(s) {
	if (debugEnabled) {
		var msg = "SUBMISSION: ";
		msg += s.fromStudent + "|";
		msg += s.toStudent + "|";
		msg += s.points + "|";
		msg += s.pointsBumpRatio + "|";
		msg += s.courseID + "|";
		msg += s.evaluationName + "|";
		msg += s.teamName + "|";
		msg += s.commentsToStudent + "\n";
		msg += s.justification;
		console.log(msg);
	}
}

function logSubmissionList(lst) {
	if (debugEnabled) {
		for ( var i = 0; i < lst.length; i++) {
			logSubmission(lst[i]);
		}
	}
}

function logSummaryList(lst) {
	if (debugEnabled) {
		for ( var i = 0; i < lst.length; i++) {
			var msg = "summary list " + i + " ";
			msg += lst[i].toStudent + "|";
			msg += lst[i].claimedPoints + "|";
			msg += lst[i].average + "|";
			msg += lst[i].difference + "|";
			msg += lst[i].courseID + "|";
			msg += lst[i].evaluationName + "|";
			msg += lst[i].teamName + "|";
			console.log(msg);
		}
	}
}
function replaceAll(source, stringToFind, stringToReplace) {
	return source.split(stringToFind).join(stringToReplace);

}

function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g, "");
}

function escape(str) {
	str = str.replace(/'/g, "\\'");
	return str;
}

/**
 * Function that encodes ASCII printable characters for function arguments
 * Character code 32-127
 * 
 * Omitting characters 128-255 as these generally do not interfere with our normal functions
 * 
 * @param str
 */
function encodeChar(str) {
	str = str.replace(/&/g, "&amp;");
	str = str.replace(/#/g, "&#35;");
	str = str.replace(/\\/g, "&#92;");

	// Skipping character 32 (space)
	str = str.replace(/!/g, "&#33;");
	str = str.replace(/"/g, "&quot;");
	// Replace # second since it appears in ASCII equivalent of characters
	str = str.replace(/\$/g, "&#36;");
	str = str.replace(/%/g, "&#37;");
	// Replace & first since it appears in ASCII equivalent of characters
	str = str.replace(/'/g, "\\'");
	str = str.replace(/\(/g, "&#40;");
	str = str.replace(/\)/g, "&#41;");
	str = str.replace(/\*/g, "&#42;");
	str = str.replace(/\+/g, "&#43;");
	str = str.replace(/,/g, "&#44;");
	str = str.replace(/-/g, "&#45;");
	str = str.replace(/\./g, "&#46;");
	str = str.replace(/\//g, "&#47;");
	// Skipping characters 48-57 (digits 0-9)
	str = str.replace(/:/g, "&#58;");
	// Skip # since it doesn't interfere with any of our processes
	str = str.replace(/</g, "&lt;");
	str = str.replace(/=/g, "&#61;");
	str = str.replace(/>/g, "&gt;");
	str = str.replace(/\?/g, "&#63;");
	str = str.replace(/@/g, "&#64;");
	// Skipping characters 65-90 (alphabets A-Z)
	str = str.replace(/\[/g, "&#91;");
	// Replace \ third so that so that any existing \ in the string is converted
	// and the \ for ' remains intact in the string
	str = str.replace(/\]/g, "&#93;");
	str = str.replace(/\^/g, "&#94;");
	str = str.replace(/_/g, "&#95;");
	str = str.replace(/`/g, "&#96;");
	// Skipping characters 97-122 (alphabets a-z)
	str = str.replace(/\{/g, "&#123;");
	str = str.replace(/\|/g, "&#124;");
	str = str.replace(/\}/g, "&#125;");
	str = str.replace(/~/g, "&#126;");
	// Skipping character 127 (command DEL)
	
	return str;
}

/**
 * Function that encodes ASCII printable characters for printing purposes
 * Character code 32-127
 *
 * Omitting characters 128-255 as these generally do not interfere with our normal functions
 * 
 * @param str
 */
function encodeCharForPrint(str) {
	str = str.replace(/&/g, "&amp;");
	str = str.replace(/#/g, "&#35;");
	
	// Skipping character 32 (space)
	str = str.replace(/!/g, "&#33;");
	str = str.replace(/"/g, "&quot;");
	// Replace # second since it appears in ASCII equivalent of characters
	str = str.replace(/\$/g, "&#36;");
	str = str.replace(/%/g, "&#37;");
	// Replace & first since it appears in ASCII equivalent of characters
	str = str.replace(/\\'/g, "&#39;");
	str = str.replace(/\'/g, "&#39;");
	str = str.replace(/\(/g, "&#40;");
	str = str.replace(/\)/g, "&#41;");
	str = str.replace(/\*/g, "&#42;");
	str = str.replace(/\+/g, "&#43;");
	str = str.replace(/,/g, "&#44;");
	str = str.replace(/-/g, "&#45;");
	str = str.replace(/\./g, "&#46;");
	str = str.replace(/\//g, "&#47;");
	// Skipping characters 48-57 (digits 0-9)
	str = str.replace(/:/g, "&#58;");
	// Skip ; since it doesn't interfere with any of our processes
	str = str.replace(/</g, "&lt;");
	str = str.replace(/=/g, "&#61;");
	str = str.replace(/>/g, "&gt;");
	str = str.replace(/\?/g, "&#63;");
	str = str.replace(/@/g, "&#64;");
	// Skipping characters 65-90 (alphabets A-Z)
	str = str.replace(/\[/g, "&#91;");
	str = str.replace(/\\/g, "&#92;");
	str = str.replace(/\]/g, "&#93;");
	str = str.replace(/\^/g, "&#94;");
	str = str.replace(/_/g, "&#95;");
	str = str.replace(/`/g, "&#96;");
	// Skipping characters 97-122 (alphabets a-z)
	str = str.replace(/\{/g, "&#123;");
	str = str.replace(/\|/g, "&#124;");
	str = str.replace(/\}/g, "&#125;");
	str = str.replace(/~/g, "&#126;");
	// Skipping character 127 (command DEL)
	
	return str;
}

function setEditEvaluationResultsStatusMessage(message) {
	if (message == "") {
		clearEditEvaluationResultsStatusMessage();
		return;
	}

	$("#coordinatorEditEvaluationResultsStatusMessage").html(message).show();
}

function clearEditEvaluationResultsStatusMessage() {
	$("#coordinatorEditEvaluationResultsStatusMessage").html("").hide();
}

function toggleEditEvaluationResultsStatusMessage(statusMsg) {
	setEditEvaluationResultsStatusMessage(statusMsg);
}

/**-----------------------UI Related Helper Functions-----------------------**/
function scrollToTop(div) {
	document.getElementById(div).scrollIntoView(true);
}

/*
 * div: statusMessage
 */
function setStatusMessage(message) {
	if (message == "") {
		clearStatusMessage();
		return;
	}
	$("#statusMessage").html(message).show();
}

function setStatusMessageToLoading() {
	$("#statusMessage").html(DISPLAY_LOADING).show();
}

function clearStatusMessage() {
	$("#statusMessage").html("").hide();
}

function toggleStatusMessage(statusMsg) {
	setStatusMessage(statusMsg);
}

function alertServerError() {
	alert(DISPLAY_SERVERERROR);
}