(ns app.core
  (:require [clj-http.client :as client]
            [clojure.string :as s])
  (:gen-class :main true))

(def csillagjegyek
  (s/split
   "kos,bika,ikrek,rák,oroszlán,szűz,mérleg,skorpió,nyilas,bak,vízöntő,halak"
   #","))

(def honapok
  (s/split
   "január,február,március,április,május,június,július,augusztus,szeptember,október,november,december"
   #","))

(defn formazas
  "horoszkopok formaza egy kis Java-s magic-kel:tm:"
  [string]
  (-> (java.text.Normalizer/normalize string java.text.Normalizer$Form/NFD)
      (clojure.string/replace #"\p{InCombiningDiacriticalMarks}+" "")
      (s/lower-case)))

(def horoszkop-lekerese
  "horoszkopos szoveg lekerese az astronet.hu-rol"
  (memoize
   (fn [csillagjegy & [datum]]
     (let [url (str "https://www.astronet.hu/horoszkop/"
                    (formazas csillagjegy)
                    "-napi-horoszkop/"
                    ((fnil formazas "") datum))

           html (:body (client/get url))

           start (+ (s/index-of html "details-content")
                    17)

           end (+ (s/index-of (apply str (drop start html)) "</div>")
                  start)]

       ((comp s/trim s/trim-newline)
        (subs html start end))))))



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
  "minden csillagjegy lecserélése egy megadott csillagjegyre egy szovegben"
  [erre szoveg]
  (s/replace szoveg
             (re-pattern
              (str "("
                   (s/join "|" (map s/capitalize csillagjegyek))
                   ")"))
             (s/capitalize erre)))


(defn -main []
  (let [csillagjegy (do (println "a csillagjegyed: ")
                        (read-line))

        szovegek (shuffle (conj (map (partial csillagjegyek-lecserelese csillagjegy)
                                     (repeatedly 4 random-horoszkop))
                                (horoszkop-lekerese csillagjegy)))

        tipp (do (dotimes [x (count szovegek)]
                   (println (str (inc x) ". " (nth szovegek x) "\n"))) 
                 (println "a fentiek közül melyik írja le legjobban a napodat?")
                 (-> (read-line)
                     (s/replace #"\." "")
                     (Integer/parseInt)))

        tenyleges (->> (horoszkop-lekerese csillagjegy)
                       (.indexOf szovegek)
                       (inc))]
    
    (println (str "\n"
                  "a napodat legjobban leíró horoszkóp: " tipp "\n"
                  "mai horoszkópod: " tenyleges "\n"
                  (if (= tipp tenyleges)
                    "egyezik!"
                    "nem egyezik!")))))
