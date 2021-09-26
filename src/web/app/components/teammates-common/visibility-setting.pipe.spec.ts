import { VisibilityControlNamePipe, VisibilityTypeDescriptionPipe } from './visibility-setting.pipe';

describe('VisibilityControlNamePipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityControlNamePipe = new VisibilityControlNamePipe();
    expect(pipe).toBeTruthy();
  });
});

describe('VisibilityTypeDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: VisibilityTypeDescriptionPipe = new VisibilityTypeDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});
