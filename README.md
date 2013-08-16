# s3files

S3 files uploader written on Clojure 

Main requirements:

1. Upload all files from specified directory to s3
2. Upload all files independent of directory size.
3. Guaranty that all files will be uploaded.
4. Concurrent uploading. 

## Installation

Download jar file from http://s3files.droxbob.com

## Usage

To upload on s3 files from some directory:

1. Put config file `config.clj` near .jar file
2. $ java -jar s3files-0.1.0-standalone.jar 

## Config in file config.clj

```
 {
  :access-key "your S3 access key"
  :secret-key "your S3 secret key"
  :bucket     "your buket name"
  :key-prefix "path/prefix/"
  :filter-by-extensions ["jpeg" "png"]
  :work-dir   "/root/files/dir" #if not specified .jar dir userd
  :batch-size 100               #if not specified 20 by default
}
```


## Thanks

[CloudMade](http://cloudmade.com)

## Notes

I'm a newbie in Clojure

## License

Copyright © 2013 Max Shytikov

Distributed under the Eclipse Public License, the same as Clojure.
