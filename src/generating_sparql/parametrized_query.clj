(ns generating-sparql.parametrized-query
  (:require [generating-sparql.data :as data]
            [clojure.java.io :as io])
  (:import [org.apache.jena.query ParameterizedSparqlString QuerySolutionMap]
           [org.apache.jena.rdf.model ResourceFactory]
           [org.apache.jena.datatypes.xsd XSDDatatype]))

(def parameterized-query (slurp (io/resource "parametrized_query.rq")))

(defn ->integer [n] (ResourceFactory/createTypedLiteral (str n) XSDDatatype/XSDinteger))

(defn query
  [limit offset]
  (let [bindings (doto (QuerySolutionMap.)
                   (.add "limit" (->integer limit))
                   (.add "offset" (->integer offset)))]
    (str (ParameterizedSparqlString. parameterized-query bindings))))

; Alternative solution
#_(defn query
  [limit offset]
  (str (doto (ParameterizedSparqlString. parameterized-query)
         (.setLiteral "limit" limit)
         (.setLiteral "offset" offset))))

#_(println (query data/limit data/offset))
