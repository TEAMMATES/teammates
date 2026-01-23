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
    (component as any).chart = null;

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

  it('should render one bar per log entry', () => {
    component.data = [
      {sourceLocation: {file: 'A', function: 'f1', line: 10}, numberOfTimes: 1},
      {sourceLocation: {file: 'B', function: 'f2', line: 20}, numberOfTimes: 3},
    ];
    
    component.ngOnInit();

    const bars = fixture.nativeElement.querySelectorAll('.bar');
    expect(bars.length).toBe(2);
  });

  it('should set xScale domain based on file and function', () => {
    component.data = [
      { sourceLocation: { file: 'A', function: 'f1', line: 10 }, numberOfTimes: 1 },
      { sourceLocation: { file: 'B', function: 'f2', line: 20 }, numberOfTimes: 3 },
    ];

    component.ngOnInit();

    const domain = component['xScale'].domain();
    expect(domain).toEqual(['Af1', 'Bf2']);
  });

  it('should set yScale max to the highest numberOfTimes', () => {
    component.data = [
      { sourceLocation: { file: 'A', function: 'f1', line: 1 }, numberOfTimes: 5 },
      { sourceLocation: { file: 'B', function: 'f2', line: 2 }, numberOfTimes: 10 },
    ];

    component.ngOnInit();

    const domain = component['yScale'].domain();
    expect(domain).toEqual([0, 10]);
  });

});
