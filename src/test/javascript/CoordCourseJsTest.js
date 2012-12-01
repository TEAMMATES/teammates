
module('instructorCourse.js');

test('verifyAddCourse()', function(){
	// 'The method has only two paths and both are tested by UI tests
	expect(0);
});


test('courseStatusToMessage(statusCode)', function(){
	equal(courseStatusToMessage(COURSE_STATUS_EMPTY), DISPLAY_COURSE_MISSING_FIELD, "Course ID and Course Name are compulsory fields.");
	equal(courseStatusToMessage(COURSE_STATUS_LONG_ID), DISPLAY_COURSE_LONG_ID, "Course ID should not exceed XX characters.");
	equal(courseStatusToMessage(COURSE_STATUS_LONG_NAME), DISPLAY_COURSE_LONG_NAME, "Course name should not exceed XX characters.");
	equal(courseStatusToMessage(COURSE_STATUS_INVALID_ID), DISPLAY_COURSE_INVALID_ID, "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.");
	equal(courseStatusToMessage("default"), DISPLAY_INVALID_INPUT, "Unexpected error. Invalid input.");
});


test('checkAddCourseParam(courseID, courseName)', function(){
	equal(checkAddCourseParam("", "Software Engineering"), COURSE_STATUS_EMPTY, "Course Status Empty");
	equal(checkAddCourseParam("CS2103", ""), COURSE_STATUS_EMPTY, "Course Status Empty");	
	equal(checkAddCourseParam(generateRandomString(COURSE_ID_MAX_LENGTH + 1), "Sofrware Engineering"), COURSE_STATUS_LONG_ID, "Course Status Long ID");
	equal(checkAddCourseParam("CS2103", generateRandomString(COURSE_NAME_MAX_LENGTH + 1)), COURSE_STATUS_LONG_NAME, "Course Status Long Name");	
	
	equal(checkAddCourseParam("CS.010_-$", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Normal Valid Input");
	equal(checkAddCourseParam(generateRandomString(COURSE_ID_MAX_LENGTH), generateRandomString(COURSE_NAME_MAX_LENGTH)), COURSE_STATUS_VALID_INPUT, "Valid Input of maximum length");
	
	//to test isCourseIDValid. Easier to test here.
	equal(checkAddCourseParam("CS10@@", "Software Engineering"), COURSE_STATUS_INVALID_ID, "@ character");
	equal(checkAddCourseParam("CS100 ", "Software Engineering"), COURSE_STATUS_INVALID_ID, "whitespace character");
	equal(checkAddCourseParam("CS100!", "Software Engineering"), COURSE_STATUS_INVALID_ID, "! character");
	equal(checkAddCourseParam("CS100#", "Software Engineering"), COURSE_STATUS_INVALID_ID, "# character");
	equal(checkAddCourseParam("CS100%", "Software Engineering"), COURSE_STATUS_INVALID_ID, "% character");
	equal(checkAddCourseParam("CS100^", "Software Engineering"), COURSE_STATUS_INVALID_ID, "^ character");
	equal(checkAddCourseParam("CS100&", "Software Engineering"), COURSE_STATUS_INVALID_ID, "& character");
	equal(checkAddCourseParam("CS100*", "Software Engineering"), COURSE_STATUS_INVALID_ID, "* character");
	equal(checkAddCourseParam("CS100\"", "Software Engineering"), COURSE_STATUS_INVALID_ID, "\" character");
	equal(checkAddCourseParam("CS100'", "Software Engineering"), COURSE_STATUS_INVALID_ID, "' character");
		
});


test('isCourseIDValid(courseID)', function(){
	//tested in checkAddCourseParam
	expect(0);
});



