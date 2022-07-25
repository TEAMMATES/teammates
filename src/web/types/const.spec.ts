import { ApiConst } from './api-const';
import { FeedbackQuestionType } from './api-output';
import {
  DEFAULT_INSTRUCTOR_PRIVILEGE,
  DEFAULT_PRIVILEGE_COOWNER,
  DEFAULT_PRIVILEGE_MANAGER,
  DEFAULT_PRIVILEGE_OBSERVER,
  DEFAULT_PRIVILEGE_TUTOR,
} from './default-instructor-privilege';
import {
  DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS,
  DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS,
  DEFAULT_CONSTSUM_RESPONSE_DETAILS,
  DEFAULT_CONTRIBUTION_QUESTION_DETAILS,
  DEFAULT_CONTRIBUTION_RESPONSE_DETAILS,
  DEFAULT_MCQ_QUESTION_DETAILS,
  DEFAULT_MCQ_RESPONSE_DETAILS,
  DEFAULT_MSQ_QUESTION_DETAILS,
  DEFAULT_MSQ_RESPONSE_DETAILS,
  DEFAULT_NUMSCALE_QUESTION_DETAILS,
  DEFAULT_NUMSCALE_RESPONSE_DETAILS,
  DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
  DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS,
  DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS,
  DEFAULT_RUBRIC_QUESTION_DETAILS,
  DEFAULT_RUBRIC_RESPONSE_DETAILS,
  DEFAULT_TEXT_QUESTION_DETAILS,
  DEFAULT_TEXT_RESPONSE_DETAILS,
} from './default-question-structs';

