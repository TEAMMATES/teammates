//TODO: Move constants from Common.js into appropriate files if not shared.

var modalSelectedRow;

function isFeedbackSessionNameValid(name) {
    if (name.indexOf("\\") >= 0 || name.indexOf("'") >= 0
            || name.indexOf("\"") >= 0) {
        return false;
    }
    if (name.match(/^[a-zA-Z0-9 ]*$/) == null) {
        return false;
    }
    return true;
}

function isFeedbackSessionNameLengthValid(name) {
    //Constant is kept in Common.java file, but checking is done in Javascript
    return name.length <= EVAL_NAME_MAX_LENGTH;
}

function isAddFeedbackSessionValid(startdate, startTime, enddate, endtime) {
    startdate = convertDateFromDDMMYYYYToMMDDYYYY(startdate);
    enddate = convertDateFromDDMMYYYYToMMDDYYYY(enddate);

    var now = new Date();

    startdate = new Date(startdate);
    enddate = new Date(enddate);

    // If the hour value is 24, then set time to 23:59
    if (startTime != 24) {
        startdate.setHours(startTime);
    } else {
        startdate.setHours(23);
        startdate.setMinutes(59);
    }
    if (endtime != 24) {
        enddate.setHours(endtime);
    } else {
        enddate.setHours(23);
        enddate.setMinutes(59);
    }

    if (startdate > enddate) {
        return false;
    } else if (now > startdate) {
        return false;
    } else if (!(startdate > enddate || enddate > startdate)) {
        if (startTime >= endtime) {
            return false;
        }
    }

    return true;
}

function isFeedbackSessionInstructionsLengthValid(instructions) {
    return instructions.length <= EVAL_INSTRUCTIONS_MAX_LENGTH;
}

/**
 * Check whether the feedback question input is valid
 * @param form
 * @returns {Boolean}
 */
function checkFeedbackQuestion(form) {
    var recipientType =
        $(form).find('select[name|='+FEEDBACK_QUESTION_RECIPIENTTYPE+']').find(":selected").val();
    if(recipientType == "STUDENTS" || recipientType == "TEAMS") {
        if($(form).find('[name|='+FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE+']:checked').val() == "custom" &&
                $(form).find('.numberOfEntitiesBox').val() == "") {
            setStatusMessage(DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID,true);
            return false;
        }
    }
    if ($(form).find('[name='+FEEDBACK_QUESTION_TEXT+']').val() == "") {
        setStatusMessage(DISPLAY_FEEDBACK_QUESTION_TEXTINVALID,true);
        return false;
    }
    if ($(form).find('[name='+FEEDBACK_QUESTION_TYPE+']').val() == "NUMSCALE") {
        if( $(form).find('[name='+FEEDBACK_QUESTION_NUMSCALE_MIN+']').val() == "" || 
                $(form).find('[name='+FEEDBACK_QUESTION_NUMSCALE_MAX+']').val() == ""||
                $(form).find('[name='+FEEDBACK_QUESTION_NUMSCALE_STEP+']').val() == "") {
            setStatusMessage(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID,true);
            return false;
        }
        var qnNum = ($(form).attr('name')=='form_addquestions') ? -1 : parseInt($(form).attr('id').substring("form_editquestion-".length),$(form).attr('id').length);
        if(updateNumScalePossibleValues(qnNum)){
            return true;
        } else {
            setStatusMessage(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID,true);
            return false;
        }
    }

    return true;
}

/**
 * Check whether the feedback session input (which is passed as a form) is valid
 * @param form
 * @returns {Boolean}
 */
function checkAddFeedbackSession(form){	
    var courseID = form.courseid.value;
    var timezone = form.timezone.value;
    var fsname = form.fsname.value;
    var startdate = form.startdate.value;
    var startTime = form.starttime.value;
    var enddate = form.enddate.value;
    var endtime = form.endtime.value;
    var gracePeriod = form.graceperiod.value;
    var publishtime = form.publishtime.value;
    var instructions = form.instructions.value;

    if (fsname == "" || courseID == "" || timezone == "" || startdate ==""
        || starttime == "" ||instructions == null || gracePeriod == "" || publishtime == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
        return false;
    }else if (!isFeedbackSessionNameValid(fsname)) {
        setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAMEINVALID, true);
        return false;
    } else if (!isFeedbackSessionNameLengthValid(fsanme)) {
        setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_LENGTHINVALID, true);
        return false;
    } else if (!isAddFeedbackSessionValid(startdate, startTime, enddate, endtime)) {
        setStatusMessage(DISPLAY_FEEDBACK_SESSION_SCHEDULEINVALID, true);
        return false;
    } else if (!isFeedbackSessionInstructionsLengthValid(instructions)) {
        setStatusMessage(DISPLAY_FEEDBACK_SESSION_INSTRUCTIONS_LENGTHINVALID, true);
        return false;
    }
    return true;
}


