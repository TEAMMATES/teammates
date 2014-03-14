function showResponseCommentAddForm(recipientIndex, giverIndex, qnIndx) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndx;

	$("#showResponseCommentAddFormButton"+id).hide();
	$("#responseCommentAddForm"+id).show();
	$("#responsecommenttext"+id).focus();
}

function showResponseCommentEditForm(recipientIndex, giverIndex, qnIndex, commentIndex) {
	var id = "-"+recipientIndex+"-"+giverIndex+"-"+qnIndex+"-"+commentIndex;
	
	$("#responseCommentRow"+id).hide();
	$("#responseCommentEditForm"+id).show();
}