import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NumScaleQuestionEditAnswerFormComponent } from './num-scale-question-edit-answer-form.component';
import { DEFAULT_NUMSCALE_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

describe('NumScaleQuestionEditAnswerFormComponent', () => {
  let component: NumScaleQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<NumScaleQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.responseDetails = DEFAULT_NUMSCALE_RESPONSE_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
