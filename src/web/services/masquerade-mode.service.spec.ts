import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MasqueradeModeService } from './masquerade-mode.service';

describe('MasqueradeModeService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      RouterTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: MasqueradeModeService = TestBed.get(MasqueradeModeService);
    expect(service).toBeTruthy();
  });
});
