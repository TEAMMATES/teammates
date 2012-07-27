
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
	
	//Valid input - Single student, tab separator, empty team name
	var validInput7 = '\tGoh Chun Teck\tchunteck.88@gmail.com';
	
	//Valid input - Single student, tab separator, whitespace team name
	var validInput8 = '  \tGoh Chun Teck\tchunteck.88@gmail.com';
	
	//Valid input - Testing max length of name (max 40 chars)
	var validInput9 = 'Team 1\t' + generateRandomString(40) + '\tchunteck.88@gmail.com';
	
	//Valid input - Testing max length of team name (max 24 chars)
	var validInput10 = generateRandomString(24) + '\tGoh Chun Teck\tchunteck.88@gmail.com';
	
	
	
	
	
	//Invalid input - Single student, | separator, missing fields
	var invalidInput1 = 'Team 1|chunteck.88@gmail.com';
	
	//Invalid input - Single student, | separator, extra fields
	var invalidInput2 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|extra field|extra field|extra field\n';
	
	//Invalid input - Single student, tab separator, invalid student name
	var invalidInput3 = 'Team 1\t' + generateRandomString(41) + '\tchunteck.88@gmail.com';
	
	//Invalid input - Single student, | separator, invalid email
	var invalidInput4 = 'Team 1|Goh Chun Teck|qwerty@invalidemail';
	
	//Invalid input - Single student, tab separator, invalid teamname
	var invalidInput5 = generateRandomString(25) + '\tGoh Chun Teck\tchunteck.88@gmail.com';
	
	//Invalid input - Multiple students, | separator, student with missing team
	var invalidInput6 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nBob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\n';
	
	//Invalid input - Multiple students, | separator, student with invalid team name
	var invalidInput7 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nSuper long team name more than 25 chars|Bob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\nNo Team|John|john@john.com|This guy has no team.';
	
	//Invalid input - Multiple students, student with student with invalid name, missing email
	var invalidInput8 = 'Team 1|Teck|chunteck.88@gmail.com|teammate 1\nTeam 1|Bob|bob@gmail.com\nTeam 2|Alice has a very long name more than 40 chars\n';
	
	//Invalid input - Single student, tab separator, empty name
	var invalidInput9 = 'Team 1\t\tchunteck.88@gmail.com';
	
	//Invalid input - Single student, tab separator, empty email
	var invalidInput10 = 'Team 1\tGoh Chun Teck\t';
	
	//Invalid input - Single student, tab separator, totally empty
	var invalidInput11 = '\t\t';
	
	//Invalid input - Single student, tab separator, whitespace email
	var invalidInput12 = 'Team 1\tGoh Chun Teck\t  ';
	
	//Invalid input - Single student, tab separator, whitespace name
	var invalidInput13 = 'Team 1\t  \tchunteck.88@gmail.com';
	
	

	
	
	//Retrieved from coordCourseEnroll.jsp. Required for adding an extra element in the test page for the test to work
	$("body").append('<textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea>');
	
	equal(checkEnrollmentInput(validInput1), true, "Single student, tab separator, valid");
	equal(checkEnrollmentInput(validInput2), true, "Single student, tab separator, comments included, valid");
	equal(checkEnrollmentInput(validInput3), true, "Multiple students, tab separator, valid");
	
	equal(checkEnrollmentInput(validInput4), true, "Single student, | separator, valid");
	equal(checkEnrollmentInput(validInput5), true, "Single student, | separator, comments included, valid");
	equal(checkEnrollmentInput(validInput6), true, "Multiple students, | separator, valid");
	
	equal(checkEnrollmentInput(validInput7), true, "Single student, \t separator, no team name");
	equal(checkEnrollmentInput(validInput8), true, "Single student, \t separator, whitespace team name");
	equal(checkEnrollmentInput(validInput9), true, "Testing max length of name(40 characters)");
	equal(checkEnrollmentInput(validInput10), true, "Testing max length of team name (24 characters)");
	
	equal(checkEnrollmentInput(invalidInput1), false, "Too little fields");
	equal(checkEnrollmentInput(invalidInput2), false, "Too many fields");
	equal(checkEnrollmentInput(invalidInput3), false, "Invalid student name");
	equal(checkEnrollmentInput(invalidInput4), false, "Invalid email");
	equal(checkEnrollmentInput(invalidInput5), false, "Invalid team name");
	equal(checkEnrollmentInput(invalidInput6), false, "Multiple students with various multiple errors");
	equal(checkEnrollmentInput(invalidInput7), false, "Multiple students with various multiple errors");
	equal(checkEnrollmentInput(invalidInput8), false, "Multiple students with various multiple errors");
	
	equal(checkEnrollmentInput(invalidInput9), false, "Single student, \t separator, no name");
	equal(checkEnrollmentInput(invalidInput10), false, "Single student, \t separator, no email");
	equal(checkEnrollmentInput(invalidInput11), false, "Single student, \t separator, all empty fields");
	equal(checkEnrollmentInput(invalidInput12), false, "Single student, \t separator, whitespace email");
	equal(checkEnrollmentInput(invalidInput13), false, "Single student, \t separator, whitespace name");
	
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


