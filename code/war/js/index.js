// AJAX
var xmlhttp = new getXMLObject();


// OPERATIONS
var OPERATION_COORDINATOR_LOGIN = "coordinator_login";
var OPERATION_STUDENT_LOGIN = "student_login";


function coordinatorLogin()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_COORDINATOR_LOGIN);
	}
	
	handleCoordinatorLogin();
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


function handleCoordinatorLogin()
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

