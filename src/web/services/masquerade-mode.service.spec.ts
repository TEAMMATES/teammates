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
    service = TestBed.get(MasqueradeModeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getMasqueradeUser should be defined', () => {
    expect(service.getMasqueradeUser()).toBeDefined();
  });

  it('setMasqueradeUser should set user', () => {
    const testUser: string = 'test';
    service.setMasqueradeUser(testUser);
    expect(service.getMasqueradeUser()).toEqual(testUser);
  });

  it('isInMasqueradingMode should be false when no user', () => {
    expect(service.isInMasqueradingMode()).toBe(false);
  });

  it('isInMasqueradingMode should be true when user set', () => {
    const testUser: string = 'test';
    service.setMasqueradeUser(testUser);
    expect(service.isInMasqueradingMode()).toBe(true);
  });
});
