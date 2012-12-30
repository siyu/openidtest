(ns openidtest.openid
  "http://code.google.com/p/openid4java/
   http://davidtanzer.net/clojure_openid
   http://sureshatt.blogspot.com/2011/05/openid-consumer-for-attribute-exchange.html")

(def ^:private consumerManager (org.openid4java.consumer.ConsumerManager.))

(def ^:private oid-discovered (atom nil))

(defn redirect->openid [oidUrl returnUrl]
  (let [discoveries (.discover consumerManager oidUrl)
        discovered (.associate consumerManager discoveries)
        authRequest (.authenticate consumerManager discovered returnUrl)]
    (reset! oid-discovered discovered)
    (println "redirect->openid: discovered=" discovered)
    (.getDestinationUrl authRequest true)))

(defn verify [req]
  (let [request req
        openidRequest (into {} (for [[k v] (:params request)] [(name k) v]))
        responseParameters (org.openid4java.message.ParameterList. openidRequest)
        discovered @oid-discovered
        receivingUrl (str (name (:scheme request)) "://"
                          ((:headers request) "host")
                          (:uri request)
                          "?" (:query-string request))
        verification (.verify consumerManager receivingUrl responseParameters 
                              discovered)
        verified (.getVerifiedId verification)
        authSuccess (.getAuthResponse verification)
        _ (println "extension=" (.hasExtension authSuccess org.openid4java.message.ax.AxMessage/OPENID_NS_AX))
        ;fetchResp (.getExtension authSuccess org.openid4java.message.ax.AxMessage/OPENID_NS_AX)
        ;email (.getAttributeValue fetchResp "email")
        ]
    (println "verify: verified=" verified)

    verified))