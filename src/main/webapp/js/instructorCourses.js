$(document).ready(function(){
    $('#ajaxForCourses').trigger('submit');
    if (typeof moment !== 'undefined') {
        prepareTimeZoneInput();
        autoDetectTimeZone();
    }
});

/**
 * Generate time zone <options> using time zone IDs from
 * Moment-Timezone library.
 */
function prepareTimeZoneInput() {
    var selectElement = document.getElementById(COURSE_TIME_ZONE);
    moment.tz.names().forEach(function(name) {
        var o = document.createElement("option");
        o.text = name;
        o.value = name;
        console.log(name);
        selectElement.appendChild(o);
    });
    $("#" + COURSE_TIME_ZONE).selectize({selectOnTab: true});
}

function autoDetectTimeZone() {
    var detectedTimeZone = moment.tz.guess();
    getSelectizeInstance("#" + COURSE_TIME_ZONE).setValue(detectedTimeZone);
}

/**
 * Pre-processes and validates the user's input.
 * @returns {Boolean} True if it is OK to proceed with the form submission.
 */
function verifyCourseData() {
    var courseID = $("#"+COURSE_ID).val();
    var courseName = $("#"+COURSE_NAME).val();
    var courseTimeZone = getSelectizeInstance("#" + COURSE_TIME_ZONE).getValue();
    
    var allErrorMessage = "";
    
    allErrorMessage += checkAddCourseParam(courseID, courseName, courseTimeZone);
    if(allErrorMessage.length>0){
        setStatusMessage(allErrorMessage, StatusType.DANGER);
        return false;
    }
    
    return true;
}

/**
 * Checks the validity of the three parameters: course ID, course name,
 * course time zone.
 * @returns {String} A message describing reasons why the input is invalid.
 * Returns an empty string if all three inputs are valid.
 */
function checkAddCourseParam(courseId, courseName, courseTimeZone) {
    var errorMessages = "";
    errorMessages += getCourseIdInvalidityInfo(courseId);
    errorMessages += getCourseNameInvalidityInfo(courseName);
    errorMessages += getCourseTimeZoneInvalidityInfo(courseTimeZone);
    
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

function getCourseTimeZoneInvalidityInfo(courseTimeZone) {
    if (moment.tz.names().indexOf(courseTimeZone) === -1) {
        return DISPLAY_COURSE_INVALID_TIME_ZONE + "<br>";
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

/**
 * Gets the selectize.js instance on element specified by selector
 */
function getSelectizeInstance(selector) {
    return $(selector)[0].selectize;
}

