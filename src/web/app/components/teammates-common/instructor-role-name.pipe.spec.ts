import { InstructorRoleNamePipe } from './instructor-role-name.pipe';

describe('InstructorRoleNamePipe', () => {
  it('create an instance', () => {
    const pipe: InstructorRoleNamePipe = new InstructorRoleNamePipe();
    expect(pipe).toBeTruthy();
  });
});
