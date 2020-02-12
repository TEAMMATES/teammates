import { TestBed } from '@angular/core/testing';

import { NationalitiesService } from './nationalities.service';

describe('NationalitiesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: NationalitiesService = TestBed.get(NationalitiesService);
    expect(service).toBeTruthy();
  });
});
