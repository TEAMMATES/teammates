const entryPerPage = 200; 

var start = 0;
var end = 0;
var total = 0;

var currentPage = 1;
var totalPages;

$(document).ready(function() {
	
	toggleSort($("#button_sort_createat").parent(), 4);
	reLabelOrderedAccountEntries();
	caculateTotalPages();
	updatePagination();
	showFirstPage();
	updateEntriesCount();
});

function updatePagination(){
	
	if(totalPages > 5) {
		if(currentPage >= 3 && (currentPage + 1) < totalPages){
			$("div#pagination_top ul.pagination li a.pageNumber").each(function(index){
				var newPageNumber = currentPage - 2 + index;
				$(this).text(newPageNumber);
			});
		} 
		
		if(currentPage >= 3 && (currentPage + 1) == totalPages){
			$("div#pagination_top ul.pagination li a.pageNumber").each(function(index){
				var newPageNumber = currentPage - 3 + index;
				$(this).text(newPageNumber);
			});
		} 
		
		if (currentPage < 3){
			$("div#pagination_top ul.pagination li a.pageNumber").each(function(index){
				$(this).text(index + 1);
			});
		}
	} else {
		$("div#pagination_top ul.pagination li a.pageNumber").each(function(index){
			$(this).text(index + 1);
			
			if((index + 1) > totalPages){
				$(this).parent().hide();
			}
		});
	}
	
	$("div#pagination_top ul.pagination li a.pageNumber").each(function(index){
		var pageNum = parseInt($(this).text());
		if(pageNum == currentPage){
			$(this).parent().attr("class", "active");
		} else {
			$(this).parent().attr("class", "");
		}
	});
	
	$("#pagination_bottom").html($("#pagination_top").html());
}

function caculateTotalPages(){
	var a = parseInt(total/entryPerPage);
	var b = total%entryPerPage;
	totalPages = b==0? a : a + 1;
}

function updateEntriesCount(){
	var newText = start + "~" + (end > total? total : end);
	
	$("span#currentPageEntryCount").text(newText);	
	$("span#totalEntryCount").text(total);
}

function hideAllEntries(){
	$("tr.accountEntry").hide();
}

function showFirstPage(){
	hideAllEntries();
	start = 1;
	end = entryPerPage;
	currentPage = 1;
	showEntryInInterval(start, end);
}

function showEntryInInterval(start, end){
	hideAllEntries();
	for(var i=start; i<=end; i++){
		$("#accountEntry_" + i).show();
	};
}

function reLabelOrderedAccountEntries(){
	total = 0;
	$("tr.accountEntry").each(function(index){
		$(this).attr("id", "accountEntry_" + (index+1));
		total ++;
	});
	
	showFirstPage();
	updateEntriesCount();
	updatePagination();
}

function showEntriesForSelectedPage(){
	start = (currentPage - 1)*entryPerPage + 1;
	end = start + entryPerPage - 1;
	showEntryInInterval(start, end);
	
}

$(document).on("click", "ul.pagination li.previous", function(){
	goToPreviousPage();
});

$(document).on("click", "ul.pagination li a.pageNumber", function(){
	currentPage = parseInt($(this).text());
	showEntriesForSelectedPage();
	updateEntriesCount();
	updatePagination();
});

$(document).on("click", "ul.pagination li.next", function(){
	goToNextPage();
});

function goToPreviousPage(){
	currentPage = (currentPage > 1)? currentPage - 1 : currentPage;
	showEntriesForSelectedPage();
	updateEntriesCount();
	updatePagination();
}

function goToNextPage(){
	currentPage = (currentPage < totalPages)? currentPage + 1 : totalPages;
	showEntriesForSelectedPage();
	updateEntriesCount();
	updatePagination();
}

$(document).keydown(function(e) {
    if(e.keyCode == 37) { //LEFT
    	goToPreviousPage();
     }
    if(e.keyCode == 39) { //RIGHT
    	goToNextPage();
     }
 });


