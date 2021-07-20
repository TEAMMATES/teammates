import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FeedbackNumericalScaleResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { SortableTableModule } from '../../sortable-table/sortable-table.module';
import { NumScaleQuestionStatisticsComponent } from './num-scale-question-statistics.component';
import { Response } from './question-statistics';

describe('NumScaleQuestionStatisticsComponent', () => {
  let component: NumScaleQuestionStatisticsComponent;
  let fixture: ComponentFixture<NumScaleQuestionStatisticsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NumScaleQuestionStatisticsComponent],
      imports: [SortableTableModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  const responses: Response<FeedbackNumericalScaleResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 5,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 2,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 1,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
  ];

  const responsesAtZero: Response<FeedbackNumericalScaleResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 0,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 0,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 0,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
  ];

  const responsesWithSelf: Response<FeedbackNumericalScaleResponseDetails>[] = [
    {
      giver: 'Alice',
      giverTeam: 'Team 1',
      giverEmail: 'alice@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 2,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Bob',
      giverTeam: 'Team 2',
      giverEmail: 'bob@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 3,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Charles',
      giverTeam: 'Team 1',
      giverEmail: 'charles@gmail.com',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 4,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
    {
      giver: 'Instructor',
      giverTeam: 'Instructors',
      giverEmail: '',
      giverSection: '',
      recipient: 'Instructor',
      recipientTeam: 'Instructors',
      recipientEmail: '',
      recipientSection: '',
      responseDetails: {
        answer: 5,
        questionType: FeedbackQuestionType.NUMSCALE,
      } as FeedbackNumericalScaleResponseDetails,
    },
  ];

  it('should calculate statistics correctly', () => {
    component.responses = responses;
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin: number = 1;
    const expectedMax: number = 5;
    const expectedAverage: number = 2.67;
    const expectedAverageExcludingSelf: number = 2.67;

    component.calculateStatistics();

    const team: string = 'Instructors';
    const recipient: string = 'Instructor';
    expect(component.teamToRecipientToScores
        [team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores
        [team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores
        [team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores
        [team][recipient].averageExcludingSelf).toEqual(expectedAverageExcludingSelf);
  });

  it('should calculate statistics correctly if responses are zero', () => {
    component.responses = responsesAtZero;
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin: number = 0;
    const expectedMax: number = 0;
    const expectedAverage: number = 0;
    const expectedAverageExcludingSelf: number = 0;

    component.calculateStatistics();

    const team: string = 'Instructors';
    const recipient: string = 'Instructor';
    expect(component.teamToRecipientToScores
        [team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores
        [team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores
        [team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores
        [team][recipient].averageExcludingSelf).toEqual(expectedAverageExcludingSelf);

  });

  it('should calculate statistics correctly if self-response exists', () => {
    component.responses = responsesWithSelf;
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin: number = 2;
    const expectedMax: number = 5;
    const expectedAverage: number = 3.5;
    const expectedAverageExcludingSelf: number = 3;

    component.calculateStatistics();

    const team: string = 'Instructors';
    const recipient: string = 'Instructor';
    expect(component.teamToRecipientToScores
        [team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores
        [team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores
        [team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores
        [team][recipient].averageExcludingSelf).toEqual(expectedAverageExcludingSelf);
  });
});
