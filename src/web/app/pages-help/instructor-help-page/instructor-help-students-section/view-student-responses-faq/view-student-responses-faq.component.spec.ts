import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewStudentResponsesFaqComponent } from './view-student-responses-faq.component';

describe('ViewStudentResponsesFaqComponent', () => {
  let component: ViewStudentResponsesFaqComponent;
  let fixture: ComponentFixture<ViewStudentResponsesFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewStudentResponsesFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewStudentResponsesFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
