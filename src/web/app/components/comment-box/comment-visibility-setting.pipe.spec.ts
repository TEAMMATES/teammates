import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe, CommentVisibilityTypeNamePipe, CommentVisibilityTypesJointNamePipe,
} from './comment-visibility-setting.pipe';

describe('CommentVisibilityControlNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityControlNamePipe = new CommentVisibilityControlNamePipe();
    expect(pipe).toBeTruthy();
  });
});

describe('CommentVisibilityTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypeDescriptionPipe = new CommentVisibilityTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});

describe('CommentVisibilityTypeNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypeNamePipe = new CommentVisibilityTypeNamePipe();
    expect(pipe).toBeTruthy();
  });
});

describe('CommentVisibilityTypesJointNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypesJointNamePipe = new CommentVisibilityTypesJointNamePipe();
    expect(pipe).toBeTruthy();
  });
});
