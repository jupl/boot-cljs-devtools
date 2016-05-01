(ns jupl.boot-cljs-devtools
  {:boot/export-tasks true}
  (:require [boot.core          :as    boot]
            [boot.task.built-in :refer [repl]]
            [boot.util          :as    util]
            [clojure.java.io    :as    io]
            [clojure.string     :as    str]))

(def ^:private deps
  '[[binaryage/devtools "0.6.1" :scope "test"]
    [binaryage/dirac    "0.2.0" :scope "test"]])

(defn- add-init! [in-file out-file]
  (let [ns ['devtools.core 'dirac.runtime]
        init ['devtools.core/install! 'dirac.runtime/install!]
        spec (-> in-file slurp read-string)]
    (when (not= :nodejs (-> spec :compiler-options :target))
      (util/info
       "Adding :require %s and :init-fns %s to %s...\n"
       ns init (.getName in-file))
      (io/make-parents out-file)
      (-> spec
          (update-in [:require] into ns)
          (update-in [:init-fns] into init)
          pr-str
          ((partial spit out-file))))))

(defn- assert-deps []
  (let [current (->> (boot/get-env :dependencies)
                     (map first)
                     set)
        missing (remove (comp current first) deps)]
    (if (seq missing)
      (util/warn (str "You are missing necessary dependencies for boot-cljs-repl.\n"
                      "Please add the following dependencies to your project:\n"
                      (str/join "\n" missing) "\n")))))

(defn- relevant-cljs-edn [prev fileset ids]
  (let [relevant (map #(str % ".cljs.edn") ids)
        f (if ids
            #(boot/by-path relevant %)
            #(boot/by-ext [".cljs.edn"] %))]
    (-> (boot/fileset-diff prev fileset)
        boot/input-files
        f)))

(defn- start-dirac []
  (require 'dirac.agent)
  ((resolve 'dirac.agent/boot!)))

(def start-dirac-once (memoize start-dirac))

(boot/deftask cljs-devtools
  [b ids BUILD_IDS #{str} "Only inject devtools into these builds (= .cljs.edn files)"]
  (let [src (boot/tmp-dir!)
        tmp (boot/tmp-dir!)
        prev (atom nil)]
    (assert-deps)
    (comp
     (repl
      :port 8230
      :server true
      :middleware ['dirac.nrepl.middleware/dirac-repl])
     (boot/with-pre-wrap fileset
       (start-dirac-once)
       (doseq [f (relevant-cljs-edn @prev fileset ids)]
         (let [path (boot/tmp-path f)
               in-file (boot/tmp-file f)
               out-file (io/file tmp path)]
           (io/make-parents out-file)
           (add-init! in-file out-file)))
       (reset! prev fileset)
       (-> fileset
           (boot/add-resource tmp)
           (boot/commit!))))))
