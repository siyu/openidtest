(ns openidtest.app
  (use [ring.util.response :only [redirect]]
       [compojure [core :only [defroutes GET POST ANY]]])
  (require [openidtest.openid :as openid]
           [compojure.handler :as handler]))

(defroutes routes
  (GET "/" [:as req] (str "s:" (get-in req [:session :openid-discovered])))
  (GET "/login" [:as req] (openid/redirect->openid req))
  (GET "/openid-return" [:as req] (openid/verify req)))

(def app (-> routes
             handler/site))