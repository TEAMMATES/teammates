$(document).ready(function(){
    toggleSort($("#button_sortcourseid"),1);
});


/**
 * Pre-processes and validates the user's input.
 * @returns {Boolean} True if it is OK to proceed with the form submission.
 */
function verifyCourseData() {
    var courseID = $("#"+COURSE_ID).val();
    var courseName = $("#"+COURSE_NAME).val();
    
    var allErrorMessage = "";
    
    allErrorMessage += checkAddCourseParam(courseID, courseName);
    if(allErrorMessage.length>0){
        setStatusMessage(allErrorMessage,true);
        return false;
    }
    
    return true;
}

/**
 * Checks the validity of the two parameters: course ID, course name.
 * @returns {String} A message describing reasons why the input is invalid.
 * Returns an empty string if all three inputs are valid.
 */
function checkAddCourseParam(courseId, courseName) {
    var errorMessages = "";
    errorMessages += getCourseIdInvalidityInfo(courseId);
    errorMessages += getCourseNameInvalidityInfo(courseName);
    
    return errorMessages;	
}

function getCourseIdInvalidityInfo(courseId){
    
    var invalidityInfo = "";
    
    courseId = courseId.trim();
    
    if (courseId === "") {
        invalidityInfo = DISPLAY_COURSE_COURSE_ID_EMPTY + "<br>";
    } else {
        // long courseId
        if(courseId.length > COURSE_ID_MAX_LENGTH) {
            invalidityInfo += DISPLAY_COURSE_LONG_ID + "<br>";
        }
        
        // invalid courseId
        if (!isCourseIDValidChars(courseId)) {
            invalidityInfo += DISPLAY_COURSE_INVALID_ID + "<br>";
        }
    }
    
    return invalidityInfo;
}


function getCourseNameInvalidityInfo(courseName){
    
    courseName = courseName.trim();
    
    if (courseName === "") {
        return DISPLAY_COURSE_COURSE_NAME_EMPTY + "<br>";
    } else if (courseName.length > COURSE_NAME_MAX_LENGTH) {
        return DISPLAY_COURSE_LONG_NAME + "<br>";
    }
    
    return "";
}

/**
 * Checks if the parameter consists of alpha-numerics and characters {@code -._$} only.
 * @returns {Boolean} true if the courseId does not contain any unacceptable characters.
 */
function isCourseIDValidChars(courseId) {
    return courseId.match(/^[a-zA-Z_$0-9.-]+$/) !== null;
}


