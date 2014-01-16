/*
This function is called to toggle the visibility of additional question information for the specified question.
*/
function toggleAdditionalQuestionInfo(identifier){
	console.log($("#questionAdditionalInfoButton-"+identifier).text());
	console.log("#questionAdditionalInfoButton-"+identifier);
	if($("#questionAdditionalInfoButton-"+identifier).text() == "[more]"){
		$("#questionAdditionalInfoButton-"+identifier).text("[less]");
	} else {
		$("#questionAdditionalInfoButton-"+identifier).text("[more]");
	}
	$("#questionAdditionalInfo-"+identifier).toggle();
}