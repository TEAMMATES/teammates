function toggleResponseCommentTable(recipientIndex, giverIndex, qnIndx) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;
	console.log(id);
	
	if($("#toggleResponseCommentTableButton"+id).text() == "[show]"){
		$("#responseCommentTable"+id).show();
		$("#toggleResponseCommentTableButton"+id).text("[hide]");
	} else {
		$("#responseCommentTable"+id).hide();
		$("#toggleResponseCommentTableButton"+id).text("[show]");
	}
}