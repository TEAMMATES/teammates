import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RequestPageComponent } from './request-page.component';
import { RouterModule } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('RequestPageComponent', () => {
  let component: RequestPageComponent;
  let fixture: ComponentFixture<RequestPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render correctly before instructor declaration is done', () => {
    component.isDeclarationDone = false;
    component.submittedFormData = null;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should render correctly after instructor declaration is done', () => {
    component.isDeclarationDone = true;
    component.submittedFormData = null;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should render correctly after form is submitted', () => {
    component.submittedFormData = {
      name: 'Jane Smith',
      institution: 'University of Example',
      country: 'Example Republic',
      email: 'js@exampleu.edu',
      comments: '',
    };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
