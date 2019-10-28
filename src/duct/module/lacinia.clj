(ns duct.module.lacinia
  (:require [duct.core :as duct]
            [duct.core.merge :as merge]
            [duct.core.env :as env]
            [integrant.core :as ig]
            [duct.handler.lacinia :refer [has-args?]]))

(def ^:private server-port
  (env/env '["PORT" Int :or 3000]))

(defn- all-resolve [schema]
  (reduce (fn [r [k v]]
            (if (map? v)
              (concat r (all-resolve v))
              (if (= :resolve k)
                (conj r v)
                r)))
          []
          schema))

(defn- resolver-keys [schema project-ns]
  (into {} (map (fn [k]
                  [k (ig/ref (keyword (str project-ns ".resolver"
                                           (when-let [ns (namespace k)]
                                             (str "." ns)))
                                      (name k)))])
                (all-resolve schema))))


(defn- derived-key [m k default]
  (if-let [kv (ig/find-derived-1 m k)]
    (key kv)
    default))

(defn- http-server-key [config]
  (derived-key config :duct.server/http :duct.server.http/jetty))

(defn- server-config [config]
  {(http-server-key config) {:port (merge/displace server-port)
                             :handler (merge/displace (ig/ref :duct.handler.lacinia/app))
                             :logger (merge/displace (ig/ref :duct/logger))}})

(defn- middleware-keys [middleware project-ns]
  (let [key-fn (fn [k]
                 (keyword (str project-ns ".middleware" 
                               (when-let [ns (namespace k)] 
                                 (str "." ns)))
                          (name k)))]
    (mapv #(if (has-args? %)
             (let [[k & args] %]
               (into [(ig/ref (key-fn k))] args))
             (ig/ref (key-fn %)))
          middleware)))

(defn- lacinia-config [config {:keys [path graphiql schema middleware]
                               :or {path "/graphql" graphiql {:endpoint "/graphiql"
                                                              :enable true}}}]
  (let [project-ns (:duct.core/project-ns config)]
    {:duct.handler.lacinia/app {:path path
                                :graphiql graphiql
                                :schema schema
                                :context {}
                                :resolvers (resolver-keys schema project-ns)
                                :middleware (middleware-keys middleware project-ns)}}))

(defmethod ig/init-key :duct.module/lacinia [_ options]
  #(duct/merge-configs %
                       (lacinia-config % options)
                       (server-config %)))
