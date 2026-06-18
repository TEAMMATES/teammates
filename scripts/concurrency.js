const os = require('node:os');

class Concurrency {
  constructor(limit = Concurrency.defaultLimit()) {
    this.limit = Math.max(1, limit);
  }

  static defaultLimit(maxLimit = 8) {
    return Math.max(1, Math.min(os.availableParallelism?.() ?? os.cpus().length, maxLimit));
  }

  async map(items, mapper) {
    const results = new Array(items.length);
    let nextIndex = 0;

    const workers = Array.from({ length: Math.min(this.limit, items.length) }, async () => {
      while (nextIndex < items.length) {
        const currentIndex = nextIndex;
        nextIndex += 1;
        results[currentIndex] = await mapper(items[currentIndex], currentIndex);
      }
    });

    await Promise.all(workers);
    return results;
  }
}

module.exports = { Concurrency };
