/*

    InstructorFeedbackResults.js

*/


//Show/hide stats
function showHideStats(){
    if($("#show-stats-checkbox").is(":checked")){
        $(".resultStatistics").show();
    } else {
        $(".resultStatistics").hide();
    }
}

//Search functionality

function filterResults(searchText){
    var element = $("#frameBodyWrapper").children("div.panel");

    if($(element).parents(".resultStatistics").length){
        return;
    }

    for(var i = 0 ; i < element.length ; i++){
        var elm = element[i];
        var heading = $(elm).children('.panel-heading');
        if($(heading[0]).text().toLowerCase().indexOf(searchText.toLowerCase()) != -1){
           $(elm).show();
        } else {
           $(elm).hide();
        } 
    }
}

function updateResultsFilter(){
    filterResults($("#results-search-box").val());
}

function toggleCollapse(e, panels){
    if($(e).html().indexOf("Expand") != -1){
        panels = panels || $("div.panel-collapse");
        isExpandingAll = true;
        var i = 0;
        for(var idx = 0; idx < panels.length; idx++){
            if($(panels[idx]).attr('class').indexOf('in') == -1){
                setTimeout(showSingleCollapse, 100 * i, panels[idx]);
                i++;
            }
        }
        var htmlString = $(e).html();
        htmlString = htmlString.replace("Expand", "Collapse");
        $(e).html(htmlString);
    } else {
        panels = panels || $("div.panel-collapse");
        isCollapsingAll = true;
        var i = 0;
        for(var idx = 0; idx < panels.length; idx++){
            if($(panels[idx]).attr('class').indexOf('in') != -1){
                setTimeout(hideSingleCollapse, 100 * i, panels[idx]);
                i++;
            }
        }
        var htmlString = $(e).html();
        htmlString = htmlString.replace("Collapse", "Expand");
        $(e).html(htmlString);
    }
}

function showSingleCollapse(e){
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon.pull-right');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-down');
    $(glyphIcon[0]).addClass('glyphicon-chevron-up');
    $(e).collapse("show");
    $(heading).find('.btn').show();
}

function hideSingleCollapse(e){
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon.pull-right');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-up');
    $(glyphIcon[0]).addClass('glyphicon-chevron-down');
    $(e).collapse("hide");
    $(heading).find('.btn').hide();
}

function toggleSingleCollapse(e){
    if(!$(e.target).is('a')){
        var glyphIcon = $(this).find('.glyphicon.pull-right');
        var className = $(glyphIcon[0]).attr('class');
        if(className.indexOf('glyphicon-chevron-up') != -1){
            hideSingleCollapse($(e.currentTarget).attr('data-target'));
        } else {
            showSingleCollapse($(e.currentTarget).attr('data-target'));
        }
    }
}

function getNextId(e){
    var id = $(e).attr('id');
    var nextId = "#panelBodyCollapse-" + (parseInt(id.split('-')[1]) + 1);
    return nextId;
}

function bindCollapseEvents(panels, numPanels){
    for(var i=0 ; i<panels.length ; i++){
        var heading = $(panels[i]).children(".panel-heading");
        var bodyCollapse = $(panels[i]).children(".panel-collapse");
        if(heading.length != 0 && bodyCollapse.length != 0){
            numPanels++;
            //$(heading[0]).attr("data-toggle","collapse");
            //Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            if($(heading[0]).attr('class') == 'panel-heading'){
                $(heading[0]).click(toggleSingleCollapse);
            }
            $(heading[0]).attr("data-target","#panelBodyCollapse-"+numPanels);
            $(heading[0]).css("cursor", "pointer");
            $(bodyCollapse[0]).attr('id', "panelBodyCollapse-"+numPanels);
        }
    }
    return numPanels;
}

window.onload = function(){
    var panels = $("div.panel");
    var numPanels = 0;

    bindCollapseEvents(panels, numPanels);
    $("a[id^='collapse-panels-button-section-']").on('click', function(){
        var panels = $(this).closest('.panel-success').children('.panel-collapse').find('div.panel.panel-warning').children('.panel-collapse');
        toggleCollapse(this, panels);
    });

    $("a[id^='collapse-panels-button-team-']").on('click', function(){
        var panels = $(this).closest('.panel-warning').children('.panel-collapse').find('div.panel.panel-primary').children('.panel-collapse');
        toggleCollapse(this, panels);
    });
};

//Set on ready events
$(document).ready(function(){
    $("#results-search-box").keyup(function(e){
        updateResultsFilter();
    });
    //prevent submitting form when enter is pressed.
    $("#results-search-box").keypress(function(e) {
        if(e.which == 13) {
            return false;
        }
    });
    
    //bind errors with default profile picture
    $(".profile-pic-icon-hover, .profile-pic-icon-click").children('img').on('error', function() {
    	$('.profile-pic').on('error', function() {
    		$(this).attr("src","../images/profile_picture_default.png");
    	});
    	$(this).attr("src","../images/profile_picture_default.png");
    });
    
    $('.profile-pic-icon-hover').popover({
    	html: true,
        trigger: 'manual',
        placement: 'top',
        delay: {show: 300, hide: 300},
        content: function () {
        	if ($(this).attr('data-link') === "") {
        		return '<img class="profile-pic" src="' + $(this).children('img')[0].src + '" />';
        	} else {
        		return '<a class="link" onclick="loadProfilePicture($(this).closest(\'.popover\').siblings(\'.profile-pic-icon-hover\'))">View Photo</a>';
        	}
        }});
    
    $('.profile-pic-icon-hover').hover(function() {
    	$(this).popover('show');
    	$(this).siblings('.popover').on('mouseleave', function() {
    		$(this).siblings('.profile-pic-icon-hover').popover("hide");
    	})
    	.on('click', function(event) {
    		event.stopPropagation();
    		window.event.cancelBubble = true;
    	});
    	
    }, function() {
    	// this is so that the user can hover over the 
    	// popover without accidentally hiding it
    	
    	setTimeout(function(obj) {
    		if (!$(obj).siblings(".popover").is(":hover")) {
                $(obj).popover("hide");
            }
    	}, 200, this);
    });
    
    
    // bind the show picture events    
    $(".profile-pic-icon-click > .student-profile-pic-view-link").on('click', function(event){
    	window.event.cancelBubble = true;
    	event.stopPropagation();
    	
        var link = $(this).parent().attr('data-link');
        $(this).siblings('img')
            .attr("src", link)
            .removeClass('hidden')
            .parent()
            .attr('data-link', '')
            .popover({
            	html: true,
                trigger: 'hover',
                placement: 'top',
                content: function () {
                	return '<img class="profile-pic" src="' + $(this).children('img')[0].src + '" />';
                }});
        
        // this code is done to directly show the picture for other
		// hover-triggered links for this student
        $(".profile-pic-icon-hover[data-link='" + link + "']")
			.children('img')
			.attr('src', link)
			.parent()
			.attr('data-link', "");
        $(this).remove();
    });

    //Show/Hide statistics
    showHideStats();
    $("#show-stats-checkbox").change(showHideStats);
});

function loadProfilePicture(obj) {
	obj.children('img')[0].src = obj.attr('data-link');
	// load the pictures in all similar links
	obj.children('img').load(function() {
		var link = $(this).parent().attr('data-link'); 
		$(this).parent().attr('data-link', "");
		obj.popover('show');

		// this code is done to directly show the picture for other
		// hover-triggered links for this student
		$(".profile-pic-icon-hover[data-link='" + link + "']")
				.children('img')
				.attr('src', link)
				.parent()
				.attr('data-link', "");
	});
}
