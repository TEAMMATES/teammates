/* global isUserTyping:false */

QUnit.module('instructorCourseEnrollPage.js');

QUnit.test('isUserTyping(strText)', (assert) => {
    assert.equal(isUserTyping('no separator \n'), true, 'Manually typing, no pipe');
    assert.equal(isUserTyping('field1 | field2 \n'), false, 'Manually typing, with pipe');
    assert.equal(isUserTyping('navie \tquahuynh@gmail.com \tcoder \n'), false, 'Copied from spreadsheet');
});
