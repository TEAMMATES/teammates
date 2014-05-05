module('index.js');

test('submissionCounter(currDate, baseDate)', function() {
    var currentDate = new Date(2013, 11, 21);
    var baseDate = new Date(2014, 11, 12);
    var submissionPerHour = 2;
    var baseCount = 36000;
    var errorMsg = "Thousands of"
    //Current date being null should result in default message
    assert.equal(submissionCounter(null, baseDate, submissionPerHour,baseCount),errorMsg);
    //Base date being null should result in default message
    assert.equal(submissionCounter(currentDate, null, submissionPerHour,baseCount),errorMsg);
    //Base date being ahead of current date should result in default message
    assert.equal(submissionCounter(currentDate, baseDate, submissionPerHour,baseCount), errorMsg);
    //Check the function with base and current date being in same month
    var baseDate1 = new Date(2013, 11, 20);
    assert.equal(submissionCounter(currentDate, baseDate1, submissionPerHour,baseCount), "36,096");
    //Check the function with valid base and current date. The resulting value should be (current date -  base date)* submission per hour + base count
    var baseDate2 = new Date(2013, 10, 30);
    assert.equal(submissionCounter(currentDate, baseDate2, submissionPerHour,baseCount), "37,056");
    //Check the function with a result that requires multiple ','
    var baseDate3 = new Date(1913, 10, 30);
    assert.equal(submissionCounter(currentDate, baseDate3, submissionPerHour,baseCount), "1,765,056");
});