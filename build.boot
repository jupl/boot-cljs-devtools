(set-env!
 :source-paths #{"src"}
 :dependencies '[[boot/core        "2.2.0"  :scope "provided"]
                 [adzerk/bootlaces "0.1.11" :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
 pom {:project     'jupl/boot-cljs-devtools
      :version     +version+
      :description "Boot task to add Chrome DevTool enhancments for CLJS."
      :url         "https://github.com/adzerk/boot-cljs-repl"
      :scm         {:url "https://github.com/adzerk/boot-cljs-repl"}
      :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})
