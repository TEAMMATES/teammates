import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { Response } from './question-statistics';
import { PerRecipientStats } from './question-statistics-calculation/rubric-question-statistics-calculation';
import { RubricQuestionStatisticsComponent } from './rubric-question-statistics.component';
import ResponseTestData from './test-data/rubricQuestionResponses.json';
import { FeedbackRubricResponseDetails } from '../../../../types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';

describe('RubricQuestionStatisticsComponent', () => {
  let component: RubricQuestionStatisticsComponent;
  let fixture: ComponentFixture<RubricQuestionStatisticsComponent>;

  beforeEach(waitForAsync(() => {
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

  it('should calculate responses correctly', () => {
    component.responses = ResponseTestData.responses as Response<FeedbackRubricResponseDetails>[];
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

    const expectedPerRecpientStatsMap: Record<string, PerRecipientStats> =
        ResponseTestData.expectedStatsMap as Record<string, PerRecipientStats>;

    component.calculateStatistics();

    expect(component.percentages).toEqual(expectedPercentages);
    expect(component.percentagesExcludeSelf).toEqual(expectedPercentagesExceptSelf);
    expect(component.subQuestionWeightAverage).toEqual(expectedWeightAverage);
    expect(component.subQuestionWeightAverageExcludeSelf).toEqual(expectedWeightAverageExcludeSelf);
    expect(component.perRecipientStatsMap).toEqual(expectedPerRecpientStatsMap);
  });

  it('should calculate responses correctly when there are no weights', () => {
    component.responses = ResponseTestData.responses as Response<FeedbackRubricResponseDetails>[];
    component.question.rubricSubQuestions = ['Question1', 'Question2', 'Question3'];
    component.question.rubricChoices = ['Yes', 'No'];
    component.question.hasAssignedWeights = false;

    const expectedPercentages: number[][] = [
      [75, 25], [50, 50], [100, 0],
    ];

    const expectedPercentagesExceptSelf: number[][] = [
      [100, 0], [50, 50], [100, 0],
    ];

    const expectedPerRecpientStatsMap: Record<string, PerRecipientStats> = {};

    component.calculateStatistics();

    expect(component.percentages).toEqual(expectedPercentages);
    expect(component.percentagesExcludeSelf).toEqual(expectedPercentagesExceptSelf);
    expect(component.perRecipientStatsMap).toEqual(expectedPerRecpientStatsMap);
  });
});
