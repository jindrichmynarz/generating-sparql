(ns generating-sparql.jena-query-builder
  (:require [generating-sparql.data :as data])
  (:import [org.apache.jena.arq.querybuilder SelectBuilder]
           [org.apache.jena.vocabulary RDF]
           [org.apache.jena.rdf.model ResourceFactory]
           [org.apache.jena.sparql.core Var]
           [org.apache.jena.sparql.expr ExprAggregator ExprVar]
           [org.apache.jena.sparql.expr.aggregate AggSample]))

(defn query
  [properties limit offset]
  (let [subquery-1 (doto (SelectBuilder.)
                     (.addPrefix "dbo" "http://dbpedia.org/ontology/")
                     (.addPrefix "foaf" "http://xmlns.com/foaf/0.1/")
                     (.addVar "person")
                     (.setDistinct true)
                     (.addWhere "?person" RDF/type "foaf:Person")
                     (.addWhere "?person" "foaf:name" (ResourceFactory/createResource))
                     (.addWhere "?person" "dbo:birthDate" (ResourceFactory/createResource))
                     (.addOrderBy "?person"))
        subquery-2 (doto (SelectBuilder.)
                     (.addVar "person")
                     (.addSubQuery subquery-1)
                     (.setLimit limit)
                     (.setOffset offset))
        builder (doto (SelectBuilder.)
                  (.addVar "person")
                  (.addSubQuery subquery-2)
                  (.addGroupBy "person"))]
    (doseq [{:keys [property varname required?]} properties]
      (.addVar builder (ExprAggregator. (Var/alloc varname)
                                        (AggSample. (ExprVar. (str \_ varname))))
               varname)
      (let [property' (ResourceFactory/createResource property)
            varname' (str "?_" varname)]
        (if required?
          (.addWhere builder "?person" property' varname')
          (.addOptional builder "?person" property' varname'))))
    (str (.build builder))))

#_(println (query data/properties data/limit data/offset))
