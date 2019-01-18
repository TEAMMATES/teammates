import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { AdminHomePageComponent } from './admin-home-page.component';

describe('AdminHomePageComponent', () => {
  let component: AdminHomePageComponent;
  let fixture: ComponentFixture<AdminHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminHomePageComponent],
      imports: [
        FormsModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
