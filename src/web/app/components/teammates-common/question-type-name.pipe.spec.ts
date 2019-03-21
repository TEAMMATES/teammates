import { QuestionTypeNamePipe } from './question-type-name.pipe';

describe('QuestionTypeNamePipe', () => {
  it('create an instance', () => {
    const pipe: QuestionTypeNamePipe = new QuestionTypeNamePipe();
    expect(pipe).toBeTruthy();
  });
});
