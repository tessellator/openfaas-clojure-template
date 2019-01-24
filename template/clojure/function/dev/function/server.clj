(ns function.server
  (:require [function.core :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defonce ^:private server (atom nil))
(defonce ^:private jetty-opts (atom nil))

(defn start!
  ([]
   (start! @jetty-opts))
  ([opts]
   (if @server
     (println "A server is already running on port" (:port @jetty-opts))
     (let [opts (reset! jetty-opts (merge {:port 8080 :join? false} opts))]
       (reset! server (run-jetty (wrap-reload #'app) opts))))))

(defn stop! []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart! []
  (stop!)
  (start!))
