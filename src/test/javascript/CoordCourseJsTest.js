
module('coordCourse.js');

test('verifyAddCourse()', function(){
	// 'The method has only two paths and both are tested by UI tests
	expect(0);
});


test('courseStatusToMessage(statusCode)', function(){
	equal(courseStatusToMessage(COURSE_STATUS_EMPTY), DISPLAY_COURSE_MISSING_FIELD, "Course ID and Course Name are compulsory fields.");
	equal(courseStatusToMessage(COURSE_STATUS_LONG_ID), DISPLAY_COURSE_LONG_ID, "Course ID should not exceed XX characters.");
	equal(courseStatusToMessage(COURSE_STATUS_LONG_NAME), DISPLAY_COURSE_LONG_NAME, "Course name should not exceed XX characters.");
	equal(courseStatusToMessage(COURSE_STATUS_INVALID_ID), DISPLAY_COURSE_INVALID_ID, "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.");
	equal(courseStatusToMessage("default"), DISPLAY_INVALID_INPUT, "Unexpected error. Invallid input.");
});


test('checkAddCourseParam(courseID, courseName)', function(){
	equal(checkAddCourseParam("", "Software Engineering"), COURSE_STATUS_EMPTY, "Course Status Empty");
	equal(checkAddCourseParam("", ""), COURSE_STATUS_EMPTY, "Course Status Empty");
	equal(checkAddCourseParam("CS2103", ""), COURSE_STATUS_EMPTY, "Course Status Empty");
	
	equal(checkAddCourseParam("CS21032103210321032103210321032103", "Sofrware Engineering"), COURSE_STATUS_LONG_ID, "Course Status Long ID");
	equal(checkAddCourseParam("CS2103", "Software Engineering Module with a very long and tedious name"), COURSE_STATUS_LONG_NAME, "Course Status Long Name");
	
	equal(checkAddCourseParam("CS10@@", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID [CS10@@]");
	equal(checkAddCourseParam("CS100 ", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID [CS100 ](space)");
	equal(checkAddCourseParam("CS!010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID CS!010");
	equal(checkAddCourseParam("C@1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID C@1010");
	equal(checkAddCourseParam("#CS1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID #CS1010");
	equal(checkAddCourseParam("CS1010%", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID CS1010%");
	equal(checkAddCourseParam("C^^1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID C^^1010");
	equal(checkAddCourseParam("C&&1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID C&&1010");
	equal(checkAddCourseParam("*CS1101*", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID *CS1101*");
	equal(checkAddCourseParam("\"CS1010\"", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID \"CS1010\"");
	equal(checkAddCourseParam("''CS1010''", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID ''CS1010''");
	
	equal(checkAddCourseParam("CS.010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Valid Input");
	equal(checkAddCourseParam("CS-010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Valid Input");
	equal(checkAddCourseParam("CS_010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Valid Input");
	equal(checkAddCourseParam("CS$010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Valid Input");
});


test('isCourseIDValid(courseID)', function(){
	//Tested in checkAddCourseParam
	expect(0);
});



