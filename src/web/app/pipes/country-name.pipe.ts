import { Pipe, PipeTransform } from '@angular/core';

const regionDisplayNames = new Intl.DisplayNames(['en'], { type: 'region' });

/**
 * Converts an ISO 3166-1 alpha-2 country code to its display name using the Intl API.
 */
@Pipe({ name: 'countryName' })
export class CountryNamePipe implements PipeTransform {
  /**
   * Returns the country/region name for the given alpha-2 code, or the original value if it cannot
   * be resolved (e.g. empty or malformed code).
   */
  transform(code: string | null | undefined): string {
    if (!code) {
      return '';
    }
    try {
      return regionDisplayNames.of(code) ?? code;
    } catch {
      // Intl.DisplayNames.of throws a RangeError on a structurally invalid code.
      return code;
    }
  }
}
