$(document).ready(function(){
	$('table.dataTable').each(function(){
		sortTable($(this),2,null,true);
	});
});