/**
 * Creates a spy object for a given class, providing spy functions for all of its methods.
 *
 * @param Class - The class for which to create the spy
 * @returns A spy object with spy functions for each method of the provided class.
 * @example
 * class MyClass {
 *   method1() {}
 *   method2() {}
 * }
 *
 * createSpyFromClass(MyClass) returns { method1: jest.fn(), method2: jest.fn() }
 */
export default function createSpyFromClass<T>(Class: new (...args: any[]) => T): Spy<T> {
  const spy = {} as Spy<T>;

  Object.getOwnPropertyNames(Class.prototype).forEach((methodName) => {
    if (typeof Class.prototype[methodName] === 'function') {
      spy[methodName as keyof T] = jest.fn();
    }
  });

  return spy;
}

// Helper type for better typing
type Spy<T> = {
  [K in keyof T]: jest.Mock<any, any>;
};
