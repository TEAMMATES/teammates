

module('coordinator.js');


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
	equal(isStudentEmailValid("a@yahoo.com"), true, "a@yahoo.com - valid");
	equal(isStudentEmailValid("teammates.coord@gmail.com"), true, "teammates.coord@gmail.com - valid");
	
	equal(isStudentEmailValid("qwerty"), false, "qwerty - invalid");
	equal(isStudentEmailValid("!@#$%.com"), false, "!@#$%.com - invalid");
	equal(isStudentEmailValid("123@321"), false, "123@321 - invalid");
	equal(isStudentEmailValid("re8$**!f712"), false, "re8$**!f712 - invalid");
	equal(isStudentEmailValid("@yahoo.com"), false, "@yahoo.com - invalid");
});
	

test('isStudentNameValid(name)', function(){
	equal(isStudentNameValid('Tom Jacobs'), true, "Tom Jacobs - valid");
	equal(isStudentNameValid('EMILY D. TANG'), true, "EMILY D. TANG - valid");
	equal(isStudentNameValid('Billy S/O Ben'), true, "Billy S/O Ben - valid");
	equal(isStudentNameValid('$#!!@#'), true, "$#!!@# - valid");
	equal(isStudentNameValid('Alice123'), true, "Alice123 - valid");
	equal(isStudentNameValid(generateRandomString(STUDENTNAME_MAX_LENGTH)), true, "Maximum characters - valid");
	
	equal(isStudentNameValid(""), false, "Empty name - invalid");
	equal(isStudentNameValid(generateRandomString(STUDENTNAME_MAX_LENGTH + 1)), false, "Exceed number of maximum characters - invalid");
});


test('isStudentTeamNameValid(teamname)', function(){
	equal(isStudentTeamNameValid("My Awesome Team"), true, "My Awesome Team - valid");
	equal(isStudentTeamNameValid("123$%^789"), true, "123$%^789 - valid");
	equal(isStudentTeamNameValid("A"), true, "A - valid");
	equal(isStudentTeamNameValid(" "), true, "whitespace - valid");
	equal(isStudentTeamNameValid(generateRandomString(TEAMNAME_MAX_LENGTH)), true, "Maximum characters - valid");
	
	equal(isStudentTeamNameValid(generateRandomString(TEAMNAME_MAX_LENGTH + 1)), false, "Exceed maximum number of characters - invalid");
});


test('isStudentInputValid(editName, editTeamName, editEmail)', function(){
	equal(isStudentInputValid("Bob", "Bob's Team", "Bob.Team@hotmail.com"), true, "All valid");
	
	equal(isStudentInputValid("", "Bob's Team", "Bob.Team@hotmail.com"), false, "Empty name - invalid");
	equal(isStudentInputValid("Bob", "", "Bob@gmail.com"), false, "Empty teamname - invalid");
	equal(isStudentInputValid("Bob", "Bob's Team", "qwerty"), false, "Invalid email");
	equal(isStudentInputValid("Billy", generateRandomString(TEAMNAME_MAX_LENGTH + 1), "d.Team@hotmail.com"), false, "Invalid teamname");
	equal(isStudentInputValid("Bob", "Bob's Team", ""), false, "Empty email - invalid");
	equal(isStudentInputValid(generateRandomString(STUDENTNAME_MAX_LENGTH + 1), "Bob's Team", ""), false, "invalid name");
	
});


function generateRandomString(len){
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var data = '';

	for (var i=0; i<len; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		data += chars.substring(rnum,rnum+1);
	}

	return data;
}