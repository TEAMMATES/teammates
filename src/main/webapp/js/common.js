var COURSE_ID_MAX_LENGTH = 21;
var COURSE_NAME_MAX_LENGTH = 38;
var EVAL_NAME_MAX_LENGTH = 38;

// Field names
var COURSE_ID = "courseid"; // Used in coordCourse.js
var COURSE_NAME = "coursename"; // Used in coordCourse.js

var EVALUATION_START = "start"; // Used in coordEval.js
var EVALUATION_STARTTIME = "starttime"; // Used in coordEval.js
var EVALUATION_TIMEZONE = "timezone"; // Used in coordEval.js

// Display messages
// Used in coordCourseEnroll.js only
var DISPLAY_ENROLLMENT_FIELDS_EXTRA = "There are too many fields.";
var DISPLAY_ENROLLMENT_FIELDS_MISSING = "There are missing fields.";
var DISPLAY_STUDENT_EMAIL_INVALID = "The e-mail address is invalid.";
// Below two are used in helperNew.js as well
var DISPLAY_STUDENT_NAME_INVALID = "Name should only consist of alphanumerics and not<br />more than 40 characters.";
var DISPLAY_STUDENT_TEAMNAME_INVALID = "Team name should contain less than 25 characters.";

// Used in coordCourse.js only
var DISPLAY_COURSE_MISSING_FIELD = "Course ID and Course Name are compulsory fields.";
var DISPLAY_COURSE_LONG_ID = "Course ID should not exceed " + COURSE_ID_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_LONG_NAME = "Course name should not exceed " + COURSE_NAME_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";

// Used in coordEval.js only
var DISPLAY_EVALUATION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in evaluation name.";
var DISPLAY_EVALUATION_NAME_LENGTHINVALID = "Evaluation name should not exceed 38 characters.";
var DISPLAY_EVALUATION_SCHEDULEINVALID = "The evaluation schedule (start/deadline) is not valid.<br />" +
										 "The start time should be in the future, and the deadline should be after start time.";
var DISPLAY_FIELDS_EMPTY = "Please fill in all the relevant fields.";




/**
* Returns Date object that shows the current time at specific timeZone
* @param timeZone
* @returns {Date}
*/
function getDateWithTimeZoneOffset(timeZone) {
	var now = new Date();

	// Convert local time zone to ms
	var nowTime = now.getTime();

	// Obtain local time zone offset
	var localOffset = now.getTimezoneOffset() * 60000;

	// Obtain UTC time
	var UTC = nowTime + localOffset;

	// Add the time zone of evaluation
	var nowMilliS = UTC + (timeZone * 60 * 60 * 1000);

	now.setTime(nowMilliS);

	return now;
}

/**---------------------------- Sorting Functions --------------------------**/
/**
* jQuery.fn.sortElements
* --------------
* @author James Padolsey (http://james.padolsey.com)
* @version 0.11
* @updated 18-MAR-2010
* --------------
* @param Function comparator:
*   Exactly the same behaviour as [1,2,3].sort(comparator)
*   
* @param Function getSortable
*   A function that should return the element that is
*   to be sorted. The comparator will run on the
*   current collection, but you may want the actual
*   resulting sort to occur on a parent or another
*   associated element.
*   
*   E.g. $('td').sortElements(comparator, function(){
*      return this.parentNode; 
*   })
*   
*   The <td>'s parent (<tr>) will be sorted instead
*   of the <td> itself.
*/
$.fn.sortElements = (function(){
	var sort = [].sort;

	return function(comparator, getSortable) {
		getSortable = getSortable || function(){return this;};
		var placements = this.map(function(){
			var sortElement = getSortable.call(this),
			parentNode = sortElement.parentNode,

			// Since the element itself will change position, we have
			// to have some way of storing it's original position in
			// the DOM. The easiest way is to have a 'flag' node:
			nextSibling = parentNode.insertBefore(
					document.createTextNode(''),
					sortElement.nextSibling
			);

			return function() {
				if (parentNode === this) {
					throw new Error(
							"You can't sort elements if any one is a descendant of another."
					);
				}
				// Insert before flag:
				parentNode.insertBefore(this, nextSibling);
				// Remove flag:
				parentNode.removeChild(nextSibling);
			};
		});
		return sort.call(this, comparator).each(function(i){
			placements[i].call(getSortable.call(this));
		});
	};
})();

