import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContributionQuestionInstructionComponent } from './contribution-question-instruction.component';

describe('ContributionQuestionInstructionComponent', () => {
  let component: ContributionQuestionInstructionComponent;
  let fixture: ComponentFixture<ContributionQuestionInstructionComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
