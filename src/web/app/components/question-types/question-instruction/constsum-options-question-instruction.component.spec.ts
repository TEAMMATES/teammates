import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumOptionsQuestionInstructionComponent } from './constsum-options-question-instruction.component';

describe('ConstsumOptionsQuestionInstructionComponent', () => {
  let component: ConstsumOptionsQuestionInstructionComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionInstructionComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
