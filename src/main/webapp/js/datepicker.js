$(document).ready(function(){
	var today = new Date();
	var yesterday = today.setDate(today.getDate() - 1);
	var tomorrow = today.setDate(today.getDate() + 1);
	
	$("#start").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
        defaultDate: new Date(),
        onSelect: function(date) {
        	$("#deadline").datepicker("option", "minDate", getMinDateForEndDate($('#start').datepicker("getDate")));
        }
    });

    $("#deadline").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: new Date(),
    	onSelect: function(date) {
    		$("#start").datepicker("option", "maxDate", date);
    	}
    });

	$("#startdate" ).datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: new Date(),
        onSelect: function(date, inst) {
    		var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker("getDate"), 
    				$('#publishdate').datepicker("getDate"));
    		$("#visibledate").datepicker("option", "maxDate", newVisibleDate);
    		
    		var newPublishDate = getMinDateForPublishDate($('#visibledate').datepicker("getDate"));
        	$("#publishdate").datepicker("option", "minDate", newPublishDate);
        }
    });

    $("#enddate").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: tomorrow,
    	minDate: today
    });

    $("#visibledate").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: yesterday,
    	maxDate: today,
    	onSelect: function(date) {
    		var newPublishDate = getMinDateForPublishDate($('#visibledate').datepicker("getDate"));
    		$("#publishdate").datepicker("option", "minDate", newPublishDate);
    	}
    });

    $("#publishdate").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: tomorrow,
    	onSelect: function() {
    		var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker("getDate"), 
    				$('#publishdate').datepicker("getDate"));
    		$("#visibledate").datepicker("option", "maxDate", newVisibleDate);
    	}
    });
});

/**
 * @assumption: startDate has a valid value
 * @returns 
 */
function getMinDateForEndDate (startDate) {	
	return startDate;
}

/**
 * @assumption: endDate has a valid value
 * @returns 
 */
function getMaxDateForStartDate (endDate) {
	return endDate;
}

/** 
 * @assumption: startDate has a valid value
 * @returns 
 */
function getMaxDateForVisibleDate (startDate, publishDate) {
	var minDate = 0;
	
	if (publishDate == null) {
		minDate = startDate;
	} else if (startDate > publishDate) {
		minDate = publishDate;
	} else {
		minDate = startDate;
	}

	return minDate;
}

/**
 * @assumption: visibleDate has a valid value
 * @returns 
 */
function getMinDateForPublishDate (visibleDate) {	
	return visibleDate;
}