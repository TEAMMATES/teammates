import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RubricQuestionEditAnswerFormComponent } from './rubric-question-edit-answer-form.component';

describe('RubricQuestionEditAnswerFormComponent', () => {
  let component: RubricQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RubricQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RubricQuestionEditAnswerFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
