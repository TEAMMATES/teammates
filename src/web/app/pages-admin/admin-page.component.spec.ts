import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminPageComponent } from './admin-page.component';
import { LoaderBarModule } from '../components/loader-bar/loader-bar.module';
import { LoadingSpinnerModule } from '../components/loading-spinner/loading-spinner.module';
import { NotificationBannerModule } from '../components/notification-banner/notification-banner.module';
import { StatusMessageModule } from '../components/status-message/status-message.module';
import { TeammatesRouterModule } from '../components/teammates-router/teammates-router.module';
import { ToastModule } from '../components/toast/toast.module';
import { PageComponent } from '../page.component';

describe('AdminPageComponent', () => {
  let component: AdminPageComponent;
  let fixture: ComponentFixture<AdminPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageComponent,
        AdminPageComponent,
      ],
      imports: [
        NgbModule,
        HttpClientTestingModule,
        LoaderBarModule,
        RouterTestingModule,
        StatusMessageModule,
        TeammatesRouterModule,
        ToastModule,
        LoadingSpinnerModule,
        NotificationBannerModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
