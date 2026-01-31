import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionEditDetailsFormComponent } from './text-question-edit-details-form.component';

describe('TextQuestionEditDetailsFormComponent', () => {
  let component: TextQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<TextQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
