(ns generating-sparql.sparql
  (:import [org.apache.jena.rdf.model Model]
           [org.apache.jena.query QueryExecutionFactory]
           [org.apache.jena.update UpdateAction UpdateFactory]))

(defn- process-select-binding
  [sparql-binding variable]
  [(keyword variable) (.get sparql-binding variable)])

(defn- process-select-solution
  "Process SPARQL SELECT `solution` for `result-vars`."
  [result-vars solution]
  (into {} (mapv (partial process-select-binding solution) result-vars)))

(defn select-query 
  "Execute SPARQL SELECT `query` on local RDF `model`."
  [^Model model
   ^String query]
  (with-open [qexec (QueryExecutionFactory/create query model)]
    (let [results (.execSelect qexec)
          result-vars (.getResultVars results)]
      (mapv (partial process-select-solution result-vars)
            (iterator-seq results)))))

(defn ^Model update-operation
  "Execute SPARQL Update `operation` on `model`."
  [^Model model
   ^String operation]
  (UpdateAction/execute (UpdateFactory/create operation) model)
  model)
