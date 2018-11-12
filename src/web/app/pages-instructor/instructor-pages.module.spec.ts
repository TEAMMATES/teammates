import { InstructorPagesModule } from './instructor-pages.module';

describe('InstructorPagesModule', () => {
  let instructorPagesModule: InstructorPagesModule;

  beforeEach(() => {
    instructorPagesModule = new InstructorPagesModule();
  });

  it('should create an instance', () => {
    expect(instructorPagesModule).toBeTruthy();
  });
});
