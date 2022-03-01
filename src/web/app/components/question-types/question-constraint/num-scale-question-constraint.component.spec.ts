import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NumScaleQuestionConstraintComponent } from './num-scale-question-constraint.component';

describe('NumScaleQuestionConstraintComponent', () => {
  let component: NumScaleQuestionConstraintComponent;
  let fixture: ComponentFixture<NumScaleQuestionConstraintComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionConstraintComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
