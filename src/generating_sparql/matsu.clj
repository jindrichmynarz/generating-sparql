(ns generating-sparql.matsu
  (:require [generating-sparql.data :as data]
            [boutros.matsu.sparql :refer :all :exclude [concat count filter]]
            [boutros.matsu.core :refer [register-namespaces]]))

(def namespaces {:dbo "http://dbpedia.org/ontology/"
                 :foaf "http://xmlns.com/foaf/0.1/"
                 :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"})

(register-namespaces (into {} (map (fn [[k v]] [k (str "<" v ">")]) namespaces)))

(defn- compact-iri
  [iri]
  (let [[[p n]] (filter (fn [[p n]] (.startsWith iri n)) namespaces)]
    [p (keyword (subs iri (count n)))]))
  
(def required-properties
  (conj (mapcat (fn [{:keys [property]}]
                  ; IRIs are compacted because of the quirks in serializing java.net.URI. 
                  [:person (compact-iri property) [[]] \. ])
                (filter :required? data/properties))
        'group))

(defmacro get-persons
  []
  `(query {}
          (select-distinct :person)
          (where :person [:rdf :type] [:foaf :Person] \.
                 ~(eval required-properties))))

#_(println (get-persons))
