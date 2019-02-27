import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSessionDeletedModalComponent } from './feedback-session-deleted-modal.component';

describe('FeedbackSessionDeletedModalComponent', () => {
  let component: FeedbackSessionDeletedModalComponent;
  let fixture: ComponentFixture<FeedbackSessionDeletedModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FeedbackSessionDeletedModalComponent],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackSessionDeletedModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
