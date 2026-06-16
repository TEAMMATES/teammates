const defaultWidth = 30;

function createProgressBar({ stream = process.stdout, width = defaultWidth } = {}) {
  function render(completed, total) {
    if (!stream.isTTY || total === 0) {
      return;
    }

    const filledWidth = Math.round((completed / total) * width);
    const emptyWidth = width - filledWidth;
    const progressBar = `${'='.repeat(filledWidth)}${'-'.repeat(emptyWidth)}`;

    stream.write(`\r[${progressBar}] ${completed}/${total}`);
  }

  function clear() {
    if (!stream.isTTY) {
      return;
    }

    stream.write('\r\u001B[2K');
  }

  return { clear, render };
}

module.exports = { createProgressBar };
