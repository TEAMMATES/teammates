import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoadingRetryModule } from '../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../components/panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../components/teammates-common/teammates-common.module';
import { NotificationsPageComponent } from './notifications-page.component';

describe('NotificationsPageComponent', () => {
  let component: NotificationsPageComponent;
  let fixture: ComponentFixture<NotificationsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NotificationsPageComponent],
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        PanelChevronModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
