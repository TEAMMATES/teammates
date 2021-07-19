import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackMsqResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { MsqQuestionStatisticsComponent } from './msq-question-statistics.component';
import { Response } from './question-statistics';

describe('MsqQuestionStatisticsComponent', () => {
  let component: MsqQuestionStatisticsComponent;
  let fixture: ComponentFixture<MsqQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const responsesNoOther: Response<FeedbackMsqResponseDetails>[] = [
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
        answers: ['optionA', 'optionB'],
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MSQ,
      } as FeedbackMsqResponseDetails,
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
        answers: ['optionA'],
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MSQ,
      } as FeedbackMsqResponseDetails,
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
        answers: [''],
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MSQ,
      } as FeedbackMsqResponseDetails,
    },
  ];

  const responsesWithOther: Response<FeedbackMsqResponseDetails>[] = [
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
        answers: ['optionA', 'optionB', 'optionC'],
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MSQ,
      } as FeedbackMsqResponseDetails,
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
        answers: ['optionA', 'etcetra'],
        isOther: true,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MSQ,
      } as FeedbackMsqResponseDetails,
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
        answers: [''],
        isOther: false,
        otherFieldContent: 'nothing',
        questionType: FeedbackQuestionType.MSQ,
      } as FeedbackMsqResponseDetails,
    },
  ];

  it('should calculate statistics correctly', () => {
    component.question.msqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = false;
    component.question.hasAssignedWeights = true;
    component.question.msqWeights = [1, 2, 3];
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
    component.question.msqChoices = ['optionA', 'optionB', 'optionC'];
    component.question.otherEnabled = true;
    component.question.hasAssignedWeights = true;
    component.question.msqWeights = [1, 2, 3];
    component.question.msqOtherWeight = 4;
    component.responses = responsesWithOther;

    component.calculateStatistics();

    const expectedAnswerFrequency: Record<string, number> = {
      optionA: 2, optionB: 1, optionC: 1, Other: 1,
    };
    const expectedPercentagePerOption: Record<string, number> = {
      optionA: 40, optionB: 20, optionC: 20, Other: 20,
    };
    const expectedWeightPerOption: Record<string, number> = {
      optionA: 1, optionB: 2, optionC: 3, Other: 4,
    };
    const expectedWeightedPrecentagePerOption: Record<string, number> = {
      optionA: 18.18, optionB: 18.18, optionC: 27.27, Other: 36.36,
    };

    expect(component.answerFrequency).toEqual(expectedAnswerFrequency);
    expect(component.percentagePerOption).toEqual(expectedPercentagePerOption);
    expect(component.weightPerOption).toEqual(expectedWeightPerOption);
    expect(component.weightedPercentagePerOption).toEqual(expectedWeightedPrecentagePerOption);
  });

});
