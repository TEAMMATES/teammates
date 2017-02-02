var path = require('path');

module.exports = {
  entry: path.resolve(__dirname, 'src/main/webapp/js/index.js'),
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'build/exploded-app/js/')
  }
};