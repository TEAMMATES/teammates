import {
    Const,
} from '../common/const';

import {
    Instructor,
    InstructorError,
    createRowForResultTable,
} from '../main/adminHome';

QUnit.module('AdminHome.js');

QUnit.assert.contains = function (context, toIdentify, message) {
    const actual = context.indexOf(toIdentify) > -1;
    this.pushResult({
        result: actual,
        actual,
        expected: toIdentify,
        message,
    });
};

QUnit.test('createRowForResultTable(name, email, institution, isSuccess, status)', (assert) => {
    const boolIndex = 3;
    const successClass = 'success';
    const failureClass = 'danger';
    function testCreateRowForResultTable(isSuccess) {
        const testProperties = ['testName', 'testMail', 'testInstitution', isSuccess, 'testStatus'];
        const result = createRowForResultTable(...testProperties);
        const expected = testProperties.slice(); // deep clone testProperties
        expected[boolIndex] = isSuccess ? successClass : failureClass;
        expected.forEach((property) => {
            assert.contains(result, property, `should contain ${property}`);
        });
    }
    [true, false].forEach(testCreateRowForResultTable);
});

QUnit.test('test conversion from instructor list to pipe-separated string', (assert) => {
    assert.expect(1);
    const instructorList = [
        Instructor.create('testName1', 'testEmail1@email.com', 'testInstitution1'),
        Instructor.create('testName2', 'testEmail2@email.com', 'testInstitution2'),
        Instructor.create('testName3', 'testEmail3@email.com', 'testInstitution3'),
    ];
    const instructorStringExpected = 'testName1 | testEmail1@email.com | testInstitution1\n'
            + 'testName2 | testEmail2@email.com | testInstitution2\n'
            + 'testName3 | testEmail3@email.com | testInstitution3';
    assert.equal(Instructor.allToString(instructorList), instructorStringExpected);
});

QUnit.test('test conversion from pipe-separated string to instructor list', (assert) => {
    assert.expect(1);
    const instructorString = 'testName1 | testEmail1@email.com | testInstitution1\n'
        + 'testName2  |   testEmail2@email.com | testInstitution2\n'
        + '     \t       \n'
        + 'testName3| testEmail3@email.com   |  testInstitution3\n'
        + '      \t                      \n';
    const instructorListExpected = [
        Instructor.create('testName1', 'testEmail1@email.com', 'testInstitution1'),
        Instructor.create('testName2', 'testEmail2@email.com', 'testInstitution2'),
        Instructor.create('testName3', 'testEmail3@email.com', 'testInstitution3'),
    ];
    assert.deepEqual(Instructor.allFromString(instructorString), instructorListExpected);
});

QUnit.test('test conversion from tab- and pipe-separated string to instructor list', (assert) => {
    assert.expect(1);
    const instructorString = 'testName1 | testEmail1@email.com | testInstitution1\n'
        + 'testName2\ttestEmail2@email.com\ttestInstitution2\n'
        + '\n'
        + ' \t  \n'
        + 'testName3 | testEmail3@email.com | testInstitution3';
    const instructorListExpected = [
        Instructor.create('testName1', 'testEmail1@email.com', 'testInstitution1'),
        Instructor.create('testName2', 'testEmail2@email.com', 'testInstitution2'),
        Instructor.create('testName3', 'testEmail3@email.com', 'testInstitution3'),
    ];
    assert.deepEqual(Instructor.allFromString(instructorString), instructorListExpected);
});

QUnit.test('test conversion from erroneous pipe-separated string to instructor list', (assert) => {
    assert.expect(1);

    const str1 = 'testName1  |   testEmail1@email.com | testInstitution1';
    const str2 = 'testName2 ||||| testInstitution2';
    const str3 = 'testName3|  testEmail3@email.com     |testInstitution3';
    const str4 = 'testName4 | testEmail4@email.com   | testInstitution4 | ????';
    const str5 = 'testName5| testEmail5@email.com | testInstitution5';
    const str6 = 'testName6 testEmail6@email.com | testInstitution6';

    const instructorString = [str1, str2, str3, str4, str5, str6].join('\n');
    const instructorListExpected = [
        Instructor.create('testName1', 'testEmail1@email.com', 'testInstitution1'),
        new InstructorError(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str2),
        Instructor.create('testName3', 'testEmail3@email.com', 'testInstitution3'),
        new InstructorError(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str4),
        Instructor.create('testName5', 'testEmail5@email.com', 'testInstitution5'),
        new InstructorError(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str6),
    ];
    assert.deepEqual(Instructor.allFromString(instructorString), instructorListExpected);
});
