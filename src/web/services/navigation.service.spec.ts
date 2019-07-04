import { TestBed } from '@angular/core/testing';

import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { NavigationService } from './navigation.service';

describe('NavigationService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      MatSnackBarModule,
      RouterTestingModule,
    ],
  }));

  it('should be created', () => {
    const service: NavigationService = TestBed.get(NavigationService);
    expect(service).toBeTruthy();
  });
});
