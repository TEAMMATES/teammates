
module('common.js');


test('scrollToTop()', function(){
	// N/A, trivial function
	expect(0);
});


test('setStatusMessage(message,error)', function(){	
	$("body").append('<div id="statusMessage"></div>');
	var message = "Status Message";
	
	//('background-color') == "rgba(0, 0, 0, 0)" is for chrome browser
	//('background-color') == "transparent" is for firefox browser
	
	setStatusMessage(message, false);
	equal($("#statusMessage").html(), message, "Normal status message");
	ok(($("#statusMessage").css('background-color') == "rgba(0, 0, 0, 0)" || $("#statusMessage").css('background-color') == "transparent"), "No background");
	setStatusMessage("", false);
	equal($("#statusMessage").html(), "", "Empty status message");
	ok(($("#statusMessage").css('background-color') == "rgba(0, 0, 0, 0)" || $("#statusMessage").css('background-color') == "transparent"), "No background");
	setStatusMessage(message, true);
	equal($("#statusMessage").html(), message, "Normal status message");
	equal($("#statusMessage").css('background-color'), "rgb(255, 153, 153)", "Red background");
	setStatusMessage("", true);
	equal($("#statusMessage").html(), "", "Normal status message");
	ok(($("#statusMessage").css('background-color') == "rgba(0, 0, 0, 0)" || $("#statusMessage").css('background-color') == "transparent"), "No background");
	
});


test('clearStatusMessage()', function(){
	clearStatusMessage();
	equal($("#statusMessage").html(), "", "Status message cleared");
	ok(($("#statusMessage").css('background-color') == "rgba(0, 0, 0, 0)" || $("#statusMessage").css('background-color') == "transparent"), "No background");
});
