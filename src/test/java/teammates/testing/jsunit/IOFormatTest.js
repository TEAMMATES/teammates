module("I/O Format");

test('trim()', function() {
	equal('course', trim(" course   "), "trim() is working!");
})