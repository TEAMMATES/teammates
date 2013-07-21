jQuery.fn.reverse = [].reverse;

var FEEDBACK_RESPONSE_RECIPIENT = "responserecipient";

// On body load event
$(document).ready(function () {

	// Bind submission event
	$('form[name="form_student_submit_response"]').submit(function() {
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

// Ensures a single question cannot have two identical recipients selected
// (Assuming recipients in dropdown list <= number of response boxes).
// This function also assumes that responses that already exist will be placed
// above new ones.
function formatRecipientLists(){
	// Makes initial selections distinct for each question
	$('select.newResponse').reverse().each(function(){		
		// Assign option of each select box based on order
		var responseIndx = $(this).attr('name').split('-')[2];
		$(this).find('option:eq('+responseIndx+')').prop('selected',true);		
		// Check for option collision (with existing responses that are untouched)
		var defensiveCheck = $(this).find('option').size();
		while(isCollides($(this)) && defensiveCheck > 0){
			$(this).find('option:selected').prev().prop('selected',true);
			defensiveCheck--;
		}
	});
	
	$('select.participantSelect').each(function () {
		// Save initial data.
		$(this).data('previouslySelected',$(this).val());
	}).change(function() {
		// Binds further changes such that a swap will occur
		// if selecting an option that has already been selected elsewhere. 
		var $select = $(this);
    	var $previouslySelected = $(this).data('previouslySelected');
    	$(this).parents('table').find('select').each(function(){
			if($select.val() == $(this).val() && $select.attr('name') != $(this).attr('name')){
				$(this).find('option[value="'+$previouslySelected+'"]').prop('selected',true);
				$(this).data('previouslySelected',$(this).val());
			}
		});
		$(this).data('previouslySelected',$(this).val());
    });
}

function isCollides($select) {
	return ($select.parents('table').find('select').filter(function(){
		return ($select.val() == $(this).val() && $select.attr('name') != $(this).attr('name'));
		}).size() > 0);
}

function reenableFieldsForSubmission() {
	$(':disabled').prop('disabled',false);
}