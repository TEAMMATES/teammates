import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoaderBarModule } from './components/loader-bar/loader-bar.module';
import { LoadingSpinnerModule } from './components/loading-spinner/loading-spinner.module';
import { NotificationBannerModule } from './components/notification-banner/notification-banner.module';
import { StatusMessageModule } from './components/status-message/status-message.module';
import { TeammatesRouterModule } from './components/teammates-router/teammates-router.module';
import { ToastModule } from './components/toast/toast.module';
import { PageComponent } from './page.component';
import { PublicPageComponent } from './public-page.component';

describe('PublicPageComponent', () => {
  let component: PublicPageComponent;
  let fixture: ComponentFixture<PublicPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageComponent,
        PublicPageComponent,
      ],
      imports: [
        LoaderBarModule,
        LoadingSpinnerModule,
        NgbModule,
        RouterModule.forRoot([]),
        TeammatesRouterModule,
        StatusMessageModule,
        ToastModule,
        NotificationBannerModule,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PublicPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
