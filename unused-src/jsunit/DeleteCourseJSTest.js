module("Coordinator Delete Course");

//mocked server-side test
test('sendDeleteCourseRequest()', function() {
	var STATUS_OPEN = 1;
	xmlhttp = new MockHttpRequest();
	sendDeleteCourseRequest("CS1102");
	equal(xmlhttp.readyState, STATUS_OPEN, "Request State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"), 
			"application/x-www-form-urlencoded;",
			"Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText,
			"operation=coordinator_deletecourse&courseid=CS1102",
			"Request Data: operation=coordinator_deletecourse");
});

test('processDeleteCourseResponse()', function() {
	xmlhttp = new MockHttpRequest();
	sendDeleteCourseRequest("CS1102");
	xmlhttp.receive(CONNECTION_OK, "<status>course deleted</status>");
	equal(processDeleteCourseResponse(), COURSE_STATUS_DELETED, "HttpServletResponse: course deleted");

	xmlhttp = new MockHttpRequest();
	sendDeleteCourseRequest("CS1010");
	xmlhttp.receive(400, "<status></status>");//random status code
	equal(processDeleteCourseResponse(), COURSE_STATUS_SERVERERROR, "HttpServletResponse: server error");

	xmlhttp = new MockHttpRequest();
	sendDeleteCourseRequest("CS1010");
	xmlhttp.receive(CONNECTION_OK, "<status>course not deleted</status>");
	equal(processDeleteCourseResponse(), COURSE_STATUS_SERVERERROR, "HttpServletResponse: course not deleted");
})