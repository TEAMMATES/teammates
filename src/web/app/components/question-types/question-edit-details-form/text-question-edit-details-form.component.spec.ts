import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionEditDetailsFormComponent } from './text-question-edit-details-form.component';
import { DEFAULT_TEXT_QUESTION_DETAILS } from '../../../../types/default-question-structs';

describe('TextQuestionEditDetailsFormComponent', () => {
  let component: TextQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<TextQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    component.model = DEFAULT_TEXT_QUESTION_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
