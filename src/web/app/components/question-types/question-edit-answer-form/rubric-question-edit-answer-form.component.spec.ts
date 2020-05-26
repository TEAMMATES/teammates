import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RubricQuestionEditAnswerFormComponent } from './rubric-question-edit-answer-form.component';

describe('RubricQuestionEditAnswerFormComponent', () => {
  let component: RubricQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RubricQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
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
