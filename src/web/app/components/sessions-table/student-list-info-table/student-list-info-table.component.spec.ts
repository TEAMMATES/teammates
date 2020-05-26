import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { StudentListInfoTableComponent } from './student-list-info-table.component';

describe('StudentListInfoTableComponent', () => {
  let component: StudentListInfoTableComponent;
  let fixture: ComponentFixture<StudentListInfoTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [StudentListInfoTableComponent],
      imports: [FormsModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentListInfoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
