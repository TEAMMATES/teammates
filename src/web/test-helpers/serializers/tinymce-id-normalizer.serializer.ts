import type { SnapshotSerializer } from 'vitest';

const TINYMCE_ID_PREFIX = 'tiny-angular_';
const NORMALIZED_ID_PREFIX = 'tinymce-editor-id-';

function walkElements(root: Element, visitor: (element: Element) => void): void {
  const stack: Element[] = [root];
  while (stack.length > 0) {
    const element = stack.pop()!;
    visitor(element);
    stack.push(...Array.from(element.children));
  }
}

const serializer: SnapshotSerializer = {
  test: (val): boolean => {
    if (typeof Element === 'undefined' || !(val instanceof Element)) {
      return false;
    }

    let foundTinyMceId = false;
    walkElements(val, (element) => {
      if (element.id.startsWith(TINYMCE_ID_PREFIX)) {
        foundTinyMceId = true;
      }
    });
    return foundTinyMceId;
  },
  serialize: (val, config, indentation, depth, refs, printer): string => {
    const normalized = (val as Element).cloneNode(true) as Element;
    const idMap = new Map<string, string>();
    let nextId = 1;

    walkElements(normalized, (element) => {
      if (!element.id.startsWith(TINYMCE_ID_PREFIX)) {
        return;
      }
      if (!idMap.has(element.id)) {
        idMap.set(element.id, `${NORMALIZED_ID_PREFIX}${nextId}`);
        nextId += 1;
      }
      element.id = idMap.get(element.id)!;
    });

    return printer(normalized, config, indentation, depth, refs);
  },
};

export default serializer;
