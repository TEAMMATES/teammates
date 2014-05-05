

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
    equal(isStudentInputValid(generateRandomString(NAME_MAX_LENGTH + 1), "Bob's Team", ""), false, "invalid name");
});
