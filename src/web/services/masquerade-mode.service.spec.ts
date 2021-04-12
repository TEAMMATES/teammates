import { TestBed } from '@angular/core/testing';

import { MasqueradeModeService } from './masquerade-mode.service';

describe('MasqueradeModeService', () => {
  let service: MasqueradeModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MasqueradeModeService,
      ],
    });
    service = TestBed.inject(MasqueradeModeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should be defined', () => {
    expect(service.getMasqueradeUser()).toBeDefined();
  });
});
