jQuery.fn.reverse = [].reverse;

var FEEDBACK_RESPONSE_RECIPIENT = "responserecipient";

// On body load event
$(document).ready(function () {

	// Bind submission event
	$('form[name="form_submit_response"]').submit(function() {
		reenableFieldsForSubmission();
	});
	
	// Format recipient dropdown lists
	formatRecipientLists();
	
	// Replace hidden dropdowns with text
	$('select.participantSelect:hidden').each(function (){
		$(this).after('<span> '+$(this).find('option:selected').html()+'</span>');
	});
	
	// Enable tooltips
	document.onmousemove = positiontip;
});

/**
 * Removes already selected options for recipients
 * from other select dropdowns within the same question.
 * Binds further changes to show/hide options such that duplicates
 * cannot be selected.
 */
function formatRecipientLists(){
	$('select.participantSelect').each(function(){
		if (!$(this).hasClass(".newResponse")) {
			// Remove options from existing responses
			var questionNumber = 
				$(this).attr('name').split('-')[1];
			var selectedOption = $(this).find('option:selected').val();
			
			if (selectedOption != "") {
				$("select[name|="+FEEDBACK_RESPONSE_RECIPIENT+"-"+questionNumber+"]").not(this).
					find("option[value='"+selectedOption+"']").hide();
			}
		}
		// Save initial data.
		$(this).data('previouslySelected',$(this).val());
	}).change(function() {
		var questionNumber = $(this).attr('name').split('-')[1];
    	var lastSelectedOption = $(this).data('previouslySelected');
		var curSelectedOption = $(this).find('option:selected').val();

    	if(lastSelectedOption != "") {
			$("select[name|="+FEEDBACK_RESPONSE_RECIPIENT+"-"+questionNumber+"]").not(this).
			find("option[value='"+lastSelectedOption+"']").show();
    	}
		if (curSelectedOption != "") {
			$("select[name|="+FEEDBACK_RESPONSE_RECIPIENT+"-"+questionNumber+"]").not(this).
				find("option[value='"+curSelectedOption+"']").hide();
		}
		// Save new data
		$(this).data('previouslySelected',$(this).val());
	});
	
	// Auto-select first valid option.
	$('select.participantSelect.newResponse').each(function(){
		var firstUnhidden = "";
		$(this).children().reverse().each(function(){
		    if (this.style.display != 'none' && $(this).val() != "") {
			    firstUnhidden = this;
		    }
		});
		$(this).val($(firstUnhidden).val()).change();
	});
}

function reenableFieldsForSubmission() {
	$(':disabled').prop('disabled',false);
}