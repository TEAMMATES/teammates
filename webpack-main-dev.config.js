const merge = require('webpack-merge');
const commonConfig = require('./webpack-main.config.js');

module.exports = merge(commonConfig, {
    devtool: 'cheap-module-eval-source-map',
    mode: 'development',
    optimization: {
        minimize: false,
    },
});
