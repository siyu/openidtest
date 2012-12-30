(ns openidtest.openid
  "http://code.google.com/p/openid4java/
   http://davidtanzer.net/clojure_openid
   http://sureshatt.blogspot.com/2011/05/openid-consumer-for-attribute-exchange.html"
  (use [ring.util.response :only [redirect]])
  (import org.openid4java.consumer.ConsumerManager
          org.openid4java.message.ax.FetchRequest
          org.openid4java.message.ax.AxMessage))

(def ^:private consumerManager (ConsumerManager.))

(defn redirect->openid [req]
  (let [oidUrl "https://www.google.com/accounts/o8/id"        
        returnUrl (str (-> :scheme req name) "://" (get-in req [:headers "host"]) "/openid-return")
        discoveries (.discover consumerManager oidUrl)
        discovered (.associate consumerManager discoveries)
        fetchRequest (doto (FetchRequest/createFetchRequest)
                       (.addAttribute "email""http://axschema.org/contact/email" true))
        authRequest (doto (.authenticate consumerManager discovered returnUrl)
                      (.addExtension fetchRequest))
        dest-url (.getDestinationUrl authRequest true)
        _ (println "redirect->openid: discovered=" discovered)]
    (assoc (redirect dest-url)
      :session (assoc (:session req) :openid-discovered discovered))))

(defn verify [req]
  (let [request req
        openidRequest (into {} (for [[k v] (:params request)] [(name k) v]))
        responseParameters (org.openid4java.message.ParameterList. openidRequest)
        discovered (get-in req [:session :openid-discovered])
        _ (println "verify: discovered=" discovered)
        receivingUrl (str (name (:scheme request)) "://"
                          ((:headers request) "host")
                          (:uri request)
                          "?" (:query-string request))
        verification (.verify consumerManager receivingUrl responseParameters 
                              discovered)
        verified (.getVerifiedId verification)

        ;; check for verification here & clear the discovered key in session
        

        ]
    (println "verify: verified=" verified)
    (if verified
      (let [authSuccess (.getAuthResponse verification)
            fetchResp (.getExtension authSuccess org.openid4java.message.ax.AxMessage/OPENID_NS_AX)
            email (.getAttributeValue fetchResp "email")
            _ (println "verify: email=" email)]
        (assoc (redirect "/") :session (assoc (:session req) :openid-discovered nil))
        )
      "not verified")))




























