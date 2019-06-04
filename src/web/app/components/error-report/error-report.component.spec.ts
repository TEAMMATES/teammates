import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ErrorReportComponent } from './error-report.component';

describe('ErrorReportComponent', () => {
  let component: ErrorReportComponent;
  let fixture: ComponentFixture<ErrorReportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorReportComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have "Something went wrong" header', () => {
    const h2 = fixture.nativeElement.querySelector('h2');
    if (h2) {
      expect(h2.textContent).toContain('Something went wrong');
    }
  });

  it('should display error message', () => {
    const error = "Error message sample";
    component.errorMessage = error;

    fixture.detectChanges();

    const p = fixture.nativeElement.querySelector('p');
    if (p) {
      expect(p.textContent).toContain(error);
    }
  });

  it('should get user input from Subject form', () => {
    const input = fixture.nativeElement.querySelectorAll('input');

    expect(component.subject).toEqual('User-submitted Error Report');
    input[1].value = 'testInput';
    input[1].dispatchEvent((new Event('input')));
    expect(component.subject).toEqual('testInput');
  });
});
