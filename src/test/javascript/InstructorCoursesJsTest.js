
module('instructorCourse.js');

/*Explanation: This is a dummy function to create an outline view in Eclipse */
function testVerifyCourseData(){}; 

test('verifyCourseData()', function(){
	// 'The method has only two paths and both are tested by UI tests
	expect(0);
});

function testIsCourseIDValidChars(){}; 
test('isCourseIDValidChars(courseId)', function(){
	
	//valid cases
	equal(isCourseIDValidChars("_$course1.2-3"), 
			true, 
			"Course Id valid");
	
	//invalid cases
	equal(isCourseIDValidChars("CS10@@"), 
			false, 
			"Course Id has @ character");
	equal(isCourseIDValidChars("CS100!"), 
			false, 
			"Course Id has ! character");
	equal(isCourseIDValidChars("CS100#"), 
			false, 
			"Course Id has # character");
	equal(isCourseIDValidChars("CS100%"), 
			false, 
			"Course Id has % character");
	equal(isCourseIDValidChars("CS100^"), 
			false, 
			"Course Id has ^ character");
	equal(isCourseIDValidChars("CS100&"), 
			false, 
			"Course Id has & character");
	equal(isCourseIDValidChars("CS100*"), 
			false, 
			"Course Id has * character");
	equal(isCourseIDValidChars("CS100\""), 
			false, 
			"Course Id has \" character");
	equal(isCourseIDValidChars("CS100'"), 
			false, 
			"Course Id has ' character");
});

function testGetCourseIdInvalidityInfo(){}; 

test('getCourseIdInvalidityInfo(courseId)', function(){
	
	//valid cases
	equal(getCourseIdInvalidityInfo(generateRandomString(COURSE_ID_MAX_LENGTH - 1)+"$   "), 
			"", 
			"Max-length course ID containing special characters and with extra whitespace padding");
	
	//invalid cases
	equal(getCourseIdInvalidityInfo(""), 
			DISPLAY_COURSE_COURSE_ID_EMPTY+"<br>", 
			"Course Id empty");
	equal(getCourseIdInvalidityInfo("   "), 
			DISPLAY_COURSE_COURSE_ID_EMPTY+"<br>", 
			"Course Id only white spaces");
	equal(getCourseIdInvalidityInfo("course*1"), 
			DISPLAY_COURSE_INVALID_ID+"<br>", 
			"Course Id only white spaces");
	equal(getCourseIdInvalidityInfo(generateRandomString(COURSE_ID_MAX_LENGTH + 1)), 
			DISPLAY_COURSE_LONG_ID+"<br>", 
			"Course Id too long");
});

function testDoesInstructorListIncludesLoggedInUser(){}; 

test('doesInstructorListIncludesLoggedInUser(instructorId, instructorList)', function(){
	
	equal(doesInstructorListIncludesLoggedInUser("googleId", "googleId|Name|email@com.sg"), 
			true, 
			"included, only one instructor in list");
	
	equal(doesInstructorListIncludesLoggedInUser("googleId", "googleId1|Name|email@com.sg \n googleId  |Name|email@com.sg \n googleId3|Name|email@com.sg"), 
			true, 
			"included, multiple instructors in list");
	
	equal(doesInstructorListIncludesLoggedInUser("googleId", "googleId1|Name|email@com.sg \n googleId3|Name|email@com.sg"), 
			false, 
			"not included, multiple instructors in list with almost similar IDs");
	
	equal(doesInstructorListIncludesLoggedInUser("googleId", "abc|googleId|email@com.sg "), 
			false, 
			"not included, one instructor, name matches the ID");
	
});

function testGetCourseNameInvalidityInfo(){}; 

test('getCourseNameInvalidityInfo(courseId)', function(){
	
	//valid cases
	equal(getCourseNameInvalidityInfo(generateRandomString("   "+COURSE_NAME_MAX_LENGTH - 2)+"$*   "), 
			"", 
			"Max-length course name containing special characters and with extra whitespace padding");
	
	//invalid cases
	equal(getCourseNameInvalidityInfo("   "), 
			DISPLAY_COURSE_COURSE_NAME_EMPTY + "<br>", 
			"Course name empty");
	equal(getCourseNameInvalidityInfo(generateRandomString(COURSE_NAME_MAX_LENGTH + 1)), 
			DISPLAY_COURSE_LONG_NAME + "<br>", 
			"Course name too long");
});

function testGetInstructorLineInvalidityInfo(){}; 

