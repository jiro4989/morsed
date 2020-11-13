(defproject morsed "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  ; :repositories {"Atilika Open Source repository" "https://www.atilika.org/nexus/content/repositories/atilika"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.atilika.kuromoji/kuromoji-ipadic "0.9.0"]
                 [org.clojure/tools.cli "1.0.194"]]
  :repl-options {:init-ns morsed.core}
  :main "morsed.core")
