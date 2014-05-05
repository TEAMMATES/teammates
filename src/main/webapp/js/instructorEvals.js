
function isEvaluationNameLengthValid(name) {
    //Constant is kept in Common.java file, but checking is done in Javascript
    return name.length <= EVAL_NAME_MAX_LENGTH;
}

function isEvaluationNameValid(name) {
    if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
            || name.indexOf("\"") >= 0) {
        return false;
    }
    if (name.match(/^[a-zA-Z0-9 ]*$/) == null) {
        return false;
    }
    return true;
}

function convertDateFromDDMMYYYYToMMDDYYYY(dateString) {
    return dateString.substring(3, 5) + "/" +
            dateString.substring(0, 2) + "/" +
            dateString.substring(6, 10);
}

function isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime) {
    start = convertDateFromDDMMYYYYToMMDDYYYY(start);
    deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);

    var now = new Date();

    start = new Date(start);
    deadline = new Date(deadline);

    // If the hour value is 24, then set time to 23:59
    if (startTime != 24) {
        start.setHours(startTime);
    } else {
        start.setHours(23);
        start.setMinutes(59);
    }
    if (deadlineTime != 24) {
        deadline.setHours(deadlineTime);
    } else {
        deadline.setHours(23);
        deadline.setMinutes(59);
    }

    if (start > deadline) {
        return false;
    } else if (now > start) {
        return false;
    } else if (!(start > deadline || deadline > start)) {
        if (startTime >= deadlineTime) {
            return false;
        }
    }

    return true;
}

function isEditEvaluationScheduleValid(start, startTime, deadline,
        deadlineTime, timeZone, activated, status) {
    start = convertDateFromDDMMYYYYToMMDDYYYY(start);
    deadline = convertDateFromDDMMYYYYToMMDDYYYY(deadline);

    var now = new Date();

    start = new Date(start);
    deadline = new Date(deadline);

    // If the hour value is 24, then set time to 23:59
    if (startTime != 24) {
        start.setHours(startTime);
    } else {
        start.setHours(23);
        start.setMinutes(59);
    }
    if (deadlineTime != 24) {
        deadline.setHours(deadlineTime);
    } else {
        deadline.setHours(23);
        deadline.setMinutes(59);
    }

    if (start.getTime() >= deadline.getTime()) {
        return false;
    } else if (status == "AWAITING") {
        // Open evaluation should be done by system only.
        // Thus, instructor cannot change evaluation ststus from AWAITING to
        // OPEN
        if (start < now) {
            return false;
        }
    }
    return true;
}

/**
 * Check whether the evaluation input (which is passed as a form) is valid
 * @param form
 * @returns {Boolean}
 */
function checkAddEvaluation(form){
    var courseID = form.courseid.value;
    var name = form.evaluationname.value;
    var commentsEnabled = form.commentsstatus.value;
    var start = form.start.value;
    var startTime = form.starttime.value;
    var deadline = form.deadline.value;
    var deadlineTime = form.deadlinetime.value;
    var timeZone = form.timezone.value;
    var gracePeriod = form.graceperiod.value;
    var instructions = form.instr.value;

    if (courseID == "" || name == "" || start == "" || startTime == ""
        || deadline == "" || deadlineTime == "" || timeZone == ""
            || gracePeriod == "" || instructions == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
        return false;
    } else if (!isEvaluationNameValid(name)) {
        setStatusMessage(DISPLAY_EVALUATION_NAMEINVALID, true);
        return false;
    } else if (!isEvaluationNameLengthValid(name)) {
        setStatusMessage(DISPLAY_EVALUATION_NAME_LENGTHINVALID, true);
        return false;
    } else if (!isAddEvaluationScheduleValid(start, startTime, deadline, deadlineTime)) {
        setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID, true);
        return false;
    }
    return true;
}

/**
 * Check whether the evaluation input (which is passed as a form) is valid
 * @param form
 * @returns {Boolean}
 */
function checkEditEvaluation(form){
    var courseID = form.courseid.value;
    var name = form.evaluationname.value;
    var commentsEnabled = form.commentsstatus.value;
    var start = form.start.value;
    var startTime = form.starttime.value;
    var deadline = form.deadline.value;
    var deadlineTime = form.deadlinetime.value;
    var timeZone = form.timezone.value;
    var gracePeriod = form.graceperiod.value;
    var instructions = form.instr.value;

    if (courseID == "" || name == "" || start == "" || startTime == ""
        || deadline == "" || deadlineTime == "" || timeZone == ""
            || gracePeriod == "" || instructions == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
        return false;
    } else if (!isEvaluationNameValid(name)) {
        setStatusMessage(DISPLAY_EVALUATION_NAMEINVALID, true);
        return false;
    } else if (!isEvaluationNameLengthValid(name)) {
        setStatusMessage(DISPLAY_EVALUATION_NAME_LENGTHINVALID, true);
        return false;
    } else if (!isEditEvaluationScheduleValid(start, startTime, deadline, deadlineTime)) {
        setStatusMessage(DISPLAY_EVALUATION_SCHEDULEINVALID, true);
        return false;
    }
    return true;
}

/**
 * To be run on page finish loading, this will select the input: start date,
 * start time, and timezone based on client's time.
 */
function selectDefaultTimeOptions(){
    var now = new Date();

    var currentDate = convertDateToDDMMYYYY(now);
    var hours = convertDateToHHMM(now).substring(0, 2);
    var currentTime = (parseInt(hours) + 1) % 24;
    var timeZone = -now.getTimezoneOffset() / 60;

    document.getElementById(EVALUATION_START).value = currentDate;
    document.getElementById(EVALUATION_STARTTIME).value = currentTime;
    document.getElementById(EVALUATION_TIMEZONE).value = ""+timeZone;
}


/**
 * Format a number to be two digits
 */
function formatDigit(num){
    return (num<10?"0":"")+num;
}

/**
 * Format a date object into DD/MM/YYYY format
 * @param date
 * @returns {String}
 */
function convertDateToDDMMYYYY(date) {
    return formatDigit(date.getDate()) + "/" +
            formatDigit(date.getMonth()+1) + "/" +
            date.getFullYear();
}

/**
 * Format a date object into HHMM format
 * @param date
 * @returns {String}
 */
function convertDateToHHMM(date) {
    return formatDigit(date.getHours()) + formatDigit(date.getMinutes());
}