(defproject vocabularytrainer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.xerial/sqlite-jdbc "3.34.0"]
                 [strigui "0.0.1-alpha11"]]
  :main vocabularytrainer.core
  :aot [vocabularytrainer.core]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
