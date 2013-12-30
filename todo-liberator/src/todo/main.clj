(ns todo.main
  (:require [ring.middleware.reload :as reload]
            [ring.util.response :refer [resource-response]]
            [org.httpkit.server :refer [run-server]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [liberator.dev :refer [wrap-trace]]))

(def stop-server (atom nil))
(def in-dev? (atom false))

;; ROUTES
;; GET / -> Hoplon app
;; GET /list - return list
;; POST /list - update list

(defn app
  [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!!!!"})

(defroutes all-routes
  (GET "/" [] (resource-response "public/index.html"))
  (ANY "/list" [] app)
  (route/resources "/")
  (route/not-found "Page not found"))

(defn start-server
  []
  (let [handler (if @in-dev?
                  (-> (site #'all-routes)
                      (reload/wrap-reload)
                      (wrap-trace :header :ui))
                  all-routes)]
    (when (not (nil? @stop-server))
      (@stop-server))
    (reset! stop-server (run-server handler {:port 3000}))))

(defn -main
  [& args]
  (when (System/getenv "DEV")
    (reset! in-dev? true))
  (start-server))
