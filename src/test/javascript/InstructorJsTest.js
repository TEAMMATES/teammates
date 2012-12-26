

module('instructor.js');


test('toggleDeleteCourseConfirmation(courseID)', function(){
	// gives a popup, can't be tested
	expect(0);
});


test('toggleDeleteEvaluationConfirmation(courseID, name)', function(){
	// N/A, displays popup
	expect(0);
});


test('togglePublishEvaluation(name)', function(){
	// N/A, uses elements in the page
	expect(0);
});


test('toggleUnpublishEvaluation(name)', function(){
	// N/A, uses elements in the page
	expect(0);
});


test('toggleRemindStudents(evaluationName)', function(){
	// N/A, uses elements in the page
	expect(0);
});


test('isStudentEmailValid(email)', function(){
	equal(isStudentEmailValid("test@gmail.com"), true, "test@gmail.com - valid");
	
	equal(isStudentEmailValid("email"), false, "email - invalid");
	equal(isStudentEmailValid("email@email"), false, "email@email - invalid");
	equal(isStudentEmailValid("@yahoo.com"), false, "@yahoo.com - invalid");
	equal(isStudentEmailValid("email.com"), false, "email.com - invalid");
});
	

test('isStudentNameValid(name)', function(){
	equal(isStudentNameValid('	Tom Jacobs,.	\'()-\/ \\  '), true, "alphanumerics, fullstop, comma, round brackets, slashes, apostrophe, hyphen - valid");
	equal(isStudentNameValid(generateRandomString(STUDENTNAME_MAX_LENGTH)), true, "Maximum characters - valid");
	
	equal(isStudentNameValid(""), false, "Empty name - invalid");
	equal(isStudentNameValid(generateRandomString(STUDENTNAME_MAX_LENGTH + 1)), false, "Exceed number of maximum characters - invalid");
	equal(isStudentNameValid("Tom! Jacobs"), false, "! character - invalid");
	equal(isStudentNameValid("Tom ^Jacobs"), false, "^ character - invalid");
	equal(isStudentNameValid("Tom#"), false, "# character - invalid");
	equal(isStudentNameValid("&Tom"), false, "& character - invalid");
	equal(isStudentNameValid("J%cobs "), false, "% character - invalid");
	equal(isStudentNameValid("Jacobs*"), false, "* character - invalid");
	equal(isStudentNameValid("	+123	 "), false, "+ character - invalid");
	equal(isStudentNameValid("a b c $ 1 2 3 4"), false, "$ character - invalid");
	equal(isStudentNameValid("1@2@3  456"), false, "@ character - invalid");
	equal(isStudentNameValid("Tom = Tom"), false, "= character - invalid");
	equal(isStudentNameValid("Tom||Jacobs"), false, "| character - invalid");
	
});


test('isStudentTeamNameValid(teamname)', function(){
	equal(isStudentTeamNameValid("Team1_-)(&*^%$#@!."), true, "Team1_-)(&*^%$#@!. - valid");
	equal(isStudentTeamNameValid(generateRandomString(TEAMNAME_MAX_LENGTH)), true, "Maximum characters - valid");
	
	equal(isStudentTeamNameValid(generateRandomString(TEAMNAME_MAX_LENGTH + 1)), false, "Exceed maximum number of characters - invalid");
});


test('isStudentInputValid(editName, editTeamName, editEmail)', function(){
	equal(isStudentInputValid("Bob", "Bob's Team", "Bob.Team@hotmail.com"), true, "Valid Input");
	
	equal(isStudentInputValid("", "Bob's Team", "Bob.Team@hotmail.com"), false, "Empty name - invalid");
	equal(isStudentInputValid("Bob", "", "Bob@gmail.com"), false, "Empty teamname - invalid");
	equal(isStudentInputValid("Bob", "Bob's Team", ""), false, "Empty email - invalid");
	equal(isStudentInputValid("Bob", "Bob's Team", "qwerty"), false, "Invalid email");
	equal(isStudentInputValid("Billy", generateRandomString(TEAMNAME_MAX_LENGTH + 1), "d.Team@hotmail.com"), false, "Invalid teamname");
	equal(isStudentInputValid(generateRandomString(STUDENTNAME_MAX_LENGTH + 1), "Bob's Team", ""), false, "invalid name");
});
