/**
 * Mock `Date` and `Math.random` such that TinyMCE generates a fixed UUID.
 * Refer to https://github.com/TEAMMATES/teammates/pull/9910#issuecomment-574006203 for more details.
 */
/* eslint-disable no-global-assign */
export const mockTinyMceUuid: () => void = (): void => {
  const mockDate: Date = new Date('2020');
  const realDate: DateConstructor = Date;
  const realMath: Math = Math;

  beforeAll(() => {
    Date = jest.fn(() => mockDate) as any;
    Math.random = jest.fn(() => 0);
  });

  afterAll(() => {
    Date = realDate;
    Math = realMath;
  });
};
/* eslint-enable no-global-assign */
