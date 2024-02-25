import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ContributionQuestionConstraintComponent } from './contribution-question-constraint.component';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import { FeedbackContributionResponseDetails } from '../../../../types/api-output';
import { FeedbackQuestionType } from '../../../../types/api-request';
import { CONTRIBUTION_POINT_NOT_SUBMITTED } from '../../../../types/feedback-response-details';
import { FeedbackResponseRecipientSubmissionFormModel }
  from '../../question-submission-form/question-submission-form-model';

describe('ContributionQuestionConstraintComponent', () => {
  let component: ContributionQuestionConstraintComponent;
  let fixture: ComponentFixture<ContributionQuestionConstraintComponent>;

  const formBuilder = createBuilder<FeedbackResponseRecipientSubmissionFormModel>({
    responseId: '123',
    recipientIdentifier: 'recipient123',
    isValid: true,
    responseDetails: { questionType: FeedbackQuestionType.CONTRIB },
  });

  const detailsBuilder = createBuilder<FeedbackContributionResponseDetails>({
    answer: 1,
    questionType: FeedbackQuestionType.CONTRIB,
  });

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ContributionQuestionConstraintComponent],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContributionQuestionConstraintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('isAllFormsAnswered: should return true when all forms are answered', () => {
    const answerOne = detailsBuilder.answer(10).build();
    const answerTwo = detailsBuilder.answer(5).build();
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];
    component.recipientSubmissionForms = recipientSubmissionForms;

    expect(component.isAllFormsAnswered).toBe(true);
  });

  it('isAllFormsAnswered: should return false when any form is not answered', () => {
    const answerOne = detailsBuilder.answer(10).build();
    const answerTwo = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];
    component.recipientSubmissionForms = recipientSubmissionForms;

    expect(component.isAllFormsAnswered).toBe(false);
  });

  it('isAllFormsNotAnswered: should return true when all forms are not answered', () => {
    const answerOne = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const answerTwo = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];

    component.recipientSubmissionForms = recipientSubmissionForms;
    expect(component.isAllFormsNotAnswered).toBe(true);
  });

  it('isAllFormsNotAnswered: should return false when any form is answered', () => {
    const answerOne = detailsBuilder.answer(10).build();
    const answerTwo = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];
    component.recipientSubmissionForms = recipientSubmissionForms;

    expect(component.isAllFormsNotAnswered).toBe(false);
  });

  it('totalRequiredContributions: should return the correct total required contributions '
    + 'based on the number of forms', () => {
      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.build(),
        formBuilder.build(),
        formBuilder.build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;

      expect(component.totalRequiredContributions).toBe(300);
    });

  it('allAnswers: should return an array of all answers with CONTRIBUTION_POINT_NOT_SUBMITTED replaced by 0', () => {
    const answerOne = detailsBuilder.answer(10).build();
    const answerTwo = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const answerThree = detailsBuilder.answer(20).build();

    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
      formBuilder.responseDetails(answerThree).build(),
    ];

    component.recipientSubmissionForms = recipientSubmissionForms;

    const expectedAnswers = [10, 0, 20];
    expect(component.allAnswers).toEqual(expectedAnswers);
  });

  it('totalAnsweredContributions: should return the correct total of all answered contributions', () => {
    const answerOne = detailsBuilder.answer(10).build();
    const answerTwo = detailsBuilder.answer(20).build();
    const answerThree = detailsBuilder.answer(30).build();

    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
      formBuilder.responseDetails(answerThree).build(),
    ];

    component.recipientSubmissionForms = recipientSubmissionForms;

    const expectedTotal = 10 + 20 + 30;
    expect(component.totalAnsweredContributions).toEqual(expectedTotal);
  });

  it('isAllContributionsDistributed: should return true when total answered contributions'
    + 'equal total required contributions', () => {
      const answerOne = detailsBuilder.answer(110).build();
      const answerTwo = detailsBuilder.answer(90).build();
      const answerThree = detailsBuilder.answer(100).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
        formBuilder.responseDetails(answerThree).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.isAllContributionsDistributed).toBe(true);
    });

  it('isAllContributionsDistributed: should return false when total answered contributions'
    + 'do not equal total required contributions', () => {
      const answerOne = detailsBuilder.answer(30).build();
      const answerTwo = detailsBuilder.answer(50).build();
      const answerThree = detailsBuilder.answer(20).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
        formBuilder.responseDetails(answerThree).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.isAllContributionsDistributed).toBe(false);
    });

  it('isInsufficientContributionsDistributed: should return true when total answered contributions'
    + 'are less than total required contributions', () => {
      const answerOne = detailsBuilder.answer(30).build();
      const answerTwo = detailsBuilder.answer(50).build();
      const answerThree = detailsBuilder.answer(20).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
        formBuilder.responseDetails(answerThree).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.isInsufficientContributionsDistributed).toBe(true);
    });

  it('isInsufficientContributionsDistributed: should return false when total answered contributions'
    + 'are equal to total required contributions', () => {
      const answerOne = detailsBuilder.answer(110).build();
      const answerTwo = detailsBuilder.answer(90).build();
      const answerThree = detailsBuilder.answer(100).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
        formBuilder.responseDetails(answerThree).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.isInsufficientContributionsDistributed).toBe(false);
    });

  it('isContributionsOverAllocated: should return true when total answered contributions'
    + 'are greater than total required contributions', () => {
      const answerOne = detailsBuilder.answer(110).build();
      const answerTwo = detailsBuilder.answer(90).build();
      const answerThree = detailsBuilder.answer(110).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
        formBuilder.responseDetails(answerThree).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.isContributionsOverAllocated).toBe(true);
    });

  it('isContributionsOverAllocated: should return false when total answered contributions'
    + 'are less than total required contributions', () => {
      const answerOne = detailsBuilder.answer(30).build();
      const answerTwo = detailsBuilder.answer(50).build();
      const answerThree = detailsBuilder.answer(20).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
        formBuilder.responseDetails(answerThree).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.isContributionsOverAllocated).toBe(false);
    });

  it('currentTotalString: should return "0%" when total answered contributions are 0', () => {
    const answerOne = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const answerTwo = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];

    component.recipientSubmissionForms = recipientSubmissionForms;
    expect(component.currentTotalString).toBe('0%');
  });

  it('currentTotalString: should return correct string when totalAnsweredContributions/100 is less than 1', () => {
    const answerOne = detailsBuilder.answer(30).build();
    const answerTwo = detailsBuilder.answer(20).build();

    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];

    component.recipientSubmissionForms = recipientSubmissionForms;
    expect(component.currentTotalString).toBe('1 x Equal Share - 50%');
  });

  it('currentTotalString: should return correct string when total answered contributions'
    + 'are equal to a multiple of 100', () => {
      const answerOne = detailsBuilder.answer(100).build();
      const answerTwo = detailsBuilder.answer(100).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.currentTotalString).toBe('2 x Equal Share');
    });

  it('currentTotalString: should return correct string when total answered contributions'
    + 'are greater than required', () => {
      const answerOne = detailsBuilder.answer(150).build();
      const answerTwo = detailsBuilder.answer(150).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.currentTotalString).toBe('2 x Equal Share + 100%');
    });

  it('currentTotalString: should return correct string when total answered contributions'
    + 'are less than required', () => {
      const answerOne = detailsBuilder.answer(100).build();
      const answerTwo = detailsBuilder.answer(50).build();

      const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
        formBuilder.responseDetails(answerOne).build(),
        formBuilder.responseDetails(answerTwo).build(),
      ];

      component.recipientSubmissionForms = recipientSubmissionForms;
      expect(component.currentTotalString).toBe('1 x Equal Share +  \n        50%');
    });

  it('expectedTotalString: should return the correct string format for total required contributions', () => {
    const answerOne = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const answerTwo = detailsBuilder.answer(CONTRIBUTION_POINT_NOT_SUBMITTED).build();
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] = [
      formBuilder.responseDetails(answerOne).build(),
      formBuilder.responseDetails(answerTwo).build(),
    ];

    component.recipientSubmissionForms = recipientSubmissionForms;
    expect(component.expectedTotalString).toBe('2 x Equal Share');
  });

});
