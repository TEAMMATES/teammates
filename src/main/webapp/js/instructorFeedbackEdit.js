/**
 * This function is called on edit page load.
 */
function readyFeedbackEditPage(){
	// Hide option tables
	$('.visibilityOptions').hide();
	
	// Bind submit text links
	$('#fsSaveLink').click(function(){
		$('#form_editfeedbacksession').submit();
	});
	$('a[id|=questionsavechangestext]').click(function(){
		$(this).parents('form.form_question').submit();
	});
	
	// Bind submit actions
	$('form[id|=form_editquestion]').submit(function(event) {
		if($(this).attr('editStatus') == "mustDeleteResponses") {
			if (confirm("Editing these fields will result in all existing responses for" +
					" this question to be deleted. Are you sure you want to continue?") == false) {
				event.stopImmediatePropagation();
				return false;
			}
		}
	});
	$('form.form_question').submit(function(){
		return checkFeedbackQuestion(this);		
	});

	// Bind destructive changes
	$('form[id|=form_editquestion]').find(":input").not('.nonDestructive').change(function() {
		var editStatus = $(this).parents('form').attr('editStatus');
		if(editStatus == "hasResponses") {
			$(this).parents('form').attr('editStatus', "mustDeleteResponses");
		}
	});
	
	// Additional formatting & bindings.
	disableEditFS();
	formatSessionVisibilityGroup();
	formatResponsesVisibilityGroup();
	formatNumberBoxes();
	formatCheckBoxes();
	formatQuestionNumbers();
	collapseIfPrivateSession();
	document.onmousemove = positiontip;
}

/**
 * Disables the editing of feedback session details.
 */
function disableEditFS(){	
	// Save then disable fields
	getCustomDateTimeFields().each(function(){
		$(this).data('last', $(this).prop('disabled'));
	});
	$('#form_editfeedbacksession').
		find("text,input,button,textarea,select").prop('disabled', true);
}

/**
 * Enables the editing of feedback session details.
 */
function enableEditFS(){
	var $customDateTimeFields = getCustomDateTimeFields();

	$($customDateTimeFields).each(function(){
		$(this).prop('disabled',
				$(this).data('last'));
	});
	$('#form_editfeedbacksession').
		find("text,input,button,textarea,select").
		not($customDateTimeFields).
		prop('disabled', false);
	$('#fsEditLink').hide();
	$('#fsSaveLink').show();
	$('#button_submit_edit').show();
}

function getCustomDateTimeFields(){
	return $('#'+FEEDBACK_SESSION_PUBLISHDATE).
				add('#'+FEEDBACK_SESSION_PUBLISHTIME).
				add('#'+FEEDBACK_SESSION_VISIBLEDATE).
				add('#'+FEEDBACK_SESSION_VISIBLETIME);
}

/**
 * Hides or show visibility checkboxes frame
 * @param elem is the question number.
 */
function toggleVisibilityOptions(elem){
	$options = $(elem).parent().parent().next('.visibilityOptions');
	if($options.is(':hidden')) {
		$options.show();
		$(elem).html("[-] Hide Visibility Options");
	} else {
		$options.hide();
		$(elem).html("[+] Show Visibility Options");
	}
}

/**
 * Enables editing of question fields and enables the "save changes" button for
 * the given question number, while hiding the edit link. Does the opposite for all other questions.
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
	$('#questionTable'+number).find('text,button,textarea,select,input').
		not('[name="receiverFollowerCheckbox"]').
		not('.disabled_radio').
		removeAttr("disabled", "disabled");
	$('#questionTable'+number).find('a').show();
	
	if(!$("#generateOptionsCheckbox-"+number).prop("checked")){
		$("#mcqChoiceTable-"+number).show();
		$("#msqChoiceTable-"+number).show();
	} else {
		$("#mcqChoiceTable-"+number).hide();
		$("#msqChoiceTable-"+number).hide();
	}		
	
	$('#'+FEEDBACK_QUESTION_EDITTEXT+'-'+number).hide();
	$('#'+FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).show();
	$('#'+'button_question_submit-'+number).show();
	$('#'+FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="edit";
	// $('#questionTable'+number).find('.visibilityOptionsLabel').click();
}

/**
 * Disable question fields and "save changes" button for the given question number,
 * and shows the edit link.
 * @param number
 */
function disableQuestion(number){
	$('#questionTable'+number).find('text,button,textarea,select,input').attr("disabled", "disabled");
	$('#questionTable'+number).find('#mcqAddOptionLink').hide();
	$('#questionTable'+number).find('#msqAddOptionLink').hide();
	$('#questionTable'+number).find('.removeOptionLink').hide();
	$('#'+FEEDBACK_QUESTION_EDITTEXT+'-'+number).show();
	$('#'+FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).hide();
	$('#'+'button_question_submit-'+number).hide();
}

/**
 * Pops up confirmation dialog whether to delete specified question
 * @param question number
 * @returns
 */
