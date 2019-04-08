import { TestBed } from '@angular/core/testing';

import { MatSnackBarModule } from '@angular/material';
import { StatusMessageService } from './status-message.service';

describe('StatusMessageService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      MatSnackBarModule,
    ],
  }));

  it('should be created', () => {
    const service: StatusMessageService = TestBed.get(StatusMessageService);
    expect(service).toBeTruthy();
  });
});
