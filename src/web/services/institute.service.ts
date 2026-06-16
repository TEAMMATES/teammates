import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { Institutes } from '../types/api-output';

/**
 * Handles institute related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class InstituteService {
  private readonly httpRequestService = inject(HttpRequestService);

  /**
   * Gets the institutes for which the given account has been verified.
   */
  getVerifiedInstitutes(accountId: string): Observable<Institutes> {
    return this.httpRequestService.get(ResourceEndpoints.INSTITUTES, { accountid: accountId });
  }
}
