// AJAX
var xmlhttp = new getXMLObject();

// OPERATIONS
var OPERATION_INSTRUCTORINATOR_LOGIN = "instructor_login";
var OPERATION_STUDENT_LOGIN = "student_login";


function instructorLogin()
{
	//send request
	requestInstructorLogin();
	//handle response
	handleInstructorLogin();
}

function requestInstructorLogin() {
	if(xmlhttp)
	{
		xmlhttp.open("POST","/teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_INSTRUCTORINATOR_LOGIN);
	}
}

function handleInstructorLogin()
{
	if (xmlhttp.status == 200) 
	{
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}

function handleStudentLogin()
{
	if (xmlhttp.status == 200) 
	{
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}

function studentLogin()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_STUDENT_LOGIN);
	}
	
	handleStudentLogin();
}

function getXMLObject()  
{
   var xmlHttp = false;
   try {
     xmlHttp = new ActiveXObject("Msxml2.XMLHTTP")  
   }
   catch (e) {
     try {
       xmlHttp = new ActiveXObject("Microsoft.XMLHTTP")  
     }
     catch (e2) {
       xmlHttp = false  
     }
   }
   if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
     xmlHttp = new XMLHttpRequest();        
   }
   return xmlHttp; 
}