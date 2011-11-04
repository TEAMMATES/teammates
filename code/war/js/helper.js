/**
 * Helper functions for Teammates
 * Require: jquery 
 */

function replaceAll(source,stringToFind,stringToReplace)
{
	return source.split(stringToFind).join(stringToReplace);

}

function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
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
	str = str.replace(/'/g, "&#039;");
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
