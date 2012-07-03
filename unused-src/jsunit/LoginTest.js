//override handleCoordinatorLogin() in index.js
function handleCoordinatorLogin()
{
	if (xmlhttp.status == 200) 
	{
		var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
		//original: window.location = url.firstChild.nodeValue;
		//modified: return url instead of redirect window
		return url.firstChild.nodeValue; 
	}
}

module("LoginTest");

test('coordLogin', function() {
	
	xmlhttp = new MockHttpRequest();
	
	requestCoordinatorLogin();
	equal(xmlhttp.readyState, 1, "Requst State: Connection Open");
	equal(xmlhttp.getRequestHeader("Content-Type"), "application/x-www-form-urlencoded;", "Request Header: content-type = application/x-www-form-urlencoded;");
	equal(xmlhttp.requestText, "operation=coordinator_login", "Request Data: operation=coordinator_login");
	
	//mock server response
	xmlhttp.receive(200, "<url><![CDATA[/coordinator.jsp]]></url>");
	
	equal(handleCoordinatorLogin(), "/coordinator.jsp", "Correct url: /coordinator.jsp");
	
});