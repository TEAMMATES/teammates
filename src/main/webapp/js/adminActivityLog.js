function submitForm(offset){
	$('input[name=offset]').val(offset);
	$('input[name=pageChange]').val("true");
	$("#activityLogFilter").submit();
}


function toggleReference(){
	$("#filterReference").toggle();
}