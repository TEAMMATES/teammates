import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmUnpublishingSessionModalComponent } from './confirm-unpublishing-session-modal.component';

describe('ConfirmUnpublishingSessionModalComponent', () => {
  let component: ConfirmUnpublishingSessionModalComponent;
  let fixture: ComponentFixture<ConfirmUnpublishingSessionModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmUnpublishingSessionModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmUnpublishingSessionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
