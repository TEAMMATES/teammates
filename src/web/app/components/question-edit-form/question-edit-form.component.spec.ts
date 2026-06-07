import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { QuestionEditFormModel } from './question-edit-form-model';
import { QuestionEditFormComponent } from './question-edit-form.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { EXAMPLE_ESSAY_QUESTION_MODEL } from '../../pages-help/instructor-help-page/instructor-help-questions-section/instructor-help-questions-data';

describe('QuestionEditFormComponent', () => {
  let component: QuestionEditFormComponent;
  let fixture: ComponentFixture<QuestionEditFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(QuestionEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('set up with EXAMPLE_ESSAY_QUESTION_MODEL', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL;
    const model: QuestionEditFormModel = component.model;
    expect(model).toBe(EXAMPLE_ESSAY_QUESTION_MODEL);
    expect(model.isUsingOtherFeedbackPath).toBeFalsy();
    expect(model.commonVisibilitySettingName).toBeTruthy();
    expect(model.isUsingOtherVisibilitySetting).toBeFalsy();
    expect(component.isCustomFeedbackVisibilitySettingAllowed).toBeTruthy();
  });

  it('triggerModelChange with EXAMPLE_ESSAY_QUESTION_MODEL', () => {
    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL;
    component.formModelChange.subscribe((data: QuestionEditFormModel) => {
      component.formModel = data;
    });
    const testStr = 'Hello World';
    component.triggerModelChange('questionNumber', 2);
    component.triggerModelChange('questionBrief', testStr);
    component.triggerModelChange('questionDescription', testStr);

    const model: QuestionEditFormModel = component.model;
    expect(model.questionNumber).toBe(2);
    expect(model.questionBrief).toBe(testStr);
    expect(model.questionDescription).toBe(testStr);
  });

  it('saveQuestionHandler saves directly when there are no responses', () => {
    const feedbackSessionsService: FeedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    const simpleModalService: SimpleModalService = TestBed.inject(SimpleModalService);
    const hasResponsesSpy = vi
      .spyOn(feedbackSessionsService, 'hasResponsesForQuestion')
      .mockReturnValue(of({ hasResponses: false }));
    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal');
    const saveSpy = vi.fn();

    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL;
    component.model.isFeedbackPathChanged = true;
    component.saveExistingQuestionEvent.subscribe(saveSpy);

    component.saveQuestionHandler();

    expect(hasResponsesSpy).toHaveBeenCalledWith(EXAMPLE_ESSAY_QUESTION_MODEL.feedbackQuestionId);
    expect(modalSpy).not.toHaveBeenCalled();
    expect(saveSpy).toHaveBeenCalledTimes(1);
  });

  it('saveQuestionHandler asks for confirmation when responses exist', async () => {
    const feedbackSessionsService: FeedbackSessionsService = TestBed.inject(FeedbackSessionsService);
    const simpleModalService: SimpleModalService = TestBed.inject(SimpleModalService);
    const hasResponsesSpy = vi
      .spyOn(feedbackSessionsService, 'hasResponsesForQuestion')
      .mockReturnValue(of({ hasResponses: true }));
    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(createMockNgbModalRef());
    const saveSpy = vi.fn();

    component.formModel = EXAMPLE_ESSAY_QUESTION_MODEL;
    component.model.isFeedbackPathChanged = true;
    component.saveExistingQuestionEvent.subscribe(saveSpy);

    component.saveQuestionHandler();
    await fixture.whenStable();

    expect(hasResponsesSpy).toHaveBeenCalledWith(EXAMPLE_ESSAY_QUESTION_MODEL.feedbackQuestionId);
    expect(modalSpy).toHaveBeenCalledWith(
      'Save the question?',
      SimpleModalType.DANGER,
      expect.stringContaining('all existing responses to be deleted'),
    );
    expect(saveSpy).toHaveBeenCalledTimes(1);
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
