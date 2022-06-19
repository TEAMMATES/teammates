import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumRangeQuestionEditAnswerFormComponent } from './num-range-question-edit-answer-form.component';

describe('NumRangeQuestionEditAnswerFormComponent', () => {
  let component: NumRangeQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<NumRangeQuestionEditAnswerFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NumRangeQuestionEditAnswerFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NumRangeQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
