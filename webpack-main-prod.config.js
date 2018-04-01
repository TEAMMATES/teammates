const merge = require('webpack-merge');
const common = require('./webpack-main.config.js');

module.exports = merge(common, {
    mode: 'production',
    devtool: 'source-map',
    optimization: {
        minimize: true,
    },
});
