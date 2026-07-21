import { inject, Injectable, signal } from '@angular/core';
import { HttpRequestService } from './http-request.service';
import { ResourceEndpoints } from '../types/api-const';
import { Observable, of, tap } from 'rxjs';
import { Config } from '../types/api-output';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private readonly httpRequestService = inject(HttpRequestService);
  private config = signal<Config | null>(null);

  getConfig(): Observable<Config> {
    const cached = this.config();
    if (cached) {
      return of(cached);
    }

    return this.httpRequestService
      .get<Config>(ResourceEndpoints.CONFIG)
      .pipe(tap((config: Config) => this.config.set(config)));
  }
}
