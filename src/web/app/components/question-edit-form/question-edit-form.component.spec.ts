import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import SpyInstance = jest.SpyInstance;

import { SimpleModalService } from '../../../services/simple-modal.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { mockTinyMceUuid } from '../../../test-helpers/mock-tinymce-uuid';
import { FeedbackParticipantType, NumberOfEntitiesToGiveFeedbackToSetting } from '../../../types/api-output';
import { FeedbackVisibilityType } from '../../../types/api-request';
import {
  EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES,
} from '../../pages-help/instructor-help-page/instructor-help-questions-section/instructor-help-questions-data';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { FeedbackPathPanelModule } from '../feedback-path-panel/feedback-path-panel.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import {
  QuestionEditBriefDescriptionFormModule,
} from '../question-edit-brief-description-form/question-edit-brief-description-form.module';
import {
  QuestionEditDetailsFormModule,
} from '../question-types/question-edit-details-form/question-edit-details-form.module';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { VisibilityMessagesModule } from '../visibility-messages/visibility-messages.module';
import { VisibilityPanelModule } from '../visibility-panel/visibility-panel.module';
import { QuestionEditFormModel } from './question-edit-form-model';
import { QuestionEditFormComponent } from './question-edit-form.component';

describe('QuestionEditFormComponent', () => {
  let component: QuestionEditFormComponent;
  let fixture: ComponentFixture<QuestionEditFormComponent>;
  let simpleModalService: SimpleModalService;

  /**
   * Helper function to check if a model is equal to
   * EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES.
   */
  const verifyEqualsToExampleQuestionModel: (model: QuestionEditFormModel) => void =
  (model: QuestionEditFormModel): void => {
    expect(model).toStrictEqual(EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES);
    expect(model.isUsingOtherFeedbackPath).toBeFalsy();
    expect(model.commonVisibilitySettingName).toBeTruthy();
    expect(model.isUsingOtherVisibilitySetting).toBeFalsy();
    expect(component.isCustomFeedbackVisibilitySettingAllowed).toBeTruthy();
  };

  mockTinyMceUuid();

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        QuestionEditFormComponent,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        TeammatesCommonModule,
        AjaxLoadingModule,
        QuestionEditBriefDescriptionFormModule,
        QuestionEditDetailsFormModule,
        NgbModule,
        FeedbackPathPanelModule,
        VisibilityMessagesModule,
        VisibilityPanelModule,
        BrowserAnimationsModule,
        PanelChevronModule,
      ],
      providers: [
        SimpleModalService,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionEditFormComponent);
    simpleModalService = TestBed.inject(SimpleModalService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('set up with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;
    const model: QuestionEditFormModel = component.model;
    verifyEqualsToExampleQuestionModel(model);

    const unsavedModel: QuestionEditFormModel = component.model;
    verifyEqualsToExampleQuestionModel(unsavedModel);
    expect(unsavedModel).toStrictEqual(model);
  });

  it('triggerUnsavedModelChange with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    const testStr: string = 'Hello World';
    component.triggerUnsavedModelChange('questionNumber', 2);
    component.triggerUnsavedModelChange('questionBrief', testStr);
    component.triggerUnsavedModelChange('questionDescription', testStr);
    component.triggerUnsavedModelChange('isQuestionHasResponses', true);

    const model: QuestionEditFormModel = component.model;
    verifyEqualsToExampleQuestionModel(model);

    const unsavedModel: QuestionEditFormModel = component.unsavedModel;
    expect(unsavedModel.questionNumber).toBe(2);
    expect(unsavedModel.questionBrief).toBe(testStr);
    expect(unsavedModel.questionDescription).toBe(testStr);
    expect(unsavedModel.isQuestionHasResponses).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('triggerUnsavedModelChangeBatch with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    const testObj: Partial<QuestionEditFormModel> = {
      giverType: FeedbackParticipantType.STUDENTS,
      recipientType: FeedbackParticipantType.INSTRUCTORS,
      isUsingOtherFeedbackPath: false,
      numberOfEntitiesToGiveFeedbackToSetting:
      NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
      customNumberOfEntitiesToGiveFeedbackTo: undefined,

    };
    component.triggerUnsavedModelChangeBatch(testObj);

    const model: QuestionEditFormModel = component.model;
    verifyEqualsToExampleQuestionModel(model);

    const unsavedModel: QuestionEditFormModel = component.unsavedModel;
    expect(unsavedModel.giverType).toBe(testObj.giverType);
    expect(unsavedModel.recipientType).toBe(testObj.recipientType);
    expect(unsavedModel.isUsingOtherFeedbackPath).toBeFalsy();
    expect(unsavedModel.numberOfEntitiesToGiveFeedbackToSetting)
    .toBe(testObj.numberOfEntitiesToGiveFeedbackToSetting);
    expect(unsavedModel.customNumberOfEntitiesToGiveFeedbackTo)
    .toBe(testObj.customNumberOfEntitiesToGiveFeedbackTo);
  });

  it('discardChangesHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES', async () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isUsingOtherFeedbackPath', true);
    component.triggerUnsavedModelChange('showResponsesTo', [FeedbackVisibilityType.RECIPIENT]);
    component.triggerUnsavedModelChange('showGiverNameTo', [FeedbackVisibilityType.RECIPIENT]);
    component.triggerUnsavedModelChange('showRecipientNameTo', [FeedbackVisibilityType.RECIPIENT]);
    component.triggerUnsavedModelChange('giverType', FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.discardChangesHandler(false);
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Discard unsaved edits?', SimpleModalType.WARNING,
    'Warning: Any unsaved changes will be lost',
    { cancelMessage: 'No, go back to editing' });
  });

  it('discardChangesHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where changes reverted', async () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('giverType', FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);
    component.triggerUnsavedModelChange('giverType', FeedbackParticipantType.STUDENTS);

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.discardChangesHandler(false);
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(0);
  });

  it('saveQuestionHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where feedback path changed', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isQuestionHasResponses', true);
    component.triggerUnsavedModelChange('recipientType', FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.saveQuestionHandler();
    expect(modalSpy).toHaveBeenCalledTimes(1);

    const modalContent: string = `
            <p>You seem to have changed the feedback path settings of this question. Please note that changing the
            feedback path will cause <b>all existing responses to be deleted.</b> Proceed?</p>
        `;
    expect(modalSpy).toHaveBeenCalledWith('Save the question?', SimpleModalType.DANGER, modalContent);
  });

  it('saveQuestionHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where feedback path reverted', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isQuestionHasResponses', true);
    component.triggerUnsavedModelChange('recipientType', FeedbackParticipantType.STUDENTS_EXCLUDING_SELF);
    component.triggerUnsavedModelChange('recipientType', FeedbackParticipantType.OWN_TEAM_MEMBERS);

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.saveQuestionHandler();
    expect(modalSpy).toHaveBeenCalledTimes(0);
  });

  it('saveQuestionHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where question details changed', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isQuestionHasResponses', true);
    component.triggerUnsavedModelChange('questionDescription', 'A new description for this question');

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.saveQuestionHandler();

    expect(modalSpy).toHaveBeenCalledTimes(1);

    const modalContent: string = `
            <p>Editing question settings in a way that potentially affects the validity of existing responses <b> may
            cause all the existing responses for this question to be deleted.</b> Proceed?</p>
        `;
    expect(modalSpy).toHaveBeenCalledWith('Save the question?', SimpleModalType.DANGER, modalContent);
  });

  it('saveQuestionHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where question details reverted', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isQuestionHasResponses', true);
    component.triggerUnsavedModelChange('questionDescription', 'A new description for this question');
    component.triggerUnsavedModelChange('questionDescription', '');

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.saveQuestionHandler();
    expect(modalSpy).toHaveBeenCalledTimes(0);
  });

  it('saveQuestionHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where visibility changed', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isQuestionHasResponses', true);
    component.triggerUnsavedModelChange('showRecipientNameTo',
    [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.STUDENTS]);

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.saveQuestionHandler();
    expect(modalSpy).toHaveBeenCalledTimes(1);

    const modalContent: string = `
            <p>You seem to have changed the visibility settings of this question. Please note that <b>the existing
            responses will remain but their visibility will be changed as per the new visibility settings.</b>
            Proceed?</p>
        `;
    expect(modalSpy).toHaveBeenCalledWith('Save the question?', SimpleModalType.WARNING, modalContent);
  });

  it('saveQuestionHandler with EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES where visibility reverted', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL_WITH_RESPONSES;

    component.triggerUnsavedModelChange('isQuestionHasResponses', true);
    component.triggerUnsavedModelChange('showRecipientNameTo',
    [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT, FeedbackVisibilityType.STUDENTS]);
    component.triggerUnsavedModelChange('showRecipientNameTo',
    [FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.RECIPIENT]);

    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.saveQuestionHandler();
    expect(modalSpy).toHaveBeenCalledTimes(0);
  });
});
