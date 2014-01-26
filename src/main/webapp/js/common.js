var COURSE_ID_MAX_LENGTH = 40;
var COURSE_NAME_MAX_LENGTH = 64;
var EVAL_NAME_MAX_LENGTH = 38;
var FEEDBACK_SESSION_NAME_MAX_LENGTH = 38;
var FEEDBACK_SESSION_INSTRUCTIONS_MAX_LENGTH = 500;

// Field names
var COURSE_ID = "courseid"; // Used in instructorCourse.js
var COURSE_NAME = "coursename"; // Used in instructorCourse.js
var COURSE_INSTRUCTOR_NAME = "instructorname"; // Used in instructorCourse.js
var COURSE_INSTRUCTOR_EMAIL = "instructoremail"; // Used in instructorCourse.js
var COURSE_INSTRUCTOR_ID = "instructorid"; // Used in instructorCourse.js

var EVALUATION_START = "start"; // Used in instructorEval.js
var EVALUATION_STARTTIME = "starttime"; // Used in instructorEval.js
var EVALUATION_TIMEZONE = "timezone"; // Used in instructorEval.js

// TODO Move to instructorFeedback.js?
var FEEDBACK_SESSION_STARTDATE = "startdate";
var FEEDBACK_SESSION_STARTTIME = "starttime"; 
var FEEDBACK_SESSION_TIMEZONE = "timezone";
var FEEDBACK_SESSION_CHANGETYPE = "feedbackchangetype";
var FEEDBACK_SESSION_VISIBLEDATE = "visibledate";
var FEEDBACK_SESSION_VISIBLETIME = "visibletime";
var FEEDBACK_SESSION_PUBLISHDATE = "publishdate";
var FEEDBACK_SESSION_PUBLISHTIME = "publishtime";
var FEEDBACK_SESSION_SESSIONVISIBLEBUTTON = "sessionVisibleFromButton";
var FEEDBACK_SESSION_RESULTSVISIBLEBUTTON = "resultsVisibleFromButton";

// TODO Move to instructorFeedbackEdit.js?
var FEEDBACK_QUESTION_GIVERTYPE ="givertype";
var FEEDBACK_QUESTION_RECIPIENTTYPE ="recipienttype";
var FEEDBACK_QUESTION_NUMBEROFENTITIES ="numofrecipients";
var FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE = "numofrecipientstype";
var FEEDBACK_QUESTION_TYPE ="questiontype";
var FEEDBACK_QUESTION_MCQCHOICE = "mcqOption";
var FEEDBACK_QUESTION_MSQCHOICE = "msqOption";
var FEEDBACK_QUESTION_NUMBEROFCHOICECREATED ="noofchoicecreated";
var FEEDBACK_QUESTION_NUMBER ="questionnum";
var FEEDBACK_QUESTION_TEXT ="questiontext";
var FEEDBACK_QUESTION_EDITTEXT = "questionedittext";
var FEEDBACK_QUESTION_EDITTYPE = "questionedittype";
var FEEDBACK_QUESTION_SAVECHANGESTEXT = "questionsavechangestext";
var FEEDBACK_QUESTION_SHOWRESPONSESTO = "showresponsesto";
var FEEDBACK_QUESTION_SHOWGIVERTO = "showgiverto";
var FEEDBACK_QUESTION_SHOWRECIPIENTTO = "showrecipientto";
var FEEDBACK_QUESTION_TYPENAME_TEXT = "Essay question";
var FEEDBACK_QUESTION_TYPENAME_MCQ = "Multiple-choice question";
var FEEDBACK_QUESTION_TYPENAME_MSQ = "Multiple-select question";

// Display messages
// Used for validating input
var DISPLAY_INPUT_FIELDS_EXTRA = "There are too many fields.";
var DISPLAY_INPUT_FIELDS_MISSING = "There are missing fields.";
var DISPLAY_GOOGLEID_INVALID = "GoogleID should only consist of alphanumerics, fullstops, dashes or underscores.";
var DISPLAY_EMAIL_INVALID = "The e-mail address is invalid.";
var DISPLAY_NAME_INVALID = "Name should only consist of alphanumerics or hyphens, apostrophes, fullstops, commas, slashes, round brackets<br> and not more than 40 characters.";
var DISPLAY_STUDENT_TEAMNAME_INVALID = "Team name should contain less than 60 characters.";

