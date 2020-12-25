import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FeedbackQuestionType, FeedbackRankRecipientsResponseDetails } from 'src/web/types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { RankRecipientsQuestionStatisticsComponent } from './rank-recipients-question-statistics.component';
import { Response } from './question-statistics';

describe('RankRecipientsQuestionStatisticsComponent', () => {
  let component: RankRecipientsQuestionStatisticsComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionStatisticsComponent>;

  beforeEach(async(() => {
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

  it('should rank correctly within team', () => {
    const defaultResponse: Response<FeedbackRankRecipientsResponseDetails> = {
      giver: '',
      giverSection: 'Tut 1',
      giverTeam: 'Team 1',
      recipient: '',
      recipientTeam: 'Team 1',
      recipientSection: 'Tut 1',
      responseDetails: {
        answer: -1,
        questionType: FeedbackQuestionType.RANK_RECIPIENTS
      }
    };

    const responses: Response<FeedbackRankRecipientsResponseDetails>[] = [
      {
        ...defaultResponse,
        giver: 'alice',
        recipient: 'bob',
        responseDetails: {
          answer: 2,
          questionType: FeedbackQuestionType.RANK_RECIPIENTS
        }
      },
      {
        ...defaultResponse,
        giver: 'bob',
        recipient: 'alice',
        responseDetails: {
          answer: 1,
          questionType: FeedbackQuestionType.RANK_RECIPIENTS
        }
      },
      {
        ...defaultResponse,
        giver: 'charlie',
        giverTeam: 'Team 2',
        recipient: 'delta',
        recipientTeam: 'Team 2',
        responseDetails: {
          answer: 1,
          questionType: FeedbackQuestionType.RANK_RECIPIENTS
        }
      },
      {
        ...defaultResponse,
        giver: 'delta',
        giverTeam: 'Team 2',
        recipient: 'charlie',
        recipientTeam: 'Team 2',
        responseDetails: {
          answer: 2,
          questionType: FeedbackQuestionType.RANK_RECIPIENTS
        }
      },
    ];

    component.responses = responses;

    component.calculateStatistics();

    expect(component.rankPerOption['charlie']).toBe(3);
    expect(component.rankPerOptionInTeam['charlie']).toBe(2);

  });

});
