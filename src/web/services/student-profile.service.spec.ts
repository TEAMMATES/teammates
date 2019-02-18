import { TestBed } from '@angular/core/testing';

import { StudentProfileService } from './student-profile.service';

describe('StudentProfileService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: StudentProfileService = TestBed.get(StudentProfileService);
    expect(service).toBeTruthy();
  });
});