// Used in instructorCourse.js only
var DISPLAY_COURSE_LONG_ID = "Course ID should not exceed "
		+ COURSE_ID_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_LONG_NAME = "Course name should not exceed "
		+ COURSE_NAME_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs in course ID.";
var DISPLAY_COURSE_COURSE_ID_EMPTY = "Course ID cannot be empty.";
var DISPLAY_COURSE_COURSE_NAME_EMPTY = "Course name cannot be empty";

//Used in instructorCourseEdit.js
var DISPLAY_INSTRUCTOR_ID_EMPTY = "Instructor ID cannot be empty.";
var DISPLAY_INSTRUCTOR_NAME_EMPTY = "Instructor name cannot be empty.";
var DISPLAY_INSTRUCTOR_EMAIL_EMPTY = "Instructor email cannot be empty.";
var DISPLAY_CANNOT_DELETE_LAST_INSTRUCTOR = "There is only ONE instructor left in the course. You are not allowed to delete this instructor.";

// Used in instructorCourseEnroll.js only
var DISPLAY_ENROLLMENT_INPUT_EMPTY = "Please input at least one student detail.";

// Used in instructorEval.js only
var DISPLAY_EVALUATION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in evaluation name.";
var DISPLAY_EVALUATION_NAME_LENGTHINVALID = "Evaluation name should not exceed 38 characters.";
var DISPLAY_EVALUATION_SCHEDULEINVALID = "The evaluation schedule (start/deadline) is not valid.<br />"
		+ "The start time should be in the future, and the deadline should be after start time.";
var DISPLAY_FIELDS_EMPTY = "Please fill in all the relevant fields.";
var DISPLAY_INVALID_INPUT = "Unexpected error. Invalid Input";

//Used in instructorFeedback.js only
var DISPLAY_FEEDBACK_SESSION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in feedback session name.";
var DISPLAY_FEEDBACK_SESSION_NAME_LENGTHINVALID = "Feedback session name should not exceed 38 characters.";
var DISPLAY_FEEDBACK_SESSION_SCHEDULEINVALID = "The feedback sesion schedule (start/end) is not valid.<br />"
		+ "The start time should be in the future, and the end time should be after start time.";
var DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID = "Please enter the maximum number of recipients each respondants should give feedback to.";
var DISPLAY_FEEDBACK_QUESTION_TEXTINVALID = "Please enter a valid question. The question text cannot be empty.";
var DISPLAY_FEEDBACK_SESSION_INSTRUCTIONS_LENGTHINVALID = "Instructions to students should not exceed 500 characters.";
var DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID = "Feedback session visible date must not be empty";
var DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID = "Feedback session publish date must not be empty";
// Max length for input
var TEAMNAME_MAX_LENGTH = 60;
var NAME_MAX_LENGTH = 40;
var INSTITUTION_MAX_LENGTH = 64;


function initializenavbar(){
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
}

/**
 * Sorts a table
 * 
 * @param divElement
 *            The sort button
 * @param colIdx
 *            The column index (1-based) as key for the sort
 */
function toggleSort(divElement, colIdx, comparator) {
	if ($(divElement).attr("class") == "buttonSortNone") {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).parent().parent().find(".buttonSortAscending").attr("class", "buttonSortNone");
		$(divElement).parent().parent().find(".buttonSortDescending").attr("class", "buttonSortNone");
		$(divElement).attr("class", "buttonSortAscending");
	} else if ($(divElement).attr("class") == "buttonSortAscending") {
		sortTable(divElement, colIdx, comparator, false);
		$(divElement).attr("class", "buttonSortDescending");
	} else {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "buttonSortAscending");
	}
}


//http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 * 
 * @param oneOfTableCell
 *            One of the table cell
 * @param colIdx
 *            The column index (1-based) as key for the sort
 * @param ascending
 * 			  if this is true, it will be ascending order, else it will be descending order
 */
