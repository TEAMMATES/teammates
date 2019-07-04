import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { SessionEditFormComponent } from './session-edit-form.component';
import { SessionEditFormModule } from './session-edit-form.module';

describe('SessionEditFormComponent', () => {
  let component: SessionEditFormComponent;
  let fixture: ComponentFixture<SessionEditFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        SessionEditFormModule,
        HttpClientTestingModule,
        RouterTestingModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SessionEditFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
