import { InstructorRoleDescriptionPipe } from './instructor-role-description.pipe';

describe('InstructorRoleDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: InstructorRoleDescriptionPipe = new InstructorRoleDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});
