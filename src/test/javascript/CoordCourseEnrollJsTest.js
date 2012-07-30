
module('coordCourseEnroll.js');

test('highlightError(start, end)', function(){
	// N/A, trivial function. This function highlights a section of text in a specific textarea element
	expect(0);
});


test('checkEnrollmentInput(input)', function(){
	//Retrieved from coordCourseEnroll.jsp. Required for adding an extra element in the test page for the test to work
	$("body").append('<textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea>');
	
	
	equal(checkEnrollmentInput('Team 1\tTom Jacobs\ttom.jacobs@gmail.com'),
			true,
			"Valid input - Single student, tab separator, no comments");
	
	equal(checkEnrollmentInput('Team 1\tTom Jacobs\ttom.jacobs@gmail.com\texchange student'),
			true,
			"Valid input - Single student, tab separator, comments included");
	
	equal(checkEnrollmentInput(
			'Team 1\tTom Jacobs\ttom.jacobs@gmail.com\tteammate 1\n' + 
			'Team 1\tBob\tbob@gmail.com\n' + 
			'Team 2\tAlice\talice@gmail.com\tanother team\n'),
			true,
			"Valid input - Multiple students, tab separator");
	
	equal(checkEnrollmentInput('Team 1|Tom Jacobs|toom.jacobs@gmail.com'),
			true,
			"Valid input - Single student, | separator, no comments");
	
	equal(checkEnrollmentInput('Team 1|Tom Jacobs|tom.jacobs@gmail.com|1 person only'),
			true,
			"Valid input - Single student, | separator, comments included");
	 
	equal(checkEnrollmentInput(
			'Team 1|Tom Jacobs|tom.jacobs@gmail.com|teammate 1\n' + 
			'Team 1|Bob|bob@gmail.com\n' + 
			'Team 2|Alice|alice@gmail.com|another team\n'),
			true,
			"Valid input - Multiple students, | separator");
	
	equal(checkEnrollmentInput('\tTom Jacobs\ttom.jacobs@gmail.com'),
			true,
			"Valid input - Single student, \t separator, no team name");
	
	//Valid input - Single student, tab separator, whitespace team name
	var validInput8 = '  \tGoh Chun Teck\tchunteck.88@gmail.com';
	equal(checkEnrollmentInput(validInput8),
			true,
			"Single student, \t separator, whitespace team name");
	
	//Valid input - Testing max length of name (max 40 chars)
	var validInput9 = 'Team 1\t' + generateRandomString(STUDENTNAME_MAX_LENGTH) + '\tchunteck.88@gmail.com';
	equal(checkEnrollmentInput(validInput9), true, "Testing max length of name(40 characters)");
	
	//Valid input - Testing max length of team name (max 24 chars)
	var validInput10 = generateRandomString(TEAMNAME_MAX_LENGTH) + '\tGoh Chun Teck\tchunteck.88@gmail.com';
	equal(checkEnrollmentInput(validInput10), true, "Testing max length of team name (24 characters)");
	
	
	
	//Invalid input - Single student, | separator, missing fields
	var invalidInput1 = 'Team 1|chunteck.88@gmail.com';
	equal(checkEnrollmentInput(invalidInput1), false, "Too little fields");
	
	//Invalid input - Single student, | separator, extra fields
	var invalidInput2 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|extra field|extra field|extra field\n';
	equal(checkEnrollmentInput(invalidInput2), false, "Too many fields");
	
	//Invalid input - Single student, tab separator, invalid student name
	var invalidInput3 = 'Team 1\t' + generateRandomString(STUDENTNAME_MAX_LENGTH + 1) + '\tchunteck.88@gmail.com';
	equal(checkEnrollmentInput(invalidInput3), false, "Invalid student name");
	
	//Invalid input - Single student, | separator, invalid email
	var invalidInput4 = 'Team 1|Goh Chun Teck|qwerty@invalidemail';
	equal(checkEnrollmentInput(invalidInput4), false, "Invalid email");
	
	//Invalid input - Single student, tab separator, invalid teamname
	var invalidInput5 = generateRandomString(TEAMNAME_MAX_LENGTH + 1) + '\tGoh Chun Teck\tchunteck.88@gmail.com';
	equal(checkEnrollmentInput(invalidInput5), false, "Invalid team name");
	
	//Invalid input - Multiple students, | separator, student with missing team
	var invalidInput6 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nBob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\n';
	equal(checkEnrollmentInput(invalidInput6), false, "Multiple students with various multiple errors");
	
	//Invalid input - Multiple students, | separator, student with invalid team name
	var invalidInput7 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nSuper long team name more than 25 chars|Bob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\nNo Team|John|john@john.com|This guy has no team.';
	equal(checkEnrollmentInput(invalidInput7), false, "Multiple students with various multiple errors");
	
	//Invalid input - Multiple students, student with student with invalid name, missing email
	var invalidInput8 = 'Team 1|Teck|chunteck.88@gmail.com|teammate 1\nTeam 1|Bob|bob@gmail.com\nTeam 2|Alice has a very long name more than 40 chars\n';
	equal(checkEnrollmentInput(invalidInput8), false, "Multiple students with various multiple errors");
	
	//Invalid input - Single student, tab separator, empty name
	var invalidInput9 = 'Team 1\t\tchunteck.88@gmail.com';
	equal(checkEnrollmentInput(invalidInput9), false, "Single student, \t separator, no name");
	
	//Invalid input - Single student, tab separator, empty email
	var invalidInput10 = 'Team 1\tGoh Chun Teck\t';
	equal(checkEnrollmentInput(invalidInput10), false, "Single student, \t separator, no email");
	
	//Invalid input - Single student, tab separator, totally empty
	var invalidInput11 = '\t\t';
	equal(checkEnrollmentInput(invalidInput11), false, "Single student, \t separator, all empty fields");
	
	//Invalid input - Single student, tab separator, whitespace email
	var invalidInput12 = 'Team 1\tGoh Chun Teck\t  ';
	equal(checkEnrollmentInput(invalidInput12), false, "Single student, \t separator, whitespace email");
	
	//Invalid input - Single student, tab separator, whitespace name
	var invalidInput13 = 'Team 1\t  \tchunteck.88@gmail.com';
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


