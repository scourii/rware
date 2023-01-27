(ns rware.bytes
  (:import (java.util Base64)))

(defn bytes->b64 
  [^bytes bytes] 
  (String. (.encode (Base64/getEncoder) bytes)))

(defn b64->bytes
  [^String str]
  (.decode (Base64/getDecoder) (.getBytes str)))
