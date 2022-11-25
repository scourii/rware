(ns rware.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [rware.encrypt :refer [encrypt-test]])
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
  [["-e" "--encrypt" "Encrypts file specified"]
   ["-d" "--decrypt PATH KEY" "Decrypts a file with specified key"]])

(defn -main  
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (condp apply [options]
      :help (usage summary)
      :encrypt (encrypt-test)
      :decrypt (println "hello")
      
      (println options))))
