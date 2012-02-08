// AJAX
var xmlhttp = new getXMLObject();

// OPERATIONS
var OPERATION_ADMINISTRATOR_ADDCOORDINATOR = "administrator_addcoordinator";
var OPERATION_ADMINISTRATOR_LOGOUT = "administrator_logout";

// PARAMETERS
var COORDINATOR_EMAIL = "coordinatoremail";
var COORDINATOR_GOOGLEID = "coordinatorgoogleID";
var COORDINATOR_NAME = "coordinatorname";



function addCoordinator(googleID, name, email)
{
	if(xmlhttp)
	{
		xmlhttp.open("POST","teammates",false); 
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;");
		xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_ADDCOORDINATOR + "&" + COORDINATOR_GOOGLEID + 
				"=" + googleID + "&" + COORDINATOR_NAME + "=" + name + "&" + COORDINATOR_EMAIL + "=" + email);
	}
}

function doAddCoordinator(form)
{
	var googleID = form.elements[0].value;
	var name = form.elements[1].value;
	var email = form.elements[2].value;

	if(googleID == "" || name == "" || email == "")
	{
		alert("Please fill in all fields.");
	}
	
	else if(!isEmailValid(email))
	{
		alert("E-mail is invalid.");
	}
	
	else if(!isGoogleIDValid(googleID))
	{
		alert("Google ID is invalid.");
	}
	
	else if(!isNameValid(name))
	{
		alert("Name is invalid.");
	}
	
	else
	{
		addCoordinator(googleID, name, email);
	}
	
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

function isEmailValid(email)
{
	if(email.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i) != null && email.length <= 40)
	{
		return true;
	}

	return false;
}

function isGoogleIDValid(googleID)
{
	if(googleID.indexOf("\\") >= 0 || googleID.indexOf("'") >= 0 || googleID.indexOf("\"") >= 0)
	{
		return false;
	}
	
	else if(googleID.match(/^[a-zA-Z0-9 .-]*$/) == null)
	{
		return false;
	}
	
	else if(googleID.length > 29)
	{
		return false;
	}
	
	return true;
}

function isNameValid(name)
{
	if(name.indexOf("\\") >= 0 || name.indexOf("'") >= 0 || name.indexOf("\"") >= 0)
	{
		return false;
	}
	
	else if(name.match(/^[a-zA-Z0-9 ,.-]*$/) == null)
	{
		return false;
	}
	
	else if(name.length > 35)
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