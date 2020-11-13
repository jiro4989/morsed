(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

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

(def cli-options
  [["-p" "--part str" "part of speech level"
    :default "名詞"]
   ["-s" "--sub str" "substring"
    :default "寿司"]
   ["-h" "--help"]])

(defn do-main [args opts]
  (println (convert (first args)
                    (:part opts)
                    (:sub opts))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (or (zero? (count arguments))
          (:help options)) (println summary)
      (seq errors) (println errors)
      :else (do-main arguments options))))
