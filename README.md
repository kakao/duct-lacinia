# Duct module.lacinia

A [Duct][] module that adds a web server and [Lacinia][] with [ring-graphql-ui][]
to a configuration.

[duct]: https://github.com/duct-framework/duct
[lacinia]: https://github.com/walmartlabs/lacinia
[ring-graphql-ui]: https://github.com/threatgrid/ring-graphql-ui

## Installation

To install, add the following to your project `:dependencies`:

    [com.kakao.duct/duct-lacinia "0.1.0"]

## Usage

To add this module to your configuration, add the `:duct.module/lacinia` key.

For example:

```edn
{:duct.module/lacinia
 {:schema {:enums
           {:episode
            {:description "The episodes of the original Star Wars trilogy."
             :values [:NEWHOPE :EMPIRE :JEDI]}}

           :objects
           {:droid
            {:fields {:primary_functions {:type (list String)}
                      :id {:type Int}
                      :name {:type String}
                      :appears_in {:type (list :episode)}}}

            :human
            {:fields {:id {:type Int}
                      :name {:type String}
                      :home_planet {:type String}
                      :appears_in {:type (list :episode)}}}}

           :queries
           {:hero {:type (non-null :human)
                   :args {:episode {:type :episode}}
                   :resolve :human/get-hero}
            :droid {:type :droid
                    :args {:id {:type String :default-value "2001"}}
                    :resolve :droid/get-droid}}}}}
```

The `:schema` should contain a map of Lacinia schema.  See the [document][] of
Lacinia for more information on the format.

[document]: https://lacinia.readthedocs.io/en/latest/index.html

The module uses the :duct.core/project-ns key and the result key to find an
appropriate Integrant key at:

```
<project-ns>.resolver.<result key namespace>/<result key name>
```

So in the above example, the project namespace is `foo` and the result
namespaced key is `:human/get-hero`, so the module looks for a
`foo.resolver.human/get-hero` Integrant key.

For example:

```edn
{:duct.profile/base
 {:duct.core/project-ns foo
  :foo.resolver.human/get-hero {}}}
```

```clojure
(defmethod ig/init-key :foo.resolver.human/get-hero [_ _]
  (fn [context arguments value]
    (let [{:keys [episode]} arguments]
      (if (= episode :NEWHOPE)
        {:id 1000
         :name "Luke"
         :home_planet "Tatooine"
         :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]}
        {:id 2000
         :name "Lando Calrissian"
         :home_planet "Socorro"
         :appears_in ["EMPIRE" "JEDI"]}))))
```

By default, the module uses the `:duct.server.http/jetty` key for the
webserver, as supplied by the [server.http.jetty][] library. However,
if a key deriving from `:duct.server/http` already exists in the
configuration, the module will use that instead.

[server.http.jetty]: https://github.com/duct-framework/server.http.jetty

The GraphiQL packaged inside the module is built usin using [ring-graphql-ui][].
By default the endpoint is `/graphiql`, but you can specify it directly by setting
the `:graphiql` key:

```edn
{:duct.module/lacinia
 {:graphiql {:endpoint "/mygraphiql"}}}
```

## License

This software is licensed under the [Apache 2 license](LICENSE), quoted below.

Copyright 2019 Kakao Corp. <http://www.kakaocorp.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this project except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
