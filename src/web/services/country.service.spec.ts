import { TestBed } from '@angular/core/testing';
import { vi } from 'vitest';

import countryCodes from '../data/country-codes.json';
import { CountryService } from './country.service';

describe('CountryService', () => {
  let service: CountryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CountryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return exactly one option per country code with no duplicates', () => {
    const codes = service.getCountryOptions().map((o) => o.code);
    expect(new Set(codes).size).toEqual(countryCodes.length);
    expect(codes.length).toEqual(countryCodes.length);
  });

  it('should resolve known codes to non-empty names', () => {
    const map = new Map(service.getCountryOptions().map((o) => [o.code, o.name]));
    expect(map.get('US')).toBeTruthy();
    expect(map.get('SG')).toBeTruthy();
    expect(map.get('GB')).toBeTruthy();
  });

  it('should return options sorted alphabetically by name', () => {
    const names = service.getCountryOptions().map((o) => o.name);
    const sorted = [...names].sort((a, b) => a.localeCompare(b));
    expect(names).toEqual(sorted);
  });

  it('should return a stable reference across multiple calls', () => {
    expect(service.getCountryOptions()).toBe(service.getCountryOptions());
  });

  it('should exclude codes that Intl cannot resolve a name for', () => {
    vi.spyOn(globalThis.Intl, 'DisplayNames').mockImplementation(function () {
      return { of: (code: string) => (code === 'US' ? 'United States' : undefined) };
    });

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({});
    const freshService = TestBed.inject(CountryService);

    expect(freshService.getCountryOptions()).toEqual([{ code: 'US', name: 'United States' }]);

    vi.restoreAllMocks();
  });
});
