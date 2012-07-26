
module('coordCourseEnroll.js');

test('highlightError(start, end)', function(){
	// N/A, trivial function. This function highlights a section of text in a specific textarea element
	expect(0);
});


test('checkEnrollmentInput(input)', function(){
	//Valid input - Single student, tab separator, no comments
	var validInput1 = 'Team 1\tGoh Chun Teck\tchunteck.88@gmail.com';
	
	//Valid input - Single student, tab separator, with comments
	var validInput2 = 'Team 1\tGoh Chun Teck\tchunteck.88@gmail.com\t1 person only';
	
	//Valid input - Multiple students, tab separator, some students with comments
	var validInput3 = 'Team 1\tGoh Chun Teck\tchunteck.88@gmail.com\tteammate 1\nTeam 1\tBob\tbob@gmail.com\nTeam 2\tAlice\talice@gmail.com\tanother team\n';
	
	//Valid input - Single student, | separator, no comments
	var validInput4 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com';
	
	//Valid input - Single student, | separator, with comments
	var validInput5 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|1 person only';
	
	//Valid input - Multiple students, | separator, some students with comments
	var validInput6 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nTeam 1|Bob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\n';
	
	
	
	//Invalid input - Single student, | separator, missing fields
	var invalidInput1 = 'Team 1|chunteck.88@gmail.com';
	
	//Invalid input - Single student, | separator, extra fields
	var invalidInput2 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|extra field|extra field|extra field\n';
	
	//Invalid input - Single student, tab separator, invalid student name
	var invalidInput3 = 'Team 1\tG0h Chun Teck with a super duper long name\tchunteck.88@gmail.com';
	
	//Invalid input - Single student, | separator, invalid email
	var invalidInput4 = 'Team 1|Goh Chun Teck|qwerty@invalidemail';
	
	//Invalid input - Single student, tab separator, invalid teamname
	var invalidInput5 = 'This is a invalid teamname because its too long\tGoh Chun Teck\tchunteck.88@gmail.com';
	
	//Invalid input - Multiple students, | separator, student with missing team
	var invalidInput6 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nBob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\n';
	
	//Invalid input - Multiple students, | separator, student with invalid team name, student with invalid email
	var invalidInput7 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nSuper long team name more than 25 chars|Bob|bob@gmail.com\nTeam 2|Alice|alice|another team\nNo Team|John|john@john.com|This guy has no team.';
	
	//Invalid input - Multiple students, student with missing team and name, student with missing team, student with invalid name, missing email
	var invalidInput8 = 'chunteck.88@gmail.com|teammate 1\nBob|bob@gmail.com\nTeam 2|Alice has a very long name more than 40 chars\n';
	
	//Retrieved from coordCourseEnroll.jsp. Required for adding an extra element in the test page for the test to work
	$("body").append('<textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea>');
	
	equal(checkEnrollmentInput(validInput1), true, "Single student, tab separator, valid");
	equal(checkEnrollmentInput(validInput2), true, "Single student, tab separator, comments included, valid");
	equal(checkEnrollmentInput(validInput3), true, "Multiple students, tab separator, valid");
	
	equal(checkEnrollmentInput(validInput4), true, "Single student, | separator, valid");
	equal(checkEnrollmentInput(validInput5), true, "Single student, | separator, comments included, valid");
	equal(checkEnrollmentInput(validInput6), true, "Multiple students, | separator, valid");
	
	equal(checkEnrollmentInput(invalidInput1), false, "Too little fields");
	equal(checkEnrollmentInput(invalidInput2), false, "Too many fields");
	equal(checkEnrollmentInput(invalidInput3), false, "Invalid student name");
	equal(checkEnrollmentInput(invalidInput4), false, "Invalid email");
	equal(checkEnrollmentInput(invalidInput5), false, "Invalid team name");
	equal(checkEnrollmentInput(invalidInput6), false, "Multiple students with various multiple errors");
	equal(checkEnrollmentInput(invalidInput7), false, "Multiple students with various multiple errors");
	equal(checkEnrollmentInput(invalidInput8), false, "Multiple students with various multiple errors");
});