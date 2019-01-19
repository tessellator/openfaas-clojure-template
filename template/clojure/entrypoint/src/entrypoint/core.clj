(ns entrypoint.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [function.core :refer [app]]))

(defn -main [& args]
  (run-jetty app {:port 4000}))
