import {
    clearBootboxButtonClickEvent,
    clearBootboxModalStub,
    ensureCorrectModal,
} from './CommonTestFunctions.es6';

import {
    Const,
} from '../common/const.es6';

import {
    bindLinksInUnregisteredPage,
} from '../common/student.es6';

QUnit.module('student.js');

QUnit.test('bindLinkInUnregisteredPage(selector)', (assert) => {
    clearBootboxButtonClickEvent();
    bindLinksInUnregisteredPage('#test-bootbox-button');
    ensureCorrectModal(assert, '#test-bootbox-button', Const.ModalDialog.UNREGISTERED_STUDENT.header,
                       Const.ModalDialog.UNREGISTERED_STUDENT.text);
    clearBootboxModalStub();
});
