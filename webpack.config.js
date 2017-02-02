const path = require('path');

const UglifyJSPlugin = require('uglifyjs-webpack-plugin');

module.exports = {
    entry: path.resolve(__dirname, 'src/main/webapp/js/index.js'),
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'build/exploded-app/js/')
    },
    plugins: [
        new UglifyJSPlugin()
    ]
};