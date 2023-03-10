(ns rware.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [rware.encrypt :refer [encrypt-file encrypt-dir decrypt-file decrypt-dir]])
  (:gen-class))

(defn- usage
  [options]
  (->> ["Usage: rware [options]"
        ""
        "Options:"
        options]
       (str/join \newline)
       (println)))

(def cli-options
  [["-h" "--help" "Prints help information"]
   ["-e" "--encrypt PATH" "Encrypts file specified"]
   ["-d" "--decrypt PATH" "Decrypts a file with specified key"]
   ["-k" "--key STRING" "Specifies the key"]])

(defmulti handle-command
  (fn [command path key] (if (.isDirectory (io/file path)) :dir :file)))

(defmethod handle-command :file
  [command path key]
  (case command
    :encrypt (encrypt-file path key)
    :decrypt (decrypt-file path key)))

(defmethod handle-command :dir
  [command path key]
  (case command
    :encrypt (encrypt-dir path key)
    :decrypt (decrypt-file path key)))

(defn -main
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)
        path (or (:encrypt options) (:decrypt options))
        key (:key options)
        command (cond
                  (:encrypt options) :encrypt
                  (:decrypt options) :decrypt
                  :else :unknown)]
    (condp apply [options]
      :help (usage summary)
      :nil (usage summary)
      (handle-command command path key))))
