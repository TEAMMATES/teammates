import { TestBed } from '@angular/core/testing';

import { MasqueradeModeService } from './masquerade-mode.service';

describe('MasqueradeModeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: MasqueradeModeService = TestBed.get(MasqueradeModeService);
    expect(service).toBeTruthy();
  });
});
