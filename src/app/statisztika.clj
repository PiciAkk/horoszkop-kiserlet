(ns app.statisztika
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(def statisztika-fajl
  (io/resource
   "statisztikak.edn"))

(defn statisztikak []
  (with-open [r (io/reader statisztika-fajl)]
    (edn/read (java.io.PushbackReader. r))))

(defn statisztika-hozzaadasa [statisztika]
  (spit statisztika-fajl
        (conj (statisztikak)
              statisztika)))