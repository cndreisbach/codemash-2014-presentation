(ns todo.main
  (:require [clojure.string :as str]
            [ring.middleware.reload :as reload]
            [ring.util.response :refer [resource-response]]
            [org.httpkit.server :refer [run-server with-channel on-close on-receive send!]]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]))

(def stop-server (atom nil))
(def in-dev? (atom false))
(def channels (atom #{}))

(defn ws-handler
  [req]
  (with-channel req channel
    (swap! channels conj channel)
    (println "channel opened: " channel)
    (on-close channel
              (fn [status]
                (swap! channels disj channel)
                (println "channel closed: " channel " - " status)))
    (on-receive channel (fn [data]
                          (println "data received: " data)
                          (doseq [chan (disj @channels channel)]
                            (println "sending data: " channel " - " data)
                            (send! chan data))))))

(defroutes all-routes
  (GET "/" [] (resource-response "public/index.html"))
  (GET "/ws" [] ws-handler)
  (route/resources "/")
  (route/not-found "Page not found"))

(defn start-server
  []
  (let [handler (if @in-dev?
                  (-> (site #'all-routes)
                      (reload/wrap-reload))
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
