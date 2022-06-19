import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionInstructionComponent } from './num-range-question-instruction.component';

describe('NumRangeQuestionInstructionComponent', () => {
  let component: NumRangeQuestionInstructionComponent;
  let fixture: ComponentFixture<NumRangeQuestionInstructionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionInstructionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
