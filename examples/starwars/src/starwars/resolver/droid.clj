(ns starwars.resolver.droid
  (:require [integrant.core :as ig]))

(defmethod ig/init-key :starwars.resolver.droid/get-droid [_ _]
  (fn [context arguments value]
    (let [{:keys [id]} arguments]
      {:id id
       :name "R2-D2"
       :primary_functions ["Buzz saw" "Electric pike"]
       :appears_in ["NEWHOPE" "EMPIRE" "JEDI"]})))
