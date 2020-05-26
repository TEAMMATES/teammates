import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminTimezonePageComponent } from './admin-timezone-page.component';

describe('AdminTimezonePageComponent', () => {
  let component: AdminTimezonePageComponent;
  let fixture: ComponentFixture<AdminTimezonePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminTimezonePageComponent],
      imports: [
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminTimezonePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
