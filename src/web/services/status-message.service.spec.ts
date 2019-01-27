import { TestBed } from '@angular/core/testing';

import { StatusMessageService } from './status-message.service';

describe('StatusMessageService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: StatusMessageService = TestBed.get(StatusMessageService);
    expect(service).toBeTruthy();
  });
});
