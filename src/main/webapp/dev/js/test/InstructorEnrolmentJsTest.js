import {
    getUpdatedHeaderString,
    getUserDataRows,
    getUpdatedData,
} from '../main/instructorCourseEnrollHelper';

QUnit.module('instructorCourseEnrollPage.js');

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

QUnit.test('getUpdatedData(spreadsheetDataRows, data)', (assert) => {
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

    const data = getUpdatedData(spreadsheetDataRows);
    assert.deepEqual(data, expectedData, 'Data successfully updated');
});
