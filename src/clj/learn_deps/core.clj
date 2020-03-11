(ns learn-deps.core
  (:require [compojure.core :refer [defroutes GET POST context]]
            [compojure.route :as route]
            [org.httpkit.server :refer [run-server]]
            [cheshire.core :as json]))

(defn request-body->map
  [request]
  (-> request
      :body
      slurp
      (json/parse-string true)))

(defroutes app
  (GET "/" [] {:status 200
               :headers {"Content-Type" "application/json"}
               :body (json/encode {:hello "world"})})
  (context "/json" []
    (GET "/" []
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/encode {:json true})})
    (GET "/:id" [id] {:status 200
                      :headers {"Content-Type" "application/json"}
                      :body (json/encode {:json true
                                          :id id})})
    (POST "/" request
      (let [response (:request (request-body->map request))]
        (println response)
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (json/encode {:json true
                             :response response})})))
  (route/not-found "Not Found"))

(defn start-server []
  (println "starting server on port 4000")
  (run-server app {:port 4000}))

(defn -main []
  (start-server))

;; interactive dev
(defonce server (atom nil))

(defn start []
  (reset! server (start-server)))

(defn stop []
  (when @server
    (@server :timeout 100)))

(defn restart []
  (stop)
  (start))
;;

(comment
  (-main))