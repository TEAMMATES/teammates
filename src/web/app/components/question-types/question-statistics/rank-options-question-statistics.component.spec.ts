import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackRankOptionsResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { Response } from './question-statistics';
import { RankOptionsQuestionStatisticsComponent } from './rank-options-question-statistics.component';

describe('RankOptionsQuestionStatisticsComponent', () => {
  let component: RankOptionsQuestionStatisticsComponent;
  let fixture: ComponentFixture<RankOptionsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RankOptionsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const responses: Response<FeedbackRankOptionsResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Alice',
      recipientTeam: 'Team 1',
      recipientEmail: 'alice@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [1, 2, 3, 4],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      } as FeedbackRankOptionsResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Bob',
      recipientTeam: 'Team 2',
      recipientEmail: 'bob@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [4, 3, 2, 1],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      } as FeedbackRankOptionsResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Charles',
      recipientTeam: 'Team 1',
      recipientEmail: 'charles@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [2, 3, 1, 4],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      } as FeedbackRankOptionsResponseDetails,
    },
  ];

  const responsesSameRank: Response<FeedbackRankOptionsResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Alice',
      recipientTeam: 'Team 1',
      recipientEmail: 'alice@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [1, 2, 3, 4],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      } as FeedbackRankOptionsResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Bob',
      recipientTeam: 'Team 2',
      recipientEmail: 'bob@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [4, 1, 2, 3],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      } as FeedbackRankOptionsResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Charles',
      recipientTeam: 'Team 1',
      recipientEmail: 'charles@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [2, 3, 1, 4],
        questionType: FeedbackQuestionType.RANK_OPTIONS,
      } as FeedbackRankOptionsResponseDetails,
    },
  ];

  it('should calculate statistics correctly', () => {
    component.question.options = ['optionA', 'optionB', 'optionC', 'optionD'];
    component.responses = responses;

    const expectedRankReceivedPerOption: Record<string, number[]> = {
      optionA: [1, 2, 4], optionB: [2, 3, 3],
      optionC: [1, 2, 3], optionD: [1, 4, 4],
    };

    const expectedRankPerOption: Record<string, number> = {
      optionA: 2, optionB: 3, optionC: 1, optionD: 4,
    };

    component.calculateStatistics();

    expect(component.ranksReceivedPerOption).toEqual(expectedRankReceivedPerOption);
    expect(component.rankPerOption).toEqual(expectedRankPerOption);
  });

  it('should calculate statistics correctly if there are equal ranks', () => {
    component.question.options = ['optionA', 'optionB', 'optionC', 'optionD'];
    component.responses = responsesSameRank;

    const expectedRankReceivedPerOption: Record<string, number[]> = {
      optionA: [1, 2, 4], optionB: [1, 2, 3],
      optionC: [1, 2, 3], optionD: [3, 4, 4],
    };

    const expectedRankPerOption: Record<string, number> = {
      optionA: 3, optionB: 1, optionC: 1, optionD: 4,
    };

    component.calculateStatistics();

    expect(component.ranksReceivedPerOption).toEqual(expectedRankReceivedPerOption);
    expect(component.rankPerOption).toEqual(expectedRankPerOption);
  });
});
