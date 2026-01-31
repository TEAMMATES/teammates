import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NumScaleQuestionEditAnswerFormComponent } from './num-scale-question-edit-answer-form.component';

describe('NumScaleQuestionEditAnswerFormComponent', () => {
  let component: NumScaleQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<NumScaleQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
