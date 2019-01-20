import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSessionClosedModalComponent } from './feedback-session-closed-modal.component';

describe('FeedbackSessionClosedModalComponent', () => {
  let component: FeedbackSessionClosedModalComponent;
  let fixture: ComponentFixture<FeedbackSessionClosedModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FeedbackSessionClosedModalComponent],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedbackSessionClosedModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
