(ns ring.middleware.lacinia
  (:require [integrant.core :as ig]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]))

(defn wrap-lacinia [handler {:keys [path schema resolvers context]
                             :or {path "/graphql"}
                             :as options}]
  (let [compiled-schema (schema/compile (attach-resolvers schema resolvers))]
    (fn [{:keys [uri] :as request}]
      (if (= uri path)
        (let [{{:keys [query variables operationName]} :params lacinia :lacinia} request
              result (lacinia/execute compiled-schema
                                      query
                                      variables
                                      (merge (:context lacinia) context)
                                      {:operation-name operationName})]
          {:status 200 :headers {} :body result})
        (handler request)))))
