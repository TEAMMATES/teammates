import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ResendReminderModalComponent } from './resend-reminder-modal.component';

describe('ResendReminderModalComponent', () => {
  let component: ResendReminderModalComponent;
  let fixture: ComponentFixture<ResendReminderModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ResendReminderModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResendReminderModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
