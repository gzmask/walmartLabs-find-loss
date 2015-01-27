(defproject walmartlabs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main walmartlabs.core
  :aot :all
  :plugins [[lein-bin "0.3.4"]]
  :bin { :name "find-loss-gain" }
  )
