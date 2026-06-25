import { Injectable } from '@angular/core';

/**
 * Service to hold the current masquerade account ID.
 */
@Injectable({
  providedIn: 'root',
})
export class MasqueradeModeService {
  static readonly MASQUERADE_ACCOUNT_ID_HEADER: string = 'X-Masquerade-Account-Id';
  private static readonly MASQUERADE_ACCOUNT_ID_STORAGE_KEY: string = 'masqueradeAccountId';

  /**
   * Gets the masquerade account ID.
   */
  getMasqueradeAccountId(): string {
    const storedAccountId: string | null = globalThis.sessionStorage.getItem(
      MasqueradeModeService.MASQUERADE_ACCOUNT_ID_STORAGE_KEY,
    );
    return storedAccountId ?? '';
  }

  /**
   * Sets the masquerade account ID.
   */
  masqueradeAs(accountId: string): void {
    globalThis.sessionStorage.setItem(MasqueradeModeService.MASQUERADE_ACCOUNT_ID_STORAGE_KEY, accountId);
  }

  /**
   * Clears the masquerade account ID.
   */
  clearMasquerade(): void {
    globalThis.sessionStorage.removeItem(MasqueradeModeService.MASQUERADE_ACCOUNT_ID_STORAGE_KEY);
  }

  /**
   * Checks whether masquerade mode is set.
   */
  isInMasqueradingMode(): boolean {
    return this.getMasqueradeAccountId() !== '';
  }

  /**
   * Gets the masquerade header for API requests.
   */
  getMasqueradeHeader(): Record<string, string> {
    return this.isInMasqueradingMode()
      ? { [MasqueradeModeService.MASQUERADE_ACCOUNT_ID_HEADER]: this.getMasqueradeAccountId() }
      : {};
  }
}
