var yearMax = 0;
var yearMin = 9999;

$(document).ready(function() {
	boundYearPager();	
	selectYearAndMonth();
	checkIfShouldDisableButton();
});

function selectYearAndMonth(){
	
	$("a#currentYearButton").text(yearMax);
	deactivateAllMonthListItems();
	$("#monthPagination ul.pagination li:last-child").attr("class", "active");
	showMatchedAccountEntries("", yearMax);
}

function boundYearPager() {
	$("td.accountCreatedDate").each(function(index) {
		
		var dateAndTime = $(this).text();
		var date = dateAndTime.split(",")[0];
		var year = parseInt(date.split(" ")[2]);
		
		if(year > yearMax){
			yearMax = year;
		}
		
		if(year < yearMin){
			yearMin = year;
		}
	});

}

$(document).on("click", "a.monthListItem", function() {

	var month = $(this).html();
	var year = $("a#currentYearButton").html();
	deactivateAllMonthListItems();
	$(this).parent().attr("class", "active");
	showMatchedAccountEntries(month, year);

});

$(document).on("click", "a#previousYearButton", function() {
	var currentYear = parseInt($("a#currentYearButton").text());
	
	if(yearMin < currentYear){
		$("a#currentYearButton").text(currentYear - 1);
	}
	
	var year = $("a#currentYearButton").html();
	var month = $("li.active a.monthListItem").text();
	showMatchedAccountEntries(month, year);
	
	checkIfShouldDisableButton();
});

$(document).on("click", "a#nextYearButton", function() {
	var currentYear = parseInt($("a#currentYearButton").text());
	
	if(yearMax > currentYear){
		$("a#currentYearButton").text(currentYear + 1);
	}
	
	var year = $("a#currentYearButton").html();
	var month = $("li.active a.monthListItem").text();
	showMatchedAccountEntries(month, year);
	
	checkIfShouldDisableButton();
});

function checkIfShouldDisableButton(){
	var current = parseInt($("a#currentYearButton").text());
	
	if(yearMax <= current){
		$("a#nextYearButton").parent().attr("class", "disabled");
	} else {
		$("a#nextYearButton").parent().attr("class", "");
	}
	
	if(yearMin >= current){
		$("a#previousYearButton").parent().attr("class", "disabled");
	} else {
		$("a#previousYearButton").parent().attr("class", "");
	}
	
}


function showMatchedAccountEntries(month, year) {
	
	if(month.indexOf("All") > -1){
		month = "";
	}
	
	var expected = month + " " + year;
	
	$("td.accountCreatedDate").each(function(index) {	
		var actual = $(this).text();
		if (actual.indexOf(expected) > -1) {
			$(this).parent().show();
		} else {
			$(this).parent().hide();
		}

	});
}

function deactivateAllMonthListItems() {
	$("a.monthListItem").each(function(index) {
		$(this).parent().attr("class", "");
	});
}
