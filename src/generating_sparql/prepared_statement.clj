(ns generating-sparql.prepared-statement
  (:require [generating-sparql.data :as data]
            [clojure.java.io :as io])
  (:import [org.apache.jena.query DatasetFactory QueryExecutionFactory QuerySolutionMap]
           [org.apache.jena.rdf.model ResourceFactory]
           [org.apache.jena.datatypes.xsd XSDDatatype]))

(def parameterized-query (slurp (io/resource "parametrized_query.rq")))

(defn ->integer [n] (ResourceFactory/createTypedLiteral (str n) XSDDatatype/XSDinteger))

(defn query
  [limit offset]
  (let [bindings (doto (QuerySolutionMap.)
                   (.add "limit" (->integer limit))
                   (.add "offset" (->integer offset)))]
    (str (.getQuery (QueryExecutionFactory/create parameterized-query
                                                  (DatasetFactory/create) ; Mock an empty dataset
                                                  bindings)))))

; Prepared statement with pre-bound variables doesn't work,
; because the parameterized query has to be a syntactically
; valid SPARQL query. `LIMIT ?limit` and `OFFSET ?offset` are
; invalid.
#_(println (query data/limit data/offset))
