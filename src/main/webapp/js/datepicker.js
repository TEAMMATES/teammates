$(document).ready(function(){
	$("#start" ).datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
        defaultDate: new Date(),
        onSelect: function(date) {
        	$("#deadline").datepicker("option", "minDate", date);
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
        onSelect: function(date) {
        	$("#visibledate").datepicker("option", "maxDate", date);
        	$("#enddate").datepicker("option", "minDate", date);
        }
    });

    $("#enddate").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: new Date(),
    	onSelect: function(date) {
    		$("#startdate").datepicker("option", "maxDate", date);
    	}
    });

    $("#visibledate").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: new Date(),
    	onSelect: function(date) {
    		$("#startdate").datepicker("option", "minDate", date);
    		$("#publishdate").datepicker("option", "minDate", date);
    	}
    });

    $("#publishdate").datepicker({
    	dateFormat: "dd/mm/yy",
    	showOtherMonths: true,
    	gotoCurrent: true,
    	defaultDate: new Date(),
    	onSelect: function(date) {
    		$("#visibledate").datepicker("option", "maxDate", date);
    	}
    });
});
