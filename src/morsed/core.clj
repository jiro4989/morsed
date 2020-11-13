(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer]))

(defn example []
  (let [tok (Tokenizer.)]
    (println (.tokenize tok "お寿司が食べたい。"))))

(defn -main []
  (example))
