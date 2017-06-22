const path = require('path');
const fs = require('fs');

const TEST_JS_FOLDER = path.resolve(__dirname, 'src/main/webapp/dev/js/test');
const COMMON_JS_FOLDER = path.resolve(__dirname, 'src/main/webapp/dev/js/common');
const OUTPUT_JS_FOLDER = path.resolve(__dirname, 'src/main/webapp/test');

const entry = {};
entry.tableSort = [`${COMMON_JS_FOLDER}/onStart.js`];
entry.jsUnitTests = fs.readdirSync(TEST_JS_FOLDER)
        .filter(fileName => fileName.endsWith('.js') && !fileName.startsWith('libs'))
        .map(fileName => `${TEST_JS_FOLDER}/${fileName}`);
entry['libs-qunit'] = [`${TEST_JS_FOLDER}/libs-qunit.js`];

const babel = {
    test: /\.js$/,
    exclude: /(node_modules|bower_components)/,
    use: {
        loader: 'babel-loader',
        options: {
            presets: ['env'],
            cacheDirectory: true,
        },
    },
};

const css = {
    test: /(qunitjs)(\/|\\).*\.css$/,
    use: [
        { loader: 'style-loader' },
        { loader: 'css-loader' },
    ],
};

module.exports = {
    entry,
    output: {
        filename: '[name].js',
        path: OUTPUT_JS_FOLDER,
    },
    module: {
        rules: [
            babel,
            css,
        ],
    },
    stats: 'errors-only',
};
