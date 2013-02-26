
module('instructorCourse.js');

test('verifyCourseData()', function(){
	// 'The method has only two paths and both are tested by UI tests
	expect(0);
});


test('courseStatusToMessage(statusCode)', function(){
	equal(courseStatusToMessage(COURSE_STATUS_EMPTY), DISPLAY_COURSE_MISSING_FIELD, "Course ID and Course Name are compulsory fields.");
	equal(courseStatusToMessage(COURSE_STATUS_LONG_ID), DISPLAY_COURSE_LONG_ID, "Course ID should not exceed XX characters.");
	equal(courseStatusToMessage(COURSE_STATUS_LONG_NAME), DISPLAY_COURSE_LONG_NAME, "Course name should not exceed XX characters.");
	equal(courseStatusToMessage(COURSE_STATUS_INVALID_ID), DISPLAY_COURSE_INVALID_ID, "Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs in course ID.");
	equal(courseStatusToMessage("default"), DISPLAY_INVALID_INPUT, "Unexpected error. Invalid input.");
});


test('checkAddCourseParam(courseID, courseName, instructorList)', function(){
	equal(checkAddCourseParam("", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_EMPTY, "Course Status Empty");
	equal(checkAddCourseParam("CS2103", "", "googid\tInstructor1\tI1@gmail.com\ngoogid2\tInstructor2\tI2@gmail.com\ngoogid3\tInstructor3\tI3@gmail.com"), COURSE_STATUS_EMPTY, "Course Status Empty");	
	equal(checkAddCourseParam(generateRandomString(COURSE_ID_MAX_LENGTH + 1), "Sofrware Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_LONG_ID, "Course Status Long ID");
	equal(checkAddCourseParam("CS2103", generateRandomString(COURSE_NAME_MAX_LENGTH + 1), "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_LONG_NAME, "Course Status Long Name");	
	equal(checkAddCourseParam("CS2103", "Software Engineering", ""), COURSE_STATUS_INSTRUCTOR_LIST_EMPTY, "Course Instructor List Empty");
	equal(checkAddCourseParam("CS.010_-$", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_VALID_INPUT, "Normal Valid Input");
	equal(checkAddCourseParam(generateRandomString(COURSE_ID_MAX_LENGTH), generateRandomString(COURSE_NAME_MAX_LENGTH), "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_VALID_INPUT, "Valid Input of maximum length");
	
	//to test isCourseIDValid. Easier to test here.
	equal(checkAddCourseParam("CS10@@", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "@ character");
	equal(checkAddCourseParam("CS100 ", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "whitespace character");
	equal(checkAddCourseParam("CS100!", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "! character");
	equal(checkAddCourseParam("CS100#", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "# character");
	equal(checkAddCourseParam("CS100%", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "% character");
	equal(checkAddCourseParam("CS100^", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "^ character");
	equal(checkAddCourseParam("CS100&", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "& character");
	equal(checkAddCourseParam("CS100*", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "* character");
	equal(checkAddCourseParam("CS100\"", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "\" character");
	equal(checkAddCourseParam("CS100'", "Software Engineering", "googid|Instructor1|I1@gmail.com"), COURSE_STATUS_INVALID_ID, "' character");
		

	equal(checkAddCourseParam("CS100", "Software Engineering", "power.ful-1988@GMmail.COM|Instructor1|I1@gmail.com"), COURSE_STATUS_VALID_INPUT, "Normal Valid Input");
	equal(checkAddCourseParam("CS100", "Software Engineering", " user@hotmail.com |Instructor1|I1@gmail.com"), COURSE_STATUS_VALID_INPUT, "Normal Valid Input");
	equal(checkAddCourseParam("CS100", "Software Engineering", "googid\n\t  @gmail.com \n\t |Instructor1|I1@gmail.com"), COURSE_STATUS_INSTRUCTOR_LIST_FIELDS_MISSING, "Space character");
	equal(checkAddCourseParam("CS100", "Software Engineering", "googid\t  @gmail.com \t |Instructor1|I1@gmail.com"), COURSE_STATUS_INSTRUCTOR_LIST_FIELDS_EXTRA, "Tab character");
	equal(checkAddCourseParam("CS100", "Software Engineering", "googid!@#$%^&*()|Instructor1|I1@gmail.com"), COURSE_STATUS_INSTRUCTOR_LIST_GOOGLEID_INVALID, "!@#$%^&*() character");
});


test('isCourseIDValid(courseID)', function(){
	//tested in checkAddCourseParam
	expect(0);
});



