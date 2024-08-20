(ns electric-starter-app.main
  (:require
   #?(:clj [clojure.java.io :as io])
   [hyperfiddle.electric :as e]
   [hyperfiddle.electric-dom2 :as dom]
   #?(:clj [electric-starter-app.xtlib :refer [latest-db>]])
   [xtdb.api #?(:clj :as :cljs :as-alias) xt]
   [electric-starter-app.views :refer [TodoList]]
   ))

;; Equiv to fiddles.cljc in xtdb_demo
;; Saving this file will automatically recompile and update in your browser

#?(:clj (defonce !xtdb-node (atom nil)))

#?(:clj 
   (defn start-xtdb! [] ; from XTDB’s getting started: xtdb-in-a-box
        (prn 'start-xtdb!)
        (assert (= "true" (System/getenv "XTDB_ENABLE_BYTEUTILS_SHA1"))) ; App must start with this env var set to "true"
        (letfn [(kv-store [dir] {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                                            :db-dir (io/file dir)
                                            :sync? true}})]
    ;may have been started by another client
          (or @!xtdb-node
            (let [node (xt/start-node
                         {:xtdb/tx-log (kv-store "data/dev/tx-log")
                          :xtdb/document-store (kv-store "data/dev/doc-store")
                          :xtdb/index-store (kv-store "data/dev/index-store")})]
              (prn :!xtdb-node node)
              (reset! !xtdb-node node)
              (prn ":xtdb-node after reset" @!xtdb-node))))))

(e/defn Root []
  (e/server
    (if-let [!xtdb (try (e/offload #(start-xtdb!))
                     (catch hyperfiddle.electric.Pending _
                       nil))]
      (TodoList. !xtdb)
      (e/client 
        (dom/p (dom/text "XTDB is starting..."))))))

(e/defn Main [ring-request]
  (e/client
    (binding [dom/node js/document.body]
      (Root.))))

#?(:clj
   (comment
  ;; simple clj - add items to the xtdb database
     (xtlib/add-item @xtlib/!xtdb-node {:xt/id 1 :name "Get Milk"}) 
     (xtlib/add-item @xtlib/!xtdb-node {:xt/id 2 :name "Pay Taxes"})
     (xtlib/add-item @xtlib/!xtdb-node {:xt/id 3 :name "Feed Cat"})
     (xtlib/add-item @xtlib/!xtdb-node {:xt/id 4 :name "Go Swimming"})

  ;; clj only - direct query to database
     #?(:clj (allitems (xt/db @!xtdb-node)))

  ;; electric - latest-db> returns a missionary `flow`
     (type (latest-db> @!xtdb-node))
  ;; => missionary.core$latest$fn__6359

  ;; To access the data in a flow use `new` to instantiate it:
     (e/def db (new (latest-db> @!xtdb-node)))
  ;; => #'xtlib/db_hf_server_server

  ;; Instantiating it returns an "electric-only" function
     (type db)
  ;; => hyperfiddle.electric.impl.lang$electric_only
     ))


#?(:cljs
   (comment
     (js/alert "Connection to Browser REPL")))