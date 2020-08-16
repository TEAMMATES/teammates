require('core-js/es/reflect');
require('core-js/proposals/reflect-metadata');

(window as any).IntersectionObserver = jest.fn(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
}));
