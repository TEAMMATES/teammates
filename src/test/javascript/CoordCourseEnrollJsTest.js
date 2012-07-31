
module('coordCourseEnroll.js');

test('highlightError(start, end)', function(){
	// N/A, trivial function. This function highlights a section of text in a specific textarea element
	expect(0);
});


test('checkEnrollmentInput(input)', function(){
	//Retrieved from coordCourseEnroll.jsp. Required for adding an extra element in the test page for the test to work
	$("body").append('<textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea>');
	
	
	equal(checkEnrollmentInput(
			'Team 1\tTom Jacobs\ttom.jacobs@gmail.com'),
			true,
			'Valid input - Single student, tab separator, no comments');
	
	equal(checkEnrollmentInput(
			'Team 1\tTom Jacobs\ttom.jacobs@gmail.com\texchange student'),
			true,
			'Valid input - Single student, tab separator, comments included');
	
	equal(checkEnrollmentInput(
			'Team 1\tTom Jacobs\ttom.jacobs@gmail.com\tteammate 1\n' + 
			'Team 1\tBob\tbob@gmail.com\n' + 
			'Team 2\tAlice\talice@gmail.com\tanother team\n'),
			true,
			'Valid input - Multiple students, tab separator');
	
	equal(checkEnrollmentInput(
			'Team 1|Tom Jacobs|toom.jacobs@gmail.com'),
			true,
			'Valid input - Single student, | separator, no comments');
	
	equal(checkEnrollmentInput(
			'Team 1|Tom Jacobs|tom.jacobs@gmail.com|1 person only'),
			true,
			'Valid input - Single student, | separator, comments included');
	 
	equal(checkEnrollmentInput(
			'Team 1|Tom Jacobs|tom.jacobs@gmail.com|teammate 1\n' + 
			'Team 1|Bob|bob@gmail.com\n' + 
			'Team 2|Alice|alice@gmail.com|another team\n'),
			true,
			'Valid input - Multiple students, | separator');
	
	equal(checkEnrollmentInput(
			'\tTom Jacobs\ttom.jacobs@gmail.com'),
			true,
			'Valid input - Single student, \t separator, no team name');
	
	equal(checkEnrollmentInput('  \tTom Jacobs\ttom.jacobs@gmail.com'),
			true,
			'Valid input - Single student, \t separator, whitespace team name');
	
	equal(checkEnrollmentInput(
			'Team 1\t' + generateRandomString(STUDENTNAME_MAX_LENGTH) + '\ta@gmail.com'),
			true,
			'Valid input - Max length of name(40 characters)');
	
	equal(checkEnrollmentInput(
			generateRandomString(TEAMNAME_MAX_LENGTH) + '\tTom Jacobs\ttom.jacobs@gmail.com'),
			true,
			'Valid input - Max length of team name(24 characters)');
	
	
	
	equal(checkEnrollmentInput(
			'Team 1|tommy@gmail.com'),
			false,
			'Invalid input - Too little fields');
	
	equal(checkEnrollmentInput(
			'Team 1|Tommy|Tommy@gmail.com|extra field|extra field|extra field\n'),
			false,
			'Invalid input - Too many fields');
	
	equal(checkEnrollmentInput(
			'Team 1\t' + generateRandomString(STUDENTNAME_MAX_LENGTH + 1) + '\ttommy@gmail.com'),
			false,
			'Invalid input - exceed student name max length');

	equal(checkEnrollmentInput(
			'Team 1|Tommy|qwerty@invalidemail'),
			false,
			'Invalid input - invalid email');
	
	equal(checkEnrollmentInput(
			generateRandomString(TEAMNAME_MAX_LENGTH + 1) + '\tTommy\ttommy@gmail.com'),
			false,
			'Invalid input - exceed team name max length');

	equal(checkEnrollmentInput(
			'Team 1|Tommy|tommy@gmail.com|teammate 1\n' + 
			'Team 2|Bob|bob@gmail\n' + 
			'Team 2|Alice|alice@gmail.com|another team\n'),
			false,
			'Invalid input - student with invalid email');
	
	equal(checkEnrollmentInput(
			'Team 1|tommy|tommy@gmail.com|teammate 1\n' + 
			generateRandomString(TEAMNAME_MAX_LENGTH + 1) + '|Bob|bob@gmail.com\n' + 
			'Team 2|Alice|alice@gmail.com|another team\n' +
			'-|John|john@john.com|This guy has no team yet.'),
			false,
			'Invalid input - student exceeding max teamname length');
	
	equal(checkEnrollmentInput(
			'Team 1|Tommy|tommy@gmail.com|teammate 1\n' +
			'Team 1|Bob|bob@gmail.com\n' +
			'Team 2|' + generateRandomString(STUDENTNAME_MAX_LENGTH + 1) + '|test@yahoo.com\n'),
			false,
			'Invalid input - student exceeding max student name length');
	
	equal(checkEnrollmentInput(
			'Team 1\t\ttest@gmail.com'),
			false,
			'Invalid input - Single student, \t separator, no name');
	
	equal(checkEnrollmentInput(
			'Team 1\tAlice\t'),
			false,
			'Invalid input - Single student, \t separator, no email');
	
	equal(checkEnrollmentInput('\t\t'),
			false,
			'Invalid input - Single student, \t separator, all empty fields');
	
	equal(checkEnrollmentInput(
			'Team 1\tAlice\t  '),
			false,
			'Invalid input - Single student, \t separator, whitespace email');
	
	equal(checkEnrollmentInput(
			'Team 1\t  \tmax@gmail.com'),
			false,
			'Single student, \t separator, whitespace name');
	
	
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