function sortTable(oneOfTableCell, colIdx, comparator, ascending) {
	//Get the table
	var table = $(oneOfTableCell);
	if (!table.is("table")){
		table =  $(oneOfTableCell).parents("table");
	}
	
	var columnType=0;
	var store = [];
	var RowList = $("tr", table);
	//Iterate through column's contents to decide which comparator to use
	for (var i = 1; i < RowList.length; i++) {
		var innerText = RowList[i].cells[colIdx-1].innerHTML;
		
		//Store rows together with the innerText to compare
		store.push([innerText, RowList[i]]);
		
		if((columnType==0 || columnType==1) && isNumber(innerText)){
			columnType=1;
		}else if((columnType==0 || columnType==2) && isDate(innerText)){
			columnType=2;
		}else{
			columnType=3;
		}
	}
	
	if(comparator==null){
		if (columnType==1){
			 comparator = sortNum;
		}else if(columnType==2){
			 comparator = sortDate;
		}else{
			 comparator = sortBase;
		}
	}
	
	store.sort(function(x,y){
		if(ascending==true){
			return comparator(x[0],y[0]);
		}else{
			return comparator(y[0],x[0]);
		}
    });
	
	var tbody = $(table.get(0)).children('tbody');

	if(tbody.size<1){
		tbody = table;
	}
	
	//Must push to target tbody else it will generate a new tbody for the table
    for(var i=0; i<store.length; i++){
        tbody.get(0).appendChild(store[i][1]);
    }
    store = null;
}

/**
 * The base comparator (ascending)
 * 
 * @param x
 * @param y
 * @returns
 */
function sortBase(x, y) {
	//Text sorting
	return (x < y ? -1 : x > y ? 1 : 0);
}

/**
 * Comparator for numbers (integer, double) (ascending)
 * 
 * @param x
 * @param y
 * @returns
 */
function sortNum(x, y){
	return x-y;
}


/**
 * Comparator for date. Allows for the same format as isDate()
 * 
 * @param x
 * @param y
 * @returns 1 if Date x is after y, 0 if same and -1 if before
 */
function sortDate(x, y){
	x = Date.parse(x);
	y = Date.parse(y);
	var comparisonResult = (x > y) ? 1 : (x < y) ? -1 : 0;
	return comparisonResult;
}

/**
* Function that returns the pattern of DayMonthYearFormat (dd/mm/yyyy)
* 
* @returns pattern string
*/
function getDayMonthYearFormat(){
	return /^\s*(\d{2})[\/\- ](\d{2})[\/\- ](\d{4}|\d{2})\s*$/;
}


/**
 * Tests whether the passed object is an actual date
 * with an accepted format
 * 
 * Allowed formats : http://dygraphs.com/date-formats.html
 * 
 * TEAMMATES currently follows the RFC2822 / IETF date syntax 
 * e.g. 02 Apr 2012, 23:59
 * 
 * @param date
 * @returns boolean
 */
function isDate(date){
	return !isNaN(Date.parse(date));
}

/**
* Function to test if param is a numerical value
* @param num
* @returns boolean
*/
function isNumber(num) {
	  return (typeof num == 'string' || typeof num == 'number') && !isNaN(num - 0) && num !== '';
}

/**
 * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
 * possibly a tag that surrounds it.
 * 
 * @param a
 * @param b
 */
function sortByPoint(a, b) {
	a = getPointValue(a, true);
	b = getPointValue(b, true);
	
	if(isNumber(a) && isNumber(b)){
		return sortNum(a, b);
	}else{
		return sortBase(a, b);
	}
}

/**
 * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
 * surrounds it.
 * 
 * @param a
 * @param b
 */
function sortByDiff(a, b) {
	a = getPointValue(a, false);
	b = getPointValue(b, false);

	if(isNumber(a) && isNumber(b)){
		return sortNum(a, b);
	}else{
		return sortBase(a, b);
	}
}

/**
 * To get point value from a formatted string
 * 
 * @param s
 *            A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *            Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
	if (s.lastIndexOf("<") != -1) {
		s = s.substring(0, s.lastIndexOf("<"));
		s = s.substring(s.lastIndexOf(">") + 1);
	}
	if (s.indexOf("/") != -1) {
		if (s.indexOf("S") != -1)
			return 999; // Case N/S
		return 1000; // Case N/A
	}
	if (s == "0%") { // Case 0%
		if (ditchZero)
			return 0;
		else
			return 100;
	}
	s = s.replace("E", "");
	s = s.replace("%", "");
	if (s == "")
		return 100; // Case E
	return 100 + eval(s); // Other typical cases
}

/** -----------------------UI Related Helper Functions-----------------------* */

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
 * Sets a status message. Change the background color to red if it's an error
 * 
 * @param message
 * @param error
 */
