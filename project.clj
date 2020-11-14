(defproject morsed "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://github.com/jiro4989/morsed"
  :plugins [[lein-cloverage "1.2.0"]
            [lein-cljfmt "0.7.0"]
            [jonase/eastwood "0.3.10"]
            [lein-kibit "0.1.8"]]
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.atilika.kuromoji/kuromoji-ipadic "0.9.0"]
                 [org.clojure/tools.cli "1.0.194"]]
  :repl-options {:init-ns morsed.core}
  :main "morsed.core"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                       :prep-tasks ["compile"]
                       :uberjar-name "morsed.jar"}}
  )
