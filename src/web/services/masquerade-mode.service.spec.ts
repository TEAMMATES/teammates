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

  it('should set masquerade user', () => {
    const testUser: string = 'test';
    service.setMasqueradeUser(testUser);
    expect(service.getMasqueradeUser()).toEqual(testUser);
  });

  it('should be false when no user is set', () => {
    expect(service.isInMasqueradingMode()).toBe(false);
  });

  it('should be true when user is set', () => {
    const testUser: string = 'test';
    service.setMasqueradeUser(testUser);
    expect(service.isInMasqueradingMode()).toBe(true);
  });
});
