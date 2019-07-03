import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Service to check if there is masquerade user present.
 *
 */
@Injectable({
  providedIn: 'root',
})
export class MasqueradeModeService {

  private user: string = '';

  constructor(private route: ActivatedRoute) { }

  /**
   * Fetches the user param from the route.
   */
  fetchMasqueradeUser(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.user) {
        this.user = queryParams.user;
      }
    });
  }

  /**
   * Gets the masquerade user.
   */
  getMasqueradeUser(): string {
    return this.user;
  }

}
