import 'qunitjs/qunit/qunit.css';

window.QUnit = require('qunitjs');

require.context(
        'file-loader?name=libs-blanket.js&context=node_modules/blanket!blanket/dist/qunit',
        true,
        /\.min\.js$/
);
