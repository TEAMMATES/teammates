function toggleResponseCommentTable(recipientIndex, giverIndex, qnIndx) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;
	
	if($("#toggleResponseCommentTableButton"+id).text() == "[show]"){
		$("#responseCommentTable"+id).show();
		$("#toggleResponseCommentTableButton"+id).text("[hide]");
	} else {
		$("#responseCommentTable"+id).hide();
		$("#toggleResponseCommentTableButton"+id).text("[show]");
	}
}

function showCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
	
	$("#responseCommentRow"+id).hide();
	$("#responseEditForm"+id).show();
}