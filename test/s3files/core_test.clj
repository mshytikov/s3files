(ns s3files.core-test
  (:require [clojure.java.io :as io])
  (:use clojure.test
        s3files.core))

(deftest test-file-ext
         (is (= "gz" (file-ext (io/file "/tmp/1.tar.gz"))))
         (is (= "png" (file-ext (io/file "/tmp/1.png"))))
         (is (= "" (file-ext (io/file "/tmp/1")))))

(deftest test-upload-file?
         (with-redefs [filter-by-extensions nil]
                      (is (upload-file? (io/file "/tmp/1")))
                      (is (upload-file? (io/file "/tmp/1")))
                      (is (upload-file? (io/file "/tmp/1.png")))
                      (is (not (upload-file? (io/file ".")))))
         (with-redefs [filter-by-extensions []]
                      (is (upload-file? (io/file "/tmp/1")))
                      (is (upload-file? (io/file "/tmp/1.png"))))
         (with-redefs [filter-by-extensions ["png" "jpg"]]
                      (is (not (upload-file? (io/file "/tmp/1"))))
                      (is (not (upload-file? (io/file "/tmp/1.bmp"))))
                      (is (upload-file? (io/file "/tmp/1.png")))
                      (is (upload-file? (io/file "/tmp/1.jpg")))))

(deftest test-relative-path
         (with-redefs [workdir "/tmp"]
                      (is (= "1/test/1.png" (relative-path (io/file "/tmp/1/test/1.png"))))
                      (is (= "1.png"        (relative-path (io/file "/tmp/1.png"))))
                      (is (= "a/b/c/1"      (relative-path (io/file "/tmp/a/b/c/1"))))))


(deftest test-metadata
         (let [data (metadata (io/file "test/fixtures/1px.gif"))]
           (is (= 2 (count data)))
           (is (= "image/gif" (:content-type data)))
           (is (= "ad4b0f606e0f8465bc4c4c170b37e1a3" (:content-md5 data)))))
