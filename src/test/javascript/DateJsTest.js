module ('date.js');

test('getTimeZoneStringFromDate(date)', function() {
    var date = 'Sun, 31 Jan 2016, 08:17 AM (AEDT)';
    equal(getTimeZoneStringFromDate(dateTwo), '(AEDT)');
    
    var dateTwo = 'Sun, 31 Jan 2016, 08:17 AM (SGT)';
    equal(getTimeZoneStringFromDate(dateTwo), '(SGT)');
});

test('convertToClientTimeZone()', function() {
    var expectedTimeZoneString = getTimeZoneStringFromDate(new Date());
    var cummulatedTimeStrings = [];
    
    convertToClientTimeZone();
    $('.client-time').each(function () {
        var date = this.innerHTML;
        cummulatedTimeStrings.push(date);
        var timeZoneOfConvertedTime = getTimeZoneStringFromDate(date);
        equal(timeZoneOfConvertedTime, expectedTimeZoneString);
    });
    
    //call once again to ensure it is not changed back or changed again
    convertToClientTimeZone();
    var counter = 0;
    $('.client-time').each(function () {
        var date = this.innerHTML;
        equal(date, cummulatedTimeStrings[counter++]);
    });
    
});

test('convertToUtc()', function() {
    var expecedTimeZoneString = 'UTC';
    var cummulatedTimeStrings = [];
    
    convertToClientTimeZone();
    convertToUtc();
    $('.client-time').each(function () {
        var date = this.innerHTML;
        cummulatedTimeStrings.push(date);
        equal(true, date.toString().endsWith('UTC'));
    });
    
    //call once again to ensure it is not changed back or changed again
    convertToUtc();
    var counter = 0;
    $('.client-time').each(function () {
        var date = this.innerHTML;
        equal(date, cummulatedTimeStrings[counter++]);
    });
    
});