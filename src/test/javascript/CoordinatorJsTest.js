

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
});
	

test('isStudentNameValid(name)', function(){
	equal(isStudentNameValid('Goh Chun Teck'), true, "Goh Chun Teck - valid");
	equal(isStudentNameValid('DAMITH C. RAJAPAKSE'), true, "DAMITH C. RAJAPAKSE - valid");
	equal(isStudentNameValid('Billy S/O Ben'), true, "Billy S/O Ben - valid");
	equal(isStudentNameValid('$#!!@#'), true, "$#!!@# - valid");
	equal(isStudentNameValid('Alice123'), true, "Alice123 - valid");
	
	equal(isStudentNameValid('Bob whose full name exceeds more than 40 characters'), false, "Bob whose full name exceeds more than 40 characters - invalid");
});


test('isStudentTeamNameValid(teamname)', function(){
	equal(isStudentTeamNameValid("My Awesome Team"), true, "My Awesome Team - valid");
	equal(isStudentTeamNameValid("123$%^789"), true, "123$%^789 - valid");
	equal(isStudentTeamNameValid("A"), true, "A - valid");
	
	equal(isStudentTeamNameValid("I have a super long teamname exceeding 25 charactrs"), false, "I have a super long teamname exceeding 25 charactrs - invalid");
});


test('isStudentInputValid(editName, editTeamName, editEmail)', function(){
	equal(isStudentInputValid("Bob", "Bob's Team", "Bob.Team@hotmail.com"), true, "All valid");
	
	equal(isStudentInputValid("", "Bob's Team", "Bob.Team@hotmail.com"), false, "Empty name - invalid");
	equal(isStudentInputValid("Bob", "", ""), false, "Empty teamname, email - invalid");
	equal(isStudentInputValid("Bob", "Bob's Team", "qwerty"), false, "Invalid email");
	equal(isStudentInputValid("Billy", "Super Long Team Name exceeding 25 characters", ".Team@hotmail"), false, "Invalid email and teamname");
	equal(isStudentInputValid("Bob", "Bob's Team", ""), false, "Empty email - invalid");
	equal(isStudentInputValid("Bob has a name with more than 40 characters", "Bob's Team", ""), false, "Empty email, invalid name");
	
});