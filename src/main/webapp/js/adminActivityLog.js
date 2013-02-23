function toggleAllServlets(toggle){
	 var checkboxes = document.getElementsByName("toggle_servlets");
	 var topHR = document.getElementById("topHR");
	 var bottomHR = document.getElementById("bottomHR");
	 
	 for (var i = 0; i < checkboxes.length; i++){
		 var checkbox = checkboxes[i];
		 checkbox.checked = toggle;
		 if (toggle == true){
		 	checkbox.parentNode.style.display = "none";
		 } else {
			 checkbox.parentNode.style.display = "table-cell";
		 }
	 }
	 
	 if(toggle == true){
		 topHR.parentNode.style.display = "none";
		 bottomHR.parentNode.style.display = "none";
	 } else {
		 topHR.parentNode.style.display = "table-cell";
		 bottomHR.parentNode.style.display = "table-cell";
	 }
}

function submitForm(offset){
	$('input[name=offset]').val(offset);
	$('input[name=pageChange]').val("true");
	$("#logSearch").submit();
}

function showServlets(){
	 var checkboxes = document.getElementsByName("toggle_servlets");
	 var topHR = document.getElementById("topHR");
	 var bottomHR = document.getElementById("bottomHR");
	 
	 for (var i = 0; i < checkboxes.length; i++){
		 var checkbox = checkboxes[i];
		 checkbox.parentNode.style.display = "table-cell";
	 }
	 
	 topHR.parentNode.style.display = "table-cell";
	 bottomHR.parentNode.style.display = "table-cell";

}