import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { SessionPermanentDeletionConfirmModalComponent } from './session-permanent-deletion-confirm-modal.component';

describe('SessionPermanentDeletionConfirmModalComponent', () => {
  let component: SessionPermanentDeletionConfirmModalComponent;
  let fixture: ComponentFixture<SessionPermanentDeletionConfirmModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(SessionPermanentDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
