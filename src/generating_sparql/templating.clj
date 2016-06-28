(ns generating-sparql.templating
  (:require [generating-sparql.data :as data]
            [stencil.core :refer [render-file]]))

(defn query
  [properties limit offset]
  (render-file "query.mustache" {:properties properties
                                 :limit limit
                                 :offset offset}))

#_(println (query data/properties data/limit data/offset))
