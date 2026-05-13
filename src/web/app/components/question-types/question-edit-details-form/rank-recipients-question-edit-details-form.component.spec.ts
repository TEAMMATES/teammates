import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankRecipientsQuestionEditDetailsFormComponent } from './rank-recipients-question-edit-details-form.component';
import { DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

describe('RankRecipientsQuestionEditDetailsFormComponent', () => {
  let component: RankRecipientsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    component.model = DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
