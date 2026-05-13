import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RankOptionsQuestionEditDetailsFormComponent } from './rank-options-question-edit-details-form.component';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

describe('RankOptionsQuestionEditDetailsFormComponent', () => {
  let component: RankOptionsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RankOptionsQuestionEditDetailsFormComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    component.model = DEFAULT_RANK_OPTIONS_QUESTION_DETAILS();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
