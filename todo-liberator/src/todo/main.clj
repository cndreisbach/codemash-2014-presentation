(ns todo.main
  (:require [ring.middleware.reload :as reload]
            [ring.util.response :refer [resource-response]]
            [org.httpkit.server :refer [run-server]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [liberator.dev :refer [wrap-trace]]
            [liberator.core :refer [defresource]]))

(def stop-server (atom nil))
(def in-dev? (atom false))

;; ROUTES
;; GET / -> Hoplon app
;; GET /list - return list
;; POST /list - update list

(defonce todo-list (atom []))

(defn app
  [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!!!!"})

(defresource list-resource
  :allowed-methods [:get :post]
  :available-media-types ["application/edn"]
  :handle-ok (fn [ctx] @todo-list)
  :post! (fn [ctx]           
           (let [body (slurp (get-in ctx [:request :body]))
                 new-list (:value (read-string body))]
             (println new-list)
             (reset! todo-list new-list))))

(defroutes all-routes
  (GET "/" [] (resource-response "public/index.html"))
  (ANY "/list" [] list-resource)
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
