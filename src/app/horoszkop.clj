(ns app.horoszkop
  (:require [clojure.string :as s]
            [clj-http.client :as client]))

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
      (s/replace #"\p{InCombiningDiacriticalMarks}+" "")
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
  "minden csillagjegy lecserelese egy megadott
   csillagjegyre egy szovegben"
  [erre szoveg]
  (let [csillagjegy-regex (->> csillagjegyek
                               (map s/capitalize)
                               (s/join "|")
                               (re-pattern))]

    (as-> szoveg $
      (s/replace $ csillagjegy-regex (s/capitalize erre))
      (s/split $ #" ")
      (conj (mapv (fn [szo kovetkezo-szo]
                    (if (and (#{"a" "az"} szo)
                             (s/starts-with?
                              kovetkezo-szo
                              (s/capitalize erre)))
                      (if ((set "bcdfghjklmnprstvz")
                           (Character/toLowerCase
                            (first kovetkezo-szo)))
                        "a"
                        "az")
                      szo))
                  $
                  (rest $))
            (last $))
      (s/join " " $))))

(comment
  (csillagjegyek-lecserelese "mérleg" "Ma az Oroszlánnak idk.")
  )

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