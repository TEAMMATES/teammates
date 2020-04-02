import { Injectable } from '@angular/core';

/**
 * Service to hold the current masquerade user.
 */
@Injectable({
  providedIn: 'root',
})
export class MasqueradeModeService {

  private userIdToMasquerade: string = '';

  constructor() { }

  /**
   * Gets the masquerade user.
   */
  getMasqueradeUser(): string {
    return this.userIdToMasquerade;
  }

  /**
   * Sets the masquerade user.
   */
  setMasqueradeUser(user: string): void {
    this.userIdToMasquerade = user;
  }

  /**
   * Checks whether masquerade mode is set.
   */
  isInMasqueradingMode(): boolean {
    return this.userIdToMasquerade !== '';
  }

}
