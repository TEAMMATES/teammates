/*
 * common.js
 * 
 * scrollToTop() -> used in multiple files
 * setStatusMessage(message, error) -> used in multiple files
 * clearStatusMessage() -> used in multiple files
 * 
 */

module('common.js');

test('getDateWithTimeZoneOffset(timeZone)', function(){
	// Unable to test because it depends on the current date. Function is not used anyway
	expect(0);
});


test('scrollToTop()', function(){
	// N/A, trivial function
	expect(0);
});


test('setStatusMessage(message,error)', function(){	
	$("body").append('<div id="statusMessage"></div>');
	var message = "Status Message";
	
	setStatusMessage(message, false);
	equal($("#statusMessage").html(), message, "Normal status message");
	equal($("#statusMessage").css('background'), "", "No background");
	setStatusMessage("", false);
	equal($("#statusMessage").html(), "", "Empty status message");
	equal($("#statusMessage").css('background'), "", "No background");
	setStatusMessage(message, true);
	equal($("#statusMessage").html(), message, "Normal status message");
	equal($("#statusMessage").css('background-color'), "rgb(255, 153, 153)", "Red background");
	setStatusMessage("", true);
	equal($("#statusMessage").html(), "", "Normal status message");
	equal($("#statusMessage").css('background'), "", "No background");
	
});


test('clearStatusMessage()', function(){
	clearStatusMessage();
	equal($("#statusMessage").html(), "", "Cleared");
	equal($("#statusMessage").css('background'), "", "No background");
});
