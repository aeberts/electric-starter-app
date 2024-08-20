(ns user 
  (:require [clojure.repl :refer [root-cause]]))

;; (defn custom-uncaught-exception-handler [throwable ^Thread thread]
;;   (let [root (root-cause throwable)]
;;     (println (str "Caused by: " root))))

;; (set! *uncaught-exception* custom-uncaught-exception-handler)