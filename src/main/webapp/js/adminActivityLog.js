function submitForm(offset) {
	$('input[name=offset]').val(offset);
	$('input[name=pageChange]').val("true");
	$("#activityLogFilter").submit();
}

function toggleReference() {
	$("#filterReference").toggle();
	
	var button = $("#detailButton").attr("class");
	
	if(button == "glyphicon glyphicon-chevron-down"){
	$("#detailButton").attr("class","glyphicon glyphicon-chevron-up");
	$("#referenceText").text("Hide Reference");
	}else{
		$("#detailButton").attr("class","glyphicon glyphicon-chevron-down");
		$("#referenceText").text("Show Reference");
	}
}

$(function() {
	$("#filterReference").toggle();
});