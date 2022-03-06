import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NumScaleQuestionInstructionComponent } from './num-scale-question-instruction.component';

describe('NumScaleQuestionInstructionComponent', () => {
  let component: NumScaleQuestionInstructionComponent;
  let fixture: ComponentFixture<NumScaleQuestionInstructionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionInstructionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
