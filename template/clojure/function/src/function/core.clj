(ns function.core)

(defn handler [req]
  {:status 200
   :body "Hello, Clojure."
   :headers {}})

(def app
  (-> handler))
