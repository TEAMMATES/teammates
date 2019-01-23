import { ContributionPointDescriptionPipe } from './contribution-point-description.pipe';

describe('ContributionPointDescriptionPipe', () => {
  it('create an instance', () => {
    const pipe: ContributionPointDescriptionPipe = new ContributionPointDescriptionPipe();
    expect(pipe).toBeTruthy();
  });
});
