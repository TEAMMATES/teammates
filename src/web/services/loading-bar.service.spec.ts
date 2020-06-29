import { TestBed } from '@angular/core/testing';

import { LoadingBarService } from './loading-bar.service';

describe('LoadingBarService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LoadingBarService = TestBed.inject(LoadingBarService);
    expect(service).toBeTruthy();
  });
});
