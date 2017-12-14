'use strict';

var fs = require('fs');
var path = require('path');
var _ = require('lodash');
var missingSupport = require('./missing-support');
var Detector = require('./detect-feature-use');
var Multimatch = require('multimatch');

function browserslist() {
  var dirs = path.resolve('.').split(path.sep);
  var config;
  var content;

  while (dirs.length) {
    config = dirs.concat(['browserslist']).join(path.sep);

    if (fs.existsSync(config) && fs.statSync(config).isFile()) {
      content = fs.readFileSync(config, { encoding: 'utf8' });

      return content.split(/\r?\n/).join(', ');
    }

    dirs = dirs.slice(0, -1);
  }
}

function doiuse(options) {
  var browserQuery = options.browsers;
  var onFeatureUsage = options.onFeatureUsage;
  var ignoreOptions = options.ignore;
  var ignoreFiles = options.ignoreFiles;

  if (!browserQuery) {
    browserQuery = browserslist();

    if (!browserQuery) {
      browserQuery = doiuse['default'].slice();
    }
  }

  var _missingSupport = missingSupport(browserQuery);

  var browsers = _missingSupport.browsers;
  var features = _missingSupport.features;

  var detector = new Detector(_.keys(features));

  return {
    info: function info() {
      return {
        browsers: browsers,
        features: features
      };
    },

    postcss: function postcss(css, result) {
      return detector.process(css, function (_ref) {
        var feature = _ref.feature;
        var usage = _ref.usage;
        var ignore = _ref.ignore;

        if (ignore && ignore.indexOf(feature) !== -1) {
          return;
        }
        if (ignoreOptions && ignoreOptions.indexOf(feature) !== -1) {
          return;
        }

        if (ignoreFiles && Multimatch(usage.source.input.from, ignoreFiles).length > 0) {
          return;
        }

        var messages = [];
        if (features[feature].missing) {
          messages.push('not supported by: ' + features[feature].missing);
        }
        if (features[feature].partial) {
          messages.push('only partially supported by: ' + features[feature].partial);
        }

        var message = features[feature].title + ' ' + messages.join(' and ') + ' (' + feature + ')';

        result.warn(message, { node: usage, plugin: 'doiuse' });

        if (onFeatureUsage) {
          var loc = usage.source;
          loc.original = css.source.input.map ? {
            start: css.source.input.map.consumer().originalPositionFor(loc.start),
            end: css.source.input.map.consumer().originalPositionFor(loc.end)
          } : {
            start: loc.start,
            end: loc.end
          };

          message = (loc.original.start.source || loc.input.file || loc.input.from) + ':' + loc.original.start.line + ':' + loc.original.start.column + ': ' + message;

          onFeatureUsage({
            feature: feature,
            featureData: features[feature],
            usage: usage,
            message: message
          });
        }
      });
    }
  };
}
doiuse['default'] = ['> 1%', 'last 2 versions', 'Firefox ESR', 'Opera 12.1'];
module.exports = doiuse;