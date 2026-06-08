import { Mock, vi } from 'vitest';

/**
 * Type for mocked HttpRequestService methods.
 * Allows calling Vitest mock methods like mockReturnValue and mockImplementation.
 */
export type MockHttpRequestService = {
  get: Mock;
  post: Mock;
  put: Mock;
  delete: Mock;
};

/**
 * Creates a mock HttpRequestService with spy functions for common HTTP methods.
 * Used to test services that depend on HttpRequestService.
 *
 * @returns A mock of HttpRequestService with get, post, put, and delete methods
 */
export function createMockHttpRequestService(): MockHttpRequestService {
  return {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  };
}
