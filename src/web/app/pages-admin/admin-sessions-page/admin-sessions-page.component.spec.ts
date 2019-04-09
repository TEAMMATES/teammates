import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminSessionsPageComponent } from './admin-sessions-page.component';

describe('AdminSessionsPageComponent', () => {
  let component: AdminSessionsPageComponent;
  let fixture: ComponentFixture<AdminSessionsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        FormsModule,
        HttpClientTestingModule,
        MatSnackBarModule,
      ],
      declarations: [AdminSessionsPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminSessionsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
