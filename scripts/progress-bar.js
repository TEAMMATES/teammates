const defaultWidth = 30;

class ProgressBar {
  constructor({ stream = process.stdout, width = defaultWidth } = {}) {
    this.stream = stream;
    this.width = width;
  }

  render(completed, total) {
    const { stream, width } = this;

    if (!stream.isTTY || total === 0) {
      return;
    }

    const filledWidth = Math.round((completed / total) * width);
    const emptyWidth = width - filledWidth;
    const progressBar = `${'='.repeat(filledWidth)}${'-'.repeat(emptyWidth)}`;

    stream.write(`\r[${progressBar}] ${completed}/${total}`);
  }

  clear() {
    const { stream } = this;

    if (!stream.isTTY) {
      return;
    }

    stream.write('\r\u001B[2K');
  }
}

module.exports = { ProgressBar };
