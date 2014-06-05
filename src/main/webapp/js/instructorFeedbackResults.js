/*

    InstructorFeedbackResults.js

*/




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