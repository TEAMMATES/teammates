import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { QuestionEditFormModel } from './question-edit-form-model';
import { QuestionEditFormComponent } from './question-edit-form.component';
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
    component.triggerModelChange('isQuestionHasResponses', true);

    const model: QuestionEditFormModel = component.model;
    expect(model.questionNumber).toBe(2);
    expect(model.questionBrief).toBe(testStr);
    expect(model.questionDescription).toBe(testStr);
    expect(model.isQuestionHasResponses).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
