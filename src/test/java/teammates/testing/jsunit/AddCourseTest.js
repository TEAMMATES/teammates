module("Coordinator AddCourse");// addCourse()----------------------------------------------------------------

//main function test
test('doAddCourse(): client-side validation', function() {
	xmlhttp = new MockHttpRequest();
	// empty fields failed
	equal(doAddCourse("", ""), DISPLAY_COURSE_EMPTY, "doAddCourse(\"\", \"\")");

	// long courseID
	equal(doAddCourse("C *&1010&@", "Programming Methodology"), DISPLAY_COURSE_INVALIDID, "doAddCourse(\"C *&1010&@\", \"Programming Methodology\")");
	
	// invalid courseID
	equal(doAddCourse("CS10$$101101010010101010110101010", "Software Engineering"), DISPLAY_COURSE_LONGID, "doAddCourse(\"CS10$$101101010010101010110101010\", \"Software Engineering\")");

	// long courseName
	equal(doAddCourse("CS1010", "A LONG LONG COURSE NAME MAY CAUSE PROBLEM."), DISPLAY_COURSE_LONGNAME, "doAddCourse(\"CS1010\", \"A LONG LONG COURSE NAME MAY CAUSE PROBLEM.\")");

});

//helper function test
test('checkAddCourseParam(courseID, courseName)', function() {
	//valid
	equal(checkAddCourseParam("IS4226", "IT Outsourcing"), COURSE_STATUS_WAITING, "Valid Input: IS4226 IT Outsourcing");
	
	//missing field
	equal(checkAddCourseParam("", ""), COURSE_STATUS_EMPTY, "empty fields");
	equal(checkAddCourseParam("", "Software Engineering"), COURSE_STATUS_EMPTY, "empty id");
	equal(checkAddCourseParam("CS3215", ""), COURSE_STATUS_EMPTY, "empty name");
	
	//long input
	equal(checkAddCourseParam("CS101010101010101010101010", "Software Engineering"), COURSE_STATUS_LONG_ID, "too long courseID");
	equal(checkAddCourseParam("CS1010", "Software Engineering Software Engineering Software Engineering "), COURSE_STATUS_LONG_NAME, "too long courseName");

	//special char
	equal(checkAddCourseParam("CS10@@", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID [CS10@@]");
	equal(checkAddCourseParam("CS100 ", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID [CS100 ](space)");
	equal(checkAddCourseParam("CS!010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID CS!010");
	equal(checkAddCourseParam("C@1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID C@1010");
	equal(checkAddCourseParam("#CS1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID #CS1010");
	equal(checkAddCourseParam("CS$010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID CS$010");
	equal(checkAddCourseParam("CS1010%", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID CS1010%");
	equal(checkAddCourseParam("C^^1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID C^^1010");
	equal(checkAddCourseParam("C&&1010", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID C&&1010");
	equal(checkAddCourseParam("*CS1101*", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID *CS1101*");
	equal(checkAddCourseParam("\"CS1010\"", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID \"CS1010\"");
	equal(checkAddCourseParam("''CS1010''", "Software Engineering"), COURSE_STATUS_INVALID_ID, "Invalid courseID ''CS1010''");
	
});

test('courseStatusToMessage(statusCode)', function() {
	
	equal(courseStatusToMessage(COURSE_STATUS_SERVERERROR), DISPLAY_SERVERERROR, "server error: " + DISPLAY_SERVERERROR);
	
	equal(courseStatusToMessage(COURSE_STATUS_SUCCESSFUL), DISPLAY_COURSE_ADDED, "course added: " + DISPLAY_COURSE_ADDED);
	
	equal(courseStatusToMessage(COURSE_STATUS_EXISTS), DISPLAY_COURSE_EXISTS, "course exists: " + DISPLAY_COURSE_EXISTS);
	
	equal(courseStatusToMessage(COURSE_STATUS_EMPTY), DISPLAY_COURSE_EMPTY, "empty field(s): " + DISPLAY_COURSE_EMPTY);
	
	equal(courseStatusToMessage(COURSE_STATUS_LONG_ID), DISPLAY_COURSE_LONGID, "courseID too long: " + DISPLAY_COURSE_LONGID);
	
	equal(courseStatusToMessage(COURSE_STATUS_LONG_NAME), DISPLAY_COURSE_LONGNAME, "courseName too long: " + DISPLAY_COURSE_LONGNAME);
	
	equal(courseStatusToMessage(COURSE_STATUS_INVALID_ID), DISPLAY_COURSE_INVALIDID, "courseID invalid: " + DISPLAY_COURSE_INVALIDID);
	
	equal(courseStatusToMessage(COURSE_STATUS_INVALID_NAME), DISPLAY_COURSE_INVALIDNAME, "courseName invalid: " + DISPLAY_COURSE_INVALIDNAME);
	
});

//mocked server-side test
test('requestAddCourse()', function() {
	xmlhttp = new MockHttpRequest();
	requestAddCourse("CS1010", "Programming Methodology");
	equal(xmlhttp.readyState, 1, "Request State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"), 
			"application/x-www-form-urlencoded;",
			"Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText,
			"operation=coordinator_addcourse&courseid=CS1010&coursename=Programming%20Methodology",
			"Request Data: operation=coordinator_addcourse");

});

test('handleAddCourse()', function() {
	xmlhttp = new MockHttpRequest();
	requestAddCourse("CS1010", "Programming Methodology");
	xmlhttp.receive(200, "<status>course added</status>");
	equal(handleAddCourse(), COURSE_STATUS_SUCCESSFUL, "HttpServletResponse: course added");

	xmlhttp = new MockHttpRequest();
	requestAddCourse("CS1010", "Programming Methodology");
	xmlhttp.receive(400, "<status>course added</status>");
	equal(handleAddCourse(), COURSE_STATUS_SERVERERROR, "HttpServletResponse: server error");

	xmlhttp = new MockHttpRequest();
	requestAddCourse("CS1010", "Programming Methodology");
	xmlhttp.receive(200, "<status>course exists</status>");
	equal(handleAddCourse(), COURSE_STATUS_EXISTS, "HttpServletResponse: course exists");
})
