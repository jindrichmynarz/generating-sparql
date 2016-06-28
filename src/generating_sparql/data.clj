(ns generating-sparql.data)

(defn prefix [iri] (partial str iri))

(def ex (prefix "http://example.com/"))

(def foaf (prefix "http://xmlns.com/foaf/0.1/"))

(def dbo (prefix "http://dbpedia.org/ontology/"))

(def properties
  [{:property (foaf "name")
    :varname "name"
    :required? true}
   {:property (dbo "birthDate")
    :varname "birthDate"
    :required? true}
   {:property (dbo "deathDate")
    :varname "deathDate"
    :required? false}])

(def limit 10000)

(def offset 0)
