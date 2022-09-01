(defproject app "0.2.0-SNAPSHOT"
  :description "egy horoszkópos játék, ami később az astronet.hu-s szövegek igazságtartalmát is fel tudja majd mérni"
  :url "https://github.com/PiciAkk/horoszkop-kiserlet"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.12.3"]
                 [org.clojure/data.json "2.4.0"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]]
  :repl-options {:init-ns app.core}
  :main app.core)
