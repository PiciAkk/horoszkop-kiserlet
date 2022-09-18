(ns app.core
  (:require [clojure.data.json :as json] 
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :refer [GET routes]]
            [app.horoszkop :as h] 
            [app.statisztika :as stat]
            #_[ring.util.response :as response])
  (:gen-class :main true))

(defn -main []
  (run-jetty
   (wrap-resource
    (routes
     (GET "/csillagjegyek" []
       (json/write-str h/csillagjegyek))

     (GET "/horoszkopok-generalasa/:csillagjegy" [csillagjegy]
       (json/write-str (h/horoszkopok-generalasa csillagjegy)))

     (GET "/statisztika-hozzaadasa/:valasztott/:tenyleges/:egyezo" [valasztott tenyleges egyezo]
       (stat/statisztika-hozzaadasa
        {:valasztott valasztott
         :tenyleges tenyleges
         :egyezo? (Boolean/parseBoolean egyezo)})
       "statisztika sikeresen hozzáadva!"))

    "public")

   {:port 3000
    :join? false}))

;; (slurp (io/resource "statistics.json"))
;; {:legjobban-leiro (osszegzes tipp szovegek)
;;    :tenyleges (osszegzes tenyleges szovegek)}