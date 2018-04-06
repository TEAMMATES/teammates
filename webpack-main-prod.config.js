const merge = require('webpack-merge');
const commonConfig = require('./webpack-main.config.js');

module.exports = merge(commonConfig, {
    mode: 'production',
    devtool: 'source-map',
    optimization: {
        minimize: true,
    },
});
