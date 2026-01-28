/**
 * Creates a deep copy of an object using JSON serialization. This function should
 * only be used in test code since structuredClone is unavailable in the test
 * environment.
 *
 * This function should not be used for objects containing functions, Dates,
 * Maps, Sets, or other non-serializable types.
 *
 * @param obj The object to deep copy
 * @returns A deep copy of the input object
 *
 * @example
 * const original = { name: 'John', age: 30 };
 * const copy = deepCopy(original);
 * // copy is a separate object with the same properties
 */
export function deepCopy<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj)) as T;
}
