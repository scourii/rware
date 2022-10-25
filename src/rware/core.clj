(ns rware.core
  (:require [tinklj.config :refer [register]]
            [tinklj.keys.keyset-handle :as keyset]
            [rware.encrypt :refer [create-key]])
  (:gen-class))

  (register)

;; 1. Generate the private key material.
(def private-keyset-handle (keyset/generate-new :ecies-p256-hkdf-hmac-sha256-aes128-gcm))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (register)
  (println create-key)
  (println "Hello, World!"))
