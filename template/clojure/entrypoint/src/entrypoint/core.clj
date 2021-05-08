(ns entrypoint.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [function.core :refer [app]])
  (:import [org.eclipse.jetty.util.component LifeCycle$Listener]
           [java.io File]))

(defn add-lifecycle-listener
  "Creates a file /tmp/.lock when Jetty is started and deletes it when Jetty is
   shutting down.

  This is associated with a healthcheck. The existence of the /tmp/.lock file
  indicates a successful healthcheck and that the container running Jetty is
  ready to be exposed to traffic."
  [server]
  (.addLifeCycleListener server
                         (reify LifeCycle$Listener
                           (lifeCycleFailure [_this _event _cause])

                           (lifeCycleStarted [_this _event]
                             (.createNewFile (File. "/tmp/.lock")))

                           (lifeCycleStarting [_this _event])

                           (lifeCycleStopped [_this _event])

                           (lifeCycleStopping [_this _event]
                             (.delete (File. "/tmp/.lock")))))
  server)

(defn -main [& _args]
  (run-jetty app {:port 4000
                  :configurator add-lifecycle-listener}))
