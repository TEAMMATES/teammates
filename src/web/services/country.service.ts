import { Injectable } from '@angular/core';

import countryCodes from '../data/country-codes.json';

export interface CountryOption {
  code: string;
  name: string;
}

/**
 * Provides country or region data formatted for display, using the browser's Intl API.
 */
@Injectable({
  providedIn: 'root',
})
export class CountryService {
  private readonly countryOptions: CountryOption[];

  constructor() {
    const displayNames = new Intl.DisplayNames(['en'], { type: 'region' });
    this.countryOptions = countryCodes
      .reduce<CountryOption[]>((acc, code) => {
        const name = displayNames.of(code);
        if (name !== undefined) acc.push({ code, name });
        return acc;
      }, [])
      .sort((a, b) => a.name.localeCompare(b.name));
  }

  getCountryOptions(): CountryOption[] {
    return this.countryOptions;
  }
}
