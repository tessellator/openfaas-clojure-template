(ns function.core)

(defn handler [_req]
  {:status 200
   :body "Hello, Clojure."
   :headers {}})

(def app
  (-> handler))
