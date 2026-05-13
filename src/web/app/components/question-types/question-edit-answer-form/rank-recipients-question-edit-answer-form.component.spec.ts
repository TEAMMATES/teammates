import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankRecipientsQuestionEditAnswerFormComponent } from './rank-recipients-question-edit-answer-form.component';
import { DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

describe('RankRecipientsQuestionEditAnswerFormComponent', () => {
  let component: RankRecipientsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.responseDetails = DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
