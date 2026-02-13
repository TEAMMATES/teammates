import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionQuestionEditAnswerFormComponent } from './contribution-question-edit-answer-form.component';

describe('ContributionQuestionEditAnswerFormComponent', () => {
  let component: ContributionQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ContributionQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
