import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentChangeGaccountFaqComponent } from './student-change-gaccount-faq.component';

describe('StudentChangeGaccountFaqComponent', () => {
  let component: StudentChangeGaccountFaqComponent;
  let fixture: ComponentFixture<StudentChangeGaccountFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StudentChangeGaccountFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentChangeGaccountFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
