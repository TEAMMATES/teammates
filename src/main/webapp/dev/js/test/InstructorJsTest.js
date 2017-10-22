import {
    executeCopyCommand,
    selectElementContents,
} from '../common/instructor';

QUnit.module('instructor.js');

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
