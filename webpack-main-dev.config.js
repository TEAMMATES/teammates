const merge = require('webpack-merge');
const common = require('./webpack-main.config.js');

module.exports = merge(common, {
    devtool: 'cheap-module-eval-source-map',
    mode: 'development',
    optimization: {
        minimize: false,
    },
});