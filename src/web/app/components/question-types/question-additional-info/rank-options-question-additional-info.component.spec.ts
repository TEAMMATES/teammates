import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RankOptionsQuestionAdditionalInfoComponent } from './rank-options-question-additional-info.component';
import { FeedbackQuestionType, FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { NO_VALUE } from '../../../../types/feedback-response-details';

describe('RankOptionsQuestionAdditionalInfoComponent', () => {
  let component: RankOptionsQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<RankOptionsQuestionAdditionalInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const rankOptionsQuestionDetails: FeedbackRankOptionsQuestionDetails = {
    minOptionsToBeRanked: NO_VALUE,
    maxOptionsToBeRanked: NO_VALUE,
    areDuplicatesAllowed: false,
    options: ['1', '2', '3'],
    questionType: FeedbackQuestionType.RANK_OPTIONS,
    questionText: '',
  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show options', () => {
    component.questionDetails = rankOptionsQuestionDetails;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

});
