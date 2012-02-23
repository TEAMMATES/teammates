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

function sanitize(str) {
	str = str.replace(/&/g, "&amp;");
	str = str.replace(/>/g, "&gt;");
	str = str.replace(/</g, "&lt;");
	str = str.replace(/"/g, "&quot;");
	str = str.replace(/'/g, "\\'");
	str = str.replace(/%/g, "&#37;");

	return str;
}

function sanitizeComments(str) {
	str = str.replace(/&/g, "&amp;");
	str = str.replace(/>/g, "&gt;");
	str = str.replace(/</g, "&lt;");
	str = str.replace(/"/g, "&quot;");
	str = str.replace(/\'/g, "&#39;");
	str = str.replace(/%/g, "&#37;");

	return str;
}

function setStatusMessage(message) {
	if (message == "") {
		clearStatusMessage();
		return;
	}
	$("#statusMessage").html(message).show();
}

function clearStatusMessage() {
	$("#statusMessage").html("").hide();
}

function toggleStatusMessage(statusMsg) {
	setStatusMessage(statusMsg);
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