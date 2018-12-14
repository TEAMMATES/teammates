import { QuestionTypesSessionSubmissionModule } from './question-types-session-submission.module';

describe('QuestionTypesSessionSubmissionModule', () => {
  let questionTypesSessionSubmissionModule: QuestionTypesSessionSubmissionModule;

  beforeEach(() => {
    questionTypesSessionSubmissionModule = new QuestionTypesSessionSubmissionModule();
  });

  it('should create an instance', () => {
    expect(questionTypesSessionSubmissionModule).toBeTruthy();
  });
});
