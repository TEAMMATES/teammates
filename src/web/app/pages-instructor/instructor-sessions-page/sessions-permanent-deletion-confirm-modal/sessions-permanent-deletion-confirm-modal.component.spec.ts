import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SessionsPermanentDeletionConfirmModalComponent } from './sessions-permanent-deletion-confirm-modal.component';

describe('SessionsPermanentDeletionConfirmModalComponent', () => {
  let component: SessionsPermanentDeletionConfirmModalComponent;
  let fixture: ComponentFixture<SessionsPermanentDeletionConfirmModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SessionsPermanentDeletionConfirmModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionsPermanentDeletionConfirmModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
