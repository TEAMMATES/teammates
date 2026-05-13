import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MsqQuestionEditDetailsFormComponent } from './msq-question-edit-details-form.component';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';

describe('MsqQuestionEditDetailsFormComponent', () => {
  let component: MsqQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<MsqQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    component.model = DEFAULT_MSQ_QUESTION_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
