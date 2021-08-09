import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailLogDetailsComponent } from './email-log-details.component';

describe('EmailLogDetailsComponent', () => {
  let component: EmailLogDetailsComponent;
  let fixture: ComponentFixture<EmailLogDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EmailLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
