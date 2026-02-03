import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankRecipientsQuestionEditAnswerFormComponent } from './rank-recipients-question-edit-answer-form.component';

describe('RankRecipientsQuestionEditAnswerFormComponent', () => {
  let component: RankRecipientsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
