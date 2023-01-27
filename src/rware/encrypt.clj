(ns rware.encrypt
  (:require [rware.bytes :refer [bytes->b64]]
            [lock-key.core :as lo])
  (:import [javax.crypto Cipher KeyGenerator SecretKey]
           [javax.crypto.spec SecretKeySpec]
           [java.security SecureRandom]))

(defn generate-key
  [crypto-key]
  (let [instance (KeyGenerator/getInstance "AES")
        sr (SecureRandom/getInstance "SHA1PRNG")]
    (.setSeed sr (.getBytes crypto-key "UTF-8"))
    (.init instance 128 sr)
    (.. instance generateKey getEncoded)))

(defn generate-cipher
  [mode crypto-key]
  (let [key-spec (SecretKeySpec. (generate-key crypto-key) "AES")
        cipher (Cipher/getInstance "AES")]
    (.init cipher mode key-spec)
    cipher))

(defn encrypt-test 
  [path crypto-key]
  (let [plain-text (.getBytes (slurp path) "UTF-8")
         cipher (generate-cipher Cipher/ENCRYPT_MODE crypto-key)]
    (println (bytes->b64 (.doFinal cipher plain-text)))
    (spit path (String. plain-text))))

(defn decrypt-file
  [path key]
  (let [file-contents (slurp path)]
        
    (String. (.doFinal key (bytes->b64 file-contents)))))

;; Seq should match now
