/**
 * Store helper functions for using JSON.
 */

export const replacer = (_key: any, value: any): any => {
  if (value instanceof Map) {
    return {
      dataType: 'Map',
      value: Array.from(value.entries()),
    };
  }
  return value;
};

export const reviver = (_key: any, value: any): any => {
  if (typeof value === 'object' && value !== null) {
    if (value.dataType === 'Map') {
      return new Map(value.value);
    }
  }
  return value;
};
