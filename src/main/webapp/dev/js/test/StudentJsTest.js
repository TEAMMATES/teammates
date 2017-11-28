import {
    clearBootboxButtonClickEvent,
    clearBootboxModalStub,
    ensureCorrectModal,
} from './CommonTestFunctions';

import {
    Const,
} from '../common/const';

import {
    bindLinksInUnregisteredPage,
} from '../common/student';

QUnit.module('student.js');

QUnit.test('bindLinkInUnregisteredPage(selector)', (assert) => {
    clearBootboxButtonClickEvent();
    bindLinksInUnregisteredPage('#test-bootbox-button');
    ensureCorrectModal(assert, '#test-bootbox-button', Const.ModalDialog.UNREGISTERED_STUDENT.header,
            Const.ModalDialog.UNREGISTERED_STUDENT.text);
    clearBootboxModalStub();
});
