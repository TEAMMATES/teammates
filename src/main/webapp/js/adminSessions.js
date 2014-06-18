function toggleContent(id) {

	$("#table_" + id).slideToggle("slow");

	var pill = $("#pill_" + id).attr("class");

	if (pill == 'active') {
		$("#pill_" + id).attr("class", " ");
	} else {
		$("#pill_" + id).attr("class", "active");
	}

}

function sessionToggleSort(divElement, colIdx, comparator) {

	if ($(divElement).attr("class") == "non-sorted") {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "ascending");
	} else if ($(divElement).attr("class") == "ascending") {
		sortTable(divElement, colIdx, comparator, false);
		$(divElement).attr("class", "descending");
	} else {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "ascending");
	}
}

function checkTopOutOfView() {
	
	var visible = $('#headTitle');
	
	if (visible) {
		$("#bottomButton").attr("style","display:none;");
	}else{
		$("#bottomButton").attr("style","asdasd");
	}
}




$(function() {
	$(".table-responsive").toggle();
	setInterval("checkTopOutOfView()", 100);
	
});

