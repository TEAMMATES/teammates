/**
 * Store helper functions for using JSON.
 */

export const mapReplacer = (_key: any, value: any): any => {
  if (value instanceof Map) {
    return {
      dataType: 'Map',
      value: Array.from(value.entries()),
    };
  }
  return value;
};

export const mapReviver = (_key: any, value: any): any => {
  if (typeof value === 'object' && value !== null) {
    if (value.dataType === 'Map') {
      return new Map(value.value);
    }
  }
  return value;
};
