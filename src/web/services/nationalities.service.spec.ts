import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NationalitiesService } from './nationalities.service';

describe('NationalitiesService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: NationalitiesService = TestBed.get(NationalitiesService);
    expect(service).toBeTruthy();
  });
});
