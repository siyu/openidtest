(ns openidtest.app
  (use [ring.util.response :only [redirect]]
       [compojure [core :only [defroutes GET POST ANY]]])
  (require [openidtest.openid :as openid]
           [compojure.handler :as handler]))

(defroutes routes
  (GET "/" [:as req] (str "home:" (get-in req [:session :auth-map])))
  (GET "/login" [:as req] (openid/redirect->openid req "/openid-return"))
  (GET "/logged-in" [:as req] (str "logged in:" (get-in req [:session :auth-map])))
  (GET "/openid-return" [:as req] (openid/verify req (fn [req] (assoc (redirect "/logged-in") :session (:session req))) (fn [_] (redirect "/")))))

(def app (-> routes
             handler/site))