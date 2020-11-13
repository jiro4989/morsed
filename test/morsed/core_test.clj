(ns morsed.core-test
  (:require [clojure.test :refer :all]
            [morsed.core :refer :all]))

(deftest convert-test
  (testing "ok"
    (is (= "寿司は寿司である" (convert "吾輩は猫である" "名詞" "寿司")))))

(deftest do-main-test
  (testing "ok"
    (is (nil? (do-main ["吾輩は猫である"] {:part "名詞" :sub "寿司"})))))
