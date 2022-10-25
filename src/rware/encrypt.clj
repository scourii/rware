(ns rware.encrypt
  (:require [tinklj.config :refer [register]]
            [tinklj.keys.keyset-handle :as keyset-handles]
            [clojure.java.io :refer [file]]))

(def create-key
  (keyset-handles/generate-new :aes128-gcm))

(defn encrypt-files
  [path]
  )