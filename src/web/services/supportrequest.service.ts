import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { SupportRequestRequest } from '../types/support-req-types';
import { SupportRequestStatus, SupportRequest } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

// import SupportRequests from '../data/support-requests.dummy.json';

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
  createSupportRequest(req: SupportRequestRequest): Observable<SupportRequest> {
    return this.httpRequestService.post(ResourceEndpoints.SUPPORT_REQUEST, {}, { ...req, status: SupportRequestStatus.NEW });
  }

  /**
   * Fetch all support requests in the database by calling the API.
   */
  getAllSupportRequests(): Observable<{supportRequests: SupportRequest[]}> {
    return this.httpRequestService.get(ResourceEndpoints.SUPPORT_REQUESTS, {});
  }

  /**
   * Fetch a support request with the given ID by calling the API.
   */
  getOneSupportRequest(queryParams: { id: string }): Observable<SupportRequest> {
    const paramsMap: Record<string, string> = {
      id: queryParams.id
    };
    return this.httpRequestService.get(ResourceEndpoints.SUPPORT_REQUEST, paramsMap)
  }

  /**
   * Update support request fields for one support request with the given ID. 
   */
  updateSupportRequest(queryParams: SupportRequest): Observable<SupportRequest> {
    const paramsMap: { [key: string]: string } = {
      id: queryParams.id,
      name: queryParams.name,
      email: queryParams.email,
      type: queryParams.type.toString(),
      message: queryParams.message,
      status: queryParams.status.toString()
    };
    return this.httpRequestService.put(ResourceEndpoints.SUPPORT_REQUEST, paramsMap);
  }

  /**
    * Deletes a support request with the given ID by calling API.
    */
  deleteSupportRequest(queryParams: { id: string }): Observable<SupportRequest> {
    const paramsMap = {
      id: queryParams.id
    }

    return this.httpRequestService.delete(ResourceEndpoints.SUPPORT_REQUEST, paramsMap);
  }
}
