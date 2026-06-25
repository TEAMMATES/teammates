import { TestBed } from '@angular/core/testing';

import { MasqueradeModeService } from './masquerade-mode.service';

describe('MasqueradeModeService', () => {
  let service: MasqueradeModeService;

  beforeEach(() => {
    globalThis.sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [MasqueradeModeService],
    });
    service = TestBed.inject(MasqueradeModeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should be defined', () => {
    expect(service.getMasqueradeAccountId()).toBeDefined();
  });

  it('should return an empty header when masquerade account ID is absent', () => {
    vi.spyOn(service, 'getMasqueradeAccountId').mockReturnValue('');

    expect(service.getMasqueradeHeader()).toEqual({});
  });

  it('should return the masquerade account ID header', () => {
    vi.spyOn(service, 'getMasqueradeAccountId').mockReturnValue('account-123');

    expect(service.getMasqueradeHeader()).toEqual({
      'X-Masquerade-Account-Id': 'account-123',
    });
  });

  it('should persist masquerade account ID across service instances', () => {
    service.masqueradeAs('account-123');
    const newService = new MasqueradeModeService();

    expect(newService.getMasqueradeAccountId()).toBe('account-123');
    expect(newService.getMasqueradeHeader()).toEqual({
      'X-Masquerade-Account-Id': 'account-123',
    });
  });

  it('should clear the masquerade account ID', () => {
    service.masqueradeAs('account-123');

    service.clearMasquerade();

    expect(service.getMasqueradeAccountId()).toBe('');
    expect(globalThis.sessionStorage.getItem('masqueradeAccountId')).toBeNull();
  });
});
