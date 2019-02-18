import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SessionsService } from './sessions.service';

describe('SessionsService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: SessionsService = TestBed.get(SessionsService);
    expect(service).toBeTruthy();
  });
});
