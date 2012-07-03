module("Coordinator AddCourse");// addCourse()----------------------------------------------------------------


//helper function unit test
test('checkAddCourseParam(courseID, courseName)', function() {
	//valid
	equal(checkAddCourseParam("IS4226", "IT Outsourcing"), COURSE_STATUS_VALID_INPUT, "Valid Input: IS4226 IT Outsourcing");
	
	//missing field
	equal(checkAddCourseParam("", ""), COURSE_STATUS_EMPTY, "empty fields");
	equal(checkAddCourseParam("", "Software Engineering"), COURSE_STATUS_EMPTY, "empty id");
	equal(checkAddCourseParam("CS3215", ""), COURSE_STATUS_EMPTY, "empty name");
	
	//long input
	equal(checkAddCourseParam("CS101010101010101010101010", "Software Engineering"), COURSE_STATUS_LONG_ID, "too long courseID");
	equal(checkAddCourseParam("CS1010", "Software Engineering Software Engineering Software Engineering "), COURSE_STATUS_LONG_NAME, "too long courseName");

	//special char
	//valid: alphabets, numbers, dots, hyphens, underscores and dollars in course ID
	equal(checkAddCourseParam("CS.010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Invalid courseID CS$010");
	equal(checkAddCourseParam("CS-010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Invalid courseID CS$010");
	equal(checkAddCourseParam("CS_010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Invalid courseID CS$010");
	equal(checkAddCourseParam("CS$010", "Software Engineering"), COURSE_STATUS_VALID_INPUT, "Invalid courseID CS$010");
	//invalid
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
	
});

test('courseStatusToMessage(statusCode)', function() {
	
	equal(courseStatusToMessage(COURSE_STATUS_SERVERERROR), DISPLAY_SERVERERROR, "server error: " + DISPLAY_SERVERERROR);
	
	equal(courseStatusToMessage(COURSE_STATUS_SUCCESSFUL), DISPLAY_COURSE_ADDED, "course added: " + DISPLAY_COURSE_ADDED);
	
	equal(courseStatusToMessage(COURSE_STATUS_EXISTS), DISPLAY_COURSE_EXISTS, "course exists: " + DISPLAY_COURSE_EXISTS);
	
	equal(courseStatusToMessage(COURSE_STATUS_EMPTY), DISPLAY_COURSE_EMPTY, "empty field(s): " + DISPLAY_COURSE_EMPTY);
	
	equal(courseStatusToMessage(COURSE_STATUS_LONG_ID), DISPLAY_COURSE_LONG_ID, "courseID too long: " + DISPLAY_COURSE_LONG_ID);
	
	equal(courseStatusToMessage(COURSE_STATUS_LONG_NAME), DISPLAY_COURSE_LONG_NAME, "courseName too long: " + DISPLAY_COURSE_LONG_NAME);
	
	equal(courseStatusToMessage(COURSE_STATUS_INVALID_ID), DISPLAY_COURSE_INVALID_ID, "courseID invalid: " + DISPLAY_COURSE_INVALID_ID);
	
	equal(courseStatusToMessage("Unknown Status"), DISPLAY_SERVERERROR, "unknown status: " + DISPLAY_SERVERERROR);
	
});

//mocked server-side test
test('sendAddCourseRequest()', function() {
	var STATUS_OPEN = 1;
	xmlhttp = new MockHttpRequest();
	sendAddCourseRequest("CS1010", "Programming Methodology");
	equal(xmlhttp.readyState, STATUS_OPEN, "Request State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"), 
			"application/x-www-form-urlencoded;",
			"Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText,
			"operation=coordinator_addcourse&courseid=CS1010&coursename=Programming%20Methodology",
			"Request Data: operation=coordinator_addcourse");

});

test('processAddCourseResponse()', function() {
	xmlhttp = new MockHttpRequest();
	sendAddCourseRequest("CS1010", "Programming Methodology");
	xmlhttp.receive(CONNECTION_OK, "<status>course added</status>");
	equal(processAddCourseResponse(), COURSE_STATUS_SUCCESSFUL, "HttpServletResponse: course added");

	xmlhttp = new MockHttpRequest();
	sendAddCourseRequest("CS1010", "Programming Methodology");
	xmlhttp.receive(400, "<status>course added</status>");//random status code
	equal(processAddCourseResponse(), COURSE_STATUS_SERVERERROR, "HttpServletResponse: server error");

	xmlhttp = new MockHttpRequest();
	sendAddCourseRequest("CS1010", "Programming Methodology");
	xmlhttp.receive(CONNECTION_OK, "<status>course exists</status>");
	equal(processAddCourseResponse(), COURSE_STATUS_EXISTS, "HttpServletResponse: course exists");
})
