(ns app.core
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :refer [GET routes]]
            [app.horoszkop :as h]
            #_[ring.util.response :as response])
  (:gen-class :main true))

(def statisztika-fajl
  (io/resource "statistics.json"))

(defn -main []
  (run-jetty
   (wrap-resource
    (routes
     (GET "/csillagjegyek" []
       (json/write-str h/csillagjegyek))

     (GET "/horoszkopok-generalasa/:csillagjegy" [csillagjegy]
       (json/write-str (h/horoszkopok-generalasa csillagjegy))))
     
    "public")
   
   {:port 3000
    :join? false}))

;; (slurp (io/resource "statistics.json"))
;; {:legjobban-leiro (osszegzes tipp szovegek)
;;    :tenyleges (osszegzes tenyleges szovegek)}