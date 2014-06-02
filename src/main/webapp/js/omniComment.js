$('#omni-comment-recipient-input').parent().focusin(function(){
	$('#omni-comment-recipient-select').show();
});

$('#omni-comment-recipient-input').parent().focusout(function(){
	$('#omni-comment-recipient-select').hide();
});