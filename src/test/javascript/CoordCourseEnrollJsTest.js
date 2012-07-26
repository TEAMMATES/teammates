
module('coordCourseEnroll.js');

test('highlightError(start, end)', function(){
	// N/A, trivial function. This function highlights a section of text in a specific textarea element
	expect(0);
});


test('checkEnrollmentInput(input)', function(){
	var input1 = 'Team 1\tGoh Chun Teck\tchunteck.88@gmail.com';
	var input2 = 'Team 1\tGoh Chun Teck\tchunteck.88@gmail.com\t1 person only';
	var input3 = 'Team 1\tGoh Chun Teck\tchunteck.88@gmail.com\tteammate 1\nTeam 1\tBob\tbob@gmail.com\nTeam 2\tAlice\talice@gmail.com\tanother team\n';
	
	var input4 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com';
	var input5 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|1 person only';
	var input6 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nTeam 1|Bob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\n';
	
	var input7 = 'Team 1|chunteck.88@gmail.com';
	var input8 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|extra field|extra field|extra field\n';
	var input9 = 'Team 1|G0h Chun Teck with a super duper long name|chunteck.88@gmail.com';
	var input10 = 'Team 1|Goh Chun Teck|qwerty@invalidemail';
	var input11 = 'This is a invalid teamname because its too long|Goh Chun Teck|chunteck.88@gmail.com';
	
	var input12 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nBob|bob@gmail.com\nTeam 2|Alice|alice@gmail.com|another team\n';
	var input13 = 'Team 1|Goh Chun Teck|chunteck.88@gmail.com|teammate 1\nSuper long team name more than 25 chars|Bob|bob@gmail.com\nTeam 2|Alice|alice|another team\n';
	var input14 = 'chunteck.88@gmail.com|teammate 1\nbob@gmail.com\nTeam 2|Alice\n';
	
	//Retrieved from coordCourseEnroll.jsp
	$("body").append('<textarea rows="6" cols="135" class ="textvalue" name="enrollstudents" id="enrollstudents"></textarea>');
	
	equal(checkEnrollmentInput(input1), true, "Single student, tab separator, valid");
	equal(checkEnrollmentInput(input2), true, "Single student, tab separator, comments included, valid");
	equal(checkEnrollmentInput(input3), true, "Multiple students, tab separator, valid");
	
	equal(checkEnrollmentInput(input4), true, "Single student, | separator, valid");
	equal(checkEnrollmentInput(input5), true, "Single student, | separator, comments included, valid");
	equal(checkEnrollmentInput(input6), true, "Multiple students, | separator, valid");
	
	equal(checkEnrollmentInput(input7), false, "Too little fields");
	equal(checkEnrollmentInput(input8), false, "Too many fields");
	equal(checkEnrollmentInput(input9), false, "Invalid student name");
	equal(checkEnrollmentInput(input10), false, "Invalid email");
	equal(checkEnrollmentInput(input11), false, "Invalid team name");
	equal(checkEnrollmentInput(input12), false, "Multiple students with various multiple errors");
	equal(checkEnrollmentInput(input13), false, "Multiple students with various multiple errors");
	equal(checkEnrollmentInput(input14), false, "Multiple students with various multiple errors");
});