(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer]))

(defn tokens [^String s]
  (.tokenize (Tokenizer.) s))

(defn part-replace [token part sub]
  (if (= (.getPartOfSpeechLevel1 token) part)
    sub
    (.getSurface token)))

(defn convert [text part sub]
  (->> (tokens text)
       (map #(part-replace % part sub))
       (apply str)))

(defn pp [text part sub]
  (println text " ---> " (convert text part sub)))

(defn -main []
  (do
    (pp "吾輩は猫である。名前はまだない。" "名詞" "寿司")
    (pp "右の頬を殴られたら左の頬を差し出せ" "名詞" "寿司")
    (pp "彼のカレー" "名詞" "寿司")
    (pp "このひき肉は引きにくいひき肉" "名詞" "寿司")
    (pp "隣の客はよく柿食う客だ" "名詞" "寿司")
  ))
