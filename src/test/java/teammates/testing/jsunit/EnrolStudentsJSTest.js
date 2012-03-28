module("Coordinator EnrolStudents");// EnrolStudents()----------------------------------------------------------------

//NOTE: all '|' characters have been replaced by '\t' before this function
test('isEnrollmentInputValid(input)', function() {
	//valid input	
	equal(isEnrollmentInputValid("TeamTeam A \tAlice\talice.tmms@gmail.com"), true, "One valid student");
	equal(isEnrollmentInputValid(
		"TeamTeam A\tAlice\talice.tmms@gmail.com\t\n"+
		"Team A\tBenny\tbenny.tmms@gmail.com\n"+
		"Team A\tCharlie\tcharlie.tmms@gmail.com\t sample comments \n" +
		"Team B\tDanny\tdanny.tmms@gmail.com\n" +
		"\n" +
		"\n" +
		"Team B\tHuy\thuy@nus.edu.sg\n" +
		"Team B\tJeannie\tteammates.coord@gmail.com"), true, "Many valid students with blank lines");


	//empty input
	equal(isEnrollmentInputValid(""), true, "Empty input");
	//invalid inputs
	equal(isEnrollmentInputValid("Team A \t student name \t verylonglonglonglonglonglonglonglongemailaddress@domail.com "), 
		false, "Long email address input");
	equal(isEnrollmentInputValid("Long team name with more than 24 chars \t student name \t emailadress"),
	 	false, "Long team name");
	equal(isEnrollmentInputValid("Team A \t This is a very long student name with more than 40 chars\t emailadress"), 
		false, "Invalid email address input");
	equal(isEnrollmentInputValid("Team A \t student\\ name \t emailadress"),
		false, "Student name contains invalid character \\\\");
	equal(isEnrollmentInputValid("Team A \t student 'name \t emailadress"),
		false, "Invalid email address input character \'");
	equal(isEnrollmentInputValid("Team A \t student \"name \t emailadress"),
		false, "Invalid email address input character \"");
	equal(isEnrollmentInputValid("Team A \t  \t emailadress"),
		false, "Empty student name");
	equal(isEnrollmentInputValid("\t student name \t emailadress"), 
		false, "Empty team name");
	equal(isEnrollmentInputValid("Team A \t student name \t"), 
		false, "Empty emailadress");
	equal(isEnrollmentInputValid("Team A \t student name \t comments \t extra field"), 
		false, "More than 4 fields");
});

//mocked server-side test
test('sendEnrolStudentsRequest(input,courseid)', function() {
	var STATUS_OPEN = 1;
	xmlhttp = new MockHttpRequest();
	sendEnrolStudentsRequest("TeamTeam A \tAlice\talice.tmms@gmail.com","CS2103");
	equal(xmlhttp.readyState, STATUS_OPEN, "Request State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"), 
			"application/x-www-form-urlencoded;",
			"Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText,
			"operation=coordinator_enrolstudents&information=TeamTeam%20A%20%09Alice%09alice.tmms%40gmail.com&courseid=CS2103",
			"Request Data: operation=coordinator_enrolstudents");

});
test('processEnrolStudentsResponse()', function() {
	xmlhttp = new MockHttpRequest();
	sendEnrolStudentsRequest("TeamTeam A \tAlice\talice.tmms@gmail.com","CS2103");
	var response = 	'<enrollmentreports>'+
						'<enrollmentreport>'+
							'<name><![CDATA[Alice]]></name>'+
							'<email><![CDATA[alice.tmms@gmail.com]]></email>'+
							'<status><![CDATA[EDITED]]></status>'+
							'<nameedited>false</nameedited>'+
							'<teamnameedited>true</teamnameedited>'+
							'<commentsedited>false</commentsedited>'+
						'</enrollmentreport>'+
					'</enrollmentreports>';
	var expectedResult = 
		[
			{
			    "studentName": "Alice",
			    "studentEmail": "alice.tmms@gmail.com",
			    "nameEdited": "false",
			    "teamNameEdited": "true",
			    "commentsEdited": "false",
			    "status": "EDITED"
		  	}
		];
	xmlhttp.receive(CONNECTION_OK,response);
	deepEqual(processEnrolStudentsResponse(), expectedResult, "HttpServletResponse: enrol successfully");			

})

