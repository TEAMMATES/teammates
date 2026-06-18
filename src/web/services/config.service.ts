import { inject, Injectable } from '@angular/core';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { Observable } from 'rxjs';
import { Config } from '../types/api-output';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private readonly httpRequestService = inject(HttpRequestService);

  getConfig(): Observable<Config> {
    return this.httpRequestService.get(ResourceEndpoints.CONFIG);
  }
}
