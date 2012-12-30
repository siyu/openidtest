(ns openidtest.web
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [openidtest.app :as app]))

(defn -main [& m]
  (run-jetty #'app/app {:join? false
                        :port (Integer. (or (System/getenv "PORT") (:port m) "8080"))}))

(-main)