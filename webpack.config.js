const path = require('path');
const glob = require('glob');
const webpack = require('webpack');

const ROOT = './src/main/webapp/js';
const BUILD = 'build/exploded-app/js';

const entry = {};
const files = glob.sync(`${ROOT}/**/*.js`, {
    ignore: `${ROOT}/lib/**`    // ignore /lib/
});
files.forEach((file) => {
    const pathObj = path.parse(file);
    const dir = pathObj.dir.replace(ROOT, '');
    const name = pathObj.name;
    const filePath = path.join(dir, name);
    entry[filePath] = file;
});

module.exports = {
    entry,
    output: {
        filename: '[name].js',
        path: path.resolve(__dirname, BUILD)
    },
    stats: 'errors-only'
};