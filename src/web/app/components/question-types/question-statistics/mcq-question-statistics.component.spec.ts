import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { McqQuestionStatisticsComponent } from './mcq-question-statistics.component';
import { Response } from './question-statistics';
import ResponseTestData from './test-data/mcqQuestionResponses.json';
import { FeedbackMcqResponseDetails } from '../../../../types/api-output';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';

describe('McqQuestionStatisticsComponent', () => {
  let component: McqQuestionStatisticsComponent;
  let fixture: ComponentFixture<McqQuestionStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate statistics correctly', () => {
    component.question.mcqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = false;
    component.question.hasAssignedWeights = true;
    component.question.mcqWeights = [1, 2, 3];
    component.responses = ResponseTestData.responsesNoOther as Response<FeedbackMcqResponseDetails>[];

    const expectedAnswerFrequency: Record<string, number> = {
      optionA: 2, optionB: 1, optionC: 0,
    };
    const expectedPercentagePerOption: Record<string, number> = {
      optionA: 66.67, optionB: 33.33, optionC: 0,
    };
    const expectedWeightPerOption: Record<string, number> = {
      optionA: 1, optionB: 2, optionC: 3,
    };
    const expectedWeightedPrecentagePerOption: Record<string, number> = {
      optionA: 50, optionB: 50, optionC: 0,
    };
    const expectedPerRecipientResponses: Record<string, any> =
        ResponseTestData.expectedPerRecipientResponses as Record<string, any>;

    component.calculateStatistics();

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.weightPerOption).toEqual(expectedWeightPerOption);
    expect(component.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
    expect(component.perRecipientResponses).toEqual(expectedPerRecipientResponses);
  });

  it('should calculate statistics correctly when other is enabled', () => {
    component.question.mcqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = true;
    component.question.hasAssignedWeights = true;
    component.question.mcqWeights = [1, 2, 3];
    component.question.mcqOtherWeight = 4;
    component.responses = ResponseTestData.responsesWithOther as Response<FeedbackMcqResponseDetails>[];

    const expectedAnswerFrequency: Record<string, number> = {
      optionA: 1, optionB: 1, optionC: 0, Other: 1,
    };
    const expectedPercentagePerOption: Record<string, number> = {
      optionA: 33.33, optionB: 33.33, optionC: 0, Other: 33.33,
    };
    const expectedWeightPerOption: Record<string, number> = {
      optionA: 1, optionB: 2, optionC: 3, Other: 4,
    };
    const expectedWeightedPrecentagePerOption: Record<string, number> = {
      optionA: 14.29, optionB: 28.57, optionC: 0, Other: 57.14,
    };
    const expectedPerRecipientResponses: Record<string, any> =
        ResponseTestData.expectedPerRecipientResponsesWithOther as Record<string, any>;

    component.calculateStatistics();

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.weightPerOption).toEqual(expectedWeightPerOption);
    expect(component.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
    expect(component.perRecipientResponses).toEqual(expectedPerRecipientResponses);
  });

  it('should calculate statistics correctly when there are no assigned weights', () => {
    component.question.mcqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = false;
    component.question.hasAssignedWeights = false;
    component.responses = ResponseTestData.responsesNoOther as Response<FeedbackMcqResponseDetails>[];

    const expectedAnswerFrequency: Record<string, number> = {
      optionA: 2, optionB: 1, optionC: 0,
    };
    const expectedPercentagePerOption: Record<string, number> = {
      optionA: 66.67, optionB: 33.33, optionC: 0,
    };
    const expectedPerRecipientResponses: Record<string, any> = {};

    component.calculateStatistics();

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.perRecipientResponses).toEqual(expectedPerRecipientResponses);
  });

  it('should calculate statistics correctly when other is enabled and no assigned weights', () => {
    component.question.mcqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = true;
    component.question.hasAssignedWeights = false;
    component.responses = ResponseTestData.responsesWithOther as Response<FeedbackMcqResponseDetails>[];

    const expectedAnswerFrequency: Record<string, number> = {
      optionA: 1, optionB: 1, optionC: 0, Other: 1,
    };
    const expectedPercentagePerOption: Record<string, number> = {
      optionA: 33.33, optionB: 33.33, optionC: 0, Other: 33.33,
    };
    const expectedPerRecipientResponses: Record<string, any> = {};

    component.calculateStatistics();

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.perRecipientResponses).toEqual(expectedPerRecipientResponses);
  });

});
