/*******************************************************************************
 * Cool DHTML tooltip script- © Dynamic Drive DHTML code library
 * (www.dynamicdrive.com) This notice MUST stay intact for legal use Visit
 * Dynamic Drive at http://www.dynamicdrive.com/ for full source code
 ******************************************************************************/

var offsetxpoint;
var offsetypoint;
var ie;
var ns6;
var enabletip;
var tipobj;

function ietruebody() {
	return (document.compatMode && document.compatMode != "BackCompat") ? document.documentElement : document.body;
}

function ddrivetip(thetext, thecolor, thewidth) {
	if (ns6 || ie) {
		if (typeof thewidth != "undefined") {
			tipobj.style.width = thewidth + "px";
		}
		if (typeof thecolor != "undefined" && thecolor != "") {
			tipobj.style.backgroundColor = thecolor;
		}
		
		tipobj.innerHTML = thetext;
		enabletip = true;
		return false;
	}
}

function positiontip(e) {
	if (enabletip) {
		var curX = (ns6) ? e.pageX : event.clientX+ietruebody().scrollLeft;
		var curY = (ns6) ? e.pageY : event.clientY+ietruebody().scrollTop;
		
		// Find out how close the mouse is to the corner of the window
		var rightedge = ie&&!window.opera ? ietruebody().clientWidth-event.clientX-offsetxpoint : window.innerWidth-e.clientX-offsetxpoint-20;
		var bottomedge = ie&&!window.opera ? ietruebody().clientHeight-event.clientY-offsetypoint : window.innerHeight-e.clientY-offsetypoint-20;
	
		var leftedge = (offsetxpoint < 0) ? offsetxpoint*(-1) : -1000;
		
		// If the horizontal distance isn't enough to accomodate the width of
		// the context menu
		if (rightedge < tipobj.offsetWidth) {
			// Move the horizontal position of the menu to the left by it's
			// width
			tipobj.style.left = ie ? ietruebody().scrollLeft+event.clientX-tipobj.offsetWidth+"px" : window.pageXOffset+e.clientX-tipobj.offsetWidth+"px";
		} else if (curX < leftedge) {
			tipobj.style.left = "5px";
		} else {
			// Position the horizontal position of the menu where the mouse is
			// positioned
			tipobj.style.left = curX + offsetxpoint + "px";
		}
		
		// Same concept with the vertical position
		if (bottomedge < tipobj.offsetHeight) {
			tipobj.style.top = ie ? ietruebody().scrollTop+event.clientY-tipobj.offsetHeight-offsetypoint+"px" : window.pageYOffset+e.clientY-tipobj.offsetHeight-offsetypoint+"px";
		} else {
			tipobj.style.top = curY + offsetypoint + "px";
			tipobj.style.visibility = "visible";
		}
	}
}

function hideddrivetip() {
	if (ns6 || ie){
		enabletip = false;
		tipobj.style.visibility = "hidden";
		tipobj.style.left = "-1000px";
		tipobj.style.backgroundColor = '';
		tipobj.style.width = '';
	}
}

function initializetooltip() {
	offsetxpoint = -60; // Customize x offset of tooltip
	offsetypoint = 20; // Customize y offset of tooltip
	ie = document.all;
	ns6 = document.getElementById && !document.all;
	enabletip = false;
	
	if (ie) {
		tipobj = document.all["dhtmltooltip"];
	} else if (ns6) {
		tipobj = document.getElementById("dhtmltooltip");
	} else {
		tipobj = "";
	}
}