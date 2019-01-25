import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailStudentFaqComponent } from './email-student-faq.component';

describe('EmailStudentFaqComponent', () => {
  let component: EmailStudentFaqComponent;
  let fixture: ComponentFixture<EmailStudentFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmailStudentFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailStudentFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
