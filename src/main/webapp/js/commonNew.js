var COURSE_ID_MAX_LENGTH = 21;
var COURSE_NAME_MAX_LENGTH = 38;
var EVAL_NAME_MAX_LENGTH = 38;

// Field names
var COURSE_ID = "courseid"; // Used in coordCourse.js
var COURSE_NAME = "coursename"; // Used in coordCourse.js

// Display messages
// Used in coordCourseEnroll.js only
var DISPLAY_ENROLLMENT_FIELDS_EXTRA = "There are too many fields.";
var DISPLAY_ENROLLMENT_FIELDS_MISSING = "There are missing fields.";
var DISPLAY_STUDENT_EMAIL_INVALID = "The e-mail address is invalid.";
// Below two are used in helperNew.js as well
var DISPLAY_STUDENT_NAME_INVALID = "Name should only consist of alphanumerics and not<br />more than 40 characters.";
var DISPLAY_STUDENT_TEAMNAME_INVALID = "Team name should contain less than 25 characters.";

// Used in coordCourse.js only
var DISPLAY_COURSE_EMPTY = "Course ID and Course Name are compulsory fields.";
var DISPLAY_COURSE_LONG_ID = "Course ID should not exceed " + COURSE_ID_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_LONG_NAME = "Course name should not exceed " + COURSE_NAME_MAX_LENGTH + " characters.";
var DISPLAY_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";

// Used in coordEval.js only
var DISPLAY_EVALUATION_NAMEINVALID = "Please use only alphabets, numbers and whitespace in evaluation name.";
var DISPLAY_EVALUATION_NAME_LENGTHINVALID = "Evaluation name should not exceed 38 characters.";
var DISPLAY_EVALUATION_SCHEDULEINVALID = "The evaluation schedule (start/deadline) is not valid.<br />" +
										 "The start time should be in the future, and the deadline should be after start time.";
var DISPLAY_FIELDS_EMPTY = "Please fill in all the relevant fields.";