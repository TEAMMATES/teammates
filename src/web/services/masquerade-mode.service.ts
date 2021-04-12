import { Injectable } from '@angular/core';

/**
 * Service to hold the current masquerade user.
 */
@Injectable({
  providedIn: 'root',
})
export class MasqueradeModeService {

  constructor() { }

  /**
   * Gets the masquerade user.
   */
  getMasqueradeUser(): string {
    const urlParams: URLSearchParams = new URLSearchParams(window.location.search);
    const userParam: string | null = urlParams.get('user');
    return userParam ? userParam : '';
  }

  /**
   * Checks whether masquerade mode is set.
   */
  isInMasqueradingMode(): boolean {
    return this.getMasqueradeUser() !== '';
  }

}
