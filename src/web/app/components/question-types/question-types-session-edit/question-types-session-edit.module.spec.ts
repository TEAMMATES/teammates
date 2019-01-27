import { QuestionTypesSessionEditModule } from './question-types-session-edit.module';

describe('QuestionTypesSessionEditModule', () => {
  let questionTypesSessionEditModule: QuestionTypesSessionEditModule;

  beforeEach(() => {
    questionTypesSessionEditModule = new QuestionTypesSessionEditModule();
  });

  it('should create an instance', () => {
    expect(questionTypesSessionEditModule).toBeTruthy();
  });
});
