(ns morsed.core
  (:import [org.atilika.kuromoji Token Tokenizer]))

(defn example []
  (let [tokenizer (.build (Tokenizer/builder))]
    (doall
     (for [token (.tokenize tokenizer "寿司が食べたい。")]
       (println (.getSurfaceForm token) "\t" (.getAllFeatures token))))))

(defn -main []
  (example))
