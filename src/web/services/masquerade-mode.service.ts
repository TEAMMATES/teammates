import { Injectable } from '@angular/core';

/**
 * Service to hold the current masquerade account ID.
 */
@Injectable({
  providedIn: 'root',
})
export class MasqueradeModeService {
  /**
   * Gets the masquerade account ID.
   */
  getMasqueradeAccountId(): string {
    const urlParams: URLSearchParams = new URLSearchParams(window.location.search);
    const accountIdParam: string | null = urlParams.get('masqueradeaccountid');
    return accountIdParam || '';
  }

  /**
   * Checks whether masquerade mode is set.
   */
  isInMasqueradingMode(): boolean {
    return this.getMasqueradeAccountId() !== '';
  }
}
