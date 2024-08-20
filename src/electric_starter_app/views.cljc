(ns electric-starter-app.views
  (:require
   #?(:clj [electric-starter-app.xtlib :as db])
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   [hyperfiddle.electric-ui4 :as ui]
   [xtdb.api #?(:clj :as :cljs :as-alias) xt]
   ))

(e/def !xtdb)
(e/def db)


#?(:clj (defn retrieve-all [db]
    (xt/q db
      '{:find [(pull ?e [*])]
        :where [[?e :xt/id]]})))

#?(:clj
   (defn all-records [db]
     (->> (xt/q db '{:find [(pull ?e [:xt/id :name])]
                     :where [[?e :xt/id]]})
       (map first)
       vec)))

#?(:clj (defn todo-records [db]
    (->> (xt/q db '{:find [(pull ?e [:xt/id :name])]
                    :where [[?e :name]]})
      vec)))

(e/defn Item [id]
  (e/server
    (let [e (xt/entity db id)
          _ (prn "id in Item: " id)]
      (e/client
        (dom/li
          (dom/label (dom/props {:for id})
            (dom/text (e/server (:name e)))))))))

(e/defn TodoItem [id]
  (e/server 
    (let [e (xt/entity db id)]
      (e/client 
        (dom/div 
          (dom/li (dom/label (dom/props {:for id}) (dom/text (e/server (:name e))) )))))))

(e/defn TodoList [!xtdb]
  (e/server
    (binding [electric-starter-app.views/!xtdb !xtdb
              db (new (db/latest-db> !xtdb))]
      (e/client
        (dom/link (dom/props {:rel :stylesheet :href "main.css"}))
        (dom/h1 (dom/text "Todo List"))
        (dom/div (dom/props {:class "todo-list"})
          (e/server
            (let [_ (println (e/offload #(all-records db)))]
              (e/for-by :xt/id [{:keys [xt/id]} (e/offload #(all-records db))]
                   (TodoItem. id)))))))))