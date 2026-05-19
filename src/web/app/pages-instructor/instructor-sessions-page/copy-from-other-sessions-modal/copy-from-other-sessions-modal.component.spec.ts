import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { CopyFromOtherSessionsModalComponent } from './copy-from-other-sessions-modal.component';

describe('CopyFromOtherSessionsModalComponent', () => {
  let component: CopyFromOtherSessionsModalComponent;
  let fixture: ComponentFixture<CopyFromOtherSessionsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(CopyFromOtherSessionsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
