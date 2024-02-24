import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Response } from './question-statistics';
import { RankRecipientsQuestionStatisticsComponent } from './rank-recipients-question-statistics.component';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackRankRecipientsResponseDetails,
} from '../../../../types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';

describe('RankRecipientsQuestionStatisticsComponent', () => {
  let component: RankRecipientsQuestionStatisticsComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionStatisticsComponent>;

  const testResponses: Response<FeedbackRankRecipientsResponseDetails>[] = [
    {
      giver: 'alice',
      giverTeam: 'Team 1',
      giverSection: 'Tutorial 1',
      recipient: 'bob',
      recipientTeam: 'Team 1',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 1,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
    {
      giver: 'bob',
      giverTeam: 'Team 1',
      giverSection: 'Tutorial 1',
      recipient: 'alice',
      recipientTeam: 'Team 1',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 2,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
    {
      giver: 'charlie',
      giverTeam: 'Team 2',
      giverSection: 'Tutorial 1',
      recipient: 'delta',
      recipientTeam: 'Team 2',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 2,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
    {
      giver: 'delta',
      giverTeam: 'Team 2',
      giverSection: 'Tutorial 1',
      recipient: 'charlie',
      recipientTeam: 'Team 2',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 1,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
    {
      giver: 'charlie',
      giverTeam: 'Team 2',
      giverSection: 'Tutorial 1',
      recipient: 'charlie',
      recipientTeam: 'Team 2',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 3,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
    {
      giver: 'delta',
      giverTeam: 'Team 2',
      giverSection: 'Tutorial 1',
      recipient: 'delta',
      recipientTeam: 'Team 2',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 1,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
    {
      giver: 'elliot',
      giverTeam: 'Team 2',
      giverSection: 'Tutorial 1',
      recipient: 'charlie',
      recipientTeam: 'Team 2',
      recipientSection: 'Tutorial 1',
      responseDetails: {
        answer: 1,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS,
      },
    },
  ];

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not calculate team rank if recipient type is invalid', () => {
    component.responses = testResponses;

    // ranks inside teams are meaningless when ranking across teams
    component.recipientType = FeedbackParticipantType.TEAMS;

    component.calculateStatistics();

    expect(component.rankPerOptionInTeam).toMatchObject({});
  });

  it('should rank correctly within team when recipient type is valid', () => {
    component.responses = testResponses;
    component.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

    const bob: string = 'bob';
    const charlie: string = 'charlie';
    const delta: string = 'delta';

    component.calculateStatistics();

    expect(component.rankPerOption[delta]).toBe(2);
    expect(component.rankPerOptionInTeam[bob]).toBe(1);
    expect(component.rankPerOptionInTeam[charlie]).toBe(2);
    expect(component.rankPerOptionInTeam[delta]).toBe(1);
    expect(component.rankPerOptionInTeamExcludeSelf[delta]).toBe(2);

  });

});
