/* 
 * This Javascript file is included in all administrator pages. Functions here 
 * should be common to the administrator pages.
 */




// AJAX
var xmlhttp = new getXMLObject();

// OPERATIONS
var OPERATION_ADMINISTRATOR_ADDINSTRUCTORINATOR = "administrator_addinstructor";
var OPERATION_ADMINISTRATOR_LOGOUT = "administrator_logout";

// PARAMETERS
var INSTRUCTOR_EMAIL = "instructoremail";
var INSTRUCTOR_GOOGLEID = "instructorid";
var INSTRUCTOR_NAME = "instructorname";



function addInstructor(googleID, name, email)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_ADDINSTRUCTORINATOR + "&" + INSTRUCTORINATOR_GOOGLEID + 
				"=" + googleID + "&" + INSTRUCTORINATOR_NAME + "=" + name + "&" + INSTRUCTORINATOR_EMAIL + "=" + email);
	}
}

function verifyInstructorData()
{
	var googleID = $('[name="'+INSTRUCTOR_GOOGLEID + '"]').val();
	var name = $('[name="'+INSTRUCTOR_NAME + '"]').val();
	var email = $('[name="'+INSTRUCTOR_EMAIL + '"]').val();
	if(googleID == "" || name == "" || email == "")
	{
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);	
		return false;
	}
	
	else if(!isEmailValid(email))
	{
		setStatusMessage(DISPLAY_EMAIL_INVALID, true);
		return false;
	}
	
	else if(!isNameValid(name))
	{
		setStatusMessage(DISPLAY_NAME_INVALID, true);
		return false;
	}

	return true;
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

function handleLogout()
{
	if (xmlhttp.status == 200) 
	{
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		window.location = url.firstChild.nodeValue;
	}
}


function isGoogleIDValid(googleID)
{
	if(googleID.indexOf("\\") >= 0 || googleID.indexOf("'") >= 0 || googleID.indexOf("\"") >= 0)
	{
		return false;
	}
	
	else if(googleID.match(/^[a-zA-Z0-9@ .-]*$/) == null)
	{
		return false;
	}
	
	else if(googleID.length > 29)
	{
		return false;
	}
	
	return true;
}


function logout()
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_LOGOUT);
	}
	
	handleLogout();
}


function showHideErrorMessage(s){
	$("#" + s).toggle();
	
}
