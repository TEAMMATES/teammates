/* global
isStudentTeamNameValid:false, isStudentInputValid:false, generateRandomString:false
executeCopyCommand:false, selectElementContents:false

TEAMNAME_MAX_LENGTH:false, NAME_MAX_LENGTH:false
*/

QUnit.module('instructor.js');

QUnit.test('isStudentTeamNameValid(teamname)', (assert) => {
    assert.equal(isStudentTeamNameValid('Team1_-)(&*^%$#@!.'), true, 'Team1_-)(&*^%$#@!. - valid');
    assert.equal(isStudentTeamNameValid(generateRandomString(TEAMNAME_MAX_LENGTH)), true, 'Maximum characters - valid');
    assert.equal(isStudentTeamNameValid(generateRandomString(TEAMNAME_MAX_LENGTH + 1)), false,
                 'Exceed maximum number of characters - invalid');
});

QUnit.test('isStudentInputValid(editName, editTeamName, editEmail)', (assert) => {
    assert.equal(isStudentInputValid('Bob', 'Bob\'s Team', 'Bob.Team@hotmail.com'), true, 'Valid Input');
    assert.equal(isStudentInputValid('', 'Bob\'s Team', 'Bob.Team@hotmail.com'), false, 'Empty name - invalid');
    assert.equal(isStudentInputValid('Bob', '', 'Bob@gmail.com'), false, 'Empty teamname - invalid');
    assert.equal(isStudentInputValid('Bob', 'Bob\'s Team', ''), false, 'Empty email - invalid');
    assert.equal(isStudentInputValid('Bob', 'Bob\'s Team', 'qwerty'), false, 'Invalid email');
    assert.equal(isStudentInputValid('Billy', generateRandomString(TEAMNAME_MAX_LENGTH + 1), 'd.Team@hotmail.com'), false,
                 'Invalid teamname');
    assert.equal(isStudentInputValid(generateRandomString(NAME_MAX_LENGTH + 1), 'Bob\'s Team', ''), false, 'invalid name');
});

QUnit.test('executeCopyCommand()', (assert) => {
    // override execCommand with mock
    const browserImplementation = document.execCommand;
    document.execCommand = function (command) {
        assert.equal(command, 'copy', 'Copy command is executed');
    };

    executeCopyCommand();

    // restore back the original execCommand
    document.execCommand = browserImplementation;
});

QUnit.test('selectElementContents(el)', (assert) => {
    window.getSelection().removeAllRanges();

    const $contentsToSelect = $('#team_all');
    selectElementContents($contentsToSelect.get(0));

    const selectedContents = window.getSelection().toString();
    assert.equal(selectedContents, $contentsToSelect.text().replace(/ /gi, ''), 'Contents are selected');

    window.getSelection().removeAllRanges();
});
