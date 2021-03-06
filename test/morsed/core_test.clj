(ns morsed.core-test
  (:require [clojure.test :refer :all]
            [morsed.core :refer :all]
            [clojure.string :refer [split-lines]]))

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
    (is (= "吾輩の寿司" (convert "吾輩の名前" {:part "名詞" :part2 "一般"} "寿司"))))
  (testing "ok: multi condition"
    (is (= "寿司の名前" (convert "吾輩の名前" {:part "名詞" :reading "ワガハイ"} "寿司"))))
  (testing "ok: regexp"
    (is (= "寿司寿司田中" (convert "彼彼女田中" {:part "名詞" :part2 "代.*"} "寿司"))))
  (testing "ok: ignore options"
    (is (= "吾輩の寿司" (convert "吾輩の名前" {:part "名詞" :part2 "一般" :print :help} "寿司")))))

(deftest usage-test
  (testing "ok:"
    (is (= (first help-top) (first (split-lines (usage "-h --help")))))))

(deftest token-freetext-test
  (testing "ok:"
    (is (= "   --surface 吾輩" (first (split-lines (token-freetext "吾輩は猫である。")))))))

(deftest token-json-test
  (testing "ok: not pretty"
    (is (= \[ (first (token-json "吾輩は猫である。" false)))))
  (testing "ok: not pretty"
    (is (= \[ (first (token-json "吾輩は猫である。" true))))))

(deftest do-main-test
  (testing "ok: one argument"
    (is (nil? (do-main ["吾輩は猫である"] {:part "名詞" :sub "寿司"}))))
  (testing "ok: multi arguments"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:part "名詞" :sub "寿司"}))))
  (testing "ok: print token"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:print true}))))
  (testing "ok: print token json format"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:print true :json true}))))
  (testing "ok: print token json format"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:print true :json true :pretty false}))))
  (testing "ok: print token pretty json format"
    (is (nil? (do-main ["吾輩は猫である" "隣の客はよく柿食う客だ"] {:print true :json true :pretty true})))))
