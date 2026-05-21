import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { SessionsPermanentDeletionConfirmModalComponent } from './sessions-permanent-deletion-confirm-modal.component';

describe('SessionsPermanentDeletionConfirmModalComponent', () => {
  let component: SessionsPermanentDeletionConfirmModalComponent;
  let fixture: ComponentFixture<SessionsPermanentDeletionConfirmModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal],
    }).compileComponents();

    fixture = TestBed.createComponent(SessionsPermanentDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
