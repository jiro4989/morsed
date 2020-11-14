(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn tokens [^String s]
  (.tokenize (Tokenizer.) s))

(defn part-replace [^Token token
                    ^String part
                    ^String sub]
  (if (= (.getPartOfSpeechLevel1 token) part)
    sub
    (.getSurface token)))

(defn convert [^String text
               ^String part
               ^String sub]
  (->> (tokens text)
       (map #(part-replace % part sub))
       (apply str)))

(def cli-options
  [["-p" "--part str" "part of speech level"
    :default "名詞"]
   ["-s" "--sub str" "substring"
    :default "寿司"]
   ["-P" "--print" "print tokens"]
   ["-h" "--help"]])

(defn args-or-stdinlines [args]
  (if (zero? (count args))
    (-> *in* java.io.BufferedReader. line-seq)
    args))

(defn print-token [^String text]
  (doseq [^Token token (tokens text)]
    (println "   --surface" (.getSurface token))
    (println "   --baseform" (.getBaseForm token))
    (println "   --conjugationform" (.getConjugationForm token))
    (println "   --conjugationtype" (.getConjugationType token))
    (println "-p --part" (.getPartOfSpeechLevel1 token))
    (println "   --part2" (.getPartOfSpeechLevel2 token))
    (println "   --part3" (.getPartOfSpeechLevel3 token))
    (println "   --part4" (.getPartOfSpeechLevel4 token))
    (println "   --pronunciation" (.getPronunciation token))
    (println "-r --reading" (.getReading token))
    (println "----------------------")
    ))

(defn do-main [args opts]
  (doseq [text (args-or-stdinlines args)]
    (if (:print opts)
      (print-token text)
      (println (convert text
                        (:part opts)
                        (:sub opts))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      (seq errors) (println errors)
      :else (do-main arguments options))))
