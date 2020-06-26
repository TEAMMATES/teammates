import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoaderBarModule } from './components/loader-bar/loader-bar.module';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { StatusMessageModule } from './components/status-message/status-message.module';
import { ToastModule } from './components/toast/toast.module';
import { PageComponent } from './page.component';

describe('PageComponent', () => {
  let component: PageComponent;
  let fixture: ComponentFixture<PageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageComponent,
        LoadingSpinnerComponent,
      ],
      imports: [
        NgbModule,
        LoaderBarModule,
        RouterTestingModule,
        StatusMessageModule,
        ToastModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
