QUnit.module('common.js');

QUnit.test('isNumber(num)', function(assert) {
    assert.equal(isNumber('-0.001'), true, 'Negative double');
    assert.equal(isNumber('12.056'), true, 'Positive double');
    assert.equal(isNumber('100356'), true, 'Positive integer');
    assert.equal(isNumber('-237'), true, 'Negative integer');
    assert.equal(isNumber('ABCDE'), false, 'Letters');
    assert.equal(isNumber('$12.57'), false, 'With Dollar Sign');
    assert.equal(isNumber('12A5'), false, 'Letter in Numbers');
    assert.equal(isNumber('0'), true, 'zero');
    assert.equal(isNumber('   124    '), true, 'With Spacing');
    assert.equal(isNumber('   12   4    '), false, 'With Spacing between');
});

/**
 * Test the whether the passed object is an actual date
 * with an accepted format
 *
 * Allowed formats : http://dygraphs.com/date-formats.html
 *
 * TEAMMATES currently follows the RFC2822 / IETF date syntax
 * e.g. 02 Apr 2012, 23:59
 */
QUnit.test('isDate(date)', function(assert) {
    assert.equal(isDate('12432567'), false, 'Numbers');
    assert.equal(isDate('0/0/0'), true, '0/0/0 - valid date on Firefox, invalid on Chrome');
    assert.equal(isDate('12/2/13'), true, '12/2/13 - valid format');
    assert.equal(isDate('12/02/2013'), true, '12/02/2013 - valid format (mm/dd/yyyy)');
    assert.equal(isDate('28/12/2013'), true, '28/12/2013 - valid format (dd/mm/yyyy)');
    assert.equal(isDate('12/12/13'), true, '12/02/13 - valid format');
    assert.equal(isDate('2013-12-12'), true, '2013-12-12 - valid format');
    assert.equal(isDate('28-12-2013'), false, '28-12-2013 - invalid format (dd-mm-yyyy)');
    assert.equal(isDate('2013-12-28'), true, '2013-12-28 - valid format (yyyy-mm-dd)');
    assert.equal(isDate('01 03 2003'), true, '01 03 2003 - valid format');
    assert.equal(isDate('A1/B3/C003'), false, 'A1/B3/C003 - invalid date');
    assert.equal(isDate('Abcdef'), false, 'Invalid Date string');
    assert.equal(isDate('02 Apr 2012, 23:59'), true, 'Valid Date string with time');
    assert.equal(isDate('02 Apr 2012'), true, 'Valid Date string without time');
    assert.equal(isDate('    12/12/01'), true, 'With Spacing in front');
    assert.equal(isDate('12 12 01       '), true, 'With Spacing behind');
    assert.equal(isDate('            12-12-01       '), false, 'With Spacing,' +
        ' invalid on Firefox and valid on Chrome');
    assert.equal(isDate('a12-12-2001'), false, 'a12-12-2001 - not in proper format');
    assert.equal(isDate('    a      12 12 2001'), false,
        '    a      12 12 2001 - not in proper format');
    assert.equal(isDate('12/12/2001   a  '), false, '12/12/2001   a  - not in proper format');
});

QUnit.test('scrollToTop()', function(assert) {
    // N/A, trivial function
    assert.expect(0);
});

QUnit.test('sortBase(x, y)', function(assert) {
    assert.equal(sortBase('abc', 'abc'), 0, 'Same text');
    assert.equal(sortBase('ABC', 'abc'), -1, 'Bigger text');
    assert.equal(sortBase('abc', 'ABC'), 1, 'Smaller text');
    assert.equal(sortBase('abc', 'efg'), -1, 'Different text');
    assert.equal(sortBase('ABC', 'efg'), -1, 'Bigger text');
    assert.equal(sortBase('abc', 'EFG'), 1, 'Smaller text');
});

QUnit.test('sortNum(x, y)', function(assert) {
    assert.equal(sortNum('1', '2'), -1, 'x=1, y=2');
    assert.equal(sortNum('-10', '2'), -12, 'x=-10, y=2');
    assert.equal(sortNum('3', '-1'), 4, 'x=3, y=-1');
    assert.equal(sortNum('0.1', '0.1'), 0, 'x=0.1, y=0.1');
    assert.equal(sortNum('-0.1', '0.1'), -0.2, 'x=-0.1, y=0.1');
    assert.equal(sortNum('0.1', '-0.1'), 0.2, 'x=-0.1, y=-0.1');
});

