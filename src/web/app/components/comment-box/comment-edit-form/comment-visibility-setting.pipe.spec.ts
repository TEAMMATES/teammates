import {
  CommentVisibilityControlNamePipe,
  CommentVisibilityTypeDescriptionPipe,
} from './comment-visibility-setting.pipe';

describe('VisibilityControlNamePipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityControlNamePipe = new CommentVisibilityControlNamePipe();
    expect(pipe).toBeTruthy();
  });
});

describe('VisibilityTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: CommentVisibilityTypeDescriptionPipe = new CommentVisibilityTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});