function checkEditFeedbackSession(form){
    if(form.visibledate.getAttribute("disabled") != ""){
        if(form.visibledate.value == ""){
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID, true);
            return false;
        }
    }
    if(form.publishdate.getAttribute("disabled") != ""){
        if(form.publishdate.value == ""){
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID, true);
            return false;
        }
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

    document.getElementById(FEEDBACK_SESSION_STARTDATE).value = currentDate;
    document.getElementById(FEEDBACK_SESSION_STARTTIME).value = currentTime;
    document.getElementById(FEEDBACK_SESSION_TIMEZONE).value = ""+timeZone;
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

function bindCopyButton() {
    $('#button_copy').on('click', function(e){
        e.preventDefault();
        var selectedCourseId = $("#" + COURSE_ID + " option:selected").text();
        var newFeedbackSessionName = $("#" + FEEDBACK_SESSION_NAME).val();
        
        var isExistingSession = false;

        var sessionsList = $("tr[id^='session']");
        if(sessionsList.length == 0){
            setStatusMessage(FEEDBACK_SESSION_COPY_INVALID, true);
            return false;
        } 

        $(sessionsList).each(function(){
            var cells = $(this).find("td");
            var courseId = $(cells[0]).text();
            var feedbackSessionName = $(cells[1]).text();
            if(selectedCourseId == courseId && newFeedbackSessionName == feedbackSessionName){
                isExistingSession = true;
                return false;
            }
        });

        if(isExistingSession){
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE, true);
        } else {
            setStatusMessage("", false);

            var firstSession = $(sessionsList[0]).find("td");
            var firstSessionCourseId = $(firstSession[0]).text();
            var firstSessionName = $(firstSession[1]).text();

            $('#copyModal').modal('show');
            $('#modalCopiedSessionName').val(newFeedbackSessionName.trim());
            $('#modalCopiedCourseId').val(selectedCourseId.trim());
            if($('#modalCourseId').val().trim() == ""){
                $('#modalCourseId').val(firstSessionCourseId);
            }
            if($('#modalSessionName').val().trim() == ""){
                $('#modalSessionName').val(firstSessionName);
            }
        }

        return false;
    });

    $('#button_copy_submit').on('click', function(e){
        e.preventDefault();

        var newFeedbackSessionName = $('#modalCopiedSessionName').val();

        if(newFeedbackSessionName.trim() == ""){
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_EMPTY, true);
            $('#copyModal').modal('hide');
            return false;
        } else if (!isFeedbackSessionNameValid(newFeedbackSessionName)) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAMEINVALID, true);
            $('#copyModal').modal('hide');
            return false;
        } else if (!isFeedbackSessionNameLengthValid(newFeedbackSessionName)) {
            setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_LENGTHINVALID, true);
            $('#copyModal').modal('hide');
            return false;
        }

        $('#copyModalForm').submit();

        return false;
    });
}

var numRowsSelected = 0;

function bindCopyEvents() {

    $('#copyTableModal >tbody>tr').on('click', function(e){
        e.preventDefault();
        var cells = $(this).find("td");
        var courseId = $(cells[1]).text();
        var feedbackSessionName = $(cells[2]).text();
        $('#modalSessionName').val(feedbackSessionName.trim());
        $('#modalCourseId').val(courseId.trim());

        if(typeof modalSelectedRow != 'undefined'){
            $(modalSelectedRow).removeClass('row-selected');
            $($(modalSelectedRow).find("td")[0]).html('<input type="radio">');
            numRowsSelected--;
        }

        modalSelectedRow = this;
        $(modalSelectedRow).addClass('row-selected');
        $($(modalSelectedRow).find("td")[0]).html('<input type="radio" checked="checked">');
        numRowsSelected++;

        if(numRowsSelected > 0){
            $('#button_copy_submit').prop('disabled', false);
        } else {
            $('#button_copy_submit').prop('disabled', true);
        }

        return false;
    });
}

