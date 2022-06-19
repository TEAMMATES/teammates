import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionConstraintComponent } from './num-range-question-constraint.component';

describe('NumRangeQuestionConstraintComponent', () => {
  let component: NumRangeQuestionConstraintComponent;
  let fixture: ComponentFixture<NumRangeQuestionConstraintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionConstraintComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
