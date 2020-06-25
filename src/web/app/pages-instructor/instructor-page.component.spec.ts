import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoaderBarModule } from '../components/loader-bar/loader-bar.module';
import { LoadingSpinnerComponent } from '../components/loading-spinner/loading-spinner.component';
import { StatusMessageModule } from '../components/status-message/status-message.module';
import { ToastModule } from '../components/toast/toast.module';
import { PageComponent } from '../page.component';
import { InstructorPageComponent } from './instructor-page.component';

describe('InstructorPageComponent', () => {
  let component: InstructorPageComponent;
  let fixture: ComponentFixture<InstructorPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageComponent,
        InstructorPageComponent,
        LoadingSpinnerComponent,
      ],
      imports: [
        NgbModule,
        HttpClientTestingModule,
        LoaderBarModule,
        RouterTestingModule,
        StatusMessageModule,
        ToastModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
