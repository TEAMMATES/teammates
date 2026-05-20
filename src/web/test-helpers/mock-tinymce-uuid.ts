/**
 * Mock `Date` and `Math.random` such that TinyMCE generates a fixed UUID.
 * Refer to https://github.com/TEAMMATES/teammates/pull/9910#issuecomment-574006203 for more details.
 */
export const mockTinyMceUuid: () => void = (): void => {
  const mockDate: Date = new Date('2020');
  let randomSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(() => {
    vi.useFakeTimers();
    vi.setSystemTime(mockDate);
    randomSpy = vi.spyOn(Math, 'random').mockReturnValue(0);
  });

  afterEach(() => {
    randomSpy.mockRestore();
    vi.useRealTimers();
  });
};
