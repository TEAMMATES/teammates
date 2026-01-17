import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogsHistogramComponent } from './logs-histogram.component';

describe('LogsHistogramComponent', () => {
  let component: LogsHistogramComponent;
  let fixture: ComponentFixture<LogsHistogramComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LogsHistogramComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogsHistogramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call drawBars on ngOnChanges when chart exists', () => {
    const drawBarsSpy = jest
      .spyOn(component as any, 'drawBars');

    component.ngOnChanges();

    expect(drawBarsSpy).toHaveBeenCalled();
  });

  it('should not call drawBars on ngOnChanges when chart does not exist', () => {
    const drawBarsSpy = jest
      .spyOn(component as any, 'drawBars');

    component['chart'] = null;
    component.ngOnChanges();

    expect(drawBarsSpy).not.toHaveBeenCalled();
  });

  it('should call createSvg on ngOnInit', () => {
    const createSvgSpy = jest.spyOn(component as any, 'createSvg');

    component.ngOnInit();

    expect(createSvgSpy).toHaveBeenCalled();
  });

  it('should call drawBars on ngOnInit', () => {
    const drawBarsSpy = jest.spyOn(component as any, 'drawBars');

    component.ngOnInit();

    expect(drawBarsSpy).toHaveBeenCalled();
  });
});
