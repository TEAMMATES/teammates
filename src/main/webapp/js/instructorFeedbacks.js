//TODO: Move constants from Common.js into appropriate files if not shared.

function isFeedbackSessionNameLengthValid(name) {
	//Constant is kept in Common.java file, but checking is done in Javascript
	return name.length <= FEEDBACK_SESSION_NAME_MAX_LENGTH;
}

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

/**
 * Check whether the parameters of a new feedback session (which is passed as a form) is valid.
 * @param form
 * @returns {Boolean}
 */
function checkAddFeedbackSession(form){
	var courseId = form.courseid.value;
	var name = form.fsname.value;
	var startDate = form.startdate.value;
	var startTime = form.starttime.value;
	var endDate = form.enddate.value;
	var endTime = form.endtime.value;
	var sessionVisibility = $('input:radio[name='+FEEDBACK_SESSION_SESSIONVISIBLEBUTTON+']:checked').val();
	var resultsVisibility = $('input:radio[name='+FEEDBACK_SESSION_RESULTSVISIBLEBUTTON+']:checked').val();
	var sessionDate = $('#'+FEEDBACK_SESSION_VISIBLEDATE).val();
	var sessionTime = $('#'+FEEDBACK_SESSION_VISIBLETIME).val();
	var resultsDate = $('#'+FEEDBACK_SESSION_PUBLISHDATE).val();
	var resultsTime = $('#'+FEEDBACK_SESSION_PUBLISHTIME).val();
	var timeZone = form.timezone.value;
	var gracePeriod = form.graceperiod.value;
	var instructions = form.instructions.value;

	if (courseId == "" || name == "" || timeZone == "") {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (sessionVisibility != "never" && 
			(startDate == "" || startTime == "" || endDate == "" || endTime == "" ||
			 gracePeriod == "" || instructions == "")){
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (sessionVisibility == "custom" && (sessionDate == "" || sessionTime == "")) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (resultsVisibility == "custom" && (resultsDate == "" || resultsTime == "")) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (!isFeedbackSessionNameValid(name)) {
		setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAMEINVALID, true);
		return false;
	} else if (!isFeedbackSessionNameLengthValid(name)) {
		setStatusMessage(DISPLAY_FEEDBACK_SESSION_NAME_LENGTHINVALID, true);
		return false;
	}
	return true;
}

/**
 * Check whether the edited feedback session parameter inputs (which is passed as a form) are valid.
 * @returns {Boolean}
 */
function checkEditFeedbackSession(){
	var startDate = $('#startdate').val();
	var startTime = $('#starttime').val();
	var endDate = $('#enddate').val();
	var endTime = $('#endtime').val();
	var gracePeriod = $('#graceperiod').val();
	var instructions = $('#instructions').val();
	var sessionVisibility = $('input:radio[name='+FEEDBACK_SESSION_SESSIONVISIBLEBUTTON+']:checked').val();
	var resultsVisibility = $('input:radio[name='+FEEDBACK_SESSION_RESULTSVISIBLEBUTTON+']:checked').val();
	var sessionDate = $('#'+FEEDBACK_SESSION_VISIBLEDATE).val();
	var sessionTime = $('#'+FEEDBACK_SESSION_VISIBLETIME).val();
	var resultsDate = $('#'+FEEDBACK_SESSION_PUBLISHDATE).val();
	var resultsTime = $('#'+FEEDBACK_SESSION_PUBLISHTIME).val();
	
	if (sessionVisibility != "never" && 
			(startDate == "" || startTime == "" || endDate == "" || endTime == "" ||
			 gracePeriod == "" || instructions == "")) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (sessionVisibility == "custom" && (sessionDate == "" || sessionTime == "")) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (resultsVisibility == "custom" && (resultsDate == "" || resultsTime == "")) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	}
	return true;
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

function readyFeedbackPage (){
	
	// Defaults
	toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLEDATE, true);
	toggleDisabledAndStoreLast(FEEDBACK_SESSION_VISIBLETIME, true);
	toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHDATE, true);
	toggleDisabledAndStoreLast(FEEDBACK_SESSION_PUBLISHTIME, true);
	
    $("select#"+FEEDBACK_SESSION_CHANGETYPE).change(function (){
    	document.location.href = $(this).val();
    });

    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();
	
    window.doPageSpecificOnload = selectDefaultTimeOptions();
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
		$('#timeFrameTable').hide();
		$('#instructionsTable').find('tr:first').hide();
		if($('#instructionsTable').find(':visible').size()==0) {
			$('#instructionsTable').hide();
		}
		$('#response_visible_from_row').hide();
	} else {
		$('#timeFrameTable').show();
		$('#instructionsTable').show();
		$('#instructionsTable').find('tr:first').show();
		$('#response_visible_from_row').show();
	}
}