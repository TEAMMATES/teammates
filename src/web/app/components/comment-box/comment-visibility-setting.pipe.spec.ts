import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe, CommentVisibilityTypesJointNamePipe,
} from './comment-visibility-setting.pipe';
import { CommentVisibilityType } from '../../../types/api-output';
import { CommentVisibilityControl } from '../../../types/comment-visibility-control';

describe('CommentVisibilityControlNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityControlNamePipe = new CommentVisibilityControlNamePipe();
    expect(pipe).toBeTruthy();
  });

  it('should return appropriate display strings given a CommentVisibilityControl', () => {
    const pipe: CommentVisibilityControlNamePipe = new CommentVisibilityControlNamePipe();

    expect(pipe.transform(CommentVisibilityControl.SHOW_COMMENT)).toBe('Can see this comment');
    expect(pipe.transform(CommentVisibilityControl.SHOW_GIVER_NAME)).toBe("Can see comment giver's name");
    expect(pipe.transform('INVALID_VALUE' as CommentVisibilityControl)).toBe('Unknown');
  });
});

describe('CommentVisibilityTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypeDescriptionPipe = new CommentVisibilityTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return appropriate display strings given a CommentVisibilityType', () => {
    const pipe: CommentVisibilityTypeDescriptionPipe = new CommentVisibilityTypeDescriptionPipe();

    expect(pipe.transform(CommentVisibilityType.GIVER)).toBe('Control what response giver(s) can view');
    expect(pipe.transform(CommentVisibilityType.RECIPIENT)).toBe('Control what response recipient(s) can view');
    expect(pipe.transform(CommentVisibilityType.INSTRUCTORS)).toBe('Control what instructors can view');
    expect(pipe.transform(CommentVisibilityType.GIVER_TEAM_MEMBERS))
      .toBe('Control what team members of response giver can view');
    expect(pipe.transform(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS))
      .toBe('Control what team members of response recipient(s) can view');
    expect(pipe.transform(CommentVisibilityType.STUDENTS))
      .toBe('Control what other students in this course can view');
    expect(pipe.transform('INVALID_VALUE' as CommentVisibilityType)).toBe('Unknown');
  });
});

describe('CommentVisibilityTypeNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypeNamePipe = new CommentVisibilityTypeNamePipe();
    expect(pipe).toBeTruthy();
  });

  it('should return appropriate display strings given a CommentVisibilityType', () => {
    const pipe: CommentVisibilityTypeNamePipe = new CommentVisibilityTypeNamePipe();

    expect(pipe.transform(CommentVisibilityType.GIVER)).toBe('Response Giver(s)');
    expect(pipe.transform(CommentVisibilityType.RECIPIENT)).toBe('Response Recipient(s)');
    expect(pipe.transform(CommentVisibilityType.INSTRUCTORS)).toBe('Instructors');
    expect(pipe.transform(CommentVisibilityType.GIVER_TEAM_MEMBERS)).toBe("Response Giver's Team Members");
    expect(pipe.transform(CommentVisibilityType.RECIPIENT_TEAM_MEMBERS)).toBe("Response Recipient's Team Members");
    expect(pipe.transform(CommentVisibilityType.STUDENTS)).toBe('Other students in this course');
    expect(pipe.transform('INVALID_VALUE' as CommentVisibilityType)).toBe('Unknown');
  });
});

describe('CommentVisibilityTypesJointNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypesJointNamePipe = new CommentVisibilityTypesJointNamePipe();
    expect(pipe).toBeTruthy();
  });

  it('should handle an empty array', () => {
    const pipe: CommentVisibilityTypesJointNamePipe = new CommentVisibilityTypesJointNamePipe();
    expect(pipe.transform([])).toBe('nobody');
  });

  it('should output the correct string when given an array of 1 CommentVisibilityTypes', () => {
    const pipe: CommentVisibilityTypesJointNamePipe = new CommentVisibilityTypesJointNamePipe();
    expect(pipe.transform([CommentVisibilityType.GIVER])).toBe('response giver(s)');
  });

  it('should output the correct string when given an array of CommentVisibilityTypes', () => {
    const pipe: CommentVisibilityTypesJointNamePipe = new CommentVisibilityTypesJointNamePipe();
    expect(pipe
      .transform([CommentVisibilityType.GIVER,
        CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.STUDENTS]))
      .toBe('response giver(s), and instructors, and other students in this course');
  });

  it('should output a string when given an array of CommentVisibilityTypes and an invalid value', () => {
    const pipe: CommentVisibilityTypesJointNamePipe = new CommentVisibilityTypesJointNamePipe();
    expect(pipe
      .transform(['INVALID_VALUE' as CommentVisibilityType,
        CommentVisibilityType.INSTRUCTORS, CommentVisibilityType.STUDENTS]))
      .toBe('unknown, and instructors, and other students in this course');
  });
});
