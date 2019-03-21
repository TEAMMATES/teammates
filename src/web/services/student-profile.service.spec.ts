import { TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { StudentProfileService } from './student-profile.service';

describe('StudentProfileService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: StudentProfileService = TestBed.get(StudentProfileService);
    expect(service).toBeTruthy();
  });
});
