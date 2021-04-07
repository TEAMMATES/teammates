import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResourceEndpoints } from '../types/api-const';
import { Nationalities } from '../types/api-output';
import { HttpRequestService } from './http-request.service';

/**
 * Handles nationalities related logic provision.
 */
@Injectable({
  providedIn: 'root',
})
export class NationalitiesService {

  constructor(private httpRequestService: HttpRequestService) { }

  /**
   * Get a list of nationalities by calling API.
   */
  getNationalities(): Observable<Nationalities> {
    return this.httpRequestService.get(ResourceEndpoints.NATIONALITIES);
  }
}