QUnit.test('sortDate(x, y)', function(assert) {
    assert.equal(sortDate('25 April 1999', '23 April 1999'), 1, '25 April 1999 - 23 April 1999');
    assert.equal(sortDate('25 April 1999 2:00', '25 April 1999 1:59'), 1,
        '25 April 1999 2:00PM - 25 April 1999 1:59PM');
    assert.equal(sortDate('25 April 1999 2:00', '25 April 1999 2:00'), 0,
        '25 April 1999 2:00PM - 25 April 1999 2:00PM');
    assert.equal(sortDate('25 April 1999 2:00', '25 April 1999 2:01'), -1,
        '25 April 1999 2:00PM - 25 April 1999 2:01PM');
});

QUnit.test('setStatusMessage(message,error)', function(assert) {
    $('body').append('<div id="statusMessage"></div>');
    var message = 'Status Message';

    //isError = false: class = overflow-auto alert alert-warning
    //isError = true: class = overflow-auto alert alert-danger

    setStatusMessage(message, false);
    assert.equal($('#statusMessage').html(), message, 'Normal status message');
    assert.ok(($('#statusMessage').attr('class') === 'overflow-auto alert alert-warning'),
        'No warning');
    setStatusMessage('', false);
    assert.equal($('#statusMessage').html(), '', 'Empty status message');
    assert.ok(($('#statusMessage').attr('class') === 'overflow-auto alert alert-warning'),
        'No warning');
    setStatusMessage(message, true);
    assert.equal($('#statusMessage').html(), message, 'Normal status message');
    assert.ok(($('#statusMessage').attr('class') === 'overflow-auto alert alert-danger'), 'No danger');
    setStatusMessage('', true);
    assert.equal($('#statusMessage').html(), '', 'Normal status message');
    assert.ok(($('#statusMessage').attr('class') === 'overflow-auto alert alert-danger'), 'No danger');
});

QUnit.test('clearStatusMessage()', function(assert) {
    clearStatusMessage();
    assert.equal($('#statusMessage').html(), '', 'Status message cleared');
    assert.ok(($('#statusMessage').css('background-color') === 'rgba(0, 0, 0, 0)' || $(
        '#statusMessage').css('background-color') === 'transparent'), 'No background');
});

QUnit.test('checkEvaluationForm()', function(assert) {
    // N/A, requires elements in the page
    assert.expect(0);
});

QUnit.test('sanitizeGoogleId(googleId)', function(assert) {
    assert.equal(sanitizeGoogleId('test  @Gmail.COM  '), 'test', 'test - valid');
    assert.equal(sanitizeGoogleId('  user@hotmail.com  '), 'user@hotmail.com',
        'user@hotmail.com - valid');
});

QUnit.test('isValidGoogleId(googleId)', function(assert) {
    assert.equal(isValidGoogleId('  test  \t\n'), true, 'test - valid');
    assert.equal(isValidGoogleId('  charile.brown  \t\n'), true, 'charile.brown - valid');
    assert.equal(isValidGoogleId('  big-small_mini  \t\n'), true, 'big-small_mini - valid');

    assert.equal(isValidGoogleId(' hello@GMail.COm \t\n '), false, 'hello@gmail.com - invalid');
    assert.equal(isValidGoogleId('wrong!'), false, 'wrong! - invalid');
    assert.equal(isValidGoogleId('not*correct'), false, 'not*correct - invalid');
    assert.equal(isValidGoogleId('is/not\correct'), false, 'is/not\correct - invalid');
});

QUnit.test('isEmailValid(email)', function(assert) {
    assert.equal(isEmailValid('test@gmail.com'), true, 'test@gmail.com - valid');
    assert.equal(isEmailValid('email'), false, 'email - invalid');
    assert.equal(isEmailValid('email@email'), false, 'email@email - invalid');
    assert.equal(isEmailValid('@yahoo.com'), false, '@yahoo.com - invalid');
    assert.equal(isEmailValid('email.com'), false, 'email.com - invalid');
});

