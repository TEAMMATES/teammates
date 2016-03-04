
QUnit.module('instructorCourseEnrollPage.js');

QUnit.test('isUserTyping(strText)', function(assert){
    assert.equal(isUserTyping("no separator \n"),true,"Manually typing, no pipe");
    assert.equal(isUserTyping("field1 | field2 \n"),false,"Manually typing, with pipe");
    assert.equal(isUserTyping("navie 	quahuynh@gmail.com 	coder \n"),false,"Copied from spreadsheet"); 
});

