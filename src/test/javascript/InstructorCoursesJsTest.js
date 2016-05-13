QUnit.module('instructorCourse.js');

/* Explanation: This is a dummy function to create an outline view in Eclipse */
function testVerifyCourseData() {}

QUnit.test('verifyCourseData()', function(assert) {
    // 'The method has only two paths and both are tested by UI tests
    expect(0);
});

function testIsCourseIDValidChars() {}
QUnit.test('isCourseIDValidChars(courseId)', function(assert) {
    // valid cases
    assert.equal(isCourseIDValidChars('_$course1.2-3'),
        true,
        'Course Id valid');

    // invalid cases
    assert.equal(isCourseIDValidChars('CS10@@'),
        false,
        'Course Id has @ character');
    assert.equal(isCourseIDValidChars('CS100!'),
        false,
        'Course Id has ! character');
    assert.equal(isCourseIDValidChars('CS100#'),
        false,
        'Course Id has # character');
    assert.equal(isCourseIDValidChars('CS100%'),
        false,
        'Course Id has % character');
    assert.equal(isCourseIDValidChars('CS100^'),
        false,
        'Course Id has ^ character');
    assert.equal(isCourseIDValidChars('CS100&'),
        false,
        'Course Id has & character');
    assert.equal(isCourseIDValidChars('CS100*'),
        false,
        'Course Id has * character');
    assert.equal(isCourseIDValidChars('CS100"'),
        false,
        'Course Id has " character');
    assert.equal(isCourseIDValidChars('CS100\''),
        false,
        'Course Id has \' character');
});

function testGetCourseIdInvalidityInfo() {}

QUnit.test('getCourseIdInvalidityInfo(courseId)', function(assert) {
    // valid cases
    assert.equal(getCourseIdInvalidityInfo(generateRandomString(COURSE_ID_MAX_LENGTH - 1) + '$   '),
        '',
        'Max-length course ID containing special characters and with extra whitespace padding');

    // invalid cases
    assert.equal(getCourseIdInvalidityInfo(''),
        DISPLAY_COURSE_COURSE_ID_EMPTY + '<br>',
        'Course Id empty');
    assert.equal(getCourseIdInvalidityInfo('   '),
        DISPLAY_COURSE_COURSE_ID_EMPTY + '<br>',
        'Course Id only white spaces');
    assert.equal(getCourseIdInvalidityInfo('course*1'),
        DISPLAY_COURSE_INVALID_ID + '<br>',
        'Course Id only white spaces');
    assert.equal(getCourseIdInvalidityInfo(generateRandomString(COURSE_ID_MAX_LENGTH + 1)),
        DISPLAY_COURSE_LONG_ID + '<br>',
        'Course Id too long');
});

function testGetCourseNameInvalidityInfo() {}

QUnit.test('getCourseNameInvalidityInfo(courseId)', function(assert) {
    // valid cases
    assert.equal(getCourseNameInvalidityInfo(generateRandomString('   ' + COURSE_NAME_MAX_LENGTH - 2) + '$*   '),
        '',
        'Max-length course name containing special characters and with extra whitespace padding');

    // invalid cases
    assert.equal(getCourseNameInvalidityInfo('   '),
        DISPLAY_COURSE_COURSE_NAME_EMPTY + '<br>',
        'Course name empty');
    assert.equal(getCourseNameInvalidityInfo(generateRandomString(COURSE_NAME_MAX_LENGTH + 1)),
        DISPLAY_COURSE_LONG_NAME + '<br>',
        'Course name too long');
});

function testCheckAddCourseParam() {}

QUnit.test('checkAddCourseParam(courseID, courseName)', function(assert) {

    assert.equal(checkAddCourseParam('valid.course.id', 'Software Engineering'),
        '',
        'All valid values');

    assert.equal(checkAddCourseParam('', 'Software Engineering'),
        DISPLAY_COURSE_COURSE_ID_EMPTY + '<br>',
        'Course ID empty');

    assert.equal(checkAddCourseParam('valid.course-id', generateRandomString(COURSE_NAME_MAX_LENGTH + 1)),
        DISPLAY_COURSE_LONG_NAME + '<br>',
        'Course name too long');

    assert.equal(checkAddCourseParam('', '', ''),
        DISPLAY_COURSE_COURSE_ID_EMPTY + '<br>' +
        DISPLAY_COURSE_COURSE_NAME_EMPTY + '<br>',
        'both values are invalid');

    assert.equal(checkAddCourseParam('invalid course id', generateRandomString(COURSE_NAME_MAX_LENGTH + 1), 'googid|Instructor1|'),
        DISPLAY_COURSE_INVALID_ID + '<br>' +
        DISPLAY_COURSE_LONG_NAME + '<br>',
        'both values are invalid');

});
