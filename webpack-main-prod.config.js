const merge = require('webpack-merge');
const common = require('./webpack-main.config.js');

module.exports = merge(common, {
    mode: 'production',
    optimization: {
        minimize: true,
    },
});
