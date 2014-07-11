/*
This function is called to toggle the visibility of additional question information for the specified question.
*/
function toggleAdditionalQuestionInfo(identifier){
    console.log($("#questionAdditionalInfoButton-"+identifier).text());
    console.log("#questionAdditionalInfoButton-"+identifier);

    var element = $("#questionAdditionalInfoButton-"+identifier)

    if(element.text() == element.attr('data-more')){
        element.text(element.attr('data-less'));
    } else {
        element.text(element.attr('data-more'));
    }
    $("#questionAdditionalInfo-"+identifier).toggle();
}