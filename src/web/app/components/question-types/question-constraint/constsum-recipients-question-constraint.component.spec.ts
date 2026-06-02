import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumRecipientsQuestionConstraintComponent } from './constsum-recipients-question-constraint.component';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import { FeedbackConstantSumRecipientsResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import {
  FeedbackResponseRecipientSubmissionFormModel,
  ResponseSubmissionStatus,
} from '../../question-submission-form/question-submission-form-model';

describe('ConstsumRecipientsQuestionConstraintComponent', () => {
  let component: ConstsumRecipientsQuestionConstraintComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionConstraintComponent>;

  const formBuilder = createBuilder<FeedbackResponseRecipientSubmissionFormModel>({
    responseId: '123',
    recipientIdentifier: 'recipient123',
    status: ResponseSubmissionStatus.SAVED,
    isValid: true,
    responseDetails: { questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS },
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('allAnswers: should return an empty array if no forms provided', () => {
    component.recipientSubmissionForms = [];
    expect(component.allAnswers).toEqual([]);
  });

  it('allAnswers: should return 0 if the answers array is empty', () => {
    const details: FeedbackConstantSumRecipientsResponseDetails = {
      questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
      answers: [],
    };
    const form = formBuilder.responseDetails(details).build();

    component.recipientSubmissionForms = [form];
    expect(component.allAnswers).toEqual([0]);
  });

  it('allAnswers: should return the first answer if it exists', () => {
    const details: FeedbackConstantSumRecipientsResponseDetails = {
      questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
      answers: [5, 10, 15],
    };
    const form = formBuilder.responseDetails(details).build();

    component.recipientSubmissionForms = [form];
    expect(component.allAnswers).toEqual([5]);
  });

  it('allAnswers: should return the first answers of multiple forms', () => {
    const detailsOne: FeedbackConstantSumRecipientsResponseDetails = {
      questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
      answers: [5, 10, 15],
    };
    const formOne = formBuilder.responseDetails(detailsOne).build();

    const detailsTwo: FeedbackConstantSumRecipientsResponseDetails = {
      questionType: FeedbackQuestionType.CONSTSUM_RECIPIENTS,
      answers: [3, 6, 9],
    };
    const formTwo = formBuilder.responseDetails(detailsTwo).build();

    component.recipientSubmissionForms = [formOne, formTwo];
    expect(component.allAnswers).toEqual([5, 3]);
  });

  it('isAllPointsUneven: should return true when all points are unique', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([1, 2, 3]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isAllPointsUneven).toEqual(true);
  });

  it('isAllPointsUneven: should return false when some points are repeated', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([1, 2, 2, 3]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isAllPointsUneven).toEqual(false);
  });

  it('isAllPointsUneven: should return true when there are no points', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isAllPointsUneven).toEqual(true);
  });

  it('isSomePointsUneven: should return true when length is 1', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([1]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isSomePointsUneven).toEqual(true);
  });

  it('isSomePointsUneven: should return true when there are multiple points and some are different', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([1, 2, 3]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isSomePointsUneven).toEqual(true);
  });

  it('isSomePointsUneven: should return false when all answers are the same and length is greater than 1', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([2, 2, 2]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isSomePointsUneven).toEqual(false);
  });

  it('isSomePointsUneven: should return true when there are no points', () => {
    const mockAllAnswers = vi.fn().mockReturnValue([]);
    Object.defineProperty(component, 'allAnswers', { get: mockAllAnswers });

    expect(component.isSomePointsUneven).toEqual(true);
  });
});
