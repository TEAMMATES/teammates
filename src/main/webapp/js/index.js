// AJAX
var xmlhttp = new getXMLObject();

// OPERATIONS
var OPERATION_INSTRUCTORINATOR_LOGIN = "instructor_login";
var OPERATION_STUDENT_LOGIN = "student_login";

function instructorLogin() {
	// send request
	requestInstructorLogin();
	// handle response
	handleInstructorLogin();
}

function requestInstructorLogin() {
	if (xmlhttp) {
		xmlhttp.open("POST", "/teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_INSTRUCTORINATOR_LOGIN);
	}
}

function handleInstructorLogin() {
	if (xmlhttp.status == 200) {
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}

function handleStudentLogin() {
	if (xmlhttp.status == 200) {
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}

function studentLogin() {
	if (xmlhttp) {
		xmlhttp.open("POST", "teammates", false);
		xmlhttp.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_LOGIN);
	}

	handleStudentLogin();
}

function getXMLObject() {
	var xmlHttp = false;
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			xmlHttp = false;
		}
	}
	if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
		xmlHttp = new XMLHttpRequest();
	}
	return xmlHttp;
}

function submissionCounter(currentDate, baseDate, submissionPerHour, baseCount) {
	var errorMsg = "Thousands of";
	if (!currentDate || !baseDate) {
		return errorMsg;
	}
	var CurrBaseDateDifference = new Date(currentDate - baseDate);
	if (CurrBaseDateDifference < 0) {
		return errorMsg;
	}

	var dd = CurrBaseDateDifference.getDate();
	var mm = CurrBaseDateDifference.getMonth();
	var yyyy = CurrBaseDateDifference.getFullYear() - 1970;
	var month = mm + yyyy * 12;
	var days = dd + month * 30;
	var hr = days * 24;
	var numberOfSubmissions = hr * submissionPerHour;
	numberOfSubmissions += baseCount;
	return formatNumber(numberOfSubmissions);
}

//Setting submission count at page load
onload = function() {
	
	// Parameters for the estimation calculation
	var baseDate = new Date('January 1, 2014 00:00:00');  //The date the parameters were adjusted
	var baseCount = 90000;     //The submission count on the above date
	var submissionPerHour = 13; //The rate at which the submission count is growing
	
	//set the submission count in the page
	var e = document.getElementById('submissionsNumber');
	var currentDate = new Date();
	e.innerHTML = submissionCounter(currentDate, baseDate, submissionPerHour, baseCount);
};

// Format large number with commas
function formatNumber(number) {
	number += '';
	var expression = /(\d+)(\d{3})/;
	while (expression.test(number)) {
		number = number.replace(expression, '$1' + ',' + '$2');
	}
	return number;
}