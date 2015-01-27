(ns walmartlabs.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [walmartlabs.file :as f])
  (:gen-class))

(sequence (comp (map inc) (filter odd?)) [1 2 3 4 5 6])

(def cli-options
  [["-h" "--help"]
   ["-p" "--price price-file" "find-loss-gain -p [price-file-name-in-directory]."
    :id :pri
    :default "prices.csv"
    :validate [#(re-find #".+\.csv" %) "Must be a CSV file."]]
   ["-d" "--directory path" "find-loss-gain -d [path-to-directory]."
    :id :dir
    :default "./resources/data/"]
   ["-t" "--threads thread-num" "find-loss-gain -t [thread-num]."
    :id :thread
    :default "1"]
   ]
  )

(def help-txt "To find the loss and gain for each store, simply run: \"find-loss-gain -p [price-file-name-in-directory] -d [path-to-directory] -t [threads-number]\" ")


(defn exit [status & msg]
  (when msg (println msg))
  (System/exit status))

(defn -main [& args]
  (let [{opts :options args :arguments summary :summary errs :errors}
        (parse-opts args cli-options) ]
    (when (not (empty? errs))
      (doseq [err errs]
        (println err))
      (exit 1))
    (when (:help opts)
      (println help-txt)
      (exit 0 summary))
    (when (:dir opts)
      (f/read-files (:dir opts) (:pri opts) (Integer/parseInt (:thread opts))))
    ))
