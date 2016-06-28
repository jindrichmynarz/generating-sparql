(ns generating-sparql.spin
  (:import [org.apache.jena.rdf.model ModelFactory]
           [org.topbraid.spin.model SPINFactory]
           [org.topbraid.spin.arq ARQFactory]))

; Disable caching to allow debugging
(.setUseCaches (ARQFactory/get) false)

(def query
  (let [model (doto (ModelFactory/createDefaultModel) (.read "query.ttl"))
        resource (.getResource model (data/ex "query"))]
    (str (.createQuery (ARQFactory/get) (SPINFactory/asQuery resource)))))
