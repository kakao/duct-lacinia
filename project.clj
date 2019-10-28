(defproject com.kakao.duct/duct-lacinia "0.1.1"
  :description "A Duct module for Lacinia"
  :url "https://github.com/kakao/duct-lacinia"
  :license {:name "Apache 2 License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [duct/core "0.7.0"]
                 [duct/server.http.jetty "0.2.0"]
                 [metosin/muuntaja "0.6.1"]
                 [metosin/muuntaja-cheshire "0.6.4"]
                 [com.walmartlabs/lacinia "0.32.0"]
                 [threatgrid/ring-graphql-ui "0.1.1"]])
