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

module("Coordinator View CourseList");

// integrated function test

// unit function test
test('sortByID', function() {
	var courselist = [ {
		"ID" : "BB1102",
		"name" : "Data Structure",
		"numberOfTeams" : "12",
		"totalStudents" : "50",
		"unregistered" : "2",
		"status" : "false"
	}, {
		"ID" : "AA1101",
		"name" : "Programming Methodology",
		"numberOfTeams" : "10",
		"totalStudents" : "40",
		"unregistered" : "2",
		"status" : "false"
	}, {
		"ID" : "CC2103",
		"name" : "Software Engineering",
		"numberOfTeams" : "3",
		"totalStudents" : "10",
		"unregistered" : "2",
		"status" : "false"
	} ];

	var expected = [ {
		"ID" : "AA1101",
		"name" : "Programming Methodology",
		"numberOfTeams" : "10",
		"totalStudents" : "40",
		"unregistered" : "2",
		"status" : "false"
	},

	{
		"ID" : "BB1102",
		"name" : "Data Structure",
		"numberOfTeams" : "12",
		"totalStudents" : "50",
		"unregistered" : "2",
		"status" : "false"
	},

	{
		"ID" : "CC2103",
		"name" : "Software Engineering",
		"numberOfTeams" : "3",
		"totalStudents" : "10",
		"unregistered" : "2",
		"status" : "false"
	} ];

	deepEqual(courselist.sort(sortByID), expected, "Course List: Sort by CourseID");
});

test('sortByName', function() {
	var courselist = [ {
		"ID" : "CC2103",
		"name" : "Software Engineering",
		"numberOfTeams" : "3",
		"totalStudents" : "10",
		"unregistered" : "2",
		"status" : "false"
	},

	{
		"ID" : "BB1102",
		"name" : "Data Structure",
		"numberOfTeams" : "12",
		"totalStudents" : "50",
		"unregistered" : "2",
		"status" : "false"
	},

	{
		"ID" : "AA1101",
		"name" : "Programming Methodology",
		"numberOfTeams" : "10",
		"totalStudents" : "40",
		"unregistered" : "2",
		"status" : "false"
	}

	];

	var expected = [ {
		"ID" : "BB1102",
		"name" : "Data Structure",
		"numberOfTeams" : "12",
		"totalStudents" : "50",
		"unregistered" : "2",
		"status" : "false"
	}, {
		"ID" : "AA1101",
		"name" : "Programming Methodology",
		"numberOfTeams" : "10",
		"totalStudents" : "40",
		"unregistered" : "2",
		"status" : "false"
	},

	{
		"ID" : "CC2103",
		"name" : "Software Engineering",
		"numberOfTeams" : "3",
		"totalStudents" : "10",
		"unregistered" : "2",
		"status" : "false"
	} ];

	deepEqual(courselist.sort(sortByName), expected, "Course List: Sort By Course Name");
});

// mocked server-side test
test('sendGetCourseListRequest()', function() {
	xmlhttp = new MockHttpRequest();
	sendGetCourseListRequest();
	equal(xmlhttp.readyState, 1, "Request State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"),
			"application/x-www-form-urlencoded;",
			"Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText, "operation=coordinator_getcourselist",
			"Request Data: operation=coordinator_addcourse");
});

test('processGetCourseListResponse()', function() {
	var response = "<courses>" + "<coursesummary>"
			+ "<courseid><![CDATA[CS1010]]></courseid>"
			+ "<coursename><![CDATA[HELLO WORLD]]></coursename>"
			+ "<coursestatus>false</coursestatus>"
			+ "<coursenumberofteams>3</coursenumberofteams>"
			+ "<coursetotalstudents>10</coursetotalstudents>"
			+ "<courseunregistered>2</courseunregistered>" + "</coursesummary>"
			+ "</courses>";

	var result = [ {
		"ID" : "CS1010",
		"name" : "HELLO WORLD",
		"numberOfTeams" : "3",
		"totalStudents" : "10",
		"unregistered" : "2",
		"status" : "false"
	} ];

	xmlhttp = new MockHttpRequest();
	sendGetCourseListRequest();
	xmlhttp.receive(400, null);//random status code
	equal(processGetCourseListResponse(), COURSE_STATUS_SERVERERROR, "HttpServletResponse: server error");

	xmlhttp = new MockHttpRequest();
	sendGetCourseListRequest();
	xmlhttp.receive(CONNECTION_OK, response);
	deepEqual(processGetCourseListResponse(), result, "HttpServletResponse: return course list");
});

function makeCourseData(courseid, coursename, status, teams, total, unregsitered) {
	var coursesummary = 
		"<coursesummary>" +
			"<courseid><![CDATA[" + courseid + "]]></courseid>" +
			"<coursename><![CDATA[" + coursename + "]]></coursename>" +
			"<coursestatus>" + status + "</coursestatus>" +
			"<coursenumberofteams>" + teams + "</coursenumberofteams>" +
			"<coursetotalstudents>" + total + "</coursetotalstudents>" +
			"<courseunregistered>" + unresgitered + "</courseunregistered>" +
		"</coursesummary>";

	return coursesummary;
}
