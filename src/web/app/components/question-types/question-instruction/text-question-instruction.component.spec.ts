import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TextQuestionInstructionComponent } from './text-question-instruction.component';

describe('TextQuestionInstructionComponent', () => {
  let component: TextQuestionInstructionComponent;
  let fixture: ComponentFixture<TextQuestionInstructionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TextQuestionInstructionComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionInstructionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
