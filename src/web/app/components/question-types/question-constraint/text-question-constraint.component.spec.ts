import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TextQuestionConstraintComponent } from './text-question-constraint.component';

describe('TextQuestionConstraintComponent', () => {
  let component: TextQuestionConstraintComponent;
  let fixture: ComponentFixture<TextQuestionConstraintComponent>;

  beforeEach(async(() => {
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
