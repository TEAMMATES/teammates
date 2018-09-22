import {
    getUpdatedHeaderString,
    getUserDataRows,
    spreadsheetDataRowsToHandsontableData,
    ajaxDataToHandsontableData,
} from '../common/instructorEnroll';

QUnit.module('instructorEnroll.js');

QUnit.test('getUpdatedHeaderString(handsontableColHeader)', (assert) => {
    assert.expect(1);
    const colHeaders = ['Section', 'Team', 'Name', 'Email', 'Comments'];
    const expectedHeaderString = 'Section|Team|Name|Email|Comments\n';
    assert.equal(getUpdatedHeaderString(colHeaders), expectedHeaderString, 'Correct header string generated');
});

QUnit.test('getUserDataRows(spreadsheetData)', (assert) => {
    assert.expect(1);
    const spreadsheetData = [['Section1', 'Team1', 'Name1', 'Email1', 'Comments1'],
        [null, 'Team2', 'Name2', 'Email2', 'Comments2'],
        ['Section3', null, 'Name3', 'Email3', 'Comments3'],
        [null, null, null, null, null], // this is an empty row and should be filtered
        ['Section4', 'Team4', null, 'Email4', 'Comments4'],
        ['Section5', 'Team5', 'Name5', null, 'Comments5'],
        ['Section6', 'Team6', 'Name6', 'Email6', null]];
    const expectedUserDataRows = 'Section1|Team1|Name1|Email1|Comments1\n'
        + '|Team2|Name2|Email2|Comments2\n'
        + 'Section3||Name3|Email3|Comments3\n'
        + 'Section4|Team4||Email4|Comments4\n'
        + 'Section5|Team5|Name5||Comments5\n'
        + 'Section6|Team6|Name6|Email6|';
    assert.equal(getUserDataRows(spreadsheetData), expectedUserDataRows, 'Correct user data string generated');
});

QUnit.test('spreadsheetDataRowsToHandsontableData(spreadsheetDataRows)', (assert) => {
    assert.expect(1);
    const spreadsheetDataRows = ['Section1|Team1|Name1|Email1|Comments1',
        '|Team2|Name2|Email2|Comments2',
        'Section3||Name3|Email3|Comments3',
        'Section4|Team4||Email4|Comments4',
        'Section5|Team5|Name5||Comments5',
        'Section6|Team6|Name6|Email6|'];

    const expectedData = [['Section1', 'Team1', 'Name1', 'Email1', 'Comments1'],
        ['', 'Team2', 'Name2', 'Email2', 'Comments2'],
        ['Section3', '', 'Name3', 'Email3', 'Comments3'],
        ['Section4', 'Team4', '', 'Email4', 'Comments4'],
        ['Section5', 'Team5', 'Name5', '', 'Comments5'],
        ['Section6', 'Team6', 'Name6', 'Email6', '']];

    const data = spreadsheetDataRowsToHandsontableData(spreadsheetDataRows);
    assert.deepEqual(data, expectedData, 'Data successfully updated');
});

QUnit.test('ajaxDataToHandsontableData(studentsData, handsontableColHeader)', (assert) => {
    assert.expect(1);
    const studentsData = [{
        comments: 'testComments1',
        course: 'testCourse1',
        email: 'testEmail1@example.com',
        googleId: '',
        key: 'testEmail1@example.com%test%842516281',
        lastName: 'testName1',
        name: 'testName1',
        section: 'testSection1',
        team: 'testTeam1',
    }, {
        comments: '',
        course: 'testCourse2',
        email: 'testEmail2@example.com',
        googleId: '',
        key: 'testEmail2@example.com%test%1208109881',
        lastName: 'testName2',
        name: 'testName2',
        section: 'testSection2',
        team: 'testTeam2',
    }];
    const handsontableColHeader = ['Section', 'Team', 'Name', 'Email', 'Comments'];
    const expectedStudentsData = [['testSection1', 'testTeam1', 'testName1', 'testEmail1@example.com', 'testComments1'],
        ['testSection2', 'testTeam2', 'testName2', 'testEmail2@example.com', '']];
    const data = ajaxDataToHandsontableData(studentsData, handsontableColHeader);
    assert.deepEqual(data, expectedStudentsData, 'Retrieved existing students\' data successfully');
});
