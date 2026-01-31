import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumScaleQuestionInstructionComponent } from './num-scale-question-instruction.component';

describe('NumScaleQuestionInstructionComponent', () => {
  let component: NumScaleQuestionInstructionComponent;
  let fixture: ComponentFixture<NumScaleQuestionInstructionComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
