(ns generating-sparql.spin-jsonld
  (:require [generating-sparql.data :as data]
            [cheshire.core :as json]
            [clojure.java.io :as io])
  (:import [org.apache.jena.rdf.model ModelFactory]
           [org.apache.jena.riot Lang RDFDataMgr]
           [org.apache.jena.query DatasetFactory]
           [org.topbraid.spin.model SPINFactory]
           [org.topbraid.spin.arq ARQFactory]))

(defn clj->jsonld
  [data]
  (json/generate-string data {:pretty true}))

(defn jsonld->model
  [jsonld]
  (let [dataset (DatasetFactory/create)]
    (RDFDataMgr/read dataset (io/input-stream (.getBytes jsonld)) Lang/JSONLD)
    (.getDefaultModel dataset)))

(defn query
  [properties limit offset]
  (let [context {"@vocab" "http://spinrdf.org/sp#"
                 "foaf" "http://xmlns.com/foaf/0.1/"
                 "rdf" "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                 "rdfs" "http://www.w3.org/2000/01/rdf-schema#"
                 "elements" {"@container" "@list"}
                 "groupBy" {"@container" "@list"}
                 "orderBy" {"@container" "@list"}
                 "predicate" {"@type" "@id"}
                 "resultVariables" {"@container" "@list"}
                 "subject" {"@type" "@id"}
                 "where" {"@container" "@list"}}
        person [{"@id" "_:person"}]
        subquery-1 {"@type" "SubQuery"
                    "query" {"@type" "Select"
                             "resultVariables" person
                             "distinct" true
                             "where" (into [{"subject" "_:person"
                                             "predicate" "rdf:type"
                                             "object" {"@id" "foaf:Person"}}]
                                           (mapv (fn [{:keys [property]}]
                                                   {"subject" "_:person"
                                                    "predicate" property
                                                    "object" {}})
                                                 (filter :required? properties)))
                             "orderBy" person}}
        subquery-2 {"@type" "SubQuery"
                    "query" {"@type" "Select"
                             "resultVariables" person
                             "where" [subquery-1]
                             "limit" limit
                             "offset" offset}}
        query' {"@context" context 
                "@graph" [{"@id" (data/ex "query")
                           "@type" "Select"
                           "rdfs:comment" {"@value" "Get persons from DBpedia"
                                           "@language" "en"}
                           "resultVariables" (into person
                                                   (mapv (fn [{:keys [varname]}]
                                                           {"@type" "Sample"
                                                            "expression" {"varName" (str "_" varname)}
                                                            "as" {"varName" varname}})
                                                         properties))
                           "where" (into [subquery-2]
                                         (mapv (fn [{:keys [property varname required?]}]
                                                 (let [triple-pattern {"subject" "_:person"
                                                                       "predicate" property
                                                                       "object" {"varName" (str "_" varname)}}]
                                                   (if required?
                                                     triple-pattern
                                                     {"@type" "Optional"
                                                      "elements" [triple-pattern]})))
                                               properties))
                           "groupBy" person}
                          {"@id" "_:person"
                           "varName" "person"}]}
        model (-> query'
                  clj->jsonld
                  jsonld->model)
        query-resource (.getResource model (data/ex "query"))]
    (str (.createQuery (ARQFactory.) (SPINFactory/asQuery query-resource)))))

#_(println (query data/properties data/limit data/offset))
