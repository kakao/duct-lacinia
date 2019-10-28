(ns duct.middleware.graphiql
  (:require [integrant.core :as ig]
            [ring-graphql-ui.core :refer [wrap-graphiql]]))

(defmethod ig/init-key :duct.middleware/graphiql [_ {:keys [path
                                                            endpoint
                                                            enable]
                                                     :or {path "/graphiql"
                                                          endpoint "/graphql"}}]
  (if enable
    #(wrap-graphiql % {:path path :endpoint endpoint})
    identity))
