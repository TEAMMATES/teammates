import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionQuestionEditAnswerFormComponent } from './contribution-question-edit-answer-form.component';
import { DEFAULT_CONTRIBUTION_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

describe('ContributionQuestionEditAnswerFormComponent', () => {
  let component: ContributionQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ContributionQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.responseDetails = DEFAULT_CONTRIBUTION_RESPONSE_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
