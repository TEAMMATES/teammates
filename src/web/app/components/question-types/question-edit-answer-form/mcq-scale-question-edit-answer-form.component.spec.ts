import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { McqScaleQuestionEditAnswerFormComponent } from './mcq-scale-question-edit-answer-form.component';

describe('McqScaleQuestionEditAnswerFormComponent', () => {
  let component: McqScaleQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<McqScaleQuestionEditAnswerFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqScaleQuestionEditAnswerFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqScaleQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
