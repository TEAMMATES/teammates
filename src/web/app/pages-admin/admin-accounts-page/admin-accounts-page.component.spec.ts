import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { AdminAccountsPageComponent } from './admin-accounts-page.component';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

describe('AdminAccountsPageComponent', () => {
  let component: AdminAccountsPageComponent;
  let fixture: ComponentFixture<AdminAccountsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [AdminAccountsPageComponent],
      imports: [
        RouterModule.forRoot([]),
        LoadingSpinnerModule,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminAccountsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
