import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { SupportRequest } from '../types/support-req-types';
import { HttpRequestService } from './http-request.service';

import SupportRequests from '../data/support-requests.dummy.json';

/**
 * Handles student related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class SupportRequestService {

  constructor(private httpRequestService: HttpRequestService) {
  }

  /**
   * Create support request by calling the API. 
   */
  createSupportRequest() {

  }

  /**
   * Fetch all support requests in the database by calling the API.
   */
  getAllSupportRequests(): Observable<SupportRequest[]> {
    return of(SupportRequests)
    // return this.httpRequestService.get(ResourceEndpoints.SUPPORT_REQUESTS)
  }

  /**
   * Fetch a support request with the given ID by calling the API.
   */
  getSupportRequest(id: string): Observable<SupportRequest> {
    const paramsMap: Record<string, string> = {
        id
      };
    return this.httpRequestService.get(ResourceEndpoints.SUPPORT_REQUESTS, paramsMap)
  }

//   /**
//    * Update support request fields. 
//    */
//   updateSupportRequest(queryParams: {}) {
//     queryParams = {} 
//   }

 /**
   * Deletes a support request with the given ID by calling API.
   */
  deleteSupportRequest(id: string): Observable<any> {
    const paramsMap: Record<string, string> = {
        id
      };
    return this.httpRequestService.delete(ResourceEndpoints.SUPPORT_REQUESTS, paramsMap);
  }
}