/**
* Sorts a table
* @param divElement
* 		The sort button
* @param colIdx
* 		The column index (1-based) as key for the sort
*/
function toggleSort(divElement,colIdx,comparator) {
	sortTable(divElement,colIdx,comparator);
	$(".buttonSortAscending").attr("class","buttonSortNone");
	$(divElement).attr("class","buttonSortAscending");
}

/**
* Sorts a table based on certain column and comparator
* @param oneOfTableCell
* 		One of the table cell
* @param colIdx
* 		The column index (1-based) as key for the sort
* @param comparator
* 		This will be used to compare two cells. If not specified,
* 		sortBaseCell will be used
*/
function sortTable(oneOfTableCell, colIdx, comparator){
	if(!comparator) comparator = sortBaseCell;
	var table = $(oneOfTableCell);
	if(!table.is("table")){
		table = $(oneOfTableCell).parentsUntil("table");
		table = $(table[table.length-1].parentNode);
	}
	keys = $("td:nth-child("+colIdx+")",table);
	keys.sortElements( comparator, function(){return this.parentNode;} );
}

/**
* The base comparator for a cell
* @param cell1
* @param cell2
* @returns
*/
function sortBaseCell(cell1, cell2){
	cell1 = cell1.innerHTML;
	cell2 = cell2.innerHTML;
	return sortBase(cell1,cell2);
}

/**
* The base comparator (ascending)
* @param x
* @param y
* @returns
*/
function sortBase(x, y) {
	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
}

/**
* Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0%
* with possibly a tag that surrounds it.
* @param a
* @param b
*/
function sortByPoint(a, b){
	a = getPointValue(a,true);
	b = getPointValue(b,true);
	
	return sortBase(a,b);
}

/**
* Comparator to sort strings in format: [+-]x% | N/A
* with possibly a tag that surrounds it.
* @param a
* @param b
*/
function sortByDiff(a, b){
	a = getPointValue(a,false);
	b = getPointValue(b,false);
	
	return sortBase(a,b);
}

/**
* To get point value from a formatted string
* @param s
* 		A table cell (td tag) that contains the formatted string
* @param ditchZero
* 		Whether 0% should be treated as lower than -90 or not
* @returns
*/
function getPointValue(s, ditchZero){
	s = s.innerHTML;
	if(s.lastIndexOf("<")!=-1){
		s = s.substring(0,s.lastIndexOf("<"));
		s = s.substring(s.lastIndexOf(">")+1);
	}
	if(s.indexOf("/")!=-1){
		if(s.indexOf("S")!=-1) return 999; // Case N/S
		return 1000; // Case N/A
	}
	if(s=="0%"){ // Case 0%
		if(ditchZero) return 0;
		else return 100;
	}
	s = s.replace("E","");
	s = s.replace("%","");
	if(s=="") return 100; // Case E
	return 100+eval(s); // Other typical cases
}

/**-----------------------UI Related Helper Functions-----------------------**/

var DIV_TOPOFPAGE = "topOfPage";
/**
* Scrolls the screen to top
*/
function scrollToTop() {
	document.getElementById(DIV_TOPOFPAGE).scrollIntoView(true);
}

/** Selector for status message div tag (to be used in JQuery) */
var DIV_STATUS_MESSAGE = "#statusMessage";

/**
* Sets a status message.
* Change the background color to red if it's an error
* @param message
* @param error
*/
function setStatusMessage(message, error) {
	if (message == "") {
		clearStatusMessage();
		return;
	}
	$(DIV_STATUS_MESSAGE).html(message);
	if(error===true){
		$(DIV_STATUS_MESSAGE).css("background","#FF9999");
	} else {
		$(DIV_STATUS_MESSAGE).css("background","");
	}
	$(DIV_STATUS_MESSAGE).show();
}

/**
* Clears the status message div tag and hides it
*/
function clearStatusMessage() {
	$(DIV_STATUS_MESSAGE).html("").css("background","").hide();
}

