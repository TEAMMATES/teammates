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
test('requestGetCourseList()', function() {
	xmlhttp = new MockHttpRequest();
	requestGetCourseList();
	equal(xmlhttp.readyState, 1, "Request State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"),
			"application/x-www-form-urlencoded;",
			"Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText, "operation=coordinator_getcourselist",
			"Request Data: operation=coordinator_addcourse");
});

test('handleGetCourseList()', function() {
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
	requestGetCourseList();
	xmlhttp.receive(400, null);
	equal(handleGetCourseList(), SERVERERROR, "HttpServletResponse: server error");

	xmlhttp = new MockHttpRequest();
	requestGetCourseList();
	xmlhttp.receive(200, response);
	deepEqual(handleGetCourseList(), result, "HttpServletResponse: return course list");
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