test('getInstructorLineInvalidityInfo(instructorLine)', function(){
	
	//valid cases
	equal(getInstructorLineInvalidityInfo("googid|Instructor1|I1@gmail.com"), 
			"", 
			"valid line with no spaces");
	equal(getInstructorLineInvalidityInfo("  googid  |  Instructor 1  |  I1@gmail.com  "), 
			"", 
			"valid line  with extra white space");
	equal(getInstructorLineInvalidityInfo("   "), 
			"", 
			"empty line, should be ignored");
	
	//invalid cases
	incorrectLine = "googid|Instructor1";
	equal(getInstructorLineInvalidityInfo(incorrectLine), 
			DISPLAY_INPUT_FIELDS_MISSING + " Incorrect line : "+ incorrectLine + "<br>", 
			"not enough fields");
	
	incorrectLine = "googid|Instructor1|i1@gmail.com|extra";
	equal(getInstructorLineInvalidityInfo(incorrectLine), 
			DISPLAY_INPUT_FIELDS_EXTRA + " Incorrect line : "+ incorrectLine + "<br>", 
			"too many fields");
	
	incorrectLine = "incorrect google id|Instructor1|i1@gmail.com";
	equal(getInstructorLineInvalidityInfo(incorrectLine), 
			DISPLAY_GOOGLEID_INVALID + " Incorrect line : "+ incorrectLine + "<br>", 
			"incorrect google id");
	
	incorrectLine = "googid|"+generateRandomString(NAME_MAX_LENGTH+1)+"|i1@gmail.com";
	equal(getInstructorLineInvalidityInfo(incorrectLine), 
			DISPLAY_NAME_INVALID + " Incorrect line : "+ incorrectLine + "<br>", 
			"incorrect name");

	incorrectLine = "googid|Instructor1|invalid.email";
	equal(getInstructorLineInvalidityInfo(incorrectLine), 
			DISPLAY_EMAIL_INVALID + " Incorrect line : "+ incorrectLine + "<br>", 
			"invalid email");

});

function testGetInstructorListInvalidityInfo(){}; 

test('getInstructorListInvalidityInfo(instructorLine)', function(){
	
	//valid cases
	equal(getInstructorListInvalidityInfo("googid|Instructor1|I1@gmail.com"), 
			"", 
			"one valid line with no spaces");
	
	equal(getInstructorListInvalidityInfo("  googid|Instructor1|I1@gmail.com   \n  \n googid2|  Instructor  2|I1@gmail.com\n"), 
			"", 
			"two valid lines with spaces and blank lines");
	
	//invalid cases
	incorrectList = "invalid google id|Instructor1|I1@gmail.com\n" +
		"  googid|Instructor1|I1@gmail.com   \n  \n" +
		"  googid|Instructor1|invalid.email   \n" +
		" googid2|  Instructor  2|I1@gmail.com\n";
	expectedError = DISPLAY_GOOGLEID_INVALID + " Incorrect line : invalid google id|Instructor1|I1@gmail.com<br>"+
		DISPLAY_EMAIL_INVALID + " Incorrect line : googid|Instructor1|invalid.email<br>";
		
	equal(getInstructorListInvalidityInfo(incorrectList), 
			expectedError, 
			"multiple valid lines and multiple invlid lines");

});

function testCheckAddCourseParam(){}; 

test('checkAddCourseParam(courseID, courseName, instructorList)', function(){

	equal(checkAddCourseParam("valid.course.id", "Software Engineering", "googid|Instructor1|I1@gmail.com \n googid2|Instructor2|I2@gmail.com"), 
			"", 
			"All valid values");
	
	equal(checkAddCourseParam("", "Software Engineering", "googid|Instructor1|I1@gmail.com"), 
			DISPLAY_COURSE_COURSE_ID_EMPTY+"<br>", 
			"Course ID empty");
		
	equal(checkAddCourseParam("valid.course-id", generateRandomString(COURSE_NAME_MAX_LENGTH + 1), "googid|Instructor1|I1@gmail.com"), 
			DISPLAY_COURSE_LONG_NAME + "<br>", 
			"Course name too long");
	
	equal(checkAddCourseParam("valid.course.id", "Software Engineering", ""), 
			DISPLAY_COURSE_INSTRUCTOR_LIST_EMPTY+"<br>", 
			"Instructor list empty");
	
	equal(checkAddCourseParam("", "", ""), 
			DISPLAY_COURSE_COURSE_ID_EMPTY + "<br>" 
			+ DISPLAY_COURSE_COURSE_NAME_EMPTY + "<br>"
			+ DISPLAY_COURSE_INSTRUCTOR_LIST_EMPTY + "<br>",
			"all three values are invalid");
	
	equal(checkAddCourseParam("invalid course id", generateRandomString(COURSE_NAME_MAX_LENGTH + 1), "googid|Instructor1|"), 
			DISPLAY_COURSE_INVALID_ID + "<br>" 
			+ DISPLAY_COURSE_LONG_NAME + "<br>"
			+ DISPLAY_EMAIL_INVALID + " Incorrect line : googid|Instructor1|"+"<br>",
			"all three values are invalid");

});