function deleteQuestion(number){
	if(number == -1){
		location.reload();
		return false;
	} else if (confirm("Are you sure you want to delete this question?")){
		document.getElementById(FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="delete"; 
		document.getElementById('form_editquestion-'+number).submit();
		return true;
	} else {
		return false;
	}
}

/**
 * Formats all questions to hide the "Number of Recipients Box" 
 * when participant type is not STUDENTS OR TEAMS, and show
 * it when it is. Formats the label for the number box to fit
 * the selection as well.
 */
function formatNumberBoxes(){
	
	// Disallow non-numeric entries [Source: http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery]
	$('input.numberOfEntitiesBox').keydown(function(event){
		var key = event.which;
        // Allow: backspace, delete, tab, escape, and enter
        if ( key == 46 || key == 8 || key == 9 || key == 27 || key == 13 || 
             // Allow: Ctrl+A
            (key == 65 && event.ctrlKey === true) || 
             // Allow: home, end, left, right
            (key >= 35 && key <= 39)) {
                 // let it happen, don't do anything
                 return;
        }
        else {
            // Ensure that it is a number and stop the keypress
            if (event.shiftKey || (key < 48 || key > 57) && (key < 96 || key > 105 )) {
                event.preventDefault();
                return false;
            }   
        }		
	});
	
	// Binds onChange of recipientType to modify numEntityBox visibility
	$("select[name="+FEEDBACK_QUESTION_RECIPIENTTYPE+"]").each(function(){
		qnNumber = $(this).prop("id").split('-')[1];
		if(qnNumber === undefined) qnNumber = '';
		value = $(this).val();
		formatNumberBox(value,qnNumber);
		tallyCheckboxes(qnNumber);
	}).change(function() {
		qnNumber = $(this).prop("id").split('-')[1];
		if(qnNumber === undefined) qnNumber = '';
		value = $(this).val();
		formatNumberBox(value,qnNumber);
		tallyCheckboxes(qnNumber);
    });
	
}

/**
 * Hides/shows the "Number of Recipients Box" of the question 
 * depending on the participant type and formats the label text for it.
 * @param value, qnNumber
 */
function formatNumberBox(value, qnNumber) {
	if (value == "STUDENTS" || value == "TEAMS") {
		$("td.numberOfEntitiesElements"+qnNumber).show();
		if(value == "STUDENTS") {
			$("span#"+FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text_inner-"+qnNumber).html("students");
		} else {
			$("span#"+FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text_inner-"+qnNumber).html("teams");
		}
	} else {
		$("td.numberOfEntitiesElements"+qnNumber).hide();
	}
	tallyCheckboxes(qnNumber);
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
function showNewQuestionFrame(type){
	$("[name="+FEEDBACK_QUESTION_TYPE+"]").val(type);
	prepareQuestionForm(type);
	$('#questionTableNew').show();
	$('#addNewQuestionTable').hide();
	$('#empty_message').hide(); 
    $('#frameBody').animate({scrollTop: $('#frameBody')[0].scrollHeight}, 1000);
    copyOptions();
}

function prepareQuestionForm(type) {
	switch(type){
	case "TEXT":
		$("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_TEXT);
		$('#mcqForm').hide();
		$('#msqForm').hide();
		break;
	case "MCQ":
		$("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED).val(2);
		$("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_MCQ);
		$('#mcqForm').show();
		$('#msqForm').hide();
		break;
	case "MSQ":
		$("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED).val(2);
		$("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_MSQ);
		$('#mcqForm').hide();
		$('#msqForm').show();
		break;
	}
}

function addMcqOption(questionNumber) {
	idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
	
	var curNumberOfChoiceCreated = parseInt($("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val());
		
	$("<tr id=\"mcqOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
		+ "<td><input type=\"radio\" disabled=\"disabled\"></td>"
		+ "<td><input type=\"text\" name=\""+FEEDBACK_QUESTION_MCQCHOICE+"-"+curNumberOfChoiceCreated+"\""
		+ " id=\""+FEEDBACK_QUESTION_MCQCHOICE+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"mcqOptionTextBox\">"
		+ "<a href=\"#\" class=\"removeOptionLink\" id=\"mcqRemoveOptionLink\" "
		+ "onclick=\"removeMcqOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
		+ " x</a></td></tr>"
	).insertBefore($("#mcqAddOptionRow" + idSuffix));

	$("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val(curNumberOfChoiceCreated+1);
}

function addMsqOption(questionNumber) {
	idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
	
	var curNumberOfChoiceCreated = parseInt($("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val());
		
	$("<tr id=\"msqOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
		+ "<td><input type=\"checkbox\" disabled=\"disabled\"></td>"
		+ "<td><input type=\"text\" name=\""+FEEDBACK_QUESTION_MSQCHOICE+"-"+curNumberOfChoiceCreated+"\""
		+ " id=\""+FEEDBACK_QUESTION_MSQCHOICE+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"msqOptionTextBox\">"
		+ "<a href=\"#\" class=\"removeOptionLink\" id=\"msqRemoveOptionLink\" "
		+ "onclick=\"removeMsqOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
		+ " x</a></td></tr>"
	).insertBefore($("#msqAddOptionRow" + idSuffix));

	$("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val(curNumberOfChoiceCreated+1);
}

function removeMcqOption(index, questionNumber) {
	idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
	$("#mcqOptionRow-"+index+idSuffix).remove();
}

function removeMsqOption(index, questionNumber) {
	idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
	$("#msqOptionRow-"+index+idSuffix).remove();
}

function toggleMcqGeneratedOptions(checkbox, questionNumber) {
	idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
	
	if (checkbox.checked) {
		$("#mcqChoiceTable"+idSuffix).find("input[type=text]").prop('disabled', true);
		$("#mcqChoiceTable"+idSuffix).hide();
		$("#generatedOptions"+idSuffix).attr("value", "STUDENTS");
	} else {
		$("#mcqChoiceTable"+idSuffix).find("input[type=text]").prop("disabled", false);
		$("#mcqChoiceTable"+idSuffix).show();
		$("#generatedOptions"+idSuffix).attr("value", "NONE");
	}
}

function toggleMsqGeneratedOptions(checkbox, questionNumber) {
	idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
	
	if (checkbox.checked) {
		$("#msqChoiceTable"+idSuffix).find("input[type=text]").prop('disabled', true);
		$("#msqChoiceTable"+idSuffix).hide();
		$("#generatedOptions"+idSuffix).attr("value", "STUDENTS");
	} else {
		$("#msqChoiceTable"+idSuffix).find("input[type=text]").prop("disabled", false);
		$("#msqChoiceTable"+idSuffix).show();
		$("#generatedOptions"+idSuffix).attr("value", "NONE");
	}
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

/**
 * Copy options(Feedback giver, recipient, and all check boxes 
 * from the previous question
 */
function copyOptions() {
	//There's no need to previous question to copy options from.
	if($("table[class*='questionTable']").size() < 2){
		return;
	}
	
	//FEEDBACK GIVER SETUP
	var $prevGiver = $("select[name='givertype']").eq(-2);
	var $currGiver = $("select[name='givertype']").last();
	
	$currGiver.val($prevGiver.val());
	
	//FEEDBACK RECIPIENT SETUP
	var $prevRecipient = $("select[name='recipienttype']").eq(-2);
	var $currRecipient = $("select[name='recipienttype']").last();
	
	$currRecipient.val($prevRecipient.val());
	
	//NUMBER OF RECIPIENT SETUP
	formatNumberBox($currRecipient.val(), '');
	var $prevRadioButtons = $("table[class*='questionTable']").eq(-2).find("input[name='numofrecipientstype']");
	var $currRadioButtons = $("table[class*='questionTable']").last().find("input[name='numofrecipientstype']");
	
	$currRadioButtons.each(function (index){
		$(this).prop('checked', $prevRadioButtons.eq(index).prop('checked'));
	});
	
	var $prevNumOfRecipients = $("input[name='numofrecipients']").eq(-2);
	var $currNumOfRecipients = $("input[name='numofrecipients']").last();
	
	$currNumOfRecipients.val($prevNumOfRecipients.val());
	
	//CHECK BOXES SETUP
	var $prevTable = $(".dataTable").eq(-2).find('.visibilityCheckbox');
	var $currTable = $(".dataTable").last().find('.visibilityCheckbox');
	
	$currTable.each(function (index) {
		$(this).prop('checked', $prevTable.eq(index).prop('checked'));
	});
}
function enableRow(el,row){
	var visibilityOptions = ($(el).parent().parent().next().next());
	var table = visibilityOptions.children().children();
	var tdElements = $($(table).children().children()[row]).children();
	if($(tdElements).parent().prop("tagName") == "tr"){
		return; 
	}
	$(tdElements).unwrap().wrapAll("<tr>");
}
function disableRow(el,row){
	var visibilityOptions = ($(el).parent().parent().next().next());
	var table = visibilityOptions.children().children();
	var tdElements = $($(table).children().children()[row]).children();
	if($(tdElements).parent().prop("tagName") == "hide"){
		return; 
	}
	$(tdElements).unwrap().wrapAll("<hide>");
	$(tdElements).parent().hide();
}

function feedbackRecipientUpdateVisibilityOptions(el){
	if($(el).val() == "OWN_TEAM" || $(el).val() == "TEAMS" || $(el).val() == "INSTRUCTORS" || $(el).val() == "OWN_TEAM_MEMBERS"){
		enableRow(el, 1);
		disableRow(el, 3);
		return;
	}else if($(el).val() == "NONE"){
		disableRow(el, 3);
		disableRow(el, 1);
		return;
	}
	
	enableRow(el, 1);
	enableRow(el, 3);
}

function feedbackGiverUpdateVisibilityOptions(el){
	if($(el).val() == "INSTRUCTORS"){
		disableRow(el, 2);
		return;
	}
	enableRow(el, 2);
}

/**
 * Sets the correct initial question number from the value field
 */
function formatQuestionNumbers(){
	var $questions = $("table[class*='questionTable']");
	
	$questions.each(function (index){
		var $selector = $(this).find('.questionNumber');
		$selector.val(index+1);
		if(index != $questions.size()-1){
			$selector.prop('disabled', true);
		}
	});
}