// ===================================================================
// Author: Matt Kruse <matt@mattkruse.com>
// WWW: http://www.mattkruse.com/
//
// NOTICE: You may use this code for any purpose, commercial or
// private, without any further permission from the author. You may
// remove this notice from your final code if you wish, however it is
// appreciated by the author if at least my web site address is kept.
//
// You may *NOT* re-distribute this code in any way except through its
// use. That means, you can include it in your product, or your web
// site, or any other form where the code is actually being used. You
// may not put the plain javascript up on your site for download or
// include it in your javascript libraries for download.
// If you wish to share this code with others, please just point them
// to the URL instead.
// Please DO NOT link directly to my .js files from your site. Copy
// the files to your server and use them there. Thank you.
// ===================================================================

/* SOURCE FILE: AnchorPosition.js */

/*
 * AnchorPosition.js Author: Matt Kruse Last modified: 10/11/02
 * 
 * DESCRIPTION: These functions find the position of an <a> tag in a document,
 * so other elements can be positioned relative to it.
 * 
 * COMPATABILITY: Netscape 4.x,6.x,Mozilla, IE 5.x,6.x on Windows. Some small
 * positioning errors - usually with Window positioning - occur on the Macintosh
 * platform.
 * 
 * FUNCTIONS: getAnchorPosition(anchorname) Returns an Object() having .x and .y
 * properties of the pixel instructorinates of the upper-left corner of the anchor.
 * Position is relative to the PAGE.
 * 
 * getAnchorWindowPosition(anchorname) Returns an Object() having .x and .y
 * properties of the pixel instructorinates of the upper-left corner of the anchor,
 * relative to the WHOLE SCREEN.
 * 
 * NOTES:
 * 
 * 1) For popping up separate browser windows, use getAnchorWindowPosition.
 * Otherwise, use getAnchorPosition
 * 
 * 2) Your anchor tag MUST contain both NAME and ID attributes which are the
 * same. For example: <A NAME="test" ID="test"> </a>
 * 
 * 3) There must be at least a space between <a> </a> for IE5.5 to see the
 * anchor tag correctly. Do not do <a></a> with no space.
 */

// getAnchorPosition(anchorname)
// This function returns an object having .x and .y properties which are the
// instructorinates
// of the named anchor, relative to the page.
function getAnchorPosition(anchorname) {
	// This function will return an Object with x and y properties
	var useWindow = false;
	var instructorinates = new Object();
	var x = 0, y = 0;
	// Browser capability sniffing
	var use_gebi = false, use_css = false, use_layers = false;
	if (document.getElementById) {
		use_gebi = true;
	} else if (document.all) {
		use_css = true;
	} else if (document.layers) {
		use_layers = true;
	}
	// Logic to find position
	if (use_gebi && document.all) {
		x = AnchorPosition_getPageOffsetLeft(document.all[anchorname]);
		y = AnchorPosition_getPageOffsetTop(document.all[anchorname]);
	} else if (use_gebi) {
		var o = document.getElementById(anchorname);
		x = AnchorPosition_getPageOffsetLeft(o);
		y = AnchorPosition_getPageOffsetTop(o);
	} else if (use_css) {
		x = AnchorPosition_getPageOffsetLeft(document.all[anchorname]);
		y = AnchorPosition_getPageOffsetTop(document.all[anchorname]);
	} else if (use_layers) {
		var found = 0;
		for ( var i = 0; i < document.anchors.length; i++) {
			if (document.anchors[i].name == anchorname) {
				found = 1;
				break;
			}
		}
		if (found == 0) {
			instructorinates.x = 0;
			instructorinates.y = 0;
			return instructorinates;
		}
		x = document.anchors[i].x;
		y = document.anchors[i].y;
	} else {
		instructorinates.x = 0;
		instructorinates.y = 0;
		return instructorinates;
	}
	instructorinates.x = x;
	instructorinates.y = y;
	return instructorinates;
}

// getAnchorWindowPosition(anchorname)
// This function returns an object having .x and .y properties which are the
// instructorinates
// of the named anchor, relative to the window
function getAnchorWindowPosition(anchorname) {
	var instructorinates = getAnchorPosition(anchorname);
	var x = 0;
	var y = 0;
	if (document.getElementById) {
		if (isNaN(window.screenX)) {
			x = instructorinates.x - document.body.scrollLeft + window.screenLeft;
			y = instructorinates.y - document.body.scrollTop + window.screenTop;
		} else {
			x = instructorinates.x + window.screenX
					+ (window.outerWidth - window.innerWidth)
					- window.pageXOffset;
			y = instructorinates.y + window.screenY
					+ (window.outerHeight - 24 - window.innerHeight)
					- window.pageYOffset;
		}
	} else if (document.all) {
		x = instructorinates.x - document.body.scrollLeft + window.screenLeft;
		y = instructorinates.y - document.body.scrollTop + window.screenTop;
	} else if (document.layers) {
		x = instructorinates.x + window.screenX
				+ (window.outerWidth - window.innerWidth) - window.pageXOffset;
		y = instructorinates.y + window.screenY
				+ (window.outerHeight - 24 - window.innerHeight)
				- window.pageYOffset;
	}
	instructorinates.x = x;
	instructorinates.y = y;
	return instructorinates;
}

// Functions for IE to get position of an object
function AnchorPosition_getPageOffsetLeft(el) {
	var ol = el.offsetLeft;
	while ((el = el.offsetParent) != null) {
		ol += el.offsetLeft;
	}
	return ol;
}
function AnchorPosition_getWindowOffsetLeft(el) {
	return AnchorPosition_getPageOffsetLeft(el) - document.body.scrollLeft;
}
function AnchorPosition_getPageOffsetTop(el) {
	var ot = el.offsetTop;
	while ((el = el.offsetParent) != null) {
		ot += el.offsetTop;
	}
	return ot;
}
function AnchorPosition_getWindowOffsetTop(el) {
	return AnchorPosition_getPageOffsetTop(el) - document.body.scrollTop;
}
