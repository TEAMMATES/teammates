import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SessionPermanentDeletionConfirmModalComponent } from './session-permanent-deletion-confirm-modal.component';

describe('SessionPermanentDeletionConfirmModalComponent', () => {
  let component: SessionPermanentDeletionConfirmModalComponent;
  let fixture: ComponentFixture<SessionPermanentDeletionConfirmModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SessionPermanentDeletionConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionPermanentDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
