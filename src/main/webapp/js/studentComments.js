$(function(){
	$("div[id^=plainCommentText]").css("margin-left","15px");
	
	// collapse and expand functionality
    var panels = $('div.panel');
    bindCollapseEvents(panels, 0);
});

function showSingleCollapse(e) {
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-down');
    $(glyphIcon[0]).addClass('glyphicon-chevron-up');
    $(e).collapse('show');
    $(heading).find('a.btn').show();
}

function hideSingleCollapse(e) {
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-up');
    $(glyphIcon[0]).addClass('glyphicon-chevron-down');
    $(e).collapse('hide');
    $(heading).find('a.btn').hide();
}

function toggleSingleCollapse(e) {
    if (!$(e.target).is('a') && !$(e.target).is('input')) {
        var glyphIcon = $(this).find('.glyphicon');
        var className = $(glyphIcon[0]).attr('class');
        if (className.indexOf('glyphicon-chevron-up') != -1) {
            hideSingleCollapse($(e.currentTarget).attr('data-target'));
        } else {
            showSingleCollapse($(e.currentTarget).attr('data-target'));
        }
    }
}

function bindCollapseEvents(panels, numPanels) {
    for (var i = 0 ; i < panels.length ; i++) {
        var heading = $(panels[i]).children('.panel-heading');
        var bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length != 0 && bodyCollapse.length != 0) {
            numPanels++;
            // $(heading[0]).attr('data-toggle', 'collapse');
            // Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            if ($(heading[0]).attr('class') == 'panel-heading') {
                $(heading[0]).click(toggleSingleCollapse);
            }
            $(heading[0]).attr('data-target', '#panelBodyCollapse-' + numPanels);
            $(heading[0]).attr('id', 'panelHeading-' + numPanels);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', 'panelBodyCollapse-' + numPanels);
        }
    }
    return numPanels;
}

