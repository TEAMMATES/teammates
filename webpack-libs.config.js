const path = require('path');
const fs = require('fs');
const webpack = require('webpack');

const LIBS_JS_FOLDER = path.resolve(__dirname, 'src/main/webapp/dev/js/libs');
const OUTPUT_JS_FOLDER = path.resolve(__dirname, 'src/main/webapp/js');

const entry = {};

fs.readdirSync(LIBS_JS_FOLDER).forEach((fileName) => {
    if (fileName.endsWith('.js')) {
        const fileNameWithoutExt = fileName.replace('.js', '');
        const filesToBundle = [
            `${LIBS_JS_FOLDER}/${fileName}`,
        ];
        entry[fileNameWithoutExt] = filesToBundle;
    }
});

const jquery = {
    test: /(bootbox|bootstrap|guillotine|jquery-highlight|jquery-ui|printthis)(\/|\\).*\.js$/,
    use: 'imports-loader?$=jquery,jQuery=jquery',
};

const css = {
    test: /((bootstrap|guillotine)(\/|\\).*|teammatesCommon)\.css$/,
    use: [
        { loader: 'style-loader' },
        { loader: 'css-loader' },
    ],
};

const glyph = {
    test: /(bootstrap(\/|\\).*\.(eot|svg|ttf|woff)|\.png)$/,
    loader: 'file-loader?publicPath=/js/',
};

const json = {
    test: /datamaps(\/|\\).*\.json$/,
    loader: 'file-loader?name=coordinates.json',
};

module.exports = {
    entry,
    output: {
        filename: '[name].js',
        path: OUTPUT_JS_FOLDER,
    },
    module: {
        rules: [
            jquery,
            css,
            glyph,
            json,
        ],
    },
    stats: 'errors-only',
    plugins: [
        new webpack.optimize.UglifyJsPlugin({
            output: {
                ascii_only: true,
            },
        }),
        new webpack.IgnorePlugin(/^\.(\/|\\)locale$/, /moment$/),
    ],
};
