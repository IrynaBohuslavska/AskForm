(ns diplomaform.handler
  (:require
   [reitit.ring :as reitit-ring]
   [diplomaform.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [config.core :refer [env]]))


(def mount-target
  [:div#app
   (spit "resources/data.clj" (pr-str [:a :b :c]))
   [:h2 "Welcome to diplomaform"]
   [:p "please wait while Figwheel/shadow-cljs is waking up ..."]
   [:p "(Check the js console for hints if nothing exciting happens.)"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")
    [:script "diplomaform.core.init_BANG_()"]]))


(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn json-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (slurp "resources/qa.json")})

(defn handler-res
  [_request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (slurp "resources/data.clj")})

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/stats" {:get {:handler index-handler}}]
     ["/qa" {:get {:handler json-handler}}]
     ["/res" {:get {:handler handler-res}}]
     ])
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))
   {:middleware middleware}))
