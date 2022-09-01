(ns app.core
  (:require [clj-http.client :as client]
            [clojure.string :as s]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [ring.adapter.jetty :refer [run-jetty]]
            #_[ring.util.response :as response])
  (:gen-class :main true))

(def statisztika-fajl
  (io/resource "statistics.json"))

(def csillagjegyek
  (s/split
   "kos,bika,ikrek,rák,oroszlán,szűz,mérleg,skorpió,nyilas,bak,vízöntő,halak"
   #","))

(def honapok
  (s/split
   "január,február,március,április,május,június,július,augusztus,szeptember,október,november,december"
   #","))

(defn formazas
  "horoszkopok formazasa egy kis Java-s magic-kel:tm:"
  [string]
  (-> (java.text.Normalizer/normalize string java.text.Normalizer$Form/NFD)
      (clojure.string/replace #"\p{InCombiningDiacriticalMarks}+" "")
      (s/lower-case)))

(defn horoszkop-lekerese
  "horoszkopos szoveg lekerese az astronet.hu-rol"
  [csillagjegy & [datum]]
  (let [url (str "https://www.astronet.hu/horoszkop/"
                 (formazas csillagjegy)
                 "-napi-horoszkop/"
                 ((fnil formazas "") datum))

        html (:body (client/get url))

        start (+ (s/index-of html "details-content")
                 17)

        end (+ (s/index-of (apply str (drop start html)) "</div>")
               start)]

    {:horoszkop ((comp s/trim s/trim-newline)
                 (subs html start end))
     :csillagjegy csillagjegy}))

(defn random-horoszkop
  "random horoszkop generalasa"
  []
  (let [evek (range 2015 2022)

        napok (map (fn [nap]
                     (-> (format "%2s" (str nap))
                         (s/replace #" " "0")))
                   (range 1 29))

        [ev honap nap csillagjegy] (map rand-nth
                                        [evek honapok napok csillagjegyek])]

    (horoszkop-lekerese csillagjegy
                        (s/join "-" [ev honap nap]))))

(defn csillagjegyek-lecserelese
  "minden csillagjegy lecserelese egy megadott csillagjegyre egy szovegben"
  [erre szoveg]
  (s/replace szoveg
             (re-pattern
              (str "("
                   (s/join "|" (map s/capitalize csillagjegyek))
                   ")"))
             (s/capitalize erre)))

(defn osszegzes
  "egy horoszkopot osszegzo map generalasa"
  [sorszam szovegek]
  (let [szoveg (nth szovegek (dec sorszam))]
    
    {:sorszam sorszam
     :csillagjegy (:csillagjegy szoveg)
     :horoszkop (:horoszkop szoveg)}))

(defn statisztika-hozzaadasa
  "uj statisztika 'felirasa'"
  [statisztika]
  (let [eddigi-statisztikak (json/read-str (slurp statisztika-fajl))

        uj-statisztikak (conj eddigi-statisztikak statisztika)]

    (spit statisztika-fajl (json/write-str uj-statisztikak))))

#_(defn -main []
    (let [csillagjegy (do (println "a csillagjegyed: ")
                          (read-line))

          szovegek (shuffle (conj (repeatedly 4 random-horoszkop)
                                  (horoszkop-lekerese csillagjegy)))

          tipp (do (dotimes [x (count szovegek)]
                     (println (str (inc x) ". "
                                   (csillagjegyek-lecserelese csillagjegy
                                                              (:horoszkop
                                                               (nth szovegek x)))
                                   "\n")))
                   (println "a fentiek közül melyik írja le legjobban a napodat?")
                   (-> (read-line)
                       (s/replace #"\." "")
                       (Integer/parseInt)))

          tenyleges (->> (horoszkop-lekerese csillagjegy)
                         (.indexOf szovegek)
                         (inc))]

      (println (str "\n"
                    "a napodat legjobban leíró horoszkóp: " tipp "\n"
                    "mai horoszkópod: " tenyleges))

      (statisztika-hozzaadasa {:legjobban-leiro (osszegzes tipp szovegek)
                               :tenyleges (osszegzes tenyleges szovegek)})))

(defn horoszkopok-generalasa
  "random horoszkopok generalasa random sorrendben,
   amelyek kozul az egyik a megadott csillagjegyhez 
   (es jelenlegi naphoz) tartozo tenyleges horoszkop"
  [csillagjegy]
  (let [random-horoszkopok (repeatedly
                            4
                            (fn []
                              (-> (random-horoszkop)
                                  (update :horoszkop
                                          (partial csillagjegyek-lecserelese
                                                   csillagjegy)))))

        tenyleges-horoszkop (horoszkop-lekerese csillagjegy)

        horoszkopok (shuffle (cons tenyleges-horoszkop random-horoszkopok))

        tenyleges-horoszkop-index (.indexOf horoszkopok tenyleges-horoszkop)]

    {:tenyleges tenyleges-horoszkop-index
     :horoszkopok horoszkopok}))

(defn -main []
  (run-jetty
   (fn [{uri :uri query :query-string}]
     {:status 200
      :headers {"Content-Type" "text/plain"
                "Access-Control-Allow-Origin" "*"}
      :body (json/write-str
             (case uri
               "/csillagjegyek" csillagjegyek
               "/horoszkopok-generalasa" (horoszkopok-generalasa
                                          (.getPath
                                           (java.net.URI.
                                            (second
                                             (s/split query #"=")))))
               "clojure ftw!"))})
   {:port 3000}))

;; (slurp (io/resource "statistics.json"))
;; {:legjobban-leiro (osszegzes tipp szovegek)
;;    :tenyleges (osszegzes tenyleges szovegek)}