(ns rware.encrypt
  (:require [tinklj.primitives :as primitives]
            [tinklj.config :refer [register]]
            [tinklj.keys.keyset-handle :as keyset-handles]
            [tinklj.keysets.keyset-storage :as keyset-storage]
            [tinklj.encryption.aead :as sut]
            [buddy.core.nonce :as nonce]
            [clojure.java.io :refer [file]]
            [clojure.test :refer [is]]))

(register :aead)

(defn encrypt-test 
  [path]
  (let [plain-text (slurp path)
        keyset-handle (keyset-handles/generate-new :aes128-gcm)
        primitive (primitives/aead keyset-handle)
        aad (.getBytes "Salt")
        encrypted (sut/encrypt primitive
                               (.getBytes plain-text)
                               aad)]
    (keyset-storage/write-clear-text-keyset-handle keyset-handle "Key")
    (spit path (String. encrypted))))

(defn decrypt-file
  [path key]
  (let [file-contents (.getBytes (slurp path))
        keyset-handle (keyset-storage/load-clear-text-keyset-handle key)
        primitive (primitives/aead keyset-handle)
        aad (.getBytes "Salt")
        decrypted (sut/decrypt primitive
                               file-contents
                               aad)]
  (println file-contents)))
