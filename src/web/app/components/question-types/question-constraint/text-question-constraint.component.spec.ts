import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TextQuestionConstraintComponent } from './text-question-constraint.component';

describe('TextQuestionConstraintComponent', () => {
  let component: TextQuestionConstraintComponent;
  let fixture: ComponentFixture<TextQuestionConstraintComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TextQuestionConstraintComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TextQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
