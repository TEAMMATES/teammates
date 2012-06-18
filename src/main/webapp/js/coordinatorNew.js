//AJAX
var xmlhttp = new getXMLObject();

//DATE OBJECT
var cal = new CalendarPopup();

//DISPLAY
var DISPLAY_FIELDS_EMPTY = "Please fill in all the relevant fields.";
var DISPLAY_LOADING = "<img src=/images/ajax-loader.gif /><br />";

window.onload = function() {
	initializetooltip();
	if(typeof doPageSpecificOnload !== 'undefined'){
		doPageSpecificOnload();
	};
};

//DynamicDrive JS mouse-hover
document.onmousemove = positiontip;