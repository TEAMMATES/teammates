import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResendInvitationEmailModalComponent } from './resend-invitation-email-modal.component';

describe('ResendInvitationEmailModalComponent', () => {
  let component: ResendInvitationEmailModalComponent;
  let fixture: ComponentFixture<ResendInvitationEmailModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ResendInvitationEmailModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResendInvitationEmailModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
