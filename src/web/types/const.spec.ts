import { ApiConst } from './api-const';

describe('Constants', () => {
  // Here we test that the constants are positive numbers
  // The exact values are implementation details that do not matter
  it('should generate length limits correctly', () => {
    expect(typeof ApiConst.COURSE_ID_MAX_LENGTH).toEqual('number');
    expect(ApiConst.COURSE_ID_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.COURSE_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.COURSE_NAME_MAX_LENGTH).toBeGreaterThan(0);

    expect(typeof ApiConst.STUDENT_NAME_MAX_LENGTH).toEqual('number');
    expect(ApiConst.STUDENT_NAME_MAX_LENGTH).toBeGreaterThan(0);

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
});