QUnit.test('isNameValid(name)', function(assert) {
    assert.equal(isNameValid('	Tom Jacobs,.	\'()-\/ \\  '), true,
        'alphanumerics, fullstop, comma, round brackets, slashes, apostrophe, hyphen - valid');
    assert.equal(isNameValid(generateRandomString(NAME_MAX_LENGTH)), true,
        'Maximum characters - valid');

    assert.equal(isNameValid(''), false, 'Empty name - invalid');
    assert.equal(isNameValid(generateRandomString(NAME_MAX_LENGTH + 1)), false,
        'Exceed number of maximum characters - invalid');
    assert.equal(isNameValid('Tom! Jacobs'), false, '! character - invalid');
    assert.equal(isNameValid('Tom ^Jacobs'), false, '^ character - invalid');
    assert.equal(isNameValid('Tom#'), false, '# character - invalid');
    assert.equal(isNameValid('&Tom'), false, '& character - invalid');
    assert.equal(isNameValid('J%cobs '), false, '% character - invalid');
    assert.equal(isNameValid('Jacobs*'), false, '* character - invalid');
    assert.equal(isNameValid('	+123	 '), false, '+ character - invalid');
    assert.equal(isNameValid('a b c $ 1 2 3 4'), false, '$ character - invalid');
    assert.equal(isNameValid('1@2@3  456'), false, '@ character - invalid');
    assert.equal(isNameValid('Tom = Tom'), false, '= character - invalid');
    assert.equal(isNameValid('Tom||Jacobs'), false, '| character - invalid');

});

QUnit.test('roundToThreeDp(num)', function(assert) {

    assert.equal(roundToThreeDp(0), 0, 'Zero test');
    assert.equal(roundToThreeDp(1), 1, 'Positive integer test');
    assert.equal(roundToThreeDp(-1), -1, 'Negative integer test');
    assert.equal(roundToThreeDp(1.001), 1.001, 'Three dp positive number test');
    assert.equal(roundToThreeDp(-1.001), -1.001, 'Three dp negative number test');
    assert.equal(roundToThreeDp(1.0015), 1.002, 'Four dp positive number rounding up test');
    assert.equal(roundToThreeDp(1.0011), 1.001, 'Four dp negative number rounding down test');
    assert.equal(roundToThreeDp(-1.0015), -1.002, 'Four dp positive number rounding "up" test');
    assert.equal(roundToThreeDp(-1.0011), -1.001, 'Four dp negative number rounding "down" test');

});

QUnit.test('sanitizeForJs(string)', function(assert) {
    assert.equal(sanitizeForJs(''), '', 'sanitization for empty string');
    assert.equal(sanitizeForJs('Will o\' Wisp'), 'Will o\\\' Wisp', 'sanitization for single quote');
    assert.equal(sanitizeForJs('Will o\'\'\'\'\'\\\\ Wisp'),
        'Will o\\\'\\\'\\\'\\\'\\\'\\\\\\\\ Wisp',
        'sanitization for single quote and slash \\');

});

QUnit.test('isBlank(string)', function(assert) {
    assert.equal(isBlank(''), true, 'Test - empty string');
    assert.equal(isBlank(' '), true, 'Test - single space');
    assert.equal(isBlank('            '), true, 'Test - multiple spaces');

    assert.equal(isBlank('test'), false, 'Test - not blank input');
    assert.equal(isBlank('test    test'), false, 'Test - spaces between strings');
    assert.equal(isBlank('     test'), false, 'Test - string with leading spaces');
    assert.equal(isBlank('test       '), false, 'Test - string with trailing spaces');
    assert.equal(isBlank('     test      '), false, 'Test - string with leading and trailing spaces');

    // type check
    assert.equal(isBlank(), false, 'Test - invalid type: empty input value');
    assert.equal(isBlank(null), false, 'Test - invalid type: null input');
    assert.equal(isBlank(0), false, 'Test - invalid type: number input');
    assert.equal(isBlank({}), false, 'Test - invalid type: object input');
    assert.equal(isBlank(undefined), false, 'Test - invalid type: undefined input');
});
