import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RankOptionsQuestionEditAnswerFormComponent } from './rank-options-question-edit-answer-form.component';

describe('RankOptionsQuestionEditAnswerFormComponent', () => {
  let component: RankOptionsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RankOptionsQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
