import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NumScaleQuestionStatisticsComponent } from './num-scale-question-statistics.component';
import { Response } from './question-statistics';
import ResponseTestData from './test-data/numScaleQuestionResponses.json';
import { FeedbackNumericalScaleResponseDetails } from '../../../../types/api-output';

describe('NumScaleQuestionStatisticsComponent', () => {
  let component: NumScaleQuestionStatisticsComponent;
  let fixture: ComponentFixture<NumScaleQuestionStatisticsComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(NumScaleQuestionStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate statistics correctly', () => {
    component.responses = ResponseTestData.responses as Response<FeedbackNumericalScaleResponseDetails>[];
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin = 1;
    const expectedMax = 5;
    const expectedAverage = 2.67;
    const expectedAverageExcludingSelf = 2.67;

    component.calculateStatistics();

    const team = 'Instructors';
    const recipient = 'Instructor';
    expect(component.teamToRecipientToScores[team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores[team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores[team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(
      expectedAverageExcludingSelf,
    );
  });

  it('should calculate statistics correctly if responses are zero', () => {
    component.responses = ResponseTestData.responsesAtZero as Response<FeedbackNumericalScaleResponseDetails>[];
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin = 0;
    const expectedMax = 0;
    const expectedAverage = 0;
    const expectedAverageExcludingSelf = 0;

    component.calculateStatistics();

    const team = 'Instructors';
    const recipient = 'Instructor';
    expect(component.teamToRecipientToScores[team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores[team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores[team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(
      expectedAverageExcludingSelf,
    );
  });

  it('should calculate statistics correctly if self-response exists', () => {
    component.responses = ResponseTestData.responsesWithSelf as Response<FeedbackNumericalScaleResponseDetails>[];
    component.question.maxScale = 5;
    component.question.minScale = 0;

    const expectedMin = 2;
    const expectedMax = 5;
    const expectedAverage = 3.5;
    const expectedAverageExcludingSelf = 3;

    component.calculateStatistics();

    const team = 'Instructors';
    const recipient = 'Instructor';
    expect(component.teamToRecipientToScores[team][recipient].min).toEqual(expectedMin);
    expect(component.teamToRecipientToScores[team][recipient].max).toEqual(expectedMax);
    expect(component.teamToRecipientToScores[team][recipient].average).toEqual(expectedAverage);
    expect(component.teamToRecipientToScores[team][recipient].averageExcludingSelf).toEqual(
      expectedAverageExcludingSelf,
    );
  });
});
