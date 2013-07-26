$(document).ready(function(){
	$('table.dataTable').each(function(){
		sortTable($(this),1,null,false);
	});
});