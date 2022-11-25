(ns rware.encrypt
  (:require [tinklj.encryption.aead :refer [encrypt]]
            [tinklj.primitives :as primitives]
            [tinklj.config :refer [register]]
            [tinklj.keys.keyset-handle :as keyset-handles]
            [tinklj.encryption.aead :as sut]
            [clojure.java.io :refer [file]]
            [clojure.test :refer [is]]))

(register :aead)

(defn encrypt-test []
  (let [plain-text (slurp "src/rware/textfile.txt")
        keyset-handle (keyset-handles/generate-new :aes128-gcm)
        primitive (primitives/aead keyset-handle)
        aad (.getBytes "Salt")
        encrypted (sut/encrypt primitive
                               (.getBytes plain-text)
                               aad)
        decrypted (sut/decrypt primitive
                               encrypted
                               aad)]
    (println "Plain-text:" plain-text)
    (println "Encrypted:"(String. encrypted))))