test('processEnrolStudentsResponse()', function() {
	xmlhttp = new MockHttpRequest();
	// the input does not matter, we only need the response
	sendEnrolStudentsRequest("TeamTeam A \tAlice\talice.tmms@gmail.com","CS2103");
	var response =
			'<enrollmentreports>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Benny]]></name>' +
					'<email><![CDATA[benny.tmms@gmail.com]]></email>' +
					'<status><![CDATA[ADDED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>false</teamnameedited>' +
					'<commentsedited>false</commentsedited>' +
				'</enrollmentreport>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Charlie]]>' +
					'</name><email><![CDATA[charlie.tmms@gmail.com]]></email>' +
					'<status><![CDATA[ADDED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>false</teamnameedited>' +
					'<commentsedited>false</commentsedited>' +
				'</enrollmentreport>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Danny]]></name>' +
					'<email><![CDATA[danny.tmms@gmail.com]]></email>' +
					'<status><![CDATA[ADDED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>false</teamnameedited>' +
					'<commentsedited>false</commentsedited>' +
				'</enrollmentreport>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Alice]]></name>' +
					'<email><![CDATA[alice.tmms@gmail.com]]></email>' +
					'<status><![CDATA[EDITED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>true</teamnameedited>' +
					'<commentsedited>true</commentsedited>' +
				'</enrollmentreport>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Benny]]></name>' +
					'<email><![CDATA[benny.tmms@gmail.com]]></email>' +
					'<status><![CDATA[REMAINED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>false</teamnameedited>' +
					'<commentsedited>false</commentsedited>' +
				'</enrollmentreport>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Charlie]]></name>' +
					'<email><![CDATA[charlie.tmms@gmail.com]]></email>' +
					'<status><![CDATA[REMAINED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>false</teamnameedited>' +
					'<commentsedited>false</commentsedited>' +
				'</enrollmentreport>' +
				'<enrollmentreport>' +
					'<name><![CDATA[Danny]]></name>' +
					'<email><![CDATA[danny.tmms@gmail.com]]></email>' +
					'<status><![CDATA[REMAINED]]></status>' +
					'<nameedited>false</nameedited>' +
					'<teamnameedited>false</teamnameedited>' +
					'<commentsedited>false</commentsedited>' +
				'</enrollmentreport>' +
			'</enrollmentreports>';
	var expectedResult = 
		[
		  {
		    "studentName": "Benny",
		    "studentEmail": "benny.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "false",
		    "commentsEdited": "false",
		    "status": "ADDED"
		  },
		  {
		    "studentName": "Charlie",
		    "studentEmail": "charlie.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "false",
		    "commentsEdited": "false",
		    "status": "ADDED"
		  },
		  {
		    "studentName": "Danny",
		    "studentEmail": "danny.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "false",
		    "commentsEdited": "false",
		    "status": "ADDED"
		  },
		  {
		    "studentName": "Alice",
		    "studentEmail": "alice.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "true",
		    "commentsEdited": "true",
		    "status": "EDITED"
		  },
		  {
		    "studentName": "Benny",
		    "studentEmail": "benny.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "false",
		    "commentsEdited": "false",
		    "status": "REMAINED"
		  },
		  {
		    "studentName": "Charlie",
		    "studentEmail": "charlie.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "false",
		    "commentsEdited": "false",
		    "status": "REMAINED"
		  },
		  {
		    "studentName": "Danny",
		    "studentEmail": "danny.tmms@gmail.com",
		    "nameEdited": "false",
		    "teamNameEdited": "false",
		    "commentsEdited": "false",
		    "status": "REMAINED"
		  }
]
	xmlhttp.receive(CONNECTION_OK,response);
	deepEqual(processEnrolStudentsResponse(), expectedResult, "HttpServletResponse: enrol successfully");	
		

})




