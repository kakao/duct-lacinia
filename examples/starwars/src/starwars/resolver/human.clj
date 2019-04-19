(ns starwars.resolver.human
  (:require [integrant.core :as ig]))

(defmethod ig/init-key :starwars.resolver.human/get-hero [_ _]
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
