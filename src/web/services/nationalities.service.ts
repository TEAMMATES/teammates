import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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

  getNationalities(): Observable<Nationalities> {
    return this.httpRequestService.get('/nationalities');
  }
}
