(ns account-service.db-test
  (:require [account-service.db :refer :all]
            [midje.sweet :refer :all]
            [datomic.api :as d]))

;; ----- Database connection for tests -----

(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://account-service-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (read-string (slurp "resources/schema.dtm"))]
      (d/transact conn schema)
      conn)))

;; ----- Account tests -----

(fact "Adding one account should allow us to find that account using the returned id"
  (with-redefs [conn (create-empty-in-memory-db)]
    (let [account (add-account {:balance 161.80M})]
      (get-account (:id account)) => {:id (:id account) :balance 161.80M})))

(fact "Adding multiple accounts should allow us to find all those accounts"
  (with-redefs [conn (create-empty-in-memory-db)]
    (let [account-1 (add-account {:balance 12.34M})
          account-2 (add-account {:balance 56.78M})
          account-3 (add-account {:balance 12.34M})]
      (get-accounts) => [{:id (:id account-1) :balance 12.34M}
                         {:id (:id account-2) :balance 56.78M}
                         {:id (:id account-3) :balance 12.34M}])))

;; ----- Transfer tests -----

(fact "Making a transfer between two valid accounts with sufficient funds should succeed"
  (with-redefs [conn (create-empty-in-memory-db)]
    (let [from-account (add-account {:balance 1618.00M})
          to-account (add-account {:balance 200.00M})]
      (make-transfer {:from-account (:id from-account)
                      :to-account   (:id to-account)
                      :amount       12.34M}) => (contains {:from-account (:id from-account)
                                                           :to-account   (:id to-account)
                                                           :amount       12.34M
                                                           :status       :transfer.status/success})
                      (get-account (:id from-account)) => {:id (:id from-account) :balance (- 1618.00M 12.34M)}
                      (get-account (:id to-account)) => {:id (:id to-account) :balance (+ 200.00M 12.34M)})))


(fact "Making a transfer from an account with insufficient funds should fail with status insufficient-funds"
  (with-redefs [conn (create-empty-in-memory-db)]
    (let [from-account (add-account {:balance 18.00M})
          to-account (add-account {:balance 200.00M})
          transfer (make-transfer {:from-account (:id from-account) :to-account (:id to-account) :amount 100.23M})]
      (get-transfer (:id transfer)) => {:id           (:id transfer)
                                        :from-account (:id from-account)
                                        :to-account   (:id to-account)
                                        :amount       100.23M
                                        :status       :transfer.status/insufficient-funds})))

(fact "Making a transfer from an account which doesn't exist should fail with status no-such-from-account"
  (with-redefs [conn (create-empty-in-memory-db)]
    (let [to-account (add-account {:balance 180.00M})
          transfer (make-transfer {:from-account 928374 :to-account (:id to-account) :amount 80.23M})]
      (get-transfer (:id transfer)) => {:id           (:id transfer)
                                        :from-account 928374
                                        :to-account   (:id to-account)
                                        :amount       80.23M
                                        :status       :transfer.status/no-such-from-account})))

(fact "Making a transfer to an account which doesn't exist should fail with status no-such-to-account"
  (with-redefs [conn (create-empty-in-memory-db)]
    (let [from-account (add-account {:balance 138.00M})
          transfer (make-transfer {:from-account (:id from-account) :to-account 98234619 :amount 100.23M})]
      (get-transfer (:id transfer)) => {:id           (:id transfer)
                                        :from-account (:id from-account)
                                        :to-account   98234619
                                        :amount       100.23M
                                        :status       :transfer.status/no-such-to-account})))
