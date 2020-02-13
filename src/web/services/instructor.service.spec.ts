import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { InstructorService } from './instructor.service';

describe('InstructorService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: InstructorService = TestBed.get(InstructorService);
    expect(service).toBeTruthy();
  });
});
