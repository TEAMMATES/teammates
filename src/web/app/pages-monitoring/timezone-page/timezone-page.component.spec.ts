import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { TimezonePageComponent } from './timezone-page.component';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';

describe('TimezonePageComponent', () => {
  let component: TimezonePageComponent;
  let fixture: ComponentFixture<TimezonePageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TimezonePageComponent],
      imports: [
        HttpClientTestingModule,
        LoadingSpinnerModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimezonePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
