import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentUseGaccountFaqComponent } from './student-use-gaccount-faq.component';

describe('StudentUseGaccountFaqComponent', () => {
  let component: StudentUseGaccountFaqComponent;
  let fixture: ComponentFixture<StudentUseGaccountFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StudentUseGaccountFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentUseGaccountFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
