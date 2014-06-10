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

function filterResults(searchText, element){
    var recurse = false;
    element = element || $("#frameBodyWrapper").find("div.panel").filter(function(index){
        var e = $("#frameBodyWrapper").find("div.panel")[index];
        var heading = $(e).children(".panel-heading");
        var body = $(e).children(".panel-body");
        if(heading.length != 0 && body.length != 0){
            recurse = true;
            return true;
        } else {
            return false;
        }
    });

    for(var i=0 ; i<element.length ; i++){
        var elm = element[i];
        if($(elm).text().toLowerCase().indexOf(searchText.toLowerCase()) == -1){
            $(elm).hide();
        } else {
            $(elm).show();

            if(recurse){
                var childElements = $(elm).find(".panel,div.row,tbody>tr");
                filterResults(searchText, childElements);
            }
        }
    }
}

function updateResultsFilter(){
    filterResults($("#results-search-box").val());
}




//This section is used to enable all panels to be collapsible.
var panelsCollapsed = false;
function toggleCollapse(){
    if(panelsCollapsed){
        $("div[class*='panelBodyCollapse-']").collapse("show");
        $("#collapse-panels-button").html("Collapse All");
    } else {
        $("div[class*='panelBodyCollapse-']").collapse("hide");
        $("#collapse-panels-button").html("Expand All");
    }
    panelsCollapsed = !panelsCollapsed;
}

function toggleSingleCollapse(e){
    if(e.target == e.currentTarget){
        $($(e.target).attr('data-target')).collapse('toggle');
    }
}

window.onload = function(){
    var panels = $("div.panel");
    var numPanels = 0;
    for(var i=0 ; i<panels.length ; i++){
        var heading = $(panels[i]).children(".panel-heading");
        var body = $(panels[i]).children(".panel-body");
        if(heading.length != 0 && body.length != 0){
            numPanels++;
            //$(heading[0]).attr("data-toggle","collapse");
            //Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            $(heading[0]).click(toggleSingleCollapse);
            $(heading[0]).attr("data-target",".panelBodyCollapse-"+numPanels);
            $(body[0]).addClass("collapse in panelBodyCollapse-"+numPanels);
        }
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