function readyFeedbackPage() {
    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();
    collapseIfPrivateSession();
    bindCopyButton();
    bindCopyEvents();

    window.doPageSpecificOnload = selectDefaultTimeOptions();

    bindUncommonSettingsEvents();
    updateUncommonSettingsInfo();
    hideUncommonPanels();
}

function bindUncommonSettingsEvents(){
    $('#editUncommonSettingsButton').click(uncommonSettingsButtonClick);
}

function updateUncommonSettingsInfo(){
    var info = "Session is visible at submission opening time, responses are only visible when you publish the results.<br>" +
                "Emails are sent when session opens (within 15 mins), 24 hrs before session closes and when results are published.";

    $('#uncommonSettingsInfoText').html(info);
}

function uncommonSettingsButtonClick(){
    var button = $('#editUncommonSettingsButton');
    var button_edit = $(button).attr('data-edit');
    if($(button).text() == button_edit){
        showUncommonPanels();
        $('#uncommonSettingsInfo').hide();
    }
}

function isDefaultSetting(){
    if ($('#sessionVisibleFromButton_atopen').prop('checked') &&
        $('#resultsVisibleFromButton_later').prop('checked') &&
        $('#sendreminderemail_open').prop('checked') &&
        $('#sendreminderemail_closing').prop('checked') &&
        $('#sendreminderemail_published').prop('checked')){
        return true;
    } else {
        return false;   
    }
}

function showUncommonPanels(){
    //Hide panels only if they do not match the default values.
    if(isDefaultSetting()){
        $('#sessionResponsesVisiblePanel').show();
        $('#sendEmailsForPanel').show();
    } else {
        $('#uncommonSettingsInfo').hide();
    }
}

function hideUncommonPanels(){
    //Hide panels only if they do not match the default values.
    if(isDefaultSetting()){
        $('#sessionResponsesVisiblePanel').hide();
        $('#sendEmailsForPanel').hide();
    } else {
        $('#uncommonSettingsInfo').hide();
    }
}

/**
 * Hides / shows the "Submissions Opening/Closing Time" and "Grace Period" options 
 * depending on whether a private session is selected.<br>
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatSessionVisibilityGroup() {
    var $sessionVisibilityBtnGroup = $('[name='+FEEDBACK_SESSION_SESSIONVISIBLEBUTTON+']');
    $sessionVisibilityBtnGroup.change(function() {
        collapseIfPrivateSession();		
        if ($sessionVisibilityBtnGroup.filter(':checked').val() == "custom") {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, false);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, false);
        } else {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, true);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, true);
        }
    });
}

/**
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatResponsesVisibilityGroup() {
    var $responsesVisibilityBtnGroup = $('[name='+FEEDBACK_SESSION_RESULTSVISIBLEBUTTON+']');
    
    $responsesVisibilityBtnGroup.change(function() {
        if ($responsesVisibilityBtnGroup.filter(':checked').val() == "custom") {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, false);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, false);
        } else {
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, true);
            toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, true);
        }
    });
}

/**
 * Saves the (disabled) state of the element in attribute data-last.<br>
 * Toggles whether the given element {@code id} is disabled or not based on
 * {@code bool}.<br>
 * Disabled if true, enabled if false.
 */
function toggleDisabledAndStoreLast(id, bool) {
    $('#'+id).prop('disabled', bool);
    $('#'+id).data('last',$('#'+id).prop('disabled'));
}

/**
 * Collapses/hides unnecessary fields/cells/tables if private session option is selected.
 */
function collapseIfPrivateSession() {
    if ($('[name='+FEEDBACK_SESSION_SESSIONVISIBLEBUTTON+']').filter(':checked').val() == "never") {
        $('#timeFramePanel').hide();
        $('#instructionsRow').hide();
        $('#responsesVisibleFromColumn').hide();
    } else {
        $('#timeFramePanel').show();
        $('#instructionsRow').show();
        $('#responsesVisibleFromColumn').show();
    }
}
