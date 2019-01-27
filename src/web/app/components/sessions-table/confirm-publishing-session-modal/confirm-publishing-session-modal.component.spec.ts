import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmPublishingSessionModalComponent } from './confirm-publishing-session-modal.component';

describe('ConfirmPublishingSessionModalComponent', () => {
  let component: ConfirmPublishingSessionModalComponent;
  let fixture: ComponentFixture<ConfirmPublishingSessionModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmPublishingSessionModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmPublishingSessionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
