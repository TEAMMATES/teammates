import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackMcqResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { McqQuestionStatisticsComponent } from './mcq-question-statistics.component';
import { Response } from './question-statistics';

describe('McqQuestionStatisticsComponent', () => {
  let component: McqQuestionStatisticsComponent;
  let fixture: ComponentFixture<McqQuestionStatisticsComponent>;

  beforeEach(async(() => {
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

  const responsesNoOther: Response<FeedbackMcqResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: '',
      recipientTeam: '',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 'optionA',
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MCQ,
      } as FeedbackMcqResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: '',
      recipientTeam: '',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 'optionA',
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MCQ,
      } as FeedbackMcqResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: '',
      recipientTeam: '',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 'optionB',
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MCQ,
      } as FeedbackMcqResponseDetails,
    },
  ];

  const responsesWithOther: Response<FeedbackMcqResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: '',
      recipientTeam: '',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 'etcetra',
        isOther: true,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MCQ,
      } as FeedbackMcqResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: '',
      recipientTeam: '',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 'optionA',
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MCQ,
      } as FeedbackMcqResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: '',
      recipientTeam: '',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 'optionB',
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MCQ,
      } as FeedbackMcqResponseDetails,
    },
  ];

  it('should calculate statistics correctly', () => {
    component.question.mcqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = false;
    component.question.hasAssignedWeights = true;
    component.question.mcqWeights = [1, 2, 3];
    component.responses = responsesNoOther;

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

    component.calculateStatistics();

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.weightPerOption).toEqual(expectedWeightPerOption);
    expect(component.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
  });

  it('should calculate statistics correctly when other is enabled', () => {
    component.question.mcqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = true;
    component.question.hasAssignedWeights = true;
    component.question.mcqWeights = [1, 2, 3];
    component.question.mcqOtherWeight = 4;
    component.responses = responsesWithOther;

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

    component.calculateStatistics();

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.weightPerOption).toEqual(expectedWeightPerOption);
    expect(component.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
  });

});
