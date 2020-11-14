(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer])
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn tokens [^String s]
  (.tokenize (Tokenizer.) s))

(defn re-match-str? [^String ptn text]
  (if (nil? ptn)
    false
    (not (nil? (re-matches (re-pattern ptn) text)))))

(defn part-replace [^Token token
                    opts
                    ^String sub]
  (cond
    (re-match-str? (:reading opts) (.getReading token)) sub
    (re-match-str? (:part opts) (.getPartOfSpeechLevel1 token)) sub
    (re-match-str? (:part2 opts) (.getPartOfSpeechLevel2 token)) sub
    (re-match-str? (:part3 opts) (.getPartOfSpeechLevel3 token)) sub
    (re-match-str? (:part4 opts) (.getPartOfSpeechLevel4 token)) sub
    (re-match-str? (:pronunciation opts) (.getPronunciation token)) sub
    (re-match-str? (:conjugationform opts) (.getConjugationForm token)) sub
    (re-match-str? (:conjugationtype opts) (.getConjugationType token)) sub
    (re-match-str? (:baseform opts) (.getBaseForm token)) sub
    (re-match-str? (:surface opts) (.getSurface token)) sub
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
    (println "----------------------")))

(def usage ["morsed morphological analyzer sed."
            "Copyright (c) 2020 jiro4989"
            "Released under the Apache License version 2.0."
            "https://github.com/jiro4989/morsed"
            ""
            "Options:"])

(defn print-usage [^String summary]
  (println (str/join \newline (concat usage
                                      (str/split summary #"\n")))))

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
      (:help options) (print-usage summary)
      (seq errors) (println errors)
      :else (do-main arguments options))))
