$(document).ready(function(){
	$('#button_add_comment').click(function(){
		if($('#commentArea').is(':visible')){
			$('#commentArea').hide();
		} else {
			$('#commentArea').show();
		}
	});

	$('#visibility-options-trigger').click(function(){
		if($('#visibility-options').is(':visible')){
			$('#visibility-options').hide();
			$('#visibility-options-trigger').html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
		} else {
			$('#visibility-options').show();
			$('#visibility-options-trigger').html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
		}
	});

	$('#button_cancel_comment').click(function(){
		$('#commentArea').hide();
	});
});