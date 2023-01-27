(ns rware.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [rware.encrypt :refer [encrypt-test decrypt-file]])
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
  [["-e" "--encrypt PATH" "Encrypts file specified"]
   ["-d" "--decrypt PATH" "Decrypts a file with specified key"]
   ["-k" "--key STRING" "Specifies the key"]])

(defn -main  
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)
        path (if (contains? options :encrypt) (:encrypt options)(:decrypt options))
        key (:key options)]
    (condp apply [options]
      :help (usage summary)
      :encrypt (encrypt-test path key)
      :decrypt (decrypt-file path key)
      (println options))))


