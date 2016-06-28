(ns generating-sparql.string-concatenation
  (:require [generating-sparql.data :as data]))

(defn query
  [properties limit offset]
  (letfn [(lines [template-fn properties]
            (str \newline
                 (->> properties
                      (map template-fn)
                      (interpose \newline)
                      (apply str))
                 \newline))]
    (str "PREFIX foaf: <http://xmlns.com/foaf/0.1/>

          SELECT ?person"
      (lines (comp #(str "(SAMPLE(?_" % ") AS ?" % ")") :varname) properties)
      "WHERE {
        {
          SELECT ?person
          WHERE {
            {
              SELECT DISTINCT ?person
              WHERE {
                ?person a foaf:Person ."
      (lines (comp #(str "?person <" % "> [] .") :property) (filter :required? properties))
              "}
              ORDER BY ?person
            }
          }
          LIMIT "
      limit
          " OFFSET "
      offset
       " }"
      (lines (fn [{:keys [property varname required?]}]
               (str (when-not required? "OPTIONAL { ")
                    "?person <" property "> ?_" varname " ."
                    (when-not required? " }")))
             properties)
      "}
      GROUP BY ?person")))

#_(println (query data/properties data/limit data/offset))
