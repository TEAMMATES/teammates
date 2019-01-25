import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SendRemindersToStudentModalComponent } from './send-reminders-to-student-modal.component';

describe('SendRemindersToStudentModalComponent', () => {
  let component: SendRemindersToStudentModalComponent;
  let fixture: ComponentFixture<SendRemindersToStudentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SendRemindersToStudentModalComponent],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SendRemindersToStudentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
