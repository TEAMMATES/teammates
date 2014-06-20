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

//This section is used to enable all panels to be collapsible.
var panelsCollapsed = false;
var isCollapsingAll = false;
var isExpandingAll = false;

function toggleCollapse(){
    if(panelsCollapsed){
        var panels = $("div.panel-collapse");
        isExpandingAll = true;
        $(panels[0]).collapse("show");
        $("#collapse-panels-button").html("Collapse All");
    } else {
        var panels = $("div.panel-collapse");
        isCollapsingAll = true;
        $(panels[0]).collapse("hide");
        $("#collapse-panels-button").html("Expand All");
    }
    panelsCollapsed = !panelsCollapsed;
}

function toggleSingleCollapse(e){
    if(e.target == e.currentTarget){
        $($(e.target).attr('data-target')).collapse('toggle');
        isCollapsingAll = false;
        isExpandingAll = false;

        var glyphIcon = $(this).children('.glyphicon');
        var className = $(glyphIcon[0]).attr('class');
        if(className.indexOf('glyphicon-chevron-up') != -1){
            $(glyphIcon[0]).removeClass('glyphicon-chevron-up');
            $(glyphIcon[0]).addClass('glyphicon-chevron-down');
        } else {
            $(glyphIcon[0]).removeClass('glyphicon-chevron-down');
            $(glyphIcon[0]).addClass('glyphicon-chevron-up');
        }
    }
}

function getNextId(e){
    var id = $(e).attr('id');
    var nextId = "#panelBodyCollapse-" + (parseInt(id.split('-')[1]) + 1);
    return nextId;
}

window.onload = function(){
    var panels = $("div.panel");
    var numPanels = 0;
    for(var i=0 ; i<panels.length ; i++){
        var heading = $(panels[i]).children(".panel-heading");
        var bodyCollapse = $(panels[i]).children(".panel-collapse");
        if(heading.length != 0 && bodyCollapse.length != 0){
            numPanels++;
            //$(heading[0]).attr("data-toggle","collapse");
            //Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            $(heading[0]).click(toggleSingleCollapse);
            $(heading[0]).attr("data-target","#panelBodyCollapse-"+numPanels);
            $(heading[0]).css("cursor", "pointer");
            $(bodyCollapse[0]).attr('id', "panelBodyCollapse-"+numPanels);

            $(bodyCollapse[0]).on('hidden.bs.collapse', function(){
                console.log('Finish hide');
                if(isCollapsingAll){
                    var id = $(this).attr('id');
                    var nextId = this;
                    do{
                        nextId = getNextId(nextId);
                    } while($(nextId).length && $('#' + id + ' ' + nextId).length);
                    
                    if($(nextId).length){
                        $(nextId).collapse('hide');
                    } else {
                        isCollapsingAll = false;
                    }
                }
            });
            $(bodyCollapse[0]).on('shown.bs.collapse', function(){
                console.log('Finish show');
                if(isExpandingAll){
                    var id = $(this).attr('id');
                    var nextId = this;
                    do{
                        nextId = getNextId(nextId);
                    } while($(nextId).length && $('#' + id + ' ' + nextId).length);
                    
                    if($(nextId).length){
                        $(nextId).collapse('show');
                    } else {
                        isExpandingAll = false;
                    }
                }
            });
        }
    }

    if($("#collapse-panels-button").html().indexOf("Expand All") != -1){
        panelsCollapsed = true;
    } else {
        panelsCollapsed = false;
    }
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

    //Show/Hide statistics
    showHideStats();
    $("#show-stats-checkbox").change(showHideStats);
});
