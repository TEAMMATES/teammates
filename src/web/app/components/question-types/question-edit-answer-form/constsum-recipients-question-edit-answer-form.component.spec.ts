import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumRecipientsQuestionEditAnswerFormComponent } from './constsum-recipients-question-edit-answer-form.component';
import { DEFAULT_CONSTSUM_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

describe('ConstsumRecipientsQuestionEditAnswerFormComponent', () => {
  let component: ConstsumRecipientsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.responseDetails = DEFAULT_CONSTSUM_RESPONSE_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
