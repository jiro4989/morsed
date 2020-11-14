(ns morsed.core-test
  (:require [clojure.test :refer :all]
            [morsed.core :refer :all]))

(deftest convert-test
  (testing "ok: reading"
    (is (= "寿司は猫である" (convert "吾輩は猫である" {:reading "ワガハイ"} "寿司"))))
  (testing "ok: part"
    (is (= "寿司は寿司である" (convert "吾輩は猫である" {:part "名詞"} "寿司"))))
  (testing "ok: part2"
    (is (= "寿司は猫である" (convert "吾輩は猫である" {:part2 "代名詞"} "寿司"))))
  (testing "ok: part3"
    (is (= "寿司は猫である" (convert "吾輩は猫である" {:part3 "一般"} "寿司"))))
  (testing "ok: part4"
    (is (= "寿司寿司寿司寿司寿司" (convert "吾輩は猫である" {:part4 "\\*"} "寿司"))))
  (testing "ok: pronunciation"
    (is (= "吾輩寿司猫である" (convert "吾輩は猫である" {:pronunciation "ワ"} "寿司"))))
  (testing "ok: conjugationform"
    (is (= "吾輩は猫で寿司" (convert "吾輩は猫である" {:conjugationform "基本形"} "寿司"))))
  (testing "ok: conjugationtype"
    (is (= "吾輩は猫寿司ある" (convert "吾輩は猫である" {:conjugationtype "特殊・ダ"} "寿司"))))
  (testing "ok: baseform"
    (is (= "吾輩は寿司である" (convert "吾輩は猫である" {:baseform "猫"} "寿司"))))
  (testing "ok: surface"
    (is (= "吾輩は寿司である" (convert "吾輩は猫である" {:surface "猫"} "寿司"))))
  (testing "ok: multi condition"
    (is (= "吾輩の寿司" (convert "吾輩の名前" {:part "名詞" :part2 "一般"} "寿司")))))

(deftest print-usage-test
  (testing "ok:"
    (is (nil? (print-usage "吾輩は猫である")))))

(deftest do-main-test
  (testing "ok: one argument"
    (is (nil? (do-main ["吾輩は猫である"] {:part "名詞" :sub "寿司"}))))
  (testing "ok: multi arguments"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:part "名詞" :sub "寿司"}))))
  (testing "ok: print token"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:print true})))))
