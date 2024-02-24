import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MaintainerPageComponent } from './maintainer-page.component';
import { LoaderBarModule } from '../components/loader-bar/loader-bar.module';
import { LoadingSpinnerModule } from '../components/loading-spinner/loading-spinner.module';
import { NotificationBannerModule } from '../components/notification-banner/notification-banner.module';
import { StatusMessageModule } from '../components/status-message/status-message.module';
import { TeammatesRouterModule } from '../components/teammates-router/teammates-router.module';
import { ToastModule } from '../components/toast/toast.module';
import { PageComponent } from '../page.component';

describe('MaintainerPageComponent', () => {
  let component: MaintainerPageComponent;
  let fixture: ComponentFixture<MaintainerPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageComponent,
        MaintainerPageComponent,
      ],
      imports: [
        NgbModule,
        HttpClientTestingModule,
        LoaderBarModule,
        LoadingSpinnerModule,
        RouterTestingModule,
        StatusMessageModule,
        TeammatesRouterModule,
        ToastModule,
        NotificationBannerModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MaintainerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