describe('Constants', () => {
  // Here we test that the constants are positive numbers
  // The exact values are implementation details that do not matter
  it('should generate length limits correctly', () => {
    expect(typeof ApiConst.COURSE_ID_MAX_LENGTH).toEqual('number');
    expect(ApiConst.COURSE_ID_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.COURSE_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.COURSE_NAME_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.PERSON_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.PERSON_NAME_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.SECTION_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.SECTION_NAME_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.TEAM_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.TEAM_NAME_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.EMAIL_MAX_LENGTH).toEqual('number');
    expect(ApiConst.EMAIL_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.FEEDBACK_SESSION_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.FEEDBACK_SESSION_NAME_MAX_LENGTH).toBeGreaterThan(0);
  });

  // Here we test that the constants are numbers
  // The exact values are implementation details that do not matter
  it('should generate numerical constants correctly', () => {
    expect(typeof ApiConst.CONTRIBUTION_POINT_NOT_SUBMITTED).toEqual('number');
    expect(typeof ApiConst.CONTRIBUTION_POINT_NOT_INITIALIZED).toEqual('number');
    expect(typeof ApiConst.CONTRIBUTION_POINT_NOT_SURE).toEqual('number');
    expect(typeof ApiConst.CONTRIBUTION_POINT_EQUAL_SHARE).toEqual('number');
    expect(typeof ApiConst.NUMERICAL_SCALE_ANSWER_NOT_SUBMITTED).toEqual('number');
    expect(typeof ApiConst.RANK_OPTIONS_ANSWER_NOT_SUBMITTED).toEqual('number');
    expect(typeof ApiConst.RANK_RECIPIENTS_ANSWER_NOT_SUBMITTED).toEqual('number');
    expect(typeof ApiConst.NO_VALUE).toEqual('number');
  });

  // Here we test that:
  // 1. The string is parseable to JSON
  // 2. The question type is correct
  // 3. There is questionText field
  // They are sufficient to ascertain that the correct structure is generated
  it('should generate question details correctly', () => {
    expect(DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.CONSTSUM_OPTIONS);
    expect(DEFAULT_CONSTSUM_OPTIONS_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS().questionType)
        .toEqual(FeedbackQuestionType.CONSTSUM_RECIPIENTS);
    expect(DEFAULT_CONSTSUM_RECIPIENTS_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_CONTRIBUTION_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.CONTRIB);
    expect(DEFAULT_CONTRIBUTION_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_MCQ_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.MCQ);
    expect(DEFAULT_MCQ_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_MSQ_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.MSQ);
    expect(DEFAULT_MSQ_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_NUMSCALE_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.NUMSCALE);
    expect(DEFAULT_NUMSCALE_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.RANK_OPTIONS);
    expect(DEFAULT_RANK_OPTIONS_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.RANK_RECIPIENTS);
    expect(DEFAULT_RANK_RECIPIENTS_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_RUBRIC_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.RUBRIC);
    expect(DEFAULT_RUBRIC_QUESTION_DETAILS().questionText).toEqual('');

    expect(DEFAULT_TEXT_QUESTION_DETAILS().questionType).toEqual(FeedbackQuestionType.TEXT);
    expect(DEFAULT_TEXT_QUESTION_DETAILS().questionText).toEqual('');
  });

  // Here we test that:
  // 1. The string is parseable to JSON
  // 2. The question type is correct
  // 3. There is either answer or answers field (depending on question type)
  // They are sufficient to ascertain that the correct structure is generated
  it('should generate response details correctly', () => {
    expect(DEFAULT_CONSTSUM_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.CONSTSUM);
    expect(DEFAULT_CONSTSUM_RESPONSE_DETAILS().answers).toBeTruthy();

    expect(DEFAULT_CONTRIBUTION_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.CONTRIB);
    expect(DEFAULT_CONTRIBUTION_RESPONSE_DETAILS().answer).toBeTruthy();

    expect(DEFAULT_MCQ_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.MCQ);
    expect(DEFAULT_MCQ_RESPONSE_DETAILS().answer).toEqual('');

    expect(DEFAULT_MSQ_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.MSQ);
    expect(DEFAULT_MSQ_RESPONSE_DETAILS().answers).toBeTruthy();

    expect(DEFAULT_NUMSCALE_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.NUMSCALE);
    expect(DEFAULT_NUMSCALE_RESPONSE_DETAILS().answer).toBeTruthy();

    expect(DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.RANK_OPTIONS);
    expect(DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS().answers).toBeTruthy();

    expect(DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.RANK_RECIPIENTS);
    expect(DEFAULT_RANK_RECIPIENTS_RESPONSE_DETAILS().answer).toBeTruthy();

    expect(DEFAULT_RUBRIC_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.RUBRIC);
    expect(DEFAULT_RUBRIC_RESPONSE_DETAILS().answer).toBeTruthy();

    expect(DEFAULT_TEXT_RESPONSE_DETAILS().questionType).toEqual(FeedbackQuestionType.TEXT);
    expect(DEFAULT_TEXT_RESPONSE_DETAILS().answer).toEqual('');
  });

  // Here we just test that the string is parseable to JSON and one representative privilege is correct
  it('should generate instructor privileges correctly', () => {
    expect(DEFAULT_INSTRUCTOR_PRIVILEGE()).toBeTruthy();
    expect(DEFAULT_INSTRUCTOR_PRIVILEGE().canModifyCourse).toBeFalsy();

    expect(DEFAULT_PRIVILEGE_COOWNER()).toBeTruthy();
    expect(DEFAULT_PRIVILEGE_COOWNER().canModifyCourse).toBeTruthy();

    expect(DEFAULT_PRIVILEGE_MANAGER()).toBeTruthy();
    expect(DEFAULT_PRIVILEGE_MANAGER().canModifyStudent).toBeTruthy();

    expect(DEFAULT_PRIVILEGE_OBSERVER()).toBeTruthy();
    expect(DEFAULT_PRIVILEGE_OBSERVER().canViewStudentInSections).toBeTruthy();

    expect(DEFAULT_PRIVILEGE_TUTOR()).toBeTruthy();
    expect(DEFAULT_PRIVILEGE_TUTOR().canSubmitSessionInSections).toBeTruthy();
  });
});
