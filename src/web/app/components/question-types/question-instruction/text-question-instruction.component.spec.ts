import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionInstructionComponent } from './text-question-instruction.component';

describe('TextQuestionInstructionComponent', () => {
  let component: TextQuestionInstructionComponent;
  let fixture: ComponentFixture<TextQuestionInstructionComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
