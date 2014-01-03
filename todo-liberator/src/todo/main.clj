(ns todo.main
  (:require [clojure.string :as str]
            [ring.middleware.reload :as reload]
            [ring.util.response :refer [resource-response]]
            [org.httpkit.server :refer [run-server]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [liberator.dev :refer [wrap-trace]]
            [liberator.core :refer [defresource]]))

(def stop-server (atom nil))
(def in-dev? (atom false))
(defonce used-ids (atom #{}))
(defonce todo-list (atom []))

(defn new-id
  []
  (let [id (str/upper-case (subs (str (java.util.UUID/randomUUID)) 0 5))]
    (dosync
     (if (contains? @used-ids id)
       (recur)
       (do
         (swap! used-ids conj id)
         id)))))

;; ROUTES
;; GET / -> Hoplon app
;; GET /todo - return list
;; POST /todo - add todo
;; GET /todo/:id - return todo
;; POST /todo/:id - update todo

(defn app
  [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    "hello HTTP!!!!"})

(defresource todos-resource
  :allowed-methods [:get :post]
  :available-media-types ["application/edn"]
  :post! (fn [{:keys [request]}]           
           (let [body (-> (:body request)
                          slurp
                          read-string)
                 new-todo (assoc body :id (new-id))]             
             (swap! todo-list conj new-todo)
             {:todo new-todo}))  
  :handle-ok (fn [_] @todo-list)
  :handle-created (fn [{:keys [todo]}] todo))

(defn delete-todo [list id]
  (vec (remove #(= (:id %) id) list)))

(defn replace-todo [list id new-todo]
  (vec (map #(if (= (:id %) id) new-todo %) list)))

(defresource todo-resource [id]
  :allowed-methods [:get :post]
  :available-media-types ["application/edn"]
  :exists? (fn [ctx]
             (if-let [todo (first (filter #(= (:id %) id) @todo-list))]
               {:todo todo}))
  :handle-ok (fn [{:keys [todo]}]
               todo)
  :post! (fn [{:keys [request]}]
           (let [body (read-string (slurp (:body request)))]
             (if (:delete body)
               (swap! todo-list delete-todo id)
               (swap! todo-list replace-todo id body)))))

(defroutes all-routes
  (GET "/" [] (resource-response "public/index.html"))
  (ANY "/todo" [] todos-resource)
  (ANY "/todo/:id" [id] (todo-resource id))
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
    (reset! stop-server (run-server handler {:port 3000}))
    @stop-server))

(defn -main
  [& args]
  (when (System/getenv "DEV")
    (reset! in-dev? true))
  (start-server))
