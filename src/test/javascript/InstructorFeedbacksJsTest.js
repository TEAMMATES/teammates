

module('instructorFeedbacks.js');

test('extractQuestionNumFromEditFormId(id)', function(){
	//Tests that extracting question number from form is correct.
	for(var i=1 ; i<1000 ; i++){
		var id = "form_editquestion-" + i;
		equal(extractQuestionNumFromEditFormId(id) , i);
	}
});
