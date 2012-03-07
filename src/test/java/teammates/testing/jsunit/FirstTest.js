module("SampleTest");
//define a function, check if para is a number
function simpleTest(para) {
	if (typeof para == "number") {
		return true;
	} else {
		return false;
	}
}
//start unit test
test('simpleTest()', function() {
	ok(simpleTest(2), '2 is a number');
	ok(!simpleTest("2"), '"2" is not a number');
});