import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RankOptionsQuestionEditAnswerFormComponent } from './rank-options-question-edit-answer-form.component';
import { DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS } from '../../../../types/default-question-structs';

describe('RankOptionsQuestionEditAnswerFormComponent', () => {
  let component: RankOptionsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<RankOptionsQuestionEditAnswerFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    component.responseDetails = DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
