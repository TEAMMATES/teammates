import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AddingQuestionPanelComponent } from './adding-question-panel.component';
import { FeedbackQuestionType } from '../../../types/api-output';

import { QuestionTypeHelpPathPipe } from '../teammates-common/question-type-help-path.pipe';

describe('AddingQuestionPanelComponent', () => {
  let component: AddingQuestionPanelComponent;
  let fixture: ComponentFixture<AddingQuestionPanelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([])],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddingQuestionPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain anchor element with corresponding search query', () => {
    expect(Object.keys(component.FeedbackQuestionType)[0]).not.toBe(component.FeedbackQuestionType.CONSTSUM);
    const firstQuestionType = Object.keys(component.FeedbackQuestionType)[0] as FeedbackQuestionType;
    const questionTypeHelpPathPipe = new QuestionTypeHelpPathPipe();
    const questionId = questionTypeHelpPathPipe.transform(firstQuestionType);
    const firstAnchorElement = document.querySelector('.btn-group')?.children[1] as HTMLAnchorElement;
    const expectedSearchQuery = `?questionId=${questionId}&section=questions`;
    expect(firstAnchorElement).toHaveProperty('search', expectedSearchQuery);
  });
});
