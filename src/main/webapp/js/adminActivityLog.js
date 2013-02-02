function toggleAllServlets(toggle){
	 var checkboxes = document.getElementsByName("toggle_servlets");
	 
	 for (var i = 0; i < checkboxes.length; i++){
		 var checkbox = checkboxes[i];
		 checkbox.checked = toggle;
	 }
}

function submitForm(offset){
	$('input[name=offset]').val(offset);
	$('input[name=pageChange]').val("true");
	$("#logSearch").submit();
}