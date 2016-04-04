(ns account-service.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [account-service.db :refer :all]
            [ring.swagger.schema :as rs]))

(s/defschema Account
  {:id      Long
   :balance s/Num})

(s/defschema NewAccount (dissoc Account :id))

(s/defschema Transfer
  {:id           Long
   :from-account s/Int
   :to-account   s/Int
   :amount       s/Num
   :status       s/Keyword})

(s/defschema NewTransfer (dissoc Transfer :id :status))

(def app
  (api
    {:swagger
     {:ui   "/"
      :spec "/swagger.json"
      :data {:info {:title       "Account Service"
                    :description "A simple service for handling accounts and transfers between the accounts."}
             :tags [{:name "api"}]}}}

    (context "/api" []
             :tags ["api"]

             (POST "/account" []
                   :return Account
                   :body [account (describe NewAccount "new account")]
                   :summary "Creates an account in the system with an initial balance"
                   (ok (add-account account)))

             (GET "/account/:id" []
                  :path-params [id :- Long]
                  :return (s/maybe Account)
                  :summary "Returns all details relevant to an account"
                  (ok (get-account id)))

             (POST "/transfer" []
                   :return Transfer
                   :body [transfer (describe NewTransfer "new transfer")]
                   :summary "Requests a transfer between two accounts"
                   (ok (make-transfer transfer)))

             (GET "/transfer/:id" []
                  :path-params [id :- Long]
                  :return (s/maybe Transfer)
                  :summary "Gets all details relevant to a transfer"
                  (ok (get-transfer id)))

             (GET "/transfers" []
                  :return [Transfer]
                  :summary "Gets all transfers"
                  (ok (get-transfers)))

             (GET "/accounts" []
                  :return [Account]
                  :summary "Gets all accounts"
                  (ok (get-accounts))))))
