import { TestBed } from '@angular/core/testing';

import { LogService } from './log.service';
import { HttpRequestService } from './http-request.service';

describe('LogService', () => {
  let service: LogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: HttpRequestService },
      ],
    });
    service = TestBed.inject(LogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
