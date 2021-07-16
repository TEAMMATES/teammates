import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FeedbackQuestionType } from '../../../../types/api-request';

import { FeedbackConstantSumResponseDetails } from '../../../../types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { ConstsumOptionsQuestionStatisticsComponent } from './constsum-options-question-statistics.component';
import { Response } from './question-statistics';

describe('ConstsumOptionsQuestionStatisticsComponent', () => {
  let component: ConstsumOptionsQuestionStatisticsComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumOptionsQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionStatisticsComponent);
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
      recipient: 'Alice',
      recipientTeam: 'Team 1',
      recipientEmail: 'alice@gmail.com',
      recipientSection: '',
      responseDetails: {
        answers: [50, 50],
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
        answers: [30, 70],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
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
        answers: [10, 90],
        questionType: FeedbackQuestionType.CONSTSUM,
      } as FeedbackConstantSumResponseDetails,
    },
  ];

  it('should calculate statistics correctly', () => {

    component.question.constSumOptions = ['optionA', 'optionB'];
    component.responses = responses;

    component.calculateStatistics();

    const expectedPointsPerOption: Record<string, number[]> = {
      optionA: [10, 30, 50], optionB: [50, 70, 90],
    };
    const expectedTotalPointsPerOption: Record<string, number> = {
      optionA: 90, optionB: 210,
    };
    const expectedAveragePointsPerOption: Record<string, number> = {
      optionA: 30, optionB: 70,
    };

    expect(component.pointsPerOption).toEqual(expectedPointsPerOption);
    expect(component.totalPointsPerOption).toEqual(expectedTotalPointsPerOption);
    expect(component.averagePointsPerOption).toEqual(expectedAveragePointsPerOption);
  });
});
