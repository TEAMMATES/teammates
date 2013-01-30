
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


test('checkEvaluationForm()', function(){
	// N/A, requires elements in the page
	expect(0);
});

test('isEmailValid(email)', function(){
	equal(isEmailValid("test@gmail.com"), true, "test@gmail.com - valid");
	
	equal(isEmailValid("email"), false, "email - invalid");
	equal(isEmailValid("email@email"), false, "email@email - invalid");
	equal(isEmailValid("@yahoo.com"), false, "@yahoo.com - invalid");
	equal(isEmailValid("email.com"), false, "email.com - invalid");
});
	

test('isNameValid(name)', function(){
	equal(isNameValid('	Tom Jacobs,.	\'()-\/ \\  '), true, "alphanumerics, fullstop, comma, round brackets, slashes, apostrophe, hyphen - valid");
	equal(isNameValid(generateRandomString(NAME_MAX_LENGTH)), true, "Maximum characters - valid");
	
	equal(isNameValid(""), false, "Empty name - invalid");
	equal(isNameValid(generateRandomString(NAME_MAX_LENGTH + 1)), false, "Exceed number of maximum characters - invalid");
	equal(isNameValid("Tom! Jacobs"), false, "! character - invalid");
	equal(isNameValid("Tom ^Jacobs"), false, "^ character - invalid");
	equal(isNameValid("Tom#"), false, "# character - invalid");
	equal(isNameValid("&Tom"), false, "& character - invalid");
	equal(isNameValid("J%cobs "), false, "% character - invalid");
	equal(isNameValid("Jacobs*"), false, "* character - invalid");
	equal(isNameValid("	+123	 "), false, "+ character - invalid");
	equal(isNameValid("a b c $ 1 2 3 4"), false, "$ character - invalid");
	equal(isNameValid("1@2@3  456"), false, "@ character - invalid");
	equal(isNameValid("Tom = Tom"), false, "= character - invalid");
	equal(isNameValid("Tom||Jacobs"), false, "| character - invalid");
	
});