function setStatusMessage(message, error) {
	if (message == "") {
		clearStatusMessage();
		return;
	}
	$(DIV_STATUS_MESSAGE).html(message);
	$(DIV_STATUS_MESSAGE).show();
	if (error === true) {
		$(DIV_STATUS_MESSAGE).attr("style",
				"display: block; background-color: rgb(255, 153, 153);");
	} else {
		$(DIV_STATUS_MESSAGE).attr("style",
				"display: block; ");
	}
	document.getElementById( 'statusMessage' ).scrollIntoView();
}

/**
 * Clears the status message div tag and hides it
 */
function clearStatusMessage() {
	$(DIV_STATUS_MESSAGE).html("");
	$(DIV_STATUS_MESSAGE).css("background", "");
	$(DIV_STATUS_MESSAGE).attr("style", "display: none;");
}

/**
 * Function to check whether the evaluation submission edit/submit form has been
 * fully filled (no unfilled textarea and dropdown box)
 * 
 * @returns {Boolean}
 */
function checkEvaluationForm() {
	points = $("select");
	comments = $("textarea");
	for ( var i = 0; i < points.length; i++) {
		if (points[i].value == '') {
			setStatusMessage("Please give contribution scale to everyone", true);
			return false;
		}
	}
	return true;
}

/**
 * Sanitize GoogleID by trimming space and '@gmail.com'
 * Used in instructorCourse, instructorCourseEdit, adminHome
 * 
 * @param googleId
 * @returns sanitizedGoolgeId
 */
function sanitizeGoogleId(googleId) {
	googleId = googleId.trim();
	var loc = googleId.toLowerCase().indexOf("@gmail.com");
	if (loc > -1) {
		googleId = googleId.substring(0, loc);   
	}
	return googleId.trim();
}

/**
 * Check if the GoogleID is valid
 * GoogleID allow only alphanumeric, full stops, dashes, underscores or valid email
 * 
 * @param googleId
 * @return {Boolean}
 */
function isValidGoogleId(googleId) {
	var isValidNonEmailGoogleId = false;
	googleId = googleId.trim();
	
	// match() retrieve the matches when matching a string against a regular expression.
	var matches = googleId.match(/^([\w-]+(?:\.[\w-]+)*)/);
	
	isValidNonEmailGoogleId = (matches != null && matches[0] == googleId);
	
	var isValidEmailGoogleId = isEmailValid(googleId);
	if (googleId.toLowerCase().indexOf("@gmail.com") > -1) {
		isValidEmailGoogleId = false;
	}
	
	// email addresses are valid google IDs too
	return isValidNonEmailGoogleId || isValidEmailGoogleId;
}

/**
 * Checks whether an e-mail is valid.
 * (Used in instructorCourseEdit.js)
 * 
 * @param email
 * @returns {Boolean}
 */
function isEmailValid(email) {
	return email
			.match(/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i) != null;
}

/**
 * Checks whether a person's name is valid.
 * (Used in instructorCourseEdit.js)
 * 
 * @param name
 * @returns {Boolean}
 */
function isNameValid(name) {
	name = name.trim();

	if (name == "") {
		return false;
	}
	if (name.match(/[^\/\\,.'\-\(\)0-9a-zA-Z \t]/)) {
		// Returns true if a character NOT belonging to the following set
		// appears in the name: slash(/), backslash(\), fullstop(.), comma(,),
		// apostrophe('), hyphen(-), round brackets(()), alpha numeric
		// characters, space, tab
		return false;
	} else if (name.length > NAME_MAX_LENGTH) {
		return false;
	} else {
		return true;
	}
}

/**
 * Checks whether an institution name is valid
 * Used in adminHome page (through administrator.js)
 * @param name
 * @returns {Boolean}
 */
function isInstitutionValid(institution) {
        institution = institution.trim();

        if (institution == "") {
                return false;
        }
        if (institution.match(/[^\/\\,.'\-\(\)0-9a-zA-Z \t]/)) {
                // Returns true if a character NOT belonging to the following set
                // appears in the name: slash(/), backslash(\), fullstop(.), comma(,),
                // apostrophe('), hyphen(-), round brackets(()), alpha numeric
                // characters, space, tab
                return false;
        } else if (institution.length > NAME_MAX_LENGTH) {
                return false;
        } else {
                return true;
                }
}
