(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn tokens [^String s]
  (.tokenize (Tokenizer.) s))

(defn part-replace [^Token token
                    opts
                    ^String sub]
  (cond
    (= (.getReading token) (:reading opts)) sub
    (= (.getPartOfSpeechLevel1 token) (:part opts)) sub
    (= (.getPartOfSpeechLevel2 token) (:part2 opts)) sub
    (= (.getPartOfSpeechLevel3 token) (:part3 opts)) sub
    (= (.getPartOfSpeechLevel4 token) (:part4 opts)) sub
    (= (.getPronunciation token) (:pronunciation opts)) sub
    (= (.getConjugationForm token) (:conjugationform opts)) sub
    (= (.getConjugationType token) (:conjugationtype opts)) sub
    (= (.getBaseForm token) (:baseform opts)) sub
    (= (.getSurface token) (:surface opts)) sub
    :else (.getSurface token)))

(defn convert [^String text
               opts
               ^String sub]
  (->> (tokens text)
       (map #(part-replace % opts sub))
       (apply str)))

(def cli-options
  [[nil "--surface str" "surface"]
   [nil "--baseform str" "baseform 基本形"]
   [nil "--conjugationform str" "conjugationform 活用形"]
   [nil "--conjugationtype str" "conjugationtype 活用型"]
   ["-p" "--part str" "part of speech level 1 品詞再分類1"]
   [nil "--part2 str" "part of speech level 2 品詞再分類2"]
   [nil "--part3 str" "part of speech level 3 品詞再分類3"]
   [nil "--part4 str" "part of speech level 4 品詞再分類4"]
   [nil "--pronunciation str" "pronunciation 発音"]
   ["-r" "--reading str" "reading 読み"]
   ["-s" "--sub str" "substring"]
   ["-P" "--print" "print tokens"]
   ["-h" "--help"]])

(defn args-or-stdinlines [args]
  (if (zero? (count args))
    (-> *in* java.io.BufferedReader. line-seq)
    args))

(defn print-token [^String text]
  (doseq [^Token token (tokens text)]
    (println "   --surface" (.getSurface token))
    (println "   --baseform 基本形" (.getBaseForm token))
    (println "   --conjugationform 活用形" (.getConjugationForm token))
    (println "   --conjugationtype 活用型" (.getConjugationType token))
    (println "-p --part 品詞再分類1" (.getPartOfSpeechLevel1 token))
    (println "   --part2 品詞再分類2" (.getPartOfSpeechLevel2 token))
    (println "   --part3 品詞再分類3" (.getPartOfSpeechLevel3 token))
    (println "   --part4 品詞再分類4" (.getPartOfSpeechLevel4 token))
    (println "   --pronunciation 発音" (.getPronunciation token))
    (println "-r --reading 読み" (.getReading token))
    (println "----------------------")
    ))

(defn do-main [args opts]
  (doseq [text (args-or-stdinlines args)]
    (if (:print opts)
      (print-token text)
      (println (convert text
                        opts
                        (:sub opts))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (println summary)
      (seq errors) (println errors)
      :else (do-main arguments options))))
