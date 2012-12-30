(ns openidtest.app
  (use [ring.util.response :only [redirect]]
       [compojure [core :only [defroutes GET POST ANY]]])
  (require [openidtest.openid :as openid]
           [compojure.handler :as handler]))

(defroutes routes
  (GET "/" [] "openid")
  (GET "/login" [] (redirect
                    (openid/redirect->openid "https://www.google.com/accounts/o8/id"
                                             "http://localhost:8080/openid-return")))
  (GET "/openid-return" [:as req] (str "now verified: " (openid/verify req))))

(def app (-> routes
             handler/site))