

module('instructorCourseEnrollPage.js');


test('isUserTyping(strText)', function(){
    equal(isUserTyping("no separator \n"),true,"Manually typing, no pipe");
    equal(isUserTyping("field1 | field2 \n"),false,"Manually typing, with pipe");
    equal(isUserTyping("navie 	quahuynh@gmail.com 	coder \n"),false,"Copied from spreadsheet");
    
});

