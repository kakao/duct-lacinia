(ns duct.middleware.lacinia
  (:require [integrant.core :as ig]
            [ring.middleware.lacinia :refer [wrap-lacinia]]))

(defmethod ig/init-key :duct.middleware/lacinia [_ {:keys [path schema resolvers context]
                                                    :or {path "/graphql"}}]
  #(wrap-lacinia % {:path path
                    :schema schema
                    :resolvers resolvers
                    :context context}))
