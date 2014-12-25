var yearMax = 0;
var yearMin = 9999;

$(document).ready(function() {
	boundYearPager();
});

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
	
	if(yearMin >= (currentYear - 1)){
		$("a#previousYearButton").parent().prop("disabled", true);
	}
});

$(document).on("click", "a#nextYearButton", function() {
	var currentYear = parseInt($("a#currentYearButton").text());
	
	if(yearMax > currentYear){
		$("a#currentYearButton").text(currentYear + 1);
	}
	
	if(yearMax <= (currentYear + 1)){
		$("a#nextYearButton").parent().prop("disabled", true);
	}
});

function showMatchedAccountEntries(month, year) {
	$("td.accountCreatedDate").each(function(index) {

		var expected = month + " " + year;
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
