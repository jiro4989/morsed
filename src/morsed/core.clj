(ns morsed.core
  (:import [com.atilika.kuromoji.ipadic Token Tokenizer])
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [cheshire.core :refer [generate-string]])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn tokens [^String s]
  (.tokenize (Tokenizer.) s))

(defn re-match-str? [^String ptn
                     ^String text]
  (if (nil? ptn)
    false
    (not (nil? (re-matches (re-pattern ptn) text)))))

(defn token-matched? [^Token token
                      opts
                      k]
  (case k
    :reading (re-match-str? (opts k) (.getReading token))
    :part (re-match-str? (opts k) (.getPartOfSpeechLevel1 token))
    :part2 (re-match-str? (opts k) (.getPartOfSpeechLevel2 token))
    :part3 (re-match-str? (opts k) (.getPartOfSpeechLevel3 token))
    :part4 (re-match-str? (opts k) (.getPartOfSpeechLevel4 token))
    :pronunciation (re-match-str? (opts k) (.getPronunciation token))
    :conjugationform (re-match-str? (opts k) (.getConjugationForm token))
    :conjugationtype (re-match-str? (opts k) (.getConjugationType token))
    :baseform (re-match-str? (opts k) (.getBaseForm token))
    :surface (re-match-str? (opts k) (.getSurface token))
    false))

(defn token-matched-all? [^Token token
                          opts]
  (loop [k (keys opts)]
    (if (empty? k)
      true
      (if-not (token-matched? token opts (first k))
        false
        (recur (rest k))))))

(defn part-replace [^Token token
                    opts
                    ^String sub]
  (if (token-matched-all? token opts)
    sub
    (.getSurface token)))

(def matching-keys [:surface :baseform :conjugationform :conjugationtype :part :part2 :part3 :part4 :pronunciation :reading])

(defn convert [^String text
               opts
               ^String sub]
  (->> (tokens text)
       (map #(part-replace % (select-keys opts matching-keys) sub))
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
   ["-j" "--json" "json format for printing (need --print)"]
   [nil "--pretty" "pretty print json (need --print and --json)" :default false]
   ["-h" "--help"]])

(defn args-or-stdinlines [args]
  (if (zero? (count args))
    (-> *in* java.io.BufferedReader. line-seq)
    args))

(defn token-freetext [^String text]
  (str/join \newline (for [^Token token (tokens text)]
                       (str/join \newline [(str "   --surface" (.getSurface token))
                                           (str "   --baseform 基本形" (.getBaseForm token))
                                           (str "   --conjugationform 活用形" (.getConjugationForm token))
                                           (str "   --conjugationtype 活用型" (.getConjugationType token))
                                           (str "-p --part 品詞再分類1" (.getPartOfSpeechLevel1 token))
                                           (str "   --part2 品詞再分類2" (.getPartOfSpeechLevel2 token))
                                           (str "   --part3 品詞再分類3" (.getPartOfSpeechLevel3 token))
                                           (str "   --part4 品詞再分類4" (.getPartOfSpeechLevel4 token))
                                           (str "   --pronunciation 発音" (.getPronunciation token))
                                           (str "-r --reading 読み" (.getReading token))
                                           "----------------------"]))))

(defn token-json [^String text
                  pretty]
  (generate-string (for [^Token token (tokens text)]
                     {:surface (.getSurface token)
                      :baseform (.getBaseForm token)
                      :conjugationform (.getConjugationForm token)
                      :conjugationtype (.getConjugationType token)
                      :part (.getPartOfSpeechLevel1 token)
                      :part2 (.getPartOfSpeechLevel2 token)
                      :part3 (.getPartOfSpeechLevel3 token)
                      :part4 (.getPartOfSpeechLevel4 token)
                      :pronunciation (.getPronunciation token)
                      :reading (.getReading token)}) {:pretty pretty}))

(def help-top ["morsed morphological analyzer sed."
               "Copyright (c) 2020 jiro4989"
               "Released under the Apache License version 2.0."
               "https://github.com/jiro4989/morsed"
               ""
               "Options:"])

(defn usage [^String summary]
  (str/join \newline (concat help-top
                             (str/split summary #"\n"))))

(defn do-main [args opts]
  (doseq [text (args-or-stdinlines args)]
    (if (:print opts)
      (if (:json opts)
        (println (token-json text (:pretty opts)))
        (println (token-freetext text)))
      (println (convert text
                        opts
                        (:sub opts))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (println (usage summary))
      (seq errors) (println errors)
      :else (do-main arguments options))))
