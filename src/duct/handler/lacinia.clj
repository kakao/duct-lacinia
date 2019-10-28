(ns duct.handler.lacinia
  (:require [integrant.core :as ig]
            [muuntaja.core :as m]
            [muuntaja.middleware :as mm]
            [muuntaja.format.cheshire :as mfc]
            [ring.middleware.lacinia :refer [wrap-lacinia]]
            [ring-graphql-ui.core :as graphql-ui]))

(defn not-found-handler [_]
  {:status 404 :headers {} :body nil})

(def has-args? vector?)

(defn- apply-middleware-args [middleware]
  (if (has-args? middleware)
    (let [[f & args] middleware]
      #(apply f % args))
    middleware))

(defn- comp-middleware [middleware]
  (apply comp (reverse (map apply-middleware-args middleware))))

(defmethod ig/init-key :duct.handler.lacinia/app [_ {:keys [path schema resolvers context graphiql middleware]}]
  (let [muutaja-options (assoc-in m/default-options [:formats "application/json"] mfc/format)
        wrap-graphiql (if (:enable graphiql)
                        #(graphql-ui/wrap-graphiql % {:path (:endpoint graphiql)
                                                      :endpoint path})
                        identity)]
    (-> not-found-handler
        (wrap-lacinia {:path path
                       :schema schema
                       :resolvers resolvers
                       :context context})
        mm/wrap-params
        (mm/wrap-format muutaja-options)
        wrap-graphiql
        ((comp-middleware middleware)))))
