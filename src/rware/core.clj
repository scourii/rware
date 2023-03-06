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

(defn handle-command
  [command path key]
  (condp = command
    :encrypt (if (.isDirectory (io/file path))
               (encrypt-dir path key)
               (encrypt-file path key))
    :decrypt (if (.isDirectory (io/file path))
               (decrypt-dir path key)
               (decrypt-file path key))))
(defn -main
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)
        path (or (:encrypt options) (:decrypt options))
        key (:key options)
        command (if (:encrypt options) :encrypt
                    (if (:decrypt options) :decrypt :unknown))]
    (condp apply [options]
      :help (usage summary)
      :unknown (usage summary)
      (handle-command command path key))))
