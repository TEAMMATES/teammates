import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConstsumRecipientsQuestionStatisticsComponent } from './constsum-recipients-question-statistics.component';
import { Response } from './question-statistics';
import { FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';

describe('ConstsumRecipientsQuestionStatisticsComponent', () => {
  let component: ConstsumRecipientsQuestionStatisticsComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const responses: Response<FeedbackConstantSumResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Bob',
      recipientTeam: 'Team 2',
      recipientEmail: 'bob@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [2],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Bob',
      recipientTeam: 'Team 2',
      recipientEmail: 'bob@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [3],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'David',
      giverTeam: 'Team 1',
      giverEmail: 'david@gmail.com',
      giverSection: '',
      recipient: 'Bob',
      recipientTeam: 'Team 2',
      recipientEmail: 'bob@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [5],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
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
        answers: [5],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Emma',
      recipientTeam: 'Team 2',
      recipientEmail: 'emma@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [9],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Emma',
      recipientTeam: 'Team 2',
      recipientEmail: 'emma@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [6],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'David',
      giverTeam: 'Team 1',
      giverEmail: 'david@gmail.com',
      giverSection: '',
      recipient: 'Emma',
      recipientTeam: 'Team 2',
      recipientEmail: 'emma@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [4],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'Emma',
      giverTeam: 'Team 2',
      giverEmail: 'emma@gmail.com',
      giverSection: '',
      recipient: 'Emma',
      recipientTeam: 'Team 2',
      recipientEmail: 'emma@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [7],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    // Fred has self-review and is the only review
    {
      giver: 'Fred',
      giverTeam: 'Team 3',
      giverEmail: 'fred@gmail.com',
      giverSection: '',
      recipient: 'Fred',
      recipientTeam: 'Team 3',
      recipientEmail: 'fred@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [2],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    // Henry has no self-review and has only one review
    {
      giver: 'Greg',
      giverTeam: 'Team 3',
      giverEmail: 'greg@gmail.com',
      giverSection: '',
      recipient: 'Henry',
      recipientTeam: 'Team 3',
      recipientEmail: 'henry@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [5],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    // Greg has no self-review and has more than one review
    {
      giver: 'Fred',
      giverTeam: 'Team 3',
      giverEmail: 'fred@gmail.com',
      giverSection: '',
      recipient: 'Greg',
      recipientTeam: 'Team 3',
      recipientEmail: 'greg@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [7],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
    {
      giver: 'Henry',
      giverTeam: 'Team 3',
      giverEmail: 'henry@gmail.com',
      giverSection: '',
      recipient: 'Greg',
      recipientTeam: 'Team 3',
      recipientEmail: 'greg@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [9],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
  ];

  it('should calculate statistics correctly', () => {

    component.responses = responses;

    const expectedPointsPerOption: Record<string, number[]> = {
      'bob@gmail.com': [2, 3, 5, 5],
      'emma@gmail.com': [4, 6, 7, 9],
      'fred@gmail.com': [2],
      'henry@gmail.com': [5],
      'greg@gmail.com': [7, 9],
    };
    const expectedTotalPointsPerOption: Record<string, number> = {
      'bob@gmail.com': 15,
      'emma@gmail.com': 26,
      'fred@gmail.com': 2,
      'henry@gmail.com': 5,
      'greg@gmail.com': 16,
    };
    const expectedAveragePointsPerOption: Record<string, number> = {
      'bob@gmail.com': 3.75,
      'emma@gmail.com': 6.5,
      'fred@gmail.com': 2,
      'henry@gmail.com': 5,
      'greg@gmail.com': 8,
    };
    const expectedAveragePointsExcludingSelf: Record<string, number> = {
      'bob@gmail.com': 3.33,
      'emma@gmail.com': 6.33,
      'fred@gmail.com': 0,
      'henry@gmail.com': 5,
      'greg@gmail.com': 8,
    };

    component.calculateStatistics();

    expect(component.pointsPerOption).toEqual(expectedPointsPerOption);
    expect(component.totalPointsPerOption).toEqual(expectedTotalPointsPerOption);
    expect(component.averagePointsPerOption).toEqual(expectedAveragePointsPerOption);
    expect(component.averagePointsExcludingSelf).toEqual(expectedAveragePointsExcludingSelf);
  });

});
