'use strict';

QUnit.module('student.js');

QUnit.test('bindLinkInUnregisteredPage(selector)', function(assert) {

    clearBootboxButtonClickEvent();
    StudentCommon.bindLinksInUnregisteredPage('#test-bootbox-button');
    ensureCorrectModal(assert, '#test-bootbox-button', Const.ModalDialogHeader.UNREGISTERED_STUDENT,
                       Const.ModalDialogText.UNREGISTERED_STUDENT);
    clearBootboxModalStub();

});
