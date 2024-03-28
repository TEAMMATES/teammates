import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { ConstsumOptionsQuestionEditAnswerFormComponent } from './constsum-options-question-edit-answer-form.component';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import { FeedbackConstantSumQuestionDetails, FeedbackConstantSumResponseDetails, FeedbackQuestionType }
  from '../../../../types/api-output';

describe('ConstsumOptionsQuestionEditAnswerFormComponent', () => {
  let component: ConstsumOptionsQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionEditAnswerFormComponent>;

  const feedbackConstantSumResponseDetailsBuilder = createBuilder<FeedbackConstantSumResponseDetails>({
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    answers: [],
  });

  const feedbackConstantSumQuestionDetailsBuilder = createBuilder<FeedbackConstantSumQuestionDetails>({
    questionType: FeedbackQuestionType.CONSTSUM_OPTIONS,
    questionText: '',
    constSumOptions: [],
    distributeToRecipients: false,
    pointsPerOption: false,
    forceUnevenDistribution: false,
    distributePointsFor: '',
    points: 0,
  });

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumOptionsQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('triggerResponse: should round up event in newAnswers when setting new number for index', () => {
    const triggerResponseDetailsChangeSpy = jest.spyOn(component, 'triggerResponseDetailsChange');
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([0, 0, 0]).build();
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.constSumOptions(['1', '2', '3']).build();

    component.triggerResponse(1, 2.5);

    expect(triggerResponseDetailsChangeSpy).toHaveBeenCalledWith('answers', [0, 3, 0]);
  });

  it('triggerResponse: should set newAnswers to be array of 0s when response answers is'
    + 'not the same length as question options', () => {
    const triggerResponseDetailsChangeSpy = jest.spyOn(component, 'triggerResponseDetailsChange');
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([5, 5]).build();
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.constSumOptions(['1', '2', '3']).build();

    component.triggerResponse(1, 1);

    expect(triggerResponseDetailsChangeSpy).toHaveBeenCalledWith('answers', [0, 1, 0]);
  });

  it('totalRequiredPoints: returns question details.points if pointerPerOption is false', () => {
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.pointsPerOption(false).points(242).build();

    expect(component.totalRequiredPoints).toBe(242);
  });

  it('totalRequiredPoints: returns question details.points mulitplied by'
    + 'total options if pointerPerOption is true', () => {
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder
                                  .pointsPerOption(true)
                                  .points(100)
                                  .constSumOptions(['1', '2', '3'])
                                  .build();

    expect(component.totalRequiredPoints).toBe(300);
  });

  it('totalAnsweredPoints: returns sum of the points in answers', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 2, 3]).build();

    expect(component.totalAnsweredPoints).toBe(6);
  });

  it('isAllPointsUneven: should return true if answers are unique', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 2, 3]).build();

    expect(component.isAllPointsUneven).toBeTruthy();
  });

  it('isAllPointsUneven: should return false if answers are non-unique', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 1, 3]).build();

    expect(component.isAllPointsUneven).toBeFalsy();
  });

  it('isSomePointsUneven: should return true if there is only 1 answer', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1]).build();

    expect(component.isSomePointsUneven).toBeTruthy();
  });

  it('isSomePointsUneven: should return true if there are more than 1 unique answers', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 2, 1]).build();

    expect(component.isSomePointsUneven).toBeTruthy();
  });

  it('isSomePointsUneven: should return false if there is only 1 unique answer', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 1, 1]).build();

    expect(component.isSomePointsUneven).toBeFalsy();
  });

  it('isAnyPointsNegative: should return false if all answers are greater than zero', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([0, 1, 2]).build();

    expect(component.isAnyPointsNegative).toBeFalsy();
  });

  it('isAnyPointsNegative: should return true if any answer is negative', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([0, -1, 2]).build();

    expect(component.isAnyPointsNegative).toBeTruthy();
  });

  it('isAnyPointBelowMinimum: should return false when minPoint is undefined'
    + 'and all answers are greater than or equals to zero', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([0, 1, 2]).build();

    expect(component.isAnyPointBelowMinimum).toBeFalsy();
  });

  it('isAnyPointBelowMinimum: should return true when minPoint is undefined and one answer is below zero', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([0, -1, 2]).build();

    expect(component.isAnyPointBelowMinimum).toBeTruthy();
  });

  it('isAnyPointBelowMinimum: should return false when minPoint is undefined and'
    + 'all answers are greater than or equals to zero', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([0, 1, 2]).build();

    expect(component.isAnyPointBelowMinimum).toBeFalsy();
  });

  it('isAnyPointBelowMinimum: should return true when minPoint is defined and one answer is below minPoint', () => {
    const minPoint = 5;
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([minPoint - 1, 1, 2]).build();
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();
    component.questionDetails.minPoint = minPoint;

    expect(component.isAnyPointBelowMinimum).toBeTruthy();
  });

  it('isAnyPointBelowMinimum: should return false when minPoint is defined'
    + 'and all answers are greater than or equal to minPoint', () => {
    const minPoint = 5;
    component.responseDetails =
      feedbackConstantSumResponseDetailsBuilder.answers([minPoint, minPoint + 1, minPoint + 2]).build();
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();
    component.questionDetails.minPoint = minPoint;

    expect(component.isAnyPointBelowMinimum).toBeFalsy();
  });

  it('isAnyPointAboveMaximum: should return true when maxPoint is undefined'
    + 'and one answer is above totalRequiredPoints', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 2, 4]).build();

    // totalRequiredPoints = answers.length * points = 3 (calculated in component.totalRequirePoints)
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.points(1).build();

    expect(component.isAnyPointAboveMaximum).toBeTruthy();
  });

  it('isAnyPointAboveMaximum: should return false when maxPoint is undefined'
    + 'and all answers are less than or equals to totalRequiredPoints', () => {
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([1, 2, 3]).build();

    // totalRequiredPoints = answers.length * points = 3 (calculated in component.totalRequirePoints)
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.points(1).build();

    expect(component.isAnyPointAboveMaximum).toBeFalsy();
  });

  it('isAnyPointAboveMaximum: should return true when maxPoint is defined'
    + 'and one answer is aboove maxPoint', () => {
    const maxPoint = 5;
    component.responseDetails = feedbackConstantSumResponseDetailsBuilder.answers([maxPoint + 1, 1, 2]).build();
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();
    component.questionDetails.maxPoint = maxPoint;

    expect(component.isAnyPointAboveMaximum).toBeTruthy();
  });

  it('isAnyPointAboveMaximum: should return false when maxPoint is defined'
    + 'and all answers are less than or equal to maxPoint', () => {
    const maxPoint = 5;
    component.responseDetails =
      feedbackConstantSumResponseDetailsBuilder.answers([maxPoint, maxPoint - 1, maxPoint - 2]).build();
    component.questionDetails = feedbackConstantSumQuestionDetailsBuilder.build();
    component.questionDetails.maxPoint = maxPoint;

    expect(component.isAnyPointAboveMaximum).toBeFalsy();
  });
});
