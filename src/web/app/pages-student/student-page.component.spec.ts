import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerComponent } from '../components/loading-spinner/loading-spinner.component';
import { StatusMessageModule } from '../components/status-message/status-message.module';
import { PageComponent } from '../page.component';
import { StudentPageComponent } from './student-page.component';

describe('StudentPageComponent', () => {
  let component: StudentPageComponent;
  let fixture: ComponentFixture<StudentPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageComponent,
        StudentPageComponent,
        LoadingSpinnerComponent,
      ],
      imports: [
        NgbModule,
        HttpClientTestingModule,
        RouterTestingModule,
        StatusMessageModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
