import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { AdminAccountsPageComponent } from './admin-accounts-page.component';

describe('AdminAccountsPageComponent', () => {
  let component: AdminAccountsPageComponent;
  let fixture: ComponentFixture<AdminAccountsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminAccountsPageComponent],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        LoadingSpinnerModule,
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
