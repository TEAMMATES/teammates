import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditStudentDetailsFaqComponent } from './edit-student-details-faq.component';

describe('EditStudentDetailsFaqComponent', () => {
  let component: EditStudentDetailsFaqComponent;
  let fixture: ComponentFixture<EditStudentDetailsFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditStudentDetailsFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditStudentDetailsFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
