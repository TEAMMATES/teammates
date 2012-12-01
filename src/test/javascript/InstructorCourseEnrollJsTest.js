
module('instructorCourseEnroll.js');

test('highlightError(start, end)', function(){
	// N/A, trivial function. This function highlights a section of text in a specific textarea element
	expect(0);
});


test('checkEnrollmentInput(input)', function(){
	//This textarea element is taken from instructorCourseEnroll.jsp. Required for
	//the test to work, since the function being tested relies on this element
	$("body").append('<textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea>');
	
	equal(checkEnrollmentInput(
	''),
	true,
	'Null line');
	
	equal(checkEnrollmentInput(
		'Team 1|Tom Jacobs|tom.jacobs@gmail.com'),
		true,
		'Typical single valid line');
	
	equal(checkEnrollmentInput( 
		'Team   1|Tom Jacobs|tom.jacobs@gmail.com\n' + 
		'Team2|Bob  |  bob@gmail.com\n' + 
		'  Team 3|  Alice|alice@gmail.com  \n' + 
		'Team4   |Jack|jack@gmail.com  \n' + 
		'   |Jill|  jill@gmail.com'),
		true,
		'| separator, whitespaces in the fields');
	
	equal(checkEnrollmentInput(
		'\n' +
		'Team 1\tTom Jacobs\ttom.jacobs@gmail.com\tteammate 1\n' + 
		'Team 1\tBob\tbob@gmail.com\t    \n' +
		'\n' +
		'\n' +
		'Team 2\tAlice\talice@gmail.com\t\n' + 
		'Team 2\tJack\tjack@gmail.com\n'),
		true,
		'\t separator, presence/absence of comments, blank lines');
	
	equal(checkEnrollmentInput(
		'|1|tom.jacobs@gmail.com\tteammate 1\n' + 
		generateRandomString(TEAMNAME_MAX_LENGTH) + '\t' + generateRandomString(STUDENTNAME_MAX_LENGTH) + '\tbob@gmail.com|    \n'),
		true,
		'mixture of | and \t separators, max/min length of fields');

	equal(checkEnrollmentInput(
		'Team1|Tom|Tom@gmail.com|extra field|extra field\n'),
		false,
		'extra fields');

	equal(checkEnrollmentInput(
		'Tom\n'),
		false,
		'too few fields');
		
	equal(checkEnrollmentInput(
		'Team 1|' + generateRandomString(STUDENTNAME_MAX_LENGTH + 1) + '|tommy@gmail.com'),
		false,
		'invalid student name');

	equal(checkEnrollmentInput(
		'Team 1|Bobby|bob@yahoo.com\n' + 
		generateRandomString(TEAMNAME_MAX_LENGTH + 1) + '|Tommy|tommy@gmail.com'),
		false,
		'invalid team name');

	equal(checkEnrollmentInput(
		'Team 1|Bob|bobby@gmail.com' + 
		'Team 1|Tommy|qwerty@invalidemail' + 
		'Team 2|Alice|alice@gmail.com'),
		false,
		'invalid email');

});

