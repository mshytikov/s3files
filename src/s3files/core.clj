(ns s3files.core
  (:require [aws.sdk.s3 :as s3])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [clojure.tools.logging :as log])
  (:require [pantomime.mime :refer [mime-type-of]])
  (:require [pandect.core :refer [md5]])
  (:import  [org.apache.commons.io FilenameUtils FileUtils])
  (:gen-class))

(def cwd (System/getProperty "user.dir"))

(def config (read-string (slurp (str cwd "/config.clj"))))

(def workdir (config :work-dir cwd))

(def key-prefix (config :key-prefix ""))

(def batch-size (config :batch-size 20))

(def filter-by-extensions (config :filter-by-extensions))

(defn file-ext [f]
  (-> f str FilenameUtils/getExtension .toLowerCase))

(defn upload-file? [f]
  (cond 
    (.isDirectory f) false
    (seq filter-by-extensions) (some #{(file-ext f)} filter-by-extensions)
    :else true))

(defn relative-path [f]
  (string/replace-first (.getPath f) (str workdir "/") ""))

(defn metadata [f]
  {:content-type (mime-type-of f)
   :content-md5  (md5 f)})

(defn upload [f]
  (let [key-postfix (relative-path f)
        cred  (select-keys config [:access-key :secret-key])
        s3key (str key-prefix key-postfix)
        bucket (config :bucket)]
    (log/debug (str "Uploading: " f " => " s3key))
    (s3/put-object cred bucket s3key f (metadata f))
    (log/info (str "Processed: " key-postfix))
    :processed))

(defn start[]
  (let [ls (file-seq (io/file workdir))
        fs (filter upload-file? ls)]
    (map #(do (send-off % upload) %) (map agent fs))))

(defn works? [a]
  (not (= :processed @a)))

(defn restart-failed-agents [agents]
  (doseq [a (filter agent-error agents)]
    (log/error (agent-error a) (str "error when uploading: " @a))
    (log/debug (str "Restarting: " @a))
    (restart-agent a @a)
    (send-off a upload)))

(defn work [media-agent]
  (loop [agents media-agent]
    (let [active-agents (take  batch-size agents)
          waiting-agents (drop batch-size agents)]
      (restart-failed-agents active-agents)
      (Thread/sleep 1000)
      (let [working-agents (filter works? active-agents)
            rest-agents (concat working-agents waiting-agents)]
        (if (seq rest-agents) (recur rest-agents))))))


(defn -main []
  (log/info "Welcome!")
  (time (work (start)))
  (shutdown-agents)
  (log/info "Have a nice day!"))
