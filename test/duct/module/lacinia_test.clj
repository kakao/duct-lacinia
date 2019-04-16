(ns duct.module.lacinia-test
  (:require [duct.module.lacinia :refer :all]
            [clojure.test :refer :all]
            [duct.core :as core]
            [integrant.core :as ig]))

(core/load-hierarchy)

(def test-schema {:queries {:test {:resolve :bar/resolver}
                            :test2 {:resolve :resolver}}})

(deftest module-lacinia-test
  (testing "default config"
    (let [config {:duct.module/lacinia {}}]
      (is (= {:duct.handler.lacinia/app
              {:path "/graphql"
               :graphiql {:endpoint "/graphiql"}
               :schema nil
               :context {}
               :resolvers {}
               :middleware []}
              :duct.server.http/jetty
              {:port 3000
               :handler (ig/ref :duct.handler.lacinia/app)
               :logger (ig/ref :duct/logger)}}
             (core/build-config config)))))

  (testing "updated resolvers key"
    (let [config {:duct.profile/base {:duct.core/project-ns 'foo}
                  :duct.module/lacinia {:schema test-schema}}]
      (is (= {:bar/resolver (ig/ref :foo.resolver.bar/resolver)
              :resolver (ig/ref :foo.resolver/resolver)}
             (-> (core/build-config config)
                 :duct.handler.lacinia/app
                 :resolvers)))))

  (testing "updated middleware key"
    (let [config {:duct.profile/base {:duct.core/project-ns 'foo}
                  :duct.module/lacinia {:schema test-schema
                                        :middleware [:wrap-cookies
                                                     [:wrap-file "/foo/bar"]]}}]
      (is (= [(ig/ref :foo.middleware/wrap-cookies)
              [(ig/ref :foo.middleware/wrap-file) "/foo/bar"]]
             (-> (core/build-config config)
                 :duct.handler.lacinia/app
                 :middleware))))))

