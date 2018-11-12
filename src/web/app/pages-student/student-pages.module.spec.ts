import { StudentPagesModule } from './student-pages.module';

describe('StudentPagesModule', () => {
  let studentPagesModule: StudentPagesModule;

  beforeEach(() => {
    studentPagesModule = new StudentPagesModule();
  });

  it('should create an instance', () => {
    expect(studentPagesModule).toBeTruthy();
  });
});
