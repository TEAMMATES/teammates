import { TestBed } from '@angular/core/testing';

import { StudentProfileService } from './student-profile.service';
import { HttpClientTestingModule } from "@angular/common/http/testing";

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
