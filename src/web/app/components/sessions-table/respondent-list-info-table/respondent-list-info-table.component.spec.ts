import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RespondentListInfoTableComponent } from './respondent-list-info-table.component';

describe('StudentListInfoTableComponent', () => {
  let component: RespondentListInfoTableComponent;
  let fixture: ComponentFixture<RespondentListInfoTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RespondentListInfoTableComponent],
      imports: [FormsModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RespondentListInfoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
