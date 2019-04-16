(ns ring.middleware.lacinia-test
  (:require [ring.middleware.lacinia :refer :all]
            [clojure.test :refer :all]))

(def test-schema {:objects {:response {:fields {:result {:type 'String}}}} 
                  :queries {:test {:type :response
                                   :args {:param {:type 'String}}
                                   :resolve :test/resolver}}})

(defn test-resolver [_ args _]
  {:result (:param args)})

(deftest wrap-lacinia-test
  (let [handler (wrap-lacinia identity {:schema test-schema
                                        :resolvers {:test/resolver test-resolver}})]
    (testing "with graphql path"
      (is (= {:status 200 :headers {} :body {:data {:test {:result "ok"}}}}
             (handler {:uri "/graphql"
                       :params {:query "query { test(param: \"ok\") { result } }"}}))))

    (testing "without graphql path"
      (is (= {} (handler {}))))))

(deftest wrap-lacinia-custom-path-test
  (let [handler (wrap-lacinia identity {:schema test-schema
                                        :path "/custom-graphql"
                                        :resolvers {:test/resolver test-resolver}})]
    (testing "with graphql path"
      (is (= {:status 200 :headers {} :body {:data {:test {:result "ok"}}}}
             (handler {:uri "/custom-graphql"
                       :params {:query "query { test(param: \"ok\") { result } }"}}))))))

(deftest wrap-lacinia-context-test
  (testing "with context"
    (let [context-resolver (fn [context _ _]
                             {:result (:value context)})
          handler (wrap-lacinia identity {:schema test-schema
                                          :path "/graphql"
                                          :resolvers {:test/resolver context-resolver}})]
      (is (= {:status 200 :headers {} :body {:data {:test {:result "context value"}}}}
             (handler {:uri "/graphql"
                       :params {:query "query { test(param: \"ok\") { result } }"}
                       :lacinia {:context {:value "context value"}}})))))
  (testing "overwrite context param"
    (let [context-resolver (fn [context _ _]
                             {:result (:value context)})
          handler (wrap-lacinia identity {:schema test-schema
                                          :path "/graphql"
                                          :context {:value "context param"}
                                          :resolvers {:test/resolver context-resolver}})]
      (is (= {:status 200 :headers {} :body {:data {:test {:result "context param"}}}}
             (handler {:uri "/graphql"
                       :params {:query "query { test(param: \"ok\") { result } }"}
                       :lacinia {:context {:value "context value"}}}))))))
