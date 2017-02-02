const path = require('path');
const glob = require("glob");
const webpack = require('webpack');

const entry = {};
glob.sync("./src/main/webapp/js/*.js").forEach((file) => {
    const filename = path.basename(file, '.js');
    entry[filename] = file
});

module.exports = {
    entry,
    output: {
        filename: '[name].js',
        path: path.resolve(__dirname, 'build/exploded-app/js/')
    },
    plugins: [
        new webpack.optimize.UglifyJsPlugin()
    ]
};