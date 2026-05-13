import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MsqQuestionEditAnswerFormComponent } from './msq-question-edit-answer-form.component';
import { DEFAULT_MSQ_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

describe('MsqQuestionEditAnswerFormComponent', () => {
  let component: MsqQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.responseDetails = DEFAULT_MSQ_RESPONSE_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
