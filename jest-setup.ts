require('core-js/es/reflect');
require('core-js/proposals/reflect-metadata');
require('@angular/localize/init');

(window as any).IntersectionObserver = jest.fn(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));
