import { QuestionTypesSessionResultModule } from './question-types-session-result.module';

describe('QuestionTypesSessionResultModule', () => {
  let questionTypesSessionResultModule: QuestionTypesSessionResultModule;

  beforeEach(() => {
    questionTypesSessionResultModule = new QuestionTypesSessionResultModule();
  });

  it('should create an instance', () => {
    expect(questionTypesSessionResultModule).toBeTruthy();
  });
});
