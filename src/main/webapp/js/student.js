/* 
 * This Javascript file is included in all student pages. Functions here 
 * should be common to the student pages.
 */



//Initial load-up
//-----------------------------------------------------------------------------
window.onload = function() {
	initializetooltip();
	//Get Element By Class Name, in this case nav hyperlinks, it should return an array of items
	var tabs = document.getElementsByClassName('nav');
	//Get the url of the current page
	var url = document.location;
			
	if (url.href.charAt(url.length-1) == '/') {
	//Get the final URL sub string of the page e.g. InstructorEval, InstructorEvalEdit, etc.
		url = url.substr(0,url.length - 1); 
	}
	//get the href link and cast it to lower case for string comparison purposes
	var curPage = url.href.split('/').pop().toLowerCase();
			
	for (i=0; i<tabs.length; i++){
	//Search the so called tabs, using an attribute call data-link as defined in the href link
	//This attribute will tell which section of the page the user is on and cast to lower case
		var link = String(tabs[i].getAttribute('data-link')).toLowerCase();
		if (curPage.indexOf(link) != -1){ 
		//if curPage contains any part of the link as defined by data-link, then its found
		tabs[i].parentNode.className = "current"; 
		//so set the parentNode classname which is the <li> in this case to class current
		//as defined in common.css
		} 
	}
};

// DynamicDrive JS mouse-hover
document.onmousemove = positiontip;

//-----------------------------------------------------------------------------

