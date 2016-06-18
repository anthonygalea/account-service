(defproject account-service "0.1.0-SNAPSHOT"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.datomic/datomic-free "0.9.5372"]
                 [clj-time "0.12.0"]
                 [expectations "2.1.8"]
                 [metosin/compojure-api "1.1.2"]]
  :plugins [[lein-datomic "0.2.0"]]
  :ring {:handler account-service.core/app}
  :datomic {:schemas ["resources" ["schema.dtm"]]}
  :profiles {:dev
             {:plugins      [[lein-ring "0.9.7"]
                             [lein-midje "3.2"]]
              :dependencies [[javax.servlet/servlet-api "2.5"]
                             [cheshire "5.6.1"]
                             [ring/ring-mock "0.3.0"]
                             [midje "1.8.3"]]
              :datomic      {:config "resources/free-transactor.properties"
                             :db-uri "datomic:free://localhost:4334/account-service-db"}}})
