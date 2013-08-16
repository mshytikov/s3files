(defproject s3files "0.1.0-SNAPSHOT"
  :description "S3 files uploader"
  :url "https://github.com/mshytikov/s3files"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-aws-s3 "0.3.7"]
                 [org.clojure/tools.logging "0.2.6"]
                 [log4j/log4j "1.2.16"]
                 [pandect "0.3.0"]
                 [com.novemberain/pantomime "2.0.0"]
                 [commons-io/commons-io "2.2"]]

  :main s3files.core)
