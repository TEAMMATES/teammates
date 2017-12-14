# eslint-plugin-json

[![Build Status](https://travis-ci.org/azeemba/eslint-plugin-json.svg)](https://travis-ci.org/azeemba/eslint-plugin-json) [![Code Climate](https://codeclimate.com/github/azeemba/eslint-plugin-json/badges/gpa.svg)](https://codeclimate.com/github/azeemba/eslint-plugin-json)

Lint JSON files

## Installation

You'll first need to install [ESLint](http://eslint.org):

```
$ npm i eslint --save-dev
```

Next, install `eslint-plugin-json`:

```
$ npm install eslint-plugin-json --save-dev
```

**Note:** If you installed ESLint globally (using the `-g` flag) then you must also install `eslint-plugin-json` globally.

## Usage

Add `json` to the plugins section of your `.eslintrc` configuration file. You can omit the `eslint-plugin-` prefix:

```json
{
    "plugins": [
        "json"
    ]
}
```

You can run ESLint on individual JSON files or you can use the `--ext` flag to add JSON files to the list.

```
eslint --ext .json --ext .js
eslint example.json
```





