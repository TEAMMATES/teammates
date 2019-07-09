import { Injectable } from '@angular/core';

/**
 * Service to check if there is masquerade user present.
 *
 */
@Injectable({
  providedIn: 'root',
})
export class MasqueradeModeService {

  private user: string = '';

  constructor() { }

  /**
   * Gets the masquerade user.
   */
  getMasqueradeUser(): string {
    return this.user;
  }

  /**
   * Updates the masquerade user.
   */
  updateMasqueradeUser(user: string): void {
    this.user = user;
  }

}
