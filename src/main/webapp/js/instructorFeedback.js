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
 * Check whether the evaluation input (which is passed as a form) is valid
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
	var sessionVisibleDate = form.visibledate.value;
	var sessionVisibleTime = form.visibletime.value;
	var resultsVisibleDate = form.publishdate.value;
	var resultsVisibleTime = form.publishtime.value;
	var sessionVisibleType = form.elements.sessionVisibleFromButton.value;
	var resultsVisibleType = form.elements.resultsVisibleFromButton.value;
	var timeZone = form.timezone.value;
	var gracePeriod = form.graceperiod.value;

	if (courseId == "" || name == "" || startDate == "" || startTime == ""
		|| endDate == "" || endTime == "" || timeZone == "" || gracePeriod == "") {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (sessionVisibleType == "custom" && (sessionVisibleDate == "" || sessionVisibleTime == "")) {
		setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
		return false;
	} else if (resultsVisibleType == "custom" && (resultsVisibleDate == "" || resultsVisibleTime == "")) {
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

/**
 * Enables question fields and "save changes" button for the given question number,
 * and hides the edit link. Does the opposite for all other questions.
 * @param number
 */
function enableEdit(qnNumber, maxQuestions) {
	var i = 1;
	while (i < maxQuestions+1) {
		if (qnNumber == i) {
			enableQuestion(i);
		} else {
			disableQuestion(i);
		}
		i++;
	}
	
	return false;
}

/**
 * Enables question fields and "save changes" button for the given question number,
 * and hides the edit link.
 * @param number
 */
function enableQuestion(number){
	$('input.visibilityCheckbox[class$='+number+']').not('[name="receiverFollowerCheckbox"]').each(function(){this.disabled = '';});
	document.getElementById(FEEDBACK_QUESTION_TEXT+'-'+number).disabled = '';
	document.getElementById(FEEDBACK_QUESTION_GIVERTYPE+'-'+number).disabled = '';
	document.getElementById(FEEDBACK_QUESTION_RECIPIENTTYPE+'-'+number).disabled = '';
	document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+'-'+number).disabled = '';
	document.getElementById(FEEDBACK_QUESTION_EDITTEXT+'-'+number).style.display = 'none';
	document.getElementById(FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).style.display = '';
	document.getElementById('button_question_submit-'+number).style.display = '';
	document.getElementById(FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="edit";
}

/**
 * Disable question fields and "save changes" button for the given question number,
 * and shows the edit link.
 * @param number
 */
function disableQuestion(number){
	$('input.visibilityCheckbox[class$='+number+']').each(function(){this.disabled = 'disabled';});
	document.getElementById(FEEDBACK_QUESTION_TEXT+'-'+number).disabled = 'disabled';
	document.getElementById(FEEDBACK_QUESTION_GIVERTYPE+'-'+number).disabled = 'disabled';
	document.getElementById(FEEDBACK_QUESTION_RECIPIENTTYPE+'-'+number).disabled = 'disabled';
	document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+'-'+number).disabled = 'disabled';
	document.getElementById(FEEDBACK_QUESTION_EDITTEXT+'-'+number).style.display = '';
	document.getElementById(FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).style.display = 'none';
	document.getElementById('button_question_submit-'+number).style.display = 'none';
}

/**
 * Pops up confirmation dialog whether to delete specified question
 * @param question number
 * @returns
 */
function deleteQuestion(number){
	if (confirm("Are you sure you want to delete this question?")){
		document.getElementById(FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="delete"; 
		document.getElementById('form_editquestion-'+number).submit();
		return true;
	} else {
		return false;
	}
}

/**
 * Hides the "Number of Recipients Box" of the question 
 * where participant type is not STUDENTS OR TEAMS.
 * @param value
 * @returns qnNumber
 */
function formatNumberBox(value, qnNumber){
	if (value == "STUDENTS" || value == "TEAMS") {
		document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+"-"+qnNumber).style.display = '';
		document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text-"+qnNumber).style.display = '';
		if(value == "STUDENTS") {
			document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text_inner-"+qnNumber).innerHTML = "students";
		} else {
			document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text_inner-"+qnNumber).innerHTML = "teams";
		}
	} else {
		document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+"-"+qnNumber).style.display = 'none';
		document.getElementById(FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text-"+qnNumber).style.display = 'none';
	}
}

/**
 * Pushes the values of all checked check boxes for the specified question
 * into the appropriate feedback question parameters.
 * @returns qnNumber
 */
function tallyCheckboxes(qnNumber){
	var checked = [];
	$('.answerCheckbox'+qnNumber+':checked').each(function () {
	    checked.push($(this).val());
	});
	$("[name="+FEEDBACK_QUESTION_SHOWRESPONSESTO+"]").val(checked.toString());
	checked = [];
	$('.giverCheckbox'+qnNumber+":checked").each(function () {
		 checked.push($(this).val());
	});
	$("[name="+FEEDBACK_QUESTION_SHOWGIVERTO+"]").val(checked.toString());
	checked = [];
	$('.recipientCheckbox'+qnNumber+':checked').each(function () {
		 checked.push($(this).val());
	});
	$("[name="+FEEDBACK_QUESTION_SHOWRECIPIENTTO+"]").val(checked.toString());
}

/**
 * Shows the new question div frame and scrolls to it
 */
function showNewQuestionFrame(){
	$('#questionTableNew').show();
	$('#button_openframe').hide();
	$('#empty_message').hide(); 
    $('#frameBody').animate({scrollTop: $('#frameBody')[0].scrollHeight}, 1000);
}

/**
 * Binds each question's check box field such that the user
 * cannot select an invalid combination.
 */
function formatCheckBoxes() {
	$(document).ready(function() {
		// TODO: change class -> name?
		$("input[class*='answerCheckbox']").change(function() {
			if ($(this).prop('checked') == false) {
				$(this).parent().parent().find("input[class*='giverCheckbox']").prop('checked',false);
				$(this).parent().parent().find("input[class*='recipientCheckbox']").prop('checked',false);
			}
		});
		$("input[class*='giverCheckbox']").change(function() {
			if ($(this).is(':checked')) {
				$query = $(this).parent().parent().find("input[class*='answerCheckbox']");
				$query.prop('checked',true);
				$query.trigger('change');
			}
		});
		$("input[class*='recipientCheckbox']").change(function() {
			if ($(this).is(':checked')) {
				$(this).parent().parent().find("input[class*='answerCheckbox']").prop('checked',true);
			}
		});
		$("input[name=receiverLeaderCheckbox]").change(function (){
			$(this).parent().parent().find("input[name=receiverFollowerCheckbox]").
									prop('checked', $(this).prop('checked'));
		});
	});
}