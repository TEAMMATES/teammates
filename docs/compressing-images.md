# TEAMMATES Compressing Images

This document describes the steps to compress an image to be used in the TEAMMATES website.

## Installation

In order to start the commands below, you would need to install the follow:

- [ImageMagick](https://www.imagemagick.org/script/download.php)
- [optipng](http://optipng.sourceforge.net/)
- [gifsicle](https://www.lcdf.org/gifsicle/)

Or, if you are on the Mac, and you use Homebrew, you can do the following:

```sh
brew update && brew install imagemagick optipng gifsicle
```

## Compressing

Once you are done, you are ready to begin. 

The commands below assume you are already in the directory where the image belongs.

### Entire folder
| Program     | Command                               | File Type | Purpose       |
|-------------|---------------------------------------|-----------|---------------|
| ImageMagick | `mogrify -resize 300 *.png`           | .png      | resize pngs   |
| Optipng     | `optipng *.png -o7 -quiet -strip all` | .png      | compress pngs |
| Gifsicle    | `gifsicle --batch -O3 *.gif`          | .gif      | compress gifs |

### Single image
| Program     | Command                                         | File Type | Purpose      |
|-------------|-------------------------------------------------|-----------|--------------|
| ImageMagick | `convert -resize 300 [filename].png`            | .png      | resize png   |
| Optipng     | `optipng [filename].png -o7 -quiet -strip all`  | .png      | compress png |
| Gifsicle    | `gifsicle -O3 [filename].gif -o [filename].gif` | .gif      | compress gif |
