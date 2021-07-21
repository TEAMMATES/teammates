import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackRubricResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { Response } from './question-statistics';
import { RubricQuestionStatisticsComponent } from './rubric-question-statistics.component';

describe('RubricQuestionStatisticsComponent', () => {
  let component: RubricQuestionStatisticsComponent;
  let fixture: ComponentFixture<RubricQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RubricQuestionStatisticsComponent],
      imports: [
        FormsModule,
        NgbModule,
        SortableTableModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const responses: Response<FeedbackRubricResponseDetails>[] = [
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
        answer: [0, 1, 0],
        questionType: FeedbackQuestionType.RUBRIC,
      } as FeedbackRubricResponseDetails,
    },
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
        answer: [1, 1, 0],
        questionType: FeedbackQuestionType.RUBRIC,
      } as FeedbackRubricResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Alice',
      recipientTeam: 'Team 1',
      recipientEmail: 'alice@gmail.com',
      recipientSection: '',
      responseDetails: {
        answer: [0, 0, 0],
        questionType: FeedbackQuestionType.RUBRIC,
      } as FeedbackRubricResponseDetails,
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
        answer: [0, 0, 0],
        questionType: FeedbackQuestionType.RUBRIC,
      } as FeedbackRubricResponseDetails,
    },
  ];

  it('should calculate responses correctly', () => {
    component.responses = responses;
    component.question.rubricSubQuestions = ['Question1', 'Question2', 'Question3'];
    component.question.rubricChoices = ['Yes', 'No'];
    component.question.hasAssignedWeights = true;
    component.question.rubricWeightsForEachCell = [[0.2, 0.8], [0.1, 0.9], [0.4, 0.6]];

    const expectedPercentages: number[][] = [
      [75, 25], [50, 50], [100, 0],
    ];

    const expectedPercentagesExceptSelf: number[][] = [
      [100, 0], [50, 50], [100, 0],
    ];

    const expectedWeightAverage: number[] = [
      0.35, 0.5, 0.4,
    ];

    const expectedWeightAverageExcludeSelf: number[] = [
      0.2, 0.5, 0.4,
    ];

    component.calculateStatistics();

    expect(component.percentages).toEqual(expectedPercentages);
    expect(component.percentagesExcludeSelf).toEqual(expectedPercentagesExceptSelf);
    expect(component.subQuestionWeightAverage).toEqual(expectedWeightAverage);
    expect(component.subQuestionWeightAverageExcludeSelf).toEqual(expectedWeightAverageExcludeSelf);
  });
});
