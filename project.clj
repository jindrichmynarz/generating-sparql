(defproject generating-sparql "0.1.0-SNAPSHOT"
  :description "Exploration of approaches for generating SPARQL"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [stencil "0.5.0"]
                 [org.apache.jena/jena-core "3.1.0"]
                 [org.apache.jena/jena-arq "3.1.0"]
                 [yesparql "0.3.0"]
                 [matsu "0.1.2"]
                 [org.topbraid/spin "2.0.0"]
                 [cheshire "5.6.2"]]
  :repositories [["org.topbraid" "http://topquadrant.com/repository/spin"]])
