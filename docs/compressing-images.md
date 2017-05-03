# Compressing Images

This document describes the steps to compress an image to be used in the TEAMMATES website.

Two choices are provided, (i) online service (ii) command-line. Use the command line if there are many images to compress.

## Online service

1. If the image is not cropped, you may want to use an image editing tool such as preview(mac), photos(windows), or use a service like [picresize](http://www.picresize.com/).
    > For a profile picture to be used in `/about.jsp` page, the maximum width is 300px. Other images' sizes are up to your discretion.

2. Use an image compression service like [kraken](https://kraken.io/web-interface) to compress the image. Remember to select lossless compression.

## Command-line

### Installation

In order to execute the commands below, you would need to install the following:

- [ImageMagick](https://www.imagemagick.org/script/download.php)
- [optipng](http://optipng.sourceforge.net/)
- [gifsicle](https://www.lcdf.org/gifsicle/)

Or, if you are on the Mac, and you use Homebrew, you can use this instead:

```sh
brew update && brew install imagemagick optipng gifsicle
```

### Compressing

The commands below assume you are already in the directory where the image belongs.

#### Entire folder
| Program     | Command                               | File Type | Purpose       |
|-------------|---------------------------------------|-----------|---------------|
| ImageMagick | `mogrify -resize 300 *.png`           | .png      | resize pngs   |
| Optipng     | `optipng *.png -o7 -quiet -strip all` | .png      | compress pngs |
| Gifsicle    | `gifsicle --batch -O3 *.gif`          | .gif      | compress gifs |

#### Single image
| Program     | Command                                         | File Type | Purpose      |
|-------------|-------------------------------------------------|-----------|--------------|
| ImageMagick | `convert -resize 300 [filename].png`            | .png      | resize png   |
| Optipng     | `optipng [filename].png -o7 -quiet -strip all`  | .png      | compress png |
| Gifsicle    | `gifsicle -O3 [filename].gif -o [filename].gif` | .gif      | compress gif |
