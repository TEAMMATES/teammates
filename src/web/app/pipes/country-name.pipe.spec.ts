import { CountryNamePipe } from './country-name.pipe';

describe('CountryNamePipe', () => {
  let countryNamePipe: CountryNamePipe;

  beforeEach(() => {
    countryNamePipe = new CountryNamePipe();
  });

  it('should be instantiated', () => {
    expect(countryNamePipe).toBeTruthy();
  });

  it('should convert an alpha-2 code to its country name', () => {
    expect(countryNamePipe.transform('SG')).toBe('Singapore');
    expect(countryNamePipe.transform('US')).toBe('United States');
  });

  it('should return an empty string for empty input', () => {
    expect(countryNamePipe.transform('')).toBe('');
    expect(countryNamePipe.transform(null)).toBe('');
    expect(countryNamePipe.transform(undefined)).toBe('');
  });

  it('should return the original value for a malformed code', () => {
    expect(countryNamePipe.transform('invalid')).toBe('invalid');
  });
});
